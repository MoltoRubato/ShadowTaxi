/*
 * This class is based on the GamePlayScreen class from the solution provided for Project 1.
 * Original code from: Project 1 Solution.
 * Modifications have been made to implement new introduced classes.
 */

import bagel.Font;
import bagel.Input;
import java.util.*;

/**
 * Represents the gameplay screen in the game.
 */
public class GamePlayScreen{
    private final Properties GAME_PROPS;
    private final Properties MSG_PROPS;

    // keep track of earning and coin timeout
    private float totalEarnings;
    private float coinFramesActive;

    private int currFrame = 0;
    private int finalBloodFrame = -1;

    // game objects
    private final static int TAXI_SPAWN_Y_MAX = 400;
    private final static int TAXI_SPAWN_Y_MIN = 200;
    private GameObject taxi;
    private ArrayList<GameObject> deadTaxis = new ArrayList<>();
    private GameObject driver;
    private GameObject[] passengers;
    private GameObject[] coins;
    private GameObject[] stars;
    private Background background1;
    private Background background2;

    private List<GameObject> otherCars = new ArrayList<>();
    private List<GameObject> enemyCars = new ArrayList<>();
    private final static int SPAWN_RATE = 200;
    private final static int ENEMY_SPAWN_RATE = 400;

    private final float TARGET;
    private final int MAX_FRAMES;

    // vars for save score into the file
    private final String PLAYER_NAME;
    private boolean savedData;

    // display text vars
    private final Font INFO_FONT;
    private final int EARNINGS_Y;
    private final int EARNINGS_X;
    private final int COIN_X;
    private final int COIN_Y;
    private final int TARGET_X;
    private final int TARGET_Y;
    private final int MAX_FRAMES_X;
    private final int MAX_FRAMES_Y;
    private final int MIN_SCREEN_Y = -50;
    private final int MAX_SCREEN_Y;

    private final int TRIP_INFO_X;
    private final int TRIP_INFO_Y;
    private final int TRIP_INFO_OFFSET_1;
    private final int TRIP_INFO_OFFSET_2;
    private final int TRIP_INFO_OFFSET_3;

    private ArrayList<WeatherCondition> weatherConditions = new ArrayList<>();
    private String currentWeather;

