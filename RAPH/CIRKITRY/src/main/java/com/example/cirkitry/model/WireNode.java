package com.example.cirkitry.model;

public class WireNode {

    private final Wire wire;
    private int degree;
    private int x, y;

    public WireNode(int x, int y, Wire wire) {
        this.x = x;
        this.y = y;
        this.wire = wire;
        this.degree = 0;
    }

    public int getDegree() { return degree; }
    public void incrementDegree() { degree++; }
    public void decrementDegree() { degree--; }

    public int getX() { return x; }
    public int getY() { return y; }

    public Wire getWire() { return wire; }
}
