/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 *
 * @author Klaudijus
 */
public class UniverseMesh {
    public double hubbleConst = 67.8;
    public double c = 299792;
    private Node rNod;
    private AssetManager aMan;
    private Vector3f [] vertices;
    private int vertexCount = 0;
    public Geometry geom;
    public Mesh mesh = new Mesh();
    
    public UniverseMesh(Node rootNode, AssetManager assetManager, int objectSize){
        rNod = rootNode;
        aMan = assetManager;
        vertices = new Vector3f[objectSize];
    }
    
    private int ConvertRaDecToX (double ra, double dec, double redshift){
        double d = redshift * c / hubbleConst;
        double x = d * sin(dec) * cos(ra); 
        return (int) round(x);
    }
    
    private int ConvertRaDecToY (double ra, double dec, double redshift){
        double d = redshift * c / hubbleConst;
        double y = d * sin(dec) * sin(ra); 
        return (int) round(y);
    }
    
    private int ConvertRaDecToZ (double ra, double dec, double redshift){
        double d = redshift * c / hubbleConst;
        double z = d * cos(dec); 
        return (int)round(z);
    }
    
//    public void AddVertex(double ra, double dec, double redshift){
//        int x = ConvertRaDecToX(ra, dec, redshift), y = ConvertRaDecToY(ra, dec, redshift), z = ConvertRaDecToZ(ra, dec, redshift);
//        vertices[vertexCount] = new Vector3f(x,y,z);
//        vertexCount++;
//    }
    private int test2 = 0;
    public void AddVertex(float cx, float cy, float cz, float redshift){
        float distance = getDistance(redshift);
        float x,y,z;
        if (distance == 0){
            x = cx; y = cy; z = cz;
        } else {
            x = distance * cx;
            y = distance * cy;
            z = distance * cz;
        }
        vertices[vertexCount] = new Vector3f(x,y,z);
        vertexCount++;
    }
    
    private float getToCenter(float x, float y, float z){
        return (float) sqrt(pow(x,2) + pow(y,2) + pow(z,2));
    }
    
    private float getDistance(double redshift){
        float d = (float) (redshift * c / hubbleConst);
        return d;
    }
    
    public void createMesh(ColorRGBA color){
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.updateBound();
        geom = new Geometry("OurMesh", mesh);
        mesh.setMode(Mesh.Mode.Points);
        mesh.updateBound();
        mesh.setStatic();
        Material mat = new Material(aMan, "Shaders/UniverseVertex/UniverseVertex.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        geom.setMaterial(mat);
        rNod.attachChild(geom);
    }
    
}
