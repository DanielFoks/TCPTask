import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPServer extends Thread {
    private final Socket socket;
    private final static AtomicInteger countOfClients = new AtomicInteger(0);

    public final static AtomicInteger countOfConnectedClients = new AtomicInteger(0);

    private static final Logger log = Logger.getLogger(TCPServer.class);

    public TCPServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            sleep(1000);
            InputStream inputStream = socket.getInputStream();

            OutputStream outputStream = socket.getOutputStream();

            byte buf[] = new byte[64 * 1024];

            int r = inputStream.read(buf);

            String data = new String(buf, 0, r);

            log.info("Message: \"" + data + "\"" + " was received");

            data = "Client № " + countOfClients + " -> message(" + data + ")";

            outputStream.write(data.getBytes());

            log.info("Message: \"" + data + "\"" + " was sent");
        } catch (IOException e) {
            log.error("Connection problems. Connection will be closed");
            log.error(e.getMessage());
        } catch (InterruptedException e) {
            log.error("Troubles with the thread");
            log.error(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("Socket can not be closed");
                log.error(e.getStackTrace());
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(33123, 0, InetAddress.getByName("localhost"));

            while (true) {
                TCPServer tcpServer = new TCPServer(serverSocket.accept());
                tcpServer.start();
                int threadNumber = countOfClients.incrementAndGet();
                log.info("New client № " + threadNumber);
                log.info("Clients connected: " + TCPServer.countOfConnectedClients.get());
            }
        } catch (IOException e) {
            log.error("Main server troubles");
            log.error(e.getStackTrace());
        }
    }

    public static AtomicInteger getCountOfClients() {
        return countOfClients;
    }
}