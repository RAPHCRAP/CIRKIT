package com.example.cirkitry;

import com.example.cirkitry.model.primitivegates.AndGate;
import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.Pin;
import com.example.cirkitry.model.Wire;
import com.example.cirkitry.model.primitives.Switch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GatesTest {

    @Test
    void AND_truthTable_basic() {
        AndGate and = new AndGate();
        Circuit circuit = new Circuit(100, 100);
        assertTrue(and.placeInCircuit(0, 0, circuit));

        Switch swA = new Switch();
        Switch swB = new Switch();
        assertTrue(circuit.addComponent(-10, 0, swA));
        assertTrue(circuit.addComponent(-10, 8, swB));

        Pin a = and.getInA();
        Pin b = and.getInB();
        Pin c = and.getOutC();

        // Wire switches to gate inputs
        Wire wA = new Wire(swA.getOut().getAbsoluteX(), swA.getOut().getAbsoluteY(), swA.getOut());
        wA.addSink(a);
        assertTrue(circuit.addWire(wA));

        Wire wB = new Wire(swB.getOut().getAbsoluteX(), swB.getOut().getAbsoluteY(), swB.getOut());
        wB.addSink(b);
        assertTrue(circuit.addWire(wB));

        // 00 -> 0
        swA.setState(false); swB.setState(false);
        circuit.tick();
        circuit.tick();
        a.updateSignal(); b.updateSignal();
        and.compute();
        and.commitPins();
        c.updateSignal();
        assertFalse(c.getSignal());

        // 01 -> 0
        swA.setState(false); swB.setState(true);
        circuit.tick();
        circuit.tick();
        a.updateSignal(); b.updateSignal();
        and.compute();
        and.commitPins();
        c.updateSignal();
        assertFalse(c.getSignal());

        // 10 -> 0
        swA.setState(true); swB.setState(false);
        circuit.tick();
        circuit.tick();
        a.updateSignal(); b.updateSignal();
        and.compute();
        and.commitPins();
        c.updateSignal();
        assertFalse(c.getSignal());

        // 11 -> 1
        swA.setState(true); swB.setState(true);
        circuit.tick();
        circuit.tick();
        a.updateSignal(); b.updateSignal();
        and.compute();
        and.commitPins();
        c.updateSignal();
        assertTrue(c.getSignal());
    }
}
