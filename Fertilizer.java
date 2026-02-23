public class Fertilizer {
    private String name;
    private int price;
    private int remainingDays;

    public Fertilizer(String name, int price, int remainingDays) {
        this.name = name;
        this.price = price;
        this.remainingDays = remainingDays;
    }

    public void consumeDay() {
        if (remainingDays > 0) {
            remainingDays--;
        }
    }

    public boolean isActive() {
        return remainingDays > 0;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getRemainingDays() {
        return remainingDays;
    }
}