import java.net.*;
import java.io.*;

public class UDPClient {
    private static final int SERVER_PORT = 9999;

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket();
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

            System.out.print("Digite seu nickname: ");
            String nickname = reader.readLine();
            byte[] sendData = (nickname + " entrou no chat.").getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            // Thread para receber mensagens do servidor
            new Thread(() -> {
                try {
                    byte[] receiveBuffer = new byte[1024];
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        clientSocket.receive(receivePacket);
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
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
                sendData = (nickname + ": " + message).getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
