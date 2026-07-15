Here is a highly professional, comprehensive, and clean `README.md` file customized specifically for your GitHub repository. It perfectly balances technical descriptions with easy-to-read formatting.

---

# 🌊 Automated Breakwater Optimiser

An interactive engineering simulator and design-space optimization desktop tool built with **Java 21** and **JavaFX**. The application integrates hydrodynamic physics (**Hudson Formula**) with real-world constructability logistics (**crane lifting capacity limit**) to evaluate rubble-mound breakwater cross-sections in real time.

---

## 🚀 Key Features

* **Three-Tier Architecture:** Clean separation of concerns between data (`BreakwaterDesign`), calculation logic (`HudsonCalculator`), optimization loop (`Optimizer`), and presentation layers (`MainApplication`).
* **Physics-Logistics Optimization Core:** Evaluates 300 unique candidate slope configurations ($\cot\theta$ from $1.0$ to $4.0$ in $0.01$ increments) frame-by-frame to isolate the layout with the absolute minimum material footprint.
* **Logistics Filter Safeguard:** Automatically screens and rejects any design option requiring concrete armor units heavier than a **25-metric-ton crane lifting limit**.
* **60 FPS Real-Time Update Cycle:** Powered by a hardware-accelerated JavaFX `AnimationTimer` render thread with an ultra-low latency of **~5 ms per frame**.
* **Dynamic Visualization Canvas:** Renders structural 2D vector profiles, moving wave animations, and instant text telemetry readouts. Includes a bold full-screen red **"CRANE OVERLOAD"** warning indicator if environmental conditions become unbuildable.

---

## 🛠️ Tech Stack & Prerequisites

* **Language:** Java 21 (LTS)
* **GUI Library:** JavaFX 21+
* **Build Tool:** Maven 3+
* **Environment:** Compatible with any standard modern IDE supporting Java desktop apps (e.g., IntelliJ IDEA, Eclipse, NetBeans).

---

## 📂 Project Architecture Layout

```
src/main/java/
└── com/breakwater/
    ├── model/
    │   └── BreakwaterDesign.java    # Enforces encapsulation, properties, and geometric bounds
    ├── physics/
    │   └── HudsonCalculator.java   # Implements empirical hydrodynamic equations
    ├── optimizer/
    │   └── Optimizer.java          # Sweeps slopes, filters weights, and minimizes area
    └── ui/
        └── MainApplication.java    # Drives UI components, sliders, and the 60FPS thread

```

---

## 📐 Governing Mathematics

The core stability logic calculates the minimum stable individual weight ($W$) for concrete armor blocks using the classic empirical **Hudson Formula (1959)
### Physical Bounds and Constants:

* **$H$:** Local Wave Height ($1.0\text{ m}$ to $10.0\text{ m}$ interactive slider input).
* **$\cot\theta$:** Layout profile slope angle ($1.0$ to $4.0$ matrix options).
* **$K_D$:** Primary armor stability constant (set to a constant $3.5$ for concrete armor blocks).
* **$S_R$:** Specific gravity of concrete relative to seawater ($2.4$).
* **$W_R$:** Unit weight density of concrete ($2.4\text{ t/m}^3$).

Additionally, the software automatically models key civil standards:

* **Structural Height ($h$):** $d \times 1.2$ (where $d$ is water depth) to establish appropriate freeboard.
* **Crest Width ($b_{\text{Top}}$):** $H \times 1.5$ to ensure a secure roadway footprint for construction machinery.

---

## ⚙️ How the Optimization Logic Works

Every frame tick triggered by adjusting input parameters:

1. The engine sweeps the design space testing structural slopes from 1:1 up to 1:4.
2. For each structural option, it calculates required block weight ($W$). If $W > 25.0$ tons, that iteration is instantly discarded.
3. For remaining valid options, it computes the cross-sectional trapezoidal area ($\text{Area} = b_{\text{Top}} \cdot h + \cot\theta \cdot h^2$).
4. The engine returns the design that achieves the **minimum material volume footprint**. If no slope can satisfy the crane lifting threshold, it returns `null` to notify the UI to display the critical safety alert banner.

---

## 📦 Building and Running the Application

Ensure you have Java 21 and Maven configured on your system environment variables path, then execute the following terminal commands:

### 1. Clone the Repository

```bash
git clone https://github.com/luckysinhaorg-jpg/breakwater-optimiser.git
cd breakwater-optimiser

```

### 2. Compile and Package

```bash
mvn clean package

```

### 3. Launch the Application Window

```bash
mvn javafx:run

```

---

## 📊 Core Simulation Operational Zones

Empirical testing across 50+ wave scenarios successfully verified three distinct structural design behaviors:

* **Plateau Zone ($H \le 4.5\text{ m}$):** Required block weights stay well within crane limits. The engine locks at the steepest possible $1:1$ slope to minimize material consumption.
* **Flattening Region ($4.5\text{ m} < H < 7.8\text{ m}$):** Keeping a steep 1:1 slope would break the crane threshold. The engine automatically flattens the structural face (e.g., $1:2.0$ or $1:3.92$) to dissipate wave energy and cap block sizes under 25 tons.
* **Break Point Zone ($H \ge 7.8\text{ m}$):** Storm waves are severe enough that no allowable slope keeps weights under 25 tons. The system removes the breakwater vector rendering and displays the red warning banner, advising engineers to swap construction equipment or adjust site selections.

---

## 👥 Project Credits

Developed as part of the **Basics of Java Programming (CS125BIC) — Experiential Learning Component** at *RV College of Engineering*.

* **Student Project Team:** Ananya S Shiggaon & Lucky Sinha
* **Academic Project Advisor:** Dr. Manonmani S (Associate Professor, CSE Dept)
