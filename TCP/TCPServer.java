import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class TCPServer {
    private static final Map<String, User> users = new ConcurrentHashMap<>();
    private static final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    private static final Map<String, List<String>> followers = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> newsletters = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> newsletterMessages = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java TCPServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor escutando na porta " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + socket.getRemoteSocketAddress());
                new ClientHandler(socket).start();
            }

        } catch (IOException ex) {
            System.out.println("Exceção do servidor: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("/REG")) {
                        handleRegistration(line);
                    } else if (line.startsWith("/LOGIN")) {
                        handleLogin(line);
                    } else if (line.startsWith("/ONLINE")) {
                        listOnlineUsers();
                    } else if (line.startsWith("/MSG")) {
                        handleDirectMessage(line);
                    } else if (line.startsWith("/FTP")) {
                        handleFileTransfer(line);
                    } else if (line.startsWith("/FOLLOW")) {
                        handleFollow(line);
                    } else if (line.startsWith("/NEWS")) {
                        handleNews(line);
                    } else if (line.startsWith("/HELP")) {
                        handleHelp();
                    } else if (line.equalsIgnoreCase("exit")) {
                        System.out.println("Cliente " + userName + " desconectado.");
                        break;
                    }
                }

            } catch (IOException ex) {
                System.out.println("Erro de I/O: " + ex.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.out.println("Erro ao fechar socket: " + ex.getMessage());
                }
            }
        }

        private void handleRegistration(String line) {
            String[] parts = line.split(" ");
            if (parts.length >= 4 && !parts[2].contains("*")) {
                String username = parts[2];
                String password = parts[3];
                users.put(username, new User(username, password));
                out.println("Usuário " + username + " registrado com sucesso.");
            } else {
                out.println("Nome de usuário ou senha inválidos.");
            }
        }

        private void handleLogin(String line) throws IOException {
            String[] parts = line.split(" ");
            if (parts.length > 1) {
                userName = parts[1];
                String password = in.readLine();
                User user = users.get(userName);
                if (user != null && user.getPassword().equals(password)) {
                    onlineUsers.add(userName);
                    out.println("Usuário " + userName + " está online.");
                    notifyFollowers(userName + " está online.");
                } else {
                    out.println("Usuário ou senha incorretos.");
                }
            }
        }

        private void listOnlineUsers() {
            out.println("Usuários online: " + String.join(", ", onlineUsers));
        }

        private void handleDirectMessage(String line) {
            String[] parts = line.split(" ", 3);
            if (parts.length > 2 && onlineUsers.contains(parts[1])) {
                String message = parts[2];
                PrintWriter recipientOut = users.get(parts[1]).getWriter();
                if (recipientOut != null) {
                    recipientOut.println(userName + ": " + message);
                }
            } else {
                out.println("Usuário " + parts[1] + " não está online.");
            }
        }

        private void handleFileTransfer(String line) {
            String[] parts = line.split(" ", 3);
            if (parts.length > 2 && onlineUsers.contains(parts[1])) {
                String filePath = parts[2];
                Path destinationPath = Paths.get("persistence/" + parts[1] + "/" + Paths.get(filePath).getFileName());
                try {
                    Files.createDirectories(destinationPath.getParent());
                    Files.copy(Paths.get(filePath), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    out.println(parts[1] + " " + destinationPath.toString());
                } catch (IOException e) {
                    out.println("Erro ao transferir o arquivo: " + e.getMessage());
                }
            } else {
                out.println("Usuário " + parts[1] + " não está online.");
            }
        }

        private void handleFollow(String line) {
            String[] parts = line.split(" ");
            if (parts.length > 2) {
                String action = parts[2];
                if ("true".equals(action)) {
                    followers.computeIfAbsent(userName, k -> new ArrayList<>()).add(parts[1]);
                    out.println("Você agora está seguindo " + parts[1]);
                    if (onlineUsers.contains(parts[1])) {
                        out.println("Usuário " + parts[1] + " está online.");
                    }
                } else if ("false".equals(action)) {
                    List<String> following = followers.get(userName);
                    if (following != null) {
                        following.remove(parts[1]);
                        out.println("Você deixou de seguir " + parts[1]);
                    }
                } else if ("who".equals(action)) {
                    out.println("Seguindo: "
                            + String.join(", ", followers.getOrDefault(userName, Collections.emptyList())));
                }
            }
        }

        private void handleNews(String line) {
            String[] parts = line.split(" ");
            if (parts.length > 1) {
                switch (parts[1]) {
                    case "create":
                        newsletters.put(userName, new ArrayList<>());
                        out.println("Canal de newsletter criado.");
                        break;
                    case "delete":
                        newsletters.remove(userName);
                        out.println("Canal de newsletter removido.");
                        break;
                    case "who":
                        out.println("Inscrições: "
                                + String.join(", ", newsletters.getOrDefault(userName, Collections.emptyList())));
                        break;
                    case "MSG":
                        String message = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
                        newsletterMessages.computeIfAbsent(userName, k -> new ArrayList<>()).add(message);
                        out.println("Mensagem da newsletter enviada: " + message);
                        break;
                }
            }
        }

        private void handleHelp() {
            String helpMessage = "      ┌───────────────┬──────────────┬─────────────────────────────────────────────┐\n"
                    +
                    "      │ command        │ example      │ description                                 │\n" +
                    "      ├───────────────┼──────────────┼─────────────────────────────────────────────┤\n" +
                    "      │ /REG           │ /REG johndoe │ Comando para registrar novo usuário         │\n" +
                    "      │ /REG password   │ /REG password abracadabra │ Definir senha para o usuário │\n" +
                    "      │ /LOGIN         │ /LOGIN johndoe │ Fazer login                               │\n" +
                    "      │ /ONLINE        │ /ONLINE      │ Listar usuários online                     │\n" +
                    "      │ /MSG           │ /MSG johndoe como vai? │ Enviar mensagem para um usuário online │\n" +
                    "      │ /FTP           │ /FTP johndoe src/foto.jpg │ Enviar arquivo para um usuário online │\n" +
                    "      │ /FOLLOW        │ /FOLLOW johndoe true │ Seguir usuário                        │\n" +
                    "      │ /FOLLOW who    │ /FOLLOW who   │ Listar quem você está seguindo           │\n" +
                    "      │ /NEWS create   │ /NEWS create  │ Criar canal de newsletter                 │\n" +
                    "      │ /NEWS delete   │ /NEWS delete  │ Remover canal de newsletter               │\n" +
                    "      │ /NEWS <username> true │ /NEWS johndoe true │ Assinar newsletter                │\n" +
                    "      │ /NEWS <message> │ /NEWS hoje tem jogo │ Enviar mensagem para a newsletter     │\n" +
                    "      │ /HELP          │ /HELP         │ Exibir esta lista de comandos             │\n" +
                    "      └───────────────┴──────────────┴─────────────────────────────────────────────┘\n";

            out.println(helpMessage);
        }

        private void notifyFollowers(String message) {
            for (String follower : followers.getOrDefault(userName, Collections.emptyList())) {
                if (onlineUsers.contains(follower)) {
                    users.get(follower).getWriter().println(message);
                }
            }
        }
    }

    private static class User {
        private final String username;
        private final String password;
        private PrintWriter writer;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public PrintWriter getWriter() {
            return writer;
        }

        public void setWriter(PrintWriter writer) {
            this.writer = writer;
        }
    }
}
