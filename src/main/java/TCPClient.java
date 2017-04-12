import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
    private final String message;
    private String receivedMessage;
    private Socket socket;

    private static final Logger log = Logger.getLogger(TCPClient.class);


    public TCPClient(String message) {
        this.message = message;
        log.info("Client was created");
    }

    public String getMessage() {
        return message;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public void connectToServer(InetAddress inetAddress, int port) {
        try {
            socket = new Socket(inetAddress, port);
            TCPServer.countOfConnectedClients.incrementAndGet();
            log.info("CONNECTED CLIENTS: " + TCPServer.countOfConnectedClients.get());
            log.info("Connection was successful");
        } catch (IOException e) {
            log.error("Connection was not successful");
            log.error(e.getMessage());
        }
    }

    public void sendMessage() {
        try {
            socket.getOutputStream().write(message.getBytes());
            log.info("Message: \"" + message + "\"" + " was sent");

            byte buf[] = new byte[64 * 1024];
            int r = socket.getInputStream().read(buf);
            String data = new String(buf, 0, r);
            receivedMessage = data;
            log.info("Message: \"" + data + "\"" + " was received");
        } catch (IOException e) {
            log.error("Connection problems");
            log.error(e.getMessage());
        }
    }

    public void closeSocket() {
        try {
            socket.close();
            TCPServer.countOfConnectedClients.updateAndGet((x) -> x - 1);
        } catch (IOException e) {
            log.error("Socket can not be closed");
            log.error(e.getMessage());
        }
    }
}
