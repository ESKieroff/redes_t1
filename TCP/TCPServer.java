import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TCPServer {
    private static final UserManager userManager = new UserManager();
    private static final Map<String, List<String>> followers = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> newsletters = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> newsletterMessages = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java TCPServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        loadData();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor escutando na porta " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + socket.getRemoteSocketAddress());
                new ClientHandler(socket, userManager).start();
            }
        }
    }

    private static void loadData() throws IOException {
        FileLoader.loadData(userManager.getUsers(), followers, newsletters, newsletterMessages);
    }
}
