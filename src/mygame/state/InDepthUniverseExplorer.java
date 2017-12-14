/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import de.lessvoid.nifty.Nifty;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import mygame.UniverseMesh;

/**
 *
 * @author klaudijus
 */
public class InDepthUniverseExplorer extends AbstractAppState {
    
    private final Node rootNode;
    private final Node localRootNode = new Node("Universe Explorer");
    private final AssetManager assetManager;
    private final FlyByCamera flyCam;
    private final Camera cam;
    private final InputManager inputManager;
    private UniverseMesh galaxyUniverse;
    private final AppStateManager stateManagerFromApp;
    private final Node guiNode;
    private NiftyJmeDisplay niftyDisplay;
    private final AudioRenderer audioRenderer;
    private final ViewPort guiViewPort;
    private final SimpleApplication theApp;
    private Nifty nifty;
    private BitmapText hudText;
    private Float GalaxyData[][];
    private Vector3f [] vertices;
    private Boolean createdGalaxies[];
    private Geometry geom[];
    private Quad gal = new Quad(5,5);
    
    public InDepthUniverseExplorer (SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        flyCam = app.getFlyByCamera();
        guiNode = app.getGuiNode();
        cam = app.getCamera();
        audioRenderer = app.getAudioRenderer();
        guiViewPort = app.getGuiViewPort();
        inputManager = app.getInputManager();
        stateManagerFromApp = app.getStateManager();
        theApp = app;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
        
        flyCam.setMoveSpeed(100);
        cam.setFrustumFar(999999999);
        cam.onFrameChange();
        GalaxyData = stateManagerFromApp.getState(FullUniverseExplorer.class).getPosArray();
        createdGalaxies = new Boolean[GalaxyData.length];
        geom = new Geometry[GalaxyData.length];
        for (int i = 0; i < GalaxyData.length; i++){
            GalaxyData[i][0] = GalaxyData[i][0] * 1000000;
            GalaxyData[i][1] = GalaxyData[i][1] * 1000000;
            GalaxyData[i][2] = GalaxyData[i][2] * 1000000;
            createdGalaxies[i] = false;
        }
        stateManagerFromApp.getState(FullUniverseExplorer.class).cleanup();
        stateManagerFromApp.detach(stateManagerFromApp.getState(FullUniverseExplorer.class));
        stateManager.getState(InDepthUniverseExplorer.class).setEnabled(true);
        vertices = new Vector3f[GalaxyData.length];
        for (int i = 0; i < GalaxyData.length; i++){
            vertices[i] = new Vector3f(GalaxyData[i][0], GalaxyData[i][1], GalaxyData[i][2]);
        }
        createMesh(ColorRGBA.White);
    }
    
    public void createMesh(ColorRGBA color){
        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.updateBound();
        Geometry geom = new Geometry("OurMesh", mesh);
        mesh.setMode(Mesh.Mode.Points);
        mesh.updateBound();
        mesh.setStatic();
        Material mat = new Material(assetManager, "Shaders/CloseUniverseMesh/UniverseVertex.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
    }
    
    @Override
    public void update(float tpf) {
        Vector3f cameraPos = cam.getLocation().clone();
        
        for (int i = 0; i < GalaxyData.length; i++){
            float distance = (float) sqrt(pow(GalaxyData[i][0] - cameraPos.x,2) + pow(GalaxyData[i][1] - cameraPos.y, 2) + pow(GalaxyData[i][2] - cameraPos.z,2));

            if (distance < 1000){
                if (createdGalaxies[i] != true){
                    geom[i] = new Geometry("Quad", gal);
                    geom[i].setLocalTranslation(GalaxyData[i][0], GalaxyData[i][1], GalaxyData[i][2]);
                    geom[i].setLocalRotation(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*90, new Vector3f(1,0,0)));
                    Material mat = new Material(assetManager,
                      "Common/MatDefs/Misc/Unshaded.j3md");
                    Texture img = assetManager.loadTexture("Textures/galaxy.png");
//                    mat.setColor("Color", ColorRGBA.Red);
                    mat.setTexture("ColorMap", img);
                    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
                    geom[i].setMaterial(mat);
                    rootNode.attachChild(geom[i]);
                    createdGalaxies[i] = true;
                }
            } else if (createdGalaxies[i]){
                createdGalaxies[i] = false;
                rootNode.detachChild(geom[i]);
            }
        }        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
}
