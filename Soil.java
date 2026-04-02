/**
 * Represents a single soil tile in the field of the
 * Verdant Sun Farming Simulator.
 *
 * In MCO2, a soil tile additionally tracks its original soil type
 * (so it can be restored after a meteorite excavation) and whether
 * it has been permanently fertilized (excavated meteorite tiles are
 * considered fertilized indefinitely, bypassing normal fertilizer logic).
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
     * Constructs a Soil object with a specified soil type.
     * The original type is stored separately so it can be restored
     * after a meteorite excavation.
     *
     * @param type The type of soil (e.g., "loam", "sand", or "gravel").
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
     * Checks whether the soil tile currently has a plant.
     *
     * @return true if a plant is present, false otherwise.
     */
    public boolean hasPlant() {
        return plant != null;
    }

    /**
     * Checks whether the soil tile currently has a temporary fertilizer applied.
     * Note: permanently fertilized tiles (post-excavation) do not use a
     * Fertilizer object — use isPermanentlyFertilized() for those.
     *
     * @return true if a Fertilizer object is present, false otherwise.
     */
    public boolean hasFertilizer() {
        return fertilizer != null;
    }

    /**
     * Checks whether this tile provides a fertilizer bonus to plant growth.
     * Returns true if either a temporary Fertilizer is applied OR the tile
     * has been permanently fertilized through meteorite excavation.
     *
     * @return true if any fertilizer effect is active on this tile.
     */
    public boolean isFertilized() {
        return fertilizer != null || isPermanentlyFertilized;
    }

    /**
     * Marks or unmarks this tile as a meteorite-impacted tile.
     *
     * @param value true to mark as meteorite tile, false to unmark.
     */
    public void setMeteoriteTile(boolean value) {
        this.isMeteoriteTile = value;
    }

    /**
     * Excavates this meteorite tile. Restores the soil type to its original
     * value, removes meteorite status, marks the tile as excavated, and
     * permanently fertilizes the tile indefinitely.
     */
    public void excavate() {
        this.isMeteoriteTile = false;
        this.isExcavated = true;
        this.type = this.originalType;
        this.isPermanentlyFertilized = true;
    }

    /**
     * Returns the current soil type of this tile.
     *
     * @return The soil type string.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the original soil type of this tile before any meteorite event.
     *
     * @return The original soil type string.
     */
    public String getOriginalType() {
        return originalType;
    }

    /**
     * Returns the plant currently growing on this soil tile.
     *
     * @return The Plant object, or null if no plant is present.
     */
    public Plant getPlant() {
        return plant;
    }

    /**
     * Places a plant on this soil tile.
     *
     * @param plant The Plant to place, or null to clear the tile.
     */
    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    /**
     * Returns the temporary fertilizer currently applied to this soil tile.
     * Returns null if no temporary fertilizer is present (check
     * isPermanentlyFertilized separately if needed).
     *
     * @return The Fertilizer object, or null if none is applied.
     */
    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    /**
     * Applies a temporary fertilizer to this soil tile.
     *
     * @param fertilizer The Fertilizer to apply, or null to remove it.
     */
    public void setFertilizer(Fertilizer fertilizer) {
        this.fertilizer = fertilizer;
    }

    /**
     * Checks if this tile is currently marked as a meteorite-impacted tile.
     *
     * @return true if the tile is a meteorite tile, false otherwise.
     */
    public boolean isMeteoriteTile() {
        return isMeteoriteTile;
    }

    /**
     * Checks if this tile has been excavated after a meteorite impact.
     *
     * @return true if the tile has been excavated, false otherwise.
     */
    public boolean isExcavated() {
        return isExcavated;
    }

    /**
     * Checks if this tile is permanently fertilized due to meteorite excavation.
     * Permanently fertilized tiles provide a fertilizer growth bonus indefinitely
     * and are never consumed, unlike temporary Fertilizer objects.
     *
     * @return true if the tile is permanently fertilized, false otherwise.
     */
    public boolean isPermanentlyFertilized() {
        return isPermanentlyFertilized;
    }
}
