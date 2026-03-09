package controller;

import javafx.fxml.FXML;
import model.*;

public class MarketController {
    private GameState state;

    public void init(GameState state) { this.state = state; }

    @FXML public void buyWheat() { buyS("Blé", 10); }
    @FXML public void buyCorn() { buyS("Maïs", 30); }

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

    @FXML public void sellAll() {
        double gain = state.productStock.getOrDefault("Lait", 0) * 50;
        state.walletProperty().set(state.walletProperty().get() + gain);
        state.productStock.clear();
    }
}