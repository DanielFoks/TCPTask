package com.andersen.tcp.Interfaces;

import java.io.BufferedReader;
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
    String receiveMessage(BufferedReader bufferedReader);

    /**
     * Close connection with client
     */
    void closeConnection();

}
