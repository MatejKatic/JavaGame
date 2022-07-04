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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author GraphX
 */
public class TimelineThread extends Thread {

    private final mainController controller;
    private final Circle circle;
    private final Rectangle bottomZone;
        private List<Rectangle> bricks = new ArrayList<>();

    public TimelineThread(mainController controller,Circle circle, Rectangle bottomZone, List<Rectangle> bricks) {
        this.controller = controller;
        this.circle = circle;
        this.bottomZone = bottomZone;
          this.bricks.addAll(bricks);
        setDaemon(true); // this thread will stop when main stops
    }

    @Override
    public void run() {

        while (true) {

            PaddleAction();
            try {
                // sleep on current thread
                Thread.sleep(10);
                if (circle.getBoundsInParent().intersects(bottomZone.getBoundsInParent())) {
                    CollisionAction();
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

    private synchronized void PaddleAction() {
        Platform.runLater(() -> controller.movePaddle());
    }

    private synchronized void CollisionAction() {
        Platform.runLater(() -> controller.checkCollisionBottomZone());
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
