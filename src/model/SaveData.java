package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SaveData {

    public double wallet;
    public String selectedSeed;

    public Map<String, Integer> seedStock  = new HashMap<>();
    public Map<String, Integer> foodStock  = new HashMap<>();
    public Map<String, Integer> productStock = new HashMap<>();

    public boolean[] unlocked = new boolean[64];

    public PlotData[] plots = new PlotData[64];

    public List<AnimalData> animals = new ArrayList<>();


    public static class PlotData {
        public String name;
        public int    growDuration;
        public long   plantTime;

        public PlotData() {}
        public PlotData(String name, int growDuration, long plantTime) {
            this.name        = name;
            this.growDuration = growDuration;
            this.plantTime   = plantTime;
        }
    }

    public static class AnimalData {
        public String species;
        public String foodNeeded;
        public String resourceProduced;
        public String icon;
        public double progress;
        public boolean isProducing;

        public AnimalData() {}
        public AnimalData(String species, String foodNeeded, String resourceProduced,
                          String icon, double progress, boolean isProducing) {
            this.species          = species;
            this.foodNeeded       = foodNeeded;
            this.resourceProduced = resourceProduced;
            this.icon             = icon;
            this.progress         = progress;
            this.isProducing      = isProducing;
        }
    }
}
