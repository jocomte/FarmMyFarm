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

    // On stocke les références des cellules pour ne pas les recréer
    private StackPane[] cellRefs = new StackPane[64];

    public void init(GameState state) {
        this.state = state;
        setupGrid(); // On construit la structure une seule fois

        Timeline loop = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            for (Crop c : state.plots) if (c != null) c.update();
            for (Animal a : state.myAnimals) a.update();
            updateUI(); // On met à jour uniquement le contenu
        }));
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();
    }

    // Crée la structure fixe de la grille
    private void setupGrid() {
        farmGrid.getChildren().clear();
        for (int i = 0; i < 64; i++) {
            StackPane cell = new StackPane();
            cell.setPrefSize(55, 55);
            int idx = i;

            // EFFET DE SURBRILLANCE (Hover)
            cell.setOnMouseEntered(e -> cell.setStyle(cell.getStyle() + "-fx-border-color: yellow; -fx-border-width: 2;"));
            cell.setOnMouseExited(e -> updateSingleCell(idx)); // Restaure le style normal

            cell.setOnMouseClicked(e -> {
                if (!state.unlocked[idx]) {
                    unlock(idx);
                } else {
                    handlePlot(idx);
                }
                updateSingleCell(idx); // Mise à jour immédiate au clic
            });

            cellRefs[i] = cell;
            farmGrid.add(cell, i % 8, i / 8);
        }
        updateUI();
    }

    private void updateUI() {
        if (state == null) return;
        for (int i = 0; i < 64; i++) {
            updateSingleCell(i);
        }
        updateAnimals();
    }

    // Met à jour le contenu d'une seule case sans la supprimer
    private void updateSingleCell(int i) {
        StackPane cell = cellRefs[i];
        cell.getChildren().clear();
        double currentMoney = state.walletProperty().get();

        if (!state.unlocked[i]) {
            cell.setStyle("-fx-background-color: #333; -fx-border-color: #000;");
            Label lockLabel = new Label("🔒\n100$");
            String color = (currentMoney < 100) ? "red" : "white";
            lockLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 10px; -fx-font-weight: bold;");
            lockLabel.setTextAlignment(TextAlignment.CENTER);
            cell.getChildren().add(lockLabel);
        } else {
            cell.setStyle("-fx-background-color: #8B4513; -fx-border-color: #5d4037;");
            if (state.plots[i] != null) {
                String icon = state.plots[i].getGrowthStage() == 2 ? "🌾" : "🌱";
                cell.getChildren().add(new Label(icon));
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
        } else if (state.plots[i].getGrowthStage() == 2) {
            state.foodStock.put(state.plots[i].getName(), state.foodStock.getOrDefault(state.plots[i].getName(), 0) + 1);
            state.plots[i] = null;
        }
    }

    private void updateAnimals() {
        animalBox.getChildren().clear();
        for (Animal a : state.myAnimals) {
            ProgressBar pb = new ProgressBar();
            pb.progressProperty().bind(a.progressProperty());
            Button btn = new Button(a.isReady() ? "Récolter" : "Nourrir");
            btn.setOnAction(e -> {
                if (a.isReady()) {
                    state.productStock.put(a.getResourceProduced(), state.productStock.getOrDefault(a.getResourceProduced(), 0) + 1);
                    a.progressProperty().set(0);
                } else if (state.foodStock.getOrDefault(a.getFoodNeeded(), 0) > 0) {
                    state.foodStock.put(a.getFoodNeeded(), state.foodStock.get(a.getFoodNeeded()) - 1);
                    a.feed();
                }
            });
            animalBox.getChildren().addAll(new Label(a.getIcon() + " " + a.getResourceProduced()), pb, btn);
        }
    }
}