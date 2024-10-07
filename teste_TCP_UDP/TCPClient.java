import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 9999);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.print("Digite seu nickname: ");
            String nickname = reader.readLine();
            writer.println(nickname + " entrou no chat.");

            // Thread para receber mensagens do servidor
            new Thread(() -> {
                try {
                    String message;
                    while ((message = socketReader.readLine()) != null) {
                        System.out.println("[CHAT] " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Enviar mensagens
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equalsIgnoreCase("sair")) {
                    break;
                }
                writer.println(nickname + ": " + message);
            }
        } catch (IOException e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }
    }
}
