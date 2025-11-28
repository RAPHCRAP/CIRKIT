package com.example.cirkitry.handler;

import java.util.ArrayList;
import java.util.List;

import com.example.cirkitry.scale.Scale;

import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;

public class SelectHandler {
    private SubScene scene3D;
    private Camera camera;
    private Parent root;
    private double mouseX, mouseY;
     private Box selectionBox;
    private double cellSize = Scale.WCellScale; 
    private int selectedCellX;
    private int selectedCellY;
    
    public SelectHandler(SubScene scene3D) {
        this.scene3D = scene3D;
        this.camera = scene3D.getCamera();
        this.root = scene3D.getRoot();



           // Initialize the selection box
        selectionBox = createSelectionBox();
        if (root instanceof Group) 
            {
        ((Group) root).getChildren().add(selectionBox);
         } else 
            {
    System.err.println("SubScene root is not a Group, cannot add selection box!");
              } 

        selectionBox.setVisible(false); // Hide initially
        
        setupMouseHandlers();
    }

    private Box createSelectionBox() {
        Box box = new Box(cellSize, cellSize, 1); // Thin box to visualize XY plane
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseColor(Color.rgb(255, 255, 0, 0.4)); // Semi-transparent yellow
        mat.setSpecularColor(Color.TRANSPARENT);
        box.setMaterial(mat);
        return box;
    }

    
    private void setupMouseHandlers() {
        scene3D.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            
            updateSelection();
        });
        
        scene3D.setOnMouseClicked(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            handleSelection();
        });
    }

    public Point3D screenToWorld(double screenX, double screenY) {
    if (camera instanceof PerspectiveCamera) {
        return screenToWorldPerspective(screenX, screenY);
    } 
    if (camera instanceof ParallelCamera) {
        return screenToWorldOrthographic(screenX, screenY);
    }
    return null;
}
private Point3D screenToWorldPerspective(double screenX, double screenY) {
    if (!(camera instanceof PerspectiveCamera)) return null;

    // --- 1. Camera position and basis vectors ---
    Point3D camPos = camera.localToScene(Point3D.ZERO);                // camera world position
    Point3D forward = toWorldDirection(new Point3D(0, 0, 1)).normalize(); // camera forward
    Point3D up = toWorldDirection(new Point3D(0, -1, 0)).normalize();       // camera up
    Point3D right = forward.crossProduct(up).normalize();                  // camera right

    // --- 2. Normalized screen coordinates [-1, 1] ---
    double nx = (screenX / scene3D.getWidth()) * 2 - 1;      // -1 left, +1 right
    double ny = 1 - (screenY / scene3D.getHeight()) * 2;     // +1 top, -1 bottom

    // --- 3. Apply FOV to compute angular offsets ---
    double fovY = Math.toRadians(((PerspectiveCamera)camera).getFieldOfView());
    double aspect = scene3D.getWidth() / scene3D.getHeight();
    double fovX = 2 * Math.atan(Math.tan(fovY / 2) * aspect);

    double offsetX = nx * Math.tan(fovX / 2);
    double offsetY = ny * Math.tan(fovY / 2);

    // --- 4. Compute ray direction in world space ---
    Point3D rayDir = forward
            .add(right.multiply(offsetX))
            .add(up.multiply(offsetY))
            .normalize();

    // --- 5. Intersect ray with Z=0 plane ---
    Point3D planeNormal = new Point3D(0, 0, 1); // Z=0 plane normal
    double denom = rayDir.dotProduct(planeNormal);
    if (Math.abs(denom) < 1e-10) return null; // ray is parallel to plane

    double t = -camPos.getZ() / denom;
    if (t < 0) return null; // intersection behind camera

    return camPos.add(rayDir.multiply(t));
}


