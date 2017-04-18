package com.andersen.tcp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServerTest {

    @Before
    public void testServerConnection() {
        TCPServer tcpServer = new TCPServer();
        tcpServer.start();
    }

    @Test
    public void testTCPClient() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {

            executor.execute(() -> {
                TCPClient tcpClient = new TCPClient();

                try {
                    tcpClient.createConnection(11122, InetAddress.getLocalHost());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                tcpClient.sendMessage("hello");

                tcpClient.sendMessage("world");

                tcpClient.sendMessage("exit");

                int countOfReceivedMessages = 0;

                while (true) {

                    String receivedMessage = tcpClient.receiveMessage();

                    if (receivedMessage != null) {

                        countOfReceivedMessages++;

                        if (receivedMessage.equals("exit")) {
                            tcpClient.closeConnection();
                            break;
                        }

                    }
                }

                Assert.assertEquals(countOfReceivedMessages, 3);
            });
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
        }

        Assert.assertEquals(TCPServer.totalNumbersOfClients.get(), 1000);
    }
}