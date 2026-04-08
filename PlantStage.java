/**
 * Represents a growth stage in a plant's lifecycle in the Verdant Sun Farming Simulator.
 *
 * Each PlantStage defines how a plant behaves during that stage, including:
 * - whether watering is required
 * - whether the stage progresses automatically
 * - whether watering negatively affects growth
 * - how many stages the plant advances per day
 * - whether crop can be produced
 * - whether soil or fertilizer bonuses apply
 *
 * Concrete subclasses implement the specific rules for each stage.
 */
public abstract class PlantStage {

    /**
     * Identifies the category of growth stage.
     */
    public enum StageType {

        SEEDLING,
        DORMANT,
        ENERGIZING,
        LOW_PRODUCTIVE,
        HIGH_PRODUCTIVE,
        FULLY_MATURE

    }

    /**
     * Returns the type identifier of this growth stage.
     *
     * @return StageType representing the current stage
     */
    public abstract StageType getStageType();

    /**
     * Indicates whether the plant must be watered in order to grow.
     *
     * @return true if watering is required for progression
     */
    public abstract boolean needsWatering();

    /**
     * Indicates whether the plant automatically progresses to the next stage.
     *
     * @return true if the stage advances without watering
     */
    public abstract boolean isAutoProgress();

    /**
     * Indicates whether watering negatively affects growth progression.
     *
     * @return true if watering prevents growth in this stage
     */
    public abstract boolean isWateringHarmful();

    /**
     * Returns the number of stage steps the plant advances in one day.
     *
     * @return base number of growth steps
     */
    public abstract int getBaseGrowth();

    /**
     * Indicates whether the plant can produce crop in this stage.
     *
     * @return true if harvesting produces crop
     */
    public abstract boolean canProduceCrop();

    /**
     * Returns the multiplier applied to the plant's base yield.
     *
     * @return crop yield multiplier value
     */
    public abstract int getYieldMultiplier();

    /**
     * Indicates whether soil preference and fertilizer bonuses apply.
     *
     * @return true if bonuses affect growth speed
     */
    public abstract boolean acceptsBonuses();

    /**
     * Returns the display name used in the GUI and logs.
     *
     * @return readable stage name
     */
    public abstract String getDisplayName();



    /**
     * Initial stage of plant growth.
     *
     * Requires watering and accepts bonuses.
     */
    public static class Seedling extends PlantStage {

        /**
         * Returns the stage type.
         *
         * @return SEEDLING
         */
        public StageType getStageType() {
            return StageType.SEEDLING;
        }

        /**
         * Indicates watering is required.
         *
         * @return true
         */
        public boolean needsWatering() {
            return true;
        }

        /**
         * Indicates stage does not auto-progress.
         *
         * @return false
         */
        public boolean isAutoProgress() {
            return false;
        }

        /**
         * Indicates watering does not harm growth.
         *
         * @return false
         */
        public boolean isWateringHarmful() {
            return false;
        }

        /**
         * Returns base growth value.
         *
         * @return 1
         */
        public int getBaseGrowth() {
            return 1;
        }

        /**
         * Indicates crop cannot be produced.
         *
         * @return false
         */
        public boolean canProduceCrop() {
            return false;
        }

        /**
         * Returns crop yield multiplier.
         *
         * @return 0
         */
        public int getYieldMultiplier() {
            return 0;
        }

        /**
         * Indicates bonuses apply.
         *
         * @return true
         */
        public boolean acceptsBonuses() {
            return true;
        }

        /**
         * Returns display name.
         *
         * @return Seedling
         */
        public String getDisplayName() {
            return "Seedling";
        }
    }



    /**
     * Resting stage of plant growth.
     *
     * Automatically progresses without watering.
     */
    public static class Dormant extends PlantStage {

        /**
         * Returns the stage type.
         *
         * @return DORMANT
         */
        public StageType getStageType() {
            return StageType.DORMANT;
        }

        /**
         * Indicates watering is not required.
         *
         * @return false
         */
        public boolean needsWatering() {
            return false;
        }

        /**
         * Indicates stage auto-progresses.
         *
         * @return true
         */
        public boolean isAutoProgress() {
            return true;
        }

        /**
         * Indicates watering does not harm growth.
         *
         * @return false
         */
        public boolean isWateringHarmful() {
            return false;
        }

        /**
         * Returns base growth value.
         *
         * @return 1
         */
        public int getBaseGrowth() {
            return 1;
        }

        /**
         * Indicates crop cannot be produced.
         *
         * @return false
         */
        public boolean canProduceCrop() {
            return false;
        }

        /**
         * Returns crop yield multiplier.
         *
         * @return 0
         */
        public int getYieldMultiplier() {
            return 0;
        }

        /**
         * Indicates bonuses do not apply.
         *
         * @return false
         */
        public boolean acceptsBonuses() {
            return false;
        }

        /**
         * Returns display name.
         *
         * @return Dormant
         */
        public String getDisplayName() {
            return "Dormant";
        }
    }



    /**
     * Nutrient absorption stage.
     *
     * Automatically progresses.
     * Watering prevents growth.
     */
    public static class Energizing extends PlantStage {

        /**
         * Returns the stage type.
         *
         * @return ENERGIZING
         */
        public StageType getStageType() {
            return StageType.ENERGIZING;
        }

