import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCPTestIT {

    @Test
    public void testTCPClient() throws IOException {
        TCPClient[] tcpClients = new TCPClient[1000];
        TCPClient[] tcpClientsBuffer = new TCPClient[10];

        int tmp=0;
        for (int i = 0; i < 1000; i++) {
            if (tmp==10){
                for (int j = 0; j < 10; j++) {
                    tcpClientsBuffer[j].closeSocket();
                    tcpClientsBuffer[j] = null;
                }
                System.out.println("THE BUFFER WAS CLEARED");
                tmp=0;
            }
            tcpClients[i]= new TCPClient("Client № "+(i+1),new Socket(InetAddress.getByName("localhost"),33123));
            tcpClients[i].sendMessage();
                tcpClientsBuffer[tmp] = tcpClients[i];
                tmp++;
        }
    }
}
