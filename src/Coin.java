import java.util.Properties;

/**
 * Class representing coins in the game. Coins can be collected by either the player or the taxi.
 * It will set one level higher priority for the passengers that are waiting to get-in or already in the taxi.
 */
public class Coin extends PowerUp {
    public Coin(int x, int y, Properties props) {
        super(x, y, props, "gameObjects.coin.image", "gameObjects.coin.radius", "gameObjects.coin.maxFrames");
    }

    /**
     * Apply the effect of the coin on the priority of the passenger.
     * @param priority The current priority of the passenger.
     * @return The new priority of the passenger.
     */
    public Integer applyEffect(Integer priority) {
        if (this.getFramesActive() <= MAX_FRAMES && priority > 1) {
            priority -= 1;
        }
        return priority;
    }

    /**
     * Handles the coin collection by a taxi.
     * @param taxi the Taxi instance that is attempting to collect the coin
     */
    @Override
    public void collide(Taxi taxi) {
        if (checkCollision(taxi)) {
            if(taxi.getDriver()!=null){
                taxi.getDriver().collectPower(this);
                setIsCollided();
            }
        }
    }

    /**
     * Handles the coin collection by a driver.
     * @param driver the Driver that is attempting to collect the coin
     */
    @Override
    public void collide(Driver driver) {
        if (checkCollision(driver)) {
            driver.collectPower(this);
            setIsCollided();
        }
    }
}
