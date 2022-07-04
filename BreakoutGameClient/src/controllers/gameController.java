/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.Gamedata;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import multicast.ClientThread;

/**
 *
 * @author GraphX
 */
public class gameController implements Initializable {

    @FXML
    private Label lblScore;
    @FXML
    private AnchorPane scene;

    @FXML
    private Circle circle;

    @FXML
    private Rectangle paddle;

    @FXML
    private Label lblMain;

    private ClientThread clientThread;
    private double width = 550;
    private double height = 200;
    private int spaceCheck = 1;
    final private List<Rectangle> bricks = new ArrayList<>();
    private double deltaX = -1;
    private double deltaY = -3;
    private int count = 0;
    private int score = 0;

    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (bricks.removeIf(brick -> checkCollisionBrick(brick))) {
                score += 10;
                lblScore.setText("Score: " + score);
            }
            bricks.removeIf(brick -> checkCollisionBrick(brick));
        }
    }));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initClientThread();
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void initClientThread() {
        clientThread = new ClientThread(this);
        clientThread.setDaemon(true);
        clientThread.start();
    }

    public void checkCollisionScene(Node node) {
        Bounds bounds = node.getBoundsInLocal();
        boolean rightBorder = circle.getLayoutX() >= (bounds.getMaxX() - circle.getRadius());
        boolean leftBorder = circle.getLayoutX() <= (bounds.getMinX() + circle.getRadius());
        boolean bottomBorder = circle.getLayoutY() >= (bounds.getMaxY() - circle.getRadius());
        boolean topBorder = circle.getLayoutY() <= (bounds.getMinY() + circle.getRadius());

        if (rightBorder || leftBorder) {
            deltaX *= -1;
        }
        if (bottomBorder || topBorder) {
            deltaY *= -1;
        }
    }

    public boolean checkCollisionBrick(Rectangle brick) {
        if (circle.getBoundsInParent().intersects(brick.getBoundsInParent())) {
            boolean rightBorder = circle.getLayoutX() >= ((brick.getX() + brick.getWidth()) - circle.getRadius());
            boolean leftBorder = circle.getLayoutX() <= (brick.getX() + circle.getRadius());
            boolean bottomBorder = circle.getLayoutY() >= ((brick.getY() + brick.getHeight()) - circle.getRadius());
            boolean topBorder = circle.getLayoutY() <= (brick.getY() + circle.getRadius());

            if (rightBorder || leftBorder) {
                deltaX *= -1;
            }
            if (bottomBorder || topBorder) {
                deltaY *= -1;
            }
            scene.getChildren().remove(brick);

            return true;
        }
        return false;
    }

    public void checkCollisionPaddle(Rectangle paddle) {

        if (circle.getBoundsInParent().intersects(paddle.getBoundsInParent())) {

            boolean rightBorder = circle.getLayoutX() >= ((paddle.getLayoutX() + paddle.getWidth()) - circle.getRadius());
            boolean leftBorder = circle.getLayoutX() <= (paddle.getLayoutX() + circle.getRadius());
            boolean bottomBorder = circle.getLayoutY() >= ((paddle.getLayoutY() + paddle.getHeight()) - circle.getRadius());
            boolean topBorder = circle.getLayoutY() <= (paddle.getLayoutY() + circle.getRadius());

            if (rightBorder || leftBorder) {
                deltaX *= -1;
            }
            if (bottomBorder || topBorder) {
                deltaY *= -1;
            }
        }

    }

    public void showData(Gamedata gameData) {
        score = (int) gameData.getScore();
        paddle.setLayoutX(gameData.getPaddleX());
        circle.setLayoutX(gameData.getCircleX());
        circle.setLayoutY(gameData.getCircleY());

        for (double i = height; i > 0; i = i - 100) {
            for (double j = width; j > 0; j = j - 55) {
                if (spaceCheck % 2 == 0) {
                    if (count <= gameData.getNumberOfBricks() * 2) {
                        Rectangle rectangle = new Rectangle(j, i, 30, 30);
                        rectangle.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                        scene.getChildren().add(rectangle);
                        bricks.add(rectangle);
                    }
                }
                count++;
                spaceCheck++;

            }

        }

        lblMain.setVisible(false);
        timeline.play();

    }

}
