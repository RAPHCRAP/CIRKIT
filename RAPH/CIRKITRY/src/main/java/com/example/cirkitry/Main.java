package com.example.cirkitry;

import java.util.HashSet;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;



public class Main extends Application {


    private PerspectiveCamera camera = new PerspectiveCamera(true);
    public HashSet<KeyCode> keysPressed = new HashSet<>();
    private  Group camGroup;
    private Group sys;
    private Group movRoot = new Group();
    private Group yawNode = new Group();
    private Group pitchNode = new Group();
    private Group rollNode = new Group();

//     private Rotate pitch = new Rotate(0, Rotate.X_AXIS);  // look up/down
//     private Rotate yaw   = new Rotate(0, Rotate.Y_AXIS);  // look left/right
//     private Rotate roll  = new Rotate(0, Rotate.Z_AXIS);  // tilt (fan flip)


    private double lastMouseX;
private double lastMouseY;
private boolean mouseDragging = false;

private double mouseSensitivity = 0.2; // you can adjust this
    

    @Override
    public void start(Stage stage) {
        camera.setNearClip(0.1f);
        camera.setFarClip(3000.f);
        Group root3D = new Group();

        Group grid = makeGrid(400, 50);
        // root3D.getChildren().add(grid);

        sys = new Group();

        Sphere sp = new Sphere(50);

        Cylinder cd = new Cylinder(10,100);
        cd.setTranslateY(cd.getTranslateY()-50);

        sys.getChildren().addAll(sp,cd);

        
       root3D.getChildren().add(sys);
        

        Box b1 = new Box(100,100,100);
        b1.setTranslateX(700);
        b1.setTranslateY(400);
        b1.setTranslateZ(0);

        
camGroup = new Group(camera);

         movRoot.getChildren().add(pitchNode);
         pitchNode.getChildren().add(yawNode);
         yawNode.getChildren().add(rollNode);
         rollNode.getChildren().add(camGroup);


         pitchNode.setRotationAxis(Rotate.Y_AXIS);
yawNode.setRotationAxis(Rotate.X_AXIS);
rollNode.setRotationAxis(Rotate.Z_AXIS);


        //  CamGroup.setTranslateX(0);
        // CamGroup.setTranslateY(0);
        // CamGroup.setTranslateZ(0);

        //   camera.translateXProperty().set(0);
        // camera.translateYProperty().set(0);
        // camera.translateZProperty().set(0);

        movRoot.translateXProperty().set(0);
        movRoot.translateYProperty().set(0);
        movRoot.translateZProperty().set(0);

        // camGroup.getTransforms().addAll(pitch,yaw,roll);
        

        
        root3D.getChildren().addAll(movRoot,b1);

       

        addAxisBoxes(root3D);

        SubScene subScene3D = new SubScene(root3D, 1400, 800, true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.BLACK);
        subScene3D.setCamera(camera);

      
        subScene3D.setOnMousePressed(e -> {
    if (e.isPrimaryButtonDown()) {
        mouseDragging = true;
        lastMouseX = e.getSceneX();
        lastMouseY = e.getSceneY();
            }  
         });

subScene3D.setOnMouseReleased(e -> {
    mouseDragging = false;
});

subScene3D.setOnMouseDragged(e -> {
    if (!mouseDragging) return;

    double dx = e.getSceneX() - lastMouseX;
    double dy = e.getSceneY() - lastMouseY;

    lastMouseX = e.getSceneX();
    lastMouseY = e.getSceneY();

    // MOUSE LOOK
    // yaw.setAngle(yaw.getAngle() + dx * mouseSensitivity);   // left/right
    // pitch.setAngle(pitch.getAngle() - dy * mouseSensitivity); // up/down (invert Y)

      yawNode.setRotate(yawNode.getRotate()-dy*mouseSensitivity);
      pitchNode.setRotate(pitchNode.getRotate()+dx*mouseSensitivity);
});

        


        // --- GUI Overlay ---
        Label cameraPosLabel = new Label();
        cameraPosLabel.setTextFill(Color.WHITE);
        VBox guiOverlay = new VBox(cameraPosLabel);
        guiOverlay.setPickOnBounds(false);

        StackPane root = new StackPane();
        root.getChildren().addAll(subScene3D, guiOverlay);

        Scene scene = new Scene(root, 1400, 800, true);
        scene.setFill(Color.BLACK);

// Mouse look
// scene.setOnMousePressed(camera::onMousePressed);
// scene.setOnMouseDragged(camera::onMouseDragged);

// Keyboard input
scene.setOnKeyPressed(e -> {
    // camera.pressKey(e.getCode());
    keysPressed.add(e.getCode());

});
scene.setOnKeyReleased(e -> {
    // camera.releaseKey(e.getCode());
    
      keysPressed.remove(e.getCode());
});

// Game loop
AnimationTimer timer = new AnimationTimer() {
    // private long lastTime = -1;

    @Override
    public void handle(long now) {
        // if (lastTime < 0) lastTime = now;
        // double deltaTime = (now - lastTime) / 1e9;
        // lastTime = now;

        // camera.handleInput(1);  // movement speed
        // camera.handleRotation(deltaTime);      // rotation keys

                 

                //  Vector3f camFor = camera.getForward();

                //  Vector3f camUp = camera.getUp();
                //  Vector3f camRight = camera.getRight();

                // // Update GUI
                // cameraPosLabel.setText(String.format(
                //         "Camera Position: [X: %.1f, Y: %.1f, Z: %.1f]\nForward: [%.2f, %.2f, %.2f]\nUp: [%.2f, %.2f, %.2f]\nRight: [%.2f, %.2f, %.2f]",
                //         camPos.x, camPos.y, camPos.z, camFor.x,camFor.y,camFor.z,camUp.x,camUp.y,camUp.z,camRight.x,camRight.y,camRight.z
                // ));

                 String str = "";
                 str = PrintCoord(str,camGroup.getTranslateX(), camGroup.getTranslateY(), camGroup.getTranslateZ());

                 cameraPosLabel.setText(str);

        InputHandle();
    }
};
timer.start();

        stage.setScene(scene);
        stage.setTitle("Smooth Free-Fly Camera");
        stage.show();
    }

   
/////// ======== INPUT HANDLE =============== //////
/// 
/// 
///   
/// 
/// 
    private String PrintCoord(String str,double x,double y,double z)
    {
         if(!str.equals(""))
         {
            str += "\n";
         }

         str += String.format("Camera Position: [X: %.1f, Y: %.1f, Z: %.1f]\n",x,y,z);

         return str;
    }
      private void InputHandle()
      {
        //   if(keysPressed.contains(KeyCode.W)) 
        //   {
        //     CamGroup.setTranslateZ(CamGroup.getTranslateZ()+10);
        //   }
        //   if(keysPressed.contains(KeyCode.S)) 
        //   {
        //     CamGroup.setTranslateZ(CamGroup.getTranslateZ()-10);
        //   }
        //   if(keysPressed.contains(KeyCode.A)) 
        //   {
        //     CamGroup.setTranslateX(CamGroup.getTranslateX()-10);
        //   }
        //   if(keysPressed.contains(KeyCode.D)) 
        //   {
        //     CamGroup.setTranslateX(CamGroup.getTranslateX()+10);
        //   }
        //   if(keysPressed.contains(KeyCode.SPACE)) 
        //   {
        //     CamGroup.setTranslateY(CamGroup.getTranslateY()-10);

        //   }
        //   if(keysPressed.contains(KeyCode.CONTROL))
        //   {
        //     CamGroup.setTranslateY(CamGroup.getTranslateY()+10);
        //   }

        double speed = 5;
        Point3D forward = getForward();
Point3D right   = getRight();
Point3D up      = getUp();


if (keysPressed.contains(KeyCode.W)) {
    movRoot.setTranslateX(movRoot.getTranslateX() + forward.getX() * speed);
    movRoot.setTranslateY(movRoot.getTranslateY() + forward.getY() * speed);
    movRoot.setTranslateZ(movRoot.getTranslateZ() + forward.getZ() * speed);
}

if (keysPressed.contains(KeyCode.S)) {
    movRoot.setTranslateX(movRoot.getTranslateX() - forward.getX() * speed);
    movRoot.setTranslateY(movRoot.getTranslateY() - forward.getY() * speed);
    movRoot.setTranslateZ(movRoot.getTranslateZ() - forward.getZ() * speed);
}

if (keysPressed.contains(KeyCode.D)) {
    movRoot.setTranslateX(movRoot.getTranslateX() + right.getX() * speed);
    movRoot.setTranslateY(movRoot.getTranslateY() + right.getY() * speed);
    movRoot.setTranslateZ(movRoot.getTranslateZ() + right.getZ() * speed);
}

if (keysPressed.contains(KeyCode.A)) {
    movRoot.setTranslateX(movRoot.getTranslateX() - right.getX() * speed);
    movRoot.setTranslateY(movRoot.getTranslateY() - right.getY() * speed);
    movRoot.setTranslateZ(movRoot.getTranslateZ() - right.getZ() * speed);
}

if (keysPressed.contains(KeyCode.SPACE)) {
    movRoot.setTranslateX(movRoot.getTranslateX() + up.getX() * speed);
    movRoot.setTranslateY(movRoot.getTranslateY() + up.getY() * speed);
    movRoot.setTranslateZ(movRoot.getTranslateZ() + up.getZ() * speed);
}

if (keysPressed.contains(KeyCode.CONTROL)) {
    movRoot.setTranslateX(movRoot.getTranslateX() - up.getX() * speed);
    movRoot.setTranslateY(movRoot.getTranslateY() - up.getY() * speed);
    movRoot.setTranslateZ(movRoot.getTranslateZ() - up.getZ() * speed);
}

  double Rspeed = 1;

           // Pitch look UP AND DOWN
                if(keysPressed.contains(KeyCode.Q))
                {
                    // yawNode.setRotationAxis(right);
                    yawNode.setRotate(yawNode.getRotate()+Rspeed);

                } 
                if(keysPressed.contains(KeyCode.E)) 
                {
                    // yawNode.setRotationAxis(right);
                    yawNode.setRotate(yawNode.getRotate()-Rspeed);
                }

                // Yaw look LEFT AND RIGHT
                if(keysPressed.contains(KeyCode.R)) 
                {
                    // pitchNode.setRotationAxis(up);
                    pitchNode.setRotate(pitchNode.getRotate()+Rspeed);
                }
                if(keysPressed.contains(KeyCode.T))  
                {
                    // pitchNode.setRotationAxis(up);
                    pitchNode.setRotate(pitchNode.getRotate()-Rspeed);
                }

                // Roll CCW CW
                if(keysPressed.contains(KeyCode.Y)) 
                {
                    // rollNode.setRotationAxis(forward);
                    rollNode.setRotate(rollNode.getRotate()+Rspeed);
                }
                if(keysPressed.contains(KeyCode.U))  
                {
                    //    rollNode.setRotationAxis(forward);
                    rollNode.setRotate(rollNode.getRotate()-Rspeed);
                }
      }


