/**
 * Represents the farming field consisting of a 10x10 grid of Soil tiles.
 * Each tile may contain a plant, fertilizer effects, or special states such as
 * meteorite tiles or permanently fertilized (excavated) tiles.
 *
 * Tile display symbols reflect the plant's current growth stage rather than
 * simply indicating mature vs growing plants. Watering checks also account
 * for stage-based watering requirements.
 */
public class Field {
    private Soil[][] grid;

    /**
     * Creates a new Field object and initializes an empty
     * 10x10 grid of Soil tiles.
     *
     * All tiles must be initialized later using initializeGrid().
     */
    public Field() {
        grid = new Soil[10][10];
    }

    /**
     * Initializes the field grid using a provided layout of soil types.
     *
     * Each string in the layout represents the soil classification
     * of the corresponding tile (e.g., "loam", "sand", "gravel").
     *
     * Pre-condition:
     * layout must be a 10x10 2D array.
     *
     * @param layout a 10x10 2D array containing soil type strings
     */
    public void initializeGrid(String[][] layout) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                grid[row][col] = new Soil(layout[row][col]);
            }
        }
    }

    /**
     * Retrieves the Soil object located at the specified grid position.
     *
     * Pre-condition:
     * row and col must be valid indices within the field bounds.
     *
     * @param row the row index of the soil tile (0–9)
     * @param col the column index of the soil tile (0–9)
     * @return the Soil object located at the given position
     */
    public Soil getSoil(int row, int col) {
        return grid[row][col];
    }

    /**
     * Checks whether the specified grid coordinates are within
     * the valid boundaries of the 10x10 field.
     *
     * @param row the row index to validate
     * @param col the column index to validate
     * @return true if the coordinates are inside the grid, false otherwise
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }

    /**
     * Checks whether at least one plant in the field can currently be watered.
     *
     * A plant is considered waterable if:
     * - the tile contains a plant
     * - the plant has not yet been watered today
     * - the plant's current growth stage requires watering
     *
     * @return true if at least one plant can be watered, false otherwise
     */
    public boolean hasWaterablePlant() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Soil soil = grid[row][col];

                if (soil.hasPlant()) {
                    Plant plant = soil.getPlant();

                    if (!plant.isWatered()
                        && plant.getCurrentStage().needsWatering()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Displays the field grid in the console using row and column labels.
     *
     * Each tile is represented by a short symbol indicating its state:
     * - plant growth stage
     * - meteorite tile
     * - excavated tile
     * - empty soil type
     *
     * Legend is displayed below the grid for reference.
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
        System.out.println("Legend:");
        System.out.println("SD = Seedling");
        System.out.println("DR = Dormant");
        System.out.println("EN = Energizing");
        System.out.println("LP = Low Productive");
        System.out.println("HP = High Productive");
        System.out.println("FM = Fully Mature");
        System.out.println("X  = Meteorite");
        System.out.println("E  = Excavated");
        System.out.println("L/S/G = Empty soil type");
    }

    /**
     * Determines the display symbol used to represent a tile.
     *
     * Priority of display:
     * 1. Meteorite tile
     * 2. Excavated tile
     * 3. Tile containing plant
     * 4. Empty soil type initial
     *
     * @param row the row index of the tile
     * @param col the column index of the tile
     * @return a short string symbol representing the tile state
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
     * Returns the display symbol corresponding to a plant's current growth stage.
     *
     * Growth stage abbreviations:
     * SD = Seedling
     * DR = Dormant
     * EN = Energizing
     * LP = Low Productive
     * HP = High Productive
     * FM = Fully Mature
     *
     * @param plant the plant whose growth stage will be evaluated
     * @return a 2-letter string representing the plant's growth stage
     */
    private String getPlantSymbol(Plant plant) {

        switch (plant.getCurrentStageType()) {

            case SEEDLING:
                return "SD";

            case DORMANT:
                return "DR";

            case ENERGIZING:
                return "EN";

            case LOW_PRODUCTIVE:
                return "LP";

            case HIGH_PRODUCTIVE:
                return "HP";

            case FULLY_MATURE:
                return "FM";

            default:
                return "??";
        }
    }
}
