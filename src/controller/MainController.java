package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.beans.binding.Bindings;
import model.GameState;

public class MainController {
    @FXML private Label moneyLabel, seedLabel;
    @FXML private Pane farmView, marketView;

    // Labels de l'inventaire du bas
    @FXML private Label wheatSeedQty, cornSeedQty, carrotSeedQty;
    @FXML private Label wheatFoodQty, cornFoodQty, carrotFoodQty;

    @FXML private FarmController farmViewController;
    @FXML private MarketController marketViewController;

    private final GameState gameState = new GameState();

    @FXML
    public void initialize() {
        moneyLabel.textProperty().bind(gameState.walletProperty().asString("Argent : %.0f $"));
        seedLabel.textProperty().bind(Bindings.concat("Graine équipée : ", gameState.selectedSeedProperty()));

        farmViewController.init(gameState);
        marketViewController.init(gameState);

        // Listener pour mettre à jour l'inventaire visuel
        gameState.seedStock.addListener((javafx.collections.MapChangeListener<? super String, ? super Integer>) c -> updateInventoryUI());
        gameState.foodStock.addListener((javafx.collections.MapChangeListener<? super String, ? super Integer>) c -> updateInventoryUI());

        showFarm();
    }

    private void updateInventoryUI() {
        wheatSeedQty.setText("x" + gameState.seedStock.get("Blé"));
        cornSeedQty.setText("x" + gameState.seedStock.get("Maïs"));
        carrotSeedQty.setText("x" + gameState.seedStock.get("Carotte"));

        wheatFoodQty.setText("Blé: " + gameState.foodStock.get("Blé"));
        cornFoodQty.setText("Maïs: " + gameState.foodStock.get("Maïs"));
        carrotFoodQty.setText("Carotte: " + gameState.foodStock.get("Carotte"));
    }

    @FXML public void equipWheat() { gameState.selectedSeedProperty().set("Blé"); }
    @FXML public void equipCorn() { gameState.selectedSeedProperty().set("Maïs"); }
    @FXML public void equipCarrot() { gameState.selectedSeedProperty().set("Carotte"); }

    @FXML public void showFarm() { farmView.setVisible(true); marketView.setVisible(false); }
    @FXML public void showMarket() { farmView.setVisible(false); marketView.setVisible(true); }
}