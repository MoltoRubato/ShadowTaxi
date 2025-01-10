/**
 * Represents an object that can take damage.
 * Classes implementing this interface must provide a mechanism
 * to handle damage inflicted upon them.
 */

public interface Damageable {
    /**
     * Applies damage to the object.
     * @param damagePoints the amount of damage to be inflicted
     */
    void takeDamage(double damagePoints);
}
