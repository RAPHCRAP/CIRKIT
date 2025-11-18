package com.example.cirkitry;

import java.util.HashSet;

import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;





public class CameraControl {

    private final PerspectiveCamera cam;
    private Vector3f up;
    private Vector3f forward;
    private Vector3f position;

    private Integer vectorRndOff;
    private Integer positionRndOff;


  // --- Input handling ---
    public HashSet<KeyCode> keysPressed = new HashSet<>();
    // private boolean panningMode = false;
    // private double mouseOldX, mouseOldY;
    // private boolean firstFrameInPanning = true;


    private void InitCamera()
    {
        cam.setNearClip(0.1f);
        cam.setFarClip(3000.0f);
    vectorRndOff = 4;
    positionRndOff = 6;

        // Default orientation
        up = new Vector3f(0, -1, 0);       // world up
        forward = new Vector3f(0, 0, 1); // forward looking down -Z
        position = new Vector3f(0, 0, 0);
    }

    public CameraControl(Scene scene) {
        cam = new PerspectiveCamera(true);
        scene.setCamera(cam);
        
        InitCamera();
        updateCamera();
    }

    public CameraControl(SubScene scene) {
        cam = new PerspectiveCamera(true);
        scene.setCamera(cam);
         InitCamera();
        updateCamera();
    }

    public  Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f absolutePosition) {
        position.set(absolutePosition.x, absolutePosition.y, absolutePosition.z);
        position.round(positionRndOff);
        updateCamera();
    }

   private Vector3f setOrthonormality() {
    // Step 1: Normalize forward
    forward = forward.normalize();

    // Step 2: Orthogonalize up against forward (Gram-Schmidt)
    up = up.subtract(forward.scale(forward.dot(up)));

    // Step 3: Keep up pointing roughly in original direction
    if (up.dot(new Vector3f(0, 1, 0)) < 0) {
        up = up.scale(-1);
    }

    // Step 4: Normalize up
    up = up.normalize();

    // Step 5: Compute right vector (ensure right-handed system)
    Vector3f right = forward.cross(up).normalize();

    // Step 6: Recompute up to ensure perfect orthonormality
    up = right.cross(forward).normalize();

    // Step 7: Round vectors for readability
    forward = forward.round(vectorRndOff);
    up = up.round(vectorRndOff);
    right = right.round(vectorRndOff);

    return right;
}

public Vector3f getForward() {
    setOrthonormality();
    return forward;
}

public Vector3f getUp() {
    setOrthonormality();
    return up;
}

public Vector3f getRight() {
    return right();
}

private void applyOrientation() {
       Vector3f f = getForward();
    Vector3f r = getRight();  // right
    Vector3f u = getUp();   // recompute up to guarantee orthonormal

    // Step 2: construct look-at matrix
    // JavaFX uses: column-major, Y-up
    // | right.x  up.x  -forward.x  tx |
    // | right.y  up.y  -forward.y  ty |
    // | right.z  up.z  -forward.z  tz |
    // |   0        0        0      1 |

    Affine affine = new Affine(
        r.x, u.x, -f.x, position.x,
        r.y, u.y, -f.y, position.y,
        r.z, u.z, -f.z, position.z
    );

    // Step 3: apply to camera
    cam.getTransforms().setAll(affine);

}


    private Vector3f right()
    {
        return setOrthonormality();
    }


    public void move(Vector3f delta) {
        position = position.add(delta).round(positionRndOff);
        updateCamera();
    }

    public void move(double x,double y,double z) {
        position.x +=x;
        position.y +=y;
        position.z +=z;

        position.round(positionRndOff);

        updateCamera();
    }

   

    
   private void updateCamera() {
    // // Ensure the forward and up vectors are orthonormal
    // Vector3f f = forward.normalize();        // Forward vector
    // Vector3f r = f.cross(up).normalize();    // Right vector
    // Vector3f u = r.cross(f).normalize();     // Corrected up vector

    // Set camera position
    cam.setTranslateX(position.x);
    cam.setTranslateY(position.y);
    cam.setTranslateZ(position.z);

    applyOrientation();

    // // Build rotation matrix from basis vectors
    // // JavaFX camera looks along -Z in local space, so forward = -f
    // double m00 = r.x, m01 = u.x, m02 = -f.x;
    // double m10 = r.y, m11 = u.y, m12 = -f.y;
    // double m20 = r.z, m21 = u.z, m22 = -f.z;

    // // Compute angle of rotation
    // double trace = m00 + m11 + m22;
    // double angleRad = Math.acos(Math.max(-1.0, Math.min(1.0, (trace - 1) / 2.0)));

    // // If the angle is very small, no rotation needed
    // if (angleRad < 1e-6) {
    //     cam.getTransforms().clear();
    //     return;
    // }

    // // Compute rotation axis from rotation matrix
    // double rx = m21 - m12;
    // double ry = m02 - m20;
    // double rz = m10 - m01;

    // Vector3f axis = new Vector3f(rx, ry, rz).normalize();

    // // Apply axis-angle rotation to camera
    // cam.getTransforms().setAll(new Rotate(Math.toDegrees(angleRad), axis.x, axis.y, axis.z));
}


