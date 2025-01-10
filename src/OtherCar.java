import bagel.Image;
import bagel.Input;
import java.util.Properties;
/**
 * Class representing the other NPC Cars in the game. OtherCars can collide with other cars and spawn randomly
 * with random image.
 */
public class OtherCar extends NPC {
    public OtherCar(Properties props) {
        super(props, new Image(getRandomImage(props)),
                "gameObjects.otherCar.minSpeedY",
                "gameObjects.otherCar.maxSpeedY");
    }

    /**
     * Update method to move the car vertically upwards by its speed.
     * @param input The keyboard input
     */
    @Override
    public void update(Input input) {
        if(this.getShouldChangeSpeedY()){
            setNewSpeed();
        }
        if (input != null) {
            adjustToInputMovement(input);
        }
        move();
        draw();

        // Render smoke if damaged
        if (this.getSmoke() != null && this.getSmoke().getFrames()>0) {
            this.getSmoke().update(input);
        }

        if (this.getFire() != null && this.getFire().isActive()) {
            this.getFire().update(input);  // Render the fire
        }
    }

    /**
     * Randomly select between two car images.
     * @param props The source of the image path
     * @return Path to the car image file.
     */
    private static String getRandomImage(Properties props) {
        String baseImagePath = props.getProperty("gameObjects.otherCar.image");

        // Randomly choose between the pictures
        int imageNumber = MiscUtils.selectAValue(1, 2);
        return String.format(baseImagePath, imageNumber);
    }
}