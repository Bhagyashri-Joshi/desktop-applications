package clockapp;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.Consumer;

/** Horizontal bar at the top: page tabs on the left, theme toggle on the right. */
public class NavBar extends JPanel implements Themeable {

    private final JButton clockTab = new JButton("Digital Clock");
    private final JButton stopwatchTab = new JButton("Stopwatch");
    private final JButton themeButton = new JButton();
    private final JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private final JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));

    private Theme theme = Theme.LIGHT;
    private String activePage = MainFrame.PAGE_CLOCK;

    public NavBar(Consumer<String> onPageSelected, Runnable onThemeToggle) {
        super(new BorderLayout());

        clockTab.addActionListener(e -> {
            setActivePage(MainFrame.PAGE_CLOCK);
            onPageSelected.accept(MainFrame.PAGE_CLOCK);
        });
        stopwatchTab.addActionListener(e -> {
            setActivePage(MainFrame.PAGE_STOPWATCH);
            onPageSelected.accept(MainFrame.PAGE_STOPWATCH);
        });
        themeButton.addActionListener(e -> onThemeToggle.run());

        for (JButton tab : new JButton[]{clockTab, stopwatchTab}) {
            tab.setFocusPainted(false);
            tab.setOpaque(true);
            tab.setContentAreaFilled(true);
            tab.setFont(tab.getFont().deriveFont(Font.BOLD, 15f));
            tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tabs.add(tab);
        }
        themeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        right.add(themeButton);

        add(tabs, BorderLayout.WEST);
        add(right, BorderLayout.EAST);

        applyTheme(theme);
    }

    public void setActivePage(String page) {
        this.activePage = page;
        applyTheme(theme);
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        setBackground(theme.surface);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, theme.border));
        tabs.setBackground(theme.surface);
        right.setBackground(theme.surface);

        styleTab(clockTab, MainFrame.PAGE_CLOCK.equals(activePage));
        styleTab(stopwatchTab, MainFrame.PAGE_STOPWATCH.equals(activePage));

        themeButton.setText(theme == Theme.LIGHT ? "Dark mode" : "Light mode");
        theme.styleButton(themeButton, false);
        themeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.border),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)));
        repaint();
    }

    private void styleTab(JButton tab, boolean active) {
        tab.setBackground(theme.surface);
        tab.setForeground(active ? theme.accent : theme.muted);
        tab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0,
                        active ? theme.accent : theme.surface),
                BorderFactory.createEmptyBorder(14, 24, 11, 24)));
    }
}