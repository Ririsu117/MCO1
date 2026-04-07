import java.util.ArrayList;
import java.util.List;

/**
 * Core game logic controller for the Verdant Sun Farming Simulator (MCO2).
 * This class manages all game state and rules with no CLI input or output.
 * All actions return result strings that the GUI displays to the player.
 *
 * The GameGUI class owns this Game instance and calls its public methods
 * in response to player interactions.
 *
 * Fields:
 *   player                    - The player of the game
 *   field                     - The 10x10 game field
 *   wateringCan               - The player's watering can
 *   highScoreTable            - Top 10 high scores
 *   availablePlants           - All plantable plant templates
 *   availableFertilizers      - All purchasable fertilizers
 *   currentDay                - The current game day (1-20)
 *   maxDays                   - Total days in the season (20)
 *   meteoriteHit              - True once the meteorite event has fired
 *   meteoriteCoords           - Grid coords of meteorite-affected tiles
 *   meteoriteExcavationsToday - Tiles excavated today (max 5)
 */
public class Game {
    private Player player;
    private Field field;
    private WateringCan wateringCan;
    private HighScoreTable highScoreTable;
    private List<Plant> availablePlants;
    private List<Fertilizer> availableFertilizers;
    private int currentDay;
    private int maxDays;
    private boolean meteoriteHit;
    private int[][] meteoriteCoords;
    private int meteoriteExcavationsToday;

    /**
     * Constructs a new Game instance with all default values.
     * The season is 20 days; meteorite fires at end of day 15.
     */
    public Game() {
        this.field = new Field();
        this.wateringCan = new WateringCan(10);
        this.highScoreTable = new HighScoreTable();
        this.availablePlants = new ArrayList<>();
        this.availableFertilizers = new ArrayList<>();
        this.currentDay = 1;
        this.maxDays = 20;
        this.meteoriteHit = false;
        this.meteoriteCoords = new int[][] {
            {3,3},{3,4},{3,5},{3,6},
            {4,3},{4,4},{4,5},{4,6},
            {5,3},{5,4},{5,5},{5,6},
            {6,3},{6,4},{6,5},{6,6}
        };
        this.meteoriteExcavationsToday = 0;
    }

    /**
     * Initializes the game with the player's name and loads all JSON data.
     * Must be called before any other game actions.
     *
     * @param playerName The name entered by the player.
     */
    public void initialize(String playerName) {
        this.player = new Player(playerName);
        this.availablePlants = JSONLoader.loadPlants("Plants.json");
        this.availableFertilizers = JSONLoader.loadFertilizers("Fertilizers.json");
        String[][] mapLayout = JSONLoader.loadMap("Map.json");
        field.initializeGrid(mapLayout);
        JSONLoader.loadHighScores("HighScores.json", highScoreTable);
    }

    /**
     * Plants a seed on the specified tile.
     *
     * @param plantIndex Index into the affordable plants list (0-based).
     * @param row        Row index of the target tile.
     * @param col        Column index of the target tile.
     * @return A result message string for the GUI to display.
     */
    public String plantSeed(int plantIndex, int row, int col) {
        List<Plant> affordable = getAffordablePlants();
        if (plantIndex < 0 || plantIndex >= affordable.size()) {
            return "Invalid plant selection.";
        }
        if (!field.isValidPosition(row, col)) {
            return "Invalid position.";
        }
        Soil soil = field.getSoil(row, col);
        if (soil.hasPlant()) {
            return "There is already a plant here!";
        }
        if (soil.isMeteoriteTile()) {
            return "Cannot plant on a meteorite tile!";
        }
        Plant template = affordable.get(plantIndex);
        Plant newPlant = new Plant(
            template.getName(), template.getSeedPrice(), template.getYield(),
            template.getPreferredSoil(), template.getStages(),
            template.getLowCropName(), template.getLowCropPrice(),
            template.getHighCropName(), template.getHighCropPrice(),
            template.isHighCropRoot()
        );
        soil.setPlant(newPlant);
        player.deductSavings(template.getSeedPrice());
        return template.getName() + " planted at " + positionToString(row, col) + "!";
    }

    /**
     * Waters the plant on the specified tile.
     *
     * @param row Row index of the tile.
     * @param col Column index of the tile.
     * @return A result message string for the GUI to display.
     */
    public String waterTile(int row, int col) {
        if (!wateringCan.canWater()) {
            return "Watering can is empty! Please refill first.";
        }
        if (!field.isValidPosition(row, col)) {
            return "Invalid position.";
        }
        Soil soil = field.getSoil(row, col);
        if (!soil.hasPlant()) {
            return "No plant at " + positionToString(row, col) + ".";
        }
        Plant plant = soil.getPlant();
        if (plant.isWatered()) {
            return "Plant at " + positionToString(row, col) + " is already watered.";
        }
        plant.water();
        wateringCan.useWater();
        return "Watered " + plant.getName() + " at " + positionToString(row, col)
            + " [" + plant.getCurrentStage().getDisplayName() + "]"
            + " | Water: " + wateringCan.getCurrentWaterLevel();
    }

