package com.example.cirkitry.model;

public abstract class PrimitiveGate extends Component {

    public PrimitiveGate(String name) {
        super(name);
    }

    @Override
    public void addSubcomponent(Component comp) {
        throw new UnsupportedOperationException(
            "Primitive gates cannot contain subcomponents.");
    }

    @Override
    public final void compute() {
        // All primitive gates only operate on pins.
        evaluate();
    }

    /**
     * Each primitive gate implements its boolean logic here.
     */
    protected abstract void evaluate();
}
