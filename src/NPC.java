import bagel.Image;

import java.util.Random;
import java.util.Properties;

/**
 * Abstract class representing the Non player Entities in the game, including enemyCars and other cars.
 * They move on their own and are spawned randomly.
 */
public abstract class NPC extends Car {
    protected static final int MIN_Y = -50;
    protected static final int MAX_Y = 768;
    protected static int[] LANE_POSITIONS;
    protected final int MIN_SPEED;
    protected final int MAX_SPEED;
    protected final int TAXI_SPEED_Y;
    private int speedY;

    public NPC(Properties props, Image image, String minSpeedYProperty, String maxSpeedYProperty) {
       super(0,0, image, Double.parseDouble(props.getProperty("gameObjects.otherCar.radius")),
               Double.parseDouble(props.getProperty("gameObjects.otherCar.damage")),
               Double.parseDouble(props.getProperty("gameObjects.otherCar.health")), props);
        LANE_POSITIONS = new int[] {
                Integer.parseInt(props.getProperty("roadLaneCenter1")),
                Integer.parseInt(props.getProperty("roadLaneCenter2")),
                Integer.parseInt(props.getProperty("roadLaneCenter3"))
        };

        // Randomly select x from the three possible lane centers
        this.setX(LANE_POSITIONS[new Random().nextInt(LANE_POSITIONS.length)]);
        // Randomly select y
        this.setY(MiscUtils.selectAValue(MIN_Y, MAX_Y));

        this.MIN_SPEED = Integer.parseInt(props.getProperty(minSpeedYProperty));
        this.MAX_SPEED = Integer.parseInt(props.getProperty(maxSpeedYProperty));

        // Randomly fix the speed between MIN_SPEED and MAX_SPEED
        this.speedY = MiscUtils.getRandomInt(MIN_SPEED, MAX_SPEED);
        this.TAXI_SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.taxi.speedY"));
    }

    /**
     * Move NPC at their own speed.
     */
    @Override
    public void move() {
        if(!this.getIsInCollisionTimeOut()){
            this.setY(this.getY() - speedY);
        }

        this.setY(this.getY() + TAXI_SPEED_Y * this.getMoveY());
    }

    /**
     * Reset the speed after collision timeout ends
     */
    public void setNewSpeed() {
        this.speedY = MiscUtils.getRandomInt(MIN_SPEED, MAX_SPEED);
        this.setShouldChangeSpeedY(false);
    }
}

