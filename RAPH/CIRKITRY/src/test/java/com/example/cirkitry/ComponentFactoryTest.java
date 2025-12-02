package com.example.cirkitry;

import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.ComponentFactory;
import com.example.cirkitry.model.PrimitiveBootloader;
import com.example.cirkitry.model.primitivegates.AndGate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentFactoryTest {

    @Test
    void registerAll_thenCreateKnownTypes() {
        PrimitiveBootloader.registerAll();
        Component and = ComponentFactory.create("AND");
        assertNotNull(and);
        assertTrue(and instanceof AndGate);

        Component led = ComponentFactory.create("LED");
        assertNotNull(led);
        assertEquals("LED", led.getType());

        assertThrows(IllegalArgumentException.class, () -> ComponentFactory.create("NOPE"));
    }
}
