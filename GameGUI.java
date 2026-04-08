import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main GUI window for the Verdant Sun Farming Simulator.
 *
 * Built using Java Swing, this class displays:
 * - the farming field grid
 * - player statistics (day, savings, water level)
 * - action buttons
 * - a scrolling action log
 *
 * The GUI operates using a "pending action" state pattern.
 * When the player clicks an action button, the GUI waits for
 * the player to click a tile where the action will be applied.
 */
public class GameGUI extends JFrame {

    private Game game;
    private FieldPanel fieldPanel;

    private JLabel lblDay;
    private JLabel lblSavings;
    private JLabel lblWater;

    private JButton btnPlant;
    private JButton btnWater;
    private JButton btnRefill;
    private JButton btnFertilize;
    private JButton btnHarvest;
    private JButton btnExcavate;
    private JButton btnNextDay;

    private JTextArea logArea;

    /**
     * Represents the action currently waiting for tile selection.
     */
    private enum PendingAction {
        NONE,
        PLANT,
        WATER,
        FERTILIZE,
        HARVEST,
        EXCAVATE
    }

    private PendingAction pendingAction = PendingAction.NONE;
    private int pendingPlantIndex = -1;
    private int pendingFertIndex = -1;

    /**
     * Constructs the main game window and initializes the game.
     *
     * Prompts the player to enter their name before starting.
     */
    public GameGUI() {

        game = new Game();

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
     * Builds and arranges all Swing components in the main window.
     */
    private void buildWindow() {

        setTitle("Verdant Sun Farming Simulator");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(6, 6));

        getRootPane().setBorder(
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );

        add(buildStatusPanel(), BorderLayout.NORTH);

        fieldPanel = new FieldPanel(game.getField());
        fieldPanel.setTileClickListener(this::onTileClicked);
        fieldPanel.setPreferredSize(new Dimension(520, 520));

        add(fieldPanel, BorderLayout.CENTER);

        add(buildRightPanel(), BorderLayout.EAST);

        add(buildLogPanel(), BorderLayout.SOUTH);

        pack();

        setLocationRelativeTo(null);

        setResizable(false);
    }

    /**
     * Builds the top status bar displaying:
     * - current day
     * - player savings
     * - watering can level
     *
     * @return constructed status panel
     */
    private JPanel buildStatusPanel() {

        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 20, 4)
        );

        panel.setBackground(new Color(245, 240, 230));

        panel.setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)
        );

        lblDay = new JLabel();
        lblSavings = new JLabel();
        lblWater = new JLabel();

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
     * Builds the right-side panel containing action buttons
     * and the color legend.
     *
     * @return constructed right panel
     */
    private JPanel buildRightPanel() {

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        panel.setPreferredSize(new Dimension(170, 520));

        JLabel actionsLabel = new JLabel("ACTIONS");

        actionsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(actionsLabel);
        panel.add(Box.createVerticalStrut(6));

        btnPlant = makeActionButton("Plant Seed");
        btnWater = makeActionButton("Water Plant");
        btnRefill = makeActionButton("Refill Can");
        btnFertilize = makeActionButton(" Fertilize");
        btnHarvest = makeActionButton("Remove/Harvest");
        btnExcavate = makeActionButton("Excavate");
        btnNextDay = makeActionButton("⏭ Next Day");

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

        panel.add(buildLegendPanel());

        return panel;
    }

    /**
     * Builds the legend panel describing tile colors.
     *
     * @return legend panel
     */
    private JPanel buildLegendPanel() {

        JPanel legend = new JPanel();

        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));

        legend.setBorder(BorderFactory.createTitledBorder("Legend"));

        legend.add(legendRow(FieldPanel.COLOR_SEEDLING, "SD Seedling"));
        legend.add(legendRow(FieldPanel.COLOR_DORMANT, "DR Dormant"));
        legend.add(legendRow(FieldPanel.COLOR_ENERGIZING, "EN Energizing"));
        legend.add(legendRow(FieldPanel.COLOR_LOW_PRODUCTIVE, "LP Low Prod."));
        legend.add(legendRow(FieldPanel.COLOR_HIGH_PRODUCTIVE, "HP High Prod."));
        legend.add(legendRow(FieldPanel.COLOR_FULLY_MATURE, "FM Fully Mature"));
        legend.add(legendRow(FieldPanel.COLOR_METEORITE, "X Meteorite"));
        legend.add(legendRow(FieldPanel.COLOR_EXCAVATED, "E Excavated"));

        return legend;
    }

    /**
     * Creates a single legend row with color swatch and label.
     *
     * @param color background color of legend square
     * @param text description of the color meaning
     * @return legend row panel
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
     * Builds the log panel displaying recent game actions.
     *
     * @return log panel
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

        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a standardized action button.
     *
     * @param text label displayed on the button
     * @return formatted JButton
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

    /**
     * Handles tile click events.
     *
     * Executes the currently selected action on the clicked tile.
     *
     * @param row row index of tile clicked
     * @param col column index of tile clicked
     */
    private void onTileClicked(int row, int col) {

        if (pendingAction == PendingAction.NONE) {
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

        pendingFertIndex = -1;

        refresh();
    }

    /**
     * Displays detailed information about the selected tile.
     *
     * @param row row index of tile
     * @param col column index of tile
     */
    private void showTileInfo(int row, int col) {

        Soil soil = game.getField().getSoil(row, col);

        char rowLabel = (char)('A' + row);

        String pos = rowLabel + "" + col;

        StringBuilder info = new StringBuilder();

        info.append("Tile ").append(pos).append("\n");

        info.append("Soil: ").append(soil.getType()).append("\n");

        if (soil.hasPlant()) {

            Plant p = soil.getPlant();

            info.append("Plant: ").append(p.getName()).append("\n");

            info.append("Stage: ").append(p.getCurrentStage().getDisplayName()).append("\n");
        }

        JOptionPane.showMessageDialog(
            this,
            info.toString(),
            "Tile Info",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Refreshes displayed values in the GUI.
     */
    private void refresh() {

        lblDay.setText("Day: " + game.getCurrentDay() + "/" + game.getMaxDays());

        lblSavings.setText("Savings: " + game.getSavings() + "g");

        lblWater.setText(
            "Water: " + game.getWaterLevel() + "/" + game.getMaxWaterLevel()
        );

        fieldPanel.refresh();

        btnWater.setEnabled(game.hasWaterablePlant() && game.canWater());

        btnRefill.setEnabled(game.canAffordRefill());

        btnExcavate.setEnabled(game.isMeteoriteHit()&& !game.excavationLimitReached()&& game.canAffordExcavation());

        btnPlant.setEnabled(!game.getAffordablePlants().isEmpty());
    }

    /**
     * Appends a message to the action log area.
     *
     * @param message text displayed in log panel
     */
    private void log(String message) {

        logArea.append(message + "\n");

        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