      private Point3D getForward() {
    return rollNode.localToSceneTransformProperty().get()
            .deltaTransform(new Point3D(0, 0, 1))
            .normalize();
}

private Point3D getUp() {
    return rollNode.localToSceneTransformProperty().get()
            .deltaTransform(new Point3D(0, -1, 0))
            .normalize();
}

private Point3D getRight() {
    return rollNode.localToSceneTransformProperty().get()
            .deltaTransform(new Point3D(1, 0, 0))
            .normalize();
}


/// ============= HELPER ================//
/// 
/// 
/// 
/// 
private void addAxisBoxes(Group root) {
    double offset = 50;
    double boxSize = 5;

    // Positive axis colors
    PhongMaterial redPos = new PhongMaterial(Color.RED);
    PhongMaterial greenPos = new PhongMaterial(Color.LIME);
    PhongMaterial bluePos = new PhongMaterial(Color.BLUE);

    // Negative axis colors (dimmer)
    PhongMaterial redNeg = new PhongMaterial(Color.RED.darker());
    PhongMaterial greenNeg = new PhongMaterial(Color.LIME.darker());
    PhongMaterial blueNeg = new PhongMaterial(Color.BLUE.darker());

    // +X, -X
    Box boxPosX = new Box(boxSize, boxSize, boxSize);
    boxPosX.setTranslateX(offset);
    boxPosX.setMaterial(redPos);

    Box boxNegX = new Box(boxSize, boxSize, boxSize);
    boxNegX.setTranslateX(-offset);
    boxNegX.setMaterial(redNeg);

    // +Y, -Y
    Box boxPosY = new Box(boxSize, boxSize, boxSize);
    boxPosY.setTranslateY(offset);
    boxPosY.setMaterial(greenPos);

    Box boxNegY = new Box(boxSize, boxSize, boxSize);
    boxNegY.setTranslateY(-offset);
    boxNegY.setMaterial(greenNeg);

    // +Z, -Z
    Box boxPosZ = new Box(boxSize, boxSize, boxSize);
    boxPosZ.setTranslateZ(offset);
    boxPosZ.setMaterial(bluePos);

    Box boxNegZ = new Box(boxSize, boxSize, boxSize);
    boxNegZ.setTranslateZ(-offset);
    boxNegZ.setMaterial(blueNeg);

    // Add all boxes to the root
    root.getChildren().addAll(boxPosX, boxNegX, boxPosY, boxNegY, boxPosZ, boxNegZ);
}


