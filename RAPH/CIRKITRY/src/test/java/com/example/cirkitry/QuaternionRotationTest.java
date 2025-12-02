package com.example.cirkitry;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuaternionRotationTest {

    @Test
    void rotateVector_zAxis90Degrees() {
        Vector3f zAxis = new Vector3f(0, 0, 1);
        Quaternion q = Quaternion.fromAxisAngle(zAxis, Math.PI / 2.0);

        Vector3f v = new Vector3f(1, 0, 0);
        Vector3f r = q.rotateVector(v);

        assertEquals(0.0, r.x, 1e-12);
        assertEquals(1.0, r.y, 1e-12);
        assertEquals(0.0, r.z, 1e-12);
    }
}
