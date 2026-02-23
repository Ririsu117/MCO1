public class Plant {
    private String name;
    private int seedPrice;
    private int yield;
    private int maxGrowth;
    private int currentGrowth;
    private String preferredSoil;
    private boolean watered;
    private int cropPrice;

    public Plant(String name, int seedPrice, int yield, int maxGrowth, String preferredSoil, int cropPrice) {
        this.name = name;
        this.seedPrice = seedPrice;
        this.yield = yield;
        this.maxGrowth = maxGrowth;
        this.currentGrowth = 0;
        this.preferredSoil = preferredSoil;
        this.watered = false;
        this.cropPrice = cropPrice;
    }

    public boolean isMature() {
        return currentGrowth >= maxGrowth;
    }

    public void grow(int stages) {
        if (!isMature()) {
            currentGrowth += stages;
            if (currentGrowth > maxGrowth) {
                currentGrowth = maxGrowth;
            }
        }
    }

    public void water() {
        watered = true;
    }

    public void resetWatered() {
        watered = false;
    }

    public int calculateHarvestValue() {
        return yield * cropPrice;
    }

    public String getName() {
    	return name;
    }
    public int getSeedPrice() {
    	return seedPrice; 
    }
    public int getYield() { 
     	return yield; 
    }
    public int getMaxGrowth() { 
    	return maxGrowth; 
    }
    public int getCurrentGrowth() { 
    	return currentGrowth; 
    }
    public String getPreferredSoil() { 
    	return preferredSoil; 
    }
    public boolean isWatered() { 
    	return watered; 
    }
    public int getCropPrice() { 
    	return cropPrice; 
    }
}