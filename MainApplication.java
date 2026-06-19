package com.breakwater;

import com.breakwater.model.BreakwaterDesign;
import com.breakwater.optimization.Optimizer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private double waveOffset = 0;
    private BreakwaterDesign design;

    private Slider waveSlider;
    private Slider depthSlider;

    private Label slopeLabel;
    private Label weightLabel;
    private Label volumeLabel;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(920, 620);
        GraphicsContext g = canvas.getGraphicsContext2D();

        // ── Sliders ──────────────────────────────────────────
        waveSlider = new Slider(1.0, 8.0, 2.5);
        waveSlider.setShowTickMarks(true);
        waveSlider.setShowTickLabels(true);
        waveSlider.setMajorTickUnit(1.0);
        waveSlider.setPrefWidth(220);

        depthSlider = new Slider(3.0, 15.0, 12.0);
        depthSlider.setShowTickMarks(true);
        depthSlider.setShowTickLabels(true);
        depthSlider.setMajorTickUnit(3.0);
        depthSlider.setPrefWidth(220);

        // ── Telemetry Labels ─────────────────────────────────
        slopeLabel  = new Label("Slope:  —");
        weightLabel = new Label("Armor Weight:  —");
        volumeLabel = new Label("Concrete Volume:  —");
        statusLabel = new Label("Status:  —");

        for (Label l : new Label[]{slopeLabel, weightLabel, volumeLabel, statusLabel}) {
            l.setFont(Font.font("Consolas", 13));
            l.setTextFill(Color.LIGHTCYAN);
        }
        statusLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 13));

        // ── Left Panel ───────────────────────────────────────
        Label title = makeLabel("SITE CONDITIONS", 15, true);
        Label waveTitle = makeLabel("Wave Height (H)  meters", 12, false);
        Label depthTitle = makeLabel("Water Depth (d)  meters", 12, false);
        Label specsTitle = makeLabel("OPTIMIZATION SPECS", 14, true);

        VBox leftPanel = new VBox(10,
                title,
                waveTitle,  waveSlider,
                depthTitle, depthSlider,
                specsTitle,
                slopeLabel, weightLabel, volumeLabel, statusLabel
        );
        leftPanel.setPadding(new Insets(20));
        leftPanel.setPrefWidth(260);
        leftPanel.setStyle("-fx-background-color: #1a2a3a;");

        // ── Layout ───────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        root.setCenter(canvas);

        // ── Animation Loop ───────────────────────────────────
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                waveOffset += 0.04;

                double H = waveSlider.getValue();
                double d = depthSlider.getValue();
                design = Optimizer.optimize(H, d);

                updateTelemetry();
                drawScene(g, canvas.getWidth(), canvas.getHeight(), H, d);
            }
        }.start();

        stage.setTitle("Breakwater Optimization System");
        stage.setScene(new Scene(root, 1200, 640));
        stage.setResizable(false);
        stage.show();
    }

    // ── Telemetry Update ─────────────────────────────────────
    private void updateTelemetry() {
        if (design == null) {
            slopeLabel.setText("Slope:  INFEASIBLE");
            weightLabel.setText("Armor Weight:  OVERLOAD");
            volumeLabel.setText("Volume:  OVERLOAD");
            statusLabel.setText("CRANE OVERLOAD");
            statusLabel.setTextFill(Color.RED);
        } else {
            slopeLabel.setText(String.format("Slope:  1 : %.2f", design.getSlope()));
            weightLabel.setText(String.format("Armor Weight:  %.2f tons", design.getArmorWeight()));
            volumeLabel.setText(String.format("Volume:  %.1f m³", design.getConcreteVolume()));
            statusLabel.setText(" OPTIMAL & SAFE");
            statusLabel.setTextFill(Color.LIGHTGREEN);
        }
    }

    // ── Main Draw ────────────────────────────────────────────
    private void drawScene(GraphicsContext g, double W, double H,
                           double waveH, double waterD) {

        // Sky gradient
        LinearGradient sky = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#b8d4e8")),
                new Stop(1.0, Color.web("#ddeeff")));
        g.setFill(sky);
        g.fillRect(0, 0, W, H);

        double horizonY = H * 0.58;

        // Seabed (sandy)
        g.setFill(Color.web("#c2a96e"));
        g.fillRect(0, horizonY, W, H - horizonY);

        // Water layer
        double scale     = 22.0;
        double waterPx   = waterD * scale * 0.5;
        double waterTopY = horizonY - waterPx;

        LinearGradient water = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#1a5276", 0.85)),
                new Stop(1.0, Color.web("#0b2d44", 0.95)));
        g.setFill(water);
        g.fillRect(0, waterTopY, W, horizonY - waterTopY);

        // Animated waves on surface
        g.setStroke(Color.web("#ffffff", 0.7));
        g.setLineWidth(2.0);
        for (int wx = 0; wx < W; wx++) {
            double wy = waterTopY
                    + Math.sin(wx * 0.025 + waveOffset) * (waveH * 1.8)
                    + Math.sin(wx * 0.05  + waveOffset * 1.3) * (waveH * 0.8);
            g.strokeLine(wx, wy, wx + 1, wy);
        }

        // Breakwater
        if (design != null) {
            drawBreakwater(g, W, horizonY, waterTopY, waveH, waterD);
        } else {
            // Infeasible alert
            g.setFill(Color.web("#ff4444", 0.85));
            g.fillRoundRect(W/2 - 260, H/2 - 30, 520, 60, 12, 12);
            g.setFill(Color.WHITE);
            g.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            g.fillText(" DESIGN INFEASIBLE REDUCE WAVE HEIGHT", W/2 - 240, H/2 + 7);
        }

        // Water depth indicator (right side — like Ananya's)
        drawDepthIndicator(g, W, waterTopY, horizonY, waterD);

        // Wave height label (left side)
        drawWaveLabel(g, waterTopY, waveH);
    }

    // ── Breakwater Drawing ───────────────────────────────────
    private void drawBreakwater(GraphicsContext g, double W, double horizonY,
                                double waterTopY, double waveH, double waterD) {

        double slope      = design.getSlope();
        double scale      = 22.0;
        double crestH_px  = waterD * scale * 0.6;   // height above seabed
        double crestW_px  = waveH  * scale * 0.8;   // crest width

        double centerX  = W * 0.52;
        double crestTopY = horizonY - crestH_px;

        // Half base width = crest/2 + slope * height
        double halfCrest = crestW_px / 2.0;
        double halfBase  = halfCrest + slope * crestH_px;

        double x1 = centerX - halfBase;   // bottom left
        double x2 = centerX - halfCrest;  // top left
        double x3 = centerX + halfCrest;  // top right
        double x4 = centerX + halfBase;   // bottom right

        // Main body gradient (dark grey → lighter grey)
        LinearGradient body = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#5a5a5a")),
                new Stop(0.5, Color.web("#888888")),
                new Stop(1.0, Color.web("#5a5a5a")));
        g.setFill(body);
        g.fillPolygon(
                new double[]{x1, x2, x3, x4},
                new double[]{horizonY, crestTopY, crestTopY, horizonY},
                4);

        // Outline
        g.setStroke(Color.web("#333333"));
        g.setLineWidth(2);
        g.strokePolygon(
                new double[]{x1, x2, x3, x4},
                new double[]{horizonY, crestTopY, crestTopY, horizonY},
                4);

        // Armor blocks on left slope
        drawArmorBlocks(g, x1, horizonY, x2, crestTopY);
        // Armor blocks on right slope
        drawArmorBlocks(g, x4, horizonY, x3, crestTopY);
        // Armor blocks on crest top
        drawCrestBlocks(g, x2, x3, crestTopY);

        // SLOPE label box on crest
        String slopeText = "SLOPE 1.0:" + String.format("%.2f", slope);
        double labelX = centerX - 55;
        double labelY = crestTopY - 30;
        g.setFill(Color.web("#1a1a2e", 0.85));
        g.fillRoundRect(labelX, labelY, 120, 24, 8, 8);
        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        g.fillText(slopeText, labelX + 8, labelY + 16);

        // "Incoming Waves" label left
        g.setFill(Color.web("#ddeeff", 0.9));
        g.setFont(Font.font("Arial", 13));
        g.fillText("Incoming Waves →", 30, waterTopY - 20);

        // "Protected Harbor" label right
        g.fillText("Protected Harbor", x4 + 30, waterTopY - 20);
    }

    // ── Armor Blocks on Slope ────────────────────────────────
    private void drawArmorBlocks(GraphicsContext g,
                                 double bx, double by,
                                 double tx, double ty) {
        int steps = 7;
        for (int i = 1; i < steps; i++) {
            double t  = (double) i / steps;
            double cx = bx + (tx - bx) * t;
            double cy = by + (ty - by) * t;
            double sz = 14 - i * 0.5;

            // perpendicular offset to sit ON the slope surface
            double dx = -(ty - by);
            double dy =  (tx - bx);
            double len = Math.sqrt(dx*dx + dy*dy);
            double ox = dx/len * 8;
            double oy = dy/len * 8;

            g.setFill(Color.web("#9e9e9e"));
            g.fillRect(cx + ox - sz/2, cy + oy - sz/2, sz, sz);
            g.setStroke(Color.web("#555555"));
            g.setLineWidth(1);
            g.strokeRect(cx + ox - sz/2, cy + oy - sz/2, sz, sz);
        }
    }

    // ── Armor Blocks on Crest ────────────────────────────────
    private void drawCrestBlocks(GraphicsContext g,
                                 double x2, double x3, double crestTopY) {
        double crestW = x3 - x2;
        int blocks = (int)(crestW / 18);
        for (int i = 0; i <= blocks; i++) {
            double bx = x2 + i * (crestW / blocks) - 7;
            g.setFill(Color.web("#9e9e9e"));
            g.fillRect(bx, crestTopY - 8, 14, 14);
            g.setStroke(Color.web("#555555"));
            g.setLineWidth(1);
            g.strokeRect(bx, crestTopY - 8, 14, 14);
        }
    }

    // ── Water Depth Indicator (right side) ───────────────────
    private void drawDepthIndicator(GraphicsContext g, double W,
                                    double waterTopY, double horizonY,
                                    double waterD) {
        double rx = W - 55;
        g.setStroke(Color.web("#aaddff"));
        g.setLineWidth(1.5);
        g.strokeLine(rx, waterTopY, rx, horizonY);
        g.strokeLine(rx - 5, waterTopY, rx + 5, waterTopY);
        g.strokeLine(rx - 5, horizonY,  rx + 5, horizonY);

        // Label box
        String txt = "d = " + String.format("%.1f", waterD) + " m";
        double midY = (waterTopY + horizonY) / 2;
        g.setFill(Color.web("#1a2a3a", 0.85));
        g.fillRoundRect(rx - 48, midY - 12, 90, 24, 8, 8);
        g.setFill(Color.web("#aaddff"));
        g.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        g.fillText(txt, rx - 38, midY + 5);
    }

    // ── Wave Height Label (left side) ─────────────────────────
    private void drawWaveLabel(GraphicsContext g, double waterTopY, double waveH) {
        String txt = "H = " + String.format("%.1f", waveH) + " m";
        g.setFill(Color.web("#1a2a3a", 0.85));
        g.fillRoundRect(18, waterTopY - 42, 100, 24, 8, 8);
        g.setFill(Color.web("#aaddff"));
        g.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        g.fillText(txt, 26, waterTopY - 25);
    }

    // ── Helper ────────────────────────────────────────────────
    private Label makeLabel(String text, int size, boolean bold) {
        Label l = new Label(text);
        l.setFont(bold
                ? Font.font("Arial", FontWeight.BOLD, size)
                : Font.font("Arial", size));
        l.setTextFill(Color.WHITE);
        return l;
    }

    public static void main(String[] args) {
        launch(args);
    }
}