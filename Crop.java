/**
 * Represents a crop produced when a plant is harvested in the
 * Verdant Sun Farming Simulator.
 *
 * Crops have a name, a base price per piece, a quantity, and a flag
 * indicating whether they are a Root Crop. Root Crops receive a 50%
 * price bonus per piece when harvested at the High Productive stage,
 * but NOT at the Fully Mature stage.
 *
 * The total sell value is calculated based on the stage at which
 * the plant was harvested.
 */
public class Crop {
    private String name;
    private int basePrice;
    private int quantity;
    private boolean isRootCrop;
    private boolean harvestedAtHighProductive;

    /**
     * Constructs a Crop with all relevant harvest details.
     *
     * @param name The name of the crop (e.g., "Turnip Tops", "Turnip Tuber").
     * @param basePrice The base selling price per piece of this crop.
     * @param quantity The number of crop pieces produced.
     * @param isRootCrop Whether this crop qualifies for the root crop bonus.
     * @param harvestedAtHighProductive Whether the plant was in High Productive
     *                                  stage when harvested (triggers root crop bonus).
     */
    public Crop(String name, int basePrice, int quantity,
                boolean isRootCrop, boolean harvestedAtHighProductive) {
        this.name = name;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.isRootCrop = isRootCrop;
        this.harvestedAtHighProductive = harvestedAtHighProductive;
    }

    /**
     * Calculates the total sell value of this crop.
     * Root Crops harvested at High Productive stage receive a 50% bonus
     * per piece. All other crops sell at base price times quantity.
     *
     * @return The total gold earned from selling this crop.
     */
    public int calculateTotalValue() {
        if (isRootCrop && harvestedAtHighProductive) {
            int bonusPrice = (int)(basePrice * 1.5);
            return bonusPrice * quantity;
        }
        return basePrice * quantity;
    }

    /**
     * Returns the name of this crop.
     *
     * @return The crop name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the base selling price per piece of this crop,
     * before any stage multipliers are applied.
     *
     * @return The base price per piece.
     */
    public int getBasePrice() {
        return basePrice;
    }

    /**
     * Returns the quantity of crop pieces produced in this harvest.
     *
     * @return The crop quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns whether this crop is a Root Crop eligible for the
     * High Productive stage price bonus.
     *
     * @return true if this is a Root Crop, false otherwise.
     */
    public boolean isRootCrop() {
        return isRootCrop;
    }

    /**
     * Returns whether this crop was harvested at the High Productive stage,
     * which triggers the Root Crop bonus if applicable.
     *
     * @return true if harvested at High Productive stage.
     */
    public boolean isHarvestedAtHighProductive() {
        return harvestedAtHighProductive;
    }
}