    // -------------------------------------------------------------------------
    // ------------------------------ GRID -------------------------------------
    // -------------------------------------------------------------------------

    private Group makeGrid(int size, int gap) {
        Group g = new Group();
        int half = size / 2;

        for (int i = -half; i <= half; i += gap) {
            g.getChildren().add(createGridLine(-half, 0, i, half, 0, i, 2, Color.GRAY));
            g.getChildren().add(createGridLine(i, 0, -half, i, 0, half, 2, Color.GRAY));
        }

        return g;
    }

    private Cylinder createGridLine(double x1, double y1, double z1,
                                    double x2, double y2, double z2,
                                    double thickness, Color color)
    {
        double dx = x2 - x1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx*dx + dz*dz);

        Cylinder line = new Cylinder(thickness, length);
        line.setMaterial(new PhongMaterial(color));

        line.setTranslateX((x1 + x2) / 2);
        line.setTranslateY(y1);
        line.setTranslateZ((z1 + z2) / 2);

        if (dx == 0) {
            line.setRotationAxis(Rotate.X_AXIS);
            line.setRotate(90);
        } else {
            line.setRotationAxis(Rotate.Z_AXIS);
            line.setRotate(90);
        }

        return line;
    }

    public static void main(String[] args) { launch(); }
}


// -----------------------------------------------------------------------------
// ----------------------------- Vector3f Helper --------------------------------
// -----------------------------------------------------------------------------


