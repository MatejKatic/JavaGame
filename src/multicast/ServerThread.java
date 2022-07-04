/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multicast;


import data.Gamedata;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ByteUtils;

public class ServerThread extends Thread {

    private static final String PROPERTIES_FILE = "socket.properties";
    private static final String CLIENT_PORT = "CLIENT_PORT";
    private static final String GROUP = "GROUP";
    private static final Properties PROPERTIES = new Properties();

    static {
        try {

            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final LinkedBlockingDeque<Gamedata> gamedatas = new LinkedBlockingDeque<>();

    public void trigger(Gamedata gameData) {
        gamedatas.add(gameData);
    }

    @Override
    public void run() {
        try (DatagramSocket server = new DatagramSocket()) {
            System.err.println("Server multicasting on port:" + server.getLocalPort());
            while (true) {

                if (!gamedatas.isEmpty()) {

                    // serialize suspect in byte[]
                    byte[] suspectBytes;
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                        oos.writeObject(gamedatas.getFirst());
                        gamedatas.clear();
                        oos.flush();
                        suspectBytes = baos.toByteArray();
                    }

                    // before sending byte[], write byte[].length into payload
                    byte[] numberOfSuspectBytes = ByteUtils.intToByteArray(suspectBytes.length);
                    InetAddress groupAddress = InetAddress.getByName(PROPERTIES.getProperty(GROUP));
                    DatagramPacket packet = new DatagramPacket(
                            numberOfSuspectBytes,
                            numberOfSuspectBytes.length,
                            groupAddress, Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT))
                    );
                    server.send(packet);

                    // now send the payload
                    // payload must be at most 64KB!
                    packet = new DatagramPacket(
                            suspectBytes,
                            suspectBytes.length,
                            groupAddress, Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT)));
                    server.send(packet);
                }
            }
        } catch (SocketException e) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
