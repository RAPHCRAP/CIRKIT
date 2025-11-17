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

    private Rotate pitch = new Rotate(0, Rotate.X_AXIS);  // look up/down
    private Rotate yaw   = new Rotate(0, Rotate.Y_AXIS);  // look left/right
    private Rotate roll  = new Rotate(0, Rotate.Z_AXIS);  // tilt (fan flip)


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

        //  CamGroup.setTranslateX(0);
        // CamGroup.setTranslateY(0);
        // CamGroup.setTranslateZ(0);

        //   camera.translateXProperty().set(0);
        // camera.translateYProperty().set(0);
        // camera.translateZProperty().set(0);

        camGroup.translateXProperty().set(0);
        camGroup.translateYProperty().set(0);
        camGroup.translateZProperty().set(0);

        camGroup.getTransforms().addAll(pitch,yaw,roll);
        

        
        root3D.getChildren().addAll(camGroup,b1);

       

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
    yaw.setAngle(yaw.getAngle() + dx * mouseSensitivity);   // left/right
    pitch.setAngle(pitch.getAngle() - dy * mouseSensitivity); // up/down (invert Y)
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

                 cameraPosLabel.setText(String.format(
                        "Camera Position: [X: %.1f, Y: %.1f, Z: %.1f]\n",
                        camGroup.getTranslateX(), camGroup.getTranslateY(), camGroup.getTranslateZ()));

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
    camGroup.setTranslateX(camGroup.getTranslateX() + forward.getX() * speed);
    camGroup.setTranslateY(camGroup.getTranslateY() + forward.getY() * speed);
    camGroup.setTranslateZ(camGroup.getTranslateZ() + forward.getZ() * speed);
}

if (keysPressed.contains(KeyCode.S)) {
    camGroup.setTranslateX(camGroup.getTranslateX() - forward.getX() * speed);
    camGroup.setTranslateY(camGroup.getTranslateY() - forward.getY() * speed);
    camGroup.setTranslateZ(camGroup.getTranslateZ() - forward.getZ() * speed);
}

if (keysPressed.contains(KeyCode.D)) {
    camGroup.setTranslateX(camGroup.getTranslateX() + right.getX() * speed);
    camGroup.setTranslateY(camGroup.getTranslateY() + right.getY() * speed);
    camGroup.setTranslateZ(camGroup.getTranslateZ() + right.getZ() * speed);
}

if (keysPressed.contains(KeyCode.A)) {
    camGroup.setTranslateX(camGroup.getTranslateX() - right.getX() * speed);
    camGroup.setTranslateY(camGroup.getTranslateY() - right.getY() * speed);
    camGroup.setTranslateZ(camGroup.getTranslateZ() - right.getZ() * speed);
}

if (keysPressed.contains(KeyCode.SPACE)) {
    camGroup.setTranslateX(camGroup.getTranslateX() + up.getX() * speed);
    camGroup.setTranslateY(camGroup.getTranslateY() + up.getY() * speed);
    camGroup.setTranslateZ(camGroup.getTranslateZ() + up.getZ() * speed);
}

if (keysPressed.contains(KeyCode.CONTROL)) {
    camGroup.setTranslateX(camGroup.getTranslateX() - up.getX() * speed);
    camGroup.setTranslateY(camGroup.getTranslateY() - up.getY() * speed);
    camGroup.setTranslateZ(camGroup.getTranslateZ() - up.getZ() * speed);
}

  double Rspeed = 1;

           // Pitch (X axis)
                if(keysPressed.contains(KeyCode.Q)) pitch.setAngle(pitch.getAngle() - Rspeed);  // up
                if(keysPressed.contains(KeyCode.E))  pitch.setAngle(pitch.getAngle() + Rspeed);

                // Yaw (Y axis)
                if(keysPressed.contains(KeyCode.R))  yaw.setAngle(yaw.getAngle() - Rspeed);  // left
                if(keysPressed.contains(KeyCode.T))  yaw.setAngle(yaw.getAngle() + Rspeed);  // right

                // Roll (Z axis)
                if(keysPressed.contains(KeyCode.Y))  roll.setAngle(roll.getAngle() - Rspeed); // CCW
                if(keysPressed.contains(KeyCode.U))  roll.setAngle(roll.getAngle() + Rspeed);  // CW
      }


      private Point3D getForward() {
    return camGroup.localToSceneTransformProperty().get()
            .deltaTransform(new Point3D(0, 0, 1))
            .normalize();
}

private Point3D getUp() {
    return camGroup.localToSceneTransformProperty().get()
            .deltaTransform(new Point3D(0, -1, 0))
            .normalize();
}

private Point3D getRight() {
    return camGroup.localToSceneTransformProperty().get()
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
