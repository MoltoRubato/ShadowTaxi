/*
 * This class is based on the Background class from the solution provided for Project 1.
 * Original code from: Project 1 Solution.
 * Modifications have been made to implement rain functionality.
 */

import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.Properties;

/**
 * A class representing the background of the game play.
 */
public class Background implements Scrollable {
    private final int WINDOW_HEIGHT;
    private final Image IMAGE;
    private final Image RAIN_IMAGE;
    private final int SPEED_Y;
    private final int X;
    private int y;
    private int moveY;
    private Image currentImage;

    public Background(int x, int y, Properties props) {
        this.X = x;
        this.y = y;
        this.moveY = 0;
        this.SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.taxi.speedY"));
        this.IMAGE = new Image(props.getProperty("backgroundImage.sunny"));
        this.RAIN_IMAGE = new Image(props.getProperty("backgroundImage.raining"));
        this.WINDOW_HEIGHT = Integer.parseInt(props.getProperty("window.height"));
        this.currentImage = IMAGE; // The weather is sunny by default
    }

    /**
     * Move the background in y direction according to the keyboard input. And render the background image.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input, Background background, String currentWeather) {
        if(input != null) {
            adjustToInputMovement(input);
        }

        // Set the image based on the current weather
        if (currentWeather.equals("SUNNY")) {
            currentImage = IMAGE;
        } else if (currentWeather.equals("RAINING")) {
            currentImage = RAIN_IMAGE;
        }

        move();
        draw();

        if (y >= WINDOW_HEIGHT * 1.5) {
            y = background.getY() - WINDOW_HEIGHT;
        }
    }

    public int getY() {
        return y;
    }

    /**
     * Move the GameObject object in the y-direction based on the speedY attribute.
     */
    @Override
    public void move() {
        this.y += SPEED_Y * moveY;
    }

    /**
     * Draw the GameObject object into the screen.
     */
    public void draw() {
        currentImage.draw(X, y);
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
