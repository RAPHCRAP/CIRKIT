package com.example.cirkitry.model;

public class AndGate extends PrimitiveGate {

    private final Pin inA;
    private final Pin inB;
    private final Pin outC;

    public AndGate() {
        super("AND");

        // Create pins
        inA = addInputPin("A");
        inB = addInputPin("B");
        outC = addOutputPin("C");
    }

    @Override
    protected void evaluate() {
        boolean a = inA.getSignal();
        boolean b = inB.getSignal();
        outC.setNextSignal(a && b);
    }

    // Optional pin getters (useful for builders or UI)
    public Pin getInA() { return inA; }
    public Pin getInB() { return inB; }
    public Pin getOutC() { return outC; }
}
