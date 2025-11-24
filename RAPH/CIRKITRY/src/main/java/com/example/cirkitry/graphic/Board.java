package com.example.cirkitry.graphic;

import com.example.cirkitry.scale.Scale;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

public class Board extends MeshView
{


    public Board(String filepath)
    {
        super(createMesh("/tile.jpeg")); 
        initBoard("/tile.jpeg");
    }

    
    private static TriangleMesh createMesh(String filePath)
    {
        Image img = new Image(Board.class.getResource(filePath).toExternalForm());
        double texelScale = (Scale.WCellScale)/100;
        double repeatScale = Scale.WSize;

        float imgW = (float) img.getWidth();
        float imgH = (float) img.getHeight();

        float tileW = (float) (imgW * texelScale);
        float tileH = (float) (imgH * texelScale);

        float worldW = tileW * (float) repeatScale;
        float worldH = tileH * (float) repeatScale;

        float halfW = worldW / 2f;
        float halfH = worldH / 2f;

        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(
            -halfW, 0, -halfH,
             halfW, 0, -halfH,
             halfW, 0,  halfH,
            -halfW, 0,  halfH
        );

        mesh.getTexCoords().addAll(
            0, 0,
            (float) repeatScale, 0,
            (float) repeatScale, (float) repeatScale,
            0, (float) repeatScale
        );

        mesh.getFaces().addAll(
            0,0, 1,1, 2,2,
            0,0, 2,2, 3,3
        );

        return mesh;
    }

    private void initBoard(String filePath) 
    {
        Image img = new Image(getClass().getResource(filePath).toExternalForm());
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(img);
        
        this.setMaterial(mat);
        this.setCullFace(CullFace.NONE);
        this.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
    }


    
}
