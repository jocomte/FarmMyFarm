package model;

import javafx.beans.property.*;

public class Animal {
    private String species, foodNeeded, resourceProduced, icon;
    private DoubleProperty  progress    = new SimpleDoubleProperty(0);
    private BooleanProperty isProducing = new SimpleBooleanProperty(false);

    public Animal(String species, String foodNeeded, String resourceProduced, String icon) {
        this.species          = species;
        this.foodNeeded       = foodNeeded;
        this.resourceProduced = resourceProduced;
        this.icon             = icon;
    }

    public void feed() {
        if (!isProducing.get()) {
            isProducing.set(true);
            progress.set(0);
        }
    }

    public void update() {
        if (isProducing.get()) {
            progress.set(progress.get() + 0.02);
            if (progress.get() >= 1.0) isProducing.set(false);
        }
    }

    public boolean isReady()                     { return progress.get() >= 1.0 && !isProducing.get(); }
    public String getSpecies()                   { return species; }
    public String getFoodNeeded()                { return foodNeeded; }
    public String getResourceProduced()          { return resourceProduced; }
    public String getIcon()                      { return icon; }
    public DoubleProperty  progressProperty()    { return progress; }
    public BooleanProperty isProducingProperty() { return isProducing; } // ← pour SaveManager
}