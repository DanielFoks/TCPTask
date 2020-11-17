package com.andersen.tcp.Interfaces;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * This interface includes basic common methods for working with the TCP server and the TCP client
 */
public interface TCPInterface {

    /**
     * @param message Message to be sent
     * @throws UnknownHostException Wrong address
     */
    void sendMessage(String message) throws UnknownHostException;

    /**
     * @param bufferedReader Stream to track incoming messages
     * @return Message that was sent
     */
    default String receiveMessage(BufferedReader bufferedReader, Logger log) {
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
     * Close connection with client
     */
    void closeConnection();

}
