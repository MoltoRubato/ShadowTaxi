/*
 * This class is based on the Passenger class from the solution provided for Project 1.
 * Original code from: Project 1 Solution.
 * Modifications have been made to implement collision, umbrella priority setting and follow driver logic.
 */

import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.util.Properties;

/**
 * Class representing the Passenger in the game. Passengers can be picked up by a Taxi to complete a trip.
 */
public class Passenger extends Person implements Scrollable {
    private static final int EJECTION_OFFSET = 100; // Offset applied when the passenger is ejected
    private static final int HEALTH_DISPLAY_FACTOR = 100;
    private final int TAXI_DETECT_RADIUS;
    private final Properties PROPS;
    private final TravelPlan travelPlan;

    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private final int PRIORITY_OFFSET;
    private final int EXPECTED_FEE_OFFSET;
    private int walkDirectionX;
    private int walkDirectionY;
    private boolean isGetInTaxi;
    private Trip trip;

    private final int SPEED_Y;
    private final boolean HAS_UMBRELLA;
    private int moveY;

    private boolean reachedFlag;
    private boolean passengerCleared = false;

    private boolean isEjected;
    private final Driver driver;

    public Passenger(int x, int y, Driver driver, int priority, int endX, int distanceY, boolean hasUmbrella,
                     Properties props) {
        super(x,y, new Image(props.getProperty("gameObjects.passenger.image")),
                Double.parseDouble(props.getProperty("gameObjects.passenger.radius")),
                Double.parseDouble(props.getProperty("gameObjects.passenger.health")),
                props);
        this.WALK_SPEED_X = Integer.parseInt(props.getProperty("gameObjects.passenger.walkSpeedX"));
        this.WALK_SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.passenger.walkSpeedY"));
        this.PROPS = props;

        this.travelPlan = new TravelPlan(endX, distanceY, priority, props);
        this.TAXI_DETECT_RADIUS = Integer.parseInt(props.getProperty("gameObjects.passenger.taxiDetectRadius"));
        this.moveY = 0;
        this.PRIORITY_OFFSET = 30;
        this.EXPECTED_FEE_OFFSET = 100;

        this.SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.taxi.speedY"));
        this.HAS_UMBRELLA = hasUmbrella;
        this.isEjected = false;
        this.driver = driver;
    }

    public TravelPlan getTravelPlan() {
        return travelPlan;
    }

    public void setEjected(boolean ejected) {
        isEjected = ejected;
    }

    /**
     * Handle ejection from a taxi
     */
    public void ejectFromTaxi() {
        this.isEjected = true;
        this.setX(this.getX() - EJECTION_OFFSET); // Eject the passenger away from taxi
    }

    /**
     * Handle following the driver after being ejected
     */
    public void followDriver() {
        if (isEjected && driver != null) {
            walkXDirectionObj(driver.getX());
            walkYDirectionObj(driver.getY());
            walk();
        }
    }

    /**
     * Update the passenger status, move according to the input, active taxi and trip status.
     * Initiate the trip if the passenger is in the taxi.
     * See move method below to understand the movement of the passenger better.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        if (isEjected) {
            followDriver(); // Follow driver’s movements after ejection
            this.setInvincible(false);
            draw();
            return;
        }

        // Scroll according to the input if nothing is happening
        if(!isGetInTaxi || (trip != null && trip.isComplete())) {
            if(input != null) {
                adjustToInputMovement(input);
            }
            this.setInvincible(false);
            move();
            draw();
        }

        // if the passenger is not in the taxi, draw the priority number on the passenger.
        if(!isGetInTaxi && trip == null) {
            drawPriority();
        }

        // Driver should take this new passenger
        if(adjacentToObject(driver.getTaxi()) && !isGetInTaxi && trip == null && driver.getPassenger() == null
                && driver.getTaxi().isDriverOnBoard()) {
            setIsGetInTaxi(driver.getTaxi(), driver);
            move(driver.getTaxi());
        } else if(isGetInTaxi) {
            // The passenger is now on the taxi
            if(trip == null) {
                //Start the passenger's trip
                getTravelPlan().setStartY(this.getY());
                trip = new Trip(this, driver, PROPS);
                driver.setTrip(trip);
                trip.setTaxi(driver.getTaxi());
            }
            this.setInvincible(true);
            move(driver.getTaxi());
        }

        if(trip != null && trip.isComplete()) {
            // Reset driver records after this passenger's trip is complete
            if(!passengerCleared){
                driver.setPassenger(null);
                passengerCleared = true;
            }
            move(driver.getTaxi());
            draw();
        }

        // Render the blood at death
        if (this.getBlood() != null && this.getBlood().isActive()) {
            this.getBlood().update(input);
        }
    }

    /**
     * Draw the priority number on the passenger.
     */
    private void drawPriority() {
        Font font = new Font(PROPS.getProperty("font"),
                Integer.parseInt(PROPS.getProperty("gameObjects.passenger.fontSize")));
        font.drawString(String.valueOf(travelPlan.getPriority()), this.getX() - PRIORITY_OFFSET, this.getY());
        font.drawString(String.valueOf(travelPlan.getExpectedFee()), this.getX() - EXPECTED_FEE_OFFSET, this.getY());
    }

