import bagel.Image;
import java.util.Properties;

/**
 * Class representing the Smoke Effect in the game. Smoke is rendered when a collision occurs on a Car.
 */
public class Smoke extends Effect {
    public Smoke(Properties props, int x, int y) {
        super(props, new Image(props.getProperty("gameObjects.smoke.image")), x, y,
                Integer.parseInt(props.getProperty("gameObjects.smoke.ttl")),
                Integer.parseInt(props.getProperty("gameObjects.taxi.speedY")) );
    }
}