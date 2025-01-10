import bagel.Image;
import java.util.Properties;

/**
 * Class representing the Fire Effect in the game. Fire is rendered when a car is damaged.
 */
public class Fire extends Effect {
    public Fire(Properties props, int x, int y) {
        super(props, new Image(props.getProperty("gameObjects.fire.image")), x, y,
                Integer.parseInt(props.getProperty("gameObjects.fire.ttl")),
                Integer.parseInt(props.getProperty("gameObjects.taxi.speedY")));
    }
}