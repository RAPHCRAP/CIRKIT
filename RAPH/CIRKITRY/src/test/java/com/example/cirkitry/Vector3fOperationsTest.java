package com.example.cirkitry;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Vector3fOperationsTest {

    @Test
    void addScaleNormalize_workAsExpected() {
        Vector3f v1 = new Vector3f(1, 2, 3);
        Vector3f v2 = new Vector3f(4, -1, 0.5);

        Vector3f sum = v1.add(v2);
        assertEquals(5.0, sum.x, 1e-9);
        assertEquals(1.0, sum.y, 1e-9);
        assertEquals(3.5, sum.z, 1e-9);

        Vector3f scaled = v1.scale(2.0);
        assertEquals(2.0, scaled.x, 1e-9);
        assertEquals(4.0, scaled.y, 1e-9);
        assertEquals(6.0, scaled.z, 1e-9);

        Vector3f v = new Vector3f(3, 0, 4); // len = 5
        Vector3f norm = v.normalize();
        assertEquals(1.0, norm.length(), 1e-12);
        assertEquals(0.6, norm.x, 1e-12);
        assertEquals(0.0, norm.y, 1e-12);
        assertEquals(0.8, norm.z, 1e-12);
    }
}
