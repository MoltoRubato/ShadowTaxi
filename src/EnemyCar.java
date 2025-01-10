import bagel.Image;
import bagel.Input;
import java.util.*;

/**
 * Class representing the Enemy cars in the game. Enemy cars can shoot fireballs which deal damage.
 */
public class EnemyCar extends NPC {
    private static final int SPAWN_RATE = 300;
    private final List<Fireball> fireballs = new ArrayList<>();  // List to store fireballs

    public EnemyCar(Properties props) {
        super(props, new Image(props.getProperty("gameObjects.enemyCar.image")),
                "gameObjects.enemyCar.minSpeedY",
                "gameObjects.enemyCar.maxSpeedY");
    }

    /**
     * Update method to move the car vertically upwards by its speed.
     * @param input The current mouse/keyboard input.
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

        handleFireballs(input);
    }

    /**
     * Spawn fireball and move fireball according to the keyboard input.
     * @param input The current mouse/keyboard input.
     */
    private void handleFireballs(Input input) {
        boolean spawnFireball = MiscUtils.canSpawn(SPAWN_RATE);

        if (spawnFireball) {
            shootFireball();
        }

        // Update fireballs
        for (Fireball fireball : fireballs) {
            fireball.update(input);
        }

        // Remove inactive fireballs
        fireballs.removeIf(fireball -> !fireball.isActive());
    }

    /**
     * Create new fireball, shot out by this enemy car.
     */
    private void shootFireball() {
        // Use the enemy car's current coordinates
        Fireball newFireball = new Fireball(this.getX(), this.getY(), GAME_PROPS);
        fireballs.add(newFireball);
    }

    public List<Fireball> getFireballs() {
        return fireballs;
    }
}
