package com.example.cirkitry.builder;

import java.util.HashMap;
import java.util.Map;

import com.example.cirkitry.model.Cell;
import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.ComponentFactory;
import com.example.cirkitry.model.Pin;
import com.example.cirkitry.model.Wire;
import com.example.cirkitry.model.WireEdge;
import com.example.cirkitry.model.WireNode;

public class CircuitBuilder {

    private final CircuitDefinition definition;
    private final Circuit circuit;

    public CircuitBuilder(CircuitDefinition def) {
        this.definition = def;
        this.circuit = new Circuit(def.getWidth(), def.getHeight());
    }

    // -----------------------------
    // Build the circuit from definition
    // -----------------------------
    public Circuit build() {
        // 1. Place all components
        for (CircuitDefinition.ComponentDef cd : definition.getComponents()) {
            Component comp = ComponentFactory.create(cd.type);
            boolean ok = circuit.addComponent(cd.x, cd.y, comp);
            if (!ok) {
                throw new IllegalStateException(
                    "Failed to place component " + cd.type + " at (" + cd.x + "," + cd.y + ")"
                );
            }
        }

        // 2. Place all wires
        for (CircuitDefinition.WireDef wd : definition.getWires()) {
            placeWireFromDefinition(wd);
        }

        return circuit;
    }

    // -----------------------------
    // Place a wire from a WireDef
    // -----------------------------
    private void placeWireFromDefinition(CircuitDefinition.WireDef wd) {
        if (wd.nodes.isEmpty())
            throw new IllegalStateException("Wire has no nodes");

        // First node must be at a component output pin
        CircuitDefinition.NodeDef firstNode = wd.nodes.get(0);
        Cell startCell = circuit.getCell(firstNode.x, firstNode.y);
        if (startCell == null || startCell.getPin() == null || !startCell.getPin().isOutput())
            throw new IllegalStateException("Wire first node must be on an output pin at (" + firstNode.x + "," + firstNode.y + ")");

        Pin sourcePin = startCell.getPin();
        Wire wire = new Wire(firstNode.x, firstNode.y, sourcePin);

        // Map for quick node lookup
        Map<String, WireNode> nodeMap = new HashMap<>();
        nodeMap.put(firstNode.x + "," + firstNode.y, wire.getNodes().get(0));

        // Iterate over edges instead of nodes
        for (CircuitDefinition.EdgeDef e : wd.edges) {
            String startKey = e.startX + "," + e.startY;
            WireNode startNode = nodeMap.get(startKey);
            if (startNode == null)
                throw new IllegalStateException("Start node not found: " + startKey);

            boolean ok = wire.extendEdge(startNode, e.endX, e.endY, circuit);
            if (!ok)
                throw new IllegalStateException(
                    "Failed to extend wire along edge (" + e.startX + "," + e.startY + ") -> (" + e.endX + "," + e.endY + ")"
                );

            // register new node
            WireNode endNode = wire.getNodes().get(wire.getNodes().size() - 1);
            nodeMap.put(endNode.getX() + "," + endNode.getY(), endNode);
        }

        // Finally register the wire
        circuit.addWire(wire);
    }

    public Circuit getCircuit() { return circuit; }

    // -----------------------------
    // Static helper to instantiate from definition
    // -----------------------------
    public static Circuit instantiate(CircuitDefinition def) {
        return new CircuitBuilder(def).build();
    }

    // -----------------------------
    // Convert a Circuit into a CircuitDefinition
    // -----------------------------
    public static CircuitDefinition toDefinition(Circuit circuit) {
        CircuitDefinition def = new CircuitDefinition(circuit.getWidth(), circuit.getHeight());

        // Components
        for (Component c : circuit.getComponents()) {
            def.addComponent(c.getType(), c.getX(), c.getY());
        }

        // Wires
        for (Wire w : circuit.getWires()) {
            CircuitDefinition.WireDef wd = new CircuitDefinition.WireDef();
            // Nodes
            for (WireNode n : w.getNodes()) {
                wd.addNode(n.getX(), n.getY());
            }
            // Edges
            for (WireEdge e : w.getEdges()) {
                wd.addEdge(e.getA().getX(), e.getA().getY(), e.getB().getX(), e.getB().getY());
            }

            def.addWire(wd);
        }

        return def;
    }
}
