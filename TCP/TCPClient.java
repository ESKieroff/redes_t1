import java.net.*;
import java.util.Scanner;
import java.io.*;

public class TCPClient {
    private static final String REGISTER_COMMAND = "/reg";
    private static final String LOGIN_COMMAND = "/login";
    private static final String ONLINE_COMMAND = "/online";
    private static final String MESSAGE_COMMAND = "/msg";
    private static final String FTP_COMMAND = "/ftp";
    private static final String FOLLOW_COMMAND = "/follow";
    private static final String NEWS_COMMAND = "/news";
    private static final String HELP_COMMAND = "/help";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TCPClient <server_ip> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        Scanner sc = new Scanner(System.in);

        try (Socket socket = new Socket(hostname, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while (true) {
                System.out.print("Digite um comando (ou 'exit' para sair): ");
                line = sc.nextLine();

                if (line.equalsIgnoreCase("exit")) {
                    break;
                }

                // REGISTER_COMMAND = "/reg"
                if (line.startsWith(REGISTER_COMMAND)) {
                    System.out.print("username: ");
                    String username = sc.nextLine();
                    System.out.print("password: ");
                    String password = sc.nextLine();
                    writer.println(REGISTER_COMMAND + " " + username + " " + password);
                }
                // LOGIN_COMMAND = "/login"
                else if (line.startsWith(LOGIN_COMMAND)) {
                    System.out.print("username: ");
                    String username = sc.nextLine();
                    System.out.print("password: ");
                    String password = sc.nextLine();
                    writer.println(LOGIN_COMMAND + " " + username + " " + password);
                }
                // ONLINE_COMMAND = "/online"
                else if (line.startsWith(ONLINE_COMMAND)) {
                    writer.println(ONLINE_COMMAND);
                }
                // MESSAGE_COMMAND = "/msg"
                // else if (line.startsWith(MESSAGE_COMMAND)) {
                // // recebe a string e separa as partes
                // String[] parts = line.split(" ", 3);
                // // verifica se o username está online
                // if (parts.length == 3) {
                // writer.println(MESSAGE_COMMAND + " " + parts[1] + " " + parts[2]);
                // } else {
                // System.out.println("Comando inválido");
                // }
                // // envia mensagem para o servidor com o comando e os parametros
                // writer.println(MESSAGE_COMMAND + " " + parts[1] + " " + parts[2]);
                // }
                else if (line.startsWith(MESSAGE_COMMAND)) {
                    String[] parts = line.split(" ", 3);
                    if (parts.length == 3) {
                        writer.println(MESSAGE_COMMAND + " " + parts[1] + " " + parts[2]);
                    } else {
                        System.out.println("Comando inválido. Use: /msg <usuario> <mensagem>");
                    }
                }

                // FTP_COMMAND = "/ftp"
                else if (line.startsWith(FTP_COMMAND)) {
                    // recebe a string e separa as partes
                    String[] parts = line.split(" ", 3);
                    // verifica se o username está online
                    if (parts.length == 3) {
                        writer.println(FTP_COMMAND + " " + parts[1] + " " + parts[2]);
                    } else {
                        System.out.println("Comando inválido");
                    }
                    // envia mensagem para o servidor com o comando e os parametros
                    writer.println(FTP_COMMAND + " " + parts[1] + " " + parts[2]);
                    // cria o repositorio para o arquivo e copia o arquivo para o repositorio
                    File file = new File(parts[2]);
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead = 0;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    }

                }
                // FOLLOW_COMMAND = "/follow"
                else if (line.startsWith(FOLLOW_COMMAND)) {
                    // chama o metodo follow passando o username e o username a ser seguido
                    String[] parts = line.split(" ", 3);
                    writer.println(FOLLOW_COMMAND + " " + parts[1] + " " + parts[2]);
                }
                // NEWS_COMMAND = "/news"
                else if (line.startsWith(NEWS_COMMAND)) {
                    // separa a string em partes, se string for igual a 2 chama o metodo news
                    // passando o username, se for 3 chama o metodo news passando o username e o
                    // titulo da noticia
                    String[] parts = line.split(" ", 3);
                    if (parts.length == 2) {
                        writer.println(NEWS_COMMAND + " " + parts[1]);
                    } else if (parts.length == 3) {
                        writer.println(NEWS_COMMAND + " " + parts[1] + " " + parts[2]);
                    }

                }
                // HELP_COMMAND = "/help"
                else if (line.startsWith(HELP_COMMAND)) {
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
        }
    }
}
