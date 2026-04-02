/**
 * Represents the player's watering can used to water plants in the field.
 * The watering can has a maximum water capacity and tracks the current
 * amount of water remaining.
 */
public class WateringCan {
    private int maxWaterLevel;
    private int currentWaterLevel;

    /**
     * Constructs a new WateringCan with the specified maximum water capacity.
     * The watering can starts full.
     *
     * @param maxWaterLevel The maximum amount of water the watering can can hold.
     */
    public WateringCan(int maxWaterLevel) {
        this.maxWaterLevel = maxWaterLevel;
        this.currentWaterLevel = maxWaterLevel;
    }

    /**
     * Checks whether the watering can still contains water.
     *
     * @return true if there is at least one unit of water remaining,
     *         false otherwise.
     */
    public boolean canWater() {
        return currentWaterLevel > 0;
    }

    /**
     * Uses one unit of water from the watering can if water is available.
     */
    public void useWater() {
        if (canWater()) {
            currentWaterLevel--;
        }
    }

    /**
     * Refills the watering can back to its maximum capacity.
     */
    public void refill() {
        currentWaterLevel = maxWaterLevel;
    }

    /**
     * Returns the maximum water capacity of the watering can.
     *
     * @return The maximum water level.
     */
    public int getMaxWaterLevel() {
        return maxWaterLevel;
    }

    /**
     * Returns the current amount of water remaining in the watering can.
     *
     * @return The current water level.
     */
    public int getCurrentWaterLevel() {
        return currentWaterLevel;
    }
}