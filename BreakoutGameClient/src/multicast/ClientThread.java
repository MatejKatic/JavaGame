package multicast;

import controllers.gameController;
import data.Gamedata;
import utils.ByteUtils;
import javafx.application.Platform;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread extends Thread {


    private static final String PROPERTIES_FILE = "socket.properties";
    private static final String CLIENT_PORT = "CLIENT_PORT";
    private static final String GROUP = "GROUP";
    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final gameController controller;

    public ClientThread(gameController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        // we use new Socket for each client call
        try (MulticastSocket client = new MulticastSocket(Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT)))) {

            InetAddress groupAddress = InetAddress.getByName(PROPERTIES.getProperty(GROUP));
            System.err.println(controller.hashCode() + " joining group");
            client.joinGroup(groupAddress);

            while (true) {
                System.err.println(controller.hashCode() + " listening...");

                // first we read the payload length
                byte[] numberOfSuspectBytes = new byte[5];
                DatagramPacket packet = new DatagramPacket(numberOfSuspectBytes, numberOfSuspectBytes.length);
                client.receive(packet);
                int length = ByteUtils.byteArrayToInt(numberOfSuspectBytes);

                // we can read payload of that length
                byte[] suspectBytes = new byte[length];
                packet = new DatagramPacket(suspectBytes, suspectBytes.length);
                client.receive(packet);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(suspectBytes);
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    Gamedata gamedata = (Gamedata) ois.readObject();
                    // send to Event Queue to be processed by the application thread
                    Platform.runLater(() -> {
                        controller.showData(gamedata);
                    });
                }
            }

        } catch (SocketException | UnknownHostException e) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
