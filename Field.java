public class Field {
    private Soil[][] grid;

    public Field() {
        grid = new Soil[10][10];
    }

    public void initializeGrid(String[][] layout) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                grid[row][col] = new Soil(layout[row][col]);
            }
        }
    }

    public Soil getSoil(int row, int col) {
        return grid[row][col];
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }

    public boolean hasWaterablePlant() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Soil soil = grid[row][col];
                if (soil.hasPlant() && !soil.getPlant().isWatered()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void display() {
        System.out.println("    0  1  2  3  4  5  6  7  8  9");
        System.out.println("   --------------------------------");
        for (int row = 0; row < 10; row++) {
            System.out.print(row + " | ");
            for (int col = 0; col < 10; col++) {
                System.out.print(getTileSymbol(row, col) + "  ");
            }
            System.out.println();
        }
    }

    private String getTileSymbol(int row, int col) {
        Soil soil = grid[row][col];
        if (soil.isMeteoriteTile()) {
            return "X";
        }
        if (soil.hasPlant()) {
            if (soil.getPlant().isMature()) {
                return "M";
            }
            return "*";
        }
        return soil.getType().substring(0, 1).toUpperCase();
    }
}