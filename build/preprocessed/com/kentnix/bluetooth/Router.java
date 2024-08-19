/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
. */
package com.kentnix.bluetooth;
 
/**
. *
 * @author kentnix
. */
import java.util.*;
 

/**
 * Handles routing table of the device
 */
class Router {
 
    Hashtable RouteTable = new Hashtable();
    static Vector DeviceName = new Vector();
    static Vector DeviceAddr = new Vector();
    static Vector Acto = new Vector();
    static Vector reto = new Vector(),  hct = new Vector();
    private static boolean isRTChanged = false;
    static String locaddr = null;
    static String locname = null;
    static String selDev = null;
 
/**Constructs new object which is used to store and retrive routing data
 */
    Router(){}
    public String getRelativeTo(String Acto) {
        for (int i = 0; i < reto.size(); i++) {
            if (this.Acto.elementAt(i).equals(Acto)) {
                return (String) reto.elementAt(i);
            }
        }
        return null;
    }
 
/**returns true if routing information has been changed
 */
    public boolean isChanged() {
 
        return isRTChanged;
    }
/**
 * updates state of routing table
 */
    public void setChanged(boolean condition) {
 
        isRTChanged = condition;
   }
 
     
    int cct = 0;
/**Removes device from routing table
 * @param devaddr Device address of device to be removed
 */
    public void removedevice(String devaddr) {
 
        int index = Acto.indexOf(devaddr);
        System.out.println("index " + index);
 
        String s = (String) reto.elementAt(index);
 
        int size = reto.size();
        if (cct == 0) {
            reto.removeAllElements();
            for (int i = 0; i < size; i++) {
 
                reto.addElement(s);
                cct = 1;
            }
        } else {
            String ss = (String) Acto.elementAt(index);
            reto.removeElementAt(index);
            reto.insertElementAt(ss, index);

        }
 
        DisplayRoutingTable();
 
 
 
 
    }
/**Saves local device information
 * @param locaddr local device bluetooth address
 * @param locname Local device bluetooth Name
 */
    public void setLocalDevice(String locaddr, String locname) {
        this.locaddr = locaddr;
        this.locname = locname;
    }
 
   /**returns current device local address
     */
    public String getLocalAddr() {
        return this.locaddr;
    }
 
    /**add s new device to routing table
     * @param Devname friendly name of remote device
     * @param DevAddr Bluetooth address of remote device
     */
    public void addDevice(String DevName, String DevAddr) {

        if (DeviceAddr.indexOf(DevAddr) == -1) {
 
            DeviceName.addElement(DevName);
            DeviceAddr.addElement(DevAddr);
            isRTChanged = true;
        } else {
        }
 
    }
/**Removes device from routing table
 * @param acto address of device to be removed
 * @param reto address of device which acted as route provider
 */
    public int removeDevice(String acto, String relto) {
       for (int i = 0; i < Acto.size(); i++) {

           if (((String) Acto.elementAt(i)).equals(acto)) {
               if (((String) reto.elementAt(i)).equals(relto)) {
                    Acto.removeElementAt(i);
                    reto.removeElementAt(i);
                    return 0;
                }
            }
 
        }
        return 1;
 
    }
    /**return s friendly name of device
     * @param Addr device address of remote device
     */
   public String getName(String Addr) {
       return (String) DeviceName.elementAt(DeviceAddr.indexOf(Addr));
 
    }
 
    public void DisplayInfo() {
 
        System.out.println("noeud dans la liste \nDeviceName  DeviceAddr");
 
        for (int i = 0; i < DeviceAddr.size(); i++) {
 
            System.out.println(DeviceName.elementAt(i) + "    " + DeviceAddr.elementAt(i));
        }

 
    }
/**Adds device to routing table
 * @param actualto address of device to be added
 * @param relativeto address of device which acted as route provider
 * @param Hopcount hop count of device
 */
    public synchronized void addNewDevice(String actualto, String relativeto, String Hopcount) {
 
       if (!(Acto.contains(actualto))) {
 
            System.out.println("..................\nActo addr" + actualto);
 
            Acto.addElement(actualto);
            reto.addElement(relativeto);
            hct.addElement(Hopcount);
            isRTChanged = true;

        } else {
           int index = Acto.indexOf(actualto);
 
            if (Integer.parseInt(Hopcount) < Integer.parseInt((String) hct.elementAt(index))) {
                Acto.removeElementAt(index);
                reto.removeElementAt(index);
                hct.removeElementAt(index);
 
                Acto.addElement(actualto);
                reto.addElement(relativeto);
                hct.addElement(Hopcount);
                isRTChanged = true;
 
 
 
           }
 
        }
 
    }
 
 
    public void DisplayRoutingTable() {
 
        System.out.println("Info Routage" + "\nActualto\t Relativeto\t  Hcount" + "\n==================================");
        for (int i = 0; i < reto.size(); i++) {
            System.out.println((String) Acto.elementAt(i) + "\t" + reto.elementAt(i) + "\t" + hct.elementAt(i));
 
        }
 
    }
 
    /**Build s path information into packet to be transmitted
     */
    public String getRoutin() {
 
        String s = "";
 
        for (int i = 0; i < reto.size(); i++) {
            String dev = (String) Acto.elementAt(i);
 
            s = s + getName(dev) + "|" + dev + "|" + (String) hct.elementAt(i) + "|";
 
        }
        return s;
 
    }
}