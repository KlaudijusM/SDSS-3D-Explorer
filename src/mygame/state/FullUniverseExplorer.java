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
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import mygame.UniverseMesh;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author Klaudijus
 */
public class FullUniverseExplorer extends AbstractAppState {

    public Integer defaultMoveSpeed = 1000; //Sets the default camera speed.
    public Float quickTravelMultiplier = 10f;
    public Float slowTravelMultiplier = 0.5f;
    private final Integer defaultFrustrumFar = 999999999; //Sets the far camera frustrum
    private final String fileName = "galaxy_list.csv";
    
    public KeyTrigger SpeedUpHotkey = new KeyTrigger(KeyInput.KEY_LSHIFT);
    public KeyTrigger SlowDownHotkey = new KeyTrigger(KeyInput.KEY_LCONTROL);
    public KeyTrigger ExtraSlowDownHotkey = new KeyTrigger(KeyInput.KEY_LMENU);
    public KeyTrigger SwitchViewHotkey = new KeyTrigger(KeyInput.KEY_SPACE);
    
    private final Node rootNode;
    private final Node localRootNode = new Node("Universe Explorer");
    private final AssetManager assetManager;
    private final FlyByCamera flyCam;
    private final Camera cam;
    private final InputManager inputManager;
    private UniverseMesh galaxyUniverse;
    private final AppStateManager stateManager;
    private final Node guiNode;
    private final SimpleApplication theApp;
    private BitmapText hudText;
    private Float GalaxyData[][];
    
    /**
     * Constructs the application state
     * @param app The application itself
     */
    public FullUniverseExplorer (SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        flyCam = app.getFlyByCamera();
        guiNode = app.getGuiNode();
        cam = app.getCamera();
        inputManager = app.getInputManager();
        stateManager = app.getStateManager();
        theApp = app;
    }
    
    /**
     * Initializes the application state
     * @param stateManager The applications State Manager.
     * @param app The application itself.
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode); //Attaches the local node to the root node.
        readData(); //Reads the data from file
        
        stateManager.detach(stateManager.getState(IntroLoadingScreen.class));
        stateManager.attach(new MainMenu(theApp));
    }
    
    public void setOptions(int tSpeed, float qTravelMult, float sTravelMult, int a, int b, int c, int d){
        defaultMoveSpeed = tSpeed;
        quickTravelMultiplier = qTravelMult;
        slowTravelMultiplier = sTravelMult;
        SpeedUpHotkey = new KeyTrigger(a);
        SlowDownHotkey = new KeyTrigger(b);
        ExtraSlowDownHotkey = new KeyTrigger(c);
        SwitchViewHotkey = new KeyTrigger(d);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        rootNode.detachChild(localRootNode);
        galaxyUniverse.destroyMesh();
        if (hudText != null){
            guiNode.detachChild(hudText);  
        }
        inputManager.removeListener(actionListener);
    }
    
    @Override
    public void update(float tpf) {
        Vector3f cameraPos = cam.getLocation().clone();
        float distance = (float) sqrt((cameraPos.getX() * cameraPos.getX()) + (cameraPos.getY() * cameraPos.getY()) + (cameraPos.getZ() * cameraPos.getZ()));
        updateDistance(distance);
    }
    
    /**
     * Gets the array containing data of each galaxy
     * @return 2D float array containing data of each galaxy
     */
    public Float[][] getPosArray(){
        return GalaxyData;
    }
    
    /**
     * Reads data from the main galaxy data file.
     */
    public void readData(){
        String line = null;

        try {
            FileReader fileReader = 
                new FileReader(fileName);
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            int galaxyCount = 0;
            ArrayList<String> fileLines = new ArrayList<String>();
            bufferedReader.readLine();
            
            while((line = bufferedReader.readLine()) != null) {
                fileLines.add(line);
                galaxyCount++;
            }
            
            GalaxyData = new Float[galaxyCount][6];
            
            galaxyUniverse = new UniverseMesh(rootNode, assetManager, galaxyCount);
            int test2 = 0;
            for (int i = 0; i < galaxyCount; i++){
                String[] parts = fileLines.get(i).split(",");
                try {
                    float cx = Float.parseFloat(parts[1]);
                    float cy = Float.parseFloat(parts[2]);
                    float cz = Float.parseFloat(parts[3]);
                    float redshift = Float.parseFloat(parts[4]);
                    float ra = Float.parseFloat(parts[5]);
                    float dec = Float.parseFloat(parts[6]);
                    float x,y,z;
                    if (redshift == 0){
                        x = cx; y = cy; z = cz;
                    } else {
                        float distance = getDistance(redshift);
                        x = distance * cx;
                        y = distance * cy;
                        z = distance * cz;
                    }
                    
                    if (x != 0 && y != 0 && z != 0) {
                        GalaxyData[i][0] = x;
                        GalaxyData[i][1] = y;
                        GalaxyData[i][2] = z;
                        GalaxyData[i][3] = redshift;
                        GalaxyData[i][4] = ra;
                        GalaxyData[i][5] = dec;
                        galaxyUniverse.AddVertex(x, y, z);
                    } else {
                        test2++;
                        System.out.println(test2);
                    }
                } catch (NumberFormatException e){
                    System.out.println("not a number"); 
                }
            }
            
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
        }
    }
    