    /**
     * Adjust the movement direction in y-axis of the GameObject based on the keyboard input.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void adjustToInputMovement(Input input) {
        if (input.wasPressed(Keys.UP)) {
            moveY = 1;
        }  else if(input.wasReleased(Keys.UP)) {
            moveY = 0;
        }
    }

    /**
     * Move in relevant to the taxi and passenger's status.
     * @param taxi active taxi
     */
    private void move(Taxi taxi) {
        if (isGetInTaxi) {
            // If the passenger is in the taxi, move the passenger along with the taxi.
            moveWithTaxi(taxi);
        }

        if(trip != null && trip.isComplete()) {
            // Walk towards end flag if the trip is completed and not in the taxi.
            if(!hasReachedFlag()) {
                TripEndFlag tef = trip.getTripEndFlag();
                walkXDirectionObj(tef.getX());
                walkYDirectionObj(tef.getY());
                walk();
            }
        } else if (taxi.getDriver() != null){
            // Walk towards the taxi.
            walkXDirectionObj(taxi.getX());
            walkYDirectionObj(taxi.getY());
            walk();
        }
    }

    /**
     * Move the GameObject object in the y-direction based on the speedY attribute.
     */
    @Override
    public void move() {
        this.setY(this.getY() + SPEED_Y * moveY);
    }

    /**
     * Draw the current health of the passenger on the screen.
     * @param gameProps properties related to the game's graphical settings
     * @param msgProps properties that contain messages and labels for the game
     */
    public void drawHealth(Properties gameProps, Properties msgProps){
        String message = msgProps.getProperty("gamePlay.passengerHealth");

        Font font = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize")));
        font.drawString(message +String.format("%.1f", Math.abs(this.getHealth() * HEALTH_DISPLAY_FACTOR)),
                Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.x")),
                Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.y")));
    }

    /**
     * Walk the people object based on the walk direction and speed.
     */
    private void walk() {
        this.setX(this.getX() + (+ WALK_SPEED_X * walkDirectionX));
        this.setY(this.getY() + (+ WALK_SPEED_Y * walkDirectionY));
    }

    /**
     * Move the people object along with taxi when the people object is in the taxi.
     * @param taxi Active taxi in the game play
     */
    private void moveWithTaxi(Taxi taxi) {
        this.setX(taxi.getX());
        this.setY(taxi.getY());
    }

    /**
     * Determine the walk direction in x-axis of the passenger based on the x direction of the object.
     * @param otherX The x coordinate of the other object
     */
    private void walkXDirectionObj(int otherX) {
        walkDirectionX = Integer.compare(otherX, this.getX());
    }

    /**
     * Determine the walk direction in y-axis of the passenger based on the x direction of the object.
     * @param otherY The y coordinate of the other object
     */
    private void walkYDirectionObj(int otherY) {
        walkDirectionY = Integer.compare(otherY, this.getY());
    }

    /**
     * Check if the passenger has reached the end flag of the trip.
     * @return a boolean value indicating if the passenger has reached the end flag.
     */
    public boolean hasReachedFlag() {
        if(trip != null) {
            TripEndFlag tef = trip.getTripEndFlag();
            if(tef.getX() == this.getX() && tef.getY() == this.getY()) {
                reachedFlag = true;
            }
            return reachedFlag;
        }
        return false;
    }

    /**
     * Check if the taxi is adjacent to the passenger. This is evaluated based on multiple criteria.
     * @param taxi The active taxi in the game play.
     * @return a boolean value indicating if the taxi is adjacent to the passenger.
     */
    private boolean adjacentToObject(Taxi taxi) {
        // Check if Taxi is stopped
        boolean taxiStopped = !taxi.isMovingX() && !taxi.isMovingY();

        // Check if Taxi is in the passenger's detect radius
        float currDistance = (float) Math.sqrt(Math.pow(taxi.getX() - this.getX(), 2) +
                Math.pow(taxi.getY() - this.getY(), 2));
        return currDistance <= TAXI_DETECT_RADIUS && taxiStopped;
    }

    /**
     * Set the get in taxi status of the people object.
     * This is used to set an indication to check whether the people object is in the taxi or not.
     * @param taxi The taxi object to be checked. If it is null, the people object is not in a taxi at the moment in
     *             the game play.
     */
    public void setIsGetInTaxi(Taxi taxi, Driver driver) {
        if(taxi == null) {
            isGetInTaxi = false;
        } else if((float) Math.sqrt(Math.pow(taxi.getX() - this.getX(), 2) +
                Math.pow(taxi.getY() - this.getY(), 2)) <= 1) {
            isGetInTaxi = true;
            driver.setPassenger(this);
        } else {
            isGetInTaxi = false;
        }
    }

    public boolean hasUmbrella() {
        return HAS_UMBRELLA;
    }
}
