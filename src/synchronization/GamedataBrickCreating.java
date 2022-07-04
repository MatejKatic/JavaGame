/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronization;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author GraphX
 */
public class GamedataBrickCreating {

    private final BlockingQueue<Rectangle> bricks = new LinkedBlockingQueue<>();

    public void addBricks(Rectangle rectangle) throws InterruptedException {
        bricks.put(rectangle);
    }

    public BlockingQueue<Rectangle> getBricks() {
        return bricks;
    }

}
