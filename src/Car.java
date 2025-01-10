import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.Properties;

/**
 * Class representing Cars in the game. Cars can collide with each other and can become damaged.
 */
public abstract class Car extends GameObject implements Damageable, Collidable, Scrollable {
    private final double EPSILON = 1e-6;
    private final double INFLICTED_DAMAGE;
    protected final static int COLLISION_TIMEOUT_FRAMES = 200;
    private final static int COLLISION_IMPACT_FRAMES = 10;
    private double health;
    private boolean damaged;
    private boolean isInCollisionTimeOut;
    private boolean isHitByFireball;
    private int collisionTimeoutCounter;
    private boolean isInvincible;
    private boolean shouldChangeSpeedY;
    private int moveY;

    private Fire fire;
    private Smoke smoke;

    public Car(int x, int y, Image image, double radius, double inflictedDamage, double health, Properties props) {
        super(props, x, y, image, radius);
        this.INFLICTED_DAMAGE = inflictedDamage;
        this.health = health;
        this.damaged = false;
        this.isInCollisionTimeOut = false;
        this.isHitByFireball = false;
        this.collisionTimeoutCounter = -1;
        this.shouldChangeSpeedY = false;
        this.isInvincible = false;
    }

    /**
     Check if this Car instance is colliding with another GameObject. Returns a boolean.
     * @param other the GameObject to check for collision with this Car
     * @return true if this Car is colliding with the specified GameObject;
     */
    @Override
    public boolean checkCollision(GameObject other) {
        double distance = Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2));
        return distance < (this.RADIUS + other.RADIUS);
    }

    /**
     Handle damages taken from collisions.
     * @param damagePoints the amount of damage to be inflicted on the Car's health
     */
    @Override
    public void takeDamage(double damagePoints) {
        this.health -= damagePoints + EPSILON;
        // Render a smoke effect
        smoke = new Smoke(GAME_PROPS, this.getX(), this.getY());

        // Check if the Car is damaged
        if (this.health <= 0 && !damaged) {
            this.damaged = true;
            fire = new Fire(GAME_PROPS, this.getX(), this.getY());
        }
    }

    /**
     * Handle collision logic with other cars.
     * @param other the other Car involved in the collision
     */
    public void handleCollision(Car other) {
        if (this.checkCollision(other)) {
            if(!isInCollisionTimeOut && !this.isInvincible){
                this.takeDamage(other.INFLICTED_DAMAGE);
            }
            if(!other.isInCollisionTimeOut && !other.isInvincible){
                other.takeDamage(this.INFLICTED_DAMAGE);
            }

            // Start collision timeout
            isInCollisionTimeOut = true;
            other.setInCollisionTimeOut(true);
            collisionTimeoutCounter = COLLISION_TIMEOUT_FRAMES;
            other.setCollisionTimeoutCounter(collisionTimeoutCounter);

            // Change speed of the Cars
            shouldChangeSpeedY = true;
            other.setShouldChangeSpeedY(true);
        }
        handleCollisionImpact(other);
    }

    /**
     Handle collision logic with other Persons.
     * @param otherPerson the other person involved in the collision
     */
    public void handleCollision(Person otherPerson){
        if (this.checkCollision(otherPerson)) {
            if(!otherPerson.getIsInCollisionTimeOut() && !otherPerson.getIsInvincible()){
                otherPerson.takeDamage(this.INFLICTED_DAMAGE);
            }

            // Start collision timeout
            isInCollisionTimeOut = true;
            otherPerson.setInCollisionTimeOut(true);
            collisionTimeoutCounter = COLLISION_TIMEOUT_FRAMES;
            otherPerson.setCollisionTimeoutCounter(collisionTimeoutCounter);

            shouldChangeSpeedY = true;
        }
        handleCollisionImpact(otherPerson);
    }

    /**
     Handle collision Impact frames with other cars.
     * @param other the other Car involved in the collision
     */
    private void handleCollisionImpact(Car other){
        // Separate collided entities for the first 10 frames
        if (collisionTimeoutCounter > (COLLISION_TIMEOUT_FRAMES - COLLISION_IMPACT_FRAMES)
                && other.getCollisionTimeoutCounter() > (COLLISION_TIMEOUT_FRAMES - COLLISION_IMPACT_FRAMES)) {
            if (this.getY() < other.getY()) {
                this.setY(this.getY() - 1);
                other.setY(other.getY() + 1);
            } else {
                this.setY(this.getY() + 1);
                other.setY(other.getY() - 1);
            }
        }
    }

    /**
     Handle collision Impact frames with other Persons.
     * @param other the other Person involved in the collision
     */
    private void handleCollisionImpact(Person other){
        // Separate collided entities for the first 10 frames
        if (collisionTimeoutCounter > (COLLISION_TIMEOUT_FRAMES - COLLISION_IMPACT_FRAMES)
                && other.getCollisionTimeoutCounter() > (COLLISION_TIMEOUT_FRAMES - COLLISION_IMPACT_FRAMES)) {
            if (this.getY() < other.getY()) {
                this.setY(this.getY() - 1);
                other.setY(other.getY() + 1);
            } else {
                this.setY(this.getY() + 1);
                other.setY(other.getY() - 1);
            }
        }
    }

    /**
     Handle collision Impact frames with Fireballs.
     */
    public void handleFireballImpact(){
        // Separate collided entities for the first 10 frames
        if (collisionTimeoutCounter > (COLLISION_TIMEOUT_FRAMES - COLLISION_IMPACT_FRAMES) && isHitByFireball) {
            this.setY(this.getY() - 1);
        }else {
            this.isHitByFireball = false;
        }
    }

    /**
     Handle collision timeout logic for this Car instance.
     */
    public void handleCollisionTimeOut() {
        // Countdown for collision timeout
        if (collisionTimeoutCounter > 0) {
            collisionTimeoutCounter--;
        } else {
            isInCollisionTimeOut = false;
        }
    }

    /**
     * Adjust the movement according to the input
     * @param input the Input object that captures user keyboard actions
     */
    @Override
    public void adjustToInputMovement(Input input) {
        if (input.isDown(Keys.UP)) {
            moveY = 1;
        } else if (input.wasReleased(Keys.UP)) {
            moveY = 0;
        }
    }

    public int getMoveY() {
        return moveY;
    }
    public void setMoveY(int moveY) {
        this.moveY = moveY;
    }
    public double getHealth() {
        return health;
    }
    public Fire getFire() {
        return fire;
    }
    public void setFire(Fire fire) {
        this.fire = fire;
    }
    public Smoke getSmoke() {
        return smoke;
    }
    public boolean isDamaged() {
        return damaged;
    }
    public void setInCollisionTimeOut(boolean inCollisionTimeOut) {
        isInCollisionTimeOut = inCollisionTimeOut;
    }
    public boolean getIsInCollisionTimeOut() {
        return isInCollisionTimeOut;
    }
    public void setShouldChangeSpeedY(boolean shouldChangeSpeedY) {
        this.shouldChangeSpeedY = shouldChangeSpeedY;
    }
    public boolean getShouldChangeSpeedY() {
        return shouldChangeSpeedY;
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
    public void setHitByFireball(boolean hitByFireball) {
        isHitByFireball = hitByFireball;
    }
}