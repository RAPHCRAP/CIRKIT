package com.example.cirkitry;

import java.util.HashMap;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.Rotate;

public class CamController {

    private PerspectiveCamera camera;
    private Rotate rotateX;
    private Rotate rotateY;
    private Group camGroup;

    private final HashMap<KeyCode, Boolean> keyPressed = new HashMap<>();

    private double pitch = 0; // rotation around X-axis
    private double yaw = 0;   // rotation around Y-axis

    private final double rotationSpeed = 1.0; // degrees per frame


    private void initCam()
    {
        
        // Create rotation transforms
        rotateX = new Rotate(0, Rotate.X_AXIS);
        rotateY = new Rotate(0, Rotate.Y_AXIS);

        camera.getTransforms().addAll(rotateY, rotateX);
    }
    public CamController(Scene scene) {
        camera = new PerspectiveCamera();
        camGroup = new Group(camera);
        
        scene.setCamera(camera);
      

        initCam();

    }

    public CamController(SubScene scene) {
        camera = new PerspectiveCamera();
        scene.setCamera(camera);

        initCam();

    }

    // Call this on key press
    public void pressKey(KeyCode code) {
        keyPressed.put(code, true);
    }

    // Call this on key release
    public void releaseKey(KeyCode code) {
        keyPressed.put(code, false);
    }

    // Call this every frame in AnimationTimer
    public void handleInput(int i) {
        // Pitch control (rotateX)
        if (keyPressed.getOrDefault(KeyCode.Q, false)) {
            pitch += rotationSpeed;
        }
        if (keyPressed.getOrDefault(KeyCode.E, false)) {
            pitch -= rotationSpeed;
        }

        // Yaw control (rotateY)
        if (keyPressed.getOrDefault(KeyCode.R, false)) {
            yaw += rotationSpeed;
        }
        if (keyPressed.getOrDefault(KeyCode.T, false)) {
            yaw -= rotationSpeed;
        }

        // Clamp pitch if you want (optional)
        if (pitch > 90) pitch = 90;
        if (pitch < -90) pitch = -90;

        // Apply rotations
        rotateX.setAngle(pitch);
        rotateY.setAngle(yaw);

double cosPitch = Math.cos(Math.toRadians(pitch));
double sinPitch = Math.sin(Math.toRadians(pitch));
double cosYaw = Math.cos(Math.toRadians(yaw));
double sinYaw = Math.sin(Math.toRadians(yaw));

double forwardX = sinYaw * cosPitch;
double forwardY = -sinPitch;       // negative because Y+ is down in JavaFX
double forwardZ = cosYaw * cosPitch;


double rightX = Math.cos(Math.toRadians(yaw));
double rightZ = -Math.sin(Math.toRadians(yaw));


        double moveSpeed = 5.0; // units per frame

// Forward/backward
if (keyPressed.getOrDefault(KeyCode.W, false)) {
    camera.setTranslateX(camera.getTranslateX() + forwardX * moveSpeed);
    camera.setTranslateY(camera.getTranslateY() + forwardY * moveSpeed);
    camera.setTranslateZ(camera.getTranslateZ() + forwardZ * moveSpeed);
}
if (keyPressed.getOrDefault(KeyCode.S, false)) {
    camera.setTranslateX(camera.getTranslateX() - forwardX * moveSpeed);
    camera.setTranslateY(camera.getTranslateY() - forwardY * moveSpeed);
    camera.setTranslateZ(camera.getTranslateZ() - forwardZ * moveSpeed);
}

// Strafe left/right
if (keyPressed.getOrDefault(KeyCode.A, false)) {
    camera.setTranslateX(camera.getTranslateX() - rightX * moveSpeed);
    camera.setTranslateZ(camera.getTranslateZ() - rightZ * moveSpeed);
}
if (keyPressed.getOrDefault(KeyCode.D, false)) {
    camera.setTranslateX(camera.getTranslateX() + rightX * moveSpeed);
    camera.setTranslateZ(camera.getTranslateZ() + rightZ * moveSpeed);
}
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Vector3f getPosition()
    {
        return new Vector3f(camera.getTranslateX(),camera.getTranslateY(),camera.getTranslateZ());
    }
}