// import java.util.HashSet;

// import javafx.animation.AnimationTimer;
// import javafx.application.Application;
// import javafx.geometry.Point3D;
// import javafx.scene.Group;
// import javafx.scene.PerspectiveCamera;
// import javafx.scene.Scene;
// import javafx.scene.input.KeyCode;
// import javafx.scene.paint.Color;
// import javafx.scene.paint.PhongMaterial;
// import javafx.scene.shape.Box;
// import javafx.scene.transform.Affine;
// import javafx.stage.Stage;

// // ======================= QUATERNION CLASS ============================
// class Quat {

//     final double w, x, y, z;

//     Quat(double w, double x, double y, double z) {
//         this.w = w; this.x = x; this.y = y; this.z = z;
//     }

//     static Quat identity() {
//         return new Quat(1, 0, 0, 0);
//     }

//     // Create from axis-angle
//     static Quat fromAxisAngle(Point3D axis, double angleDeg) {
//         double rad = Math.toRadians(angleDeg);
//         double s = Math.sin(rad / 2);
//         return new Quat(Math.cos(rad / 2), axis.getX()*s, axis.getY()*s, axis.getZ()*s);
//     }

//     // Multiply two quaternions (combines rotations)
//     Quat multiply(Quat b) {
//         return new Quat(
//                 w*b.w - x*b.x - y*b.y - z*b.z,
//                 w*b.x + x*b.w + y*b.z - z*b.y,
//                 w*b.y - x*b.z + y*b.w + z*b.x,
//                 w*b.z + x*b.y - y*b.x + z*b.w
//         );
//     }

//     // Convert quaternion to JavaFX Affine matrix
//     Affine toAffine() {
//         double xx = x*x, yy = y*y, zz = z*z;
//         double xy = x*y, xz = x*z, yz = y*z;
//         double wx = w*x, wy = w*y, wz = w*z;

//         return new Affine(
//                 1 - 2*(yy+zz), 2*(xy - wz),   2*(xz + wy),   0,
//                 2*(xy + wz),   1 - 2*(xx+zz), 2*(yz - wx),   0,
//                 2*(xz - wy),   2*(yz + wx),   1 - 2*(xx+yy), 0
//         );
//     }
// }



// // ============================ APP ===================================
// public class Main extends Application {

//     private final HashSet<KeyCode> keys = new HashSet<>();

//     private Group camGroup = new Group();
//     private Quat camOrientation = Quat.identity();

