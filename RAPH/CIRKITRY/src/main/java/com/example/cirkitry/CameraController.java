
package com.example.cirkitry;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;



public class CameraController {
    private Group camGroup;
    private Point3D currentForward = new Point3D(0, 0, -1); // Initial forward
    private Point3D currentUp = new Point3D(0, 1, 0);       // Initial up
    private Point3D currentRight = new Point3D(1, 0, 0);    // Initial right
    
    // Rotation speeds (in degrees per key press)
    private final double PITCH_SPEED = 5.0;  // Look up/down
    private final double YAW_SPEED = 5.0;    // Look left/right  
    private final double ROLL_SPEED = 5.0;   // Roll CCW/CW
    
    private Rotate currentRotation = new Rotate(0, new Point3D(0, 1, 0));

    public CameraController(Group camGroup) {
        this.camGroup = camGroup;
        camGroup.getTransforms().add(currentRotation);
    }

    public void handleKeyEvent(KeyEvent event) {

        boolean rotationChanged = false;
        
        switch (event.getCode()) {
            case Q: // Look up (pitch down)
                pitch(-PITCH_SPEED);
                rotationChanged = true;
                break;
            case E: // Look down (pitch up)  
                pitch(PITCH_SPEED);
                rotationChanged = true;
                break;
            case R: // Look left (yaw left)
                yaw(-YAW_SPEED);
                rotationChanged = true;
                break;
            case T: // Look right (yaw right)
                yaw(YAW_SPEED);
                rotationChanged = true;
                break;
            case Y: // Roll counter-clockwise
                roll(-ROLL_SPEED);
                rotationChanged = true;
                break;
            case U: // Roll clockwise
                roll(ROLL_SPEED);
                rotationChanged = true;
                break;
        }
        
        if (rotationChanged) {
            updateCameraRotation();
        }
    }

    private void pitch(double angle) {
        // Pitch around current right axis
        Rotate pitchRot = new Rotate(angle, currentRight);
        currentForward = pitchRot.deltaTransform(currentForward);
        currentUp = pitchRot.deltaTransform(currentUp);
    }

    private void yaw(double angle) {
        // Yaw around current up axis  
        Rotate yawRot = new Rotate(angle, currentUp);
        currentForward = yawRot.deltaTransform(currentForward);
        currentRight = yawRot.deltaTransform(currentRight);
    }

    private void roll(double angle) {
        // Roll around current forward axis
        Rotate rollRot = new Rotate(angle, currentForward);
        currentUp = rollRot.deltaTransform(currentUp);
        currentRight = rollRot.deltaTransform(currentRight);
    }

    private void updateCameraRotation() {
        // Normalize vectors to prevent drift
        currentForward = currentForward.normalize();
        currentUp = currentUp.normalize();
        currentRight = currentRight.normalize();
        
        // Ensure orthogonality
        currentUp = currentForward.crossProduct(currentRight).normalize();
        currentRight = currentUp.crossProduct(currentForward).normalize();
        
        // Calculate rotation to align with current orientation
        Point3D defaultForward = new Point3D(0, 0, -1);
        Point3D defaultUp = new Point3D(0, 1, 0);
        
        // Calculate rotation axis and angle
        Point3D rotationAxis = defaultForward.crossProduct(currentForward);
        if (rotationAxis.magnitude() < 1e-6) {
            if (defaultForward.dotProduct(currentForward) < 0) {
                // 180 degree flip - use up axis
                rotationAxis = defaultUp;
            } else {
                // No rotation needed
                currentRotation.setAngle(0);
                return;
            }
        }
        rotationAxis = rotationAxis.normalize();
        
        double dot = defaultForward.dotProduct(currentForward);
        double angle = Math.acos(Math.max(-1, Math.min(1, dot))) * 180 / Math.PI;
        
        currentRotation.setAxis(rotationAxis);
        currentRotation.setAngle(angle);
    }

    // Smooth interpolation towards a target forward (for external control)
    public void smoothLookAt(Point3D targetForward, double interpolationFactor) {
        targetForward = targetForward.normalize();
        
        // Calculate the rotation needed between current and target
        Point3D axis = currentForward.crossProduct(targetForward);
        if (axis.magnitude() < 1e-6) {
            if (currentForward.dotProduct(targetForward) < 0) {
                axis = currentUp; // 180° flip around up axis
            } else {
                return; // Already aligned
            }
        }
        axis = axis.normalize();
        
        double dot = currentForward.dotProduct(targetForward);
        double targetAngle = Math.acos(Math.max(-1, Math.min(1, dot))) * 180 / Math.PI;
        
        // Apply a small portion of the rotation
        Rotate incrementalRot = new Rotate(targetAngle * interpolationFactor, axis);
        currentForward = incrementalRot.deltaTransform(currentForward);
        currentUp = incrementalRot.deltaTransform(currentUp);
        currentRight = incrementalRot.deltaTransform(currentRight);
        
        updateCameraRotation();
    }

    // Getters for current orientation
    public Point3D getCurrentForward() { return currentForward; }
    public Point3D getCurrentUp() { return currentUp; }
    public Point3D getCurrentRight() { return currentRight; }



}