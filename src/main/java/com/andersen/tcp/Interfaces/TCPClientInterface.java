package com.andersen.tcp.Interfaces;


import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This interface for TCP client
 */
public interface TCPClientInterface extends TCPInterface {

    /**
     * Created for more comfortable use
     *
     * @return Message that was sent
     */
    String receiveMessage();

    /**
     * @param port        Port for connection
     * @param inetAddress Address for connection
     * @return Socket for connect to server and work with it
     * @throws UnknownHostException Throw when address is incorrect
     */
    Socket createConnection(int port, InetAddress inetAddress) throws UnknownHostException;

}
