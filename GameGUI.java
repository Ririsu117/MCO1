import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main GUI window for the Verdant Sun Farming Simulator (MCO2).
 * Built with Java Swing. Displays the field, player stats, action buttons,
 * and a log of recent actions.
 *
 * Layout:
 *   - Top bar:    Day / Savings / Water level labels
 *   - Center:     FieldPanel (10x10 colored tile grid)
 *   - Right panel: Action buttons + color legend
 *   - Bottom:     Scrollable action log
 *
 * The GUI uses a simple "pending action" state machine. When the player
 * clicks an action button, the GUI enters a mode where the next tile
 * click performs that action on the selected tile.
 */
public class GameGUI extends JFrame {

    // ── Core game logic ──────────────────────────────────────
    private Game game;
    private FieldPanel fieldPanel;

    // ── Top status bar labels ────────────────────────────────
    private JLabel lblDay;
    private JLabel lblSavings;
    private JLabel lblWater;

    // ── Action buttons ───────────────────────────────────────
    private JButton btnPlant;
    private JButton btnWater;
    private JButton btnRefill;
    private JButton btnFertilize;
    private JButton btnHarvest;
    private JButton btnExcavate;
    private JButton btnNextDay;

    // ── Action log ───────────────────────────────────────────
    private JTextArea logArea;

    // ── Pending action state ─────────────────────────────────
    /** Tracks which action is waiting for a tile click. */
    private enum PendingAction {
        NONE, PLANT, WATER, FERTILIZE, HARVEST, EXCAVATE
    }
    private PendingAction pendingAction = PendingAction.NONE;
    private int pendingPlantIndex  = -1;
    private int pendingFertIndex   = -1;

    /**
     * Constructs and shows the main game window.
     * Asks the player for their name before starting.
     */
    public GameGUI() {
        game = new Game();

        // Ask for player name via dialog before showing main window
        String name = JOptionPane.showInputDialog(
            null,
            "Enter your farmer name:",
            "Verdant Sun",
            JOptionPane.PLAIN_MESSAGE
        );
        if (name == null || name.trim().isEmpty()) {
            name = "Farmer";
        }
        game.initialize(name.trim());

        buildWindow();
        refresh();
        setVisible(true);
    }

