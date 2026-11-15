import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class BMICalculator extends JFrame {

    // ---- Palette ----
    private static final Color UNDERWEIGHT = new Color(0x2563EB);
    private static final Color NORMAL      = new Color(0x16A34A);
    private static final Color OVERWEIGHT  = new Color(0xD97706);
    private static final Color OBESE       = new Color(0xDC2626);
    private static final Color NEUTRAL     = new Color(0xE5E7EB);
    private static final Color NEUTRAL_TXT = new Color(0x6B7280);

    // ---- Components ----
    private final JRadioButton metricBtn   = new JRadioButton("Metric", true);
    private final JRadioButton imperialBtn = new JRadioButton("Imperial");

    private final JLabel weightLabel = new JLabel();
    private final JLabel heightLabel = new JLabel();
    private final JLabel inchLabel   = new JLabel("Height (in)");

    private final JTextField weightField = new JTextField(8);
    private final JTextField heightField = new JTextField(8);
    private final JTextField inchField   = new JTextField(8);

    private final JLabel bmiValue      = new JLabel("--", SwingConstants.CENTER);
    private final JLabel categoryLabel = new JLabel("Enter your details", SwingConstants.CENTER);
    private final JPanel resultPanel   = new JPanel(new GridLayout(2, 1));

    private final JButton calcBtn  = new JButton("Calculate");
    private final JButton clearBtn = new JButton("Clear");
    private final JButton saveBtn  = new JButton("Save");

    // ---- Last result (for saving) ----
    private double lastBmi;
    private String lastCategory, lastWeight, lastHeight, lastUnits;

    public BMICalculator() {
        super("BMI Calculator");
        setIconImage(new ImageIcon(BMICalculator.class.getResource("/icon.png")).getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        buildUi();
        wireEvents();
        updateUnitLabels();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // ================= UI =================

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Unit toggle
        ButtonGroup group = new ButtonGroup();
        group.add(metricBtn);
        group.add(imperialBtn);
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        togglePanel.add(metricBtn);
        togglePanel.add(imperialBtn);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        addRow(form, c, 0, weightLabel, weightField);
        addRow(form, c, 1, heightLabel, heightField);
        addRow(form, c, 2, inchLabel, inchField);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.add(togglePanel, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);

        // Result
        bmiValue.setFont(bmiValue.getFont().deriveFont(Font.BOLD, 36f));
        categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.PLAIN, 15f));
        resultPanel.setOpaque(true);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        resultPanel.add(bmiValue);
        resultPanel.add(categoryLabel);
        setResultColors(NEUTRAL, NEUTRAL_TXT);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.add(calcBtn);
        buttons.add(clearBtn);
        buttons.add(saveBtn);
        saveBtn.setEnabled(false);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.add(resultPanel, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);
        root.add(bottom, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(calcBtn); // Enter key = Calculate
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, JLabel label, JTextField field) {
        c.gridy = row;
        c.gridx = 0; c.weightx = 0;
        p.add(label, c);
        c.gridx = 1; c.weightx = 1;
        p.add(field, c);
    }

    // ================= Events =================

    private void wireEvents() {
        metricBtn.addActionListener(e -> onUnitChange());
        imperialBtn.addActionListener(e -> onUnitChange());
        calcBtn.addActionListener(e -> calculate());
        clearBtn.addActionListener(e -> clearAll());
        saveBtn.addActionListener(e -> saveResult());
    }

    private void onUnitChange() {
        clearAll();
        updateUnitLabels();
    }

    private void updateUnitLabels() {
        boolean metric = metricBtn.isSelected();
        weightLabel.setText(metric ? "Weight (kg)" : "Weight (lb)");
        heightLabel.setText(metric ? "Height (cm)" : "Height (ft)");
        inchLabel.setVisible(!metric);
        inchField.setVisible(!metric);
        pack();
    }

    // ================= Logic =================

    private void calculate() {
        try {
            double weight = parsePositive(weightField.getText(), "Weight");
            double bmi;
            String heightText;

            if (metricBtn.isSelected()) {
                double cm = parsePositive(heightField.getText(), "Height");
                if (cm < 50 || cm > 272) throw new IllegalArgumentException("Height should be between 50 and 272 cm.");
                if (weight > 635)        throw new IllegalArgumentException("Weight looks too high. Please check the value.");
                double m = cm / 100.0;
                bmi = weight / (m * m);
                heightText = trim(cm) + " cm";
                lastWeight = trim(weight) + " kg";
                lastUnits = "Metric";
            } else {
                double ft = parsePositive(heightField.getText(), "Height (ft)");
                double in = inchField.getText().trim().isEmpty() ? 0 : parseNonNegative(inchField.getText(), "Height (in)");
                double totalIn = ft * 12 + in;
                if (totalIn < 20 || totalIn > 107) throw new IllegalArgumentException("Height should be between 2 and 9 ft.");
                if (weight > 1400)                 throw new IllegalArgumentException("Weight looks too high. Please check the value.");
                bmi = 703.0 * weight / (totalIn * totalIn);
                heightText = trim(ft) + " ft " + trim(in) + " in";
                lastWeight = trim(weight) + " lb";
                lastUnits = "Imperial";
            }

            lastBmi = bmi;
            lastHeight = heightText;
            lastCategory = categorize(bmi);

            bmiValue.setText(String.format("%.1f", bmi));
            categoryLabel.setText(lastCategory);
            setResultColors(colorFor(bmi), Color.WHITE);
            saveBtn.setEnabled(true);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private double parsePositive(String s, String name) {
        double v = parseNonNegative(s, name);
        if (v <= 0) throw new IllegalArgumentException(name + " must be greater than zero.");
        return v;
    }

    private double parseNonNegative(String s, String name) {
        try {
            double v = Double.parseDouble(s.trim());
            if (v < 0 || Double.isNaN(v) || Double.isInfinite(v)) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a valid number.");
        }
    }

    private static String categorize(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal weight";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }

    private static Color colorFor(double bmi) {
        if (bmi < 18.5) return UNDERWEIGHT;
        if (bmi < 25.0) return NORMAL;
        if (bmi < 30.0) return OVERWEIGHT;
        return OBESE;
    }

    private void setResultColors(Color bg, Color fg) {
        resultPanel.setBackground(bg);
        bmiValue.setForeground(fg);
        categoryLabel.setForeground(fg);
    }

    private static String trim(double v) {
        return (v == Math.floor(v)) ? String.valueOf((long) v) : String.format("%.1f", v);
    }

    private void clearAll() {
        weightField.setText("");
        heightField.setText("");
        inchField.setText("");
        bmiValue.setText("--");
        categoryLabel.setText("Enter your details");
        setResultColors(NEUTRAL, NEUTRAL_TXT);
        saveBtn.setEnabled(false);
        weightField.requestFocusInWindow();
    }

    // ================= Export =================

    /** Appends the latest result as a row to a CSV file (header added if the file is new). */
    private void saveResult() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("bmi_results.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        boolean needsHeader = !file.exists() || file.length() == 0;

        try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
            if (needsHeader) out.println("Date,Units,Weight,Height,BMI,Category");
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            out.printf("%s,%s,%s,%s,%.1f,%s%n", time, lastUnits, lastWeight, lastHeight, lastBmi, lastCategory);
            JOptionPane.showMessageDialog(this, "Saved to " + file.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save file:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= Main =================

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new BMICalculator().setVisible(true));
    }
}