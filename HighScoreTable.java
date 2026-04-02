import java.util.ArrayList;
import java.util.List;

/**
 * Represents a high score table that stores the top player scores
 * based on their final savings at the end of the game.
 * The table maintains a maximum number of entries and sorts them
 * in descending order of savings.
 */
public class HighScoreTable {
    private List<HighScoreEntry> entries;
    private int maxEntries;

    /**
     * Constructs a new HighScoreTable with an empty list of entries
     * and a maximum capacity of 10 high scores.
     */
    public HighScoreTable() {
        this.entries = new ArrayList<>();
        this.maxEntries = 10;
    }

    /**
     * Determines whether a player's savings qualify for the high score table.
     *
     * @param savings The player's final savings.
     * @return true if the score qualifies for the table, false otherwise.
     */
    public boolean qualifies(int savings) {
        if (entries.size() < maxEntries) {
            return true;
        }
        return savings > entries.get(entries.size() - 1).getSavings();
    }

    /**
     * Adds a new entry to the high score table if it qualifies.
     * The table is sorted after insertion and trimmed if it exceeds
     * the maximum number of entries.
     *
     * @param name The player's name.
     * @param savings The player's final savings score.
     */
    public void addEntry(String name, int savings) {
        if (!qualifies(savings)) {
            return;
        }
        entries.add(new HighScoreEntry(name, savings));
        sortEntries();
        if (entries.size() > maxEntries) {
            entries.remove(entries.size() - 1);
        }
    }

    /**
     * Sorts the high score entries in descending order of savings.
     * This method uses a simple bubble sort algorithm.
     */
    private void sortEntries() {
        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = 0; j < entries.size() - i - 1; j++) {
                if (entries.get(j).getSavings() < entries.get(j + 1).getSavings()) {
                    HighScoreEntry temp = entries.get(j);
                    entries.set(j, entries.get(j + 1));
                    entries.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Displays the high score table in the console.
     * Each entry includes the player's ranking, name, and savings.
     */

    public void display() {
        System.out.println("=== HIGH SCORES ===");
        for (int i = 0; i < entries.size(); i++) {
            System.out.println((i + 1) + ". " + entries.get(i).getName() + " - " + entries.get(i).getSavings());
        }
    }

    /**
     * Returns the list of high score entries currently stored in the table.
     *
     * @return A list containing HighScoreEntry objects.
     */
    public List<HighScoreEntry> getEntries() {
        return entries;
    }
}