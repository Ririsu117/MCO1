/**
 * Represents the farming field consisting of a 10x10 grid of soil tiles.
 * Each tile may contain a plant, fertilizer, or special states such as
 * meteorite tiles or permanently fertilized (excavated) tiles.
 *
 * Tile display symbols now reflect the plant's current growth
 * stage rather than just mature vs. growing, and watering checks
 * account for stage-based watering rules.
 */
public class Field {
    private Soil[][] grid;

    /**
     * Constructs a new Field object and initializes
     * an empty 10x10 grid of soil tiles.
     */
    public Field() {
        grid = new Soil[10][10];
    }

    /**
     * Initializes the grid using a provided layout of soil types.
     *
     * @param layout A 2D array containing soil type strings
     *  (e.g., "loam", "sand", "gravel") for each tile.
     */
    public void initializeGrid(String[][] layout) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                grid[row][col] = new Soil(layout[row][col]);
            }
        }
    }

    /**
     * Retrieves the Soil object located at a specific grid position.
     *
     * @param row The row index of the soil tile.
     * @param col The column index of the soil tile.
     * @return The Soil object at the specified position.
     */
    public Soil getSoil(int row, int col) {
        return grid[row][col];
    }

    /**
     * Checks whether the specified grid coordinates are within
     * the valid bounds of the field.
     *
     * @param row The row index to check.
     * @param col The column index to check.
     * @return true if the position is within the 10x10 grid, false otherwise.
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }

    /**
     * Determines if there are any plants in the field that can be watered.
     * A plant is waterable only if its current stage requires
     * watering (needsWatering() is true) and it has not already been
     * watered today.
     *
     * @return true if at least one waterable plant exists, false otherwise.
     */
    public boolean hasWaterablePlant() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Soil soil = grid[row][col];
                if (soil.hasPlant()) {
                    Plant plant = soil.getPlant();
                    if (!plant.isWatered() && plant.getCurrentStage().needsWatering()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Displays the field grid in the console with row and column labels.
     * Each tile symbol represents the current state of that soil tile.
     */
    public void display() {
        System.out.println("     0   1   2   3   4   5   6   7   8   9");
        System.out.println("   -----------------------------------------");
        for (int row = 0; row < 10; row++) {
            char rowLabel = (char)('A' + row);
            System.out.print(rowLabel + " | ");
            for (int col = 0; col < 10; col++) {
                System.out.printf("%-3s ", getTileSymbol(row, col));
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Legend: SD=Seedling DR=Dormant EN=Energizing");
        System.out.println("        LP=LowProd HP=HighProd FM=FullyMature");
        System.out.println("        X=Meteorite E=Excavated L/S/G=Empty soil");
    }

    /**
     * Determines the symbol used to represent a tile when the field is displayed.
     * Symbols now reflect the plant's current growth stage.
     *
     * @param row The row index of the tile.
     * @param col The column index of the tile.
     * @return A short string symbol representing the tile's current state.
     */
    private String getTileSymbol(int row, int col) {
        Soil soil = grid[row][col];

        if (soil.isMeteoriteTile()) {
            return "X";
        }

        if (soil.isExcavated()) {
            if (soil.hasPlant()) {
                return getPlantSymbol(soil.getPlant());
            }
            return "E";
        }

        if (soil.hasPlant()) {
            return getPlantSymbol(soil.getPlant());
        }

        return soil.getType().substring(0, 1).toUpperCase();
    }

    /**
     * Returns a short display symbol for a plant based on its current stage.
     *
     * @param plant The plant to get the symbol for.
     * @return A 2-letter stage symbol string.
     */
    private String getPlantSymbol(Plant plant) {
        switch (plant.getCurrentStageType()) {
            case SEEDLING:      return "SD";
            case DORMANT:       return "DR";
            case ENERGIZING:    return "EN";
            case LOW_PRODUCTIVE: return "LP";
            case HIGH_PRODUCTIVE: return "HP";
            case FULLY_MATURE:  return "FM";
            default:            return "??";
        }
    }
}
