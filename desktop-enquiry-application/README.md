# Enquiry Management System

A desktop application (a small mini-CRM) for recording and managing customer enquiries. Built with **Java Swing**, **MySQL** and **JDBC**. A college, training institute or small company can use it to register enquiries, track their status, schedule follow-ups and generate reports.

## Table of Contents

* [Features](#features)
* [Screens / Pages](#screens--pages)
* [How Status Tracking & Follow-ups Work](#how-status-tracking--follow-ups-work)
* [Tech Stack](#tech-stack)
* [Project Directory Structure](#project-directory-structure)
* [Architecture Overview](#architecture-overview)
* [Installation & Setup](#installation--setup)
* [Usage](#usage)
* [Troubleshooting](#troubleshooting)
* [Future Improvements](#future-improvements)
* [Disclaimer](#disclaimer)
* [Author](#author)

---

## Features

| Feature | Description |
|---|---|
| Add Enquiry | Record name, email, phone, subject and description |
| View Enquiries | All records shown in a sortable-looking table, newest first |
| Update Enquiry | Edit any existing record (double-click a row or use **Edit**) |
| Delete Enquiry | Remove a record, with a confirmation prompt |
| Status Tracking | New, Pending, In Progress, Resolved |
| Follow-up Scheduling | Store the next follow-up date for each enquiry |
| Search | Search by name, email or enquiry ID |
| Filtering | Filter the list by status |
| Dashboard | Totals per status plus a "Follow-ups Due" counter |
| Reports | Export the current (filtered) list to a CSV file |
| Input Validation | Required fields, email format, phone format and date format are checked |
| Secure Config | Database credentials are kept in a git-ignored `.env` file |

## Screens / Pages

1. **Dashboard tab** - six pastel cards: Total Enquiries, New, Pending, In Progress, Resolved and Follow-ups Due. It refreshes whenever you switch back to the tab or change data.
2. **Enquiries tab** - search box, status filter, the enquiries table, and the buttons **Add**, **Edit**, **Delete** and **Export Report (CSV)**.
3. **Add / Edit Enquiry dialog** - a form with validation, used for both creating and updating records.
4. **Export dialog** - a standard save-file dialog for the CSV report.


## How Status Tracking & Follow-ups Work

**Status lifecycle**

```
New  ->  Pending  ->  In Progress  ->  Resolved
```

Every enquiry starts as **New**. You can move it to any status from the Edit dialog. The dashboard counts enquiries per status using a `GROUP BY status` query.

**Follow-ups Due**

An enquiry is counted as "due" when all of these are true:

* its status is **not** Resolved,
* it has a follow-up date, and
* that date is **today or earlier** (so overdue follow-ups are included).

Follow-up dates are entered in `yyyy-MM-dd` format, for example `2026-12-01`. Leave the field blank if no follow-up is needed.

**Search rules**

The search box matches a partial **name** or **email**, or an exact **enquiry ID**. It combines with the status filter, so you can search "priya" within only "Pending" enquiries.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java (JDK 11 or newer) |
| GUI | Java Swing (Nimbus look and feel) |
| Database | MySQL 8.x |
| Connectivity | JDBC with MySQL Connector/J |
| IDE | Visual Studio Code (Extension Pack for Java) |
| Version Control | Git and GitHub |

## Project Directory Structure

```
desktop-enquiry-application/
`--assets/
    `--icon.png
|-- Main.java             # Entry point: sets look and feel, checks DB, opens the window
|-- MainFrame.java        # Main window: Dashboard tab + Enquiries tab
|-- EnquiryDialog.java    # Add / Edit form with validation
|-- Enquiry.java          # Model class (one enquiry)
|-- EnquiryDAO.java       # Data access layer: all SQL queries
|-- DBConnection.java     # JDBC connection, reads config from .env
|-- schema.sql            # Creates the database, table and sample data
|-- .env.example          # Template for your local credentials (safe to commit)
|-- .env                  # Your real credentials (git-ignored, create it yourself)
|-- .gitignore
|-- README.md
`-- lib/
    `-- mysql-connector-j-x.y.z.jar   # Download separately (git-ignored)
```

## Architecture Overview

The application follows a simple layered design:

```
+---------------------------+
|  UI layer                 |  MainFrame, EnquiryDialog (Swing)
+-------------+-------------+
              |
+-------------v-------------+
|  Data access layer        |  EnquiryDAO (PreparedStatements)
+-------------+-------------+
              |
+-------------v-------------+
|  Connection layer         |  DBConnection (JDBC, .env config)
+-------------+-------------+
              |
+-------------v-------------+
|  MySQL database           |  enquiry_db.enquiries
+---------------------------+

Model: Enquiry (plain Java object passed between the layers)
```

* **Model** - `Enquiry` holds the fields of one record.
* **DAO** - `EnquiryDAO` is the only class that contains SQL. It uses `PreparedStatement` everywhere, which prevents SQL injection.
* **UI** - `MainFrame` and `EnquiryDialog` call the DAO and never talk to the database directly.
* **Configuration** - `DBConnection` reads `DB_URL`, `DB_USER` and `DB_PASS` from environment variables, then from `.env`, then from defaults.

**Database table: `enquiries`**

| Column | Type | Notes |
|---|---|---|
| id | INT, auto increment | Primary key |
| name | VARCHAR(100) | Required |
| email | VARCHAR(100) | Required |
| phone | VARCHAR(20) | Optional |
| subject | VARCHAR(150) | Required |
| description | TEXT | Optional |
| status | ENUM | New, Pending, In Progress, Resolved |
| follow_up_date | DATE | Optional |
| created_at | TIMESTAMP | Set automatically |

## Installation & Setup

**Prerequisites**

* JDK 11 or newer
* MySQL Server 8.x (and MySQL Workbench or the `mysql` command line)
* Visual Studio Code with the "Extension Pack for Java" (or any Java IDE)
* Git

**1. Clone the repository**

```bash
git clone https://github.com/Bhagyashri-Joshi/desktop-applications.git
cd desktop-applications/desktop-enquiry-application
```

**2. Create the database**

Run `schema.sql` in MySQL Workbench, or from the terminal:

```bash
mysql -u root -p < schema.sql
```

This creates `enquiry_db`, the `enquiries` table and three sample rows.

**3. Add the MySQL JDBC driver**

1. Download "Platform Independent" Connector/J from https://dev.mysql.com/downloads/connector/j/
2. Extract the ZIP.
3. Copy the `mysql-connector-j-x.y.z.jar` file **directly into** a `lib/` folder in the project (not the extracted folder, just the `.jar`).

**4. Configure your credentials**

```bash
copy .env.example .env        # Windows (PowerShell: Copy-Item .env.example .env)
cp .env.example .env          # Linux / macOS
```

Open `.env` and set your MySQL password:

```
DB_URL=jdbc:mysql://localhost:3306/enquiry_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASS=your_mysql_password_here
```

**5. Compile and run**

Windows (PowerShell):

```powershell
javac -cp "lib/*" *.java
java -cp ".;lib/*" Main
```

Linux / macOS:

```bash
javac -cp "lib/*" *.java
java -cp ".:lib/*" Main
```

Always run from the project folder, because `.env` is read from the current directory.

**Running from VS Code**

Open the folder, make sure the JAR is listed under **Java Projects -> Referenced Libraries** (or add `"java.project.referencedLibraries": ["lib/**/*.jar"]` to `.vscode/settings.json`), then open `Main.java` and click **Run**.

## Usage

1. **Dashboard** - open the app to see counts for each status and how many follow-ups are due.
2. **Add an enquiry** - go to the **Enquiries** tab, click **Add**, fill in the form (Name, Email and Subject are required) and click **Save**.
3. **Edit** - select a row and click **Edit**, or double-click the row. Change the status or follow-up date as work progresses.
4. **Delete** - select a row, click **Delete** and confirm.
5. **Search and filter** - type a name, email or ID, pick a status, then press Enter or click **Search**. Click **Clear** to reset.
6. **Export a report** - filter the list the way you want it, click **Export Report (CSV)** and choose where to save. The file opens in Excel or Google Sheets.

## Troubleshooting

| Problem | Cause and fix |
|---|---|
| `No suitable driver found for jdbc:mysql...` | The Connector/J `.jar` is missing from the classpath. Put the `.jar` file directly inside `lib/` and run with `-cp ".;lib/*"` (or add it under Referenced Libraries in VS Code). |
| `Access denied for user 'root'@'localhost'` | Wrong password. Check `DB_PASS` in `.env`, and that `.env` is in the folder you run from. |
| `Unknown database 'enquiry_db'` | `schema.sql` has not been run. Run it in MySQL first. |
| `Communications link failure` | MySQL server is not running or the port is not 3306. Start MySQL and check `DB_URL`. |
| `.env` changes have no effect | The app reads `.env` from the current working directory. Run from the project folder, and make sure the file is named exactly `.env` (not `.env.txt`). |
| Class not found: `Main` | Compile first with `javac -cp "lib/*" *.java`, and run from the folder containing the `.class` files. |
| Date error when saving | Follow-up dates must be `yyyy-MM-dd`, for example `2026-12-01`. |
| Password committed to GitHub by mistake | Change your MySQL password, keep it only in `.env`, and run `git rm --cached .env` if the file was ever tracked. |

## Future Improvements

* Colour-coded status in the table rows
* Date picker for the follow-up field
* Table sorting by column and pagination for large data
* User login with roles (admin / staff)
* Email or desktop reminders for due follow-ups
* Enquiry history / notes timeline per record
* Charts on the dashboard (status distribution, enquiries per month)
* Export to PDF and Excel
* Split into packages (`model`, `dao`, `ui`, `db`) and build with Maven or Gradle
* Unit tests for the DAO layer

## Disclaimer

This project was built for learning and internship purposes. It is not hardened for production use: there is no authentication, no encryption of stored data, and the database connection uses the default local setup. Do not store sensitive personal data in it, and never commit your `.env` file.

## Author

**Bhagyashri Joshi**
