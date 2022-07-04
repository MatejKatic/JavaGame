/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author GraphX
 */
public class ScoreTask extends Task<Void> {

    private List<Rectangle> bricks = new ArrayList<>();
    private Circle circle;
    private final Rectangle bottomZone;
    private int score;

    public ScoreTask(List<Rectangle> bricks, Circle circle, int score, Rectangle bottomZone) {
        this.bricks.addAll(bricks);
        this.circle = circle;
        this.score = score + 10;
        this.bottomZone = bottomZone;
    }

    @Override
    protected Void call() throws Exception {
        while (!bricks.isEmpty()) {
            if (bricks.removeIf(brick -> checkCollision(brick))) {
                updateMessage(String.valueOf(score));
                score += 10;
            }  
            if (circle.getBoundsInParent().intersects(bottomZone.getBoundsInParent())) {
                bricks.removeAll(bricks);

            }

        }
        return null;
    }

    public synchronized boolean checkCollision(Rectangle brick) {
        if (circle.getBoundsInParent().intersects(brick.getBoundsInParent())) {
            return true;
        }
        return false;
    }

}
