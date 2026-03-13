package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;

import java.io.*;
import java.nio.file.*;


public class SaveManager {

    private static final String APP_NAME  = "FarmMyFarm";
    private static final String FILE_NAME = "save.json";
    private static final Gson   GSON      = new GsonBuilder().setPrettyPrinting().create();


    public static Path getSavePath() {
        String os   = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        Path dir;

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            dir = Paths.get(appData != null ? appData : home, APP_NAME);
        } else if (os.contains("mac")) {
            dir = Paths.get(home, "Library", "Application Support", APP_NAME);
        } else {
            dir = Paths.get(home, "." + APP_NAME);
        }
        return dir.resolve(FILE_NAME);
    }

    // ── SAVE ──────────────────────────────────────────────────

    public static void save(GameState state) throws IOException {
        SaveData data = toSaveData(state);
        Path path = getSavePath();
        Files.createDirectories(path.getParent());
        Files.writeString(path, GSON.toJson(data));
    }

    // ── LOAD ──────────────────────────────────────────────────

    public static boolean load(GameState state) throws IOException {
        Path path = getSavePath();
        if (!Files.exists(path)) return false;

        SaveData data = GSON.fromJson(Files.readString(path), SaveData.class);
        applyToState(data, state);
        return true;
    }

    public static boolean saveExists() {
        return Files.exists(getSavePath());
    }


    private static SaveData toSaveData(GameState state) {
        SaveData data = new SaveData();

        data.wallet      = state.walletProperty().get();
        data.selectedSeed = state.selectedSeedProperty().get();
        data.unlocked    = state.unlocked.clone();

        data.seedStock.putAll(state.seedStock);
        data.foodStock.putAll(state.foodStock);
        data.productStock.putAll(state.productStock);

        for (int i = 0; i < 64; i++) {
            Crop c = state.plots[i];
            if (c != null) {
                data.plots[i] = new SaveData.PlotData(c.getName(), c.getGrowDuration(), c.getPlantTime());
            }
        }

        for (Animal a : state.myAnimals) {
            data.animals.add(new SaveData.AnimalData(
                a.getSpecies(), a.getFoodNeeded(), a.getResourceProduced(),
                a.getIcon(), a.progressProperty().get(), a.isProducingProperty().get()
            ));
        }

        return data;
    }


    private static void applyToState(SaveData data, GameState state) {
        state.walletProperty().set(data.wallet);
        state.selectedSeedProperty().set(data.selectedSeed);

        System.arraycopy(data.unlocked, 0, state.unlocked, 0, 64);

        state.seedStock.putAll(data.seedStock);
        state.foodStock.putAll(data.foodStock);
        state.productStock.putAll(data.productStock);

        for (int i = 0; i < 64; i++) {
            if (data.plots[i] != null) {
                state.plots[i] = Crop.restore(
                    data.plots[i].name,
                    data.plots[i].growDuration,
                    data.plots[i].plantTime
                );
            } else {
                state.plots[i] = null;
            }
        }

        state.myAnimals.clear();
        if (data.animals != null) {
            for (SaveData.AnimalData ad : data.animals) {
                Animal a = new Animal(ad.species, ad.foodNeeded, ad.resourceProduced, ad.icon);
                a.progressProperty().set(ad.progress);
                a.isProducingProperty().set(ad.isProducing);
                state.myAnimals.add(a);
            }
        }
    }
}
