import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Server {
    public static LinkedList<ServerConnection> serverList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        short port = 0;
        do {
            System.out.println("Enter port: ");
            if (in.hasNextShort())
                port = in.nextShort();
            else
                System.out.println("Incorrect input!");
            in.nextLine();
        } while (port <= 0);
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server started...");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerConnection(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }

    }
}
