package com.sylvaincorre.acarsserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.function.Function;

/**
 * UDP server to handle ACARS messages received from acarsdec.
 *
 * Created by Sylvain Corré on 01/08/16.
 */
public class Server {
    /**
     * The max packet size.
     */
    private static final int MAX_PACKET_SIZE = 512;

    /**
     * The message handler. This method is invoked for each message received.
     */
    private final Function<AcarsMessage, Void> messageHandler;

    /**
     * The server socket.
     */
    private final DatagramSocket serverSocket;

    /**
     * Constructor.
     *
     * @param port the port number on which the server is listening for incoming messages
     * @param messageHandler the message handler
     * @throws SocketException if the server socket could not be created
     */
    public Server(int port, Function<AcarsMessage, Void> messageHandler) throws SocketException {
        this.messageHandler = messageHandler;
        this.serverSocket = new DatagramSocket(port);
    }

    /**
     * Runs forever the server, passing every ACARS message received to the message handler.
     */
    public void run() {
        byte[] packetBuffer = new byte[MAX_PACKET_SIZE];

        System.out.println("Listening on port " + serverSocket.getLocalPort() + "...");

        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(packetBuffer, MAX_PACKET_SIZE);
                serverSocket.receive(packet);

                AcarsMessage message = new AcarsMessage(
                        new String(packetBuffer, packet.getOffset(), packet.getLength()));
                messageHandler.apply(message);
            }
        } catch (IOException e) {
            System.err.println("Fail to receive UDP packet (" + e.getMessage() + ")");
        }
    }
}
