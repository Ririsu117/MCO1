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
    
 // Helper method to parse grid position like "A5" into row and column
    private int[] parsePosition(String position) {
        if (position == null || position.length() < 2) {
            return null;
        }
        position = position.toUpperCase().trim();
        char rowChar = position.charAt(0);
        String colStr = position.substring(1);
        
        // Convert letter to row (A=0, B=1, ... J=9)
        int row = rowChar - 'A';
        
        // Parse column number (0-9)
        int col;
        try {
            col = Integer.parseInt(colStr);
        } catch (NumberFormatException e) {
            return null;
        }
        
        // Validate range
        if (row < 0 || row > 9 || col < 0 || col > 9) {
            return null;
        }
        
        return new int[]{row, col};
    }

    // Helper method to convert row/col to position string like "A5"
    private String positionToString(int row, int col) {
        char rowChar = (char)('A' + row);
        return rowChar + "" + col;
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
        System.out.println("Enter position to plant (e.g. A5, B3): ");
        System.out.println("Or type 0 to cancel.");
        String position = scanner.nextLine();
        if (position.equals("0")) return;
        int[] coords = parsePosition(position);
        if (coords == null) {
            System.out.println("Invalid position format. Use letter+number (e.g. A5)");
            return;
        }
        
        int row = coords[0];
        int col = coords[1];
        
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
        System.out.println(selectedPlant.getName() + " planted at " + positionToString(row, col) + "!");
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
        	System.out.println("Enter position (e.g. A5): ");
        	String position = scanner.nextLine();
        	int[] coords = parsePosition(position);
        	if (coords == null) {
        	    System.out.println("Invalid position format.");
        	    return;
        	}
        	
        	int row = coords[0];
        	int col = coords[1];
        	
            waterTile(row, col);
            field.display();
        } else if (choice.equals("2")) {
        	System.out.println("Enter positions separated by commas (e.g. A5,B3,C7): ");
        	String input = scanner.nextLine();
        	String[] positions = input.split(",");
        	for (String position : positions) {
        	    if (!wateringCan.canWater()) {
        	        System.out.println("Watering can is empty!");
        	        break;
        	    }
        	    int[] coords = parsePosition(position.trim());
        	    if (coords == null) {
        	        System.out.println("Invalid position " + position + ", skipping.");
        	        continue;
        	    }
        	    waterTile(coords[0], coords[1]);
        	}
            field.display();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void waterTile(int row, int col) {
        if (!field.isValidPosition(row, col)) {
        	System.out.println("Invalid position " + positionToString(row, col) + ", skipping.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (!soil.hasPlant()) {
        	System.out.println("No plant at " + positionToString(row, col) + ", skipping.");
            return;
        }
        if (soil.getPlant().isWatered()) {
        	System.out.println("Plant at " + positionToString(row, col) + " is already watered, skipping.");
            return;
        }
        soil.getPlant().water();
        wateringCan.useWater();
        System.out.println("Watered plant at " + positionToString(row, col) + "! Water level: " + wateringCan.getCurrentWaterLevel());
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
        	System.out.println("Enter position (e.g. A5): ");
        	String position = scanner.nextLine();
        	int[] coords = parsePosition(position);
        	if (coords == null) {
        	    System.out.println("Invalid position format.");
        	    return;
        	}
        	int row = coords[0];
        	int col = coords[1];
            applyFertilizerToTile(row, col, selectedFert);
            field.display();
        } else if (tileChoice.equals("2")) {
        	System.out.println("Enter positions separated by commas (e.g. A5,B3,C7): ");
        	String input = scanner.nextLine();
        	String[] positions = input.split(",");
        	for (String position : positions) {
        	    if (!player.canAfford(selectedFert.getPrice())) {
        	        System.out.println("Not enough savings to continue fertilizing!");
        	        break;
        	    }
        	    int[] coords = parsePosition(position.trim());
        	    if (coords == null) {
        	        System.out.println("Invalid position " + position + ", skipping.");
        	        continue;
        	    }
        	    applyFertilizerToTile(coords[0], coords[1], selectedFert);
        	}
            field.display();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void applyFertilizerToTile(int row, int col, Fertilizer selectedFert) {
        if (!field.isValidPosition(row, col)) {
        	System.out.println("Invalid position " + positionToString(row, col) + ", skipping.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (soil.hasFertilizer()) {
        	System.out.println("Soil at " + positionToString(row, col) + " already has fertilizer, skipping.");
            return;
        }
        Fertilizer newFert = new Fertilizer(
            selectedFert.getName(),
            selectedFert.getPrice(),
            selectedFert.getRemainingDays()
        );
        soil.setFertilizer(newFert);
        player.deductSavings(selectedFert.getPrice());
        System.out.println("Applied " + selectedFert.getName() + " at " + positionToString(row, col) + "! Savings: " + player.getSavings());
    }

    private void removeOrHarvest() {
        System.out.println("\n--- REMOVE/HARVEST A PLANT ---");
        System.out.println("1. Single tile");
        System.out.println("2. Multiple tiles");
        System.out.println("0. Cancel");
        String choice = scanner.nextLine();
        if (choice.equals("0")) return;
        if (choice.equals("1")) {
        	System.out.println("Enter position (e.g. A5): ");
        	String position = scanner.nextLine();
        	int[] coords = parsePosition(position);
        	if (coords == null) {
        	    System.out.println("Invalid position format.");
        	    return;
        	}
        	int row = coords[0];
        	int col = coords[1];
            removeOrHarvestTile(row, col);
            field.display();
        } else if (choice.equals("2")) {
        	System.out.println("Enter positions separated by commas (e.g. A5,B3,C7): ");
        	String input = scanner.nextLine();
        	String[] positions = input.split(",");
        	for (String position : positions) {
        	    int[] coords = parsePosition(position.trim());
        	    if (coords == null) {
        	        System.out.println("Invalid position " + position + ", skipping.");
        	        continue;
        	    }
        	    removeOrHarvestTile(coords[0], coords[1]);
        	}
        	field.display();
        } else {
        	System.out.println("Invalid choice.");
        }
    }
    private void removeOrHarvestTile(int row, int col) {
        if (!field.isValidPosition(row, col)) {
        	System.out.println("Invalid position " + positionToString(row, col) + ", skipping.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (!soil.hasPlant()) {
        	System.out.println("No plant at " + positionToString(row, col) + ", skipping.");
            return;
        }
        Plant plant = soil.getPlant();
        if (plant.isMature()) {
            int harvestValue = plant.calculateHarvestValue();
            player.addSavings(harvestValue);
            System.out.println("Harvested " + plant.getName() + " for " + harvestValue + "! Savings: " + player.getSavings());
        } else {
        	System.out.println("Removed " + plant.getName() + " from " + positionToString(row, col) + ".");
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
        	System.out.println("Enter position (e.g. D4): ");
        	String position = scanner.nextLine();
        	int[] coords = parsePosition(position);
        	if (coords == null) {
        	    System.out.println("Invalid position format.");
        	    return;
        	}
        	int row = coords[0];
        	int col = coords[1];
            excavateTile(row, col);
            field.display();
        } else if (choice.equals("2")) {
        	System.out.println("Enter positions separated by commas (e.g. D4,D5,E4): ");
        	String input = scanner.nextLine();
        	String[] positions = input.split(",");
        	for (String position : positions) {
        	    if (meteoriteExcavationsToday >= 5) {
        	        System.out.println("Reached excavation limit of 5 tiles for today!");
        	        break;
        	    }
        	    if (!player.canAfford(500)) {
        	        System.out.println("Not enough savings to continue excavating!");
        	        break;
        	    }
        	    int[] coords = parsePosition(position.trim());
        	    if (coords == null) {
        	        System.out.println("Invalid position " + position + ", skipping.");
        	        continue;
        	    }
        	    excavateTile(coords[0], coords[1]);
        	}
        	
            field.display();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void excavateTile(int row, int col) {
        if (!field.isValidPosition(row, col)) {
        	System.out.println("Invalid position " + positionToString(row, col) + ", skipping.");
            return;
        }
        Soil soil = field.getSoil(row, col);
        if (!soil.isMeteoriteTile()) {
        	System.out.println("Tile at " + positionToString(row, col) + " is not a meteorite tile, skipping.");
            return;
        }
        if (!player.canAfford(500)) {
            System.out.println("Not enough savings to excavate!");
            return;
        }
        player.deductSavings(500);
        soil.excavate();
        meteoriteExcavationsToday++;
        System.out.println("Excavated tile at " + positionToString(row, col) + "! Savings: " + player.getSavings());
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