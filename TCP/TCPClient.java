import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient {
    private static final String REGISTER_COMMAND = "/reg";
    private static final String LOGIN_COMMAND = "/login";
    private static final String ONLINE_COMMAND = "/online";
    private static final String MESSAGE_COMMAND = "/msg";
    private static final String FTP_COMMAND = "/ftp";
    private static final String FOLLOW_COMMAND = "/follow";
    private static final String UNFOLLOW_COMMAND = "/unfollow";
    private static final String FOLLOWING_COMMAND = "/following";
    private static final String FOLLOWERS_COMMAND = "/followers";
    private static final String NEWS_COMMAND = "/news";
    private static final String HELP_COMMAND = "/help";

    private static PrintWriter writer;
    private static BufferedReader in;
    private static Socket socket;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TCPClient <server_ip> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        Scanner sc = new Scanner(System.in);

        try {
            socket = new Socket(hostname, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Thread para receber mensagens
            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println("Nova mensagem: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao receber mensagem: " + e.getMessage());
                }
            }).start();

            String line;
            while (true) {
                System.out.print("Digite um comando: ");
                line = sc.nextLine();

                if (line.equalsIgnoreCase("exit")) {
                    break;
                }

                if (line.startsWith(REGISTER_COMMAND)) {
                    System.out.print("username: ");
                    String username = sc.nextLine();
                    System.out.print("password: ");
                    String password = sc.nextLine();
                    writer.println(REGISTER_COMMAND + " " + username + " " + password);
                } else if (line.startsWith(LOGIN_COMMAND)) {
                    System.out.print("username: ");
                    String username = sc.nextLine();
                    System.out.print("password: ");
                    String password = sc.nextLine();
                    writer.println(LOGIN_COMMAND + " " + username + " " + password);
                } else if (line.startsWith(ONLINE_COMMAND)) {
                    writer.println(ONLINE_COMMAND);
                } else if (line.startsWith(MESSAGE_COMMAND)) {
                    writer.println(line);
                } else if (line.startsWith(FTP_COMMAND)) {
                    String[] parts = line.split(" ", 3);
                    if (parts.length == 3) {
                        writer.println(FTP_COMMAND + " " + parts[1] + " " + parts[2]);
                        File file = new File(parts[2]);
                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = fis.read(buffer)) != -1) {
                                writer.write(buffer, 0, bytesRead);
                            }
                        }
                    } else {
                        System.out.println("Comando inválido");
                    }
                } else if (line.startsWith(FOLLOW_COMMAND)) {
                    String[] parts = line.split(" ", 2);
                    if (parts.length > 1) {
                        writer.println(FOLLOW_COMMAND + " " + parts[1]);
                    } else {
                        System.out.println("Comando inválido: usuário não especificado.");
                    }
                } else if (line.startsWith(UNFOLLOW_COMMAND)) {
                    String[] parts = line.split(" ", 2);
                    if (parts.length > 1) {
                        writer.println(UNFOLLOW_COMMAND + " " + parts[1]);
                    } else {
                        System.out.println("Comando inválido: usuário não especificado.");
                    }
                } else if (line.startsWith(FOLLOWING_COMMAND)) {
                    String[] parts = line.split(" ", 2);
                    if (parts.length > 1) {
                        writer.println(FOLLOWING_COMMAND + " " + parts[1]);
                    } else {
                        System.out.println("Comando inválido: usuário não especificado.");
                    }
                } else if (line.startsWith(FOLLOWERS_COMMAND)) {
                    String[] parts = line.split(" ", 2);
                    if (parts.length > 1) {
                        writer.println(FOLLOWERS_COMMAND + " " + parts[1]);
                    } else {
                        System.out.println("Comando inválido: usuário não especificado.");
                    }
                } else if (line.startsWith(NEWS_COMMAND)) {
                    String[] parts = line.split(" ", 3);
                    if (parts.length == 2) {
                        writer.println(NEWS_COMMAND + " " + parts[1]);
                    } else if (parts.length == 3) {
                        writer.println(NEWS_COMMAND + " " + parts[1] + " " + parts[2]);
                    }
                } else if (line.startsWith(HELP_COMMAND)) {
                    writer.println(HELP_COMMAND);
                } else {
                    writer.println(line);
                }

                String response = in.readLine();
                System.out.println("Resposta do servidor: " + response);
            }
        } catch (UnknownHostException ex) {
            System.out.println("Servidor não encontrado: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Erro de I/O: " + ex.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }
    }
}
