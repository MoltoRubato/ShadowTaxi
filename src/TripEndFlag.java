/*
 * This class is based on the TripEndFlag class from the solution provided for Project 1.
 * Original code from: Project 1 Solution.
 */

import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.util.Properties;

/**
 * A class representing the trip end flag in the game play.
 * Objects of this class will only move up and down based on the keyboard input. No other functionalities needed.
 */
public class TripEndFlag extends GameObject implements Scrollable{
    private final int SPEED_Y;
    private int moveY;

    public TripEndFlag(int x, int y, Properties props) {
        super(props,x,y,new Image(props.getProperty("gameObjects.tripEndFlag.image")),
                Double.parseDouble(props.getProperty("gameObjects.tripEndFlag.radius")));
        this.moveY = 0;
        this.SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.taxi.speedY"));
    }

    /**
     * Move the object in y direction according to the keyboard input, and render the trip flag image.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void update(Input input) {
        if(input != null) {
            adjustToInputMovement(input);
        }

        move();
        draw();
    }

    /**
     * Move the GameObject object in the y-direction based on the speedY attribute.
     */
    @Override
    public void move() {
        this.setY(this.getY() + SPEED_Y * moveY);
    }

    /**
     * Adjust the movement direction in y-axis of the GameObject based on the keyboard input.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void adjustToInputMovement(Input input) {
        if (input.wasPressed(Keys.UP)) {
            moveY = 1;
        }  else if(input.wasReleased(Keys.UP)) {
            moveY = 0;
        }
    }
}
