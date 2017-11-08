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
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

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
//    private UniverseMesh starUniverse;
    private final AppStateManager stateManagerFromApp;
    
    public FullUniverseExplorer (SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        flyCam = app.getFlyByCamera();
        cam = app.getCamera();
        inputManager = app.getInputManager();
        stateManagerFromApp = app.getStateManager();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
        
        readData();
        flyCam.setMoveSpeed(5000);
        cam.setFrustumFar(50000);
        cam.onFrameChange();
        initKeys();
        stateManagerFromApp.detach(stateManagerFromApp.getState(IntroLoadingScreen.class));
        stateManager.getState(FullUniverseExplorer.class).setEnabled(true);
    }
    
    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        
        super.cleanup();
    }
    
    @Override
    public void update(float tpf) {
//        int maxdis = 256;
//        int vertexCount = galaxyUniverse.mesh.getVertexCount();
//        float[] colorArray = new float[vertexCount * 4];
//        Vector3f camPos = cam.getLocation();
//        FloatBuffer colorBuffer = galaxyUniverse.mesh.getFloatBuffer(VertexBuffer.Type.Color);
//
//        FloatBuffer vertices = galaxyUniverse.mesh.getFloatBuffer(VertexBuffer.Type.Position);
//
//        vertices.rewind();
//
//        float value = 0, valueR = 0, valueG = 0, valueB = 0, valueA = 0;
//        int colorIndex = 0;
//
//        for (int i = 0; i < vertexCount; i++) {
//            float posx = vertices.get();
//            float posy = vertices.get();
//            float posz = vertices.get();
//
//            Vector3f vertexPos = new Vector3f(posx, posy, posz);
//
//            float dis = vertexPos.distance(camPos);
//
//            if (dis > maxdis) {
//                value = 0;
//            } else {
//                value = (maxdis - dis) / maxdis;
//                valueR = 0.3f;
//                valueG = 0.3f;
//                valueB = 0.3f;
//                valueA = 0f;
//
//                colorArray[colorIndex++] = valueR;
//                colorArray[colorIndex++] = valueG/2;
//                colorArray[colorIndex++] = valueB/4;
//                colorArray[colorIndex++] = 0f;
//            }
//        }
//        galaxyUniverse.mesh.setBuffer(VertexBuffer.Type.Color, 4, colorArray);
//        galaxyUniverse.mesh.getBuffer(VertexBuffer.Type.Color).setUpdateNeeded();
    }
    
    public void readData(){
        String fileName = "galaxy_list.csv";

        // This will reference one line at a time
        String line = null;
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            int i = 0;
            galaxyUniverse = new UniverseMesh(rootNode, assetManager, 2697417);
            while((line = bufferedReader.readLine()) != null) {
                if (i > 0){
                   String[] parts = line.split(",");
                   try {
//                      double ra = Double.parseDouble(parts[1]);
//                      double dec = Double.parseDouble(parts[2]);
                        double cx = Double.parseDouble(parts[1]);
                        double cy = Double.parseDouble(parts[2]);
                        double cz = Double.parseDouble(parts[3]);
                      double redshift = Double.parseDouble(parts[4]);
//                      galaxyUniverse.AddVertex(ra, dec, redshift);
                        galaxyUniverse.AddVertex(cx, cy, cz, redshift);
                   }catch (NumberFormatException e){
                      System.out.println("not a number"); 
                   } 
                }
                i++;
            }   
            galaxyUniverse.createMesh(ColorRGBA.White);
            // Always close files.
            i=0;
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
            // Or we could just do this: 
            // ex.printStackTrace();
        }
//        fileName = "star_list.csv";
//
//        // This will reference one line at a time
//        line = null;
//        
//        try {
//            // FileReader reads text files in the default encoding.
//            FileReader fileReader = 
//                new FileReader(fileName);
//
//            // Always wrap FileReader in BufferedReader.
//            BufferedReader bufferedReader = 
//                new BufferedReader(fileReader);
//            
//            int i = 0;
//            starUniverse = new UniverseMesh(rootNode, assetManager, 1729180);
//            while((line = bufferedReader.readLine()) != null) {
//                if (i > 0){
//                   String[] parts = line.split(",");
//                   try {
//                      double ra = Double.parseDouble(parts[1]);
//                      double dec = Double.parseDouble(parts[2]);
//                      double redshift = Double.parseDouble(parts[4]);
//                      starUniverse.AddVertex(ra, dec, redshift);
//                   }catch (NumberFormatException e){
//                      System.out.println("not a number"); 
//                   } 
//                }
//                i++;
//            }   
//            starUniverse.createMesh(ColorRGBA.Yellow);
//            // Always close files.
//            i=0;
//            bufferedReader.close();         
//        }
//        catch(FileNotFoundException ex) {
//            System.out.println(
//                "Unable to open file '" + 
//                fileName + "'");                
//        }
//        catch(IOException ex) {
//            System.out.println(
//                "Error reading file '" 
//                + fileName + "'");                  
//            // Or we could just do this: 
//            // ex.printStackTrace();
//        }
    }
        
    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("SpeedUpTravel",  new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("SlowDownTravel",  new KeyTrigger(KeyInput.KEY_LCONTROL));
        // Add the names to the action listener.
        inputManager.addListener(actionListener,"SpeedUpTravel", "SlowDownTravel");
    }
    
    private ActionListener actionListener = new ActionListener() {
      public void onAction(String name, boolean keyPressed, float tpf) {
          if (name.equals("SpeedUpTravel")){
              flyCam.setMoveSpeed(10000);
              if (!keyPressed){
                  flyCam.setMoveSpeed(5000);
              }
          }
          else if (name.equals("SlowDownTravel")){
              flyCam.setMoveSpeed(100);
              if (!keyPressed){
                  flyCam.setMoveSpeed(5000);
              }
          }
      }
    };
}
