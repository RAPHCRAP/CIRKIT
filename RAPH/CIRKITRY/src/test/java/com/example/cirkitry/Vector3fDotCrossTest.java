package com.example.cirkitry;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Vector3fDotCrossTest {

    @Test
    void dotAndCross_areConsistent() {
        Vector3f a = new Vector3f(1, 0, 0);
        Vector3f b = new Vector3f(0, 1, 0);

        assertEquals(0.0, a.dot(b), 1e-12);
        assertEquals(1.0, a.dot(a), 1e-12);

        Vector3f c = a.cross(b);
        assertEquals(0.0, c.x, 1e-12);
        assertEquals(0.0, c.y, 1e-12);
        assertEquals(1.0, c.z, 1e-12);

        // Orthogonality: a · (a × b) == 0 and b · (a × b) == 0
        assertEquals(0.0, a.dot(c), 1e-12);
        assertEquals(0.0, b.dot(c), 1e-12);
    }
}
