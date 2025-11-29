package com.example.cirkitry.model.primitives;



import com.example.cirkitry.model.AbstractSink;


public class Led extends AbstractSink {

    public Led() {
        super("LED");
        this.height=3;
        this.width=3;
        this.status = false; // default off
    }

    @Override
    protected void evaluate() {
        // Simply propagate input to status
        status = in.getSignal();
    }

    @Override
    protected void layoutPins()
    {
        in.setRelative(-1, 1);
    }
}