package controller;

import javafx.fxml.FXML;
import model.*;

public class MarketController {
    private GameState state;

    public void init(GameState state) { this.state = state; }

    // --- LOGIQUE D'ACHAT ---
    @FXML public void buyWheat() { buyS("Blé", 10); }
    @FXML public void buyCorn() { buyS("Maïs", 30); }
    @FXML public void buyCarott() { buyS("Carotte", 60); }

    private void buyS(String t, double c) {
        if (state.walletProperty().get() >= c) {
            state.walletProperty().set(state.walletProperty().get() - c);
            state.seedStock.put(t, state.seedStock.get(t) + 1);
            state.selectedSeedProperty().set(t);
        }
    }

    @FXML public void buyCow() {
        if (state.walletProperty().get() >= 150) {
            state.walletProperty().set(state.walletProperty().get() - 150);
            state.myAnimals.add(new Animal("Vache", "Maïs", "Lait", "🐄"));
        }
    }

    @FXML public void buyChicken() {
        if (state.walletProperty().get() >= 50) {
            state.walletProperty().set(state.walletProperty().get() - 50);
            state.myAnimals.add(new Animal("Poule", "Blé", "Oeuf", "🐔"));
        }
    }

    @FXML public void buyPig() {
        if (state.walletProperty().get() >= 300) {
            state.walletProperty().set(state.walletProperty().get() - 300);
            state.myAnimals.add(new Animal("Cochon", "Carotte", "Bacon", "🐖"));
        }
    }



    // --- NOUVELLE LOGIQUE DE VENTE À LA CARTE ---

    @FXML public void sellWheat() { sellFood("Blé", 25); } // Prix de vente du Blé
    @FXML public void sellCorn() { sellFood("Maïs", 60); } // Prix de vente du Maïs
    @FXML public void sellCarrot() { sellFood("Carotte", 120); }

    @FXML public void sellMilk() { sellProduct("Lait", 80); }
    @FXML public void sellEgg() { sellProduct("Oeuf", 40); }
    @FXML public void sellBacon() { sellProduct("Bacon", 150); }

    private void sellFood(String name, double price) {
        int qty = state.foodStock.getOrDefault(name, 0);
        if (qty > 0) {
            state.walletProperty().set(state.walletProperty().get() + (qty * price));
            state.foodStock.put(name, 0); // Vide le stock après vente
        }
    }

    private void sellProduct(String name, double price) {
        int qty = state.productStock.getOrDefault(name, 0);
        if (qty > 0) {
            state.walletProperty().set(state.walletProperty().get() + (qty * price));
            state.productStock.put(name, 0); // Vide le stock après vente
        }
    }
}