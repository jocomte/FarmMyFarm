package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.beans.binding.Bindings;
import model.GameState;
import util.SaveManager;

public class MainController {
    @FXML private Label moneyLabel, seedLabel, saveStatusLabel;
    @FXML private Pane farmView, marketView;

    @FXML private Label wheatSeedQty, cornSeedQty, carrotSeedQty;
    @FXML private Label wheatFoodQty, cornFoodQty, carrotFoodQty;
    @FXML private Label eggQty, milkQty, baconQty;

    @FXML private FarmController farmViewController;
    @FXML private MarketController marketViewController;

    private final GameState gameState = new GameState();

    @FXML
    public void initialize() {
        moneyLabel.textProperty().bind(gameState.walletProperty().asString("%.0f $"));
        seedLabel.textProperty().bind(Bindings.concat("Équipé : ", gameState.selectedSeedProperty()));

        farmViewController.init(gameState);
        marketViewController.init(gameState);

        gameState.seedStock.addListener(
                (javafx.collections.MapChangeListener<? super String, ? super Integer>) c -> updateInventoryUI());
        gameState.foodStock.addListener(
                (javafx.collections.MapChangeListener<? super String, ? super Integer>) c -> updateInventoryUI());
        gameState.productStock.addListener(
                (javafx.collections.MapChangeListener<? super String, ? super Integer>) c -> updateInventoryUI());

        // Indique si une save existe au démarrage
        saveStatusLabel.setText(SaveManager.saveExists() ? "💾 Save existante" : "Pas de save");

        updateInventoryUI();
        showFarm();
    }

    // ── SAVE ──────────────────────────────────────────────────

    @FXML
    public void saveGame() {
        try {
            SaveManager.save(gameState);
            saveStatusLabel.setText("✅ Sauvegardé !");
            showInfo("Partie sauvegardée", "Votre progression a été sauvegardée.");
        } catch (Exception e) {
            saveStatusLabel.setText("❌ Erreur save");
            showError("Erreur de sauvegarde", e.getMessage());
        }
    }

    // ── LOAD ──────────────────────────────────────────────────

    @FXML
    public void loadGame() {
        try {
            boolean found = SaveManager.load(gameState);
            if (found) {
                saveStatusLabel.setText("✅ Chargé !");
                // Reconstruit la grille pour refléter les nouvelles données
                farmViewController.init(gameState);
                updateInventoryUI();
                showInfo("Partie chargée", "Votre progression a été restaurée.");
            } else {
                showInfo("Aucune save", "Aucun fichier de sauvegarde trouvé.");
            }
        } catch (Exception e) {
            saveStatusLabel.setText("❌ Erreur load");
            showError("Erreur de chargement", e.getMessage());
        }
    }

    // ── Inventaire ────────────────────────────────────────────

    private void updateInventoryUI() {
        wheatSeedQty.setText("x"  + gameState.seedStock.getOrDefault("Blé", 0));
        cornSeedQty.setText("x"   + gameState.seedStock.getOrDefault("Maïs", 0));
        carrotSeedQty.setText("x" + gameState.seedStock.getOrDefault("Carotte", 0));

        wheatFoodQty.setText(String.valueOf(gameState.foodStock.getOrDefault("Blé", 0)));
        cornFoodQty.setText(String.valueOf(gameState.foodStock.getOrDefault("Maïs", 0)));
        carrotFoodQty.setText(String.valueOf(gameState.foodStock.getOrDefault("Carotte", 0)));

        eggQty.setText(String.valueOf(gameState.productStock.getOrDefault("Oeuf", 0)));
        milkQty.setText(String.valueOf(gameState.productStock.getOrDefault("Lait", 0)));
        baconQty.setText(String.valueOf(gameState.productStock.getOrDefault("Bacon", 0)));
    }

    // ── Navigation ────────────────────────────────────────────

    @FXML public void equipWheat()  { gameState.selectedSeedProperty().set("Blé"); }
    @FXML public void equipCorn()   { gameState.selectedSeedProperty().set("Maïs"); }
    @FXML public void equipCarrot() { gameState.selectedSeedProperty().set("Carotte"); }

    @FXML public void showFarm()   { farmView.setVisible(true);  marketView.setVisible(false); }
    @FXML public void showMarket() { farmView.setVisible(false); marketView.setVisible(true); }

    // ── Alertes ───────────────────────────────────────────────

    private void showInfo(String title, String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #2c1a0e; -fx-font-family: 'Georgia';");
        a.show();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}