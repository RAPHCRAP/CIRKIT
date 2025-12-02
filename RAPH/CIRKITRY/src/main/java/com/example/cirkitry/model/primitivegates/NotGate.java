package com.example.cirkitry.model.primitivegates;

import com.example.cirkitry.model.Pin;
import com.example.cirkitry.model.Primitive;

public class NotGate extends Primitive {

    private final Pin inA;
    private final Pin outC;

    public NotGate() {
        super("NOT");
        this.type = "NOT";
        inA = addInputPin("A");
        outC = addOutputPin("C");
    }

    @Override
    protected void evaluate() {
        outC.setNextSignal(!inA.getSignal());
    }

    public Pin getInA() { return inA; }
    public Pin getOutC() { return outC; }
}