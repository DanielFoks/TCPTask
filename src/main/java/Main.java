import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {


    public static void main(String[] args) {
        try {
            for (int i = 0; i < 100; i++) {
                TCPClient tcpClient = new TCPClient("Hello",new Socket(InetAddress.getByName("localhost"),33123));
                tcpClient.sendMessage();
                tcpClient.closeSocket();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
