package com.andersen.tcp.Interfaces;

import java.net.ServerSocket;

public interface TCPServerInterface extends TCPInterface {

    /**
     * @param port Port for connection
     * @return Main socket for connection clients
     */
    ServerSocket createConnection(int port);

    /**
     * Close server socket
     */
    void closeServerConnection();

}
