package model;

public class Crop {
    private String name;
    private int growthStage = 0; // 0: 🌱, 1: 🌿, 2: 🌾
    private long plantTime;
    private int growDuration;

    public Crop(String name, int growDuration) {
        this.name = name;
        this.growDuration = growDuration;
        this.plantTime = System.currentTimeMillis() / 1000;
    }

    public void update() {
        long elapsed = (System.currentTimeMillis() / 1000) - plantTime;
        if (elapsed >= growDuration) growthStage = 2;
        else if (elapsed >= growDuration / 2) growthStage = 1;
    }

    public int getGrowthStage() { return growthStage; }
    public String getName() { return name; }
}