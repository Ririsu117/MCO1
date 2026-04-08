import java.util.ArrayList;
import java.util.List;

/**
 * Core game logic controller.
 *
 * This class manages the entire game state and enforces all gameplay rules.
 * It performs no direct input/output operations. Instead, all public methods
 * return result messages that are displayed by the GUI.
 *
 * Responsibilities include:
 * - managing player progression and savings
 * - handling planting, watering, fertilizing, harvesting, and excavation
 * - advancing plant growth each day
 * - triggering special events (meteorite impact)
 * - maintaining high score records
 *
 * The GameGUI class owns this Game instance and calls its public methods
 * in response to player interactions.
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
     * Creates a new Game instance with default values.
     *
     * Default configuration:
     * - season length = 20 days
     * - meteorite event occurs at end of day 15
     * - watering can capacity = 10 uses
     * - excavation limit = 5 tiles per day
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
     * Initializes the game data and prepares the playing field.
     *
     * Loads:
     * - plant definitions from JSON
     * - fertilizer definitions from JSON
     * - field map layout from JSON
     * - high score records
     *
     * Must be called before any gameplay actions occur.
     *
     * @param playerName the name entered by the player
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
     * Plants a seed on the selected tile.
     *
     * Requirements:
     * - tile must be within bounds
     * - tile must not already contain a plant
     * - tile must not be a meteorite tile
     * - player must afford the seed cost
     *
     * @param plantIndex index within the affordable plant list
     * @param row row index of the target tile
     * @param col column index of the target tile
     * @return result message describing the action outcome
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

                template.getName(),
                template.getSeedPrice(),
                template.getYield(),
                template.getPreferredSoil(),
                template.getStages(),
                template.getLowCropName(),
                template.getLowCropPrice(),
                template.getHighCropName(),
                template.getHighCropPrice(),
                template.isHighCropRoot()

        );

        soil.setPlant(newPlant);

        player.deductSavings(template.getSeedPrice());

        return template.getName()
                + " planted at "
                + positionToString(row, col)
                + "!";
    }

    /**
     * Waters the plant on the specified tile.
     *
     * Requirements:
     * - watering can must have water remaining
     * - tile must contain a plant
     * - plant must not already be watered today
     *
     * @param row row index of the tile
     * @param col column index of the tile
     * @return result message describing the action outcome
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
            return "Plant at "
                    + positionToString(row, col)
                    + " is already watered.";
        }

        plant.water();

        wateringCan.useWater();

        return "Watered "
                + plant.getName()
                + " at "
                + positionToString(row, col)
                + " ["
                + plant.getCurrentStage().getDisplayName()
                + "]"
                + " | Water: "
                + wateringCan.getCurrentWaterLevel();
    }

    /**
     * Refills the watering can to maximum capacity.
     *
     * Cost: 100 gold
     *
     * @return result message describing the action outcome
     */
    public String refillWateringCan() {

        if (!player.canAfford(100)) {
            return "Not enough savings to refill! (costs 100g)";
        }

        player.deductSavings(100);

        wateringCan.refill();

        return "Watering can refilled! Savings: "
                + player.getSavings();
    }

    /**
     * Applies fertilizer to a tile.
     *
     * Requirements:
     * - player must afford fertilizer cost
     * - tile must not already contain active fertilizer
     *
     * @param fertIndex index within fertilizer list
     * @param row row index of tile
     * @param col column index of tile
     * @return result message describing the action outcome
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
            return "Soil at "
                    + positionToString(row, col)
                    + " already has fertilizer.";
        }

        Fertilizer newFert = new Fertilizer(

                selected.getName(),
                selected.getPrice(),
                selected.getRemainingDays()

        );

        soil.setFertilizer(newFert);

        player.deductSavings(selected.getPrice());

        return "Applied "
                + selected.getName()
                + " at "
                + positionToString(row, col)
                + "! Savings: "
                + player.getSavings();
    }

    /**
     * Removes or harvests the plant on a tile.
     *
     * If the plant is harvestable:
     * - crop is generated
     * - crop value is added to player savings
     *
     * Otherwise:
     * - plant is simply removed
     *
     * @param row row index of tile
     * @param col column index of tile
     * @return result message describing the action outcome
     */
    public String removeOrHarvest(int row, int col) {

        if (!field.isValidPosition(row, col)) {
            return "Invalid position.";
        }

        Soil soil = field.getSoil(row, col);

        if (!soil.hasPlant()) {
            return "No plant at "
                    + positionToString(row, col)
                    + ".";
        }

        Plant plant = soil.getPlant();

        String result;

        if (plant.canHarvest()) {

            Crop crop = plant.harvest();

            if (crop != null) {

                int value = crop.calculateTotalValue();

                player.addSavings(value);

                result =
                        "Harvested "
                        + crop.getQuantity()
                        + "x "
                        + crop.getName()
                        + " for "
                        + value
                        + "g!"
                        + " ["
                        + plant.getCurrentStage().getDisplayName()
                        + "]"
                        + " | Savings: "
                        + player.getSavings();

            }
            else {

                result = "Nothing to harvest.";

            }

        }
        else {

            result =
                    "Removed "
                    + plant.getName()
                    + " from "
                    + positionToString(row, col)
                    + " ["
                    + plant.getCurrentStage().getDisplayName()
                    + "].";

        }

        soil.setPlant(null);

        return result;
    }

    /**
     * Excavates a meteorite tile.
     *
     * Effects:
     * - costs 500 gold
     * - restores original soil type
     * - permanently fertilizes the tile
     *
     * Limit:
     * maximum of 5 excavations per day
     *
     * @param row row index of tile
     * @param col column index of tile
     * @return result message describing the action outcome
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
            return positionToString(row, col)
                    + " is not a meteorite tile.";
        }

        if (!player.canAfford(500)) {
            return "Not enough savings to excavate (costs 500g).";
        }

        player.deductSavings(500);

        soil.excavate();

        meteoriteExcavationsToday++;

        return "Excavated "
                + positionToString(row, col)
                + "! Restored to "
                + soil.getType()
                + " soil, permanently fertilized."
                + " Savings: "
                + player.getSavings();
    }

    /**
     * Advances the game to the next day.
     *
     * Processes:
     * - plant growth progression
     * - fertilizer duration reduction
     * - watering reset
     * - daily income bonus (+50g)
     * - meteorite event trigger (day 16)
     *
     * @return summary of daily changes
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

                    if (!(stage.isWateringHarmful()
                            && plant.isWatered())) {

                        plant.grow(stage.getBaseGrowth());

                    }

                }
                else if (stage.needsWatering()
                        && plant.isWatered()) {

                    int growStages = stage.getBaseGrowth();

                    boolean isSeedling =
                            stage.getStageType()
                                    == PlantStage.StageType.SEEDLING;

                    if (stage.acceptsBonuses()
                            && soil.getType().equals(
                                    plant.getPreferredSoil())) {

                        growStages += isSeedling ? 2 : 1;

                    }

                    if (stage.acceptsBonuses()
                            && soil.isFertilized()) {

                        growStages += isSeedling ? 2 : 1;

                    }

                    plant.grow(growStages);
                }

                plant.resetWatered();

                if (soil.hasFertilizer()) {

                    boolean isEnergizing =
                            plant.getCurrentStage()
                                    .getStageType()
                                    == PlantStage.StageType.ENERGIZING;

                    soil.getFertilizer().consumeDay();

                    if (isEnergizing) {
                        soil.getFertilizer().consumeDay();
                    }

                    if (!soil.getFertilizer().isActive()) {
                        soil.setFertilizer(null);
                    }
                }
            }
        }

        player.addSavings(50);

        currentDay++;

        meteoriteExcavationsToday = 0;

        log.append("Day ended. +50g | Savings: ")
           .append(player.getSavings());

        if (currentDay == 16 && !meteoriteHit) {

            meteoriteHit = true;

            for (int[] coord : meteoriteCoords) {

                Soil soil = field.getSoil(coord[0], coord[1]);

                soil.setPlant(null);

                soil.setMeteoriteTile(true);
            }

            log.append("\n★ METEORITE HIT! Tiles D3-G6 destroyed!")
               .append("\nExcavate them for 500g each (max 5/day).");
        }

        return log.toString();
    }

    /**
     * Ends the game and evaluates high score qualification.
     *
     * @return final summary including ranking table
     */
    public String endGame() {

        StringBuilder sb = new StringBuilder();

        sb.append("GAME OVER!\n");

        sb.append("Final Savings: ")
          .append(player.getSavings())
          .append("g\n\n");

        if (highScoreTable.qualifies(player.getSavings())) {

            highScoreTable.addEntry(
                    player.getName(),
                    player.getSavings()
            );

            JSONLoader.saveHighScores(
                    "HighScores.json",
                    highScoreTable
            );

            sb.append("You made the High Score Table!\n\n");
        }
        else {

            sb.append(
                    "You did not qualify for the High Score Table.\n\n"
            );
        }

        sb.append("=== HIGH SCORES ===\n");

        List<HighScoreEntry> entries =
                highScoreTable.getEntries();

        for (int i = 0; i < entries.size(); i++) {

            sb.append(i + 1)
              .append(". ")
              .append(entries.get(i).getName())
              .append(" - ")
              .append(entries.get(i).getSavings())
              .append("g\n");
        }

        return sb.toString();
    }

    /** @return the game field */
    public Field getField() { 
        return field; 
    }

    /** @return current day number */
    public int getCurrentDay() { 
        return currentDay; 
    }

    /** @return total days in season */
    public int getMaxDays() {
        return maxDays; 
    }

    /** @return player savings */
    public int getSavings() { return player.getSavings(); }

    /** @return player name */
    public String getPlayerName() { 
        return player.getName(); 
    }

    /** @return watering can water level */
    public int getWaterLevel() { 
        return wateringCan.getCurrentWaterLevel(); 
    }

    /** @return watering can capacity */
    public int getMaxWaterLevel() { 
        return wateringCan.getMaxWaterLevel(); 
    }

    /** @return true if watering can has water */
    public boolean canWater() { 
        return wateringCan.canWater(); 
    }

    /** @return true if meteorite event already triggered */
    public boolean isMeteoriteHit() { 
        return meteoriteHit; 
    }

    /** @return true if season ended */
    public boolean isGameOver() { 
        return currentDay > maxDays; 
    }

    /** @return true if player can afford watering refill */
    public boolean canAffordRefill() { 
        return player.canAfford(100); 
    }

    /** @return true if player can afford excavation */
    public boolean canAffordExcavation() { 
        return player.canAfford(500);
    }

    /** @return true if excavation limit reached */
    public boolean excavationLimitReached() {
        return meteoriteExcavationsToday >= 5;
    }

    /** @return all plant templates */
    public List<Plant> getAvailablePlants() {
        return availablePlants;
    }

    /** @return all fertilizer templates */
    public List<Fertilizer> getAvailableFertilizers() {
        return availableFertilizers;
    }

    /**
     * Filters plants based on affordability.
     *
     * @return list of plants the player can afford
     */
    public List<Plant> getAffordablePlants() {

        List<Plant> affordable = new ArrayList<>();

        for (Plant p : availablePlants) {

            if (player.canAfford(p.getSeedPrice())) {

                affordable.add(p);
            }
        }

        return affordable;
    }

    /**
     * Checks if any plant in the field currently needs watering.
     *
     * @return true if at least one plant can be watered
     */
    public boolean hasWaterablePlant() {

        return field.hasWaterablePlant();
    }

    /**
     * Converts grid coordinates to display format (example: A5).
     *
     * @param row row index
     * @param col column index
     * @return formatted tile position string
     */
    private String positionToString(int row, int col) {

        return (char)('A' + row) + "" + col;
    }
}
