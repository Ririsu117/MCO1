/**
 * Represents a plant that can be grown in the Verdant Sun Farming Simulator.
 *
 * Each plant progresses through a sequence of PlantStage objects
 * rather than a simple integer growth counter. The plant tracks its current
 * position in that stage sequence, and all growth, watering, and harvest
 * behavior is delegated to the active PlantStage.
 *
 * Each plant also carries two sets of crop data: one for the Low Productive
 * stage and one for the High Productive stage. These are used when producing
 * a Crop object upon harvest.
 */
public class Plant {
    private String name;
    private int seedPrice;
    private int yield;
    private String preferredSoil;
    private boolean watered;

    /** The ordered sequence of growth stages this plant goes through. */
    private PlantStage[] stages;

    /** Index into stages[] pointing to the plant's current stage. */
    private int currentStageIndex;

    /** Crop name when harvested at Low Productive stage. */
    private String lowCropName;

    /** Base price per piece when harvested at Low Productive stage. */
    private int lowCropPrice;

    /** Crop name when harvested at High Productive stage. */
    private String highCropName;

    /** Base price per piece when harvested at High Productive stage. */
    private int highCropPrice;

    /** Whether the High Productive crop is a Root Crop (50% bonus applies). */
    private boolean highCropIsRoot;

    /**
     * Constructs a Plant with the full MCO2 stage-based attributes.
     *
     * @param name           The name of the plant.
     * @param seedPrice      The cost to purchase the seed.
     * @param yield          The number of crop pieces produced per harvest.
     * @param preferredSoil  The soil type preferred by this plant.
     * @param stages         The ordered array of PlantStage objects for this plant.
     * @param lowCropName    The crop name produced at Low Productive stage.
     * @param lowCropPrice   The base price per piece at Low Productive stage.
     * @param highCropName   The crop name produced at High Productive stage.
     * @param highCropPrice  The base price per piece at High Productive stage.
     * @param highCropIsRoot Whether the High Productive crop is a Root Crop.
     */
    public Plant(String name, int seedPrice, int yield, String preferredSoil,
                 PlantStage[] stages,
                 String lowCropName, int lowCropPrice,
                 String highCropName, int highCropPrice,
                 boolean highCropIsRoot) {
        this.name = name;
        this.seedPrice = seedPrice;
        this.yield = yield;
        this.preferredSoil = preferredSoil;
        this.stages = stages;
        this.currentStageIndex = 0;
        this.watered = false;
        this.lowCropName = lowCropName;
        this.lowCropPrice = lowCropPrice;
        this.highCropName = highCropName;
        this.highCropPrice = highCropPrice;
        this.highCropIsRoot = highCropIsRoot;
    }

    /**
     * Returns the plant's current active PlantStage.
     *
     * @return The PlantStage at the current stage index.
     */
    public PlantStage getCurrentStage() {
        return stages[currentStageIndex];
    }

    /**
     * Returns the type of the plant's current stage.
     *
     * @return The StageType enum value of the current stage.
     */
    public PlantStage.StageType getCurrentStageType() {
        return getCurrentStage().getStageType();
    }

    /**
     * Checks whether the plant has reached its Fully Mature stage.
     * A plant is fully mature when its current stage is FullyMature.
     *
     * @return true if the plant is fully mature, false otherwise.
     */
    public boolean isFullyMature() {
        return getCurrentStage().getStageType() == PlantStage.StageType.FULLY_MATURE;
    }

    /**
     * Checks whether the plant can produce crop if harvested right now.
     * This is true for Low Productive, High Productive, and Fully Mature stages.
     *
     * @return true if harvesting yields crop, false otherwise.
     */
    public boolean canHarvest() {
        return getCurrentStage().canProduceCrop();
    }

