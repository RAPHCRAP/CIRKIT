
package com.example.cirkitry.builder;


import java.util.ArrayList;
import java.util.List;

public class CircuitDefinition {

    private int width, height;  // circuit size
    private final List<ComponentDef> components = new ArrayList<>();
    private final List<WireDef> wires = new ArrayList<>();

    public CircuitDefinition() {}

    public CircuitDefinition(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // -----------------------------
    // Component Definition
    // -----------------------------
    public static class ComponentDef {
        public final String type;
        public final int x, y;

        public ComponentDef(String type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }

    public void addComponent(String type, int x, int y) {
        components.add(new ComponentDef(type, x, y));
    }

    public List<ComponentDef> getComponents() { return components; }

    // -----------------------------
    // Wire Definition
    // -----------------------------
    public static class WireDef {
        public final List<NodeDef> nodes = new ArrayList<>();
        public final List<EdgeDef> edges = new ArrayList<>();

        public void addNode(int x, int y) {
            nodes.add(new NodeDef(x, y));
        }

        public void addEdge(int startX, int startY, int endX, int endY) {
            edges.add(new EdgeDef(startX, startY, endX, endY));
        }
    }

    public static class NodeDef {
        public final int x, y;
        public NodeDef(int x, int y) { this.x = x; this.y = y; }
    }

    public static class EdgeDef {
        public final int startX, startY, endX, endY;
        public EdgeDef(int startX, int startY, int endX, int endY) {
            this.startX = startX; this.startY = startY;
            this.endX = endX; this.endY = endY;
        }
    }

    public void addWire(WireDef wire) {
        wires.add(wire);
    }

    public List<WireDef> getWires() { return wires; }
}
