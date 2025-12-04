package com.example.cirkitry;

import com.example.cirkitry.builder.CircuitBuilder;
import com.example.cirkitry.builder.CircuitDefinition;
import com.example.cirkitry.controller.AppController;
import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.PrimitiveBootloader;

import javafx.application.Application;
import javafx.stage.Stage;



public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // ------------------------
        // 1. Create world
        // ------------------------
        

        Circuit circuit = WireTest.demoCircuit();

        CircuitDefinition def = CircuitBuilder.toDefinition(circuit);

        Circuit c = CircuitBuilder.instantiate(def);
        
        AppController control = new AppController(c);

        


  

        stage.setTitle("{~CIRKITRY~}");
        stage.setResizable(false);
        stage.setScene(control.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        
        PrimitiveBootloader.registerAll();
        launch();
    }
}

