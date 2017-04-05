import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread{
    private Socket socket;
    private static int countOfClients;

    public TCPServer(Socket socket){
        this.socket = socket;

        start();
    }

    @Override
    public void run() {
        try {
            this.sleep(1000);

            InputStream inputStream = socket.getInputStream();

            OutputStream outputStream = socket.getOutputStream();

            byte buf[] = new byte[64*1024];

            int r = inputStream.read(buf);

            String data = new String(buf, 0, r);

            data = "Client № "+countOfClients+" -> message("+data+")";

            outputStream.write(data.getBytes());

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        countOfClients = 0;

        try {
            ServerSocket serverSocket = new ServerSocket(33123,0, InetAddress.getByName("localhost"));

            while (true){
                new TCPServer(serverSocket.accept());
                countOfClients++;
                System.out.println("New client № "+countOfClients);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
