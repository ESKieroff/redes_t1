import java.net.*;
import java.util.Scanner;
import java.io.*;

public class TCPClient {

    private static final String REGISTER_COMMAND = "/REG";
    private static final String MESSAGE_COMMAND = "/MSG";
    private static final String LOGIN_COMMAND = "/INN";
    private static final String FOLLOW_COMMAND = "/FOLLOW";
    private static final String NEWS_COMMAND = "/NEWS";
    private static final String HELP_COMMAND = "/HELP";

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

                writer.println(line); // Envia comando para o servidor

                // Recebe e imprime resposta do servidor
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
