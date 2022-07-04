/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class Gamedata implements Serializable {

    private static final long serialVersionUID = 1L;

    public Gamedata() {
    }

    private int NumberOfBricks;
    private double score;
    private double paddleX;
    private double circleX;
    private double circleY;

    public Gamedata(int numberOfBricks, double score, double paddleX, double circleX, double circleY) {
        this.NumberOfBricks = numberOfBricks;
        this.score = score;
        this.paddleX = paddleX;
        this.circleX = circleX;
        this.circleY = circleY;
    }

    public int getNumberOfBricks() {
        return NumberOfBricks;
    }

    public void setNumberOfBricks(int numberOfBricks) {
        this.NumberOfBricks = numberOfBricks;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getPaddleX() {
        return paddleX;
    }

    public void setPaddleX(double paddleX) {
        this.paddleX = paddleX;
    }


    public double getCircleX() {
        return circleX;
    }

    public void setCircleX(double circleX) {
        this.circleX = circleX;
    }

    public double getCircleY() {
        return circleY;
    }

    public void setCircleY(double circleY) {
        this.circleY = circleY;
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(NumberOfBricks);
        out.writeDouble(score);
        out.writeDouble(paddleX);
        out.writeDouble(circleX);
        out.writeDouble(circleY);

    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        NumberOfBricks = in.readInt();
        score = in.readDouble();
        paddleX = in.readDouble();
        circleX = in.readDouble();
        circleY = in.readDouble();
    }





}
