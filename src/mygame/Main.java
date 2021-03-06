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

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import mygame.state.IntroLoadingScreen;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    public static void main(String[] args) throws IOException {
        AppSettings settings = new AppSettings(true);
        settings.setTitle("SDSS Galaxy Explorer");
        settings.setFrameRate(-1);
        //settings.setIcons(new BufferedImage[]{ImageIO.read(new File("assets/Interface/sdsslogo.png"))});
        settings.setSettingsDialogImage("Interface/sdsslogo.png");
        Main app = new Main();
        
        app.setSettings(settings);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        stateManager.attach(new IntroLoadingScreen(this));
    } 
}
