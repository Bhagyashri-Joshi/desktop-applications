# BMI Calculator

A lightweight desktop application built with **Java Swing** that calculates Body Mass Index (BMI) from a person's weight and height. It has a clean, minimal interface, supports both metric and imperial units, shows a colour-coded result with a category label, and can export results to a CSV file.

---

## Table of Contents

- [Features](#features)
- [Screens / Pages](#screens--pages)
- [How BMI Is Calculated](#how-bmi-is-calculated)
- [Tech Stack](#tech-stack)
- [Project Directory Structure](#project-directory-structure)
- [Architecture Overview](#architecture-overview)
- [Installation & Setup](#installation--setup)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)
- [Future Improvements](#future-improvements)
- [Disclaimer](#disclaimer)
- [Author](#author)

---

## Features

**General**
- Single-window, minimal and uncluttered interface
- Metric / Imperial toggle that updates the input labels instantly
- Resizable window with a minimum size
- Press **Enter** to calculate without reaching for the mouse
- Runs on Windows, macOS and Linux (uses the system look and feel)

**BMI Calculation**
- Metric input: weight in kilograms, height in centimetres
- Imperial input: weight in pounds, height in feet and inches (inches are optional)
- Result shown to one decimal place in large, easy-to-read text
- Category label: Underweight, Normal weight, Overweight or Obese
- Colour-coded result panel that changes with the category

**Export**
- Save the latest result to a CSV file with one click
- Results are appended, so repeated saves build up a log that opens directly in Excel or Google Sheets
- A header row is added automatically when the file is new
- Save button is enabled only after a result has been calculated

**Input Validation**
- Rejects empty, non-numeric, zero and negative values
- Rejects unrealistic heights and weights with a clear warning dialog instead of crashing

---

## Screens / Pages

| Screen | Description |
|--------|-------------|
| **Main window** | Unit toggle, weight and height fields, colour-coded result panel, and Calculate / Clear / Save buttons |
| **Warning dialog** | Appears when the input is invalid and explains what to fix |
| **Save dialog** | Standard file chooser to pick where the CSV log is stored |

---

## How BMI Is Calculated

| Units | Formula |
|-------|---------|
| Metric | `BMI = weight (kg) / height (m)²` |
| Imperial | `BMI = 703 × weight (lb) / height (in)²` |

**Categories and colours** (standard WHO cut-offs)

| BMI | Category | Colour |
|-----|----------|--------|
| Below 18.5 | Underweight | Blue |
| 18.5 to 24.9 | Normal weight | Green |
| 25.0 to 29.9 | Overweight | Amber |
| 30.0 and above | Obese | Red |

**Example:** 70 kg and 175 cm gives `70 / (1.75 × 1.75) = 22.9`, which is **Normal weight**.

**Accepted input ranges**

| Units | Weight | Height |
|-------|--------|--------|
| Metric | Greater than 0, up to 635 kg | 50 to 272 cm |
| Imperial | Greater than 0, up to 1400 lb | About 20 to 107 inches in total (feet plus inches) |

**CSV format**

```
Date,Units,Weight,Height,BMI,Category
2026-11-15 10:42:07,Metric,70 kg,175 cm,22.9,Normal weight
2026-11-15 10:43:31,Imperial,154 lb,5 ft 9 in,22.7,Normal weight
```

---

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java (8 or newer) |
| GUI toolkit | Java Swing (`javax.swing`) and AWT |
| Date and time | `java.time` (`LocalDateTime`, `DateTimeFormatter`) |
| File export | `java.io` (`FileWriter`, `PrintWriter`) |
| Layouts | `BorderLayout`, `GridBagLayout`, `GridLayout`, `FlowLayout` |
| Look and feel | System native look and feel |
| Build | Plain `javac` (no external dependencies) |

---

## Project Directory Structure

```
bmi-calculator/
├── BmiCalculator.java   # Entire application: UI, validation, calculation and CSV export
└── README.md
```

After compiling, `javac` also generates `BmiCalculator.class` in the same folder.

---

## Architecture Overview

The whole application lives in a single class, `BmiCalculator`, which extends `JFrame`. The code is split into clearly marked sections:

- **UI** (`buildUi`, `addRow`): builds the toggle, form, result panel and buttons using nested panels and layout managers.
- **Events** (`wireEvents`, `onUnitChange`, `updateUnitLabels`): connects buttons and radio buttons to their handlers and swaps labels and the inches field when the unit system changes.
- **Logic** (`calculate`, `parsePositive`, `parseNonNegative`, `categorize`, `colorFor`): validates input, computes BMI, decides the category and picks the result colour.
- **Export** (`saveResult`): opens a file chooser and appends the latest result to a CSV file.
- **Main** (`main`): sets the system look and feel and opens the window on the Swing event thread via `SwingUtilities.invokeLater`.

---

## Installation & Setup

### Prerequisites

- **JDK 8 or newer** (JDK 17 or later recommended)
- Check that Java is installed:

```bash
java -version
javac -version
```

### 1. Get the project

Clone the repository or copy the project folder to your machine:

```bash
git clone <your-repository-url>
cd bmi-calculator
```

### 2. Compile

Run this from the folder that contains `BmiCalculator.java`:

```bash
javac BmiCalculator.java
```

### 3. Run

```bash
java BmiCalculator
```

### Running from an IDE

1. Create a new Java project (IntelliJ IDEA, Eclipse, NetBeans or VS Code).
2. Add `BmiCalculator.java` to the source folder.
3. Run the `BmiCalculator` class.

### Optional: build a runnable JAR

```bash
javac BmiCalculator.java
jar cfe BmiCalculator.jar BmiCalculator BmiCalculator.class
java -jar BmiCalculator.jar
```

---

## Usage

1. Launch the app. **Metric** is selected by default.
2. Choose **Metric** or **Imperial** at the top. Switching clears the fields and updates the labels.
3. Enter your weight and height. In Imperial mode, enter feet in the height field and inches in the extra field below it.
4. Click **Calculate** (or press **Enter**). The BMI value and category appear in the coloured panel.
5. Click **Save** to append the result to a CSV file of your choice.
6. Click **Clear** to reset the fields and start again.

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `javac` is not recognised | Install a JDK and add its `bin` folder to your `PATH` |
| `Could not find or load main class BmiCalculator` | Run `java BmiCalculator` from the same folder where the `.class` file was generated |
| `class BmiCalculator is public, should be declared in a file named BmiCalculator.java` | Make sure the file name matches the class name exactly, including capital letters |
| Warning dialog: "must be a valid number" | Use digits and an optional decimal point only, with no units or commas |
| Save button is greyed out | Calculate a result first; Save is enabled only when there is a result |
| Window looks different on each OS | The app uses the native look and feel, so button and field styling follows your system |

---

## Future Improvements

- BMI scale bar showing where the result falls on the range
- History table inside the app instead of only a CSV file
- Dark mode and a theme toggle
- Convert values automatically when switching between Metric and Imperial
- Age and gender based guidance, including a healthy weight range for the entered height
- Keyboard shortcuts for Clear and Save
- Export to PDF or text report

---

## Disclaimer

BMI is a general screening measure and not a medical diagnosis. It does not account for muscle mass, age, sex or body composition. Consult a healthcare professional for health advice.

---

## Author

**Bhagyashri Joshi**