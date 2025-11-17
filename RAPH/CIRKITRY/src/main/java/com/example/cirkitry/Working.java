// package com.example.cirkitry;

// import java.util.HashSet;

// import javafx.animation.AnimationTimer;
// import javafx.application.Application;
// import javafx.scene.Group;
// import javafx.scene.Scene;
// import javafx.scene.SceneAntialiasing;
// import javafx.scene.SubScene;
// import javafx.scene.control.Label;
// import javafx.scene.input.KeyCode;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.paint.PhongMaterial;
// import javafx.scene.shape.Box;
// import javafx.scene.shape.Cylinder;
// import javafx.scene.transform.Rotate;
// import javafx.stage.Stage;




// public class Main extends Application {


//     private CameraController camera;
//     public HashSet<KeyCode> keysPressed = new HashSet<>();
    

//     @Override
//     public void start(Stage stage) {
//         Group root3D = new Group();

//         Group grid = makeGrid(400, 50);
//         root3D.getChildren().add(grid);
//         addAxisBoxes(root3D);

//         SubScene subScene3D = new SubScene(root3D, 1400, 800, true, SceneAntialiasing.BALANCED);
//         subScene3D.setFill(Color.BLACK);
//         camera = new CameraController(subScene3D);



//         // --- GUI Overlay ---
//         Label cameraPosLabel = new Label();
//         cameraPosLabel.setTextFill(Color.WHITE);
//         VBox guiOverlay = new VBox(cameraPosLabel);
//         guiOverlay.setPickOnBounds(false);

//         StackPane root = new StackPane();
//         root.getChildren().addAll(subScene3D, guiOverlay);

//         Scene scene = new Scene(root, 1000, 800, true);
//         scene.setFill(Color.BLACK);

// // Mouse look
// // scene.setOnMousePressed(camera::onMousePressed);
// // scene.setOnMouseDragged(camera::onMouseDragged);

// // Keyboard input
// scene.setOnKeyPressed(e -> {
//     camera.keysPressed.add(e.getCode());
//     keysPressed.add(e.getCode());

// });
// scene.setOnKeyReleased(e -> {
//     camera.keysPressed.remove(e.getCode());
    
//       keysPressed.remove(e.getCode());
// });

// // Game loop
// AnimationTimer timer = new AnimationTimer() {
//     // private long lastTime = -1;

//     @Override
//     public void handle(long now) {
//         // if (lastTime < 0) lastTime = now;
//         // double deltaTime = (now - lastTime) / 1e9;
//         // lastTime = now;

//         camera.handleInput(1);  // movement speed
//         // camera.handleRotation(deltaTime);      // rotation keys

//                  Vector3f camPos = camera.getPosition();

//                  Vector3f camFor = camera.getForward();

//                  Vector3f camUp = camera.getUp();
//                  Vector3f camRight = camera.getRight();

//                 // Update GUI
//                 cameraPosLabel.setText(String.format(
//                         "Camera Position: [X: %.1f, Y: %.1f, Z: %.1f]\nForward: [%.2f, %.2f, %.2f]\nUp: [%.2f, %.2f, %.2f]\nRight: [%.2f, %.2f, %.2f]",
//                         camPos.x, camPos.y, camPos.z, camFor.x,camFor.y,camFor.z,camUp.x,camUp.y,camUp.z,camRight.x,camRight.y,camRight.z
//                 ));

//         InputHandle();
//     }
// };
// timer.start();

//         stage.setScene(scene);
//         stage.setTitle("Smooth Free-Fly Camera");
//         stage.show();
//     }

   
// /////// ======== INPUT HANDLE =============== //////
// /// 
// /// 
//       private void InputHandle()
//       {
          
//       }


// /// ============= HELPER ================//
// /// 
// /// 
// /// 
// /// 
// private void addAxisBoxes(Group root) {
//     double offset = 50;
//     double boxSize = 5;

//     // Positive axis colors
//     PhongMaterial redPos = new PhongMaterial(Color.RED);
//     PhongMaterial greenPos = new PhongMaterial(Color.LIME);
//     PhongMaterial bluePos = new PhongMaterial(Color.BLUE);

//     // Negative axis colors (dimmer)
//     PhongMaterial redNeg = new PhongMaterial(Color.RED.darker());
//     PhongMaterial greenNeg = new PhongMaterial(Color.LIME.darker());
//     PhongMaterial blueNeg = new PhongMaterial(Color.BLUE.darker());

//     // +X, -X
//     Box boxPosX = new Box(boxSize, boxSize, boxSize);
//     boxPosX.setTranslateX(offset);
//     boxPosX.setMaterial(redPos);

//     Box boxNegX = new Box(boxSize, boxSize, boxSize);
//     boxNegX.setTranslateX(-offset);
//     boxNegX.setMaterial(redNeg);

//     // +Y, -Y
//     Box boxPosY = new Box(boxSize, boxSize, boxSize);
//     boxPosY.setTranslateY(offset);
//     boxPosY.setMaterial(greenPos);

//     Box boxNegY = new Box(boxSize, boxSize, boxSize);
//     boxNegY.setTranslateY(-offset);
//     boxNegY.setMaterial(greenNeg);

//     // +Z, -Z
//     Box boxPosZ = new Box(boxSize, boxSize, boxSize);
//     boxPosZ.setTranslateZ(offset);
//     boxPosZ.setMaterial(bluePos);

//     Box boxNegZ = new Box(boxSize, boxSize, boxSize);
//     boxNegZ.setTranslateZ(-offset);
//     boxNegZ.setMaterial(blueNeg);

//     // Add all boxes to the root
//     root.getChildren().addAll(boxPosX, boxNegX, boxPosY, boxNegY, boxPosZ, boxNegZ);
// }


//     // -------------------------------------------------------------------------
//     // ------------------------------ GRID -------------------------------------
//     // -------------------------------------------------------------------------

//     private Group makeGrid(int size, int gap) {
//         Group g = new Group();
//         int half = size / 2;

//         for (int i = -half; i <= half; i += gap) {
//             g.getChildren().add(createGridLine(-half, 0, i, half, 0, i, 2, Color.GRAY));
//             g.getChildren().add(createGridLine(i, 0, -half, i, 0, half, 2, Color.GRAY));
//         }

//         return g;
//     }

//     private Cylinder createGridLine(double x1, double y1, double z1,
//                                     double x2, double y2, double z2,
//                                     double thickness, Color color)
//     {
//         double dx = x2 - x1;
//         double dz = z2 - z1;
//         double length = Math.sqrt(dx*dx + dz*dz);

//         Cylinder line = new Cylinder(thickness, length);
//         line.setMaterial(new PhongMaterial(color));

//         line.setTranslateX((x1 + x2) / 2);
//         line.setTranslateY(y1);
//         line.setTranslateZ((z1 + z2) / 2);

//         if (dx == 0) {
//             line.setRotationAxis(Rotate.X_AXIS);
//             line.setRotate(90);
//         } else {
//             line.setRotationAxis(Rotate.Z_AXIS);
//             line.setRotate(90);
//         }

//         return line;
//     }

//     public static void main(String[] args) { launch(); }
// }


// // -----------------------------------------------------------------------------
// // ----------------------------- Vector3f Helper --------------------------------
// // -----------------------------------------------------------------------------
