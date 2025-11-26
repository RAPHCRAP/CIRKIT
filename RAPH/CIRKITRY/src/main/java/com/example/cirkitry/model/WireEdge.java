package com.example.cirkitry.model;

public class WireEdge {
    private final WireNode a;
    private final WireNode b;

    public WireEdge(WireNode a, WireNode b) {
        if (a.getX() != b.getX() && a.getY() != b.getY())
            throw new IllegalArgumentException("Edges must be straight");

        this.a = a;
        this.b = b;
    }

    public WireNode getA() { return a; }
    public WireNode getB() { return b; }
}
