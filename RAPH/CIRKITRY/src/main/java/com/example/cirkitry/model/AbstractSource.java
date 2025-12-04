package com.example.cirkitry.model;

public abstract class AbstractSource extends Component {

    protected final Pin out;    // every source has a single output pin
    protected boolean state=false;    // current state of the source

    public AbstractSource(String name) {
        super(name);
        this.out = addOutputPin("OUT");
    }

    // Every source must provide a way to compute/evaluate its state
    @Override
    public void compute() {
        evaluate();
        out.setNextSignal(state);
    }

    // Force subclasses to implement how they determine their state
    protected abstract void evaluate();

    // Accessor
    public boolean getState() {
        return state;
    }

    public Pin getOut() {
        return out;
    }

    // External control to turn the switch on/off
    public void setState(boolean newState) {
        this.state = newState;
    }

    @Override
    public void addSubcomponent(Component comp) {
        throw new UnsupportedOperationException("Sources cannot contain subcomponents");
    }
}