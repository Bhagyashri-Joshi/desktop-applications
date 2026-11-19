import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            // Fail early with a readable message if MySQL is not reachable
            try (Connection c = DBConnection.getConnection()) {
                new MainFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to MySQL:\n" + ex.getMessage()
                        + "\n\nCheck DBConnection.java (URL / user / password) and that schema.sql has been run.",
                        "Database error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
