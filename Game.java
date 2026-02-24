import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    private Scanner scanner;

    public Game() {
        this.player = null;
        this.field = new Field();
        this.wateringCan = new WateringCan(10);
        this.highScoreTable = new HighScoreTable();
        this.availablePlants = new ArrayList<>();
        this.availableFertilizers = new ArrayList<>();
        this.currentDay = 1;
        this.maxDays = 15;
        this.meteoriteHit = false;
        this.meteoriteCoords = new int[][] {
            {3,3},{3,4},{3,5},{3,6},
            {4,3},{4,4},{4,5},{4,6},
            {5,3},{5,4},{5,5},{5,6},
            {6,3},{6,4},{6,5},{6,6}
        };
        this.meteoriteExcavationsToday = 0;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Verdant Sun Farming Simulator!");
        System.out.println("Enter your name: ");
        String name = scanner.nextLine();
        player = new Player(name);
        availablePlants = JSONLoader.loadPlants("Plants.json");
        availableFertilizers = JSONLoader.loadFertilizers("Fertilizers.json");
        String[][] mapLayout = JSONLoader.loadMap("Map.json");
        field.initializeGrid(mapLayout);
        JSONLoader.loadHighScores("HighScores.json", highScoreTable);
        while (currentDay <= maxDays) {
            displayDayInfo();
            field.display();
            showMainMenu();
        }
        endGame();
    }

    private void displayDayInfo() {
        System.out.println("=============================");
        System.out.println("Day: " + currentDay + " / " + maxDays);
        System.out.println("Savings: " + player.getSavings());
        System.out.println("Water Level: " + wateringCan.getCurrentWaterLevel() + " / " + wateringCan.getMaxWaterLevel());
        System.out.println("=============================");
    }

    private void showMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Plant a seed");
        if (field.hasWaterablePlant()) {
            System.out.println("2. Water a plant");
        }
        if (player.canAfford(100)) {
            System.out.println("3. Refill watering can");
        }
        System.out.println("4. Apply fertilizer");
        System.out.println("5. Remove/Harvest a plant");
        if (meteoriteHit) {
            System.out.println("6. Excavate meteorite");
        }
        System.out.println("7. Next day");
        System.out.println("Enter your choice: ");
        String choice = scanner.nextLine();
        handleMainMenuChoice(choice);
    }

    private void handleMainMenuChoice(String choice) {
        if (choice.equals("1")) {
            plantSeed();
        } else if (choice.equals("2")) {
            waterPlant();
        } else if (choice.equals("3")) {
            refillWateringCan();
        } else if (choice.equals("4")) {
            applyFertilizer();
        } else if (choice.equals("5")) {
            removeOrHarvest();
        } else if (choice.equals("6")) {
            if (meteoriteHit) {
                excavateMeteorite();
            } else {
                System.out.println("Invalid choice.");
            }
        } else if (choice.equals("7")) {
            nextDay();
        } else {
            System.out.println("Invalid choice. Please try again.");
        }
    }

    private void plantSeed() {
        System.out.println("\n--- PLANT A SEED ---");
        List<Plant> affordablePlants = new ArrayList<>();
        for (Plant plant : availablePlants) {
            if (player.canAfford(plant.getSeedPrice())) {
                affordablePlants.add(plant);
            }
        }
        if (affordablePlants.isEmpty()) {
            System.out.println("You cannot afford any plants!");
            return;
        }
        for (int i = 0; i < affordablePlants.size(); i++) {
            Plant plant = affordablePlants.get(i);
            System.out.println((i + 1) + ". " + plant.getName() +
                " | Price: " + plant.getSeedPrice() +
                " | Preferred Soil: " + plant.getPreferredSoil() +
                " | Max Growth: " + plant.getMaxGrowth() +
                " | Crop Value: " + plant.calculateHarvestValue());
        }
        System.out.println("0. Cancel");
        System.out.println("Choose a plant: ");
        String choice = scanner.nextLine();
        if (choice.equals("0")) return;
        int plantIndex;
        try {
            plantIndex = Integer.parseInt(choice) - 1;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return;
        }
        if (plantIndex < 0 || plantIndex >= affordablePlants.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        Plant selectedPlant = affordablePlants.get(plantIndex);
        System.out.println("Enter row and column to plant (e.g. 3 4): ");
        System.out.println("Or type 0 to cancel.");
        String coords = scanner.nextLine();
        if (coords.equals("0")) return;
        int row, col;
        try {
            String[] parts = coords.split(" ");
            row = Integer.parseInt(parts[0]);
            col = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return;
        }
        if (!field.isValidPosition(row, col)) {
            System.out.println("Invalid position.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (soil.hasPlant()) {
            System.out.println("There is already a plant here!");
            return;
        }
        if (soil.isMeteoriteTile()) {
            System.out.println("You cannot plant on a meteorite tile!");
            return;
        }
        Plant newPlant = new Plant(
            selectedPlant.getName(),
            selectedPlant.getSeedPrice(),
            selectedPlant.getYield(),
            selectedPlant.getMaxGrowth(),
            selectedPlant.getPreferredSoil(),
            selectedPlant.getCropPrice()
        );
        soil.setPlant(newPlant);
        player.deductSavings(selectedPlant.getSeedPrice());
        System.out.println(selectedPlant.getName() + " planted at row " + row + ", col " + col + "!");
        field.display();
    }

    private void waterPlant() {
        if (!wateringCan.canWater()) {
            System.out.println("Your watering can is empty! Please refill it first.");
            return;
        }
        System.out.println("\n--- WATER A PLANT ---");
        System.out.println("1. Water a single tile");
        System.out.println("2. Water multiple tiles");
        System.out.println("0. Cancel");
        System.out.println("Enter your choice: ");
        String choice = scanner.nextLine();
        if (choice.equals("0")) return;
        if (choice.equals("1")) {
            System.out.println("Enter row and column (e.g. 3 4): ");
            String coords = scanner.nextLine();
            int row, col;
            try {
                String[] parts = coords.split(" ");
                row = Integer.parseInt(parts[0]);
                col = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                System.out.println("Invalid input.");
                return;
            }
            waterTile(row, col);
            field.display();
        } else if (choice.equals("2")) {
            System.out.println("Enter tiles as row col pairs separated by commas (e.g. 3 4,5 6,1 2): ");
            String input = scanner.nextLine();
            String[] tiles = input.split(",");
            for (String tile : tiles) {
                if (!wateringCan.canWater()) {
                    System.out.println("Watering can is empty!");
                    break;
                }
                try {
                    String[] parts = tile.trim().split(" ");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    waterTile(row, col);
                } catch (Exception e) {
                    System.out.println("Invalid input, skipping.");
                }
            }
            field.display();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void waterTile(int row, int col) {
        if (!field.isValidPosition(row, col)) {
            System.out.println("Invalid position (" + row + ", " + col + "), skipping.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (!soil.hasPlant()) {
            System.out.println("No plant at (" + row + ", " + col + "), skipping.");
            return;
        }
        if (soil.getPlant().isWatered()) {
            System.out.println("Plant at (" + row + ", " + col + ") is already watered, skipping.");
            return;
        }
        soil.getPlant().water();
        wateringCan.useWater();
        System.out.println("Watered plant at (" + row + ", " + col + ")! Water level: " + wateringCan.getCurrentWaterLevel());
    }

    private void refillWateringCan() {
        player.deductSavings(100);
        wateringCan.refill();
        System.out.println("Watering can refilled! Savings: " + player.getSavings());
    }

    private void applyFertilizer() {
        System.out.println("\n--- APPLY FERTILIZER ---");
        for (int i = 0; i < availableFertilizers.size(); i++) {
            Fertilizer f = availableFertilizers.get(i);
            if (player.canAfford(f.getPrice())) {
                System.out.println((i + 1) + ". " + f.getName() +
                    " | Price: " + f.getPrice() +
                    " | Days: " + f.getRemainingDays());
            }
        }
        System.out.println("0. Cancel");
        System.out.println("Choose a fertilizer: ");
        String choice = scanner.nextLine();
        if (choice.equals("0")) return;
        int fertIndex;
        try {
            fertIndex = Integer.parseInt(choice) - 1;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return;
        }
        if (fertIndex < 0 || fertIndex >= availableFertilizers.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        Fertilizer selectedFert = availableFertilizers.get(fertIndex);
        if (!player.canAfford(selectedFert.getPrice())) {
            System.out.println("You cannot afford this fertilizer!");
            return;
        }
        System.out.println("1. Apply to a single tile");
        System.out.println("2. Apply to multiple tiles");
        System.out.println("0. Cancel");
        String tileChoice = scanner.nextLine();
        if (tileChoice.equals("0")) return;
        if (tileChoice.equals("1")) {
            System.out.println("Enter row and column (e.g. 3 4): ");
            String coords = scanner.nextLine();
            int row, col;
            try {
                String[] parts = coords.split(" ");
                row = Integer.parseInt(parts[0]);
                col = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                System.out.println("Invalid input.");
                return;
            }
            applyFertilizerToTile(row, col, selectedFert);
            field.display();
        } else if (tileChoice.equals("2")) {
            System.out.println("Enter tiles as row col pairs separated by commas (e.g. 3 4,5 6,1 2): ");
            String input = scanner.nextLine();
            String[] tiles = input.split(",");
            for (String tile : tiles) {
                if (!player.canAfford(selectedFert.getPrice())) {
                    System.out.println("Not enough savings to continue fertilizing!");
                    break;
                }
                try {
                    String[] parts = tile.trim().split(" ");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    applyFertilizerToTile(row, col, selectedFert);
                } catch (Exception e) {
                    System.out.println("Invalid input, skipping.");
                }
            }
            field.display();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void applyFertilizerToTile(int row, int col, Fertilizer selectedFert) {
        if (!field.isValidPosition(row, col)) {
            System.out.println("Invalid position (" + row + ", " + col + "), skipping.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (soil.hasFertilizer()) {
            System.out.println("Soil at (" + row + ", " + col + ") already has fertilizer, skipping.");
            return;
        }
        Fertilizer newFert = new Fertilizer(
            selectedFert.getName(),
            selectedFert.getPrice(),
            selectedFert.getRemainingDays()
        );
        soil.setFertilizer(newFert);
        player.deductSavings(selectedFert.getPrice());
        System.out.println("Applied " + selectedFert.getName() + " at (" + row + ", " + col + ")! Savings: " + player.getSavings());
    }

    private void removeOrHarvest() {
        System.out.println("\n--- REMOVE/HARVEST A PLANT ---");
        System.out.println("1. Single tile");
        System.out.println("2. Multiple tiles");
        System.out.println("0. Cancel");
        String choice = scanner.nextLine();
        if (choice.equals("0")) return;
        if (choice.equals("1")) {
            System.out.println("Enter row and column (e.g. 3 4): ");
            String coords = scanner.nextLine();
            int row, col;
            try {
                String[] parts = coords.split(" ");
                row = Integer.parseInt(parts[0]);
                col = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                System.out.println("Invalid input.");
                return;
            }
            removeOrHarvestTile(row, col);
            field.display();
        } else if (choice.equals("2")) {
            System.out.println("Enter tiles as row col pairs separated by commas (e.g. 3 4,5 6,1 2): ");
            String input = scanner.nextLine();
            String[] tiles = input.split(",");
            for (String tile : tiles) {
                try {
                    String[] parts = tile.trim().split(" ");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    removeOrHarvestTile(row, col);
                } catch (Exception e) {
                    System.out.println("Invalid input, skipping.");
                }
            }
            field.display();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void removeOrHarvestTile(int row, int col) {
        if (!field.isValidPosition(row, col)) {
            System.out.println("Invalid position (" + row + ", " + col + "), skipping.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (!soil.hasPlant()) {
            System.out.println("No plant at (" + row + ", " + col + "), skipping.");
            return;
        }
        Plant plant = soil.getPlant();
        if (plant.isMature()) {
            int harvestValue = plant.calculateHarvestValue();
            player.addSavings(harvestValue);
            System.out.println("Harvested " + plant.getName() + " for " + harvestValue + "! Savings: " + player.getSavings());
        } else {
            System.out.println("Removed " + plant.getName() + " from (" + row + ", " + col + ").");
        }
        soil.setPlant(null);
    }

    private void excavateMeteorite() {
        if (meteoriteExcavationsToday >= 5) {
            System.out.println("You have already excavated 5 tiles today!");
            return;
        }
        System.out.println("\n--- EXCAVATE METEORITE ---");
        System.out.println("1. Single tile");
        System.out.println("2. Multiple tiles");
        System.out.println("0. Cancel");
        String choice = scanner.nextLine();
        if (choice.equals("0")) return;
        if (choice.equals("1")) {
            System.out.println("Enter row and column (e.g. 3 4): ");
            String coords = scanner.nextLine();
            int row, col;
            try {
                String[] parts = coords.split(" ");
                row = Integer.parseInt(parts[0]);
                col = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                System.out.println("Invalid input.");
                return;
            }
            excavateTile(row, col);
            field.display();
        } else if (choice.equals("2")) {
            System.out.println("Enter tiles as row col pairs separated by commas (e.g. 3 4,5 6,1 2): ");
            String input = scanner.nextLine();
            String[] tiles = input.split(",");
            for (String tile : tiles) {
                if (meteoriteExcavationsToday >= 5) {
                    System.out.println("Reached excavation limit of 5 tiles for today!");
                    break;
                }
                if (!player.canAfford(500)) {
                    System.out.println("Not enough savings to continue excavating!");
                    break;
                }
                try {
                    String[] parts = tile.trim().split(" ");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    excavateTile(row, col);
                } catch (Exception e) {
                    System.out.println("Invalid input, skipping.");
                }
            }
            field.display();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void excavateTile(int row, int col) {
        if (!field.isValidPosition(row, col)) {
            System.out.println("Invalid position (" + row + ", " + col + "), skipping.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (!soil.isMeteoriteTile()) {
            System.out.println("Tile at (" + row + ", " + col + ") is not a meteorite tile, skipping.");
            return;
        }
        if (!player.canAfford(500)) {
            System.out.println("Not enough savings to excavate!");
            return;
        }
        player.deductSavings(500);
        soil.excavate();
        meteoriteExcavationsToday++;
        System.out.println("Excavated tile at (" + row + ", " + col + ")! Savings: " + player.getSavings());
    }

    private void nextDay() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Soil soil = field.getSoil(row, col);
                if (soil.hasPlant() && soil.getPlant().isWatered()) {
                    int growStages = 1;
                    if (soil.getPlant().getPreferredSoil().equals(soil.getType())) {
                        growStages++;
                    }
                    if (soil.hasFertilizer() || soil.isExcavated()) {
                        growStages++;
                    }
                    soil.getPlant().grow(growStages);
                    soil.getPlant().resetWatered();
                    if (soil.hasFertilizer()) {
                        soil.getFertilizer().consumeDay();
                        if (!soil.getFertilizer().isActive()) {
                            soil.setFertilizer(null);
                        }
                    }
                } else if (soil.hasPlant()) {
                    soil.getPlant().resetWatered();
                }
            }
        }
        player.addSavings(50);
        currentDay++;
        meteoriteExcavationsToday = 0;
        if (currentDay == 8 && !meteoriteHit) {
            triggerMeteoriteEvent();
        }
        System.out.println("Day ended! Savings: " + player.getSavings());
    }

    private void triggerMeteoriteEvent() {
        meteoriteHit = true;
        System.out.println("\n*** A METEORITE HAS HIT THE FIELD! ***");
        for (int[] coord : meteoriteCoords) {
            Soil soil = field.getSoil(coord[0], coord[1]);
            if (soil.hasPlant()) {
                soil.setPlant(null);
            }
            soil.setMeteoriteTile(true);
        }
        System.out.println("Some tiles have been destroyed! You can now excavate them.");
        field.display();
    }

    private void endGame() {
        System.out.println("\n=============================");
        System.out.println("GAME OVER!");
        System.out.println("Final Savings: " + player.getSavings());
        System.out.println("=============================");
        if (highScoreTable.qualifies(player.getSavings())) {
            highScoreTable.addEntry(player.getName(), player.getSavings());
            JSONLoader.saveHighScores("HighScores.json", highScoreTable);
            System.out.println("You made it to the high score table!");
        } else {
            System.out.println("You did not qualify for the high score table.");
        }
        highScoreTable.display();
    }

}