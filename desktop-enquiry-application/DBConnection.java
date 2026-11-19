import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties ENV = loadEnvFile();

    private static final String URL  = get("DB_URL",
            "jdbc:mysql://localhost:3306/enquiry_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    private static final String USER = get("DB_USER", "root");
    private static final String PASS = get("DB_PASS", "");

    private static Properties loadEnvFile() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(".env")) {
            p.load(in);
        } catch (IOException ignored) {
            // no .env file: fall back to environment variables / defaults
        }
        return p;
    }

    private static String get(String key, String fallback) {
        String v = System.getenv(key);
        if (v == null || v.isEmpty()) v = ENV.getProperty(key);
        return (v == null || v.trim().isEmpty()) ? fallback : v.trim();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}