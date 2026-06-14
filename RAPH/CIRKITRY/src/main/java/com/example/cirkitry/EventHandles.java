package com.example.cirkitry;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class EventHandles {

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private final Set<KeyCode> handledKeys = new HashSet<>();

    private final Scene scene;

    public EventHandles(Scene scene)
    {   
        this.scene = scene;
        attachKeyControls(scene);
    }

    // true while key is held
    public boolean contains(KeyCode key)
    {
        return activeKeys.contains(key);
    }

    // true ONLY once per press
    public boolean justPressed(KeyCode key)
    {
        if (activeKeys.contains(key) && !handledKeys.contains(key))
        {
            handledKeys.add(key);
            return true;
        }
        return false;
    }

    private void attachKeyControls(Scene scene)
    {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            activeKeys.add(e.getCode());
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            activeKeys.remove(e.getCode());
            handledKeys.remove(e.getCode()); // reset for next press
        });
    }
}