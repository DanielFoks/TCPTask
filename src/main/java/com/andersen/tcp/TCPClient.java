package com.andersen.tcp;

import com.andersen.tcp.Interfaces.TCPClientInterface;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
     * @param port        Port for connection
     * @param inetAddress Address for connection
     * @return Socket for connect to server and work with it
     * @throws UnknownHostException Throw when address is incorrect
     */
    @Override
    public Socket createConnection(int port, InetAddress inetAddress) throws UnknownHostException {
        try {
            socket = new Socket(inetAddress, port);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            log.info("Connection was successful");
        } catch (IOException e) {
            log.error("Connection was not successful: " + e.getMessage(), e);
        }
        return socket;
    }

    /**
     * @param message Message to be sent
     */
    @Override
    public void sendMessage(String message) {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(message);
            if (log.isDebugEnabled()) {
                log.debug("Message: \"" + message + "\"" + " was sent");
            }
        } catch (IOException e) {
            log.error("Can not send the message: " + e.getMessage(), e);
        }
    }

    /**
     * @param bufferedReader Stream to track incoming messages
     * @return Message that was sent
     */
    @Override
    public String receiveMessage(BufferedReader bufferedReader) {
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
     * @return Message that was sent
     */
    @Override
    public String receiveMessage() {
        return receiveMessage(this.bufferedReader);
    }

    /**
     * Disconnect from server
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