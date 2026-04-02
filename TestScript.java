public class TestScript {
    
    public static void main(String[] args) {
        System.out.println("VERDANT SUN - TEST CASES\n");
        
        testPlant();
        testPlayer();
        testWateringCan();
        testFertilizer();
        testSoil();
        testField();
        testHighScoreEntry();
        testHighScoreTable();
        
        System.out.println("ALL TESTS COMPLETED");
    }
    
    //PLANT TESTS
    private static void testPlant() {
        System.out.println(">>> PLANT CLASS TESTS\n");
        
        Plant p1 = new Plant("Turnip", 10, 2, 3, "loam", 6);
        System.out.println("Test 1 - isMature (new plant, growth=0):");
        System.out.println("Expected: false, Actual: " + p1.isMature());
        
        Plant p2 = new Plant("Wheat", 15, 4, 5, "loam", 5);
        p2.grow(5);
        System.out.println("Test 2 - isMature (at max growth):");
        System.out.println("Expected: true, Actual: " + p2.isMature());
        
        Plant p3 = new Plant("Turnip", 10, 2, 5, "loam", 6);
        p3.grow(2);
        System.out.println("Test 3 - grow (2 stages):");
        System.out.println("Expected: 2, Actual: " + p3.getCurrentGrowth());
        
        Plant p4 = new Plant("Turnip", 10, 2, 3, "loam", 6);
        p4.water();
        System.out.println("Test 4 - water (sets flag):");
        System.out.println("Expected: true, Actual: " + p4.isWatered());
        
        Plant p5 = new Plant("Turnip", 10, 2, 3, "loam", 6);
        System.out.println("Test 5 - calculateHarvestValue:");
        System.out.println("Expected: 12, Actual: " + p5.calculateHarvestValue());
        
        System.out.println();
    }
    
    //PLAYER TESTS
    private static void testPlayer() {
        System.out.println(">>> PLAYER CLASS TESTS\n");
        
        Player player1 = new Player("Test1");
        System.out.println("Test 1 - canAfford (exact amount 1000):");
        System.out.println("Expected: true, Actual: " + player1.canAfford(1000));
        
        Player player2 = new Player("Test2");
        System.out.println("Test 2 - canAfford (more than savings 1500):");
        System.out.println("Expected: false, Actual: " + player2.canAfford(1500));
        
        Player player3 = new Player("Test3");
        player3.deductSavings(100);
        System.out.println("Test 3 - deductSavings (100):");
        System.out.println("Expected: 900, Actual: " + player3.getSavings());
        
        Player player4 = new Player("Test4");
        player4.deductSavings(1500);
        System.out.println("Test 4 - deductSavings (cannot go negative):");
        System.out.println("Expected: 1000, Actual: " + player4.getSavings());
        
        Player player5 = new Player("Test5");
        player5.addSavings(500);
        System.out.println("Test 5 - addSavings (500):");
        System.out.println("Expected: 1500, Actual: " + player5.getSavings());
        
        System.out.println();
    }
    
    //WATERING CAN TESTS
    private static void testWateringCan() {
        System.out.println(">>> WATERING CAN CLASS TESTS\n");
        
        WateringCan can1 = new WateringCan(10);
        System.out.println("Test 1 - canWater (level=10):");
        System.out.println("Expected: true, Actual: " + can1.canWater());
        
        WateringCan can2 = new WateringCan(10);
        for(int i = 0; i < 10; i++) can2.useWater();
        System.out.println("Test 2 - canWater (level=0):");
        System.out.println("Expected: false, Actual: " + can2.canWater());
        
        WateringCan can3 = new WateringCan(10);
        can3.useWater();
        System.out.println("Test 3 - useWater (decreases by 1):");
        System.out.println("Expected: 9, Actual: " + can3.getCurrentWaterLevel());
        
        WateringCan can4 = new WateringCan(10);
        for(int i = 0; i < 15; i++) can4.useWater();
        System.out.println("Test 4 - useWater (cannot go below 0):");
        System.out.println("Expected: 0, Actual: " + can4.getCurrentWaterLevel());
        
        WateringCan can5 = new WateringCan(10);
        can5.useWater();
        can5.useWater();
        can5.refill();
        System.out.println("Test 5 - refill (restores to max):");
        System.out.println("Expected: 10, Actual: " + can5.getCurrentWaterLevel());
        
        System.out.println();
    }
    
    //FERTILIZER TESTS
    private static void testFertilizer() {
        System.out.println(">>> FERTILIZER CLASS TESTS\n");
        
        Fertilizer f1 = new Fertilizer("Quick", 100, 3);
        f1.consumeDay();
        System.out.println("Test 1 - consumeDay (3->2):");
        System.out.println("Expected: 2, Actual: " + f1.getRemainingDays());
        
        Fertilizer f2 = new Fertilizer("Quick", 100, 0);
        f2.consumeDay();
        System.out.println("Test 2 - consumeDay (stays at 0):");
        System.out.println("Expected: 0, Actual: " + f2.getRemainingDays());
        
        Fertilizer f3 = new Fertilizer("Quick", 100, 1);
        System.out.println("Test 3 - isActive (days=1):");
        System.out.println("Expected: true, Actual: " + f3.isActive());
        
        Fertilizer f4 = new Fertilizer("Quick", 100, 0);
        System.out.println("Test 4 - isActive (days=0):");
        System.out.println("Expected: false, Actual: " + f4.isActive());
        
        Fertilizer f5 = new Fertilizer("Premium", 200, 6);
        System.out.println("Test 5 - getName:");
        System.out.println("Expected: Premium, Actual: " + f5.getName());
        
        System.out.println();
    }
    
    //SOIL TESTS
    private static void testSoil() {
        System.out.println(">>> SOIL CLASS TESTS\n");
        
        Soil soil1 = new Soil("loam");
        System.out.println("Test 1 - hasPlant (no plant):");
        System.out.println("Expected: false, Actual: " + soil1.hasPlant());
        
        Soil soil2 = new Soil("loam");
        soil2.setPlant(new Plant("Turnip", 10, 2, 3, "loam", 6));
        System.out.println("Test 2 - hasPlant (with plant):");
        System.out.println("Expected: true, Actual: " + soil2.hasPlant());
        
        Soil soil3 = new Soil("loam");
        soil3.setFertilizer(new Fertilizer("Quick", 100, 2));
        System.out.println("Test 3 - hasFertilizer (with fertilizer):");
        System.out.println("Expected: true, Actual: " + soil3.hasFertilizer());
        
        Soil soil4 = new Soil("loam");
        soil4.setMeteoriteTile(true);
        soil4.excavate();
        System.out.println("Test 4 - excavate (removes meteorite):");
        System.out.println("Expected: false, Actual: " + soil4.isMeteoriteTile());
        
        Soil soil5 = new Soil("sand");
        System.out.println("Test 5 - getType (sand):");
        System.out.println("Expected: sand, Actual: " + soil5.getType());
        
        System.out.println();
    }
    
    //FIELD TESTS
    private static void testField() {
        System.out.println(">>> FIELD CLASS TESTS\n");
        
        Field field = new Field();
        String[][] map = new String[10][10];
        for(int i=0; i<10; i++) 
            for(int j=0; j<10; j++) 
                map[i][j] = "loam";
        field.initializeGrid(map);
        
        System.out.println("Test 1 - isValidPosition (0,0):");
        System.out.println("Expected: true, Actual: " + field.isValidPosition(0, 0));
        
        System.out.println("Test 2 - isValidPosition (9,9):");
        System.out.println("Expected: true, Actual: " + field.isValidPosition(9, 9));
        
        System.out.println("Test 3 - isValidPosition (10,5):");
        System.out.println("Expected: false, Actual: " + field.isValidPosition(10, 5));
        
        Soil soil = field.getSoil(0, 0);
        System.out.println("Test 4 - getSoil (0,0):");
        System.out.println("Expected: loam, Actual: " + soil.getType());
        
        System.out.println("Test 5 - hasWaterablePlant (no plants):");
        System.out.println("Expected: false, Actual: " + field.hasWaterablePlant());
        
        System.out.println();
    }
    
    //HIGH SCORE ENTRY TESTS
    private static void testHighScoreEntry() {
        System.out.println(">>> HIGH SCORE ENTRY CLASS TESTS\n");
        
        HighScoreEntry entry1 = new HighScoreEntry("Stephanie", 7000);
        System.out.println("Test 1 - getName:");
        System.out.println("Expected: Stephanie, Actual: " + entry1.getName());
        
        HighScoreEntry entry2 = new HighScoreEntry("John Doe", 5000);
        System.out.println("Test 2 - getName (with spaces):");
        System.out.println("Expected: John Doe, Actual: " + entry2.getName());
        
        System.out.println("Test 3 - getSavings:");
        System.out.println("Expected: 7000, Actual: " + entry1.getSavings());
        
        HighScoreEntry entry3 = new HighScoreEntry("Test", 0);
        System.out.println("Test 4 - getSavings (zero):");
        System.out.println("Expected: 0, Actual: " + entry3.getSavings());
        
        HighScoreEntry entry4 = new HighScoreEntry("Dan", 6700);
        System.out.println("Test 5 - getSavings (6700):");
        System.out.println("Expected: 6700, Actual: " + entry4.getSavings());
        
        System.out.println();
    }
    
    //HIGH SCORE TABLE TESTS
    private static void testHighScoreTable() {
        System.out.println(">>> HIGH SCORE TABLE CLASS TESTS\n");
        
        HighScoreTable table1 = new HighScoreTable();
        System.out.println("Test 1 - qualifies (empty table):");
        System.out.println("Expected: true, Actual: " + table1.qualifies(100));
        
        HighScoreTable table2 = new HighScoreTable();
        table2.addEntry("Player1", 7000);
        table2.addEntry("Player2", 6700);
        table2.addEntry("Player3", 2100);
        System.out.println("Test 2 - qualifies (higher than lowest):");
        System.out.println("Expected: true, Actual: " + table2.qualifies(3000));
        
        System.out.println("Test 3 - qualifies (lower than lowest):");
        System.out.println("Expected: false, Actual: " + table2.qualifies(1000));
        
        HighScoreTable table3 = new HighScoreTable();
        table3.addEntry("P1", 9000);
        table3.addEntry("P2", 8500);
        table3.addEntry("P3", 8000);
        table3.addEntry("P4", 7500);
        table3.addEntry("P5", 7000);
        table3.addEntry("P6", 6500);
        table3.addEntry("P7", 6000);
        table3.addEntry("P8", 5500);
        table3.addEntry("P9", 5000);
        table3.addEntry("P10", 4500);
        System.out.println("Test 3 - qualifies (lower than lowest if table is FULL):");
        System.out.println("Expected: false, Actual: " + table3.qualifies(1000));
        
        HighScoreTable table4 = new HighScoreTable();
        table4.addEntry("Player1", 7000);
        table4.addEntry("Player2", 2100);
        table4.addEntry("Player3", 6500);
        System.out.println("Test 5 - addEntry (sort order):");
        System.out.println("Expected: 7000, Actual: " + table4.getEntries().get(0).getSavings());
        
        System.out.println();
    }
}