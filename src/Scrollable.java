import bagel.Input;

/**
 * Represents an object that can be scrolled or moved based on user input.
 * Classes implementing this interface must provide mechanisms to adjust
 * their position according to input and to move themselves accordingly.
 */
public interface Scrollable {

    /**
     * Adjusts the object's movement based on user input.
     * @param input the Input object that captures user keyboard actions
     */
    void adjustToInputMovement(Input input);

    /**
     * Moves the object
     */
    void move();
}