//  ============ HANDLES ================//


 // =============== MOUSE HANDLES ====== //
public void onMousePressed(MouseEvent e) {
    // mouseOldX = e.getSceneX();
    // mouseOldY = e.getSceneY();
    // panningMode = true;
    // firstFrameInPanning = true;
}

public void onMouseDragged(MouseEvent e) {
    // if (!panningMode) return;

    // double deltaX = e.getSceneX() - mouseOldX;
    // double deltaY = e.getSceneY() - mouseOldY;

    // if (firstFrameInPanning) {
    //     firstFrameInPanning = false;
    //     deltaX = 0;
    //     deltaY = 0;
    // }

    // mouseOldX = e.getSceneX();
    // mouseOldY = e.getSceneY();

    // double sensitivity = 0.2;
    // rotateYawPitch(-deltaX * sensitivity, -deltaY * sensitivity);
}

// ================ HANDLE ROTATE ======== //
public void handleRotation(double deltaTime) {
    // double deltaAngle = 90 * deltaTime; // degrees per second

    // if (keysPressed.contains(KeyCode.R)) rotateAroundX(deltaAngle, 1);
    // if (keysPressed.contains(KeyCode.T)) rotateAroundX(-deltaAngle, 1);
    // if (keysPressed.contains(KeyCode.Y)) rotateAroundY(deltaAngle, 1);
    // if (keysPressed.contains(KeyCode.U)) rotateAroundY(-deltaAngle, 1);
    // if (keysPressed.contains(KeyCode.I)) rotateAroundZ(deltaAngle, 1);
    // if (keysPressed.contains(KeyCode.O)) rotateAroundZ(-deltaAngle, 1);
}


  // ====================== HANDLE MOVEMENT =================//


public void handleInput(double moveSpeed) {
    Vector3f dir = getForward();
    Vector3f upr = getUp();
    Vector3f per = getRight();

    if (keysPressed.contains(KeyCode.W)) move(dir.scale(moveSpeed));
    if (keysPressed.contains(KeyCode.S)) move(dir.scale(-moveSpeed));
    if (keysPressed.contains(KeyCode.A)) move(per.scale(-moveSpeed));
    if (keysPressed.contains(KeyCode.D)) move(per.scale(moveSpeed));
    if (keysPressed.contains(KeyCode.SPACE)) move(upr.scale(moveSpeed));
    if (keysPressed.contains(KeyCode.CONTROL)) move(upr.scale(-moveSpeed));
}


}



// class CameraController {

//     private final PerspectiveCamera cam;
//     private Vector3f position;
//     private Vector3f forward;
//     private Vector3f up;

//     private final int vectorRndOff = 6;    // for readability
//     private final int positionRndOff = 6;

//     public HashSet<KeyCode> keysPressed = new HashSet<>();

//     public CameraController(Scene scene) {
//         cam = new PerspectiveCamera(true);
//         scene.setCamera(cam);
//         initCamera();
//     }

//     public CameraController(SubScene scene) {
//         cam = new PerspectiveCamera(true);
//         scene.setCamera(cam);
//         initCamera();
//     }

//     private void initCamera() {
//         cam.setNearClip(0.1f);
//         cam.setFarClip(3000.0f);

//         // Default vectors
//         position = new Vector3f(0, 0, 0);
//         forward = new Vector3f(0, 0, 1);  // looking down +Z
//         up = new Vector3f(0, 1, 0);       // Y-up

//         updateCamera();
//     }

//     // ===== Camera movement =====
//     public void move(Vector3f delta) {
//         position = position.add(delta).round(positionRndOff);
//         updateCamera();
//     }

//     public void move(double x, double y, double z) {
//         position = position.add(new Vector3f(x, y, z)).round(positionRndOff);
//         updateCamera();
//     }

//     public Vector3f getForward() {
//         orthonormalize();
//         return forward;
//     }

//     public Vector3f getUp() {
//         orthonormalize();
//         return up;
//     }

//     public Vector3f getRight() {
//         return orthonormalize();
//     }

//     // ===== Orthonormalize the camera basis =====
//     private Vector3f orthonormalize() {
//         forward = forward.normalize();
//         Vector3f right = forward.cross(up).normalize();
//         up = right.cross(forward).normalize();

