package com.example.cirkitry.model;

public class InternalWire {
    private final Pin src;
    private final Pin dst;

    public InternalWire(Pin src, Pin dst) {
        this.src = src;
        this.dst = dst;
    }

    public Pin getSrc()
    {
        return src;
    }

    public Pin getDst()
    {
        return dst;
    }

    public void propagate() {
        dst.setNextSignal(src.getSignal());
    }
}
