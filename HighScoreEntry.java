/**
 * Represents a single entry in the high score table.
 * Each entry stores the player's name and their final savings.
 */
public class HighScoreEntry {
    private String name;
    private int savings;

    /**
     * Constructs a new HighScoreEntry with the specified player name
     * and savings amount.
     *
     * @param name The name of the player.
     * @param savings The player's final savings score.
     */
    public HighScoreEntry(String name, int savings) {
        this.name = name;
        this.savings = savings;
    }

    /**
     * Returns the name of the player associated with this high score entry.
     *
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the savings value stored for this high score entry.
     *
     * @return The player's final savings score.
     */
    public int getSavings() {
        return savings;
    }
}