//         // optional rounding
//         forward = forward.round(vectorRndOff);
//         up = up.round(vectorRndOff);
//         right = right.round(vectorRndOff);

//         return right;
//     }

//     // ===== Apply camera orientation via Affine =====
//     private void updateCamera() {
//         Vector3f f = forward.normalize();
//         Vector3f r = f.cross(up).normalize();
//         Vector3f u = r.cross(f).normalize();

//         // Round for readability
//         f = f.round(vectorRndOff);
//         r = r.round(vectorRndOff);
//         u = u.round(vectorRndOff);

//         // Build column-major Affine (JavaFX uses this format)
//         Affine affine = new Affine(
//                 r.x, u.x, -f.x, position.x,
//                 r.y, u.y, -f.y, position.y,
//                 r.z, u.z, -f.z, position.z
//         );

//         cam.getTransforms().setAll(affine);

//         // Save cleaned vectors
//         forward = f;
//         up = u;
//     }

//     // // ===== Vector rotations (Rodrigues formula) =====
//     // public void rotateAroundAxis(Vector3f axis, double angleRad) {
//     //     forward = forward.rotateAroundAxis(axis, angleRad).normalize();
//     //     up = up.rotateAroundAxis(axis, angleRad).normalize();
//     //     updateCamera();
//     // }

//     // public void rotateYawPitch(double yawDeg, double pitchDeg) {
//     //     double yawRad = Math.toRadians(yawDeg);
//     //     double pitchRad = Math.toRadians(pitchDeg);

//     //     Vector3f right = getRight();

//     //     // Yaw around world up (0,1,0)
//     //     forward = forward.rotateAroundAxis(new Vector3f(0, 1, 0), yawRad).normalize();

//     //     // Recompute right
//     //     right = getRight();

//     //     // Pitch around camera right
//     //     forward = forward.rotateAroundAxis(right, pitchRad).normalize();

//     //     // Recompute up
//     //     up = right.cross(forward).normalize();

//     //     updateCamera();
//     // }

//     // // ===== Utility methods =====
//     public void getWorldPosition(Vector3f pos) {
//         pos = position;
//     }

//     public PerspectiveCamera getCamera() {
//         return cam;
//     }

//     //  ============ HANDLES ================//


//  // =============== MOUSE HANDLES ====== //
// public void onMousePressed(MouseEvent e) {
//     // mouseOldX = e.getSceneX();
//     // mouseOldY = e.getSceneY();
//     // panningMode = true;
//     // firstFrameInPanning = true;
// }

// public void onMouseDragged(MouseEvent e) {
//     // if (!panningMode) return;

//     // double deltaX = e.getSceneX() - mouseOldX;
//     // double deltaY = e.getSceneY() - mouseOldY;

//     // if (firstFrameInPanning) {
//     //     firstFrameInPanning = false;
//     //     deltaX = 0;
//     //     deltaY = 0;
//     // }

//     // mouseOldX = e.getSceneX();
//     // mouseOldY = e.getSceneY();

//     // double sensitivity = 0.2;
//     // rotateYawPitch(-deltaX * sensitivity, -deltaY * sensitivity);
// }

// // ================ HANDLE ROTATE ======== //
// public void handleRotation(double deltaTime) {
//     // double deltaAngle = 90 * deltaTime; // degrees per second

//     // if (keysPressed.contains(KeyCode.R)) rotateAroundX(deltaAngle, 1);
//     // if (keysPressed.contains(KeyCode.T)) rotateAroundX(-deltaAngle, 1);
//     // if (keysPressed.contains(KeyCode.Y)) rotateAroundY(deltaAngle, 1);
//     // if (keysPressed.contains(KeyCode.U)) rotateAroundY(-deltaAngle, 1);
//     // if (keysPressed.contains(KeyCode.I)) rotateAroundZ(deltaAngle, 1);
//     // if (keysPressed.contains(KeyCode.O)) rotateAroundZ(-deltaAngle, 1);
// }


//   // ====================== HANDLE MOVEMENT =================//


// public void handleInput(double moveSpeed) {
//     Vector3f dir = forward;
//     Vector3f upr = up;
//     Vector3f per = getRight();

//     if (keysPressed.contains(KeyCode.W)) move(dir.scale(moveSpeed));
//     if (keysPressed.contains(KeyCode.S)) move(dir.scale(-moveSpeed));
//     if (keysPressed.contains(KeyCode.A)) move(per.scale(-moveSpeed));
//     if (keysPressed.contains(KeyCode.D)) move(per.scale(moveSpeed));
//     if (keysPressed.contains(KeyCode.SPACE)) move(upr.scale(moveSpeed));
//     if (keysPressed.contains(KeyCode.CONTROL)) move(upr.scale(-moveSpeed));
// }
// }

