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
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author Klaudijus
 */
public class FullUniverseExplorer extends AbstractAppState {

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
    private Nifty nifty;
    private BitmapText hudText;
    
    public FullUniverseExplorer (SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        flyCam = app.getFlyByCamera();
        guiNode = app.getGuiNode();
        cam = app.getCamera();
        audioRenderer = app.getAudioRenderer();
        guiViewPort = app.getGuiViewPort();
        inputManager = app.getInputManager();
        stateManagerFromApp = app.getStateManager();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
        
        readData();
        flyCam.setMoveSpeed(1000);
        cam.setFrustumFar(999999999);
        cam.onFrameChange();
        initKeys();
        stateManagerFromApp.detach(stateManagerFromApp.getState(IntroLoadingScreen.class));
        stateManager.getState(FullUniverseExplorer.class).setEnabled(true);
        drawHud();

    }
    
    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        
        super.cleanup();
    }
    
    @Override
    public void update(float tpf) {
        Vector3f cameraPos = cam.getLocation().clone();
        float distance = (float) sqrt((cameraPos.getX() * cameraPos.getX()) + (cameraPos.getY() * cameraPos.getY()) + (cameraPos.getZ() * cameraPos.getZ()));
        updateDistance(distance);
    }
    
    public void readData(){
        String fileName = "galaxy_list.csv";
        
        String line = null;

        try {
            FileReader fileReader = 
                new FileReader(fileName);
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            int galaxyCount = 0;
            ArrayList<String> fileLines = new ArrayList<String>();
            line = bufferedReader.readLine();
            
            while((line = bufferedReader.readLine()) != null) {
                fileLines.add(line);
                galaxyCount++;
            }
            
            galaxyUniverse = new UniverseMesh(rootNode, assetManager, galaxyCount);
            int test2 = 0;
            for (int i = 0; i < galaxyCount; i++){
                String[] parts = fileLines.get(i).split(",");
                try {
                    float cx = Float.parseFloat(parts[1]);
                    float cy = Float.parseFloat(parts[2]);
                    float cz = Float.parseFloat(parts[3]);
                    float redshift = Float.parseFloat(parts[4]);
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
                        galaxyUniverse.AddVertex(cx, cy, cz, redshift);
                    } else {
                        test2++;
                        System.out.println(test2);
                    }
                } catch (NumberFormatException e){
                    System.out.println("not a number"); 
                }
            }
            
            galaxyUniverse.createMesh(ColorRGBA.White);
            
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
    
    private double getDistanceToCenter(float x, float y, float z){
        return sqrt(pow(x,2) + pow(y,2) + pow(z,2));
    }
    
    private float getDistance(double redshift){
        float d = (float) (redshift * 299792 / 67.8);
        return d;
    }
        
    private void initKeys() {
        inputManager.addMapping("SpeedUpTravel",  new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("SlowDownTravel",  new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addMapping("ExtraSlowDownTravel",  new KeyTrigger(KeyInput.KEY_LMENU));
        
        inputManager.addListener(actionListener,"SpeedUpTravel", "SlowDownTravel", "ExtraSlowDownTravel");
    }
    
    private ActionListener actionListener = new ActionListener() {
      public void onAction(String name, boolean keyPressed, float tpf) {
          if (name.equals("SpeedUpTravel")){
              flyCam.setMoveSpeed(100000);
              if (!keyPressed){
                  flyCam.setMoveSpeed(1000);
              }
          }
          else if (name.equals("SlowDownTravel")){
              flyCam.setMoveSpeed(500);
              if (!keyPressed){
                  flyCam.setMoveSpeed(1000);
              }
          }
          else if (name.equals("ExtraSlowDownTravel")){
              flyCam.setMoveSpeed(1);
              if (!keyPressed){
                  flyCam.setMoveSpeed(1000);
              }
          }
      }
    };
    
    private void drawHud(){
        BitmapFont myFont = assetManager.loadFont("Interface/Fonts/Ubuntu.fnt");
        hudText = new BitmapText(myFont, false);
        hudText.setSize(round(myFont.getCharSet().getRenderedSize() * 1.5));      // font size
        hudText.setColor(ColorRGBA.Red);                             // font color
        hudText.setText("0 Mega Parsecs (Mpc) away from planet Earth");
        hudText.setLocalTranslation((cam.getWidth())/2, hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);
    }
    
    private void updateDistance(float distance){
        BigDecimal result;
        result=round2(distance,2);
        hudText.setText(result + " Mega Parsecs (Mpc) away from planet Earth");
    }
    
    private static BigDecimal round2(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       
        return bd;
    }
    
}
