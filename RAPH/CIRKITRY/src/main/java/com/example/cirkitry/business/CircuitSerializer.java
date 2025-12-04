package com.example.cirkitry.business;

import com.example.cirkitry.builder.CircuitDefinition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CircuitSerializer {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // CircuitDefinition only (deprecated now)
    public static String serialize(CircuitDefinition def) {
        return gson.toJson(def);
    }

    public static CircuitDefinition deserialize(String json) {
        return gson.fromJson(json, CircuitDefinition.class);
    }

    // NEW: full file wrapper
    public static String serializeFile(CircuitFile file) {
        return gson.toJson(file);
    }

    public static CircuitFile deserializeFile(String json) {
        return gson.fromJson(json, CircuitFile.class);
    }
}
