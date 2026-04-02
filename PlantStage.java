/**
 * Abstract class representing a single growth stage of a plant in
 * the Verdant Sun Farming Simulator.
 *
 * Each stage defines its own behavior for growth progression,
 * watering requirements, fertilizer interactions, and crop production.
 * Concrete subclasses implement the specific rules for each stage type.
 */
public abstract class PlantStage {

    /**
     * Enum identifying the type of each plant stage.
     * Used for display purposes and stage-specific logic in Game.
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
     * Returns the type identifier of this stage.
     *
     * @return The StageType enum value for this stage.
     */
    public abstract StageType getStageType();

    /**
     * Returns whether this stage requires watering to advance to the next stage.
     * If false, watering has no effect on growth progression.
     *
     * @return true if watering is needed for growth, false otherwise.
     */
    public abstract boolean needsWatering();

    /**
     * Returns whether this stage progresses automatically each day,
     * regardless of whether the plant is watered.
     * Only the Dormant stage returns true.
     *
     * @return true if the plant auto-progresses in this stage.
     */
    public abstract boolean isAutoProgress();

    /**
     * Returns whether watering this stage halts growth rather than helping it.
     * Only the Energizing stage returns true.
     *
     * @return true if watering stops growth in this stage.
     */
    public abstract boolean isWateringHarmful();

    /**
     * Returns the base number of stages grown when the normal growth
     * condition is met (e.g. watered on a normal stage).
     * The Seedling stage doubles this for all bonuses.
     *
     * @return The base growth stage count for this stage.
     */
    public abstract int getBaseGrowth();

    /**
     * Returns whether this stage can produce crop when harvested.
     * Only Low Productive, High Productive, and Fully Mature return true.
     *
     * @return true if harvesting yields crop, false otherwise.
     */
    public abstract boolean canProduceCrop();

    /**
     * Returns the crop yield multiplier for this stage.
     * Low Productive = 1x, High Productive = 2x, Fully Mature = 2x.
     * All other stages return 0 (no crop).
     *
     * @return The yield multiplier for this stage.
     */
    public abstract int getYieldMultiplier();

    /**
     * Returns whether preferred soil and fertilizer bonuses
     * are active in this stage. Dormant and Energizing stages
     * suppress these bonuses.
     *
     * @return true if bonuses from preferred soil and fertilizer apply.
     */
    public abstract boolean acceptsBonuses();

    /**
     * Returns a short display label for this stage,
     * used in the GUI and console output.
     *
     * @return A human-readable stage name string.
     */
    public abstract String getDisplayName();


    // =========================================================
    // Concrete stage subclasses
    // =========================================================

    /**
     * Seedling stage — the initial stage of any plant.
     * Requires watering to progress. All growth bonuses
     * (preferred soil, fertilizer) are doubled in this stage.
     * Base growth is 1 stage; with preferred soil = +1 extra (total 2 per bonus);
     * with fertilizer = +1 extra (doubled); combined all bonuses on seedling
     * allows up to 5 stages of growth in one day if all conditions are met.
     */
    public static class Seedling extends PlantStage {

        /** {@inheritDoc} */
        @Override
        public StageType getStageType() { return StageType.SEEDLING; }

        /** {@inheritDoc} */
        @Override
        public boolean needsWatering() { return true; }

        /** {@inheritDoc} */
        @Override
        public boolean isAutoProgress() { return false; }

        /** {@inheritDoc} */
        @Override
        public boolean isWateringHarmful() { return false; }

        /**
         * Returns 1 as the base growth. The Seedling stage doubles all
         * bonuses on top of this in the Game's nextDay logic.
         *
         * @return 1 base growth stage.
         */
        @Override
        public int getBaseGrowth() { return 1; }

        /** {@inheritDoc} */
        @Override
        public boolean canProduceCrop() { return false; }

        /** {@inheritDoc} */
        @Override
        public int getYieldMultiplier() { return 0; }

        /** {@inheritDoc} */
        @Override
        public boolean acceptsBonuses() { return true; }

        /** {@inheritDoc} */
        @Override
        public String getDisplayName() { return "Seedling"; }
    }

    /**
     * Dormant stage — the plant rests and stores energy.
     * Progresses automatically each day regardless of watering.
     * Fertilizer and preferred soil have no effect.
     * Cannot produce crop if harvested.
     */
    public static class Dormant extends PlantStage {

        /** {@inheritDoc} */
        @Override
        public StageType getStageType() { return StageType.DORMANT; }

        /** {@inheritDoc} */
        @Override
        public boolean needsWatering() { return false; }

        /** {@inheritDoc} */
        @Override
        public boolean isAutoProgress() { return true; }

        /** {@inheritDoc} */
        @Override
        public boolean isWateringHarmful() { return false; }

        /** {@inheritDoc} */
        @Override
        public int getBaseGrowth() { return 1; }

        /** {@inheritDoc} */
        @Override
        public boolean canProduceCrop() { return false; }

        /** {@inheritDoc} */
        @Override
        public int getYieldMultiplier() { return 0; }

