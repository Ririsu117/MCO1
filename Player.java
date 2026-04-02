/**
 * Represents the player of the farming simulator game.
 * The player has a name and a savings balance used to
 * purchase seeds, fertilizers, and other game actions.
 */
public class Player {
    private String name;
    private int savings;

    /**
     * Constructs a new Player with the specified name.
     * The player starts with an initial savings of 1000.
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.savings = 1000;
    }

    /**
     * Checks whether the player has enough savings to afford
     * a specified amount.
     *
     * @param amount The amount of money required.
     * @return true if the player has enough savings, false otherwise.
     */
    public boolean canAfford(int amount) {
        return savings >= amount;
    }

    /**
     * Deducts a specified amount from the player's savings
     * if they can afford it.
     *
     * @param amount The amount to deduct from savings.
     */
    public void deductSavings(int amount) {
        if (canAfford(amount)) {
            savings -= amount;
        }
    }

    /**
     * Adds a specified amount to the player's savings.
     *
     * @param amount The amount to add to savings.
     */
    public void addSavings(int amount) {
        savings += amount;
    }

    /**
     * Returns the name of the player.
     *
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current savings balance of the player.
     *
     * @return The player's savings.
     */
    public int getSavings() {
        return savings;
    }
}