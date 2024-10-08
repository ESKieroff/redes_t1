// Servidor UDP
// - Recebe um pacote de algum cliente
// - Separa o dado, o endereco IP e a porta deste cliente
// - Imprime o mensagem recebida

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPServer {
   public static void main(String args[]) throws Exception {
         if (args.length < 1) {
            System.out.println("Usage: java UDPServer <port>");
            return;
         }

         int port = Integer.parseInt(args[0]);

         // cria socket do servidor com a porta especificada
         DatagramSocket serverSocket = new DatagramSocket(port);

         while(true) {
            byte[] receiveData = new byte[1024];

            // declara o pacote a ser recebido
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // recebe o pacote do cliente
            serverSocket.receive(receivePacket);

            // obtem os dados, o endereco IP e a porta do cliente
            String sentence = new String(receivePacket.getData());
            InetAddress ipAddress = receivePacket.getAddress();
            int receivePort = receivePacket.getPort();

            // imprime remetente da mensagem
            System.out.println("Recebi mensagem de " + ipAddress.getHostAddress() + ":" + receivePort);

            // imprime a linha recebida do cliente
            System.out.println("Mensagem recebida: " + sentence);

            // cria pacote com o dado, o endereco do server e porta do servidor
            DatagramPacket sendPacket = new DatagramPacket(receiveData, receiveData.length, ipAddress, receivePort);

            // envia o pacote
            serverSocket.send(sendPacket);

            if (sentence.startsWith("FIM"))
               break;
         }

         serverSocket.close();
      }
}
