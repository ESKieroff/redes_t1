import java.net.*;
import java.util.*;

public class UDPServer {
    private static Set<ClientInfo> clients = new HashSet<>();
    private static final int SERVER_PORT = 9999;

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
            System.out.println("[SERVIDOR UDP INICIADO] Aguardando mensagens...");
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("[RECEBIDO] " + message);
                
                // Armazena informações do cliente remetente
                clients.add(new ClientInfo(packet.getAddress(), packet.getPort()));

                // Retransmite a mensagem para todos os outros clientes
                for (ClientInfo client : clients) {
                    if (!(client.getAddress().equals(packet.getAddress()) && client.getPort() == packet.getPort())) {
                        DatagramPacket sendPacket = new DatagramPacket(
                                message.getBytes(), message.length(), client.getAddress(), client.getPort());
                        serverSocket.send(sendPacket);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Classe para armazenar informações dos clientes
    private static class ClientInfo {
        private InetAddress address;
        private int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientInfo that = (ClientInfo) o;
            return port == that.port && address.equals(that.address);
        }

        @Override
        public int hashCode() {
            return Objects.hash(address, port);
        }
    }
}
