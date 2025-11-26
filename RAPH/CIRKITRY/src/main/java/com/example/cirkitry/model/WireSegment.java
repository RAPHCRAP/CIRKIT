package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.List;

public class WireSegment {

    private final int x1, y1;
    private final int x2, y2;

    public WireSegment(int x1, int y1, int x2, int y2) {
        if (x1 != x2 && y1 != y2)
            throw new IllegalArgumentException("WireSegment must be straight (horizontal or vertical)");

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public List<Cell> getCoveredCells(Circuit circuit) {
        List<Cell> result = new ArrayList<>();

        if (x1 == x2) {
            // vertical
            int low = Math.min(y1, y2);
            int high = Math.max(y1, y2);

            for (int y = low; y <= high; y++)
                result.add(circuit.getCell(x1, y));
        }
        else {
            // horizontal
            int low = Math.min(x1, x2);
            int high = Math.max(x1, x2);

            for (int x = low; x <= high; x++)
                result.add(circuit.getCell(x, y1));
        }

        return result;
    }

    // getters
    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }
}
