/*
. * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kentnix.bluetooth;
 
/**
 *
 * @author kentnix
 *
. *
 */
import java.util.*;
import javax.bluetooth.*;

public class QualityOfService {
 
    static boolean isServerBusy;
    static boolean isClientBusy;
    static boolean isClientRunning = false;
    static boolean isServerRunning = false;
    static boolean isDeviceSearchCompleted = false;
    static boolean isSearchServiceCompleted = false;
    static boolean isDeviceReady = true;
   static Vector devices = new Vector();
    static Vector deviceaddr = new Vector();
    static boolean isS = false;
/**
 * returns current set of devices which have been discovered
 */
    public Vector getDevices() {
        return devices;
    }
/**
 * sauvegarde la liste des peripherique courant .
 * @param devices liste a enregistrer
 */
    public void putDevices(Vector devices) {
 
        this.devices = devices;
    }
 
    /**
     * Adds any new device discovery by server
     * @param dev noeud trouvé
     * @param devaddr qdresse du noeud en auestion
     */
    public void addDev(RemoteDevice dev, String devaddr) {
 
        if (devices.indexOf(dev) == -1) {
            devices.addElement(dev);
 
            // if(deviceaddr.indexOf(devaddr)==-1)
            deviceaddr.addElement(devaddr);
        }
    // deviceURLs.addElement(url);
    }
 
    /**renvoit les adresse de tout les noeuds trouvé
    */
    public Vector getDevaddr() {
        return deviceaddr;
    }
    /**
     * enregistre l' adress des peripheriques de la liste courante .

     */
    public void putDevAddr(Vector deviceaddr) {
 
        this.deviceaddr = deviceaddr;
 
    }
 /**
     * supprime l'adresse de la liste courante .
 * @param deviceaddr address list to be stored
     */
    public void removeDev(String dev) {
 
        int index = deviceaddr.indexOf(dev);
        if (index != -1) {
 
            deviceaddr.removeElementAt(index);
            devices.removeElementAt(index);
        }
 
    }
 
    QualityOfService() {
    }
 
    public synchronized void setDeviceReady(boolean state) {
 
        isDeviceReady = state;
    }
 
    public synchronized boolean isDeviceReady() {
 
        return isDeviceReady;
   }
 
    
 
 
 
    public synchronized void setServerState(boolean state) {
 
        isServerBusy = state;
    }
 
    public synchronized void setClientState(boolean state) {
 
        isClientBusy = state;
    }
 
   public synchronized boolean getServerState() {
 
        return isServerBusy;
    }
 
    public synchronized boolean getClientState() {
 
        return isClientBusy;
    }
 
    public synchronized void setClientRunning(boolean state) {
 
        isClientRunning = state;
    }
 
    public synchronized boolean isClientRunning() {
 
 
        return false;
    }
 
    public synchronized void setServiceSearchCompleted(boolean state) {
 
        isSearchServiceCompleted = state;
    }
 
    public synchronized boolean isServiceSearchCompleted() {
 
        return isSearchServiceCompleted;
    }
 
    public synchronized void setDeviceSearchCompleted(boolean state) {
 
        isDeviceSearchCompleted = state;
    }

    public synchronized boolean isDeviceSearchCompleted() {
 
        return isDeviceSearchCompleted;
    }
}