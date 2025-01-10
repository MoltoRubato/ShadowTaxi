import bagel.Image;
import java.util.Properties;
/**
 * Abstract class representing the Persons in the game. Persons have a health value and can be killed.
 */
public abstract class Person extends GameObject implements Damageable {
    private final double EPSILON = 1e-6;
    protected final static int COLLISION_TIMEOUT_FRAMES = 200;
    private final static int COLLISION_IMPACT_FRAMES = 10;
    private double health;
    private boolean dead;
    private boolean isInCollisionTimeOut;
    private boolean isHitByFireball;
    private int collisionTimeoutCounter;
    private boolean isInvincible;
    private Blood blood;

    public Person(int x, int y, Image image, double radius, double health, Properties props) {
        super(props, x, y, image, radius);
        this.health = health;
        this.dead = false;
        this.isInCollisionTimeOut = false;
        this.collisionTimeoutCounter = -1;
        this.isInvincible = false;
        this.isHitByFireball = false;
    }

    /**
     * Handle damage taken from collision.
     * @param damagePoints The damage taken
     */
    @Override
    public void takeDamage(double damagePoints) {
        if (!this.isInvincible){
            this.health -= damagePoints + EPSILON;
        }
        // Trigger blood effect
        if (this.health <= 0 && !dead) {
            this.dead = true;
            blood = new Blood(GAME_PROPS, this.getX(), this.getY());
        }
    }

    /**
     * Handle collision time out after a collision.
     */
    protected void handleCollisionTimeOut() {
        // Countdown for collision timeout
        if (collisionTimeoutCounter > 0) {
            collisionTimeoutCounter--;
        } else {
            isInCollisionTimeOut = false;
        }
    }

    /**
     Handle collision Impact frames with Fireballs.
     */
    protected void handleFireballImpact(){
        // Separate collided entities for the first 10 frames
        if (collisionTimeoutCounter > (COLLISION_TIMEOUT_FRAMES - COLLISION_IMPACT_FRAMES) && isHitByFireball) {
            this.setY(this.getY() - 1);
        }else {
            this.isHitByFireball = false;
        }
    }

    public double getHealth() {
        return health;
    }
    public void setInCollisionTimeOut(boolean inCollisionTimeOut) {
        isInCollisionTimeOut = inCollisionTimeOut;
    }

    public Blood getBlood() {
        return blood;
    }

    public boolean getIsInCollisionTimeOut() {
        return isInCollisionTimeOut;
    }

    public int getCollisionTimeoutCounter() {
        return collisionTimeoutCounter;
    }
    public void setCollisionTimeoutCounter(int collisionTimeoutCounter) {
        this.collisionTimeoutCounter = collisionTimeoutCounter;
    }
    public void setInvincible(boolean invincible) {
        isInvincible = invincible;
    }

    public boolean getIsInvincible() {
        return isInvincible;
    }

    public boolean isDead() {
        return dead;
    }
    public void setHitByFireball(boolean hitByFireball) {
        isHitByFireball = hitByFireball;
    }
}
