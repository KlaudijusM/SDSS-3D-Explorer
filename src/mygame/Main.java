package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import mygame.state.FullUniverseExplorer;
import mygame.state.IntroLoadingScreen;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        stateManager.attach(new IntroLoadingScreen(this));
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
