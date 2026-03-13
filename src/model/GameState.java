package model;

import javafx.beans.property.*;
import javafx.collections.*;

public class GameState {
    private final DoubleProperty wallet = new SimpleDoubleProperty(500.0);
    private final StringProperty selectedSeed = new SimpleStringProperty("Blé");

    public ObservableMap<String, Integer> seedStock = FXCollections.observableHashMap();
    public ObservableMap<String, Integer> foodStock = FXCollections.observableHashMap();
    public ObservableMap<String, Integer> productStock = FXCollections.observableHashMap();

    public Crop[] plots = new Crop[64];
    public boolean[] unlocked = new boolean[64];
    public ObservableList<Animal> myAnimals = FXCollections.observableArrayList();

    public GameState() {
        for (int i = 0; i < 5; i++) unlocked[i] = true;
        seedStock.put("Blé", 0); seedStock.put("Maïs", 0); seedStock.put("Carotte", 0);
        foodStock.put("Blé", 0); foodStock.put("Maïs", 0); foodStock.put("Carotte", 0);
        productStock.put("Lait", 0); productStock.put("Oeuf", 0); productStock.put("Bacon", 0);
    }

    public DoubleProperty walletProperty() { return wallet; }
    public StringProperty selectedSeedProperty() { return selectedSeed; }
}