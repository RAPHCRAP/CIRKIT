package com.example.cirkitry.model.primitives;

import com.example.cirkitry.model.AbstractSource;

public class Switch extends AbstractSource {

    public Switch() {
        super("SWITCH");
        this.type = "SWITCH";
        this.width =5;
        this.height =5;
        this.state = false; // default off
    }

    @Override
    protected void evaluate() {
        // Nothing automatic; state is controlled externally via setState()
      
        out.setNextSignal(state);
    }

    @Override
    protected void layoutPins()
    {
        out.setRelative(4, 2);
    }

    // Toggle the switch
    public void toggle() {
        state = !state;
        stateUpdate();
    }

    
}