//     @Override
//     public void start(Stage stage) {
//         // --- Box object
//         Box box = new Box(200,200,200);
//         box.setMaterial(new PhongMaterial(Color.ORANGE));

//         Group objectGroup = new Group(box);

//         // --- Camera inside camera group
//         PerspectiveCamera camera = new PerspectiveCamera(true);
//         camera.setNearClip(0.1);
//         camera.setFarClip(10000);

//         camGroup.getChildren().add(camera);
//         camGroup.setTranslateZ(-600);

//         // --- Root
//         Group root = new Group(objectGroup, camGroup);

//         Scene scene = new Scene(root, 1200, 800, true);
//         scene.setFill(Color.SILVER);
//         scene.setCamera(camera);

//         // --- key press tracking
//         scene.setOnKeyPressed(e -> keys.add(e.getCode()));
//         scene.setOnKeyReleased(e -> keys.remove(e.getCode()));

//         // --- Animation loop
//         new AnimationTimer() {
//             @Override
//             public void handle(long now) {
//                 handleControls();
//             }
//         }.start();

//         stage.setTitle("Quaternion Camera Example");
//         stage.setScene(scene);
//         stage.show();
//     }


//     // -------------------- CAMERA ROTATION --------------------
//     private void applyRotation() {
//         // Overwrite camGroup transform entirely
//         camGroup.getTransforms().setAll(camOrientation.toAffine());
//     }


//     private void handleControls() {
//         double speed = 5;
//         double rotSpeed = 1; // degrees per frame

//         // -------- ROTATION COMMANDS --------
//         if (keys.contains(KeyCode.Q)) rotatePitch(-rotSpeed);  // look up
//         if (keys.contains(KeyCode.E)) rotatePitch(+rotSpeed);  // look down
//         if (keys.contains(KeyCode.R)) rotateYaw(+rotSpeed);    // look left
//         if (keys.contains(KeyCode.T)) rotateYaw(-rotSpeed);    // look right
//         if (keys.contains(KeyCode.Y)) rotateRoll(-rotSpeed);   // roll CCW
//         if (keys.contains(KeyCode.U)) rotateRoll(+rotSpeed);   // roll CW

//         applyRotation();

//         // -------- MOVEMENT --------
//         Point3D f = getForward();
//         Point3D r = getRight();
//         Point3D u = getUp();

//         if (keys.contains(KeyCode.W)) move(f.multiply(speed));
//         if (keys.contains(KeyCode.S)) move(f.multiply(-speed));
//         if (keys.contains(KeyCode.D)) move(r.multiply(speed));
//         if (keys.contains(KeyCode.A)) move(r.multiply(-speed));
//         if (keys.contains(KeyCode.SPACE)) move(u.multiply(speed));
//         if (keys.contains(KeyCode.CONTROL)) move(u.multiply(-speed));
//     }


//     private void move(Point3D d) {
//         camGroup.setTranslateX(camGroup.getTranslateX() + d.getX());
//         camGroup.setTranslateY(camGroup.getTranslateY() + d.getY());
//         camGroup.setTranslateZ(camGroup.getTranslateZ() + d.getZ());
//     }


//     // -------------------- QUATERNION ROTATIONS --------------------
//     private void rotatePitch(double deg) {
//         Quat q = Quat.fromAxisAngle(new Point3D(1,0,0), deg);
//         camOrientation = q.multiply(camOrientation);
//     }

//     private void rotateYaw(double deg) {
//         Quat q = Quat.fromAxisAngle(new Point3D(0,1,0), deg);
//         camOrientation = q.multiply(camOrientation);
//     }

//     private void rotateRoll(double deg) {
//         Quat q = Quat.fromAxisAngle(new Point3D(0,0,1), deg);
//         camOrientation = q.multiply(camOrientation);
//     }


//     // -------------------- DIRECTION VECTORS (FROM MATRIX) --------------------
//     private Point3D getForward() {
//         return camGroup.localToSceneTransformProperty().get()
//                 .deltaTransform(new Point3D(0,0,1)).normalize();
//     }

//     private Point3D getUp() {
//         return camGroup.localToSceneTransformProperty().get()
//                 .deltaTransform(new Point3D(0,-1,0)).normalize();
//     }

//     private Point3D getRight() {
//         return camGroup.localToSceneTransformProperty().get()
//                 .deltaTransform(new Point3D(1,0,0)).normalize();
//     }


//     public static void main(String[] args) {
//         launch(args);
//     }
// }
