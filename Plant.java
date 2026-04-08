/**
 * Represents a plant that can be grown in the Verdant Sun Farming Simulator.
 *
 * Each plant progresses through an ordered sequence of PlantStage objects
 * rather than using a simple numeric growth counter. The plant tracks its
 * current position within the stage sequence, and all growth behavior,
 * watering rules, and harvest logic are determined by the active PlantStage.
 *
 * Each plant produces different crop results depending on the stage reached:
 * - Low Productive stage produces the low-tier crop
 * - High Productive stage produces the high-tier crop
 * - Fully Mature stage also produces the high-tier crop, but without root bonus
 *
 * Crop yield and pricing depend on the plant's configured attributes.
 */
public class Plant {

    private String name;
    private int seedPrice;
    private int yield;
    private String preferredSoil;
    private boolean watered;

    /** Ordered sequence of growth stages the plant progresses through. */
    private PlantStage[] stages;

    /** Index representing the plant's current position in the stage sequence. */
    private int currentStageIndex;

    /** Crop name produced when harvested at Low Productive stage. */
    private String lowCropName;

    /** Base price per crop unit at Low Productive stage. */
    private int lowCropPrice;

    /** Crop name produced when harvested at High Productive stage. */
    private String highCropName;

    /** Base price per crop unit at High Productive stage. */
    private int highCropPrice;

    /** Indicates whether the High Productive crop is classified as a Root Crop. */
    private boolean highCropIsRoot;

    /**
     * Creates a Plant with stage-based growth attributes.
     *
     * @param name name of the plant
     * @param seedPrice cost of purchasing the seed
     * @param yield base number of crop units produced
     * @param preferredSoil soil type that provides growth bonus
     * @param stages ordered sequence of PlantStage objects
     * @param lowCropName crop produced at Low Productive stage
     * @param lowCropPrice base price per unit at Low Productive stage
     * @param highCropName crop produced at High Productive stage
     * @param highCropPrice base price per unit at High Productive stage
     * @param highCropIsRoot true if high-tier crop qualifies for root bonus
     */
    public Plant(
            String name,
            int seedPrice,
            int yield,
            String preferredSoil,
            PlantStage[] stages,
            String lowCropName,
            int lowCropPrice,
            String highCropName,
            int highCropPrice,
            boolean highCropIsRoot
    ) {

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
     * Returns the PlantStage currently active for this plant.
     *
     * @return current PlantStage object
     */
    public PlantStage getCurrentStage() {
        return stages[currentStageIndex];
    }

    /**
     * Returns the StageType of the current PlantStage.
     *
     * @return current stage type enum value
     */
    public PlantStage.StageType getCurrentStageType() {
        return getCurrentStage().getStageType();
    }

    /**
     * Determines whether the plant has reached the Fully Mature stage.
     *
     * @return true if the plant is fully mature
     */
    public boolean isFullyMature() {
        return getCurrentStage().getStageType()
                == PlantStage.StageType.FULLY_MATURE;
    }

    /**
     * Determines whether the plant can currently produce crop.
     *
     * Harvest is possible at:
     * - Low Productive stage
     * - High Productive stage
     * - Fully Mature stage
     *
     * @return true if crop can be produced
     */
    public boolean canHarvest() {
        return getCurrentStage().canProduceCrop();
    }

    /**
     * Advances the plant forward in its stage sequence.
     *
     * The plant will not progress beyond its final stage.
     *
     * @param stages number of stages to advance
     */
    public void grow(int stages) {

        if (isFullyMature()) {
            return;
        }

        int maxIndex = this.stages.length - 1;

        currentStageIndex =
                Math.min(currentStageIndex + stages, maxIndex);
    }

    /**
     * Marks the plant as watered for the current day.
     */
    public void water() {
        watered = true;
    }

    /**
     * Resets the watering status at the end of the day.
     */
    public void resetWatered() {
        watered = false;
    }

    /**
     * Produces a Crop object based on the plant's current stage.
     *
     * Stage outcomes:
     * - Low Productive → low crop yield (1x)
     * - High Productive → high crop yield (2x) with root bonus if applicable
     * - Fully Mature → high crop yield (2x) without root bonus
     *
     * @return Crop object if harvestable, otherwise null
     */
    public Crop harvest() {

        PlantStage stage = getCurrentStage();

        if (!stage.canProduceCrop()) {
            return null;
        }

        PlantStage.StageType type =
                stage.getStageType();

        if (type == PlantStage.StageType.LOW_PRODUCTIVE) {

            return new Crop(
                    lowCropName,
                    lowCropPrice,
                    yield,
                    false,
                    false
            );
        }

        if (type == PlantStage.StageType.HIGH_PRODUCTIVE) {

            return new Crop(
                    highCropName,
                    highCropPrice,
                    yield * 2,
                    highCropIsRoot,
                    true
            );
        }

        if (type == PlantStage.StageType.FULLY_MATURE) {

            return new Crop(
                    highCropName,
                    highCropPrice,
                    yield * 2,
                    false,
                    false
            );
        }

        return null;
    }

    /**
     * Returns the plant name.
     *
     * @return plant name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the seed purchase price.
     *
     * @return seed cost
     */
    public int getSeedPrice() {
        return seedPrice;
    }

    /**
     * Returns the base crop yield amount.
     *
     * @return base yield value
     */
    public int getYield() {
        return yield;
    }

    /**
     * Returns the preferred soil type of this plant.
     *
     * @return preferred soil type
     */
    public String getPreferredSoil() {
        return preferredSoil;
    }

    /**
     * Returns the full stage sequence of this plant.
     *
     * @return array of PlantStage objects
     */
    public PlantStage[] getStages() {
        return stages;
    }

    /**
     * Returns the index of the current growth stage.
     *
     * @return current stage index
     */
    public int getCurrentStageIndex() {
        return currentStageIndex;
    }

    /**
     * Returns the total number of stages in the plant lifecycle.
     *
     * @return number of growth stages
     */
    public int getTotalStages() {
        return stages.length;
    }

    /**
     * Checks whether the plant has already been watered today.
     *
     * @return true if watered
     */
    public boolean isWatered() {
        return watered;
    }

    /**
     * Returns crop name produced at Low Productive stage.
     *
     * @return low-tier crop name
     */
    public String getLowCropName() {
        return lowCropName;
    }

    /**
     * Returns crop price at Low Productive stage.
     *
     * @return low-tier crop price
     */
    public int getLowCropPrice() {
        return lowCropPrice;
    }

    /**
     * Returns crop name produced at High Productive stage.
     *
     * @return high-tier crop name
     */
    public String getHighCropName() {
        return highCropName;
    }

    /**
     * Returns crop price at High Productive stage.
     *
     * @return high-tier crop price
     */
    public int getHighCropPrice() {
        return highCropPrice;
    }

    /**
     * Indicates whether the high-tier crop qualifies as a Root Crop.
     *
     * Root crops receive a 50% value bonus during High Productive stage.
     *
     * @return true if crop is classified as root crop
     */
    public boolean isHighCropRoot() {
        return highCropIsRoot;
    }
}
