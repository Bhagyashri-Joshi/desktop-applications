import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    private final EnquiryDAO dao = new EnquiryDAO();
    private List<Enquiry> current = new ArrayList<>();

    // Enquiries tab
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Email", "Phone", "Subject", "Status", "Follow-up", "Created"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField searchField = new JTextField(20);
    private final JComboBox<String> filterBox = new JComboBox<>(
            new String[]{"All", "New", "Pending", "In Progress", "Resolved"});

    // Dashboard tab
    private final JLabel totalLbl = new JLabel("0", SwingConstants.CENTER);
    private final JLabel newLbl = new JLabel("0", SwingConstants.CENTER);
    private final JLabel pendingLbl = new JLabel("0", SwingConstants.CENTER);
    private final JLabel progressLbl = new JLabel("0", SwingConstants.CENTER);
    private final JLabel resolvedLbl = new JLabel("0", SwingConstants.CENTER);
    private final JLabel dueLbl = new JLabel("0", SwingConstants.CENTER);

    public MainFrame() {
        super("Enquiry Management System");
        setIconImage(loadAppIcon());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", buildDashboard());
        tabs.addTab("Enquiries", buildEnquiryPanel());
        tabs.addChangeListener(e -> { if (tabs.getSelectedIndex() == 0) refreshDashboard(); });
        add(tabs);

        refreshTable();
        refreshDashboard();
    }

    static Image loadAppIcon() {
        return Toolkit.getDefaultToolkit().getImage(
                new File("assets" + File.separator + "icon.png").getAbsolutePath());
    }

    // ---------------------------------------------------------------- Dashboard

    private JPanel buildDashboard() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        grid.add(card("Total Enquiries", totalLbl, new Color(189, 210, 239)));
        grid.add(card("New", newLbl, new Color(186, 225, 245)));
        grid.add(card("Pending", pendingLbl, new Color(255, 222, 190)));
        grid.add(card("In Progress", progressLbl, new Color(223, 208, 242)));
        grid.add(card("Resolved", resolvedLbl, new Color(196, 234, 210)));
        grid.add(card("Follow-ups Due", dueLbl, new Color(250, 205, 205)));
        return grid;
    }

    private JPanel card(String title, JLabel value, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setForeground(new Color(55, 65, 85));
        t.setFont(t.getFont().deriveFont(Font.BOLD, 16f));
        value.setForeground(new Color(55, 65, 85));
        value.setFont(value.getFont().deriveFont(Font.BOLD, 44f));
        p.add(t, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private void refreshDashboard() {
        try {
            Map<String, Integer> c = dao.countByStatus();
            int total = c.values().stream().mapToInt(Integer::intValue).sum();
            totalLbl.setText(String.valueOf(total));
            newLbl.setText(String.valueOf(c.get("New")));
            pendingLbl.setText(String.valueOf(c.get("Pending")));
            progressLbl.setText(String.valueOf(c.get("In Progress")));
            resolvedLbl.setText(String.valueOf(c.get("Resolved")));
            dueLbl.setText(String.valueOf(dao.countFollowUpsDue()));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // ---------------------------------------------------------------- Enquiries tab

    private JPanel buildEnquiryPanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: search + filter
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        top.add(new JLabel("Search (name / email / ID):"));
        top.add(searchField);
        top.add(new JLabel("Status:"));
        top.add(filterBox);
        top.add(searchBtn);
        top.add(clearBtn);
        searchBtn.addActionListener(e -> refreshTable());
        searchField.addActionListener(e -> refreshTable());
        filterBox.addActionListener(e -> refreshTable());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            refreshTable();
        });

        // Centre: table
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: actions
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton exportBtn = new JButton("Export Report (CSV)");
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        exportBtn.addActionListener(e -> onExport());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) onEdit();
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(delBtn);
        bottom.add(exportBtn);

        root.add(top, BorderLayout.NORTH);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private void refreshTable() {
        try {
            current = dao.search(searchField.getText(), (String) filterBox.getSelectedItem());
            tableModel.setRowCount(0);
            for (Enquiry e : current) {
                tableModel.addRow(new Object[]{
                        e.getId(), e.getName(), e.getEmail(), e.getPhone(), e.getSubject(),
                        e.getStatus(),
                        e.getFollowUpDate() == null ? "" : e.getFollowUpDate().toString(),
                        e.getCreatedAt() == null ? "" : e.getCreatedAt().toString().substring(0, 16)
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private Enquiry selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enquiry first.");
            return null;
        }
        return current.get(row);
    }

    private void onAdd() {
        EnquiryDialog d = new EnquiryDialog(this, null);
        d.setVisible(true);
        if (d.isSaved()) {
            try {
                dao.add(d.getEnquiry());
                refreshTable();
                refreshDashboard();
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void onEdit() {
        Enquiry sel = selected();
        if (sel == null) return;
        EnquiryDialog d = new EnquiryDialog(this, sel);
        d.setVisible(true);
        if (d.isSaved()) {
            try {
                dao.update(d.getEnquiry());
                refreshTable();
                refreshDashboard();
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void onDelete() {
        Enquiry sel = selected();
        if (sel == null) return;
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete enquiry #" + sel.getId() + " from " + sel.getName() + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                dao.delete(sel.getId());
                refreshTable();
                refreshDashboard();
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    /** Exports the rows currently shown in the table to a CSV file. */
    private void onExport() {
        if (current.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to export.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("enquiry_report_" + LocalDate.now() + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter w = new PrintWriter(fc.getSelectedFile(), "UTF-8")) {
            w.println("ID,Name,Email,Phone,Subject,Description,Status,Follow-up,Created");
            for (Enquiry e : current) {
                w.println(String.join(",",
                        String.valueOf(e.getId()), csv(e.getName()), csv(e.getEmail()), csv(e.getPhone()),
                        csv(e.getSubject()), csv(e.getDescription()), csv(e.getStatus()),
                        e.getFollowUpDate() == null ? "" : e.getFollowUpDate().toString(),
                        e.getCreatedAt() == null ? "" : e.getCreatedAt().toString()));
            }
            JOptionPane.showMessageDialog(this, "Report saved to:\n" + fc.getSelectedFile().getAbsolutePath());
        } catch (IOException ex) {
            showError(ex);
        }
    }

    private String csv(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"").replace("\n", " ") + "\"";
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}