    /**
     * Refills the watering can to maximum capacity. Costs 100 gold.
     *
     * @return A result message string for the GUI to display.
     */
    public String refillWateringCan() {
        if (!player.canAfford(100)) {
            return "Not enough savings to refill! (costs 100g)";
        }
        player.deductSavings(100);
        wateringCan.refill();
        return "Watering can refilled! Savings: " + player.getSavings();
    }

    /**
     * Applies fertilizer to the specified tile.
     *
     * @param fertIndex Index into availableFertilizers (0-based).
     * @param row       Row index of the tile.
     * @param col       Column index of the tile.
     * @return A result message string for the GUI to display.
     */
    public String applyFertilizer(int fertIndex, int row, int col) {
        if (fertIndex < 0 || fertIndex >= availableFertilizers.size()) {
            return "Invalid fertilizer selection.";
        }
        Fertilizer selected = availableFertilizers.get(fertIndex);
        if (!player.canAfford(selected.getPrice())) {
            return "Not enough savings for " + selected.getName() + "!";
        }
        if (!field.isValidPosition(row, col)) {
            return "Invalid position.";
        }
        Soil soil = field.getSoil(row, col);
        if (soil.isFertilized()) {
            return "Soil at " + positionToString(row, col) + " already has fertilizer.";
        }
        Fertilizer newFert = new Fertilizer(
            selected.getName(), selected.getPrice(), selected.getRemainingDays());
        soil.setFertilizer(newFert);
        player.deductSavings(selected.getPrice());
        return "Applied " + selected.getName() + " at " + positionToString(row, col)
            + "! Savings: " + player.getSavings();
    }

    /**
     * Removes or harvests the plant on the specified tile.
     * If the plant can produce crop it is harvested; otherwise removed.
     *
     * @param row Row index of the tile.
     * @param col Column index of the tile.
     * @return A result message string for the GUI to display.
     */
    public String removeOrHarvest(int row, int col) {
        if (!field.isValidPosition(row, col)) {
            return "Invalid position.";
        }
        Soil soil = field.getSoil(row, col);
        if (!soil.hasPlant()) {
            return "No plant at " + positionToString(row, col) + ".";
        }
        Plant plant = soil.getPlant();
        String result;
        if (plant.canHarvest()) {
            Crop crop = plant.harvest();
            if (crop != null) {
                int value = crop.calculateTotalValue();
                player.addSavings(value);
                result = "Harvested " + crop.getQuantity() + "x " + crop.getName()
                    + " for " + value + "g!"
                    + " [" + plant.getCurrentStage().getDisplayName() + "]"
                    + " | Savings: " + player.getSavings();
            } else {
                result = "Nothing to harvest.";
            }
        } else {
            result = "Removed " + plant.getName() + " from "
                + positionToString(row, col)
                + " [" + plant.getCurrentStage().getDisplayName() + "].";
        }
        soil.setPlant(null);
        return result;
    }

    /**
     * Excavates a meteorite tile at the specified position.
     * Costs 500 gold; tile becomes permanently fertilized.
     *
     * @param row Row index of the tile.
     * @param col Column index of the tile.
     * @return A result message string for the GUI to display.
     */
    public String excavateTile(int row, int col) {
    if (meteoriteExcavationsToday >= 5) {
        return "Already excavated 5 tiles today (daily limit).";
    }
    if (!field.isValidPosition(row, col)) {
        return "Invalid position.";
    }
    Soil soil = field.getSoil(row, col);
    if (!soil.isMeteoriteTile()) {
        return positionToString(row, col) + " is not a meteorite tile.";
    }
    if (!player.canAfford(500)) {
        return "Not enough savings to excavate (costs 500g).";
    }
    player.deductSavings(500);

    // Restore original soil type, clear meteorite flag, permanently fertilize
    player.deductSavings(500);
    soil.excavate(); // restores type, clears meteorite flag, permanently fertilizes
    meteoriteExcavationsToday++;

    meteoriteExcavationsToday++;
    return "Excavated " + positionToString(row, col)
        + "! Restored to " + soil.getType() + " soil, permanently fertilized."
        + " Savings: " + player.getSavings();
}

