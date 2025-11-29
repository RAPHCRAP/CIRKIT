package com.example.cirkitry.graphic;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Arrow extends MeshView {
    private double height;    // Height of the triangular face
    private double base;      // Base width of the triangular face  
    private double depth;     // Elongation depth (length of the prism)
    
    public Arrow(double height, double base, double depth) {
        this.height = height;
        this.base = base;
        this.depth = depth;
        initArrow();
    }
    
    private void initArrow() {
        TriangleMesh mesh = new TriangleMesh();
        
        float h = (float) height;
        float b = (float) base;
        float d = (float) depth;
        float halfBase = b / 2.0f;
        
        // Vertices for a triangular prism
        // Front triangle vertices (z = 0)
        // Back triangle vertices (z = -depth)
float[] points = {
    // Front triangle vertices (z = depth/2)
    0, h/2, d/2,           // 0: Top front (apex)
    -halfBase, -h/2, d/2,  // 1: Bottom left front  
    halfBase, -h/2, d/2,   // 2: Bottom right front
    
    // Back triangle vertices (z = -depth/2) 
    0, h/2, -d/2,          // 3: Top back (apex)
    -halfBase, -h/2, -d/2, // 4: Bottom left back
    halfBase, -h/2, -d/2   // 5: Bottom right back
};
        
        // Texture coordinates
        float[] texCoords = {
            // Basic UV mapping
            0.5f, 0.0f,  // 0: top front
            0.0f, 1.0f,  // 1: bottom left front
            1.0f, 1.0f,  // 2: bottom right front
            0.5f, 0.0f,  // 3: top back
            0.0f, 1.0f,  // 4: bottom left back
            1.0f, 1.0f   // 5: bottom right back
        };
        
        // Faces - 8 triangles (2 for each rectangular side + 2 for triangular ends)
        int[] faces = {
            // ===== TRIANGULAR ENDS (2 faces) =====
            
            // Front triangle (facing forward)
            0, 0,  1, 1,  2, 2,
            
            // Back triangle (facing backward)  
            3, 3,  5, 5,  4, 4,
            
            // ===== RECTANGULAR SIDES (6 faces - 2 triangles each) =====
            
            // Bottom rectangular face (two triangles)
            1, 1,  4, 4,  5, 5,
            1, 1,  5, 5,  2, 2,
            
            // Left rectangular face (two triangles)
            0, 0,  3, 3,  4, 4,
            0, 0,  4, 4,  1, 1,
            
            // Right rectangular face (two triangles)
            0, 0,  2, 2,  5, 5,
            0, 0,  5, 5,  3, 3
        };
        
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);
        
        this.setMesh(mesh);
        setDefaultMaterial();
    }
    
    private void setDefaultMaterial() {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        material.setSpecularColor(Color.WHITE);
        this.setMaterial(material);
    }
    
    // Getters and setters
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
        refreshMesh();
    }
    
    public double getBase() {
        return base;
    }
    
    public void setBase(double base) {
        this.base = base;
        refreshMesh();
    }
    
    public double getDepth() {
        return depth;
    }
    
    public void setDepth(double depth) {
        this.depth = depth;
        refreshMesh();
    }
    
    public void setSize(double height, double base, double depth) {
        this.height = height;
        this.base = base;
        this.depth = depth;
        refreshMesh();
    }
    
    private void refreshMesh() {
        initArrow();
    }
    
    public void setColor(Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color.brighter());
        this.setMaterial(material);
    }
    
    // Method to get face count for verification
    public int getFaceCount() {
        return 8; // 8 triangles total
    }
    
    // Method to get vertex count for verification  
    public int getVertexCount() {
        return 6; // 3 front + 3 back
    }
    
    // Method to get actual face count (5 polygonal faces)
    public int getPolygonalFaceCount() {
        return 5; // 2 triangles + 3 rectangles
    }
}