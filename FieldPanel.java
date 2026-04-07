import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A JPanel that displays the 10x10 farming field as a grid of colored buttons.
 * Each button represents a soil tile and is color-coded based on its current
 * state: soil type, plant growth stage, meteorite impact, or excavation.
 *
 */
public class FieldPanel extends JPanel {

    /** Color constants matching the spec stage legend. */
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
     * Functional interface for handling tile click events.
     * The GameGUI implements this to respond when the player clicks a tile.
     */
    public interface TileClickListener {
        /**
         * Called when a tile button is clicked.
         *
         * @param row The row index of the clicked tile.
         * @param col The column index of the clicked tile.
         */
        void onTileClicked(int row, int col);
    }

    /**
     * Constructs a FieldPanel for the given field.
     * Initializes the 10x10 grid of tile buttons.
     *
     * @param field The game field to display.
     */
    public FieldPanel(Field field) {
        this.field = field;
        this.buttons = new JButton[10][10];
        setLayout(new GridLayout(10, 10, 2, 2));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        initButtons();
    }

    /**
     * Creates all 10x10 tile buttons and adds them to the panel.
     * Each button fires the tileClickListener when clicked.
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
     * Sets the listener that receives tile click events.
     *
     * @param listener The TileClickListener to notify on clicks.
     */
    public void setTileClickListener(TileClickListener listener) {
        this.tileClickListener = listener;
    }

    /**
     * Refreshes all tile buttons to reflect the current field state.
     * Should be called after any game action that changes the field.
     */
    public void refresh() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                updateButton(row, col);
            }
        }
    }

    /**
     * Updates a single tile button's label, background color, and tooltip
     * to reflect the current state of the corresponding soil tile.
     *
     * @param row The row index of the tile to update.
     * @param col The column index of the tile to update.
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
        } else if (soil.isExcavated() && !soil.hasPlant()) {
            label   = "E";
            bg      = COLOR_EXCAVATED;
            tooltip = pos + ": Excavated [" + soil.getType() + "] - Permanently fertilized";
        } else if (soil.hasPlant()) {
            Plant plant = soil.getPlant();
            PlantStage stage = plant.getCurrentStage();
            label   = getStageLabel(stage);
            bg      = getStageColor(stage);
            fg      = getStageTextColor(stage);
            tooltip = buildPlantTooltip(pos, plant, soil);
        } else {
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
     * Returns the short text label for a plant's current stage.
     *
     * @param stage The current PlantStage.
     * @return A short 2-3 character label string.
     */
    private String getStageLabel(PlantStage stage) {
        switch (stage.getStageType()) {
            case SEEDLING:        return "SD";
            case DORMANT:         return "DR";
            case ENERGIZING:      return "EN";
            case LOW_PRODUCTIVE:  return "LP";
            case HIGH_PRODUCTIVE: return "HP";
            case FULLY_MATURE:    return "FM";
            default:              return "??";
        }
    }

    /**
     * Returns the background color for a given plant stage,
     * matching the spec's color legend.
     *
     * @param stage The current PlantStage.
     * @return The Color to use as the tile background.
     */
    private Color getStageColor(PlantStage stage) {
        switch (stage.getStageType()) {
            case SEEDLING:        return COLOR_SEEDLING;
            case DORMANT:         return COLOR_DORMANT;
            case ENERGIZING:      return COLOR_ENERGIZING;
            case LOW_PRODUCTIVE:  return COLOR_LOW_PRODUCTIVE;
            case HIGH_PRODUCTIVE: return COLOR_HIGH_PRODUCTIVE;
            case FULLY_MATURE:    return COLOR_FULLY_MATURE;
            default:              return Color.WHITE;
        }
    }

    /**
     * Returns the appropriate text color for a stage tile.
     * Fully Mature uses white text on dark background.
     *
     * @param stage The current PlantStage.
     * @return The foreground Color for the tile label.
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
     * @param soilType The soil type string.
     * @return The Color for the soil type.
     */
    private Color getSoilColor(String soilType) {
        switch (soilType) {
            case "loam":   return COLOR_LOAM;
            case "sand":   return COLOR_SAND;
            case "gravel": return COLOR_GRAVEL;
            default:       return Color.WHITE;
        }
    }

    /**
     * Builds a detailed tooltip string for a tile with a plant on it.
     * Shows plant name, stage, watered status, and fertilizer info.
     *
     * @param pos   The position string (e.g. "A5").
     * @param plant The plant on this tile.
     * @param soil  The soil tile.
     * @return A formatted tooltip string.
     */
    private String buildPlantTooltip(String pos, Plant plant, Soil soil) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(pos).append(": ").append(plant.getName());
        sb.append("<br>Stage: ").append(plant.getCurrentStage().getDisplayName());
        sb.append(" (").append(plant.getCurrentStageIndex() + 1)
          .append("/").append(plant.getTotalStages()).append(")");
        sb.append("<br>Watered: ").append(plant.isWatered() ? "Yes" : "No");
        if (soil.isPermanentlyFertilized()) {
            sb.append("<br>Fertilizer: Permanent");
        } else if (soil.hasFertilizer()) {
            sb.append("<br>Fertilizer: ")
              .append(soil.getFertilizer().getRemainingDays()).append(" days left");
        }
        if (plant.canHarvest()) {
            sb.append("<br><b>Ready to harvest!</b>");
        }
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Highlights a set of selected tiles with a cyan border to indicate
     * they are part of a multi-tile selection in progress.
     * Pass null or empty array to clear all highlights.
     *
     * @param selectedTiles A 2D boolean array where true = highlighted.
     */
    public void highlightTiles(boolean[][] selectedTiles) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (selectedTiles != null && selectedTiles[row][col]) {
                    buttons[row][col].setBorder(
                        BorderFactory.createLineBorder(Color.CYAN, 2));
                } else {
                    buttons[row][col].setBorder(
                        UIManager.getBorder("Button.border"));
                }
            }
        }
    }
}
