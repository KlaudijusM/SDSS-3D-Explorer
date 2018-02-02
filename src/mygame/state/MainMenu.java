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
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Menu;
import de.lessvoid.nifty.controls.MenuItemActivatedEvent;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventTopicSubscriber;

/**
 *
 * @author Klaudijus
 */
public class MainMenu extends AbstractAppState implements ScreenController, RawInputListener {

    private Node rootNode;
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
    private Properties defaultProps;
    private Properties prop = new Properties();
    private int DefaultTravelSpeed;
    private float QuickTravelMultiplier;
    private float SlowTravelMultiplier;
    private int KeyQuickTravel;
    private int KeySlowTravel;
    private int KeyExtraSlowTravel;
    private int KeySwitchView;
    private Element popup;
    private boolean ftKeyRemapInitiated = false;
    private boolean stKeyRemapInitiated = false;
    private boolean estKeyRemapInitiated = false;
    private boolean swKeyRemapInitiated = false;
    
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
        inputManager.addRawInputListener(this);
        
        rootNode.attachChild(localRootNode);
        setDefaultProperties();
        
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
        loadPropertiesFromFile();

        }
    
    /**
     * Switches states to the explorer.
     */
    public void startExploration(){
        stateManager.getState(FullUniverseExplorer.class).initGalaxy();
        stateManager.detach(stateManager.getState(MainMenu.class));
    }
    
    /**
     * Sets up a specific key to be remapped.
     * @param buttonNumber Which button from order to be remapped
     */
    public void setUpKeyRemap(String buttonNumber){
        int buttonNo = Integer.parseInt(buttonNumber);
        System.out.println("------Key Remap Initiated------");
        System.out.println("Switching off all controls...");
        disableNiftyControls();
        System.out.println("Done!");
        System.out.println("Showing Info Select Panel");
        setSelectKeyPopUpVisibility(true);
        String buttonText = null;
        switch (buttonNo){
                case 1: {
                    buttonText = getOptionsScreenNiftyControl("ftkButton").getText();
                    ftKeyRemapInitiated = true;
                    break;
                }
                case 2: {
                    buttonText = getOptionsScreenNiftyControl("stkButton").getText();
                    stKeyRemapInitiated = true;
                    break;
                }
                case 3: {
                    buttonText = getOptionsScreenNiftyControl("estkButton").getText();
                    estKeyRemapInitiated = true;
                    break;
                }
                case 4: {
                    buttonText = getOptionsScreenNiftyControl("swkButton").getText();
                    swKeyRemapInitiated = true;
                    break;
                }
        }
        System.out.println("Current key mapped: " + buttonText.toUpperCase());
        System.out.println("Listening for keys...");
    }
    
    public boolean keyAlreadyMapped(int key){
        if (KeyQuickTravel == key || KeySlowTravel == key || KeyExtraSlowTravel == key || KeySwitchView == key){
            return true;
        }
        else return false;
    }

    @Override
    public void beginInput() {
    }

    @Override
    public void endInput() {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
        if(evt.isReleased()){
            keyInputToString(evt.getKeyCode());
            if(ftKeyRemapInitiated || stKeyRemapInitiated || estKeyRemapInitiated || swKeyRemapInitiated){
                validatePressedKey(evt.getKeyCode());
            }
            else {
                System.out.println("No key listening events are running! Ignoring pressed key");
            }
        }
    }
    
    /**
     * Gets a button from the options screen by id.
     * @param id The id of the button
     * @return The button
     */
    public Button getOptionsScreenNiftyControl(String id){
        return nifty.getScreen("optionsScreen").findNiftyControl(id, Button.class);
    }
    
    /**
     * Changes the visibility of the Select Key PopUp
     * @param status If it should be visible or not
     */
    public void setSelectKeyPopUpVisibility(boolean status){
        nifty.getScreen("optionsScreen").findElementById("changeKeyPopUp").setVisible(status);
    }
    
    /**
     * Validates a pressed key to check if the user wants to cancel, unasign or set a key and if the key is permitted.
     * @param key Key that the user has pressed
     */
    public void validatePressedKey(int key){
        if (keyAlreadyMapped(key)) { System.out.println("Duplicate key mapping detected!");}
        else if (keyInputToString(key) == "escape"){
            System.out.println("ESC key pressed, no buttons altered. Reenabling button controls");
            System.out.println(System.getProperty("line.separator"));
        }
        else if (keyInputToString(key) == "delete"){
            System.out.println("DELETE key pressed, unassigning set key!");
            System.out.println(System.getProperty("line.separator"));
            if (ftKeyRemapInitiated){
                KeyQuickTravel = KeyInput.KEY_UNKNOWN;
                getOptionsScreenNiftyControl("ftkButton").setText("N/A");
                ftKeyRemapInitiated = false;
            }
            else if (stKeyRemapInitiated){
                KeySlowTravel = KeyInput.KEY_UNKNOWN;
                getOptionsScreenNiftyControl("stkButton").setText("N/A");
                stKeyRemapInitiated = false;
            }
            else if (estKeyRemapInitiated){
                KeyExtraSlowTravel = KeyInput.KEY_UNKNOWN;
                getOptionsScreenNiftyControl("estkButton").setText("N/A");
                estKeyRemapInitiated = false;
            }
            else if (swKeyRemapInitiated){
                KeySwitchView = KeyInput.KEY_UNKNOWN;
                getOptionsScreenNiftyControl("swkButton").setText("N/A");
                swKeyRemapInitiated = false;
            }
        }
        else if (keyInputToString(key) == null || "N/A".equals(keyInputToString(key))){ System.out.println("Unavailable key! Mapping Cancelled.");}
        else {
            System.out.println("Key Input Recieved! Key recieved: " + keyInputToString(key));
            System.out.println(System.getProperty("line.separator"));
            if (ftKeyRemapInitiated) {
                ftKeyRemapInitiated = false;
                getOptionsScreenNiftyControl("ftkButton").setText(keyInputToString(key).toUpperCase());
                KeyQuickTravel = key;
            }
            else if (stKeyRemapInitiated) {
                stKeyRemapInitiated = false;
                getOptionsScreenNiftyControl("stkButton").setText(keyInputToString(key).toUpperCase());
                KeySlowTravel = key;
            }
            else if (estKeyRemapInitiated) {
                estKeyRemapInitiated = false;
                getOptionsScreenNiftyControl("estkButton").setText(keyInputToString(key).toUpperCase());
                KeyExtraSlowTravel = key;
            }
            else if (swKeyRemapInitiated) {
                swKeyRemapInitiated = false;
                getOptionsScreenNiftyControl("swkButton").setText(keyInputToString(key).toUpperCase());
                KeySwitchView = key;
            }
        }
        setSelectKeyPopUpVisibility(false);
        enableNiftyControls();
        updatePropertiesInFullUniverseExplorer();
    }
    
    /**
     * Disables interface controls for the user.
     */
    public void disableNiftyControls(){
        Screen tempScreen = nifty.getScreen("optionsScreen");
        tempScreen.findNiftyControl("ftkButton", Button.class).disable();
        tempScreen.findNiftyControl("stkButton", Button.class).disable();
        tempScreen.findNiftyControl("estkButton", Button.class).disable();
        tempScreen.findNiftyControl("swkButton", Button.class).disable();
        tempScreen.findNiftyControl("ntspeed", Slider.class).disable();
        tempScreen.findNiftyControl("qtmultiplier", Slider.class).disable();
        tempScreen.findNiftyControl("stmultiplier", Slider.class).disable();
        tempScreen.findNiftyControl("backButton", Button.class).disable();
    }
    
    /**
     * Enables interface controls for the user.
     */
    public void enableNiftyControls(){
        Screen tempScreen = nifty.getScreen("optionsScreen");
        tempScreen.findNiftyControl("ftkButton", Button.class).enable();
        tempScreen.findNiftyControl("stkButton", Button.class).enable();
        tempScreen.findNiftyControl("estkButton", Button.class).enable();
        tempScreen.findNiftyControl("swkButton", Button.class).enable();
        tempScreen.findNiftyControl("ntspeed", Slider.class).enable();
        tempScreen.findNiftyControl("qtmultiplier", Slider.class).enable();
        tempScreen.findNiftyControl("stmultiplier", Slider.class).enable();
        tempScreen.findNiftyControl("backButton", Button.class).enable();
    }
 
    /**
     * Stops the application.
     */
    public void exitExploration(){
        theApp.stop();
    }
    
    /**
     * Shows the options screen.
     */
    public void startOptionScreen(){
        nifty.gotoScreen("optionsScreen");
        nifty.update();
        nifty.getScreen("optionsScreen").findNiftyControl("ntspeed", Slider.class).setValue((float) DefaultTravelSpeed);
        nifty.getScreen("optionsScreen").findNiftyControl("qtmultiplier", Slider.class).setValue((float) QuickTravelMultiplier);
        nifty.getScreen("optionsScreen").findNiftyControl("stmultiplier", Slider.class).setValue((float) SlowTravelMultiplier);
        nifty.getScreen("optionsScreen").findNiftyControl("ftkButton", Button.class).setText(keyInputToString(KeyQuickTravel).toUpperCase());
        nifty.getScreen("optionsScreen").findNiftyControl("stkButton", Button.class).setText(keyInputToString(KeySlowTravel).toUpperCase());
        nifty.getScreen("optionsScreen").findNiftyControl("estkButton", Button.class).setText(keyInputToString(KeyExtraSlowTravel).toUpperCase());
        nifty.getScreen("optionsScreen").findNiftyControl("swkButton", Button.class).setText(keyInputToString(KeySwitchView).toUpperCase());
    }
    
    /**
     * Shows the main menu and saves properties.
     */
    public void showMainMenuScreen(){
        applySettings();
        nifty.gotoScreen("start");
    }
    
    /**
     * Updates properties in another state.
     */
    public void updatePropertiesInFullUniverseExplorer(){
        stateManager.getState(FullUniverseExplorer.class).setOptions(DefaultTravelSpeed, QuickTravelMultiplier, SlowTravelMultiplier, KeyQuickTravel, KeySlowTravel, KeyExtraSlowTravel, KeySwitchView);
    }
    
    /**
     * Saves settings/keys to property variable.
     */
    private void applySettings(){
        DefaultTravelSpeed = Math.round(nifty.getScreen("optionsScreen").findNiftyControl("ntspeed", Slider.class).getValue());
        QuickTravelMultiplier = nifty.getScreen("optionsScreen").findNiftyControl("qtmultiplier", Slider.class).getValue();
        SlowTravelMultiplier = nifty.getScreen("optionsScreen").findNiftyControl("stmultiplier", Slider.class).getValue();
        prop.setProperty("DefaultTravelSpeed", String.valueOf(DefaultTravelSpeed));
        prop.setProperty("QuickTravelMultiplier", String.valueOf(QuickTravelMultiplier));
        prop.setProperty("SlowTravelMultiplier", String.valueOf(SlowTravelMultiplier));
        prop.setProperty("KeyQuickTravel", String.valueOf(keyInputToString(KeyQuickTravel)).toLowerCase());
        prop.setProperty("KeySlowTravel", String.valueOf(keyInputToString(KeySlowTravel)).toLowerCase());
        prop.setProperty("KeyExtraSlowTravel", String.valueOf(keyInputToString(KeyExtraSlowTravel)).toLowerCase());
        prop.setProperty("KeySwitchView", String.valueOf(keyInputToString(KeySwitchView)).toLowerCase());
        updatePropertiesInFullUniverseExplorer();
        writePropertiesToFile();
    }
    
    /**
     * Writes set properties to file.
     */
    private void writePropertiesToFile(){
        try {
            FileOutputStream output = new FileOutputStream("config.properties");
            prop.store(output, null);
        } catch (FileNotFoundException ex) {
            generatePropertiesFile();
        } catch (IOException ex) {
            Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Loads properties set in a file.
     */
    private void loadPropertiesFromFile(){
        try {
            FileInputStream input = new FileInputStream("config.properties");
            prop.load(input);
            DefaultTravelSpeed = Math.round(Float.valueOf(prop.getProperty("DefaultTravelSpeed", "1000")));
            QuickTravelMultiplier = Float.valueOf(prop.getProperty("QuickTravelMultiplier", "10"));
            SlowTravelMultiplier = Float.valueOf(prop.getProperty("SlowTravelMultiplier", "0.5"));
            KeyQuickTravel = stringToKeyInput(prop.getProperty("KeyQuickTravel", "left shift"));
            KeySlowTravel = stringToKeyInput(prop.getProperty("KeySlowTravel", "left control"));
            KeyExtraSlowTravel = stringToKeyInput(prop.getProperty("KeyExtraSlowTravel", "left alt"));
            KeySwitchView = stringToKeyInput(prop.getProperty("KeySwitchView", "space"));
            stateManager.getState(FullUniverseExplorer.class).setOptions(DefaultTravelSpeed, QuickTravelMultiplier, SlowTravelMultiplier, KeyQuickTravel, KeySlowTravel, KeyExtraSlowTravel, KeySwitchView);
        } catch (FileNotFoundException ex) {
            generatePropertiesFile();
        } catch (IOException ex) {
            Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Converts a KeyInput to string used to properly showcase keys to a user.
     * @param key The KeyInput to be converted to KeyInput
     */
    private String keyInputToString(int key){
        String keyString = null;
        
        switch (key) {
            case KeyInput.KEY_UNKNOWN: keyString = "N/A"; break;
            case KeyInput.KEY_0: keyString = "0"; break;
            case KeyInput.KEY_1: keyString = "1"; break;
            case KeyInput.KEY_2: keyString = "2"; break;
            case KeyInput.KEY_3: keyString = "3"; break;
            case KeyInput.KEY_4: keyString = "4"; break;
            case KeyInput.KEY_5: keyString = "5"; break;
            case KeyInput.KEY_6: keyString = "6"; break;
            case KeyInput.KEY_7: keyString = "7"; break;
            case KeyInput.KEY_8: keyString = "8"; break;
            case KeyInput.KEY_9: keyString = "9"; break;
            //case KeyInput.KEY_A: keyString = "a"; break;
            case KeyInput.KEY_ADD: keyString = "+"; break;
            case KeyInput.KEY_APOSTROPHE: keyString = "'"; break;
            case KeyInput.KEY_B: keyString = "b"; break;
            case KeyInput.KEY_C: keyString = "c"; break;
            case KeyInput.KEY_COLON: keyString = ":"; break;
            case KeyInput.KEY_COMMA: keyString = ","; break;
            //case KeyInput.KEY_D: keyString = "d"; break;
            case KeyInput.KEY_DELETE: keyString = "delete"; break;
            case KeyInput.KEY_DIVIDE: keyString = "divide"; break;
//            case KeyInput.KEY_DOWN: keyString = "down arrow"; break;
            case KeyInput.KEY_E: keyString = "e"; break;
            case KeyInput.KEY_END: keyString = "end"; break;
            case KeyInput.KEY_EQUALS: keyString = "="; break;
            case KeyInput.KEY_ESCAPE: keyString = "escape"; break;
            case KeyInput.KEY_F: keyString = "f"; break;
            case KeyInput.KEY_G: keyString = "g"; break;
            case KeyInput.KEY_GRAVE: keyString = "`"; break;
            case KeyInput.KEY_H: keyString = "h"; break;
            case KeyInput.KEY_HOME: keyString = "home"; break;
            case KeyInput.KEY_I: keyString = "i"; break;
            case KeyInput.KEY_INSERT: keyString = "insert"; break;
            case KeyInput.KEY_J: keyString = "j"; break;
            case KeyInput.KEY_K: keyString = "k"; break;
            case KeyInput.KEY_L: keyString = "l"; break;
            case KeyInput.KEY_LBRACKET: keyString = "["; break;
            case KeyInput.KEY_LCONTROL: keyString = "left control"; break;
//            case KeyInput.KEY_LEFT: keyString = "left arrow"; break;
            case KeyInput.KEY_LMENU: keyString = "left alt"; break;
//            case KeyInput.KEY_LMETA: keyString = "left win"; break;
            case KeyInput.KEY_LSHIFT: keyString = "left shift"; break;
            case KeyInput.KEY_M: keyString = "m"; break;
            case KeyInput.KEY_MINUS: keyString = "-"; break;
            case KeyInput.KEY_MULTIPLY: keyString = "*"; break;
            case KeyInput.KEY_N: keyString = "n"; break;
            case KeyInput.KEY_NUMPAD0: keyString = "num0"; break;
            case KeyInput.KEY_NUMPAD1: keyString = "num1"; break;
            case KeyInput.KEY_NUMPAD2: keyString = "num2"; break;
            case KeyInput.KEY_NUMPAD3: keyString = "num3"; break;
            case KeyInput.KEY_NUMPAD4: keyString = "num4"; break;
            case KeyInput.KEY_NUMPAD5: keyString = "num5"; break;
            case KeyInput.KEY_NUMPAD6: keyString = "num6"; break;
            case KeyInput.KEY_NUMPAD7: keyString = "num7"; break;
            case KeyInput.KEY_NUMPAD8: keyString = "num8"; break;
            case KeyInput.KEY_NUMPAD9: keyString = "num9"; break;
            case KeyInput.KEY_NUMPADENTER: keyString = "numEnter"; break;
            case KeyInput.KEY_NUMPADEQUALS: keyString = "num="; break;
            case KeyInput.KEY_O: keyString = "o"; break;
            case KeyInput.KEY_P: keyString = "p"; break;
            case KeyInput.KEY_PAUSE: keyString = "pause"; break;
            case KeyInput.KEY_PERIOD: keyString = "."; break;
            case KeyInput.KEY_PGDN: keyString = "PgDn"; break;
            case KeyInput.KEY_PGUP: keyString = "PgUp"; break;
//            case KeyInput.KEY_POWER: keyString = "power"; break;
            //case KeyInput.KEY_Q: keyString = "q"; break;
            case KeyInput.KEY_R: keyString = "r"; break;
            case KeyInput.KEY_RBRACKET: keyString = "]"; break;
            case KeyInput.KEY_RCONTROL: keyString = "right control"; break;
            case KeyInput.KEY_RETURN: keyString = "Enter"; break;
//            case KeyInput.KEY_RIGHT: keyString = "right arrow"; break;
            case KeyInput.KEY_RMENU: keyString = "right alt"; break;
//            case KeyInput.KEY_RMETA: keyString = "right win"; break;
            case KeyInput.KEY_RSHIFT: keyString = "right shift"; break;
            //case KeyInput.KEY_S: keyString = "s"; break;
            case KeyInput.KEY_SCROLL: keyString = "Scroll"; break;
            case KeyInput.KEY_SEMICOLON: keyString = ";"; break;
            case KeyInput.KEY_SLASH: keyString = "/"; break;
//            case KeyInput.KEY_SLEEP: keyString = "sleep"; break;
            case KeyInput.KEY_SPACE: keyString = "space"; break;
            case KeyInput.KEY_STOP: keyString = "stop"; break;
            case KeyInput.KEY_SUBTRACT: keyString = "subtract"; break;
//            case KeyInput.KEY_SYSRQ: keyString = "SysRq"; break;
            case KeyInput.KEY_T: keyString = "t"; break;
            case KeyInput.KEY_TAB: keyString = "tab"; break;
            case KeyInput.KEY_U: keyString = "u"; break;
            case KeyInput.KEY_UNDERLINE: keyString = "_"; break;
//            case KeyInput.KEY_UP: keyString = "up arrow"; break;
            case KeyInput.KEY_V: keyString = "v"; break;
            //case KeyInput.KEY_W: keyString = "w"; break;
            case KeyInput.KEY_X: keyString = "x"; break;
            case KeyInput.KEY_Y: keyString = "y"; break;
            //case KeyInput.KEY_Z: keyString = "z"; break;
            default:
                keyString = "N/A";
                break;
        }
        return keyString;
    }
    
    /**
     * Converts a string to KeyInput used to bind keys.
     * @param setKey The string to be converted to KeyInput
     */
    private int stringToKeyInput(String setKey){
        int key = -1;
        
        switch (setKey) {
            case "N/A": key = KeyInput.KEY_UNKNOWN; break;
            case "0": key = KeyInput.KEY_0; break;
            case "1": key = KeyInput.KEY_1; break;
            case "2": key = KeyInput.KEY_2; break;
            case "3": key = KeyInput.KEY_3; break;
            case "4": key = KeyInput.KEY_4; break;
            case "5": key = KeyInput.KEY_5; break;
            case "6": key = KeyInput.KEY_6; break;
            case "7": key = KeyInput.KEY_7; break;
            case "8": key = KeyInput.KEY_8; break;
            case "9": key = KeyInput.KEY_9; break;
            //case "a": key = KeyInput.KEY_A; break;
            case "+": key = KeyInput.KEY_ADD; break;
            case "'": key = KeyInput.KEY_APOSTROPHE; break;
            case "b": key = KeyInput.KEY_B; break;
            case "c": key = KeyInput.KEY_C; break;
            case ":": key = KeyInput.KEY_COLON; break;
            case ",": key = KeyInput.KEY_COMMA; break;
            //case "d": key = KeyInput.KEY_D; break;
            case "delete": key = KeyInput.KEY_DELETE; break;
            case "divide": key = KeyInput.KEY_DIVIDE; break;
//            case "down arrow": key = KeyInput.KEY_DOWN; break;
            case "e": key = KeyInput.KEY_E; break;
            case "end": key = KeyInput.KEY_END; break;
            case "=": key = KeyInput.KEY_EQUALS; break;
            case "escape": key = KeyInput.KEY_ESCAPE; break;
            case "f": key = KeyInput.KEY_F; break;
            case "g": key = KeyInput.KEY_G; break;
            case "`": key = KeyInput.KEY_GRAVE; break;
            case "h": key = KeyInput.KEY_H; break;
            case "home": key = KeyInput.KEY_HOME; break;
            case "i": key = KeyInput.KEY_I; break;
            case "insert": key = KeyInput.KEY_INSERT; break;
            case "j": key = KeyInput.KEY_J; break;
            case "k": key = KeyInput.KEY_K; break;
            case "l": key = KeyInput.KEY_L; break;
            case "[": key = KeyInput.KEY_LBRACKET; break;
            case "left control": key = KeyInput.KEY_LCONTROL; break;
//            case "left arrow": key = KeyInput.KEY_LEFT; break;
            case "left alt": key = KeyInput.KEY_LMENU; break;
//            case "left win": key = KeyInput.KEY_LMETA; break;
            case "left shift": key = KeyInput.KEY_LSHIFT; break;
            case "m": key = KeyInput.KEY_M; break;
            case "-": key = KeyInput.KEY_MINUS; break;
            case "*": key = KeyInput.KEY_MULTIPLY; break;
            case "n": key = KeyInput.KEY_N; break;
            case "num0": key = KeyInput.KEY_NUMPAD0; break;
            case "num1": key = KeyInput.KEY_NUMPAD1; break;
            case "num2": key = KeyInput.KEY_NUMPAD2; break;
            case "num3": key = KeyInput.KEY_NUMPAD3; break;
            case "num4": key = KeyInput.KEY_NUMPAD4; break;
            case "num5": key = KeyInput.KEY_NUMPAD5; break;
            case "num6": key = KeyInput.KEY_NUMPAD6; break;
            case "num7": key = KeyInput.KEY_NUMPAD7; break;
            case "num8": key = KeyInput.KEY_NUMPAD8; break;
            case "num9": key = KeyInput.KEY_NUMPAD9; break;
            case "numEnter": key = KeyInput.KEY_NUMPADENTER; break;
            case "num=": key = KeyInput.KEY_NUMPADEQUALS; break;
            case "o": key = KeyInput.KEY_O; break;
            case "p": key = KeyInput.KEY_P; break;
            case "pause": key = KeyInput.KEY_PAUSE; break;
            case ".": key = KeyInput.KEY_PERIOD; break;
            case "PgDn": key = KeyInput.KEY_PGDN; break;
            case "PgUp": key = KeyInput.KEY_PGUP; break;
//            case "power": key = KeyInput.KEY_POWER; break;
            //case "q": key = KeyInput.KEY_Q; break;
            case "r": key = KeyInput.KEY_R; break;
            case "]": key = KeyInput.KEY_RBRACKET; break;
            case "right control": key = KeyInput.KEY_RCONTROL; break;
            case "Enter": key = KeyInput.KEY_RETURN; break;
//            case "right arrow": key = KeyInput.KEY_RIGHT; break;
            case "right alt": key = KeyInput.KEY_RMENU; break;
//            case "right win": key = KeyInput.KEY_RMETA; break;
            case "right shift": key = KeyInput.KEY_RSHIFT; break;
            //case "s": key = KeyInput.KEY_S; break;
            case "Scroll": key = KeyInput.KEY_SCROLL; break;
            case ";": key = KeyInput.KEY_SEMICOLON; break;
            case "/": key = KeyInput.KEY_SLASH; break;
//            case "sleep": key = KeyInput.KEY_SLEEP; break;
            case "space": key = KeyInput.KEY_SPACE; break;
            case "stop": key = KeyInput.KEY_STOP; break;
            case "subtract": key = KeyInput.KEY_SUBTRACT; break;
//            case "SysRq": key = KeyInput.KEY_SYSRQ; break;
            case "t": key = KeyInput.KEY_T; break;
            case "tab": key = KeyInput.KEY_TAB; break;
            case "u": key = KeyInput.KEY_U; break;
            case "_": key = KeyInput.KEY_UNDERLINE; break;
//            case "up arrow": key = KeyInput.KEY_UP; break;
            case "v": key = KeyInput.KEY_V; break;
            //case "w": key = KeyInput.KEY_W; break;
            case "x": key = KeyInput.KEY_X; break;
            case "y": key = KeyInput.KEY_Y; break;
            //case "z": key = KeyInput.KEY_Z; break;
            default:
                key = KeyInput.KEY_UNKNOWN;
                break;
        }
        return key;
    }
    
    /**
     * Sets the default properties.
     */
    private void setDefaultProperties(){
        defaultProps = new Properties();
        defaultProps.setProperty("KeyQuickTravel", "left shift");
        defaultProps.setProperty("KeySlowTravel", "left control");
        defaultProps.setProperty("KeyExtraSlowTravel", "left alt");
        defaultProps.setProperty("KeySwitchView", "space");
        defaultProps.setProperty("DefaultTravelSpeed", "1000");
        defaultProps.setProperty("QuickTravelMultiplier", "10");
        defaultProps.setProperty("SlowTravelMultiplier", "0.5");
    }
    
    /**
     * Generates the properties file and writes default config to it.
     */
    private void generatePropertiesFile(){
        FileOutputStream fileOut = null;
        try {
            File file = new File("config.properties");
            fileOut = new FileOutputStream(file);
            defaultProps.store(fileOut, "Configuration");
            fileOut.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileOut.close();
            } catch (IOException ex) {
                Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        inputManager.removeRawInputListener(this);
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

    @Override
    public void onTouchEvent(TouchEvent evt) {
    }
}
