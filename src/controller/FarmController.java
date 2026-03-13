package controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import model.*;

public class FarmController {
    @FXML private GridPane farmGrid;
    @FXML private VBox animalBox;
    private GameState state;

    private static final String[] STAGE_ICONS = { "🌱", "🌿", "🌾", "✅" };
    private static final String[] STAGE_BG = {
        "#3d2a0e",
        "#2d3d0e",
        "#3a4d10",
        "#4a6d10",
    };

    private StackPane[] cellRefs = new StackPane[64];

    public void init(GameState state) {
        this.state = state;
        setupGrid();

        Timeline loop = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            for (Crop c : state.plots) if (c != null) c.update();
            for (Animal a : state.myAnimals) a.update();
            updateUI();
        }));
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();
    }

    private void setupGrid() {
        farmGrid.getChildren().clear();
        for (int i = 0; i < 64; i++) {
            StackPane cell = new StackPane();
            cell.setPrefSize(58, 58);
            int idx = i;

            cell.setOnMouseEntered(e -> {
                if (state.unlocked[idx])
                    cell.setStyle(getCellStyle(idx) + "-fx-border-color: #f5c842; -fx-border-width: 2;");
            });
            cell.setOnMouseExited(e -> updateSingleCell(idx));
            cell.setOnMouseClicked(e -> {
                if (!state.unlocked[idx]) unlock(idx);
                else                      handlePlot(idx);
                updateSingleCell(idx);
            });

            cellRefs[i] = cell;
            farmGrid.add(cell, i % 8, i / 8);
        }
        updateUI();
    }

    private String getCellStyle(int i) {
        Crop crop = (state != null) ? state.plots[i] : null;
        String bg = (!state.unlocked[i]) ? "#1a1208"
                  : (crop != null)       ? STAGE_BG[crop.getGrowthStage()]
                  : "#4a2f0e";
        return "-fx-background-color: " + bg + "; -fx-border-color: #5a3a1a; -fx-border-width: 1;"
             + "-fx-background-radius: 4; -fx-border-radius: 4;";
    }

    private void updateUI() {
        if (state == null) return;
        for (int i = 0; i < 64; i++) updateSingleCell(i);
        updateAnimals();
    }

    private void updateSingleCell(int i) {
        StackPane cell = cellRefs[i];
        cell.getChildren().clear();
        double money = state.walletProperty().get();

        if (!state.unlocked[i]) {
            cell.setStyle("-fx-background-color: #1a1208; -fx-border-color: #2a1a08;"
                        + "-fx-border-width: 1; -fx-background-radius: 4; -fx-border-radius: 4;");
            Label lockLabel = new Label("🔒\n100$");
            String color = (money < 100) ? "#e05050" : "#6a5a4a";
            lockLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 9px;"
                             + "-fx-font-weight: bold; -fx-alignment: center;");
            lockLabel.setTextAlignment(TextAlignment.CENTER);
            cell.getChildren().add(lockLabel);

        } else {
            Crop crop = state.plots[i];
            String bg = (crop != null) ? STAGE_BG[crop.getGrowthStage()] : "#4a2f0e";
            cell.setStyle("-fx-background-color: " + bg + "; -fx-border-color: #5a3a1a;"
                        + "-fx-border-width: 1; -fx-background-radius: 4; -fx-border-radius: 4;");
            if (crop != null) {
                Label icon = new Label(STAGE_ICONS[crop.getGrowthStage()]);
                icon.setStyle("-fx-font-size: 22px;");
                cell.getChildren().add(icon);
            }
        }
    }

    private void unlock(int i) {
        if (state.walletProperty().get() >= 100) {
            state.walletProperty().set(state.walletProperty().get() - 100);
            state.unlocked[i] = true;
        }
    }

    private void handlePlot(int i) {
        if (state.plots[i] == null) {
            String s = state.selectedSeedProperty().get();
            if (state.seedStock.getOrDefault(s, 0) > 0) {
                state.plots[i] = new Crop(s, 10);
                state.seedStock.put(s, state.seedStock.get(s) - 1);
            }
        } else if (state.plots[i].isReady()) {
            state.foodStock.put(state.plots[i].getName(),
                state.foodStock.getOrDefault(state.plots[i].getName(), 0) + 1);
            state.plots[i] = null;
        }
    }

    private void updateAnimals() {
        animalBox.getChildren().clear();

        Label titre = new Label("🐾  ANIMAUX");
        titre.setStyle("-fx-text-fill: #c8860a; -fx-font-size: 14px; -fx-font-weight: bold;"
                     + "-fx-font-family: 'Georgia'; -fx-padding: 0 0 8 0;");
        animalBox.getChildren().add(titre);

        if (state.myAnimals.isEmpty()) {
            Label empty = new Label("Aucun animal\npour l'instant.");
            empty.setStyle("-fx-text-fill: #5a4a3a; -fx-font-size: 11px;"
                         + "-fx-font-family: 'Georgia'; -fx-alignment: center;");
            empty.setTextAlignment(TextAlignment.CENTER);
            animalBox.getChildren().add(empty);
            return;
        }

        for (Animal a : state.myAnimals) {
            VBox card = new VBox(6);
            card.setStyle(
                "-fx-background-color: rgba(0,0,0,0.25); -fx-background-radius: 10;"
              + "-fx-border-color: #5a3a1a; -fx-border-radius: 10; -fx-border-width: 1;"
              + "-fx-padding: 10;");

            HBox header = new HBox(8);
            header.setStyle("-fx-alignment: CENTER_LEFT;");
            Label icon = new Label(a.getIcon());
            icon.setStyle("-fx-font-size: 20px;");
            Label name = new Label(a.getSpecies());
            name.setStyle("-fx-text-fill: #f5e6a0; -fx-font-weight: bold; -fx-font-size: 12px;");
            Label prod = new Label("→ " + a.getResourceProduced());
            prod.setStyle("-fx-text-fill: #8a9a70; -fx-font-size: 11px;");
            header.getChildren().addAll(icon, name, prod);

            ProgressBar pb = new ProgressBar();
            pb.progressProperty().bind(a.progressProperty());
            pb.setPrefWidth(220);
            pb.setStyle("-fx-accent: #6ab04c;");

            boolean ready = a.isReady();
            Button btn = new Button(ready ? "✅  Récolter" : "🌾  Nourrir");
            btn.setPrefWidth(220);
            btn.setStyle(ready
                ? "-fx-background-color: #2d6a1a; -fx-text-fill: #a8e880; -fx-font-weight: bold;"
                + "-fx-font-size: 12px; -fx-background-radius: 6; -fx-border-color: #4a9a2a;"
                + "-fx-border-radius: 6; -fx-cursor: hand; -fx-padding: 6;"
                : "-fx-background-color: #3a2a10; -fx-text-fill: #f5d890; -fx-font-weight: bold;"
                + "-fx-font-size: 12px; -fx-background-radius: 6; -fx-border-color: #7a5a20;"
                + "-fx-border-radius: 6; -fx-cursor: hand; -fx-padding: 6;");

            btn.setOnAction(e -> {
                if (a.isReady()) {
                    state.productStock.put(a.getResourceProduced(),
                        state.productStock.getOrDefault(a.getResourceProduced(), 0) + 1);
                    a.progressProperty().set(0);
                } else if (state.foodStock.getOrDefault(a.getFoodNeeded(), 0) > 0) {
                    state.foodStock.put(a.getFoodNeeded(), state.foodStock.get(a.getFoodNeeded()) - 1);
                    a.feed();
                }
            });

            card.getChildren().addAll(header, pb, btn);
            animalBox.getChildren().add(card);
        }
    }
}