private Point3D screenToWorldOrthographic(double screenX, double screenY) {
    if (!(camera instanceof ParallelCamera)) return null;

    // --- 1. Camera position and basis vectors ---
    Point3D camPos = camera.localToScene(Point3D.ZERO);                // camera world position
    Point3D forward = toWorldDirection(new Point3D(0, 0, 1)).normalize(); // camera forward
    Point3D up = toWorldDirection(new Point3D(0, -1, 0)).normalize();       // camera up
    Point3D right = forward.crossProduct(up).normalize();                  // camera right

    // --- 2. Normalized screen coordinates [-1, 1] ---
    double nx = (screenX / scene3D.getWidth()) * 2 - 1;      // -1 left, +1 right
    double ny = 1 - (screenY / scene3D.getHeight()) * 2;     // +1 top, -1 bottom

    // --- 3. Orthographic screen scale (world units) ---
    double orthoWidth = 20;   // customize as needed
    double orthoHeight = 20;  // customize as needed

    // --- 4. Compute ray origin in world space ---
    Point3D rayOrigin = camPos
            .add(right.multiply(nx * orthoWidth / 2))
            .add(up.multiply(ny * orthoHeight / 2));

    // --- 5. Ray direction is camera forward ---
    Point3D rayDir = forward;

    // --- 6. Intersect ray with Z=0 plane ---
    Point3D planeNormal = new Point3D(0, 0, 1); // Z=0 plane
    double denom = rayDir.dotProduct(planeNormal);
    if (Math.abs(denom) < 1e-10) return null; // ray parallel → no intersection

    double t = -rayOrigin.getZ() / denom;
    if (t < 0) return null; // intersection behind camera

    return rayOrigin.add(rayDir.multiply(t));
}



private Point3D toWorldDirection(Point3D camDir) {

    // world position of camera origin
    Point3D origin  = camera.localToScene(Point3D.ZERO);

    // world position of a point one unit in camDir direction
    Point3D end     = camera.localToScene(camDir);

    // subtract to get real-world direction
    return end.subtract(origin).normalize();
}


     private void updateSelection() {
        Point3D worldPos = screenToWorld(mouseX, mouseY);
        
        
        if (worldPos != null) {

            Point3D cell = worldToGridCell(worldPos);

            // Update selection box position
            placeSelectionBox(cell);

            // Optional: call your actual selection logic
            selectAtPosition(worldPos);
        }
    }
    
    private void handleSelection() {
        Point3D worldPos = screenToWorld(mouseX, mouseY);
        
        if (worldPos != null) {
            // Perform actual selection
            selectAtPosition(worldPos);
        }
    }
    
private Point3D worldToGridCell(Point3D worldPos) {
    if (worldPos == null) return null;

    int cellX = (int) Math.floor(worldPos.getX() / cellSize);
    int cellY = (int) Math.floor(worldPos.getY() / cellSize);

    return new Point3D(cellX, cellY, 0);
}

private void placeSelectionBox(Point3D cell) {
        if (cell == null) return;

        double centerX = cell.getX() * cellSize + cellSize / 2.0;
        double centerY = cell.getY() * cellSize + cellSize / 2.0;

        selectionBox.setTranslateX(centerX);
        selectionBox.setTranslateY(centerY);
        selectionBox.setTranslateZ(0); // On Z=0 plane

        selectionBox.setVisible(true);
    }

    private void highlightAtPosition(Point3D worldPos) {
        // Find nodes near this world position
        // You can raycast or use spatial partitioning
        for (Node node : getAllSelectableNodes()) {
            if (isNodeAtPosition(node, worldPos)) {
                // Highlight logic
                node.setEffect(new Glow(0.5));
            }
        }
    }
    
    private void selectAtPosition(Point3D worldPos) {
        // // Your selection logic here
        // System.out.printf("Selected at: (%.2f, %.2f, %.2f)\n", 
        //     worldPos.getX(), worldPos.getY(), worldPos.getZ());
    }
    
    private List<Node> getAllSelectableNodes() {
        List<Node> selectable = new ArrayList<>();
        collectSelectableNodes(root, selectable);
        return selectable;
    }
    
    private void collectSelectableNodes(Node node, List<Node> selectable) {
        if (node instanceof MeshView || node instanceof Group) {
            selectable.add(node);
        }
        
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                collectSelectableNodes(child, selectable);
            }
        }
    }
    
    private boolean isNodeAtPosition(Node node, Point3D worldPos) {
        // Simple bounding box check - you can make this more sophisticated
        Bounds bounds = node.getBoundsInParent();
        return bounds.contains(worldPos.getX(), worldPos.getY(), worldPos.getZ());
    }

    // YAY
}