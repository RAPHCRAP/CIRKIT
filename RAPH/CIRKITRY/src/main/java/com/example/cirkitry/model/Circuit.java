package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.List;

public class Circuit {

    private final int width;
    private final int height;

    private final Cell[][] grid;
    private final List<Component> components = new ArrayList<>();
    private final List<Wire> wires = new ArrayList<>();


    public Circuit(int width, int height) {
        this.width = width;
        this.height = height;

        grid = new Cell[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }
    }

    // ------------------------------------------------------------
    // Grid Access
    // ------------------------------------------------------------

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return null;
        return grid[x][y];
    }


    // ------------------------------------------------------------
    // Component Management
    // ------------------------------------------------------------

    public void addComponent(Component c) {
        // Register the component
        components.add(c);

        // For each occupied cell the component claims:
        for (Cell cell : c.getOccupiedCells()) {
            int x = cell.getX();
            int y = cell.getY();

            Cell gridCell = getCell(x, y);
            if (gridCell == null) continue;

            // Occupy the cell
            gridCell.setComponent(c);
        }
    }


    // ------------------------------------------------------------
    // Wire Management
    // ------------------------------------------------------------

    // public void addWire(Wire w) {
    //     wires.add(w);

    //     // Mark wire segments onto the grid
    //     for (Cell c : w.getCells()) {
    //         Cell gridCell = getCell(c.getX(), c.getY());
    //         if (gridCell == null) continue;

    //         gridCell.setWire(w);
    //     }
    // }


    // ------------------------------------------------------------
    // Simulation Tick
    // ------------------------------------------------------------

    public void tick() {

        // 1. Let all components read their pin inputs
        //    (Pins are typically updated by wires)

        // 2. Let each component compute its output
        for (Component c : components) {
            c.computeOutput();
        }

        // 3. Let each wire propagate signal from output pins to input pins
        for (Wire w : wires) {
            w.propagate();
        }
    }
}