    /**
     * Calculates the distance based upon the redshift
     * @param redshift The redshift of the galaxy
     * @return The Distance
     */
    private float getDistance(double redshift){
        float d = (float) (redshift * 299792 / 67.8);
        return d;
    }
    
    public void initGalaxy(){
        flyCam.setMoveSpeed(defaultMoveSpeed);
        cam.setFrustumFar(defaultFrustrumFar);
        cam.onFrameChange();
        
        initKeys();
        drawHud();
        flyCam.setDragToRotate(false);
        galaxyUniverse.createMesh(ColorRGBA.White, "Shaders/UniverseVertex/UniverseVertex.j3md");
        cam.setLocation(new Vector3f(0, 0, 5000));
        stateManager.getState(FullUniverseExplorer.class).setEnabled(true);
    }
        
    /**
     * Initiates keybind mappings.
     */
    private void initKeys() {
        inputManager.addMapping("SpeedUpTravel",  SpeedUpHotkey);
        inputManager.addMapping("SlowDownTravel",  SlowDownHotkey);
        inputManager.addMapping("ExtraSlowDownTravel",  ExtraSlowDownHotkey);
        inputManager.addMapping("SwitchView", SwitchViewHotkey);
        
        inputManager.addListener(actionListener,"SpeedUpTravel", "SlowDownTravel", "ExtraSlowDownTravel", "SwitchView");
    }
    
    /**
     * Adds an action listener to recieve hotkey input.
     */
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            switch (name) {
                case "SpeedUpTravel":
                    flyCam.setMoveSpeed((float) (defaultMoveSpeed * quickTravelMultiplier));
                    if (!keyPressed) flyCam.setMoveSpeed(defaultMoveSpeed);
                    break;
                case "SlowDownTravel":
                    flyCam.setMoveSpeed((float) (defaultMoveSpeed * slowTravelMultiplier));
                    if (!keyPressed) flyCam.setMoveSpeed(defaultMoveSpeed);
                    break;
                case "ExtraSlowDownTravel":
                    flyCam.setMoveSpeed(1);
                    if (!keyPressed) flyCam.setMoveSpeed(defaultMoveSpeed);
                    break;
                case "SwitchView":
                    flyCam.setMoveSpeed(defaultMoveSpeed);
                    cleanup();
                    stateManager.attach(new InDepthUniverseExplorer(theApp));
                    stateManager.getState(InDepthUniverseExplorer.class).setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    };
    
    /**
     * Draws text on screen to show how far is the user from Earth.
     */
    private void drawHud(){
        BitmapFont myFont = assetManager.loadFont("Interface/Fonts/Ubuntu.fnt");
        hudText = new BitmapText(myFont, false);
        hudText.setSize(round(myFont.getCharSet().getRenderedSize() * 1.5));      // font size
        hudText.setColor(ColorRGBA.Red);                             // font color
        hudText.setText("0 Mega Parsecs (Mpc) away from planet Earth");
        hudText.setLocalTranslation((cam.getWidth())/2, hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);
    }
    
    /**
     * Updates on screen text with distance parameter.
     * @param distance Distance to earth from camera.
     */
    private void updateDistance(float distance){
        BigDecimal result;
        result=round2(distance,2);
        hudText.setText(result + " Mega Parsecs (Mpc) away from planet Earth");
    }
    
    /**
     * Formats a float to be rounded up.
     * @param d Float to be formatted.
     * @param decimalPlace Number of decimal places to be formatted to.
     * @return The formatted number.
     */
    private static BigDecimal round2(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       
        return bd;
    }
    
}
