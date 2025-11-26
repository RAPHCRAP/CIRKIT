package com.example.cirkitry.model;

public abstract class AbstractSink extends Component {

    protected final Pin in;      // every sink has a single input pin
    protected boolean status;     // current status (e.g., LED on/off)

    public AbstractSink(String name) {
        super(name);
        this.in = addInputPin("IN");
    }

    @Override
    public void compute() {
        evaluate();
    }

    // Force subclasses to define how they respond to input
    protected abstract void evaluate();

    // Accessor
    public boolean getStatus() {
        return status;
    }

    public Pin getIn() {
        return in;
    }

    @Override
    public void addSubcomponent(Component comp) {
        throw new UnsupportedOperationException("Sinks cannot contain subcomponents");
    }
}