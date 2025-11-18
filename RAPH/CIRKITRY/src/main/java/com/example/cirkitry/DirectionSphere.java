package com.example.cirkitry;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class DirectionSphere extends Group {
    private static final double SPHERE_RADIUS = 20.0;
    private static final double RING_RADIUS = 25.0;
    private static final double RING_THICKNESS = 1.5;
    private static final double CYLINDER_LENGTH = 40.0;
    private static final double CYLINDER_RADIUS = 2.0;
    
    private final Sphere sphere;
    private final Cylinder xRing, yRing, zRing;
    private final Cylinder forwardCylinder;
    
    public DirectionSphere() {
        super();
        
        // Create white sphere
        sphere = new Sphere(SPHERE_RADIUS);
        PhongMaterial sphereMaterial = new PhongMaterial(Color.WHITE);
        sphere.setMaterial(sphereMaterial);
        
        // Create colored rings using cylinders
        xRing = createRing(Color.RED, new Rotate(90, Rotate.Z_AXIS)); // Red ring around X-axis
        yRing = createRing(Color.BLUE, new Rotate(90, Rotate.X_AXIS)); // Blue ring around Y-axis  
        zRing = createRing(Color.GREEN, new Rotate(90,Rotate.Y_AXIS)); // Green ring around Z-axis
        
        // Create forward-pointing cylinder (initially pointing in Z+ direction)
        forwardCylinder = new Cylinder(CYLINDER_RADIUS, CYLINDER_LENGTH);
        PhongMaterial cylinderMaterial = new PhongMaterial(Color.YELLOW);
        forwardCylinder.setMaterial(cylinderMaterial);

        forwardCylinder.getTransforms().add(new Rotate(90,Rotate.X_AXIS));
        
        // Position cylinder so it extends forward from sphere surface
        forwardCylinder.setTranslateZ(SPHERE_RADIUS + CYLINDER_LENGTH / 2);
        
        // Add all components to the group
        this.getChildren().addAll(sphere, xRing, yRing, zRing, forwardCylinder);
    }
    
    private Cylinder createRing(Color color, Rotate orientation) {
        // Create a thin cylinder that will act as a ring
        Cylinder ring = new Cylinder(RING_THICKNESS / 2, RING_RADIUS * 2);
        PhongMaterial material = new PhongMaterial(color);
        ring.setMaterial(material);
        
        // Apply the orientation rotation
        ring.getTransforms().add(orientation);
        
        return ring;
    }
    
    /**
     * Alternative method to create better-looking rings using multiple cylinders
     */
    public DirectionSphere(boolean useDetailedRings) {
        super();
        
        if (!useDetailedRings) {
            // Use the simple constructor
            DirectionSphere simple = new DirectionSphere();
            this.getChildren().addAll(simple.getChildren());
            this.sphere = simple.sphere;
            this.xRing = simple.xRing;
            this.yRing = simple.yRing;
            this.zRing = simple.zRing;
            this.forwardCylinder = simple.forwardCylinder;
            return;
        }
        
        // Create white sphere
        sphere = new Sphere(SPHERE_RADIUS);
        PhongMaterial sphereMaterial = new PhongMaterial(Color.WHITE);
        sphere.setMaterial(sphereMaterial);
        
        // Create detailed rings using multiple cylinders
        xRing = createDetailedRing(Color.RED, "X");
        yRing = createDetailedRing(Color.BLUE, "Y");
        zRing = createDetailedRing(Color.GREEN, "Z");
        
        // Create forward-pointing cylinder
        forwardCylinder = new Cylinder(CYLINDER_RADIUS, CYLINDER_LENGTH);
        PhongMaterial cylinderMaterial = new PhongMaterial(Color.YELLOW);
        forwardCylinder.setMaterial(cylinderMaterial);
        forwardCylinder.setTranslateZ(SPHERE_RADIUS + CYLINDER_LENGTH / 2);
        
        this.getChildren().addAll(sphere, xRing, yRing, zRing, forwardCylinder);
    }
    
    private Cylinder createDetailedRing(Color color, String axis) {
        // For a more ring-like appearance, we can create a cylinder and position it differently
        Cylinder ring = new Cylinder(RING_THICKNESS, RING_RADIUS * 2);
        PhongMaterial material = new PhongMaterial(color);
        ring.setMaterial(material);
        
        switch (axis) {
            case "X":
                // Red ring around X-axis - cylinder along Y, rotated to be around X
                ring.setRotationAxis(Rotate.Z_AXIS);
                ring.setRotate(90);
                break;
            case "Y":
                // Blue ring around Y-axis - cylinder along X, rotated to be around Y  
                ring.setRotationAxis(Rotate.X_AXIS);
                ring.setRotate(90);
                break;
            case "Z":
                // Green ring around Z-axis - cylinder along X (default)
                // No additional rotation needed
                break;
        }
        
        return ring;
    }
    
    /**
     * Even better approach: Create circular rings using a Group of multiple small cylinders
     */
    private Group createCircularRing(Color color, String axis) {
        Group ringGroup = new Group();
        int segments = 16; // Number of segments to form the ring
        double segmentAngle = 360.0 / segments;
        
        for (int i = 0; i < segments; i++) {
            Cylinder segment = new Cylinder(RING_THICKNESS / 2, RING_RADIUS * Math.PI / segments);
            PhongMaterial material = new PhongMaterial(color);
            segment.setMaterial(material);
            
            double angle = Math.toRadians(i * segmentAngle);
            double x = RING_RADIUS * Math.cos(angle);
            double y = RING_RADIUS * Math.sin(angle);
            
            switch (axis) {
                case "X":
                    // Ring around X-axis (in YZ plane)
                    segment.setRotationAxis(Rotate.X_AXIS);
                    segment.setRotate(90);
                    segment.setTranslateY(y);
                    segment.setTranslateZ(x);
                    break;
                case "Y":
                    // Ring around Y-axis (in XZ plane)
                    segment.setRotationAxis(Rotate.Y_AXIS);
                    segment.setRotate(90);
                    segment.setTranslateX(x);
                    segment.setTranslateZ(y);
                    break;
                case "Z":
                    // Ring around Z-axis (in XY plane)
                    segment.setTranslateX(x);
                    segment.setTranslateY(y);
                    break;
            }
            
            // Rotate each segment to be tangent to the circle
            segment.setRotationAxis(Rotate.Z_AXIS);
            segment.setRotate(i * segmentAngle);
            
            ringGroup.getChildren().add(segment);
        }
        
        return ringGroup;
    }
    
    /**
     * Gets the forward direction vector of this group in world coordinates
     */
    public Point3D getForwardDirection() {
        return this.localToSceneTransformProperty().get()
                .deltaTransform(new Point3D(0, 0, 1))
                .normalize();
    }
    
    /**
     * Gets the right direction vector of this group in world coordinates
     */
    public Point3D getRightDirection() {
        return this.localToSceneTransformProperty().get()
                .deltaTransform(new Point3D(1, 0, 0))
                .normalize();
    }
    
    /**
     * Gets the up direction vector of this group in world coordinates
     */
    public Point3D getUpDirection() {
        return this.localToSceneTransformProperty().get()
                .deltaTransform(new Point3D(0, 1, 0))
                .normalize();
    }
    
    // Getters for the components
    public Sphere getSphere() { return sphere; }
    public Cylinder getXRing() { return xRing; }
    public Cylinder getYRing() { return yRing; }
    public Cylinder getZRing() { return zRing; }
    public Cylinder getForwardCylinder() { return forwardCylinder; }
    
    /**
     * Example usage method to demonstrate the forward vector
     */
    public void printOrientation() {
        Point3D forward = getForwardDirection();
        Point3D right = getRightDirection();
        Point3D up = getUpDirection();
        
        System.out.printf("Forward: (%.2f, %.2f, %.2f)%n", 
            forward.getX(), forward.getY(), forward.getZ());
        System.out.printf("Right:   (%.2f, %.2f, %.2f)%n", 
            right.getX(), right.getY(), right.getZ());
        System.out.printf("Up:      (%.2f, %.2f, %.2f)%n", 
            up.getX(), up.getY(), up.getZ());
    }
    
    /**
     * Method to use circular rings instead of simple cylinders
     */
    public void useCircularRings() {
        // Remove existing rings
        this.getChildren().removeAll(xRing, yRing, zRing);
        
        // Create and add circular rings
        Group xCircularRing = createCircularRing(Color.RED, "X");
        Group yCircularRing = createCircularRing(Color.BLUE, "Y"); 
        Group zCircularRing = createCircularRing(Color.GREEN, "Z");
        
        this.getChildren().addAll(xCircularRing, yCircularRing, zCircularRing);
    }
}