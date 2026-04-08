import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A JPanel that visually displays the 10x10 farming field as a grid of buttons.
 *
 * Each button represents one Soil tile in the Field model and is color-coded
 * based on its current state:
 * - soil type (loam, sand, gravel)
 * - plant growth stage
 * - meteorite impact tile
 * - excavated or permanently fertilized tile
 *
 * The panel also handles user interaction through tile click events and
 * provides tooltips describing the tile's detailed status.
 */
public class FieldPanel extends JPanel {

    /** Color constants corresponding to the official stage color legend. */
    public static final Color COLOR_SEEDLING       = new Color(102, 204, 102);
    public static final Color COLOR_DORMANT        = new Color(102, 153, 255);
    public static final Color COLOR_ENERGIZING     = new Color(178, 102, 255);
    public static final Color COLOR_LOW_PRODUCTIVE = new Color(255, 178, 102);
    public static final Color COLOR_HIGH_PRODUCTIVE= new Color(255, 102, 102);
    public static final Color COLOR_FULLY_MATURE   = new Color(50,  50,  50);
    public static final Color COLOR_METEORITE      = new Color(160, 160, 160);
    public static final Color COLOR_EXCAVATED      = new Color(255, 230, 100);
    public static final Color COLOR_LOAM           = new Color(210, 190, 160);
    public static final Color COLOR_SAND           = new Color(230, 215, 170);
    public static final Color COLOR_GRAVEL         = new Color(190, 190, 185);

    private Field field;
    private JButton[][] buttons;
    private TileClickListener tileClickListener;

    /**
     * Functional interface used to handle tile click events.
     *
     * Implemented by GameGUI so the controller can respond when
     * the player selects a tile in the field.
     */
    public interface TileClickListener {

        /**
         * Executes when a tile button is clicked.
         *
         * @param row the row index of the selected tile (0–9)
         * @param col the column index of the selected tile (0–9)
         */
        void onTileClicked(int row, int col);
    }

    /**
     * Creates a FieldPanel linked to the specified Field model.
     *
     * Initializes a 10x10 grid layout and creates all tile buttons.
     *
     * @param field the Field model containing soil and plant data
     */
    public FieldPanel(Field field) {
        this.field = field;
        this.buttons = new JButton[10][10];
        setLayout(new GridLayout(10, 10, 2, 2));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        initButtons();
    }

    /**
     * Creates and configures the 10x10 grid of tile buttons.
     *
     * Each button:
     * - represents one Soil tile
     * - uses compact styling for readability
     * - notifies the TileClickListener when clicked
     */
    private void initButtons() {

        for (int row = 0; row < 10; row++) {

            for (int col = 0; col < 10; col++) {

                final int r = row;
                final int c = col;

                JButton btn = new JButton();

                btn.setFont(new Font("SansSerif", Font.BOLD, 10));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setFocusPainted(false);

                btn.addActionListener(e -> {

                    if (tileClickListener != null) {
                        tileClickListener.onTileClicked(r, c);
                    }

                });

                buttons[row][col] = btn;
                add(btn);
            }
        }
    }

    /**
     * Assigns the listener that will receive tile click events.
     *
     * @param listener the TileClickListener to notify when a tile is clicked
     */
    public void setTileClickListener(TileClickListener listener) {
        this.tileClickListener = listener;
    }

    /**
     * Updates all tile buttons to reflect the current state of the Field.
     *
     * Should be called after any game action that modifies:
     * - plant growth
     * - watering status
     * - fertilizer effects
     * - excavation state
     * - meteorite impacts
     */
    public void refresh() {

        for (int row = 0; row < 10; row++) {

            for (int col = 0; col < 10; col++) {

                updateButton(row, col);

            }
        }
    }

    /**
     * Updates one tile button's:
     * - label text
     * - background color
     * - text color
     * - tooltip description
     *
     * Display priority:
     * 1. Meteorite tile
     * 2. Excavated tile
     * 3. Tile containing plant
     * 4. Empty soil tile
     *
     * @param row the row index of the tile (0–9)
     * @param col the column index of the tile (0–9)
     */
    private void updateButton(int row, int col) {

        Soil soil = field.getSoil(row, col);
        JButton btn = buttons[row][col];

        String label;
        Color bg;
        Color fg = Color.BLACK;
        String tooltip;

        char rowLabel = (char)('A' + row);
        String pos = rowLabel + "" + col;

        if (soil.isMeteoriteTile()) {

            label   = "X";
            bg      = COLOR_METEORITE;
            tooltip = pos + ": Meteorite tile (excavate for 500g)";

        }
        else if (soil.isExcavated() && !soil.hasPlant()) {

            label   = "E";
            bg      = COLOR_EXCAVATED;
            tooltip = pos + ": Excavated [" + soil.getType() + "] - Permanently fertilized";

        }
        else if (soil.hasPlant()) {

            Plant plant = soil.getPlant();
            PlantStage stage = plant.getCurrentStage();

            label   = getStageLabel(stage);
            bg      = getStageColor(stage);
            fg      = getStageTextColor(stage);
            tooltip = buildPlantTooltip(pos, plant, soil);

        }
        else {

            label   = soil.getType().substring(0, 1).toUpperCase();
            bg      = getSoilColor(soil.getType());

            tooltip = pos + ": " + soil.getType()
                    + (soil.isPermanentlyFertilized() ? " [Perm. Fertilized]" : "")
                    + (soil.hasFertilizer()
                        ? " [Fertilized: " + soil.getFertilizer().getRemainingDays() + " days]"
                        : "");

        }

        btn.setText(label);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setToolTipText(tooltip);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
    }

