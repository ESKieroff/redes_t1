// Cliente UDP
// - Le uma linha do teclado
// - Envia o pacote (linha digitada) ao servidor UDP

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPClient {
   public static void main(String args[]) throws Exception {
      if (args.length < 2) {
         System.out.println("Usage: java UDPClient <server_ip> <port>");
         return;
      }

      String serverAddr = args[0];
      int port = Integer.parseInt(args[1]);

      // cria o stream do teclado
      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

      // declara socket cliente
      DatagramSocket clientSocket = new DatagramSocket();

      // obtem endereco IP do servidor a partir de uma string (IP ou nome)
      InetAddress ipAddress = InetAddress.getByName(serverAddr);

      byte[] sendData = new byte[1024];

      // le uma linha do teclado
      System.out.print("Digite uma mensagem: ");
      String sentence = inFromUser.readLine();
      sendData = sentence.getBytes();

      // cria pacote com o dado, o endereco do server e porta do servidor
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);

      // envia o pacote
      clientSocket.send(sendPacket);

      byte[] receiveData = new byte[1024];

      // declara o pacote a ser recebido
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

      // recebe o pacote do cliente
      clientSocket.receive(receivePacket);
   
      // obtem os dados, o endereco IP e a porta do cliente
      String sentenceResp = new String(receivePacket.getData());
      InetAddress receiveipAddress = receivePacket.getAddress();
      int receivePort = receivePacket.getPort();

      // imprime remetente da mensagem
      System.out.println("Recebi mensagem de " + receiveipAddress.getHostAddress() + ":" + receivePort);

      // imprime a linha recebida do cliente
      System.out.println("Mensagem recebida: " + sentenceResp);

      // fecha o cliente
      clientSocket.close();
   }
}