    public GamePlayScreen(Properties gameProps, Properties msgProps, String playerName) {
        this.GAME_PROPS = gameProps;
        this.MSG_PROPS = msgProps;

        // read game objects from file and weather file and populate the game objects and weather conditions
        ArrayList<String[]> lines = IOUtils.readCommaSeperatedFile(gameProps.getProperty("gamePlay.objectsFile"));

        loadWeatherConditions();
        populateGameObjects(lines);

        this.TARGET = Float.parseFloat(gameProps.getProperty("gamePlay.target"));
        this.MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));

        // display text vars
        INFO_FONT = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize")));
        EARNINGS_Y = Integer.parseInt(gameProps.getProperty("gamePlay.earnings.y"));
        EARNINGS_X = Integer.parseInt(gameProps.getProperty("gamePlay.earnings.x"));
        COIN_X = Integer.parseInt(gameProps.getProperty("gameplay.coin.x"));
        COIN_Y = Integer.parseInt(gameProps.getProperty("gameplay.coin.y"));
        TARGET_X = Integer.parseInt(gameProps.getProperty("gamePlay.target.x"));
        TARGET_Y = Integer.parseInt(gameProps.getProperty("gamePlay.target.y"));
        MAX_FRAMES_X = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames.x"));
        MAX_FRAMES_Y = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames.y"));
        MAX_SCREEN_Y = Integer.parseInt(gameProps.getProperty("window.height"));

        // current trip info vars
        TRIP_INFO_X = Integer.parseInt(gameProps.getProperty("gamePlay.tripInfo.x"));
        TRIP_INFO_Y = Integer.parseInt(gameProps.getProperty("gamePlay.tripInfo.y"));
        TRIP_INFO_OFFSET_1 = 30;
        TRIP_INFO_OFFSET_2 = 60;
        TRIP_INFO_OFFSET_3 = 90;

        this.PLAYER_NAME = playerName;
    }

    /**
     * Populate the game objects from the lines read from the game objects file.
     * @param lines list of lines read from the game objects file. lines are processed into String arrays using comma as
     *             delimiter.
     */
    private void populateGameObjects(ArrayList<String[]> lines) {

        // two background images stacked in y-axis are used to create a scrolling effect
        background1 = new Background(
                Integer.parseInt(GAME_PROPS.getProperty("window.width")) / 2,
                Integer.parseInt(GAME_PROPS.getProperty("window.height")) / 2,
                GAME_PROPS);
        background2 = new Background(
                Integer.parseInt(GAME_PROPS.getProperty("window.width")) / 2,
                -1 * Integer.parseInt(GAME_PROPS.getProperty("window.height")) / 2,
                GAME_PROPS);

        int passengerCount = 0;
        int coinCount = 0;
        int starCount = 0;
        for(String[] lineElement: lines) {
            if(lineElement[0].equals(GameObjectType.PASSENGER.name())) {
                passengerCount++;
            } else if(lineElement[0].equals(GameObjectType.COIN.name())) {
                coinCount++;
            } else if(lineElement[0].equals(GameObjectType.INVINCIBLE_POWER.name())) {
                starCount++;
            }
        }
        passengers = new GameObject[passengerCount];
        coins = new GameObject[coinCount];
        stars = new GameObject[starCount];

        // process each line in the file
        int passenger_idx = 0;
        int coin_idx = 0;
        int star_idx = 0;
        for(String[] lineElement: lines) {
            int x = Integer.parseInt(lineElement[1]);
            int y = Integer.parseInt(lineElement[2]);

            if(lineElement[0].equals(GameObjectType.TAXI.name())) {
                taxi = new Taxi(x, y, this.GAME_PROPS);
            } else if(lineElement[0].equals(GameObjectType.DRIVER.name())) {
                driver = new Driver(x, y, (Taxi) taxi, passengerCount, this.GAME_PROPS);
            } else if(lineElement[0].equals(GameObjectType.PASSENGER.name())) {
                int priority = Integer.parseInt(lineElement[3]);
                int travelEndX = Integer.parseInt(lineElement[4]);
                int travelEndY = Integer.parseInt(lineElement[5]);
                boolean hasUmbrella = (Integer.parseInt(lineElement[6]) == 1);

                Passenger passenger = new Passenger(x, y, (Driver) driver, priority, travelEndX, travelEndY,
                        hasUmbrella, GAME_PROPS);
                passengers[passenger_idx] = passenger;
                passenger_idx++;

            } else if(lineElement[0].equals(GameObjectType.COIN.name())) {
                Coin coinPower = new Coin(x, y, this.GAME_PROPS);
                coins[coin_idx] = coinPower;
                coin_idx++;

            } else if(lineElement[0].equals(GameObjectType.INVINCIBLE_POWER.name())) {
                Star starPower = new Star(x, y, this.GAME_PROPS);
                stars[star_idx] = starPower;
                star_idx++;
            }
        }

        // The driver begins the game in the taxi
        ((Taxi) taxi).setDriver((Driver) driver);
        ((Taxi) taxi).setDriverOnBoard(true);
    }

    /**
     * Load weather conditions from the weather file.
     */
    private void loadWeatherConditions() {
        ArrayList<String[]> weatherData =
                IOUtils.readCommaSeperatedFile(GAME_PROPS.getProperty("gamePlay.weatherFile"));
        for (String[] line : weatherData) {
            String condition = line[0];
            int startFrame = Integer.parseInt(line[1]);
            int endFrame = Integer.parseInt(line[2]);
            weatherConditions.add(new WeatherCondition(condition, startFrame, endFrame));
        }
    }

    /**
     * Update the current Weather according to the frame duration of each weather condition
     */
    private void updateWeather() {
        for (WeatherCondition weatherCondition : weatherConditions) {
            if (weatherCondition.isActive(currFrame)) {
                currentWeather = weatherCondition.getCONDITION();
                break;
            }
        }
    }

    /**
     * Update the states of the game objects based on the keyboard input.
     * Handle the spawning of other cars in random intervals
     * Change the background image and change priorities based on the weather condition
     * Handle collision between game objects
     * Spawn new taxi if the active taxi is destroyed
     * @param input Keyboard input
     * @return true if the game is finished, false otherwise
     */
    public boolean update(Input input) {
        currFrame++;
        updateWeather();

        background1.update(input, background2, currentWeather);
        background2.update(input, background1, currentWeather);

        // Update driver status
        driver.update(input);
        ((Driver) driver).drawHealth(GAME_PROPS, MSG_PROPS);

        // Update passenger status
        for(GameObject passenger: passengers) {
            if (currentWeather.equals("RAINING") && ((Passenger) passenger).hasUmbrella()){
                ((Passenger) passenger).getTravelPlan().setPriority(1);
            }
            passenger.update(input);
        }

        // Draw the current or the lowest health passenger's health on screen
        if(((Driver) driver).getPassenger() != null){
            ((Driver) driver).getPassenger().drawHealth(GAME_PROPS, MSG_PROPS);
        }else{
            ((Passenger) getMinimumHealthPassenger()).drawHealth(GAME_PROPS,MSG_PROPS);
        }

        updateTaxi(input);
        updatePowerUp(input);
        updateNPC(input);

        handleCollisions();
        displayInfo();

        // End game if a person dies
        checkPersonDead();
        if (finalBloodFrame > 0) {
            finalBloodFrame--;
            return false;  // Ending game as soon as the blood has finished rendering
        }

        totalEarnings = ((Driver) driver).calculateTotalEarnings();

        return isGameOver() || isLevelCompleted() || (finalBloodFrame == 0) || isTaxiOffScreen();
    }

    private void updateTaxi(Input input){
        // Update taxi status
        if (taxi != null) {
            taxi.update(input);
            // Check if taxi is damaged and immediately add it to deadTaxis
            if (((Taxi) taxi).getHealth() <= 0) {
                deadTaxis.add(taxi);     // Add damaged taxi to deadTaxis list
                taxi = null;             // Remove current taxi

                // Immediately spawn a new taxi
                spawnNewTaxi();
                ((Driver) driver).setTaxi((Taxi) taxi);
                ((Taxi) taxi).setDriver((Driver) driver);
            }
            ((Taxi) taxi).drawHealth(GAME_PROPS, MSG_PROPS);
        }
        for(GameObject deadTaxi: deadTaxis) {
            ((Taxi) deadTaxi).updateWithDriver(input);
        }
    }

    private void updatePowerUp(Input input){
        // Update each coin
        if(coins.length > 0) {
            int minFramesActive = ((Coin) coins[0]).getMaxFrames();
            for(GameObject coinPower: coins) {
                Coin currCoin = (Coin) coinPower;
                coinPower.update(input);
                currCoin.collide((Taxi) taxi);
                currCoin.collide((Driver) driver);

                // check if there's active coin and finding the coin with maximum ttl
                int framesActive = currCoin.getFramesActive();
                if(currCoin.getIsActive() && minFramesActive > framesActive) {
                    minFramesActive = framesActive;
                }
            }
            coinFramesActive = minFramesActive;
        }

        // Update each star
        if(stars.length > 0) {
            int minFramesActive = ((Star) stars[0]).getMaxFrames();
            for(GameObject starPower: stars) {
                starPower.update(input);
                ((Star) starPower).collide((Taxi) taxi);
                ((Star) starPower).collide((Driver) driver);

                // check if there's active star and finding the star with maximum ttl
                int framesActive = ((Star) starPower).getFramesActive();
                if(((Star) starPower).getIsActive() && minFramesActive > framesActive) {
                    minFramesActive = framesActive;
                }
            }
        }
    }

    private void updateNPC(Input input){
        // Handle spawning of NPCs
        if (MiscUtils.canSpawn(SPAWN_RATE)) {
            OtherCar newCar = new OtherCar(GAME_PROPS);
            otherCars.add(newCar);
        }
        if (MiscUtils.canSpawn(ENEMY_SPAWN_RATE)) {
            EnemyCar newEnemy = new EnemyCar(GAME_PROPS);
            enemyCars.add(newEnemy);
        }

        // Update other cars
        Iterator<GameObject> carIterator = otherCars.iterator();
        while (carIterator.hasNext()) {
            GameObject car = carIterator.next();
            car.update(input);
            if (((OtherCar) car).isDamaged() && !((OtherCar) car).getFire().isActive()) {
                carIterator.remove();
            }
        }

        // Update Enemy cars
        Iterator<GameObject> enemyIterator = enemyCars.iterator();
        while (enemyIterator.hasNext()) {
            GameObject enemy = enemyIterator.next();
            enemy.update(input);
            if (((EnemyCar)enemy).isDamaged() && !((EnemyCar) enemy).getFire().isActive()) {
                enemyIterator.remove();
            }
        }
    }

    /**
     * Find the passenger with the least amount of health
     */
    private GameObject getMinimumHealthPassenger() {
        double minHealth = Integer.MAX_VALUE; // Set to a very large value initially to find minimum
        GameObject minHealthPassenger = null;

        for (GameObject passenger : passengers) {
            if (!((Passenger) passenger).isDead()) {
                double passengerHealth = ((Passenger) passenger).getHealth();
                if (passengerHealth < minHealth) {
                    minHealth = passengerHealth;
                    minHealthPassenger = passenger;
                }
            }
        }
        return minHealthPassenger;
    }

    /**
     * Handle collision between entities
     */
    private void handleCollisions() {
        if (taxi == null) return;

        // Update fireballs and check for collisions with game objects
        for (GameObject enemyCar : enemyCars) {
            for (Fireball fireball : ((EnemyCar) enemyCar).getFireballs()) {
                fireball.handleCollision((Driver) driver);
                fireball.handleCollision((Taxi) taxi);

                for (GameObject passenger : passengers) {
                    fireball.handleCollision((Passenger) passenger);
                }

                for (GameObject car : otherCars) {
                    fireball.handleCollision((OtherCar) car);
                }

                for (GameObject car : enemyCars) {
                    if (!car.equals(enemyCar)) {  // Skip checking collision with itself
                        fireball.handleCollision((EnemyCar) car);
                    }
                }
            }
        }

        // Check for collisions between the taxi and other cars
        for (GameObject otherCar : otherCars) {
            ((Taxi) taxi).handleCollision((OtherCar) otherCar);

            ((OtherCar) otherCar).handleCollision((Driver) driver);
            for(GameObject passenger: passengers){
                ((OtherCar) otherCar).handleCollision((Passenger) passenger);
            }
        }

        // Check for collisions between the taxi and enemy cars
        for (GameObject enemyCar : enemyCars) {
            ((Taxi) taxi).handleCollision((EnemyCar) enemyCar);

            ((EnemyCar) enemyCar).handleCollision((Driver) driver);
            for(GameObject passenger: passengers){
                ((EnemyCar) enemyCar).handleCollision((Passenger) passenger);
                ((Passenger) passenger).handleCollisionTimeOut(); // update each passenger's collision timeout
            }
        }

        // Check for collisions between other cars and enemy cars
        for (GameObject otherCar : otherCars) {
            for (GameObject enemyCar : enemyCars) {
                ((OtherCar) otherCar).handleCollision((EnemyCar) enemyCar);
            }
        }

        // Check for collisions between other cars and other cars
        for (int i = 0; i < otherCars.size(); i++) {
            GameObject car1 = otherCars.get(i);
            for (int j = i + 1; j < otherCars.size(); j++) {
                GameObject car2 = otherCars.get(j);
                ((OtherCar) car1).handleCollision((OtherCar) car2);
            }
            ((OtherCar) car1).handleCollisionTimeOut(); // update each otherCar's collision time out
        }

        // Check for collisions between enemy cars and enemy cars
        for (int i = 0; i < enemyCars.size(); i++) {
            GameObject car1 = enemyCars.get(i);
            for (int j = i + 1; j < enemyCars.size(); j++) {
                GameObject car2 = enemyCars.get(j);
                ((EnemyCar) car1).handleCollision((EnemyCar) car2);
            }
            ((EnemyCar) car1).handleCollisionTimeOut(); // update each enemyCar's collision time out
        }

        ((Taxi) taxi).handleCollisionTimeOut();
        ((Driver) driver).handleCollisionTimeOut();
    }

    /**
     * Spawn a taxi randomly
     */
    private void spawnNewTaxi() {
        int x = MiscUtils.selectAValue(NPC.LANE_POSITIONS[0], NPC.LANE_POSITIONS[2]);
        int y = MiscUtils.getRandomInt(TAXI_SPAWN_Y_MIN, TAXI_SPAWN_Y_MAX);
        taxi = new Taxi(x, y, GAME_PROPS);
    }

    /**
     * Display the game information on the screen.
     */
    public void displayInfo() {
        INFO_FONT.drawString(MSG_PROPS.getProperty("gamePlay.earnings") + getTotalEarnings(), EARNINGS_X, EARNINGS_Y);
        INFO_FONT.drawString(MSG_PROPS.getProperty("gamePlay.target") + String.format("%.02f", TARGET), TARGET_X,
                TARGET_Y);
        INFO_FONT.drawString(MSG_PROPS.getProperty("gamePlay.remFrames") + (MAX_FRAMES - currFrame), MAX_FRAMES_X,
                MAX_FRAMES_Y);

        if(coins.length > 0 && ((Coin) coins[0]).getMaxFrames() != coinFramesActive) {
            INFO_FONT.drawString(String.valueOf(Math.round(coinFramesActive)), COIN_X, COIN_Y);
        }

        Trip lastTrip = ((Driver) driver).getLastTrip();
        if(lastTrip != null) {
            if(lastTrip.isComplete()) {
                INFO_FONT.drawString(MSG_PROPS.getProperty("gamePlay.completedTrip.title"), TRIP_INFO_X, TRIP_INFO_Y);
            } else {
                INFO_FONT.drawString(MSG_PROPS.getProperty("gamePlay.onGoingTrip.title"), TRIP_INFO_X, TRIP_INFO_Y);
            }
            INFO_FONT.drawString(MSG_PROPS.getProperty("gamePlay.trip.expectedEarning")
                    + lastTrip.getPassenger().getTravelPlan().getExpectedFee(), TRIP_INFO_X, TRIP_INFO_Y
                    + TRIP_INFO_OFFSET_1);
            INFO_FONT.drawString(MSG_PROPS.getProperty("gamePlay.trip.priority")
                    + lastTrip.getPassenger().getTravelPlan().getPriority(), TRIP_INFO_X, TRIP_INFO_Y
                    + TRIP_INFO_OFFSET_2);
            if(lastTrip.isComplete()) {
                INFO_FONT.drawString(MSG_PROPS.getProperty("gamePlay.trip.penalty") + String.format("%.02f",
                        lastTrip.getPenalty()), TRIP_INFO_X, TRIP_INFO_Y + TRIP_INFO_OFFSET_3);
            }
        }
    }

    public String getTotalEarnings() {
        return String.format("%.02f", totalEarnings);
    }

    /**
     * Check if the game is over. If the game is over and not saved the score, save the score.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        // Game is over if the current frame is greater than the max frames
        boolean isGameOver = currFrame >= MAX_FRAMES;
        if(currFrame >= MAX_FRAMES && !savedData) {
            savedData = true;
            IOUtils.writeLineToFile(GAME_PROPS.getProperty("gameEnd.scoresFile"), PLAYER_NAME + "," + totalEarnings);
        }
        return isGameOver;
    }

    /**
     * Check if the level is completed. If the level is completed and not saved the score, save the score.
     * @return true if the level is completed, false otherwise.
     */
    public boolean isLevelCompleted() {
        // Level is completed if the total earnings is greater than or equal to the target earnings
        boolean isLevelCompleted = totalEarnings >= TARGET;
        if(isLevelCompleted && !savedData) {
            savedData = true;
            IOUtils.writeLineToFile(GAME_PROPS.getProperty("gameEnd.scoresFile"), PLAYER_NAME + "," + totalEarnings);
        }
        return isLevelCompleted;
    }

    /**
     * Check if the driver or any passenger is dead. If so, save the score.
     */
    public void checkPersonDead() {
        boolean isPersonDead = ((Driver) driver).isDead();
        for(GameObject passenger:passengers){
            if (((Passenger) passenger).isDead()){
                isPersonDead = true;
                break;
            }
        }

        if(isPersonDead && !savedData) {
            savedData = true;
            IOUtils.writeLineToFile(GAME_PROPS.getProperty("gameEnd.scoresFile"), PLAYER_NAME + "," + totalEarnings);
            finalBloodFrame = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.blood.ttl")) ;
        }
    }

    /**
     * Check if the newly spawned taxi is off-screen
     * @return true if the taxi is off-screen, false otherwise.
     */
    public boolean isTaxiOffScreen() {
        boolean isOffScreen = ((taxi.getY() < MIN_SCREEN_Y) || (taxi.getY() > MAX_SCREEN_Y))
                && !((Taxi) taxi).isDriverOnBoard();


        if(isOffScreen && !savedData) {
            savedData = true;
            IOUtils.writeLineToFile(GAME_PROPS.getProperty("gameEnd.scoresFile"), PLAYER_NAME + "," + totalEarnings);
        }
        return isOffScreen;
    }
}
