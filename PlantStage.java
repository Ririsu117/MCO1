/**
 * Represents a growth stage of a plant in the Verdant Sun Farming Simulator.
 *
 * Each stage defines rules for:
 * • growth progression
 * • watering requirements
 * • fertilizer interactions
 * • crop production behavior
 *
 * Concrete subclasses implement the specific logic for each stage.
 */
public abstract class PlantStage {

    /**
     * Identifies the category of plant stage.
     *
     * Used for display and stage-specific logic in Game.
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
     * Returns the type of this plant stage.
     *
     * @return StageType identifier of the stage.
     */
    public abstract StageType getStageType();

    /**
     * Indicates whether watering is required for the plant
     * to progress to the next stage.
     *
     * @return true if watering is required for growth.
     */
    public abstract boolean needsWatering();

    /**
     * Indicates whether the stage progresses automatically each day
     * regardless of watering.
     *
     * @return true if the stage progresses automatically.
     */
    public abstract boolean isAutoProgress();

    /**
     * Indicates whether watering negatively affects growth in this stage.
     *
     * @return true if watering prevents growth.
     */
    public abstract boolean isWateringHarmful();

    /**
     * Returns the base number of stage advancements gained
     * when growth conditions are met.
     *
     * @return number of stages progressed.
     */
    public abstract int getBaseGrowth();

    /**
     * Indicates whether this stage produces crop when harvested.
     *
     * @return true if the stage can produce crop.
     */
    public abstract boolean canProduceCrop();

    /**
     * Returns the crop yield multiplier applied at harvest.
     *
     * @return multiplier applied to base yield.
     */
    public abstract int getYieldMultiplier();

    /**
     * Indicates whether soil and fertilizer bonuses apply in this stage.
     *
     * @return true if bonuses are applied.
     */
    public abstract boolean acceptsBonuses();

    /**
     * Returns the display label of this stage.
     *
     * @return human-readable stage name.
     */
    public abstract String getDisplayName();


    /**
     * Initial plant stage.
     *
     * Requires watering to grow.
     * Bonuses from preferred soil and fertilizer are doubled.
     */
    public static class Seedling extends PlantStage {

        /**
         * Returns the stage type identifier.
         *
         * @return SEEDLING stage type.
         */
        @Override
        public StageType getStageType() {
            return StageType.SEEDLING;
        }

        /**
         * Indicates watering is required.
         *
         * @return true
         */
        @Override
        public boolean needsWatering() {
            return true;
        }

        /**
         * Indicates this stage does not auto-progress.
         *
         * @return false
         */
        @Override
        public boolean isAutoProgress() {
            return false;
        }

        /**
         * Indicates watering does not harm growth.
         *
         * @return false
         */
        @Override
        public boolean isWateringHarmful() {
            return false;
        }

        /**
         * Returns the base growth value.
         *
         * @return 1 stage progression.
         */
        @Override
        public int getBaseGrowth() {
            return 1;
        }

        /**
         * Indicates this stage cannot produce crop.
         *
         * @return false
         */
        @Override
        public boolean canProduceCrop() {
            return false;
        }

        /**
         * Returns crop multiplier.
         *
         * @return 0
         */
        @Override
        public int getYieldMultiplier() {
            return 0;
        }

        /**
         * Indicates bonuses apply.
         *
         * @return true
         */
        @Override
        public boolean acceptsBonuses() {
            return true;
        }

        /**
         * Returns display label.
         *
         * @return "Seedling"
         */
        @Override
        public String getDisplayName() {
            return "Seedling";
        }
    }


    /**
     * Resting stage of plant.
     *
     * Automatically progresses without watering.
     * Soil and fertilizer bonuses are ignored.
     */
    public static class Dormant extends PlantStage {

        @Override
        public StageType getStageType() {
            return StageType.DORMANT;
        }

        @Override
        public boolean needsWatering() {
            return false;
        }

        @Override
        public boolean isAutoProgress() {
            return true;
        }

        @Override
        public boolean isWateringHarmful() {
            return false;
        }

        @Override
        public int getBaseGrowth() {
            return 1;
        }

        @Override
        public boolean canProduceCrop() {
            return false;
        }

        @Override
        public int getYieldMultiplier() {
            return 0;
        }

        @Override
        public boolean acceptsBonuses() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Dormant";
        }
    }


    /**
     * Nutrient absorption stage.
     *
     * Automatically progresses.
     * Watering prevents growth.
     * Fertilizer loses an additional day of effectiveness.
     */
    public static class Energizing extends PlantStage {

        @Override
        public StageType getStageType() {
            return StageType.ENERGIZING;
        }

        @Override
        public boolean needsWatering() {
            return false;
        }

        @Override
        public boolean isAutoProgress() {
            return true;
        }

        @Override
        public boolean isWateringHarmful() {
            return true;
        }

        @Override
        public int getBaseGrowth() {
            return 1;
        }

        @Override
        public boolean canProduceCrop() {
            return false;
        }

        @Override
        public int getYieldMultiplier() {
            return 0;
        }

        @Override
        public boolean acceptsBonuses() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Energizing";
        }
    }


    /**
     * First productive stage of plant.
     *
     * Requires watering.
     * Accepts soil and fertilizer bonuses.
     * Produces 1x crop yield.
     */
    public static class LowProductive extends PlantStage {

        @Override
        public StageType getStageType() {
            return StageType.LOW_PRODUCTIVE;
        }

        @Override
        public boolean needsWatering() {
            return true;
        }

        @Override
        public boolean isAutoProgress() {
            return false;
        }

        @Override
        public boolean isWateringHarmful() {
            return false;
        }

        @Override
        public int getBaseGrowth() {
            return 1;
        }

        @Override
        public boolean canProduceCrop() {
            return true;
        }

        @Override
        public int getYieldMultiplier() {
            return 1;
        }

        @Override
        public boolean acceptsBonuses() {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Low Productive";
        }
    }


    /**
     * High productivity stage.
     *
     * Requires watering.
     * Accepts bonuses.
     * Produces 2x crop yield.
     */
    public static class HighProductive extends PlantStage {

        @Override
        public StageType getStageType() {
            return StageType.HIGH_PRODUCTIVE;
        }

        @Override
        public boolean needsWatering() {
            return true;
        }

        @Override
        public boolean isAutoProgress() {
            return false;
        }

        @Override
        public boolean isWateringHarmful() {
            return false;
        }

        @Override
        public int getBaseGrowth() {
            return 1;
        }

        @Override
        public boolean canProduceCrop() {
            return true;
        }

        @Override
        public int getYieldMultiplier() {
            return 2;
        }

        @Override
        public boolean acceptsBonuses() {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "High Productive";
        }
    }


    /**
     * Final growth stage.
     *
     * Does not progress further.
     * Produces 2x crop yield.
     */
    public static class FullyMature extends PlantStage {

        @Override
        public StageType getStageType() {
            return StageType.FULLY_MATURE;
        }

        @Override
        public boolean needsWatering() {
            return false;
        }

        @Override
        public boolean isAutoProgress() {
            return false;
        }

        @Override
        public boolean isWateringHarmful() {
            return false;
        }

        @Override
        public int getBaseGrowth() {
            return 0;
        }

        @Override
        public boolean canProduceCrop() {
            return true;
        }

        @Override
        public int getYieldMultiplier() {
            return 2;
        }

        public boolean acceptsBonuses() {
            return false;
        }

        public String getDisplayName() {
            return "Fully Mature";
        }
    }
}
