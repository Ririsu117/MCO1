/**
 * Represents a fertilizer that can be applied to soil tiles 
 * to enhance plant growth.
 *
 * A special Meteorite Fertilizer variant exists for tiles
 * excavated after the meteorite event. This variant is considered
 * permanently active and is never consumed by day progression.
 * The isMeteoriteFertilizer flag identifies this variant so that
 * consumeDay() and isActive() behave accordingly.
 *
 * For the Energizing plant stage, the fertilizer loses an additional
 * effect day per nextDay cycle. This is handled externally in Game
 * by calling consumeDay() twice; the Fertilizer class itself does
 * not need to know about plant stages.
 */
public class Fertilizer {
    private String name;
    private int price;
    private int remainingDays;
    private boolean isMeteoriteFertilizer;

    /**
     * Constructs a standard Fertilizer with the specified attributes.
     * This fertilizer will be consumed over time as days pass.
     *
     * @param name          The name of the fertilizer.
     * @param price         The cost to purchase the fertilizer.
     * @param remainingDays The number of days the fertilizer remains active.
     */
    public Fertilizer(String name, int price, int remainingDays) {
        this.name = name;
        this.price = price;
        this.remainingDays = remainingDays;
        this.isMeteoriteFertilizer = false;
    }

    /**
     * Constructs a Fertilizer with full control over all attributes,
     * including whether it is a permanent Meteorite Fertilizer.
     *
     * @param name                 The name of the fertilizer.
     * @param price                The cost to purchase the fertilizer.
     * @param remainingDays        The number of days the fertilizer remains active.
     * @param isMeteoriteFertilizer Whether this is a permanent meteorite fertilizer.
     */
    public Fertilizer(String name, int price, int remainingDays,
                      boolean isMeteoriteFertilizer) {
        this.name = name;
        this.price = price;
        this.remainingDays = remainingDays;
        this.isMeteoriteFertilizer = isMeteoriteFertilizer;
    }

    /**
     * Reduces the remaining active days of the fertilizer by one.
     * If this is a Meteorite Fertilizer, this method does nothing.
     * meteorite fertilizer is never consumed.
     */
    public void consumeDay() {
        if (isMeteoriteFertilizer) {
            return;
        }
        if (remainingDays > 0) {
            remainingDays--;
        }
    }

    /**
     * Checks whether the fertilizer is still active.
     * Meteorite Fertilizer is always active.
     * Standard fertilizer is active while remainingDays is greater than zero.
     *
     * @return true if the fertilizer effect is still in effect, false otherwise.
     */
    public boolean isActive() {
        if (isMeteoriteFertilizer) {
            return true;
        }
        return remainingDays > 0;
    }

    /**
     * Returns the name of the fertilizer.
     *
     * @return The fertilizer name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the purchase price of the fertilizer.
     *
     * @return The fertilizer price.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Returns the number of days the fertilizer effect remains active.
     * For Meteorite Fertilizer, this returns -1 to indicate infinite duration.
     *
     * @return The remaining active days, or -1 if permanently active.
     */
    public int getRemainingDays() {
        if (isMeteoriteFertilizer) {
            return -1;
        }
        return remainingDays;
    }

    /**
     * Checks whether this fertilizer is a permanent Meteorite Fertilizer.
     * Meteorite Fertilizer is never consumed and always active.
     *
     * @return true if this is a Meteorite Fertilizer, false otherwise.
     */
    public boolean isMeteoriteFertilizer() {
        return isMeteoriteFertilizer;
    }
}
