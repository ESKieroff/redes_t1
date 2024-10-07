import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
    private static Set<Socket> clients = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            System.out.println("[SERVIDOR INICIADO] Aguardando conexões...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                System.out.println("[NOVA CONEXÃO] Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                // Cria uma nova thread para lidar com o cliente
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("[RECEBIDO] " + message);
                broadcast(message, clientSocket);
            }
        } catch (IOException e) {
            System.out.println("[DESCONECTADO] Cliente desconectado: " + clientSocket.getInetAddress().getHostAddress());
        } finally {
            try {
                clientSocket.close();
                clients.remove(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcast(String message, Socket sourceSocket) {
        for (Socket client : clients) {
            if (!client.equals(sourceSocket)) {
                try {
                    PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                    writer.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
