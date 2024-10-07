import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Map<String, List<String>> newsletters = new HashMap<>();
    private Map<String, List<String>> newsletterMessages = new HashMap<>();
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private UserManager userManager;
    private String userName;

    public ClientHandler(Socket socket, UserManager userManager) {
        this.socket = socket;
        this.userManager = userManager;

    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                Command(line);
            }
        } catch (IOException ex) {
            System.out.println("Erro de I/O: " + ex.getMessage());
        } finally {
            try {
                if (userName != null) {
                    userManager.removeOnlineUser(userName);
                    userManager.notifyFollowers(userName, userName + " está offline.");
                }
                socket.close();
            } catch (IOException ex) {
                System.out.println("Erro ao fechar socket: " + ex.getMessage());
            }
        }
    }

    private void Command(String line) {
        if (line.startsWith("/reg")) {
            Registration(line);
        } else if (line.startsWith("/login")) {
            Login(line);
        } else if (line.startsWith("/online")) {
            listOnlineUsers();
        } else if (line.startsWith("/msg")) {
            DirectMessage(line);
        } else if (line.startsWith("/ftp")) {
            FileTransfer(line);
        } else if (line.startsWith("/follow")) {
            Follow(line);
        } else if (line.startsWith("/unfollow")) {
            Follow(line);
        } else if (line.startsWith("/following")) {
            FollowList(line);
        } else if (line.startsWith("/followers")) {
            FollowList(line);
        }
        else if (line.startsWith("/news")) {
            News(line);
        } else if (line.startsWith("/help")) {
            Help();
        } else if (line.equalsIgnoreCase("exit")) {
            System.out.println("Cliente " + userName + " desconectado.");
        }
    }

    private void Registration(String line) {
        String[] parts = line.split(" ");
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            try {
                userManager.registerUser(username, password);
                out.println("Usuário " + username + " registrado com sucesso.");
                userManager.loginUser(username, password);
                userManager.addOnlineUser(username);
            } catch (IllegalArgumentException e) {
                out.println("Erro: " + e.getMessage());
            }
        } else {
            out.println("Erro: Comando de registro inválido.");
        }
    }

    private void Login(String line) {
        String[] parts = line.split(" ");
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];

            if (userManager.loginUser(username, password)) {
                userName = username;
                User user = userManager.getUser(username);
                user.setWriter(out);

                out.println("Usuário " + username + " logado com sucesso.");
                userManager.notifyFollowers(userName, username + " está online.");

            } else {
                out.println("Erro: Usuário ou senha incorretos.");
            }
        }
    }

    private void listOnlineUsers() {
        Set<String> onlineUsers = userManager.getOnlineUsers();
        out.println("Usuários online: " + String.join(", ", onlineUsers));
    }

    // private void DirectMessage(String line) {
    // String[] parts = line.split(" ", 3);
    // if (parts.length > 2) {
    // String recipient = parts[1];
    // String message = parts[2];

    // if (userManager.getOnlineUsers().contains(recipient)) {
    // User recipientUser = userManager.getUser(recipient);
    // if (recipientUser != null) {
    // PrintWriter recipientOut = recipientUser.getWriter();
    // recipientOut.println(userName + ": " + message);
    // }
    // } else {
    // out.println("Usuário " + recipient + " não está online.");
    // }
    // } else {
    // out.println("Erro: Comando de mensagem inválido.");
    // }
    // }

    // private void DirectMessage(String line) {
    // String[] parts = line.split(" ", 3);
    // if (parts.length > 2) {
    // String recipient = parts[1];
    // String message = parts[2];

    // User recipientUser = userManager.getUser(recipient);
    // if (recipientUser != null && userManager.isUserOnline(recipient)) {
    // PrintWriter recipientOut = recipientUser.getWriter();
    // if (recipientOut != null) {
    // recipientOut.println(userName + ": " + message);
    // out.println("Mensagem enviada para " + recipient);
    // } else {
    // out.println("Erro ao enviar a mensagem: Destinatário não configurado
    // corretamente.");
    // }
    // } else {
    // out.println("Usuário " + recipient + " não está online.");
    // }
    // } else {
    // out.println("Erro: Comando de mensagem inválido.");
    // }
    // }

    private void DirectMessage(String line) {
        String[] parts = line.split(" ", 3);
        if (parts.length > 2) {
            String recipient = parts[1];
            String message = parts[2];

            // Verifica se o destinatário está online
            if (userManager.getOnlineUsers().contains(recipient)) {
                User recipientUser = userManager.getUser(recipient);
                if (recipientUser != null && recipientUser.getWriter() != null) {
                    PrintWriter recipientOut = recipientUser.getWriter();
                    recipientOut.println(userName + ": " + message);
                    out.println("Mensagem enviada para " + recipient);
                } else {
                    out.println("Erro ao enviar a mensagem: Destinatário não configurado corretamente.");
                }
            } else {
                out.println("Usuário " + recipient + " não está online.");
            }
        } else {
            out.println("Erro: Comando de mensagem inválido.");
        }
    }

    // private void FileTransfer(String line) {
    // String[] parts = line.split(" ", 3);
    // if (parts.length > 2) {
    // String recipient = parts[1];
    // String filePath = parts[2];

    // if (userManager.getOnlineUsers().contains(recipient)) {
    // Path destinationPath = Paths.get("persistence/" + recipient + "/" +
    // Paths.get(filePath).getFileName());
    // try {
    // Files.createDirectories(destinationPath.getParent());
    // Files.copy(Paths.get(filePath), destinationPath,
    // StandardCopyOption.REPLACE_EXISTING);
    // out.println("Arquivo transferido para " + recipient + " em " +
    // destinationPath);
    // } catch (IOException e) {
    // out.println("Erro ao transferir o arquivo: " + e.getMessage());
    // }
    // } else {
    // out.println("Usuário " + recipient + " não está online.");
    // }
    // } else {
    // out.println("Erro: Comando de transferência de arquivo inválido.");
    // }
    // }

    private void FileTransfer(String line) {
        String[] parts = line.split(" ", 3);
        if (parts.length > 2) {
            String recipient = parts[1];
            String filePath = parts[2];

            if (userManager.getOnlineUsers().contains(recipient)) {
                Path destinationPath = Paths.get("persistence/" + recipient + "/" + Paths.get(filePath).getFileName());
                try {
                    Files.createDirectories(destinationPath.getParent());
                    Files.copy(Paths.get(filePath), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                    // Notifica o remetente que a transferência foi concluída
                    out.println("Arquivo transferido para " + recipient + " em " + destinationPath);

                    // Notifica o destinatário sobre o novo arquivo
                    User recipientUser = userManager.getUser(recipient);
                    if (recipientUser != null && recipientUser.getWriter() != null) {
                        recipientUser.getWriter()
                                .println(userName + " enviou um arquivo para você: " + destinationPath);
                    }

                } catch (IOException e) {
                    out.println("Erro ao transferir o arquivo: " + e.getMessage());
                }
            } else {
                out.println("Usuário " + recipient + " não está online.");
            }
        } else {
            out.println("Erro: Comando de transferência de arquivo inválido.");
        }
    }

    // private void Follow(String line) {
    // String[] parts = line.split(" ");
    // if (parts.length == 3) {
    // String action = parts[2];
    // String userToFollow = parts[1];

    // if ("true".equals(action)) {
    // userManager.addFollower(userName, userToFollow);
    // out.println("Você agora está seguindo " + userToFollow);
    // } else if ("false".equals(action)) {
    // userManager.removeFollower(userName, userToFollow);
    // out.println("Você deixou de seguir " + userToFollow);
    // } else if ("who".equals(action)) {
    // List<String> followers = userManager.getFollowers(userName);
    // out.println("Seguindo: " + String.join(", ", followers));
    // }
    // } else {
    // out.println("Erro: Comando de seguir inválido.");
    // }
    // }

    private void Follow(String line) {
        if (parts.length == 2) {
            String userToFollow = parts[1];
            String action = parts[0];

            // Seguir o usuário
            if ("follow".equalsIgnoreCase(action)) {
                if (userManager.getUser(userToFollow) != null && !userToFollow.equals(userName)) {
                    userManager.addFollower(userName, userToFollow);
                    out.println("Você agora está seguindo " + userToFollow);
                } else {
                    out.println("Erro: Usuário não encontrado ou você tentou seguir a si mesmo.");
                }
            }
            // Deixar de seguir o usuário
            else if ("unfollow".equalsIgnoreCase(action)) {
                if (userManager.getFollowers(userName).contains(userToFollow)) {
                    userManager.removeFollower(userName, userToFollow);
                    out.println("Você deixou de seguir " + userToFollow);
                } else {
                    out.println("Erro: Você não está seguindo " + userToFollow + ".");
                }
            }
            // Comando inválido
            else {
                out.println("Erro: Ação desconhecida para o comando de seguir.");
            }
        } else {
            out.println("Erro: Comando de seguir inválido. Use '/follow <username> true|false' ou '/follow who'.");
        }
    }

    private void FollowList(String line) {
        if (parts.length == 1) {
            String userToFollow = parts[1];
            String action = parts[0];
            // Listar seguidores
            if ("following".equalsIgnoreCase(action)) {
                List<String> followingList = userManager.getFollowers(userName);
                if (followingList.isEmpty()) {
                    out.println("Você não está seguindo ninguém.");
                } else {
                    out.println("Seguindo: " + String.join(", ", followingList));
                }

            }
            // Listar dos meus seguidores
            else if ("followers".equalsIgnoreCase(action)) {
                List<String> followersList = userManager.getFollowers(userToFollow);
                if (followersList.isEmpty()) {
                    out.println(userToFollow + " não possui seguidores.");
                } else {
                    out.println(userToFollow + " possui os seguintes seguidores: " + String.join(", ", followersList));
                }
            }
            // Comando inválido
            else {
                out.println("Erro: Ação desconhecida para o comando de seguir.");
            }
        } else {
            out.println("Erro: Comando de seguir inválido. Use '/follow <username> true|false' ou '/follow who'.");
        }

    }
    // private void News(String line) {
    // String[] parts = line.split(" ");
    // if (parts.length < 2) {
    // out.println("Erro: Comando de newsletter inválido.");
    // return;
    // }

    // String action = parts[1];
    // String newsletterOwner;
    // switch (action) {
    // case "create":
    // if (userManager.createNewsletter(userName)) {
    // out.println("Canal de newsletter criado.");
    // } else {
    // out.println("Erro: Você já possui um canal de newsletter.");
    // }
    // break;

    // case "delete":
    // if (userManager.deleteNewsletter(userName)) {
    // out.println("Canal de newsletter removido.");
    // } else {
    // out.println("Erro: Você não possui um canal de newsletter.");
    // }
    // break;

    // case "subscribe":
    // if (parts.length < 3) {
    // out.println("Erro: Especifique o canal de newsletter para assinar.");
    // return;
    // }
    // newsletterOwner = parts[2];
    // if (userManager.subscribeToNewsletter(userName, newsletterOwner)) {
    // out.println("Você assinou a newsletter de " + newsletterOwner + ".");
    // } else {
    // out.println("Erro: O usuário " + newsletterOwner
    // + " não possui um canal de newsletter ou você já está inscrito.");
    // }
    // break;

    // case "unsubscribe":
    // if (parts.length < 3) {
    // out.println("Erro: Especifique o canal de newsletter para cancelar
    // assinatura.");
    // return;
    // }
    // newsletterOwner = parts[2];
    // if (userManager.unsubscribeFromNewsletter(userName, newsletterOwner)) {
    // out.println("Você cancelou a assinatura da newsletter de " + newsletterOwner
    // + ".");
    // } else {
    // out.println("Erro: Você não está assinado na newsletter de " +
    // newsletterOwner + ".");
    // }
    // break;

    // case "list":
    // List<String> subscribedNewsletters =
    // userManager.getSubscribedNewsletters(userName);
    // if (subscribedNewsletters.isEmpty()) {
    // out.println("Você não está inscrito em nenhuma newsletter.");
    // } else {
    // out.println("Você está inscrito nas newsletters de: " + String.join(", ",
    // subscribedNewsletters));
    // }
    // break;

    // case "msg":
    // if (parts.length < 3) {
    // out.println("Erro: Especifique a mensagem a ser enviada.");
    // return;
    // }
    // String message = String.join(" ", Arrays.copyOfRange(parts, 2,
    // parts.length));
    // if (userManager.sendNewsletterMessage(userName, message)) {
    // out.println("Mensagem enviada para os assinantes da sua newsletter.");
    // } else {
    // out.println("Erro: Você não possui um canal de newsletter ou não há
    // assinantes.");
    // }
    // break;

    // default:
    // out.println("Erro: Comando de newsletter inválido.");
    // break;
    // }
    // }

    private void News(String line) {
        String[] parts = line.split(" ", 3);

        if (parts.length < 2) {
            out.println(
                    "Erro: Comando de newsletter inválido. Use '/news create', '/news delete', '/news subscribe <username>', '/news unsubscribe <username>', '/news list', ou '/news msg <mensagem>'.");
            return;
        }

        String action = parts[1];

        switch (action.toLowerCase()) {
            // Criar canal de newsletter
            case "create":
                if (userManager.createNewsletter(userName)) {
                    out.println("Canal de newsletter criado com sucesso.");
                } else {
                    out.println("Erro: Você já possui um canal de newsletter.");
                }
                break;

            // Deletar canal de newsletter
            case "delete":
                if (userManager.deleteNewsletter(userName)) {
                    out.println("Canal de newsletter removido com sucesso.");
                } else {
                    out.println("Erro: Você não possui um canal de newsletter.");
                }
                break;

            // Assinar canal de newsletter de outro usuário
            case "subscribe":
                if (parts.length < 3) {
                    out.println("Erro: Especifique o nome do usuário para assinar a newsletter.");
                    return;
                }
                String newsletterOwner = parts[2];
                if (userManager.subscribeToNewsletter(userName, newsletterOwner)) {
                    out.println("Você assinou a newsletter de " + newsletterOwner + ".");
                } else {
                    out.println("Erro: O usuário " + newsletterOwner
                            + " não possui um canal de newsletter ou você já está inscrito.");
                }
                break;

            // Cancelar assinatura de um canal de newsletter
            case "unsubscribe":
                if (parts.length < 3) {
                    out.println("Erro: Especifique o nome do usuário para cancelar a assinatura.");
                    return;
                }
                newsletterOwner = parts[2];
                if (userManager.unsubscribeFromNewsletter(userName, newsletterOwner)) {
                    out.println("Você cancelou a assinatura da newsletter de " + newsletterOwner + ".");
                } else {
                    out.println("Erro: Você não está assinado na newsletter de " + newsletterOwner + ".");
                }
                break;

            // Listar newsletters em que o usuário está inscrito
            case "list":
                List<String> subscribedNewsletters = userManager.getSubscribedNewsletters(userName);
                if (subscribedNewsletters.isEmpty()) {
                    out.println("Você não está inscrito em nenhuma newsletter.");
                } else {
                    out.println("Você está inscrito nas newsletters de: " + String.join(", ", subscribedNewsletters));
                }
                break;

            // Enviar mensagem para os assinantes da própria newsletter
            case "msg":
                if (parts.length < 3) {
                    out.println("Erro: Especifique a mensagem a ser enviada.");
                    return;
                }
                String message = parts[2];
                if (userManager.sendNewsletterMessage(userName, message)) {
                    out.println("Mensagem enviada para os assinantes da sua newsletter.");
                } else {
                    out.println("Erro: Você não possui um canal de newsletter ou não há assinantes.");
                }
                break;

            // Comando inválido
            default:
                out.println(
                        "Erro: Comando de newsletter desconhecido. Use '/news create', '/news delete', '/news subscribe <username>', '/news unsubscribe <username>', '/news list', ou '/news msg <mensagem>'.");
                break;
        }
    }

    private void Help() {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("      ┌───────────────┬──────────────┬─────────────────────────────────────────────┐\n");
        helpMessage.append("      │ command        │ example      │ description                                 │\n");
        helpMessage.append("      ├───────────────┼──────────────┼─────────────────────────────────────────────┤\n");
        helpMessage.append("      │ /REG           │ /REG johndoe │ Comando para registrar novo usuário         │\n");
        helpMessage.append("      │ /REG password  │ /REG password abracadabra │ Definir senha para o usuário │\n");
        helpMessage.append("      │ /LOGIN         │ /LOGIN johndoe │ Fazer login                               │\n");
        helpMessage.append("      │ /ONLINE        │ /ONLINE      │ Listar usuários online                     │\n");
        helpMessage
                .append("      │ /MSG           │ /MSG johndoe como vai? │ Enviar mensagem para um usuário online │\n");
        helpMessage.append(
                "      │ /FTP           │ /FTP johndoe src/foto.jpg │ Enviar arquivo para um usuário online │\n");
        helpMessage.append("      │ /FOLLOW        │ /FOLLOW johndoe true │ Seguir usuário                        │\n");
        helpMessage.append("      │ /FOLLOW who    │ /FOLLOW who   │ Listar quem você está seguindo           │\n");
        helpMessage.append("      │ /NEWS create   │ /NEWS create  │ Criar canal de newsletter                 │\n");
        helpMessage.append("      │ /NEWS delete   │ /NEWS delete  │ Remover canal de newsletter               │\n");
        helpMessage
                .append("      │ /NEWS <username> true │ /NEWS johndoe true │ Assinar newsletter                │\n");
        helpMessage.append("      │ /NEWS <message> │ /NEWS hoje tem jogo │ Enviar mensagem para a newsletter     │\n");
        helpMessage.append("      │ /HELP          │ /HELP         │ Exibir esta lista de comandos             │\n");
        helpMessage.append("      └───────────────┴──────────────┴─────────────────────────────────────────────┘\n");

        out.println(helpMessage.toString());
    }

    private void notifyFollowers(String message) {
        userManager.notifyFollowers(userName, message);
    }
}
