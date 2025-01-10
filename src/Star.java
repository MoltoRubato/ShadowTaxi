import java.util.*;

/**
 * Class representing the Star Power-up in the game.
 * The Star can make the player invincible to collision damage for a period of time.
 */
public class Star extends PowerUp {
    public Star(int x, int y, Properties props) {
        super(x, y, props, "gameObjects.invinciblePower.image",
                "gameObjects.invinciblePower.radius", "gameObjects.invinciblePower.maxFrames");
    }

    /**
     * Apply the invincible effect to a taxi
     *  @param taxi The involved Taxi
     */
    public void applyEffect(Taxi taxi) {
        taxi.setInvincible(this.getFramesActive() <= MAX_FRAMES);
    }

    /**
     * Apply the invincible effect to a driver
     *  @param driver The involved driver
     */
    public void applyEffect(Driver driver) {
        driver.setInvincible(this.getFramesActive() <= MAX_FRAMES);
    }

    /**
     * handle this power up collection by a taxi.
     * @param taxi The involved Taxi
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
     * handle this power up collection by a driver.
     * @param driver The involved driver
     */
    @Override
    public void collide(Driver driver) {
        if (checkCollision(driver)) {
            driver.collectPower(this);
            setIsCollided();
        }
    }
}