    /**
     * Returns the short display label representing a plant's growth stage.
     *
     * @param stage the current PlantStage
     * @return a 2–3 character abbreviation of the stage
     */
    private String getStageLabel(PlantStage stage) {

        switch (stage.getStageType()) {

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

    /**
     * Returns the background color corresponding to a plant's growth stage.
     *
     * @param stage the current PlantStage
     * @return the Color used for the tile background
     */
    private Color getStageColor(PlantStage stage) {

        switch (stage.getStageType()) {

            case SEEDLING:
                return COLOR_SEEDLING;

            case DORMANT:
                return COLOR_DORMANT;

            case ENERGIZING:
                return COLOR_ENERGIZING;

            case LOW_PRODUCTIVE:
                return COLOR_LOW_PRODUCTIVE;

            case HIGH_PRODUCTIVE:
                return COLOR_HIGH_PRODUCTIVE;

            case FULLY_MATURE:
                return COLOR_FULLY_MATURE;

            default:
                return Color.WHITE;
        }
    }

    /**
     * Returns the appropriate text color for a stage tile.
     *
     * Fully Mature uses white text due to its dark background.
     *
     * @param stage the current PlantStage
     * @return the Color used for label text
     */
    private Color getStageTextColor(PlantStage stage) {

        if (stage.getStageType() == PlantStage.StageType.FULLY_MATURE) {
            return Color.WHITE;
        }

        return Color.BLACK;
    }

    /**
     * Returns the background color for an empty soil tile.
     *
     * @param soilType the soil classification string
     * @return the Color representing the soil type
     */
    private Color getSoilColor(String soilType) {

        switch (soilType) {

            case "loam":
                return COLOR_LOAM;

            case "sand":
                return COLOR_SAND;

            case "gravel":
                return COLOR_GRAVEL;

            default:
                return Color.WHITE;
        }
    }

    /**
     * Builds a detailed tooltip describing a tile containing a plant.
     *
     * Tooltip includes:
     * - plant name
     * - current stage
     * - stage progress
     * - watering status
     * - fertilizer duration
     * - harvest readiness
     *
     * HTML formatting allows multi-line display.
     *
     * @param pos the tile position label (example: A5)
     * @param plant the plant occupying the tile
     * @param soil the soil tile containing the plant
     * @return an HTML-formatted tooltip string
     */
    private String buildPlantTooltip(String pos, Plant plant, Soil soil) {

        StringBuilder sb = new StringBuilder();

        sb.append("<html>");

        sb.append(pos).append(": ").append(plant.getName());

        sb.append("<br>Stage: ")
          .append(plant.getCurrentStage().getDisplayName());

        sb.append(" (")
          .append(plant.getCurrentStageIndex() + 1)
          .append("/")
          .append(plant.getTotalStages())
          .append(")");

        sb.append("<br>Watered: ")
          .append(plant.isWatered() ? "Yes" : "No");

        if (soil.isPermanentlyFertilized()) {

            sb.append("<br>Fertilizer: Permanent");

        }
        else if (soil.hasFertilizer()) {

            sb.append("<br>Fertilizer: ")
              .append(soil.getFertilizer().getRemainingDays())
              .append(" days left");

        }

        if (plant.canHarvest()) {

            sb.append("<br><b>Ready to harvest!</b>");

        }

        sb.append("</html>");

        return sb.toString();
    }

    /**
     * Highlights selected tiles with a cyan border.
     *
     * Used to indicate multi-tile selection in progress.
     * Passing null removes all highlights.
     *
     * @param selectedTiles a 10x10 boolean array
     *                      true  = highlighted tile
     *                      false = normal tile
     */
    public void highlightTiles(boolean[][] selectedTiles) {

        for (int row = 0; row < 10; row++) {

            for (int col = 0; col < 10; col++) {

                if (selectedTiles != null && selectedTiles[row][col]) {

                    buttons[row][col].setBorder(
                        BorderFactory.createLineBorder(Color.CYAN, 2)
                    );

                }
                else {

                    buttons[row][col].setBorder(
                        UIManager.getBorder("Button.border")
                    );

                }
            }
        }
    }
}
