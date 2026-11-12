package clockapp;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Page 1: live digital clock in 12-hour format with AM/PM and the current date. */
public class ClockPanel extends JPanel implements Themeable {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm:ss", Locale.ENGLISH);
    private static final DateTimeFormatter AMPM_FMT =
            DateTimeFormatter.ofPattern("a", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH);

    private final JLabel timeLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel ampmLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel dateLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel timeRow;

    public ClockPanel() {
        super(new GridBagLayout());

        timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 88));
        ampmLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        dateLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));

        FlowLayout rowLayout = new FlowLayout(FlowLayout.CENTER, 12, 0);
        rowLayout.setAlignOnBaseline(true);
        timeRow = new JPanel(rowLayout);
        timeRow.setOpaque(false);
        timeRow.add(timeLabel);
        timeRow.add(ampmLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(timeRow, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 0, 0);
        add(dateLabel, gbc);

        updateTime();
        new Timer(200, e -> updateTime()).start();
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(TIME_FMT));
        ampmLabel.setText(now.format(AMPM_FMT).toUpperCase(Locale.ENGLISH));
        dateLabel.setText(now.format(DATE_FMT));
    }

    @Override
    public void applyTheme(Theme theme) {
        setBackground(theme.background);
        timeLabel.setForeground(theme.foreground);
        ampmLabel.setForeground(theme.accent);
        dateLabel.setForeground(theme.muted);
        repaint();
    }
}