        /**
         * Indicates watering is not required.
         *
         * @return false
         */
        public boolean needsWatering() {
            return false;
        }

        /**
         * Indicates stage auto-progresses.
         *
         * @return true
         */
        public boolean isAutoProgress() {
            return true;
        }

        /**
         * Indicates watering harms growth.
         *
         * @return true
         */
        public boolean isWateringHarmful() {
            return true;
        }

        /**
         * Returns base growth value.
         *
         * @return 1
         */
        public int getBaseGrowth() {
            return 1;
        }

        /**
         * Indicates crop cannot be produced.
         *
         * @return false
         */
        public boolean canProduceCrop() {
            return false;
        }

        /**
         * Returns crop yield multiplier.
         *
         * @return 0
         */
        public int getYieldMultiplier() {
            return 0;
        }

        /**
         * Indicates bonuses do not apply.
         *
         * @return false
         */
        public boolean acceptsBonuses() {
            return false;
        }

        /**
         * Returns display name.
         *
         * @return Energizing
         */
        public String getDisplayName() {
            return "Energizing";
        }
    }



    /**
     * First productive stage of plant.
     *
     * Requires watering and produces crop.
     */
    public static class LowProductive extends PlantStage {

        /**
         * Returns the stage type.
         *
         * @return LOW_PRODUCTIVE
         */
        public StageType getStageType() {
            return StageType.LOW_PRODUCTIVE;
        }

        /**
         * Indicates watering is required.
         *
         * @return true
         */
        public boolean needsWatering() {
            return true;
        }

        /**
         * Indicates stage does not auto-progress.
         *
         * @return false
         */
        public boolean isAutoProgress() {
            return false;
        }

        /**
         * Indicates watering does not harm growth.
         *
         * @return false
         */
        public boolean isWateringHarmful() {
            return false;
        }

        /**
         * Returns base growth value.
         *
         * @return 1
         */
        public int getBaseGrowth() {
            return 1;
        }

        /**
         * Indicates crop can be produced.
         *
         * @return true
         */
        public boolean canProduceCrop() {
            return true;
        }

        /**
         * Returns crop yield multiplier.
         *
         * @return 1
         */
        public int getYieldMultiplier() {
            return 1;
        }

        /**
         * Indicates bonuses apply.
         *
         * @return true
         */
        public boolean acceptsBonuses() {
            return true;
        }

        /**
         * Returns display name.
         *
         * @return Low Productive
         */
        public String getDisplayName() {
            return "Low Productive";
        }
    }



    /**
     * High productivity stage of plant.
     *
     * Produces double crop yield.
     */
    public static class HighProductive extends PlantStage {

        /**
         * Returns the stage type.
         *
         * @return HIGH_PRODUCTIVE
         */
        public StageType getStageType() {
            return StageType.HIGH_PRODUCTIVE;
        }

        /**
         * Indicates watering is required.
         *
         * @return true
         */
        public boolean needsWatering() {
            return true;
        }

        /**
         * Indicates stage does not auto-progress.
         *
         * @return false
         */
        public boolean isAutoProgress() {
            return false;
        }

        /**
         * Indicates watering does not harm growth.
         *
         * @return false
         */
        public boolean isWateringHarmful() {
            return false;
        }

        /**
         * Returns base growth value.
         *
         * @return 1
         */
        public int getBaseGrowth() {
            return 1;
        }

        /**
         * Indicates crop can be produced.
         *
         * @return true
         */
        public boolean canProduceCrop() {
            return true;
        }

        /**
         * Returns crop yield multiplier.
         *
         * @return 2
         */
        public int getYieldMultiplier() {
            return 2;
        }

        /**
         * Indicates bonuses apply.
         *
         * @return true
         */
        public boolean acceptsBonuses() {
            return true;
        }

        /**
         * Returns display name.
         *
         * @return High Productive
         */
        public String getDisplayName() {
            return "High Productive";
        }
    }



    /**
     * Final stage of plant growth.
     *
     * Does not progress further.
     */
    public static class FullyMature extends PlantStage {

        /**
         * Returns the stage type.
         *
         * @return FULLY_MATURE
         */
        public StageType getStageType() {
            return StageType.FULLY_MATURE;
        }

        /**
         * Indicates watering is not required.
         *
         * @return false
         */
        public boolean needsWatering() {
            return false;
        }

        /**
         * Indicates stage does not auto-progress.
         *
         * @return false
         */
        public boolean isAutoProgress() {
            return false;
        }

        /**
         * Indicates watering does not harm growth.
         *
         * @return false
         */
        public boolean isWateringHarmful() {
            return false;
        }

        /**
         * Returns base growth value.
         *
         * @return 0
         */
        public int getBaseGrowth() {
            return 0;
        }

        /**
         * Indicates crop can be produced.
         *
         * @return true
         */
        public boolean canProduceCrop() {
            return true;
        }

        /**
         * Returns crop yield multiplier.
         *
         * @return 2
         */
        public int getYieldMultiplier() {
            return 2;
        }

        /**
         * Indicates bonuses do not apply.
         *
         * @return false
         */
        public boolean acceptsBonuses() {
            return false;
        }

        /**
         * Returns display name.
         *
         * @return Fully Mature
         */
        public String getDisplayName() {
            return "Fully Mature";
        }
    }

}
