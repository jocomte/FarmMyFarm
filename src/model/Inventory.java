package model;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    // Stocke le nom de la ressource et sa quantité
    private Map<String, Integer> resources = new HashMap<>();

    public void addResource(String name, int amount) {
        resources.put(name, resources.getOrDefault(name, 0) + amount);
    }

    public int getQuantity(String name) {
        return resources.getOrDefault(name, 0);
    }

    public void clearResource(String name) {
        resources.put(name, 0);
    }

    public Map<String, Integer> getResources() {
        return resources;
    }
}
