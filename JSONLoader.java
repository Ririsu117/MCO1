import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class JSONLoader {

    public static String readFile(String filename) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading file: " + filename);
        }
        return content.toString();
    }

    public static List<Plant> loadPlants(String filename) {
        List<Plant> plants = new ArrayList<>();
        String content = readFile(filename);
        content = content.replace("{", "").replace("}", "");
        String[] plantEntries = content.split("\"[a-z]+\":\\{");
        for (String entry : plantEntries) {
            entry = entry.trim();
            if (entry.isEmpty()) continue;
            entry = entry.replaceAll(",$", "");
            String name = extractValue(entry, "name");
            int price = Integer.parseInt(extractValue(entry, "price"));
            int yield = Integer.parseInt(extractValue(entry, "yield"));
            int maxGrowth = Integer.parseInt(extractValue(entry, "max_growth"));
            String preferredSoil = extractValue(entry, "preferred_soil");
            int cropPrice = Integer.parseInt(extractValue(entry, "crop_price"));
            plants.add(new Plant(name, price, yield, maxGrowth, preferredSoil, cropPrice));
        }
        return plants;
    }

    public static List<Fertilizer> loadFertilizers(String filename) {
        List<Fertilizer> fertilizers = new ArrayList<>();
        String content = readFile(filename);
        content = content.replace("{", "").replace("}", "");
        String[] fertilizerEntries = content.split("\"[a-z]+\":\\{");
        for (String entry : fertilizerEntries) {
            entry = entry.trim();
            if (entry.isEmpty()) continue;
            entry = entry.replaceAll(",$", "");
            String name = extractValue(entry, "name");
            int price = Integer.parseInt(extractValue(entry, "price"));
            int days = Integer.parseInt(extractValue(entry, "days"));
            fertilizers.add(new Fertilizer(name, price, days));
        }
        return fertilizers;
    }

    public static String[][] loadMap(String filename) {
        String[][] map = new String[10][10];
        String content = readFile(filename);
        String mapSection = content.substring(content.indexOf("\"map\":[[") + 8, content.lastIndexOf("]]"));
        String[] rows = mapSection.split("],\\[");
        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].replace("[", "").replace("]", "").replace("\"", "").split(",");
            for (int j = 0; j < cols.length; j++) {
                String symbol = cols[j].trim();
                if (symbol.equals("l")) map[i][j] = "loam";
                else if (symbol.equals("s")) map[i][j] = "sand";
                else map[i][j] = "gravel";
            }
        }
        return map;
    }

    public static void loadHighScores(String filename, HighScoreTable table) {
        String content = readFile(filename);
        content = content.replace("{", "").replace("}", "");
        String[] entries = content.split("\"[0-9]+\":\\{");
        for (String entry : entries) {
            entry = entry.trim();
            if (entry.isEmpty()) continue;
            entry = entry.replaceAll(",$", "");
            String name = extractValue(entry, "name");
            int savings = Integer.parseInt(extractValue(entry, "savings"));
            table.addEntry(name, savings);
        }
    }

    public static void saveHighScores(String filename, HighScoreTable table) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write("{\n");
            
            List<HighScoreEntry> entries = table.getEntries();
            
            for (int i = 0; i < entries.size(); i++) {
                HighScoreEntry entry = entries.get(i);
                writer.write("  \"" + i + "\": {\n");
                writer.write("    \"name\": \"" + entry.getName() + "\",\n");
                writer.write("    \"savings\": " + entry.getSavings() + "\n");
                writer.write("  }");
                if (i < entries.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            
            writer.write("}\n");
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing file: " + filename);
        }
    }

    private static String extractValue(String entry, String key) {
        String search = "\"" + key + "\":";
        int start = entry.indexOf(search) + search.length();
        int end = entry.indexOf(",", start);
        if (end == -1) end = entry.length();
        String value = entry.substring(start, end).trim();
        value = value.replace("\"", "").replace("}", "").replace("{", "");
        return value.trim();
    }
}