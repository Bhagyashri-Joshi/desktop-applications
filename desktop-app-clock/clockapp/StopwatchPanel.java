package clockapp;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

/** Page 2: stopwatch with Start/Stop, Lap, Reset and millisecond precision. */
public class StopwatchPanel extends JPanel implements Themeable {

    private final JLabel display = new JLabel("00:00:00.000", SwingConstants.CENTER);
    private final JButton startStopButton = new JButton("Start");
    private final JButton lapButton = new JButton("Lap");
    private final JButton resetButton = new JButton("Reset");

    private final DefaultListModel<String> lapModel = new DefaultListModel<>();
    private final JList<String> lapList = new JList<>(lapModel);
    private final JScrollPane lapScroll = new JScrollPane(lapList);
    private final JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    private final JPanel top = new JPanel(new BorderLayout(0, 18));

    private final Timer timer = new Timer(16, e -> refreshDisplay());

    private boolean running = false;
    private long startNanos = 0;
    private long accumulatedNanos = 0;
    private long lastLapMs = 0;
    private int lapCount = 0;

    private Theme theme = Theme.LIGHT;

    public StopwatchPanel() {
        super(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(28, 40, 28, 40));

        display.setFont(new Font(Font.MONOSPACED, Font.BOLD, 64));

        for (JButton b : new JButton[]{startStopButton, lapButton, resetButton}) {
            b.setFont(b.getFont().deriveFont(Font.BOLD, 15f));
            buttonRow.add(b);
        }
        buttonRow.setOpaque(false);

        top.setOpaque(false);
        top.add(display, BorderLayout.CENTER);
        top.add(buttonRow, BorderLayout.SOUTH);

        lapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lapList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        lapList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected,
                                                          boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, false);
                label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                return label;
            }
        });

        add(top, BorderLayout.NORTH);
        add(lapScroll, BorderLayout.CENTER);

        startStopButton.addActionListener(e -> toggleRunning());
        lapButton.addActionListener(e -> recordLap());
        resetButton.addActionListener(e -> reset());

        updateButtons();
        applyTheme(theme);
    }

    private long elapsedMillis() {
        long total = accumulatedNanos;
        if (running) {
            total += System.nanoTime() - startNanos;
        }
        return total / 1_000_000L;
    }

    private void toggleRunning() {
        if (running) {
            accumulatedNanos += System.nanoTime() - startNanos;
            running = false;
            timer.stop();
        } else {
            startNanos = System.nanoTime();
            running = true;
            timer.start();
        }
        refreshDisplay();
        updateButtons();
    }

    private void recordLap() {
        if (!running) {
            return;
        }
        long total = elapsedMillis();
        long split = total - lastLapMs;
        lastLapMs = total;
        lapCount++;
        lapModel.add(0, String.format("Lap %02d    %s    +%s",
                lapCount, format(total), format(split)));
    }

    private void reset() {
        running = false;
        timer.stop();
        accumulatedNanos = 0;
        lastLapMs = 0;
        lapCount = 0;
        lapModel.clear();
        refreshDisplay();
        updateButtons();
    }

    private void refreshDisplay() {
        display.setText(format(elapsedMillis()));
    }

    private void updateButtons() {
        startStopButton.setText(running ? "Stop" : "Start");
        lapButton.setEnabled(running);
        resetButton.setEnabled(!running && (elapsedMillis() > 0 || !lapModel.isEmpty()));
    }

    private static String format(long ms) {
        long hours = ms / 3_600_000L;
        long minutes = (ms / 60_000L) % 60;
        long seconds = (ms / 1_000L) % 60;
        long millis = ms % 1_000L;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        setBackground(theme.background);
        display.setForeground(theme.foreground);

        theme.styleButton(startStopButton, true);
        theme.styleButton(lapButton, false);
        theme.styleButton(resetButton, false);

        lapList.setBackground(theme.surface);
        lapList.setForeground(theme.foreground);
        lapList.setSelectionBackground(theme.accent);
        lapList.setSelectionForeground(theme.accentText);
        lapScroll.getViewport().setBackground(theme.surface);
        lapScroll.setBorder(BorderFactory.createLineBorder(theme.border));
        repaint();
    }
}