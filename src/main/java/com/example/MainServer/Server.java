package com.example.MainServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;


public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    public void start() throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(23);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            logger.info("User connect");

            Thread clientThread = new Thread(() -> {
                try {
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                    String numsAsk;
                    String maskAsk;

                    while (true) {

                        for (; ; ) {

                            outToClient.writeBytes("Enter num : ");
                            numsAsk = inFromClient.readLine();
                            outToClient.writeBytes("num = " + numsAsk + "\n");
                            boolean b = numsAsk.matches("[-+]?\\d+");
                            if (b) break;
                            logger.info("Error type of user`s input");
                        }

                        outToClient.writeBytes("Enter mask : ");
                        maskAsk = inFromClient.readLine();

                        QueueThread queueThread = new QueueThread();
                        queueThread.menu(Integer.parseInt(numsAsk), maskAsk, outToClient);
                    }
                } catch (SocketException e) {
                    logger.info("Connection close user`s or lost connection");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        connectionSocket.close();
                        logger.info("Connection close");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            clientThread.start();
        }
    }
}
