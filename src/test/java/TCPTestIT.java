import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPTestIT {

    @Test
    public void testTCPClient() throws IOException {
        TCPClient[] tcpClients = new TCPClient[1000];
        TCPClient[] tcpClientsBuffer = new TCPClient[10];

        int tmp=0;
        for (int i = 0; i < 5; i++) {
            if (tmp==10){
                for (int j = 0; j < 10; j++) {
                    tcpClientsBuffer[j].closeSocket();
                    tcpClientsBuffer[j] = null;
                }
                tmp=0;
            }
            tcpClients[i]= new TCPClient("Client № "+(i+1));
            tcpClients[i].connectToServer(InetAddress.getByName("localhost"),33123);
            tcpClients[i].sendMessage();
            Assert.assertEquals(tcpClients[i].getMessage() + " -> message(Client № "+(i+1)+")", tcpClients[i].getReceivedMessage());
                tcpClientsBuffer[tmp] = tcpClients[i];
                tmp++;
        }
        Assert.assertEquals(TCPServer.getCountOfClients().get(),new AtomicInteger(5));
    }
}
