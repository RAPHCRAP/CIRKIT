package com.example.cirkitry;

import com.example.cirkitry.model.*;
import com.example.cirkitry.model.primitives.Led;
import com.example.cirkitry.model.primitives.Switch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class CircuitTest {

    private Circuit circuit;

    @BeforeEach
    void setup() {
        circuit = new Circuit(100, 100);
    }

    @Test
    void getCell_inBounds_and_outOfBounds() {
        assertNotNull(circuit.getCell(0, 0));
        assertNotNull(circuit.getCell(10, -10));
        // Large likely out of bounds
        assertNull(circuit.getCell(10000, 0));
        assertNull(circuit.getCell(0, -10000));
    }

    @Test
    void addComponent_success_placesCellsAndPins() {
        Switch sw = new Switch();
        boolean placed = circuit.addComponent(0, 0, sw);
        assertTrue(placed);
        // Occupied cells contain component
        for (int x = sw.getX(); x < sw.getX() + sw.getWidth(); x++) {
            for (int y = sw.getY(); y < sw.getY() + sw.getHeight(); y++) {
                Cell c = circuit.getCell(x, y);
                assertNotNull(c);
                assertEquals(sw, c.getComponent());
            }
        }
        // Pin placed
        Cell pinCell = circuit.getCell(sw.getOut().getAbsoluteX(), sw.getOut().getAbsoluteY());
        assertNotNull(pinCell);
        assertEquals(sw.getOut(), pinCell.getPin());
    }

    @Test
    void addComponent_outOfBounds_fails() {
        Switch sw = new Switch();
        // Try to place far outside
        boolean placed = circuit.addComponent(10_000, 10_000, sw);
        assertFalse(placed);
    }

    @Test
    void detachComponent_clearsCellsPinsAndRemovesFromList() {
        Switch sw = new Switch();
        assertTrue(circuit.addComponent(0, 0, sw));
        assertTrue(circuit.detachComponent(sw));
        // Cells cleared
        for (int x = sw.getX(); x < sw.getX() + sw.getWidth(); x++) {
            for (int y = sw.getY(); y < sw.getY() + sw.getHeight(); y++) {
                Cell c = circuit.getCell(x, y);
                assertNotNull(c);
                assertNull(c.getComponent());
            }
        }
        // Pin cells cleared
        Cell pinCell = circuit.getCell(sw.getOut().getAbsoluteX(), sw.getOut().getAbsoluteY());
        if (pinCell != null) {
            assertNull(pinCell.getPin());
        }
        assertFalse(circuit.getComponents().contains(sw));
    }

    @Test
    void extractCompositeFromRect_emptySelection_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            circuit.extractCompositeFromRect(-1, -1, 1, 1, "Empty");
        });
    }
}
