public class WateringCan {
    private int maxWaterLevel;
    private int currentWaterLevel;

    public WateringCan(int maxWaterLevel) {
        this.maxWaterLevel = maxWaterLevel;
        this.currentWaterLevel = maxWaterLevel;
    }

    public boolean canWater() {
        return currentWaterLevel > 0;
    }

    public void useWater() {
        if (canWater()) {
            currentWaterLevel--;
        }
    }

    public void refill() {
        currentWaterLevel = maxWaterLevel;
    }

    public int getMaxWaterLevel() {
        return maxWaterLevel;
    }

    public int getCurrentWaterLevel() {
        return currentWaterLevel;
    }
}