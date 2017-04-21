package com.andersen.tcp;

import com.andersen.tcp.interfaces.TCPClientInterface;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class is tcp client
 */
public class TCPClient implements TCPClientInterface {

    /**
     * Socket for connect to server and work with it
     */
    private Socket socket;

    /**
     * Stream to track incoming messages
     */
    private BufferedReader bufferedReader;

    /**
     * log4j logger
     */
    private static final Logger log = Logger.getLogger(TCPClient.class);


    public TCPClient() {
        log.info("Client was created");
    }

    /**
     * Create client socket.
     *
     * @param port        Port for connection
     * @param inetAddress Address for connection
     * @return Socket for connect to server and work with it
     * @throws IOException if connection was not successful
     */
    @Override
    public Socket createConnection(int port, InetAddress inetAddress) {
        try {
            socket = new Socket(inetAddress, port);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            log.info("Connection was successful to: " + inetAddress);
        } catch (IOException e) {
            log.error("Connection was not successful: " + e.getMessage(), e);
        }
        return socket;
    }

    /**
     * Sends message to server.
     *
     * @param message Message to be sent.
     * @return true if message was sent. False if was not.
     * @throws IOException if can not send the message.
     */
    @Override
    public synchronized boolean sendMessage(String message) {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(message);
            if (log.isDebugEnabled()) {
                log.debug("Message: \"" + message + "\"" + " was sent");
            }
            return true;
        } catch (IOException e) {
            log.error("Can not send the message: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Receives message from client.
     *
     * @return Message that was received. NULL if the message was not received.
     * @throws IOException if message can not be received.
     */
    @Override
    public synchronized String receiveMessage() {
        String message = null;
        try {
            message = bufferedReader.readLine();
            if (log.isDebugEnabled()) {
                log.debug("Message: \"" + message + "\"" + " was received");
            }

        } catch (IOException e) {
            log.error("Message can not be received: " + e.getMessage(), e);
        }
        return message;
    }

    /**
     * Disconnect from server
     *
     * @throws IOException if socket can not be closed.
     */
    @Override
    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            log.error("Socket can not be closed: " + e.getMessage(), e);
        }
    }
}