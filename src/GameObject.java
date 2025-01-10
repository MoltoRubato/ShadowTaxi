import java.util.Properties;
import bagel.*;

/**
 * Abstract Class representing all GameObjects in the game. A class is a gameObject if it has associated x,y
 * coordinates and an Image
 */
public abstract class GameObject {
    protected final Properties GAME_PROPS;
    private int x;
    private int y;
    protected final Image IMAGE;
    protected final double RADIUS;

    public GameObject(Properties properties, int x, int y, Image image, double radius) {
        this.GAME_PROPS = properties;
        this.x = x;
        this.y = y;
        this.IMAGE = image;
        this.RADIUS = radius;
    }

    protected abstract void update(Input input);

    protected void draw() {
        IMAGE.draw(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getRADIUS() {
        return RADIUS;
    }
}