public class Player {
    private String name;
    private int savings;

    public Player(String name) {
        this.name = name;
        this.savings = 1000;
    }

    public boolean canAfford(int amount) {
        return savings >= amount;
    }

    public void deductSavings(int amount) {
        if (canAfford(amount)) {
            savings -= amount;
        }
    }

    public void addSavings(int amount) {
        savings += amount;
    }

    public String getName() {
        return name;
    }

    public int getSavings() {
        return savings;
    }
}