    /**
     * Advances the game to the next day using stage-aware growth logic.
     *
     * @return A result message string including any special events.
     */
    public String nextDay() {
        StringBuilder log = new StringBuilder();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Soil soil = field.getSoil(row, col);
                if (!soil.hasPlant()) continue;

                Plant plant = soil.getPlant();
                PlantStage stage = plant.getCurrentStage();

                if (stage.isAutoProgress()) {
                    if (!(stage.isWateringHarmful() && plant.isWatered())) {
                        plant.grow(stage.getBaseGrowth());
                    }
                } else if (stage.needsWatering() && plant.isWatered()) {
                    int growStages = stage.getBaseGrowth();
                    boolean isSeedling =
                        stage.getStageType() == PlantStage.StageType.SEEDLING;
                    if (stage.acceptsBonuses()
                            && soil.getType().equals(plant.getPreferredSoil())) {
                        growStages += isSeedling ? 2 : 1;
                    }
                    if (stage.acceptsBonuses() && soil.isFertilized()) {
                        growStages += isSeedling ? 2 : 1;
                    }
                    plant.grow(growStages);
                }

                plant.resetWatered();

                if (soil.hasFertilizer()) {
                    boolean isEnergizing =
                        plant.getCurrentStage().getStageType()
                            == PlantStage.StageType.ENERGIZING;
                    soil.getFertilizer().consumeDay();
                    if (isEnergizing) soil.getFertilizer().consumeDay();
                    if (!soil.getFertilizer().isActive()) soil.setFertilizer(null);
                }
            }
        }

        player.addSavings(50);
        currentDay++;
        meteoriteExcavationsToday = 0;
        log.append("Day ended. +50g | Savings: ").append(player.getSavings());

        if (currentDay == 16 && !meteoriteHit) {
            meteoriteHit = true;
            for (int[] coord : meteoriteCoords) {
                Soil soil = field.getSoil(coord[0], coord[1]);
                soil.setPlant(null);
                soil.setMeteoriteTile(true);
            }
            log.append("\n\u2605 METEORITE HIT! Tiles D3-G6 destroyed!")
               .append("\nExcavate them for 500g each (max 5/day).");
        }

        return log.toString();
    }

    /**
     * Finalizes the game, saves the high score, and returns the end summary.
     *
     * @return A formatted end-game summary string for the GUI to display.
     */
    public String endGame() {
        StringBuilder sb = new StringBuilder();
        sb.append("GAME OVER!\n");
        sb.append("Final Savings: ").append(player.getSavings()).append("g\n\n");
        if (highScoreTable.qualifies(player.getSavings())) {
            highScoreTable.addEntry(player.getName(), player.getSavings());
            JSONLoader.saveHighScores("HighScores.json", highScoreTable);
            sb.append("You made the High Score Table!\n\n");
        } else {
            sb.append("You did not qualify for the High Score Table.\n\n");
        }
        sb.append("=== HIGH SCORES ===\n");
        List<HighScoreEntry> entries = highScoreTable.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            sb.append((i + 1)).append(". ")
              .append(entries.get(i).getName())
              .append(" - ").append(entries.get(i).getSavings()).append("g\n");
        }
        return sb.toString();
    }

    /** @return The game field. */
    public Field getField() { return field; }

    /** @return The current day number. */
    public int getCurrentDay() { return currentDay; }

    /** @return The total number of days in the season. */
    public int getMaxDays() { return maxDays; }

    /** @return The player's current savings. */
    public int getSavings() { return player.getSavings(); }

    /** @return The player's name. */
    public String getPlayerName() { return player.getName(); }

    /** @return The watering can's current water level. */
    public int getWaterLevel() { return wateringCan.getCurrentWaterLevel(); }

    /** @return The watering can's maximum water level. */
    public int getMaxWaterLevel() { return wateringCan.getMaxWaterLevel(); }

    /** @return True if the watering can has water. */
    public boolean canWater() { return wateringCan.canWater(); }

    /** @return True if the meteorite event has occurred. */
    public boolean isMeteoriteHit() { return meteoriteHit; }

    /** @return True if the game season is over. */
    public boolean isGameOver() { return currentDay > maxDays; }

    /** @return True if the player can afford the watering can refill. */
    public boolean canAffordRefill() { return player.canAfford(100); }

    /** @return True if the player can afford excavation. */
    public boolean canAffordExcavation() { return player.canAfford(500); }

    /** @return True if today's excavation limit has been reached. */
    public boolean excavationLimitReached() {
        return meteoriteExcavationsToday >= 5;
    }

    /** @return The list of all plant templates. */
    public List<Plant> getAvailablePlants() { return availablePlants; }

    /** @return The list of all fertilizer options. */
    public List<Fertilizer> getAvailableFertilizers() { return availableFertilizers; }

    /**
     * Returns plants the player can currently afford.
     *
     * @return A filtered list of affordable Plant templates.
     */
    public List<Plant> getAffordablePlants() {
        List<Plant> affordable = new ArrayList<>();
        for (Plant p : availablePlants) {
            if (player.canAfford(p.getSeedPrice())) affordable.add(p);
        }
        return affordable;
    }

    /**
     * Returns whether the field has any plant that can be watered right now.
     *
     * @return true if at least one un-watered plant needs watering.
     */
    public boolean hasWaterablePlant() { return field.hasWaterablePlant(); }

    /**
     * Converts grid coordinates to a position string (e.g., "A5").
     *
     * @param row The row index.
     * @param col The column index.
     * @return The position string.
     */
    private String positionToString(int row, int col) {
        return (char)('A' + row) + "" + col;
    }
}