        /** {@inheritDoc} */
        @Override
        public boolean acceptsBonuses() { return false; }

        /** {@inheritDoc} */
        @Override
        public String getDisplayName() { return "Dormant"; }
    }

    /**
     * Energizing stage — the plant stores nutrients from the soil.
     * Watering halts growth (the plant should NOT be watered in this stage).
     * Any fertilizer applied loses an additional effect day when this stage
     * is active (handled in Game's nextDay logic).
     * Cannot produce crop if harvested.
     */
    public static class Energizing extends PlantStage {

        /** {@inheritDoc} */
        @Override
        public StageType getStageType() { return StageType.ENERGIZING; }

        /** {@inheritDoc} */
        @Override
        public boolean needsWatering() { return false; }

        /** {@inheritDoc} */
        @Override
        public boolean isAutoProgress() { return true; }

        /** {@inheritDoc} */
        @Override
        public boolean isWateringHarmful() { return true; }

        /** {@inheritDoc} */
        @Override
        public int getBaseGrowth() { return 1; }

        /** {@inheritDoc} */
        @Override
        public boolean canProduceCrop() { return false; }

        /** {@inheritDoc} */
        @Override
        public int getYieldMultiplier() { return 0; }

        /** {@inheritDoc} */
        @Override
        public boolean acceptsBonuses() { return false; }

        /** {@inheritDoc} */
        @Override
        public String getDisplayName() { return "Energizing"; }
    }

    /**
     * Low Productive stage — the plant begins producing crop.
     * Behaves normally: requires watering, accepts preferred soil
     * and fertilizer bonuses. Produces crop equal to 1x the plant's yield.
     */
    public static class LowProductive extends PlantStage {

        /** {@inheritDoc} */
        @Override
        public StageType getStageType() { return StageType.LOW_PRODUCTIVE; }

        /** {@inheritDoc} */
        @Override
        public boolean needsWatering() { return true; }

        /** {@inheritDoc} */
        @Override
        public boolean isAutoProgress() { return false; }

        /** {@inheritDoc} */
        @Override
        public boolean isWateringHarmful() { return false; }

        /** {@inheritDoc} */
        @Override
        public int getBaseGrowth() { return 1; }

        /** {@inheritDoc} */
        @Override
        public boolean canProduceCrop() { return true; }

        /** {@inheritDoc} */
        @Override
        public int getYieldMultiplier() { return 1; }

        /** {@inheritDoc} */
        @Override
        public boolean acceptsBonuses() { return true; }

        /** {@inheritDoc} */
        @Override
        public String getDisplayName() { return "Low Productive"; }
    }

    /**
     * High Productive stage — the plant produces crop at double yield.
     * Behaves normally: requires watering, accepts preferred soil
     * and fertilizer bonuses. Produces crop equal to 2x the plant's yield.
     */
    public static class HighProductive extends PlantStage {

        /** {@inheritDoc} */
        @Override
        public StageType getStageType() { return StageType.HIGH_PRODUCTIVE; }

        /** {@inheritDoc} */
        @Override
        public boolean needsWatering() { return true; }

        /** {@inheritDoc} */
        @Override
        public boolean isAutoProgress() { return false; }

        /** {@inheritDoc} */
        @Override
        public boolean isWateringHarmful() { return false; }

        /** {@inheritDoc} */
        @Override
        public int getBaseGrowth() { return 1; }

        /** {@inheritDoc} */
        @Override
        public boolean canProduceCrop() { return true; }

        /** {@inheritDoc} */
        @Override
        public int getYieldMultiplier() { return 2; }

        /** {@inheritDoc} */
        @Override
        public boolean acceptsBonuses() { return true; }

        /** {@inheritDoc} */
        @Override
        public String getDisplayName() { return "High Productive"; }
    }

    /**
     * Fully Mature stage — the plant has reached its final state.
     * Behaves like High Productive (2x yield crop) but will never
     * advance further regardless of watering, fertilizer, or preferred soil.
     * Note: Root crops harvested at this stage do NOT receive the 50% root
     * crop bonus — that bonus only applies at High Productive stage.
     */
    public static class FullyMature extends PlantStage {

        /** {@inheritDoc} */
        @Override
        public StageType getStageType() { return StageType.FULLY_MATURE; }

        /** {@inheritDoc} */
        @Override
        public boolean needsWatering() { return false; }

        /** {@inheritDoc} */
        @Override
        public boolean isAutoProgress() { return false; }

        /** {@inheritDoc} */
        @Override
        public boolean isWateringHarmful() { return false; }

        /** {@inheritDoc} */
        @Override
        public int getBaseGrowth() { return 0; }

        /** {@inheritDoc} */
        @Override
        public boolean canProduceCrop() { return true; }

        /** {@inheritDoc} */
        @Override
        public int getYieldMultiplier() { return 2; }

        /** {@inheritDoc} */
        @Override
        public boolean acceptsBonuses() { return false; }

        /** {@inheritDoc} */
        @Override
        public String getDisplayName() { return "Fully Mature"; }
    }
}
