/**
 * Represents an object that can be involved in collision detection.
 * Classes implementing this interface must provide a mechanism
 * to check for collisions with other GameObjects.
 */

public interface Collidable {
    /**
     * Checks if this object is colliding with another GameObject.
     * @param other the GameObject to check for a collision with
     * @return true if a collision is detected; false otherwise
     */
    boolean checkCollision(GameObject other);
}
