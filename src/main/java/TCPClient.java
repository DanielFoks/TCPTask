import java.io.IOException;
import java.net.Socket;

public class TCPClient {
    private String message;
    private Socket socket;

    public TCPClient(String message, Socket socket) {
        this.message = message;
        this.socket = socket;
    }

    public String getMessage() {
        return message;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(){
        try {
            socket.getOutputStream().write(message.getBytes());

            byte buf[] = new byte[64*1024];
            int r = socket.getInputStream().read(buf);
            String data = new String(buf, 0, r);

            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