    /**
     * Advances the plant forward by the given number of stages in its
     * stage sequence. The plant will not advance past its final stage.
     * If the current stage is Fully Mature, no advancement occurs.
     *
     * @param stages The number of stage steps to advance.
     */
    public void grow(int stages) {
        if (isFullyMature()) {
            return;
        }
        int maxIndex = this.stages.length - 1;
        currentStageIndex = Math.min(currentStageIndex + stages, maxIndex);
    }

    /**
     * Marks the plant as watered for the current day.
     */
    public void water() {
        watered = true;
    }

    /**
     * Resets the watered status of the plant at the end of each day.
     */
    public void resetWatered() {
        watered = false;
    }

    /**
     * Produces and returns a Crop object based on the plant's current stage.
     * Returns null if the plant is not in a harvestable stage.
     *
     * Low Productive stage produces the low crop at 1x yield.
     * High Productive stage produces the high crop at 2x yield,
     * with the root crop bonus flag set if applicable.
     * Fully Mature stage produces the high crop at 2x yield,
     * but the root crop bonus does NOT apply.
     *
     * @return A Crop object representing the harvested yield, or null if
     *         the plant cannot be harvested at its current stage.
     */
    public Crop harvest() {
        PlantStage stage = getCurrentStage();
        if (!stage.canProduceCrop()) {
            return null;
        }

        PlantStage.StageType type = stage.getStageType();

        if (type == PlantStage.StageType.LOW_PRODUCTIVE) {
            return new Crop(lowCropName, lowCropPrice, yield, false, false);
        }

        if (type == PlantStage.StageType.HIGH_PRODUCTIVE) {
            return new Crop(highCropName, highCropPrice, yield * 2,
                            highCropIsRoot, true);
        }

        if (type == PlantStage.StageType.FULLY_MATURE) {
            // Root crop bonus does NOT apply at fully mature stage
            return new Crop(highCropName, highCropPrice, yield * 2,
                            false, false);
        }

        return null;
    }

    /**
     * Returns the plant's name.
     *
     * @return The plant name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the price of the plant's seed.
     *
     * @return The seed price.
     */
    public int getSeedPrice() {
        return seedPrice;
    }

    /**
     * Returns the base crop yield of the plant (before stage multipliers).
     *
     * @return The yield amount.
     */
    public int getYield() {
        return yield;
    }

    /**
     * Returns the preferred soil type for this plant.
     *
     * @return The preferred soil type string.
     */
    public String getPreferredSoil() {
        return preferredSoil;
    }

    /**
     * Returns the full stage sequence array for this plant.
     *
     * @return The array of PlantStage objects.
     */
    public PlantStage[] getStages() {
        return stages;
    }

    /**
     * Returns the index of the plant's current stage in its stage sequence.
     *
     * @return The current stage index.
     */
    public int getCurrentStageIndex() {
        return currentStageIndex;
    }

    /**
     * Returns the total number of stages in this plant's stage sequence.
     *
     * @return The total stage count.
     */
    public int getTotalStages() {
        return stages.length;
    }

    /**
     * Checks whether the plant has been watered today.
     *
     * @return true if watered, false otherwise.
     */
    public boolean isWatered() {
        return watered;
    }

    /**
     * Returns the crop name produced at the Low Productive stage.
     *
     * @return The low stage crop name.
     */
    public String getLowCropName() {
        return lowCropName;
    }

    /**
     * Returns the base price per piece at the Low Productive stage.
     *
     * @return The low stage crop price.
     */
    public int getLowCropPrice() {
        return lowCropPrice;
    }

    /**
     * Returns the crop name produced at the High Productive stage.
     *
     * @return The high stage crop name.
     */
    public String getHighCropName() {
        return highCropName;
    }

    /**
     * Returns the base price per piece at the High Productive stage.
     *
     * @return The high stage crop price.
     */
    public int getHighCropPrice() {
        return highCropPrice;
    }

    /**
     * Returns whether the High Productive crop is a Root Crop,
     * making it eligible for the 50% price bonus at that stage.
     *
     * @return true if the high stage crop is a Root Crop.
     */
    public boolean isHighCropRoot() {
        return highCropIsRoot;
    }
}
