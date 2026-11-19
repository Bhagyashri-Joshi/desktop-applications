CREATE DATABASE IF NOT EXISTS enquiry_db;
USE enquiry_db;

CREATE TABLE IF NOT EXISTS enquiries (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    subject         VARCHAR(150) NOT NULL,
    description     TEXT,
    status          ENUM('New','Pending','In Progress','Resolved') NOT NULL DEFAULT 'New',
    follow_up_date  DATE NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Optional sample data
INSERT INTO enquiries (name, email, phone, subject, description, status, follow_up_date) VALUES
('Aarav J', 'aarav@example.com', '987654321', 'Course fees', 'Asked about the Java course fee structure.', 'New', CURDATE() + INTERVAL 2 DAY),
('Priti P', 'pritipatil@example.com', '912345678', 'Admission process', 'Wants documents checklist for admission.', 'Pending', CURDATE()),
('Rohan S', 'rohan@example.com', '998877665', 'Batch timings', 'Needs weekend batch details.', 'Resolved', NULL);