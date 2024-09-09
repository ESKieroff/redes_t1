import java.net.*;
import java.util.Scanner;
import java.io.*;
 
// Le uma linha do teclado
// Envia a linha digitada ao servidor

public class TCPClient {
 
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
 
            String line = sc.nextLine();

            writer.println(line); 

            // Recebe mensagem do servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            line = in.readLine();
            System.out.println("Mensagem recebida: " + line);

        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
