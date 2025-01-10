import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.Properties;

/**
 * Class representing the driver in the game. Drivers can drive taxis, and can change to a new taxi if the previous
 * taxi is damaged.
 */
public class Driver extends Person{
    private static final int EJECTION_OFFSET = 50; // Offset applied when the driver is ejected
    private static final int HEALTH_DISPLAY_FACTOR = 100;
    private final int SPEED_Y;
    private final int SPEED_X;
    private final int TAXI_GET_IN_RADIUS;

    private final Trip[] TRIPS;
    private int tripCount;
    private Trip trip;

    private boolean inTaxi;
    private Taxi taxi;
    private Passenger passenger; // Reference to the current passenger

    private Coin coinPower;
    private Star starPower;

    public Driver(int startX, int startY, Taxi taxi, int maxTripCount, Properties props) {
        super(startX,startY, new Image(props.getProperty("gameObjects.driver.image")),
                Double.parseDouble(props.getProperty("gameObjects.driver.radius")),
                Double.parseDouble(props.getProperty("gameObjects.driver.health")),
                props);
        this.inTaxi = true; // Driver initially starts in the taxi
        this.taxi = taxi;
        this.SPEED_X = Integer.parseInt(props.getProperty("gameObjects.driver.walkSpeedX"));
        this.SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.driver.walkSpeedY"));
        this.TAXI_GET_IN_RADIUS = Integer.parseInt(props.getProperty("gameObjects.driver.taxiGetInRadius"));
        TRIPS = new Trip[maxTripCount];
    }

    /**
     * If it's a new trip, it will be added to the list of trips.
     * @param trip trip object
     */
    public void setTrip(Trip trip) {
        this.trip = trip;
        if(trip != null) {
            this.TRIPS[tripCount] = trip;
            tripCount++;
        }
    }
    public Trip getTrip() {
        return this.trip;
    }

    /**
     * Get the last trip from the list of trips.
     * @return Trip object
     */
    public Trip getLastTrip() {
        if(tripCount == 0) {
            return null;
        }
        return TRIPS[tripCount - 1];
    }

    /**
     * Update the driver's movement and interaction with the taxi.
     * @param input the Input object that captures user keyboard actions for movement
     */
    @Override
    public void update(Input input) {
        // If the driver is in the taxi, move with the taxi
        if (inTaxi) {
            this.setX(taxi.getX());
            this.setY(taxi.getY());
            this.setInvincible(true);
        } else {
            // Move independently if not in the taxi
            move(input);
            this.setInvincible(false);

            // Check if the driver can enter the taxi
            if (canEnterTaxi()) {
                enterTaxi();
            }
            draw();
        }

        // Apply invincible power if collected
        if(starPower!=null){
            starPower.applyEffect(this);
        }

        // Render the blood
        if (this.getBlood() != null && this.getBlood().isActive()) {
            this.getBlood().update(input);
        }

        // Check the status of the trip
        if(trip != null && trip.hasReachedEnd()) {
            trip.end();
        }

        // the flag of the current trip renders to the screen
        if(tripCount > 0) {
            Trip lastTrip = TRIPS[tripCount - 1];
            if(!lastTrip.getPassenger().hasReachedFlag()) {
                lastTrip.getTripEndFlag().update(input);
            }
        }
    }

    /**
     * Calculate total earnings.
     * @return float, total earnings
     */
    public float calculateTotalEarnings() {
        float totalEarnings = 0;
        for(Trip trip : TRIPS) {
            if (trip != null) {
                totalEarnings += trip.getFee();
            }
        }
        return totalEarnings;
    }

    /**
     * Move the driver based on key input when on foot.
     * @param input the Input object that captures user keyboard actions
     */
    private void move(Input input) {
        if (input.isDown(Keys.UP)) {
            this.setY(this.getY() - SPEED_Y);
        }
        if (input.isDown(Keys.DOWN)) {
            this.setY(this.getY() + SPEED_Y);
        }
        if (input.isDown(Keys.LEFT)) {
            this.setX(this.getX() - SPEED_X);
        }
        if (input.isDown(Keys.RIGHT)) {
            this.setX(this.getX() + SPEED_X);
        }
    }

    /**
     * Eject the driver from the taxi. Adjust position to be slightly away from the taxi.
     */
    public void ejectFromTaxi() {
        if(inTaxi){
            this.setX(this.getX() - EJECTION_OFFSET);
        }
        inTaxi = false;
        if (passenger != null && trip != null) {
            passenger.ejectFromTaxi(); // Eject the passenger too
            trip.setTaxi(null);
        }
    }

    /**
     * Check if the driver is close enough to enter the taxi.
     * @return true if the Euclidean distance between the driver and taxi is <= 10, otherwise false.
     */
    private boolean canEnterTaxi() {
        double distance = Math.sqrt(Math.pow(taxi.getX() - this.getX(), 2) + Math.pow(taxi.getY() - this.getY(), 2));
        return distance <= TAXI_GET_IN_RADIUS && !taxi.isDamaged();
    }

    /**
     * Make the driver enter the taxi.
     */
    private void enterTaxi() {
        inTaxi = true;
        taxi.setDriverOnBoard(true);

        if(trip != null){
            trip.setTaxi(taxi);
            passenger.setEjected(false);
        }
    }

    /**
     * Draw the Driver's health on the screen.
     * @param gameProps properties related to the game's graphical settings
     * @param msgProps properties that contain messages and labels for the game
     */
    public void drawHealth(Properties gameProps, Properties msgProps){
        String message = msgProps.getProperty("gamePlay.driverHealth");
        Font font = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize")));
        font.drawString(message + String.format("%.1f", Math.abs(this.getHealth() * HEALTH_DISPLAY_FACTOR)),
                Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.x")),
                Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.y")));
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void collectPower(Coin coin) {
        coinPower = coin;
    }

    public void collectPower(Star star) {
        starPower = star;
    }

    public Coin getCoinPower() {
        return coinPower;
    }

    public Star getStarPower() {
        return starPower;
    }
}
