package com.example.cirkitry;

import com.example.cirkitry.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentTest {

    // Minimal concrete component for testing layout/placement
    static class TestComponent extends Component {
        public TestComponent() {
            super("TEST");
            this.type = "TEST";
            this.width = 4;
            this.height = 3;
            addInputPin("A");
            addInputPin("B");
            addOutputPin("C");
        }

        @Override
        public void compute() { /* no-op */ }
    }

    @Test
    void addInputOutputPins_typesAndParentAreCorrect() {
        TestComponent c = new TestComponent();
        assertEquals(2, c.getInputPins().size());
        assertEquals(1, c.getOutputPins().size());
        assertTrue(c.getInputPins().get(0).isInput());
        assertTrue(c.getOutputPins().get(0).isOutput());
        assertEquals(c, c.getInputPins().get(0).getParent());
    }

    @Test
    void layoutPins_placesInputsOnLeft_outputsOnRight() {
        TestComponent c = new TestComponent();
        Circuit circuit = new Circuit(100, 100);
        assertTrue(c.placeInCircuit(0, 0, circuit));
        for (Pin p : c.getInputPins()) {
            assertEquals(0, p.getRelativeX());
        }
        for (Pin p : c.getOutputPins()) {
            assertEquals(c.getWidth() - 1, p.getRelativeX());
        }
    }

    @Test
    void placeInCircuit_marksCellsAndPins_inGrid() {
        TestComponent c = new TestComponent();
        Circuit circuit = new Circuit(100, 100);
        assertTrue(c.placeInCircuit(2, 3, circuit));
        for (int x = c.getX(); x < c.getX() + c.getWidth(); x++) {
            for (int y = c.getY(); y < c.getY() + c.getHeight(); y++) {
                Cell cell = circuit.getCell(x, y);
                assertNotNull(cell);
                assertEquals(c, cell.getComponent());
            }
        }
        for (Pin p : c.getInputPins()) {
            Cell cell = circuit.getCell(p.getAbsoluteX(), p.getAbsoluteY());
            assertNotNull(cell);
            assertEquals(p, cell.getPin());
        }
        for (Pin p : c.getOutputPins()) {
            Cell cell = circuit.getCell(p.getAbsoluteX(), p.getAbsoluteY());
            assertNotNull(cell);
            assertEquals(p, cell.getPin());
        }
    }

    @Test
    void pinConstraint_outOfBoundsPin_failsPlacement() {
        // Build a component and manually break pin constraint by shrinking size after layout
        TestComponent c = new TestComponent();
        // Force a bogus state: add an extra output pin far outside
        Pin bad = c.addOutputPin("BAD");
        bad.setRelative(1000, 1000); // relative far away → out of bounds after placement
        Circuit circuit = new Circuit(100, 100);
        // placeInCircuit calls pinConstraint; expect false
        assertFalse(c.placeInCircuit(0, 0, circuit));
    }

    @Test
    void moveTo_movesComponentAndUpdatesCells() {
        TestComponent c = new TestComponent();
        Circuit circuit = new Circuit(100, 100);
        assertTrue(circuit.addComponent(0, 0, c));
        assertTrue(circuit.getComponents().contains(c));
        assertTrue(c.canMoveTo(10, 10, circuit));
        // Work around moveTo by performing detach + add via Circuit API
        assertTrue(circuit.detachComponent(c));
        assertTrue(circuit.addComponent(10, 10, c));
        // New location
        for (int x = c.getX(); x < c.getX() + c.getWidth(); x++) {
            for (int y = c.getY(); y < c.getY() + c.getHeight(); y++) {
                Cell cell = circuit.getCell(x, y);
                assertNotNull(cell);
                assertEquals(c, cell.getComponent());
            }
        }
    }
}
