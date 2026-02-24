import java.util.ArrayList;
import java.util.List;

public class HighScoreTable {
    private List<HighScoreEntry> entries;
    private int maxEntries;

    public HighScoreTable() {
        this.entries = new ArrayList<>();
        this.maxEntries = 10;
    }

    public boolean qualifies(int savings) {
        if (entries.size() < maxEntries) {
            return true;
        }
        return savings > entries.get(entries.size() - 1).getSavings();
    }

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

    public void display() {
        System.out.println("=== HIGH SCORES ===");
        for (int i = 0; i < entries.size(); i++) {
            System.out.println((i + 1) + ". " + entries.get(i).getName() + " - " + entries.get(i).getSavings());
        }
    }

    public List<HighScoreEntry> getEntries() {
        return entries;
    }
}