package com.example.cirkitry.model.primitivegates;

import com.example.cirkitry.model.Pin;
import com.example.cirkitry.model.Primitive;

public class XnorGate extends Primitive {

    private final Pin inA;
    private final Pin inB;
    private final Pin outC;

    public XnorGate() {
        super("XNOR");
        inA = addInputPin("A");
        inB = addInputPin("B");
        outC = addOutputPin("C");
    }

    @Override
    protected void evaluate() {
        outC.setNextSignal(!(inA.getSignal() ^ inB.getSignal()));
    }

    public Pin getInA() { return inA; }
    public Pin getInB() { return inB; }
    public Pin getOutC() { return outC; }
}