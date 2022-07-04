/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import controllers.mainController;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utils.ThreadUtils;

/**
 *
 * @author GraphX
 */
public class GamedataManager extends Thread {

    private final double width;
    private final double height;

    public GamedataManager(double width, double height, mainController controller) {
        this.width = width;
        this.height = height;
        this.controller = controller;
    }

    private final mainController controller;

    @Override
    public void run() {
        boolean loaded = true;
        GamedataBrickCreating storage = new GamedataBrickCreating();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleExecutor(scheduledExecutorService, storage);
        while (loaded) {

            try {
                Thread.sleep(1);
                Platform.runLater(() -> controller.createBricksSync(storage.getBricks()));
            } catch (InterruptedException ex) {
                Logger.getLogger(GamedataManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            loaded = false;
        }
        ThreadUtils.stopExecutor(scheduledExecutorService, 1, TimeUnit.SECONDS);
    }

    private void scheduleExecutor(ScheduledExecutorService scheduledExecutorService, GamedataBrickCreating storage) {
        // start producer
        scheduledExecutorService.schedule(() -> {
            for (double i = height; i > 0; i = i - 80) {
                for (double j = width; j > 0; j = j - 55) {
                    try {
                         Rectangle rectangle = new Rectangle(j, i, 30, 30);
                         rectangle.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                        storage.addBricks(rectangle);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GamedataManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }, 10, TimeUnit.MILLISECONDS);
    }

}
