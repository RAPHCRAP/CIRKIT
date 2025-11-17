package com.example.cirkitry;

import java.util.HashSet;

import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.Affine;

public class QCameraController {

    private final PerspectiveCamera cam;
    private Vector3f position;

    // Orientation quaternion
    private Quaternion orientation;

    private final int vectorRndOff = 6;
    private final int positionRndOff = 6;

    public HashSet<KeyCode> keysPressed = new HashSet<>();

    public QCameraController(Scene scene) {
        cam = new PerspectiveCamera(true);
        scene.setCamera(cam);
        initCamera();
    }

    public QCameraController(SubScene scene) {
        cam = new PerspectiveCamera(true);
        scene.setCamera(cam);
        initCamera();
    }

    private void initCamera() {
        cam.setNearClip(0.1f);
        cam.setFarClip(3000.0f);

        position = new Vector3f(0, 0, 0);
        orientation = new Quaternion(); // identity quaternion
        updateCamera();
    }

    // ===== Get camera axes =====
    public Vector3f getForward() {
        return orientation.rotateVector(new Vector3f(0, 0, 1));
    }

    public Vector3f getUp() {
        return orientation.rotateVector(new Vector3f(0, 1, 0));
    }

    public Vector3f getRight() {
        return orientation.rotateVector(new Vector3f(1, 0, 0));
    }

    // ===== Move camera =====
    public void move(Vector3f delta) {
        position = position.add(delta).round(positionRndOff);
        updateCamera();
    }

    public void move(double x, double y, double z) {
        position = position.add(new Vector3f(x, y, z)).round(positionRndOff);
        updateCamera();
    }

    // ===== Rotate camera using quaternions =====
    public void rotateAroundAxis(Vector3f axis, double angleRad) {
        Quaternion q = Quaternion.fromAxisAngle(axis.normalize(), angleRad);
        orientation = q.multiply(orientation).normalize();
        updateCamera();
    }

    public void rotateYawPitch(double yawDeg, double pitchDeg) {
        double yawRad = Math.toRadians(yawDeg);
        double pitchRad = Math.toRadians(pitchDeg);

        // Yaw: rotate around world up (0,1,0)
        Quaternion yawQ = Quaternion.fromAxisAngle(new Vector3f(0, 1, 0), yawRad);

        // Pitch: rotate around camera right
        Vector3f right = getRight();
        Quaternion pitchQ = Quaternion.fromAxisAngle(right, pitchRad);

        orientation = pitchQ.multiply(yawQ).multiply(orientation).normalize();
        updateCamera();
    }

    // ===== Apply to PerspectiveCamera =====
    private void updateCamera() {
        Vector3f f = getForward().normalize();
        Vector3f r = getRight().normalize();
        Vector3f u = getUp().normalize();

        // Round for readability
        f = f.round(vectorRndOff);
        r = r.round(vectorRndOff);
        u = u.round(vectorRndOff);

        Affine affine = new Affine(
                r.x, u.x, -f.x, position.x,
                r.y, u.y, -f.y, position.y,
                r.z, u.z, -f.z, position.z
        );

        cam.getTransforms().setAll(affine);
    }

    public Vector3f getPosition() {
        return position;
    }

    public PerspectiveCamera getCamera() {
        return cam;
    }






    public void handleInput(double moveSpeed) {
    // Get camera axes
    Vector3f dir = getForward().normalize();  // forward
    Vector3f upr = getUp().normalize();       // up
    Vector3f per = getRight().normalize();    // right

    // Keyboard movement
    if (keysPressed.contains(KeyCode.W)) move(dir.scale(moveSpeed));
    if (keysPressed.contains(KeyCode.S)) move(dir.scale(-moveSpeed));
    if (keysPressed.contains(KeyCode.A)) move(per.scale(-moveSpeed));
    if (keysPressed.contains(KeyCode.D)) move(per.scale(moveSpeed));
    if (keysPressed.contains(KeyCode.SPACE)) move(upr.scale(moveSpeed));
    if (keysPressed.contains(KeyCode.CONTROL)) move(upr.scale(-moveSpeed));
}

}

    

