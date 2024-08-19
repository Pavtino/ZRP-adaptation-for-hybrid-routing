/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.kentnix.bluetooth;

import javax.microedition.midlet.*;

/**
 * @author kentnix
 */
public class application extends MIDlet {
    public void startApp() {
        API d = new API(this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
