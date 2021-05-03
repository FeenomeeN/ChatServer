import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ServerConnection extends Thread {
    private Socket socket;// сокет, через который сервер общается с клиентом,
    private BufferedReader in;// поток чтения из сокета
    private BufferedWriter out;// поток завписи в сокет

    public ServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        start();
    }

    @Override
    public void run() {
        String word;
        try {
            // первое сообщение отправленное сюда - это никнейм
            word = in.readLine();
            try {
                out.write(word + "\n");
                out.flush();// flush() нужен для выталкивания оставшихся данных
                // если такие есть, и очистки потока для дьнейших нужд
            } catch (IOException ignored) {
                System.err.println("Exception while writing user name " + word + " " + ignored );
            }
            try {
                while (true) {
                    word = in.readLine();
                    if(word.equals("stop")) {
                        this.downService();
                        break;
                    }
                    System.out.println("Echoing: " + word);
                    for (ServerConnection vr : Server.serverList) {
                        vr.send(word);// отослать принятое сообщение с привязанного клиента всем остальным влючая его
                    }
                }
            } catch (NullPointerException e) {
                System.out.println(Server.serverList.size());
                this.downService();
            }
        } catch (IOException e) {
            this.downService();
        }
    }
  //  отсылка одного сообщения клиенту по указанному потоку
    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (SocketException e) {

        } catch (IOException e) {
            System.err.println("Exception while sending message" + msg + " " + e);
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerConnection vr : Server.serverList) {
                    if (vr.equals(this)) vr.interrupt();
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException e) {
            System.err.println("Exception while shutting down user thread" + e);
        }
    }
}
