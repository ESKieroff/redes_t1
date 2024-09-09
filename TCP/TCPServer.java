import java.io.*;
import java.net.*;
 
// Recebe uma mensagem de algum cliente
// Imprime mensagem na tela

public class TCPServer {
 
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java TCPServer <port>");
            return;
        }
 
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;
        
        try { 
            serverSocket = new ServerSocket(port);

            System.out.println("Server is listening on port " + port);
 
            while (true) {
                Socket socket = serverSocket.accept();
 
                System.out.println("New client connected: " + socket.getRemoteSocketAddress()); 
 
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = in.readLine();

                System.out.println("Mensagem recebida: " + line);

                // Envia mensagem de volta para o cliente
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(line);

                socket.close();

                if (line.startsWith("FIM")) {
                    System.out.println("Finalizando servidor");
                    break;
                }
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        serverSocket.close();

    }
}
