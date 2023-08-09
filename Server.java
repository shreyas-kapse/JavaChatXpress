import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.PKIXBuilderParameters;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() throws IOException {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new connection is established");
                ClientHandler clienthandler = new ClientHandler(socket);
                Thread thread = new Thread(clienthandler);
                thread.start();

            }
        } catch (IOException e) {
            System.out.println(e);

        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}