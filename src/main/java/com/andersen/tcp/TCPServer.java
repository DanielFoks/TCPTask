package com.andersen.tcp;

import com.andersen.tcp.interfaces.TCPServerInterface;
import com.andersen.tcp.interfaces.TCPServerListenerInterface;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is TCP server.
 */
public class TCPServer extends Thread implements TCPServerInterface {

    /**
     * Main socket for connection clients.
     */
    private ServerSocket serverSocket;

    /**
     * log4j logger.
     */
    private static final Logger log = Logger.getLogger(TCPServer.class);

    /**
     * This variable counts the number of clients that have been connected for the entire server lifetime.
     */
    private AtomicInteger totalNumbersOfClients = new AtomicInteger(0);

    /**
     * This variable counts the number of clients that are connected now.
     */
    private AtomicInteger connectedClients = new AtomicInteger(0);


    /**
     * Service for threads control.
     */
    private ExecutorService executorService;

    /**
     * List of tasks to control their completeness.
     */
    private List<Future<?>> futureList = new ArrayList<>();

    public TCPServer() {
    }


    /**
     * Waiting for clients to connect.
     */
    @Override
    public void run() {

        serverSocket = createServerSocket(11122);

        executorService = Executors.newCachedThreadPool();


        while (true) {

            TCPServerListenerListener serverListener;
            Future<?> future;

            try {
                serverListener = new TCPServerListenerListener(serverSocket.accept());

                log.info("Total number of clients: " + totalNumbersOfClients.incrementAndGet());
                log.info("Connected clients: " + connectedClients.incrementAndGet());
                future = executorService.submit(serverListener);
                futureList.add(future);
            } catch (IOException e) {
                log.error("ServerSocket troubles: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Create Server Socket.
     *
     * @param port Port for connection.
     * @return Main socket for connection clients. NULL when can not create Server Socket.
     * @throws IOException if can not create ServerSocket.
     */
    @Override
    public ServerSocket createServerSocket(int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            log.error("Can not create ServerSocket: " + e.getMessage(), e);
            return serverSocket;
        }
    }

    /**
     * Close server socket and shutdown ExecutorService.
     *
     * @throws IOException if socket can not be closed.
     */
    @Override
    public void closeServerConnection() {
        try {
            executorService.shutdown();

            while (!executorService.isTerminated()) {
            }

            log.info("All threads were closed.");

            serverSocket.close();
        } catch (IOException e) {
            log.error("ServerSocket can not be closed: " + e.getMessage(), e);
        }
    }

    /**
     * Checks all tasks for completeness.
     *
     * @return true if all tasks completed.
     */
    @Override
    public boolean isAllFuturesDone() {
        boolean allDone = true;
        for (Future<?> future : futureList) {
            allDone &= future.isDone();
        }

        return allDone;
    }

    public AtomicInteger getTotalNumbersOfClients() {
        return totalNumbersOfClients;
    }


    /**
     * Multithreaded class for working with the client.
     */
    private class TCPServerListenerListener implements Runnable, TCPServerListenerInterface {

        /**
         * Clients socket.
         */
        private final Socket clientSocket;

        /**
         * Clients BufferedReader.
         */
        private BufferedReader bufferedReader;

        public TCPServerListenerListener(Socket socket) {
            this.clientSocket = socket;
        }


        /**
         * Receives message from client and send it to him.
         */
        @Override
        public void run() {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            log.info("New client (" + clientSocket.getInetAddress() + ") was connected.");

            try {
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                log.error("BufferedReader troubles: " + e.getMessage(), e);
            }


            while (true) {
                String receivedMessage = receiveMessage();

                if (receivedMessage != null) {

                    sendMessage(receivedMessage);

                    if (receivedMessage.equals("exit")) {
                        connectedClients.updateAndGet((x) -> x - 1);
                        log.info("Client (" + clientSocket.getInetAddress() + ") was disconnected.");
                        log.info("Connected clients: " + connectedClients.get());
                        closeConnection();
                        break;
                    }
                }
            }
        }


        /**
         * Sends message to client.
         *
         * @param message Message to be sent.
         * @return true if message was sent. False if was not.
         * @throws IOException if can not send the message.
         */
        @Override
        public synchronized boolean sendMessage(String message) {
            PrintStream printStream;
            try {
                printStream = new PrintStream(clientSocket.getOutputStream(), true);
                printStream.println(message);
                if (log.isDebugEnabled()) {
                    log.debug("Message: \"" + message + "\"" + " was sent");
                }
                return true;
            } catch (IOException e) {
                log.error("Can not send the message : " + e.getMessage(), e);
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
         * Close connection with client.
         *
         * @throws IOException if socket can not be closed.
         */
        @Override
        public synchronized void closeConnection() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("Socket can not be closed: " + e.getMessage(), e);
            }
        }
    }

}