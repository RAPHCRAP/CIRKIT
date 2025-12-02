package com.example.cirkitry.model.primitivegates;

import com.example.cirkitry.model.Pin;
import com.example.cirkitry.model.Primitive;

public class XorGate extends Primitive {

    private final Pin inA;
    private final Pin inB;
    private final Pin outC;

    public XorGate() {
        super("XOR");
        this.type = "XOR";

                this.width =5;
        this.height =4;
        inA = addInputPin("A");
        inB = addInputPin("B");
        outC = addOutputPin("C");
    }

    @Override
    protected void evaluate() {
        outC.setNextSignal(inA.getSignal() ^ inB.getSignal());
    }

    public Pin getInA() { return inA; }
    public Pin getInB() { return inB; }
    public Pin getOutC() { return outC; }
}