/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import controllers.mainController;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author GraphX
 */
public class CricleThread extends Thread {

    private final Circle circle;
    private final Rectangle bottomZone;
    private final mainController controller;
    private List<Rectangle> bricks = new ArrayList<>();

    public CricleThread(Circle circle, Rectangle bottomZone, mainController controller, List<Rectangle> bricks) {
        this.circle = circle;
        this.bottomZone = bottomZone;
        this.controller = controller;
        this.bricks.addAll(bricks);
        setDaemon(true);
    }

    @Override
    public void run() {

        while (true) {
            CircleAction();
            try {
                Thread.sleep(10);
                if (circle.getBoundsInParent().intersects(bottomZone.getBoundsInParent())) {
                    System.out.println("stopping");
                    stopMe();
                }
                if (bricks.isEmpty()) {
                     System.out.println("stopping");
                      stopMe();
                }
            } catch (InterruptedException ex) {
                System.out.println("interrupted");
                return;
            }
        }
    }

    private synchronized void CircleAction() {
        Platform.runLater(() -> controller.CircleAction());
    }

    public void stopMe() throws InterruptedException {
        while (isAlive()) {
            // interrupt thread
            interrupt();
            // join this thread to caller thread
            join();
        }
    }

}
