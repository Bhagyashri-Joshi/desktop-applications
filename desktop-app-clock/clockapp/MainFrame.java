package clockapp;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;

/** Application window: navigation bar on top, switchable pages below. */
public class MainFrame extends JFrame {

    public static final String PAGE_CLOCK = "clock";
    public static final String PAGE_STOPWATCH = "stopwatch";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);
    private final NavBar navBar;
    private final ClockPanel clockPanel = new ClockPanel();
    private final StopwatchPanel stopwatchPanel = new StopwatchPanel();

    private Theme theme = Theme.LIGHT;

    public MainFrame() {
        super("Digital Clock & Stopwatch");
        setWindowIcon();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        navBar = new NavBar(this::showPage, this::toggleTheme);
        pages.add(clockPanel, PAGE_CLOCK);
        pages.add(stopwatchPanel, PAGE_STOPWATCH);

        add(navBar, BorderLayout.NORTH);
        add(pages, BorderLayout.CENTER);

        applyTheme();
        showPage(PAGE_CLOCK);

        setMinimumSize(new Dimension(620, 480));
        setSize(760, 540);
        setLocationRelativeTo(null);
    }

    private void setWindowIcon() {
        URL iconResource = MainFrame.class.getResource("/clockapp/assets/clock.png");
        if (iconResource != null) {
            setIconImage(new ImageIcon(iconResource).getImage());
            return;
        }

        File iconFile = new File("clockapp/assets/clock.png");
        if (!iconFile.isFile()) {
            iconFile = new File("assets/clock.png");
        }
        if (!iconFile.isFile()) {
            throw new IllegalStateException("Missing application icon: clock.png");
        }
        setIconImage(new ImageIcon(iconFile.getAbsolutePath()).getImage());
    }

    private void showPage(String page) {
        cardLayout.show(pages, page);
    }

    private void toggleTheme() {
        theme = theme.toggle();
        applyTheme();
    }

    private void applyTheme() {
        getContentPane().setBackground(theme.background);
        pages.setBackground(theme.background);
        navBar.applyTheme(theme);
        clockPanel.applyTheme(theme);
        stopwatchPanel.applyTheme(theme);
        repaint();
    }
}