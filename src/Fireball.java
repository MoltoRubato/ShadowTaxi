import bagel.Image;
import bagel.Input;
import java.util.Properties;

/**
 * Class representing the Fireball object in the game. Fireballs are shot by enemyCars and deal damage.
 */
public class Fireball extends GameObject implements Collidable{
    private final int SPEED_Y;
    private final double DAMAGE_POINTS;
    private boolean active;

    public Fireball(int x, int y, Properties props) {
        super(props, x, y, new Image(props.getProperty("gameObjects.fireball.image")),
                Double.parseDouble(props.getProperty("gameObjects.fireball.radius")));
        this.SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.fireball.shootSpeedY"));
        this.DAMAGE_POINTS = Double.parseDouble(props.getProperty("gameObjects.fireball.damage"));
        this.active = true;
    }

    /**
     * Handles fireball movement each update.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        if (active) {
            move();
            draw();
        }
    }

    /**
     * Move the Fireball up until the top of the screen.
     */
    private void move() {
        this.setY(this.getY()-SPEED_Y); // Moves the fireball upwards
        if (this.getY() < 0) {
            active = false;  // Stop rendering when it reaches the top of the screen
        }
    }

    /**
     * Render the fireball while it is active
     */
    @Override
    public void draw() {
        if (active) {
            IMAGE.draw(this.getX(), this.getY());
        }
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Deactivate fireball and stop it from rendering
     */
    public void deactivate() {
        active = false;
    }

    /**
     * Handle collision with a Person
     * @param target The Person involved in the collision
     */
    public void handleCollision(Person target) {
        if (active && this.checkCollision(target)) {
            target.takeDamage(DAMAGE_POINTS);
            target.setInCollisionTimeOut(true);
            target.setCollisionTimeoutCounter(200);
            target.setHitByFireball(true);
            this.deactivate();  // Stop rendering fireball after a collision
        }
        target.handleFireballImpact();
    }

    /**
     * Check if a collision occurs with a GameObject
     * @param target The GameObject involved in the collision
     * @return true if a collision occurs under the circumstances, false otherwise
     */
    public boolean checkCollision(GameObject target) {
        double distance = Math.sqrt(Math.pow(this.getX() - target.getX(), 2) +
                Math.pow(this.getY() - target.getY(), 2));
        return distance < (this.RADIUS + target.RADIUS);
    }

    /**
     * Handle collision with a Car
     * @param target The Car involved in the collision
     */
    public void handleCollision(Car target) {
        if (active && this.checkCollision(target)) {

            if(!target.getIsInvincible()){
                target.takeDamage(DAMAGE_POINTS);
            }

            target.setInCollisionTimeOut(true);
            target.setCollisionTimeoutCounter(Car.COLLISION_TIMEOUT_FRAMES);
            target.setShouldChangeSpeedY(true);
            target.setHitByFireball(true);

            this.deactivate();  // Stop rendering fireball after a collision
        }
        target.handleFireballImpact();
    }
}