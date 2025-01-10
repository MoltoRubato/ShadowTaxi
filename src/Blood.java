import bagel.Image;
import java.util.Properties;

/**
 * Class representing the Blood effect in the game. Blood is rendered at the deaths of a person.
 */
public class Blood extends Effect{
    public Blood(Properties props, int x, int y) {
        super(props, new Image(props.getProperty("gameObjects.blood.image")), x, y,
                Integer.parseInt(props.getProperty("gameObjects.blood.ttl")),
                Integer.parseInt(props.getProperty("gameObjects.taxi.speedY")));
    }
}