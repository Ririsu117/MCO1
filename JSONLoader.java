import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for loading and saving JSON data
 * used by the Verdant Sun Farming Simulator. Handles plants
 * (with stage sequences), fertilizers, map layout, and high scores.
 *
 * Uses a custom hand-written parser since no external libraries
 * are permitted. All extraction methods search within an isolated
 * block string so prefix-key collisions (e.g. "price" vs "crop_price")
 * are avoided by always requiring an exact key match bounded by
 * a quote character before and a colon after.
 */
public class JSONLoader {

    /**
     * Reads the contents of a file and returns it as a single string
     * with all whitespace trimmed per line.
     *
     * @param filename The name of the file to read.
     * @return The file contents as a single string.
     */
    public static String readFile(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + filename);
        }
        return content.toString();
    }

    /**
     * Extracts the value for a given key from a JSON block string.
     * Matches the key exactly (surrounded by quotes, followed by colon)
     * to prevent partial-key matches like "price" matching "crop_price".
     *
     * Handles both string values ("value") and bare values (123, true, false).
     *
     * @param block The JSON object content string (without outer braces).
     * @param key   The exact key to search for.
     * @return The extracted value string, or empty string if not found.
     */
    private static String extractValue(String block, String key) {
        // Require the key to be preceded by a quote so "price" won't
        // accidentally match inside "crop_price" or "low_crop_price".
        String search = "\"" + key + "\":";
        int idx = 0;
        while (idx < block.length()) {
            int keyIndex = block.indexOf(search, idx);
            if (keyIndex == -1) return "";

            // Make sure the character before the opening quote is not a word char,
            // so "low_crop_price" is not matched when searching for "price".
            // The char before the quote must be either start-of-string, comma,
            // whitespace, or opening brace.
            char before = (keyIndex > 0) ? block.charAt(keyIndex - 1) : '{';
            if (before == ',' || before == '{' || before == ' ' || before == '\t') {
                int valueStart = keyIndex + search.length();
                // Skip spaces
                while (valueStart < block.length()
                        && block.charAt(valueStart) == ' ') {
                    valueStart++;
                }
                if (valueStart >= block.length()) return "";

                if (block.charAt(valueStart) == '"') {
                    // String value
                    int strStart = valueStart + 1;
                    int strEnd = block.indexOf('"', strStart);
                    if (strEnd == -1) return "";
                    return block.substring(strStart, strEnd);
                } else {
                    // Bare value: number, true, false
                    int valueEnd = valueStart;
                    while (valueEnd < block.length()
                            && block.charAt(valueEnd) != ','
                            && block.charAt(valueEnd) != '}'
                            && block.charAt(valueEnd) != ']') {
                        valueEnd++;
                    }
                    return block.substring(valueStart, valueEnd).trim();
                }
            }
            idx = keyIndex + search.length();
        }
        return "";
    }

    /**
     * Extracts a JSON string array for a given key from a block.
     * For example: "stages": ["seedling","dormant"] returns {"seedling","dormant"}.
     *
     * @param block The JSON object content string.
     * @param key   The exact key whose array value to extract.
     * @return A String array of values, or empty array if not found.
     */
    private static String[] extractArray(String block, String key) {
        String search = "\"" + key + "\":";
        int keyIndex = block.indexOf(search);
        if (keyIndex == -1) return new String[0];

        int arrayStart = block.indexOf('[', keyIndex + search.length());
        int arrayEnd   = block.indexOf(']', arrayStart);
        if (arrayStart == -1 || arrayEnd == -1) return new String[0];

        String arrayContent = block.substring(arrayStart + 1, arrayEnd).trim();
        if (arrayContent.isEmpty()) return new String[0];

        String[] rawItems = arrayContent.split(",");
        String[] result = new String[rawItems.length];
        for (int i = 0; i < rawItems.length; i++) {
            result[i] = rawItems[i].replace("\"", "").trim();
        }
        return result;
    }

    /**
     * Finds the index of the closing brace matching the opening brace
     * at openIndex, tracking nesting depth.
     *
     * @param content   The full content string.
     * @param openIndex Index of the opening '{'.
     * @return Index of the matching '}', or -1 if not found.
     */
    private static int findClosingBrace(String content, int openIndex) {
        int depth = 0;
        for (int i = openIndex; i < content.length(); i++) {
            if (content.charAt(i) == '{') depth++;
            else if (content.charAt(i) == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Converts a stage name string into the corresponding PlantStage instance.
     *
     * @param stageName Stage name as stored in Plants.json.
     * @return The matching PlantStage subclass instance.
     */
    private static PlantStage parseStage(String stageName) {
        switch (stageName.toLowerCase().trim()) {
            case "seedling":        return new PlantStage.Seedling();
            case "dormant":         return new PlantStage.Dormant();
            case "energizing":      return new PlantStage.Energizing();
            case "low_productive":  return new PlantStage.LowProductive();
            case "high_productive": return new PlantStage.HighProductive();
            case "fully_mature":    return new PlantStage.FullyMature();
            default:
                System.out.println("Unknown stage: " + stageName + ", defaulting to Seedling.");
                return new PlantStage.Seedling();
        }
    }

    /**
     * Loads plant data from a JSON file into a list of Plant objects.
     * Each entry must include: name, price, yield, preferred_soil,
     * low_crop_name, low_crop_price, high_crop_name, high_crop_price,
     * high_crop_is_root, and stages (array).
     *
     * @param filename The JSON file containing plant data.
     * @return A list of Plant objects, empty if parsing fails.
     */
    public static List<Plant> loadPlants(String filename) {
        List<Plant> plants = new ArrayList<>();
        String content = readFile(filename);

        int i = 0;
        while (i < content.length()) {
            // Find the next top-level key (plant key like "turnip")
            int keyStart = content.indexOf('"', i);
            if (keyStart == -1) break;
            int keyEnd = content.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;
            int colonIndex = content.indexOf(':', keyEnd);
            if (colonIndex == -1) break;
            int braceOpen = content.indexOf('{', colonIndex);
            if (braceOpen == -1) break;
            int braceClose = findClosingBrace(content, braceOpen);
            if (braceClose == -1) break;

            // block is the content inside the plant's { }
            String block = content.substring(braceOpen + 1, braceClose);

            String name             = extractValue(block, "name");
            String priceStr         = extractValue(block, "price");
            String yieldStr         = extractValue(block, "yield");
            String prefSoil         = extractValue(block, "preferred_soil");
            String lowCropName      = extractValue(block, "low_crop_name");
            String lowCropPriceStr  = extractValue(block, "low_crop_price");
            String highCropName     = extractValue(block, "high_crop_name");
            String highCropPriceStr = extractValue(block, "high_crop_price");
            String highCropIsRoot   = extractValue(block, "high_crop_is_root");
            String[] stageNames     = extractArray(block, "stages");

            if (!name.isEmpty() && !priceStr.isEmpty() && stageNames.length > 0) {
                try {
                    int price     = Integer.parseInt(priceStr.trim());
                    int yield     = Integer.parseInt(yieldStr.trim());
                    int lowPrice  = Integer.parseInt(lowCropPriceStr.trim());
                    int highPrice = Integer.parseInt(highCropPriceStr.trim());
                    boolean isRoot = Boolean.parseBoolean(highCropIsRoot.trim());

                    PlantStage[] stages = new PlantStage[stageNames.length];
                    for (int s = 0; s < stageNames.length; s++) {
                        stages[s] = parseStage(stageNames[s]);
                    }

                    plants.add(new Plant(
                        name, price, yield, prefSoil, stages,
                        lowCropName, lowPrice,
                        highCropName, highPrice, isRoot
                    ));
                    System.out.println("Loaded plant: " + name);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping malformed plant entry: " + name
                        + " | Error: " + e.getMessage());
                }
            }

            i = braceClose + 1;
        }

        System.out.println("Total plants loaded: " + plants.size());
        return plants;
    }

    /**
     * Loads fertilizer data from a JSON file into a list of Fertilizer objects.
     *
     * @param filename The JSON file containing fertilizer data.
     * @return A list of Fertilizer objects.
     */
    public static List<Fertilizer> loadFertilizers(String filename) {
        List<Fertilizer> fertilizers = new ArrayList<>();
        String content = readFile(filename);

        int i = 0;
        while (i < content.length()) {
            int keyStart = content.indexOf('"', i);
            if (keyStart == -1) break;
            int keyEnd = content.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;
            int colonIndex = content.indexOf(':', keyEnd);
            if (colonIndex == -1) break;
            int braceOpen = content.indexOf('{', colonIndex);
            if (braceOpen == -1) break;
            int braceClose = findClosingBrace(content, braceOpen);
            if (braceClose == -1) break;

            String block    = content.substring(braceOpen + 1, braceClose);
            String name     = extractValue(block, "name");
            String priceStr = extractValue(block, "price");
            String daysStr  = extractValue(block, "days");

            if (!name.isEmpty() && !priceStr.isEmpty()) {
                try {
                    int price = Integer.parseInt(priceStr.trim());
                    int days  = Integer.parseInt(daysStr.trim());
                    fertilizers.add(new Fertilizer(name, price, days));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping malformed fertilizer entry.");
                }
            }

            i = braceClose + 1;
        }

        return fertilizers;
    }

    /**
     * Loads the map layout from a JSON file into a 10x10 String grid.
     *
     * @param filename The JSON file containing map data.
     * @return A 2D String array of soil types.
     */
    public static String[][] loadMap(String filename) {
        String[][] map = new String[10][10];
        String content = readFile(filename);

        int mapKeyIndex = content.indexOf("\"map\"");
        if (mapKeyIndex == -1) {
            System.out.println("Error: 'map' key not found in " + filename);
            return map;
        }

        int arrayStart = content.indexOf('[', mapKeyIndex) + 1;
        int arrayEnd   = content.lastIndexOf(']') - 1;
        String mapContent = content.substring(arrayStart, arrayEnd);
        String[] rows = mapContent.split("],\\s*\\[");

        for (int r = 0; r < rows.length && r < 10; r++) {
            String rowClean = rows[r].replace("[", "").replace("]", "");
            String[] cells = rowClean.split(",");
            for (int c = 0; c < cells.length && c < 10; c++) {
                String symbol = cells[c].replace("\"", "").trim();
                if (symbol.equals("l"))      map[r][c] = "loam";
                else if (symbol.equals("s")) map[r][c] = "sand";
                else                         map[r][c] = "gravel";
            }
        }

        return map;
    }

    /**
     * Loads high score entries from a JSON file into the provided table.
     *
     * @param filename The JSON file containing high score data.
     * @param table    The HighScoreTable to populate.
     */
    public static void loadHighScores(String filename, HighScoreTable table) {
        String content = readFile(filename);

        int i = 0;
        while (i < content.length()) {
            int keyStart = content.indexOf('"', i);
            if (keyStart == -1) break;
            int keyEnd = content.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;
            int colonIndex = content.indexOf(':', keyEnd);
            if (colonIndex == -1) break;
            int braceOpen = content.indexOf('{', colonIndex);
            if (braceOpen == -1) break;
            int braceClose = findClosingBrace(content, braceOpen);
            if (braceClose == -1) break;

            String block      = content.substring(braceOpen + 1, braceClose);
            String name       = extractValue(block, "name");
            String savingsStr = extractValue(block, "savings");

            if (!name.isEmpty() && !savingsStr.isEmpty()) {
                try {
                    int savings = Integer.parseInt(savingsStr.trim());
                    table.addEntry(name, savings);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping malformed high score entry.");
                }
            }

            i = braceClose + 1;
        }
    }

    /**
     * Saves the current high score table to a JSON file.
     *
     * @param filename The output file path.
     * @param table    The HighScoreTable to save.
     */
    public static void saveHighScores(String filename, HighScoreTable table) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("{\n");
            List<HighScoreEntry> entries = table.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                HighScoreEntry entry = entries.get(i);
                writer.write("  \"" + (i + 1) + "\": {\n");
                writer.write("    \"name\": \"" + entry.getName() + "\",\n");
                writer.write("    \"savings\": " + entry.getSavings() + "\n");
                writer.write("  }");
                if (i < entries.size() - 1) writer.write(",");
                writer.write("\n");
            }
            writer.write("}\n");
        } catch (Exception e) {
            System.out.println("Error writing file: " + filename);
        }
    }
}