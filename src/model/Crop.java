package model;

public class Crop {
    private String name;
    private int growthStage = 0; // 0:🌱 1:🌿 2:🌾 3:✅
    private long plantTime;
    private int growDuration;

    public Crop(String name, int growDuration) {
        this.name         = name;
        this.growDuration = growDuration;
        this.plantTime    = System.currentTimeMillis() / 1000;
    }

    private Crop(String name, int growDuration, long plantTime) {
        this.name         = name;
        this.growDuration = growDuration;
        this.plantTime    = plantTime;
    }

    public static Crop restore(String name, int growDuration, long plantTime) {
        Crop c = new Crop(name, growDuration, plantTime);
        c.update();
        return c;
    }

    public void update() {
        long elapsed = (System.currentTimeMillis() / 1000) - plantTime;
        if      (elapsed >= growDuration)           growthStage = 3;
        else if (elapsed >= growDuration * 2 / 3)   growthStage = 2;
        else if (elapsed >= growDuration / 3)        growthStage = 1;
        else                                         growthStage = 0;
    }

    public boolean isReady()     { return growthStage == 3; }
    public int getGrowthStage()  { return growthStage; }
    public String getName()      { return name; }
    public int getGrowDuration() { return growDuration; } // ← pour SaveManager
    public long getPlantTime()   { return plantTime; }    // ← pour SaveManager
}