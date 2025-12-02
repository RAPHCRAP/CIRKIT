package com.example.cirkitry;

import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.Pin;
import com.example.cirkitry.model.PinType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PinTest {

    static class Dummy extends Component {
        public Dummy() { super("D"); this.type = "D"; }
        @Override public void compute() {}
    }

    @Test
    void signalLifecycle_setNextSignal_thenUpdateSignal() {
        Dummy parent = new Dummy();
        Pin p = new Pin(PinType.INPUT, parent);
        assertFalse(p.getSignal());
        p.setNextSignal(true);
        p.updateSignal();
        assertTrue(p.getSignal());
        p.setNextSignal(false);
        p.updateSignal();
        assertFalse(p.getSignal());
    }

    @Test
    void clear_resetsSignalAndConnections() {
        Dummy parent = new Dummy();
        Pin p = new Pin(PinType.INPUT, parent);
        p.setNextSignal(true);
        p.updateSignal();
        p.clear();
        assertFalse(p.getSignal());
        assertTrue(p.getConnections().isEmpty());
    }

    @Test
    void absoluteCoordinates_parentPlusRelative() {
        Dummy parent = new Dummy();
        parent.placeInCircuit(5, 7, new com.example.cirkitry.model.Circuit(100, 100));
        Pin p = new Pin(PinType.INPUT, parent);
        p.setRelative(3, 2);
        assertEquals(8, p.getAbsoluteX());
        assertEquals(9, p.getAbsoluteY());
        assertEquals(3, p.getRelativeX());
        assertEquals(2, p.getRelativeY());
    }
}
