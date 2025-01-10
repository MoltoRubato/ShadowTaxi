import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.util.Properties;
/**
 * Abstract Class representing the Power-ups in the game.
 * A Power-up can be collected by the player in a taxi or as a driver.
 */
public abstract class PowerUp extends GameObject implements Collidable, Scrollable{
    protected final int MAX_FRAMES;
    protected final int SPEED_Y;
    private int moveY;
    private boolean isCollided;
    private int framesActive = 0;

    public PowerUp(int x, int y, Properties props, String imageProp, String radiusProp, String maxFramesProp) {
        super(props,x,y, new Image(props.getProperty(imageProp)), Double.parseDouble(props.getProperty(radiusProp)));
        this.moveY = 0;
        this.SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.taxi.speedY"));
        this.MAX_FRAMES = Integer.parseInt(props.getProperty(maxFramesProp));
    }

    /**
     * Adjust rendering and movement according to the input.
     * @param input The keyboard input
     */
    @Override
    public void update(Input input) {
        if (isCollided) {
            framesActive++;
        } else {
            if (input != null) {
                adjustToInputMovement(input);
            }
            move();
            draw();
        }
    }

    @Override
    public void move() {
        this.setY(this.getY() + SPEED_Y * moveY);
    }

    /**
     * Adjust position relative to the input.
     * @param input The keyboard input
     */
    @Override
    public void adjustToInputMovement(Input input) {
        if (input.wasPressed(Keys.UP)) {
            moveY = 1;
        } else if (input.wasReleased(Keys.UP)) {
            moveY = 0;
        }
    }

    public abstract void collide(Taxi taxi);
    public abstract void collide(Driver driver);

    /**
     * Checks if a taxi or a driver had collected the power up
     * @param other The other GameObject
     * @return true if a collision occurs under the circumstances, false if not
     */
    @Override
    public boolean checkCollision(GameObject other){
        double collisionDistance = RADIUS + other.getRADIUS();
        double currDistance = Math.sqrt(Math.pow(this.getX() - other.getX(), 2) +
                Math.pow(this.getY() - other.getY(), 2));
        return currDistance <= collisionDistance;
    }

    public void setIsCollided() {
        this.isCollided = true;
    }

    public boolean getIsActive() {
        return isCollided && framesActive <= MAX_FRAMES && framesActive > 0;
    }

    public int getFramesActive() {
        return framesActive;
    }

    public int getMaxFrames() {
        return MAX_FRAMES;
    }
}
