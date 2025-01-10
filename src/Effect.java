import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.Properties;

/**
 * Abstract Class representing the Effects in the game. Effects are rendered on screen on a gameObject.
 */
public abstract class Effect extends GameObject implements Scrollable{
    private int frames;
    private int moveY;
    private final int SPEED_Y;

    public Effect(Properties props, Image image, int x, int y, int frames, int speed) {
        super(props, x, y, image, 0);
        this.frames = frames;
        this.SPEED_Y = speed;
    }

    /**
     * Move the Effect according to the keyboard input.
     * @param input The current keyboard input.
     */
    @Override
    public void update(Input input) {
        if (frames > 0) {
            if (input != null) {
                adjustToInputMovement(input);
            }
            move();

            // render Effect
            IMAGE.draw(this.getX(), this.getY());
            frames--;
        }
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
        if (input.isDown(Keys.UP)) {
            moveY = 1;
        }  else if(input.wasReleased(Keys.UP)) {
            moveY = 0;
        }
    }

    public boolean isActive() {
        return frames > 0;
    }
    public int getFrames() {
        return frames;
    }
}
