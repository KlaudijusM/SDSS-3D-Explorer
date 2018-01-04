/*
 * The MIT License
 *
 * Copyright 2018 klaudijus.
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
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author klaudijus
 */
public class MainMenu extends AbstractAppState implements ScreenController {

    private Node rootNode;
    private final Node localRootNode = new Node("Loading Screen");
    private AssetManager assetManager;
    private FlyByCamera flyCam;
    private ViewPort guiViewPort;
    private AudioRenderer audioRenderer;
    private InputManager inputManager;
    private AppStateManager stateManager;
    private SimpleApplication theApp;
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    
    public MainMenu (SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        audioRenderer = app.getAudioRenderer();
        guiViewPort = app.getGuiViewPort();
        stateManager = app.getStateManager();
        flyCam = app.getFlyByCamera();
        theApp = app;
    }
    
    /**
     * Initializes parameters and creates the overlay.
     * @param stateManager The applications State Manager.
     * @param app The application itself.
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
        
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
        assetManager, inputManager, audioRenderer, guiViewPort);
        // Create a new NiftyGUI object
        nifty = niftyDisplay.getNifty();
        // Read XML and initialise custom ScreenController
        nifty.fromXml("Interface/MainMenu.xml", "start", this);
        // Attach the Nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        // Disable the fly cam
        flyCam.setDragToRotate(true);
    }
    
    public void startExploration(){
        stateManager.getState(FullUniverseExplorer.class).initGalaxy();
        stateManager.detach(stateManager.getState(MainMenu.class));
    }
    
    public void exitExploration(){
        theApp.stop();
    }
    
    public void startOptionScreen(){
        
    }
    
    /**
     * Cleans up, detaches elements and removes itself.
     */
    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        niftyDisplay.cleanup();
        nifty.exit();
        flyCam.setDragToRotate(false);
        guiViewPort.detachScene(localRootNode);
        guiViewPort.removeProcessor(niftyDisplay);
        super.cleanup();
    }  

    @Override
    public void bind(Nifty nifty, Screen screen) {
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }
}
