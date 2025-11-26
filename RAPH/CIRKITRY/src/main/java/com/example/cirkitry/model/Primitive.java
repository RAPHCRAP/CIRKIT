package com.example.cirkitry.model;

public abstract class Primitive extends Component {

    public Primitive(String name) {
        super(name);
        this.type = "Abstract{Composite}";
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
