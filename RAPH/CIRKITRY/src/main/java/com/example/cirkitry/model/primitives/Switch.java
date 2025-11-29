package com.example.cirkitry.model.primitives;

import com.example.cirkitry.model.AbstractSource;

public class Switch extends AbstractSource {

    public Switch() {
        super("SWITCH");
        this.width =3;
        this.height =3;
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
        out.setRelative(3, 1);
    }

    // Toggle the switch
    public void toggle() {
        state = !state;
    }

    // External control to turn the switch on/off
    public void setState(boolean newState) {
        this.state = newState;
    }
}