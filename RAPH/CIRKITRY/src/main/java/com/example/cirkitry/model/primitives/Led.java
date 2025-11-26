package com.example.cirkitry.model.primitives;



import com.example.cirkitry.model.AbstractSink;


public class Led extends AbstractSink {

    public Led() {
        super("LED");
        this.status = false; // default off
    }

    @Override
    protected void evaluate() {
        // Simply propagate input to status
        status = in.getSignal();
    }
}