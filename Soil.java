/**
 * Represents a single soil tile in the field of the
 * Verdant Sun Farming Simulator.
 *
 * Each tile stores information about:
 * - soil classification (loam, sand, gravel)
 * - plant currently growing on the tile
 * - fertilizer effects applied to the tile
 * - meteorite impact state
 * - excavation status
 *
 * Soil tiles remember their original soil type so that
 * meteorite-damaged tiles can be restored after excavation.
 *
 * Excavated meteorite tiles become permanently fertilized,
 * meaning they always provide fertilizer growth bonuses and
 * do not require temporary fertilizer application.
 */
public class Soil {

    private String type;
    private String originalType;
    private Plant plant;
    private Fertilizer fertilizer;

    private boolean isMeteoriteTile;
    private boolean isExcavated;
    private boolean isPermanentlyFertilized;

    /**
     * Creates a soil tile with the specified soil type.
     *
     * The original soil type is stored separately so the tile
     * can be restored if a meteorite impact is excavated.
     *
     * @param type soil classification ("loam", "sand", or "gravel")
     */
    public Soil(String type) {

        this.type = type;

        this.originalType = type;

        this.plant = null;

        this.fertilizer = null;

        this.isMeteoriteTile = false;

        this.isExcavated = false;

        this.isPermanentlyFertilized = false;
    }

    /**
     * Checks whether a plant currently exists on this tile.
     *
     * @return true if a plant is present
     */
    public boolean hasPlant() {

        return plant != null;
    }

    /**
     * Checks whether a temporary fertilizer is applied.
     *
     * Permanently fertilized tiles do not use a Fertilizer object.
     *
     * @return true if a temporary fertilizer exists
     */
    public boolean hasFertilizer() {

        return fertilizer != null;
    }

    /**
     * Determines whether this tile provides a fertilizer growth bonus.
     *
     * Fertilizer bonus applies if:
     * - temporary fertilizer is present OR
     * - tile is permanently fertilized via excavation
     *
     * @return true if fertilizer effect is active
     */
    public boolean isFertilized() {

        return fertilizer != null || isPermanentlyFertilized;
    }

    /**
     * Sets whether this tile is marked as a meteorite tile.
     *
     * @param value true to mark as meteorite tile
     */
    public void setMeteoriteTile(boolean value) {

        this.isMeteoriteTile = value;
    }

    /**
     * Excavates a meteorite tile.
     *
     * Effects:
     * - removes meteorite status
     * - restores original soil type
     * - marks tile as excavated
     * - permanently fertilizes the tile
     */
    public void excavate() {

        this.isMeteoriteTile = false;

        this.isExcavated = true;

        this.type = this.originalType;

        this.isPermanentlyFertilized = true;
    }

    /**
     * Returns the current soil type.
     *
     * @return soil classification string
     */
    public String getType() {

        return type;
    }

    /**
     * Returns the original soil type before meteorite impact.
     *
     * @return original soil classification
     */
    public String getOriginalType() {

        return originalType;
    }

    /**
     * Returns the plant currently on this tile.
     *
     * @return Plant object or null if empty
     */
    public Plant getPlant() {

        return plant;
    }

    /**
     * Places or removes a plant on this tile.
     *
     * @param plant Plant to place, or null to remove plant
     */
    public void setPlant(Plant plant) {

        this.plant = plant;
    }

    /**
     * Returns the temporary fertilizer applied to this tile.
     *
     * Does not apply to permanently fertilized tiles.
     *
     * @return Fertilizer object or null
     */
    public Fertilizer getFertilizer() {

        return fertilizer;
    }

    /**
     * Applies or removes temporary fertilizer.
     *
     * @param fertilizer Fertilizer to apply, or null to remove
     */
    public void setFertilizer(Fertilizer fertilizer) {

        this.fertilizer = fertilizer;
    }

    /**
     * Checks whether this tile is currently marked as a meteorite tile.
     *
     * @return true if tile contains meteorite damage
     */
    public boolean isMeteoriteTile() {

        return isMeteoriteTile;
    }

    /**
     * Checks whether this tile has been excavated.
     *
     * @return true if excavation has occurred
     */
    public boolean isExcavated() {

        return isExcavated;
    }

    /**
     * Checks whether this tile is permanently fertilized.
     *
     * Permanently fertilized tiles provide infinite fertilizer bonuses
     * and are never consumed.
     *
     * @return true if permanently fertilized
     */
    public boolean isPermanentlyFertilized() {

        return isPermanentlyFertilized;
    }
}
