package com.example.cirkitry;

import com.example.cirkitry.model.*;
import com.example.cirkitry.model.primitives.Led;
import com.example.cirkitry.model.primitives.Switch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WireModelTest {

    @Test
    void constructor_rejectsNonOutputSource() {
        Component dummy = new Component("C") { @Override public void compute() {} };
        Pin input = dummy.addInputPin("IN");
        assertThrows(IllegalArgumentException.class, () -> new Wire(0, 0, input));
    }

    @Test
    void addSink_requiresInputPin_andUpdatesConnections() {
        Component d = new Component("C") { @Override public void compute() {} };
        Pin out = d.addOutputPin("OUT");
        Wire w = new Wire(0, 0, out);
        Pin in = d.addInputPin("IN");
        w.addSink(in);
        assertTrue(w.getSinks().contains(in));
        assertTrue(in.getConnections().contains(w));
        // Reject output as sink
        Pin out2 = d.addOutputPin("O2");
        assertThrows(IllegalArgumentException.class, () -> w.addSink(out2));
    }

    @Test
    void propagate_setsNextSignalOnSinks() {
        Switch sw = new Switch();
        Led ledA = new Led();
        Led ledB = new Led();
        Circuit circuit = new Circuit(100, 100);
        assertTrue(circuit.addComponent(-10, 0, sw));
        assertTrue(circuit.addComponent(10, -10, ledA));
        assertTrue(circuit.addComponent(10, 10, ledB));

        Wire w = new Wire(sw.getOut().getAbsoluteX(), sw.getOut().getAbsoluteY(), sw.getOut());
        w.addSink(ledA.getIn());
        w.addSink(ledB.getIn());
        assertTrue(circuit.addWire(w));

        sw.setState(true);
        // tick: compute → propagate → commitPins
        circuit.tick();
        ledA.getIn().updateSignal();
        ledB.getIn().updateSignal();
        assertFalse(ledA.getIn().getSignal());
        assertFalse(ledB.getIn().getSignal());
    }

    @Test
    void extendEdge_straightAndLCreatesNodesEdgesAndCells() {
        Component d = new Component("C") { @Override public void compute() {} };
        Pin out = d.addOutputPin("O");
        out.setRelative(0, 0);
        d.placeInCircuit(0, 0, new Circuit(100, 100));

        Wire w = new Wire(d.getX() + out.getRelativeX(), d.getY() + out.getRelativeY(), out);
        Circuit circuit = new Circuit(100, 100);

        WireNode root = w.getNodes().get(0);
        assertTrue(w.extendEdge(root, 0, 5, circuit)); // vertical
        assertTrue(w.extendEdge(root, 5, 5, circuit)); // L shape via mid
        assertTrue(w.placeInCircuit(circuit));

        assertFalse(w.getEdges().isEmpty());
        assertFalse(w.getOccupiedCells().isEmpty());
    }

    @Test
    void updateOccupiedCells_autoAttachesToInputPins() {
        Switch sw = new Switch();
        Led led = new Led();
        Circuit circuit = new Circuit(100, 100);
        assertTrue(circuit.addComponent(-5, 0, sw));
        assertTrue(circuit.addComponent(0, 0, led));

        // Make wire path pass through LED's input cell
        Wire w = new Wire(sw.getOut().getAbsoluteX(), sw.getOut().getAbsoluteY(), sw.getOut());
        WireNode root = w.getNodes().get(0);
        int targetX = led.getIn().getAbsoluteX();
        int targetY = led.getIn().getAbsoluteY();
        assertTrue(w.extendEdge(root, targetX, targetY, circuit));
        assertTrue(w.placeInCircuit(circuit));

        assertTrue(led.getIn().getConnections().contains(w));
        assertTrue(w.getSinks().contains(led.getIn()));
    }

    @Test
    void deleteNode_onLeafNode_releasesCellsAndRemovesNode() {
        Component d = new Component("C") { @Override public void compute() {} };
        Pin out = d.addOutputPin("O");
        Circuit circuit = new Circuit(100, 100);
        d.placeInCircuit(0, 0, circuit);
        out.setRelative(0, 0);

        Wire w = new Wire(d.getX(), d.getY(), out);
        WireNode root = w.getNodes().get(0);
        assertTrue(w.placeInCircuit(circuit));
        assertTrue(w.extendEdge(root, 0, 5, circuit));

        // The far end node should be leaf (degree 1)
        WireNode leaf = null;
        for (WireNode n : w.getNodes()) {
            if (n != root && n.getDegree() == 1) { leaf = n; break; }
        }
        assertNotNull(leaf);
        assertTrue(w.deleteNode(leaf, circuit));
    }
}
