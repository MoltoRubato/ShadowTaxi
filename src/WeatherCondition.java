/**
 * Class representing the Weather conditions in the game.
 * Weather conditions last for a period of time between certain frames.
 */
public class WeatherCondition {
    private final String CONDITION;
    private final int startFrame;
    private final int endFrame;

    public WeatherCondition(String condition, int startFrame, int endFrame) {
        this.CONDITION = condition;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
    }

    /**
     *  Checks if the current weather condition is active.
     * @param frame the current frame of the game
     * @return true if this weather condition is active at the current frame, false if not
     */
    public boolean isActive(int frame) {
        return frame >= startFrame && frame <= endFrame;
    }

    public String getCONDITION() {
        return CONDITION;
    }
}