    /**
     * Builds all Swing components and lays them out in the main window.
     */
    private void buildWindow() {
        setTitle("Verdant Sun Farming Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top: status bar
        JPanel statusPanel = buildStatusPanel();
        add(statusPanel, BorderLayout.NORTH);

        // Center: field grid
        fieldPanel = new FieldPanel(game.getField());
        fieldPanel.setTileClickListener(this::onTileClicked);
        fieldPanel.setPreferredSize(new Dimension(520, 520));
        add(fieldPanel, BorderLayout.CENTER);

        // Right: action buttons + legend
        JPanel rightPanel = buildRightPanel();
        add(rightPanel, BorderLayout.EAST);

        // Bottom: action log
        JPanel logPanel = buildLogPanel();
        add(logPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Builds the top status bar showing day, savings, and water level.
     *
     * @return The constructed status JPanel.
     */
    private JPanel buildStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        panel.setBackground(new Color(245, 240, 230));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        lblDay     = new JLabel();
        lblSavings = new JLabel();
        lblWater   = new JLabel();

        Font statusFont = new Font("SansSerif", Font.BOLD, 13);
        lblDay.setFont(statusFont);
        lblSavings.setFont(statusFont);
        lblWater.setFont(statusFont);

        panel.add(new JLabel("🌱 Verdant Sun"));
        panel.add(lblDay);
        panel.add(lblSavings);
        panel.add(lblWater);
        return panel;
    }

    /**
     * Builds the right-side panel containing action buttons and the legend.
     *
     * @return The constructed right JPanel.
     */
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        panel.setPreferredSize(new Dimension(170, 520));

        // Action buttons
        JLabel actionsLabel = new JLabel("ACTIONS");
        actionsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(actionsLabel);
        panel.add(Box.createVerticalStrut(6));

        btnPlant    = makeActionButton("🌱 Plant Seed");
        btnWater    = makeActionButton("💧 Water Plant");
        btnRefill   = makeActionButton("🪣 Refill Can");
        btnFertilize= makeActionButton("🧪 Fertilize");
        btnHarvest  = makeActionButton("🌾 Remove/Harvest");
        btnExcavate = makeActionButton("⛏ Excavate");
        btnNextDay  = makeActionButton("⏭ Next Day");

        btnNextDay.setBackground(new Color(180, 220, 180));

        btnPlant.addActionListener(e -> startPlantAction());
        btnWater.addActionListener(e -> startWaterAction());
        btnRefill.addActionListener(e -> doRefill());
        btnFertilize.addActionListener(e -> startFertilizeAction());
        btnHarvest.addActionListener(e -> startHarvestAction());
        btnExcavate.addActionListener(e -> startExcavateAction());
        btnNextDay.addActionListener(e -> doNextDay());

        panel.add(btnPlant);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnWater);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnRefill);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnFertilize);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnHarvest);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnExcavate);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnNextDay);

        panel.add(Box.createVerticalStrut(16));

        // Color legend
        panel.add(buildLegendPanel());

        return panel;
    }

    /**
     * Builds the stage color legend panel shown below the action buttons.
     *
     * @return The constructed legend JPanel.
     */
    private JPanel buildLegendPanel() {
        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setBorder(BorderFactory.createTitledBorder("Legend"));

        legend.add(legendRow(FieldPanel.COLOR_SEEDLING,        "SD Seedling"));
        legend.add(legendRow(FieldPanel.COLOR_DORMANT,         "DR Dormant"));
        legend.add(legendRow(FieldPanel.COLOR_ENERGIZING,      "EN Energizing"));
        legend.add(legendRow(FieldPanel.COLOR_LOW_PRODUCTIVE,  "LP Low Prod."));
        legend.add(legendRow(FieldPanel.COLOR_HIGH_PRODUCTIVE, "HP High Prod."));
        legend.add(legendRow(FieldPanel.COLOR_FULLY_MATURE,    "FM Fully Mature"));
        legend.add(legendRow(FieldPanel.COLOR_METEORITE,       "X  Meteorite"));
        legend.add(legendRow(FieldPanel.COLOR_EXCAVATED,       "E  Excavated"));

        return legend;
    }

    /**
     * Creates a single legend row with a colored swatch and a label.
     *
     * @param color The swatch color.
     * @param text  The label text.
     * @return A JPanel row.
     */
    private JPanel legendRow(Color color, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        JLabel swatch = new JLabel("  ");
        swatch.setOpaque(true);
        swatch.setBackground(color);
        swatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        row.add(swatch);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        row.add(lbl);
        return row;
    }

    /**
     * Builds the bottom log panel that displays recent action results.
     *
     * @return The constructed log JPanel.
     */
    private JPanel buildLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Action Log"));
        panel.setPreferredSize(new Dimension(700, 100));

        logArea = new JTextArea(4, 60);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(logArea);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a standard action button with consistent sizing.
     *
     * @param text The button label.
     * @return The constructed JButton.
     */
    private JButton makeActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(160, 32));
        btn.setPreferredSize(new Dimension(160, 32));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    // =========================================================
    // Action handlers
    // =========================================================

    /**
     * Handles a tile click on the field panel.
     * Executes the current pending action on the clicked tile,
     * then resets the pending state.
     *
     * @param row The row index of the clicked tile.
     * @param col The column index of the clicked tile.
     */
    private void onTileClicked(int row, int col) {
        if (pendingAction == PendingAction.NONE) {
            // No action pending — show tile info
            showTileInfo(row, col);
            return;
        }

        String result = "";
        switch (pendingAction) {
            case PLANT:
                result = game.plantSeed(pendingPlantIndex, row, col);
                break;
            case WATER:
                result = game.waterTile(row, col);
                break;
            case FERTILIZE:
                result = game.applyFertilizer(pendingFertIndex, row, col);
                break;
            case HARVEST:
                result = game.removeOrHarvest(row, col);
                break;
            case EXCAVATE:
                result = game.excavateTile(row, col);
                break;
            default:
                break;
        }

        log(result);
        pendingAction = PendingAction.NONE;
        pendingPlantIndex = -1;
        pendingFertIndex  = -1;
        refresh();
    }

    /**
     * Shows an info dialog with the current state of the clicked tile.
     *
     * @param row Row index.
     * @param col Column index.
     */
    private void showTileInfo(int row, int col) {
        Soil soil = game.getField().getSoil(row, col);
        char rowLabel = (char)('A' + row);
        String pos = rowLabel + "" + col;
        StringBuilder info = new StringBuilder();
        info.append("Tile ").append(pos).append("\n");
        info.append("Soil: ").append(soil.getType()).append("\n");

        if (soil.isMeteoriteTile()) {
            info.append("State: Meteorite tile\n");
        } else if (soil.isExcavated()) {
            info.append("State: Excavated (permanently fertilized)\n");
        } else if (soil.isPermanentlyFertilized()) {
            info.append("Fertilizer: Permanent\n");
        } else if (soil.hasFertilizer()) {
            info.append("Fertilizer: ").append(soil.getFertilizer().getName())
                .append(" (").append(soil.getFertilizer().getRemainingDays())
                .append(" days left)\n");
        }

        if (soil.hasPlant()) {
            Plant p = soil.getPlant();
            info.append("Plant: ").append(p.getName()).append("\n");
            info.append("Stage: ").append(p.getCurrentStage().getDisplayName())
                .append(" (").append(p.getCurrentStageIndex() + 1)
                .append("/").append(p.getTotalStages()).append(")\n");
            info.append("Watered: ").append(p.isWatered() ? "Yes" : "No").append("\n");
            if (p.canHarvest()) {
                info.append(">>> Ready to harvest! <<<\n");
            }
        }

        JOptionPane.showMessageDialog(this, info.toString(),
            "Tile Info: " + pos, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Starts the plant action: asks the player to choose a plant,
     * then waits for a tile click.
     */
    private void startPlantAction() {
        List<Plant> affordable = game.getAffordablePlants();
        if (affordable.isEmpty()) {
            log("You cannot afford any plants!");
            return;
        }
        String[] options = new String[affordable.size()];
        for (int i = 0; i < affordable.size(); i++) {
            Plant p = affordable.get(i);
            options[i] = p.getName()
                + " (" + p.getSeedPrice() + "g)"
                + " | Pref: " + p.getPreferredSoil()
                + " | " + p.getLowCropName() + "/" + p.getHighCropName();
        }
        int choice = JOptionPane.showOptionDialog(
            this, "Choose a plant to plant:", "Plant a Seed",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);

        if (choice < 0) return;
        pendingPlantIndex = choice;
        pendingAction = PendingAction.PLANT;
        log("Click a tile to plant " + affordable.get(choice).getName() + "...");
    }

    /**
     * Starts the water action: waits for a tile click to water a plant.
     */
    private void startWaterAction() {
        if (!game.canWater()) {
            log("Watering can is empty! Refill first.");
            return;
        }
        if (!game.hasWaterablePlant()) {
            log("No plants to water right now.");
            return;
        }
        pendingAction = PendingAction.WATER;
        log("Click a tile to water its plant...");
    }

    /**
     * Immediately refills the watering can (no tile selection needed).
     */
    private void doRefill() {
        String result = game.refillWateringCan();
        log(result);
        refresh();
    }

    /**
     * Starts the fertilize action: asks the player to choose a fertilizer,
     * then waits for a tile click.
     */
    private void startFertilizeAction() {
        List<Fertilizer> fertilizers = game.getAvailableFertilizers();
        if (fertilizers.isEmpty()) {
            log("No fertilizers available.");
            return;
        }
        String[] options = new String[fertilizers.size()];
        for (int i = 0; i < fertilizers.size(); i++) {
            Fertilizer f = fertilizers.get(i);
            options[i] = f.getName()
                + " (" + f.getPrice() + "g, " + f.getRemainingDays() + " days)";
        }
        int choice = JOptionPane.showOptionDialog(
            this, "Choose a fertilizer:", "Apply Fertilizer",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);

        if (choice < 0) return;
        pendingFertIndex = choice;
        pendingAction = PendingAction.FERTILIZE;
        log("Click a tile to apply " + fertilizers.get(choice).getName() + "...");
    }

    /**
     * Starts the harvest/remove action: waits for a tile click.
     */
    private void startHarvestAction() {
        pendingAction = PendingAction.HARVEST;
        log("Click a tile to remove/harvest its plant...");
    }

    /**
     * Starts the excavate action: waits for a tile click.
     */
    private void startExcavateAction() {
        if (!game.isMeteoriteHit()) {
            log("No meteorite has hit yet.");
            return;
        }
        if (game.excavationLimitReached()) {
            log("Already excavated 5 tiles today.");
            return;
        }
        if (!game.canAffordExcavation()) {
            log("Not enough savings to excavate (costs 500g).");
            return;
        }
        pendingAction = PendingAction.EXCAVATE;
        log("Click a meteorite tile (X) to excavate it for 500g...");
    }

    /**
     * Advances to the next day. If the game is over, shows the end screen.
     */
    private void doNextDay() {
        String result = game.nextDay();
        log(result);
        refresh();

        if (game.isGameOver()) {
            String endSummary = game.endGame();
            JOptionPane.showMessageDialog(this, endSummary,
                "Game Over!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    // =========================================================
    // UI helpers
    // =========================================================

    /**
     * Refreshes all display components to reflect the current game state.
     * Updates the status bar, field panel, and button enabled states.
     */
    private void refresh() {
        // Update status labels
        lblDay.setText("Day: " + game.getCurrentDay() + "/" + game.getMaxDays());
        lblSavings.setText("Savings: " + game.getSavings() + "g");
        lblWater.setText("Water: " + game.getWaterLevel() + "/" + game.getMaxWaterLevel());

        // Update field display
        fieldPanel.refresh();

        // Update button states
        btnWater.setEnabled(game.hasWaterablePlant() && game.canWater());
        btnRefill.setEnabled(game.canAffordRefill());
        btnExcavate.setEnabled(
            game.isMeteoriteHit()
            && !game.excavationLimitReached()
            && game.canAffordExcavation()
        );
        btnPlant.setEnabled(!game.getAffordablePlants().isEmpty());
    }

    /**
     * Appends a message to the action log area and scrolls to the bottom.
     *
     * @param message The message to append.
     */
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
