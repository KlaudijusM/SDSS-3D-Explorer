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
package mygame.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import mygame.UniverseMesh;

/**
 *
 * @author klaudijus
 */
public class InDepthUniverseExplorer extends AbstractAppState {
    
    private final Integer spreadAmount = 10000; //How much should the universe be scaled by
    private final Integer defaultMoveSpeed = 200; //Sets the default camera speed.
    private final Integer defaultFrustrumFar = 999999999; //Sets the far camera frustrum
    
    private final Node rootNode;
    private final Node localRootNode = new Node("Universe Explorer");
    private final AssetManager assetManager;
    private final FlyByCamera flyCam;
    private final Camera cam;
    private UniverseMesh galaxyUniverse;
    private final AppStateManager stateManager;
    private Float GalaxyData[][];
    private Boolean createdGalaxies[];
    private BufferedImage createdGalaxiesImages[];
    private Geometry geom[];
    private Quad galaxy = new Quad(5,5);
    
    public InDepthUniverseExplorer (SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        flyCam = app.getFlyByCamera();
        cam = app.getCamera();
        stateManager = app.getStateManager();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
        
        flyCam.setMoveSpeed(defaultMoveSpeed);
        cam.setFrustumFar(defaultFrustrumFar);
        cam.onFrameChange();
        
        GalaxyData = stateManager.getState(FullUniverseExplorer.class).getPosArray();
        createdGalaxies = new Boolean[GalaxyData.length];
        createdGalaxiesImages = new BufferedImage[GalaxyData.length]; 
        geom = new Geometry[GalaxyData.length];
        for (int i = 0; i < GalaxyData.length; i++){
            GalaxyData[i][0] = GalaxyData[i][0] * spreadAmount;
            GalaxyData[i][1] = GalaxyData[i][1] * spreadAmount;
            GalaxyData[i][2] = GalaxyData[i][2] * spreadAmount;
            createdGalaxies[i] = false;
        }
        
        stateManager.getState(FullUniverseExplorer.class).cleanup();
        stateManager.detach(stateManager.getState(FullUniverseExplorer.class));
        stateManager.getState(InDepthUniverseExplorer.class).setEnabled(true);
        
        galaxyUniverse = new UniverseMesh(rootNode, assetManager, GalaxyData.length);
        
        for (Float[] GalaxyData1 : GalaxyData) {
            galaxyUniverse.AddVertex(GalaxyData1[0], GalaxyData1[1], GalaxyData1[2]);
        }
        
        galaxyUniverse.createMesh(ColorRGBA.White, "Shaders/CloseUniverseMesh/UniverseVertex.j3md");
    }
    
    @Override
    public void update(float tpf) {
        Vector3f cameraPos = cam.getLocation().clone();
        
        for (int i = 0; i < GalaxyData.length; i++){
            float distance = (float) sqrt(pow(GalaxyData[i][0] - cameraPos.x,2) + pow(GalaxyData[i][1] - cameraPos.y, 2) + pow(GalaxyData[i][2] - cameraPos.z,2));

            if (distance < 500){
                if (createdGalaxies[i] != true){
                    geom[i] = new Geometry("Quad", galaxy);
                    geom[i].setLocalTranslation(GalaxyData[i][0], GalaxyData[i][1], GalaxyData[i][2]);
                    geom[i].setLocalRotation(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*90, new Vector3f(1,0,0)));
                    geom[i].lookAt(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f));
                    Material mat = new Material(assetManager,
                      "Common/MatDefs/Misc/Unshaded.j3md");
                    
                    URL imgUrl = getGalaxyImgUrl(GalaxyData[i][4], GalaxyData[i][5]);
                    try {
                        createdGalaxiesImages[i] = ImageIO.read(imgUrl);
                    } catch (IOException ex) {
                        Logger.getLogger(InDepthUniverseExplorer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    AWTLoader loader = new AWTLoader();
                    Image load = loader.load(createdGalaxiesImages[i], false);
                    Texture2D texture1 = new Texture2D(load) {};

//                    mat.setColor("Color", ColorRGBA.Red);
                    mat.setTexture("ColorMap", texture1);
                    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
                    geom[i].setMaterial(mat);
                    rootNode.attachChild(geom[i]);
                    createdGalaxies[i] = true;
                }
            } else if (createdGalaxies[i]){
                createdGalaxies[i] = false;
                createdGalaxiesImages[i] = null;
                rootNode.detachChild(geom[i]);
            }
        }        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        galaxyUniverse.destroyMesh();
    }
    
    public URL getGalaxyImgUrl(float ra, float dec){
        URL url = null;
        try {
            url = new URL("http://skyserver.sdss.org/dr14/SkyServerWS/ImgCutout/getjpeg?TaskName=Skyserver.Chart.ShowNearest&ra=" + ra + "&dec=" + dec + "&scale=0.5");
        } catch (MalformedURLException ex) {
            Logger.getLogger(InDepthUniverseExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return url;
    }
    
}
