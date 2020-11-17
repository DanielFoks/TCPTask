package com.andersen.tcp;

import com.andersen.tcp.Interfaces.TCPServerInterface;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is multithreading TCP server
 */
public class TCPServer extends Thread implements TCPServerInterface {

    /**
     * Socket for each client in his thread
     */
    private final Socket clientSocket;

    /**
     * Main socket for connection clients
     */
    private static ServerSocket serverSocket;

    /**
     * Boolean variable to identify is server initialize or the server connects the client
     */
    private boolean isInitializeServer;

    /**
     * log4j logger
     */
    private static final Logger log = Logger.getLogger(TCPServer.class);

    /**
     * This variable counts the number of clients that have been connected for the entire server lifetime
     */
    public static AtomicInteger totalNumbersOfClients = new AtomicInteger(0);

    /**
     * This variable counts the number of clients that are connected now
     */
    private static AtomicInteger connectedClients = new AtomicInteger(0);


    public TCPServer(Socket socket) {
        this.clientSocket = socket;
        this.isInitializeServer = false;
    }

    public TCPServer() {
        this.isInitializeServer = true;
        clientSocket = null;
    }


    @Override
    public void run() {

        if (isInitializeServer) {

            serverSocket = createConnection(11122);

            while (true) {

                TCPServer tcpServer;

                try {
                    tcpServer = new TCPServer(serverSocket.accept());
                    log.info("Total number of clients: " + totalNumbersOfClients.incrementAndGet());
                    log.info("Connected clients: " + connectedClients.incrementAndGet());
                    tcpServer.start();
                } catch (IOException e) {
                    log.error("ServerSocket troubles: " + e.getMessage(), e);
                }
            }

        } else {

            log.info("New client (" + clientSocket.getInetAddress() + ") was connected.");

            BufferedReader bufferedReader = null;

            try {
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                log.error("BufferedReader troubles: " + e.getMessage(), e);
            }


            while (true) {
                String receivedMessage = receiveMessage(bufferedReader, log);

                if (receivedMessage != null) {

                    sendMessage(receivedMessage);

                    if (receivedMessage.equals("exit")) {
                        connectedClients.updateAndGet((x) -> x - 1);
                        log.info("Client (" + clientSocket.getInetAddress() + ") was disconnected.");
                        log.info("Connected clients: " + connectedClients.incrementAndGet());
                        closeConnection();
                        break;
                    }
                }
            }
        }
    }

    /**
     * @param port Port for connection
     * @return Main socket for connection clients
     */
    @Override
    public ServerSocket createConnection(int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            log.error("Can not create ServerSocket: " + e.getMessage(), e);
            return serverSocket;
        }
    }


    /**
     * @param message Message to be sent
     */
    @Override
    public void sendMessage(String message) {
        PrintStream printStream;
        try {
            printStream = new PrintStream(clientSocket.getOutputStream(), true);
            printStream.println(message);
            if (log.isDebugEnabled()) {
                log.debug("Message: \"" + message + "\"" + " was sent");
            }
        } catch (IOException e) {
            log.error("Can not send the message : " + e.getMessage(), e);
        }
    }

    /**
     * Close connection with client
     */
    @Override
    public void closeConnection() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            log.error("Socket can not be closed: " + e.getMessage(), e);
        }
    }

    /**
     * Close server socket
     */
    @Override
    public void closeServerConnection() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("ServerSocket can not be closed: " + e.getMessage(), e);
        }
    }
}