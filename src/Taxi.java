/*
 * This class is based on the Taxi class from the solution provided for Project 1.
 * Original code from: Project 1 Solution.
 * Modifications have been made to implement collision and damage functionality.
 */

import bagel.*;
import java.util.*;

/**
 * The class representing the taxis in the game play
 */
public class Taxi extends Car {
    private static final int HEALTH_DISPLAY_FACTOR = 100;
    private Image actualImage;
    private final Image DAMAGED_IMAGE;
    private final int SPEED_X;
    private boolean isMovingY;
    private boolean isMovingX;

    private Driver driver;
    private boolean driverOnBoard;
    private final int SPEED_Y;

    public Taxi(int x, int y, Properties props) {
        super(x,y,new Image(props.getProperty("gameObjects.taxi.image")),
                Double.parseDouble(props.getProperty("gameObjects.taxi.radius")),
                Double.parseDouble(props.getProperty("gameObjects.taxi.damage")),
                Double.parseDouble(props.getProperty("gameObjects.taxi.health")), props);

        this.actualImage = this.IMAGE;
        this.SPEED_X = Integer.parseInt(props.getProperty("gameObjects.taxi.speedX"));
//        this.SPEED_X = 5;
        this.SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.taxi.speedY"));
        this.DAMAGED_IMAGE = new Image(props.getProperty("gameObjects.taxi.damagedImage"));
        this.driverOnBoard = false;
    }

    public boolean isMovingY() {
        return isMovingY;
    }

    public boolean isMovingX() {
        return isMovingX;
    }

    /**
     *  update the passenger status based on input if the passenger is not in the taxi or the trip is completed.
     *  This means the passenger is go down when taxi moves up.
     *  @param input The Keyboard input
     */
    public void updateWithDriver(Input input) {
        if (input != null) {
            adjustToDriver(input);
        }

        move();
        draw();

        if (this.getFire() != null && this.getFire().isActive()) {
            this.getFire().update(input);  // Render the fire
        }
    }

    /**
     * Move the GameObject object in the y-direction based on the speedY attribute.
     */
    @Override
    public void move() {
        this.setY(this.getY() + SPEED_Y * this.getMoveY());
    }

    /**
     * Update the Taxi's movement states based on the input.
     * Render the taxi into the screen.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        // if the driver has coin power, apply the effect of the coin on the priority of the passenger
        if (driver.getTrip() != null && driver.getCoinPower() != null) {
            TravelPlan tp = driver.getTrip().getPassenger().getTravelPlan();
            int newPriority = tp.getPriority();
            if(!tp.getCoinPowerApplied()) {
                newPriority = driver.getCoinPower().applyEffect(tp.getPriority());
            }
            if(newPriority < tp.getPriority()) {
                tp.setCoinPowerApplied();
            }
            tp.setPriority(newPriority);
        }

        if (driver.getStarPower() != null){
            driver.getStarPower().applyEffect(this);
        }

        if(driverOnBoard){
            if(input != null && !this.isDamaged()) {
                adjustToInputMovement(input);
            }
        } else {
            // No driver control, update according to input
            updateWithDriver(input);
        }

        // Check if the taxi is damaged or health is 0
        if (this.isDamaged()) {
            actualImage = DAMAGED_IMAGE;
            this.setFire(new Fire(GAME_PROPS, this.getX(), this.getY()));
            ejectDriver(driver);  // Eject the driver immediately when taxi is damaged

            // Reset moveY to stop the taxi from moving downwards
            this.setMoveY(0);
            isMovingY = false;
        }
        draw();

        // Render smoke if taxi takes damage
        if (this.getSmoke() != null && this.getSmoke().getFrames()>0) {
            this.getSmoke().update(input);
        }
    }

    /**
     * Adjust the movement direction in y-axis of the GameObject based on the keyboard input.
     * @param input The current mouse/keyboard input.
     */
    private void adjustToDriver(Input input) {
        if (input.isDown(Keys.UP)) {
            this.setMoveY(1);
        } else if(input.wasReleased(Keys.UP)) {
            this.setMoveY(0);
        }
    }

    /**
     * Draw the current image into the screen.
     */
    @Override
    public void draw() {
        actualImage.draw(this.getX(), this.getY());
    }

    /**
     * Draw the current health of the taxi on the screen.
     * @param gameProps properties related to the game's graphical settings
     * @param msgProps properties that contain messages and labels for the game
     */
    public void drawHealth(Properties gameProps, Properties msgProps){
        String message = msgProps.getProperty("gamePlay.taxiHealth");

        Font font = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize")));
        font.drawString(message + String.format("%.1f", Math.abs(this.getHealth() * HEALTH_DISPLAY_FACTOR)),
                Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.x")),
                Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.y")));
    }

    /**
     * Adjust the movement of the taxi based on the keyboard input.
     * If the taxi has a driver, and taxi has health>0 the taxi can only move left and right (fixed in y direction).
     * If the taxi does not have a driver, the taxi can move in all directions.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void adjustToInputMovement(Input input) {
        if (input.wasPressed(Keys.UP)) {
            isMovingY = true;
        }  else if(input.wasReleased(Keys.UP)) {
            isMovingY = false;
        } else if(input.isDown(Keys.LEFT)) {
            this.setX(this.getX() - SPEED_X);
            isMovingX = true;
        }  else if(input.isDown(Keys.RIGHT)) {
            this.setX(this.getX() + SPEED_X);
            isMovingX =  true;
        } else if(input.wasReleased(Keys.LEFT) || input.wasReleased(Keys.RIGHT)) {
            isMovingX = false;
        }
    }

    /**
     *  Eject the driver from this taxi when the taxi is damaged.
     *  @param driver The driver being ejected
     */
    private void ejectDriver(Driver driver) {
        driver.ejectFromTaxi();
        driverOnBoard = false;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Driver getDriver() {
        return driver;
    }

    public boolean isDriverOnBoard() {
        return driverOnBoard;
    }

    public void setDriverOnBoard(boolean driverOnBoard) {
        this.driverOnBoard = driverOnBoard;
    }
}
