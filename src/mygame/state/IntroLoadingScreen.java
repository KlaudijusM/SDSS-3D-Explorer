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
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Klaudijus
 */
public class IntroLoadingScreen extends AbstractAppState {

    private final Node rootNode;
    private final Node localRootNode = new Node("Loading Screen");
    private final AssetManager assetManager;
    private final FlyByCamera flyCam;
    private final ViewPort guiViewPort;
    private final AudioRenderer audioRenderer;
    private final InputManager inputManager;
    private final AppStateManager stateManager;
    private final SimpleApplication theApp;
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    
    public IntroLoadingScreen (SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        audioRenderer = app.getAudioRenderer();
        guiViewPort = app.getGuiViewPort();
        stateManager = app.getStateManager();
        flyCam = app.getFlyByCamera();
        theApp = app;
        
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
        
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
        assetManager, inputManager, audioRenderer, guiViewPort);
        /** Create a new NiftyGUI object */
        nifty = niftyDisplay.getNifty();
        /** Read your XML and initialize your custom ScreenController */
        nifty.fromXml("Interface/LoadingScreen.xml", "start");
        // nifty.fromXml("Interface/helloworld.xml", "start", new MySettingsScreen(data));
        // attach the Nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        // disable the fly cam
        flyCam.setDragToRotate(true);
        stateManager.attach(new FullUniverseExplorer(theApp));
        stateManager.getState(FullUniverseExplorer.class).setEnabled(false);
        
        
    }
    
    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        niftyDisplay.cleanup();
        nifty.exit();
        flyCam.setDragToRotate(false);
        guiViewPort.clearScenes();
        guiViewPort.detachScene(localRootNode);
        guiViewPort.removeProcessor(niftyDisplay);
        
        super.cleanup();
    }
    
    @Override
    public void update(float tpf) {
    }
}
