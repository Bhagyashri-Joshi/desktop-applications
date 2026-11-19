import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Modal form used for both adding and editing an enquiry. */
public class EnquiryDialog extends JDialog {

    private final JTextField nameField = new JTextField(25);
    private final JTextField emailField = new JTextField(25);
    private final JTextField phoneField = new JTextField(25);
    private final JTextField subjectField = new JTextField(25);
    private final JTextArea descArea = new JTextArea(5, 25);
    private final JComboBox<String> statusBox = new JComboBox<>(Enquiry.STATUSES);
    private final JTextField followUpField = new JTextField(25);

    private final Enquiry enquiry;
    private boolean saved = false;

    public EnquiryDialog(Frame owner, Enquiry existing) {
        super(owner, existing == null ? "Add Enquiry" : "Edit Enquiry #" + existing.getId(), true);
        setIconImage(MainFrame.loadAppIcon());
        this.enquiry = existing == null ? new Enquiry() : existing;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        followUpField.setToolTipText("Format: yyyy-MM-dd (leave blank for none)");

        addRow(form, g, 0, "Name *", nameField);
        addRow(form, g, 1, "Email *", emailField);
        addRow(form, g, 2, "Phone", phoneField);
        addRow(form, g, 3, "Subject *", subjectField);
        addRow(form, g, 4, "Description", new JScrollPane(descArea));
        addRow(form, g, 5, "Status", statusBox);
        addRow(form, g, 6, "Follow-up (yyyy-MM-dd)", followUpField);

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancel);
        buttons.add(save);

        if (existing != null) fill(existing);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);
        pack();
        setLocationRelativeTo(owner);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridy = row;
        g.gridx = 0; g.weightx = 0;
        p.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        p.add(field, g);
    }

    private void fill(Enquiry e) {
        nameField.setText(e.getName());
        emailField.setText(e.getEmail());
        phoneField.setText(e.getPhone());
        subjectField.setText(e.getSubject());
        descArea.setText(e.getDescription());
        statusBox.setSelectedItem(e.getStatus());
        followUpField.setText(e.getFollowUpDate() == null ? "" : e.getFollowUpDate().toString());
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String subject = subjectField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || subject.isEmpty()) {
            error("Name, Email and Subject are required.");
            return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            error("Please enter a valid email address.");
            return;
        }
        if (!phone.isEmpty() && !phone.matches("^[0-9+\\- ]{7,15}$")) {
            error("Phone should contain 7-15 digits (you may use +, - or spaces).");
            return;
        }

        Date followUp = null;
        String fu = followUpField.getText().trim();
        if (!fu.isEmpty()) {
            try {
                followUp = Date.valueOf(LocalDate.parse(fu));
            } catch (DateTimeParseException ex) {
                error("Follow-up date must be in yyyy-MM-dd format, e.g. 2026-12-01.");
                return;
            }
        }

        enquiry.setName(name);
        enquiry.setEmail(email);
        enquiry.setPhone(phone);
        enquiry.setSubject(subject);
        enquiry.setDescription(descArea.getText().trim());
        enquiry.setStatus((String) statusBox.getSelectedItem());
        enquiry.setFollowUpDate(followUp);
        saved = true;
        dispose();
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    public boolean isSaved() { return saved; }
    public Enquiry getEnquiry() { return enquiry; }
}