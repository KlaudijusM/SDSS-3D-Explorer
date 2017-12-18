/*
 * The MIT License
 *
 * Copyright 2017 Klaudijus.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

/**
 * 
 * @author Klaudijus
 * Used to create a universe point mesh.
 */
public class UniverseMesh {
    private Node rootNode; //Root Node
    private Node localRootNode = new Node(); //Local Root Node
    private AssetManager assetManager; //Asset Manager
    private Vector3f [] vertices; //Vertices of the mesh as vectors
    private int vertexCount = 0; //Vertex Count
    public Geometry geom;
    public Mesh mesh = new Mesh();
    
    /**
     * Initalizes the UniverseMesh
     * 
     * Sets the local variables equal to the root variables and creates a new
     * array of vertices.
     * 
     * @param rootNode The Root Node of the application.
     * @param assetManager The applications Asset Manager.
     * @param objectSize The number of objects this mesh will have.
     */
    public UniverseMesh(Node rootNode, AssetManager assetManager, int objectSize){
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        vertices = new Vector3f[objectSize];
        this.rootNode.attachChild(this.localRootNode);
    }
    
    /**
     * Adds a vertex to the vertex array.
     * 
     * @param x The X coordinate of a single object (galaxy/star).
     * @param y The Y coordinate of a single object (galaxy/star).
     * @param z The Z coordinate of a single object (galaxy/star).
     */
    public void AddVertex(float x, float y, float z){
        vertices[vertexCount] = new Vector3f(x,y,z);
        vertexCount++;
    }
    
    /**
     * Creates a Universe mesh.
     * 
     * @param color The Color of the mesh
     * @param shaderLocation Relative path to the shader
     * "Shaders/UniverseVertex/UniverseVertex.j3md"
     */
    public void createMesh(ColorRGBA color, String shaderLocation){
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.updateBound();
        geom = new Geometry("OurMesh", mesh);
        mesh.setMode(Mesh.Mode.Points);
        mesh.updateBound();
        mesh.setStatic();
        Material mat = new Material(assetManager, shaderLocation);
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        geom.setMaterial(mat);
        localRootNode.attachChild(geom);
    }
    
    /**
     * Destroys the created mesh.
     */
    public void destroyMesh(){
        rootNode.detachChild(localRootNode);
        mesh = null;
    }
}
