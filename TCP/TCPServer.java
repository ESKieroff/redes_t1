import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {

    private static final Map<String, PrintWriter> clients = new HashMap<>();
    private static final Set<String> onlineUsers = new HashSet<>();
    private static final Map<String, List<String>> followers = new HashMap<>();
    private static final Map<String, List<String>> newsletters = new HashMap<>();

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
                    } else if (line.startsWith("/INN")) {
                        handleLogin(line);
                    } else if (line.startsWith("/MSG")) {
                        handleDirectMessage(line);
                    } else if (line.startsWith("/FOLLOW")) {
                        handleFollow(line);
                    } else if (line.startsWith("/NEWS")) {
                        handleNews(line);
                    } else if (line.startsWith("/HELP")) {
                        showHelp();
                    } else if (line.equalsIgnoreCase("FIM")) {
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
            String[] parts = line.split(" ", 2);
            if (parts.length > 1) {
                userName = parts[1];
                clients.put(userName, out);
                out.println("Usuário " + userName + " registrado com sucesso.");
                System.out.println("Usuário registrado: " + userName);
            }
        }

        private void handleLogin(String line) {
            String[] parts = line.split(" ", 2);
            if (parts.length > 1 && clients.containsKey(parts[1])) {
                userName = parts[1];
                onlineUsers.add(userName);
                out.println("Usuário " + userName + " está online.");
                notifyFollowers(userName + " está online.");
            }
        }

        private void handleDirectMessage(String line) {
            String[] parts = line.split(" ", 3);
            if (parts.length > 2 && onlineUsers.contains(parts[1])) {
                String message = parts[2];
                PrintWriter recipientOut = clients.get(parts[1]);
                if (recipientOut != null) {
                    recipientOut.println(userName + " (DM): " + message);
                }
            }
        }

        private void handleFollow(String line) {
            String[] parts = line.split(" ", 3);
            if (parts.length > 2) {
                String follower = parts[1];
                String followee = parts[2];
                if (!followers.containsKey(followee)) {
                    followers.put(followee, new ArrayList<>());
                }
                followers.get(followee).add(follower);
                out.println("Você agora está seguindo " + followee);
            }
        }

        private void handleNews(String line) {
            String[] parts = line.split(" ", 3);
            if (parts.length > 1) {
                if (parts[1].equalsIgnoreCase("who")) {
                    out.println("Você está inscrito nas newsletters de: " + newsletters.keySet());
                } else if (parts.length > 2) {
                    if (parts[2].equalsIgnoreCase("true")) {
                        newsletters.putIfAbsent(parts[1], new ArrayList<>());
                        newsletters.get(parts[1]).add(userName);
                        out.println("Você se inscreveu na newsletter de " + parts[1]);
                    } else if (parts[2].equalsIgnoreCase("false")) {
                        newsletters.get(parts[1]).remove(userName);
                        out.println("Você se desinscreveu da newsletter de " + parts[1]);
                    } else {
                        sendNewsletter(line);
                    }
                }
            }
        }

        private void sendNewsletter(String line) {
            String message = line.substring(6); // Remove "/NEWS "
            newsletters.forEach((user, subscribers) -> {
                for (String subscriber : subscribers) {
                    PrintWriter subscriberOut = clients.get(subscriber);
                    if (subscriberOut != null) {
                        subscriberOut.println("Newsletter de " + user + ": " + message);
                    }
                }
            });
        }

        private void notifyFollowers(String message) {
            for (String follower : followers.keySet()) {
                List<String> followerList = followers.get(follower);
                if (followerList.contains(userName)) {
                    PrintWriter followerOut = clients.get(follower);
                    if (followerOut != null) {
                        followerOut.println(message);
                    }
                }
            }
        }

        private void showHelp() {
            out.println("Comandos disponíveis:");
            out.println("/REG <nickname> - Registrar um usuário.");
            out.println("/INN <registereduser> - Fazer login como usuário registrado.");
            out.println("/MSG <user> <message> - Enviar uma mensagem direta para um usuário.");
            out.println("/FOLLOW <follower> <followee> - Seguir um usuário.");
            out.println("/NEWS <user> <true/false> - Assinar ou desinscrever-se de uma newsletter.");
            out.println("/NEWS <message> - Enviar uma newsletter.");
            out.println("/NEWS who - Mostrar newsletters que você está inscrito.");
            out.println("/HELP - Mostrar esta ajuda.");
        }
    }
}
