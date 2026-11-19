import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Data access layer: all SQL lives here (JDBC with PreparedStatements). */
public class EnquiryDAO {

    public void add(Enquiry e) throws SQLException {
        String sql = "INSERT INTO enquiries (name, email, phone, subject, description, status, follow_up_date) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getSubject());
            ps.setString(5, e.getDescription());
            ps.setString(6, e.getStatus());
            ps.setDate(7, e.getFollowUpDate());
            ps.executeUpdate();
        }
    }

    public void update(Enquiry e) throws SQLException {
        String sql = "UPDATE enquiries SET name=?, email=?, phone=?, subject=?, description=?, status=?, follow_up_date=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getSubject());
            ps.setString(5, e.getDescription());
            ps.setString(6, e.getStatus());
            ps.setDate(7, e.getFollowUpDate());
            ps.setInt(8, e.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM enquiries WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Search by name, email or enquiry ID. Pass "All" (or null) as status for no status filter. */
    public List<Enquiry> search(String keyword, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM enquiries WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR email LIKE ? OR CAST(id AS CHAR) = ?)");
            String k = keyword.trim();
            params.add("%" + k + "%");
            params.add("%" + k + "%");
            params.add(k);
        }
        if (status != null && !status.equals("All")) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY id DESC");

        List<Enquiry> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Enquiry> getAll() throws SQLException {
        return search(null, "All");
    }

    /** Counts per status, in the order of Enquiry.STATUSES. */
    public Map<String, Integer> countByStatus() throws SQLException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String s : Enquiry.STATUSES) counts.put(s, 0);
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT status, COUNT(*) FROM enquiries GROUP BY status")) {
            while (rs.next()) counts.put(rs.getString(1), rs.getInt(2));
        }
        return counts;
    }

    /** Unresolved enquiries whose follow-up date is today or earlier. */
    public int countFollowUpsDue() throws SQLException {
        String sql = "SELECT COUNT(*) FROM enquiries WHERE status <> 'Resolved' AND follow_up_date IS NOT NULL AND follow_up_date <= CURDATE()";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Enquiry map(ResultSet rs) throws SQLException {
        Enquiry e = new Enquiry();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setEmail(rs.getString("email"));
        e.setPhone(rs.getString("phone"));
        e.setSubject(rs.getString("subject"));
        e.setDescription(rs.getString("description"));
        e.setStatus(rs.getString("status"));
        e.setFollowUpDate(rs.getDate("follow_up_date"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        return e;
    }
}