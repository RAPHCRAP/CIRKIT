package com.example.cirkitry;

import java.util.HashSet;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class EventHandles {

        private final HashSet<KeyCode> activeKeys = new HashSet<>();


        public EventHandles()
        {

        }


        public boolean contains(KeyCode e)
        {
            if(activeKeys.contains(e)) 
                {
                    return true;
                }
            
            
            return false;
        }
        
    public void attachKeyControls(Scene scene) {

   scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            
        activeKeys.add(e.getCode());

    });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
         
        activeKeys.remove(e.getCode());

    });
}
    
}
