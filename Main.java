import javax.swing.SwingUtilities;

/**
 * Entry point for the Verdant Sun Farming Simulator (MCO2).
 * Launches the Swing GUI on the Event Dispatch Thread.
 */
public class Main {
    /**
     * Main method. Starts the game GUI.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI());
    }
}
