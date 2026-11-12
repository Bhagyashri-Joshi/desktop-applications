package clockapp;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Cross-platform (Metal) look and feel respects custom button/tab colours on every OS.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the default look and feel.
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}