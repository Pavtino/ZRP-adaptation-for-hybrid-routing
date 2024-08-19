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
 * Table de routage
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
 

    Router(){}
    public String getRelativeTo(String Acto) {
        for (int i = 0; i < reto.size(); i++) {
            if (this.Acto.elementAt(i).equals(Acto)) {
                return (String) reto.elementAt(i);
            }
        }
        return null;
    }
 
/**Retourne vrai si les infos du routage ont changer
 */
    public boolean isChanged() {
 
        return isRTChanged;
    }
/**
 * Mise a jour de la table de routage
 */
    public void setChanged(boolean condition) {
 
        isRTChanged = condition;
   }
 
     
    int cct = 0;
/**suppression de peripherique de la table de routage
 * @param devaddr adress du noeud a retirer
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
/**Sauvegarde des infos local
 * @param locaddr adress bluetooth local
 * @param locname nom du peripherique local
 */
    public void setLocalDevice(String locaddr, String locname) {
        this.locaddr = locaddr;
        this.locname = locname;
    }
 
 
    public String getLocalAddr() {
        return this.locaddr;
    }
 
    /**Ajout de s nouveau noeud dans la table
     * @param Devname nom du peripherique distant
     * @param DevAddr Bluetooth 
     */
    public void addDevice(String DevName, String DevAddr) {

        if (DeviceAddr.indexOf(DevAddr) == -1) {
 
            DeviceName.addElement(DevName);
            DeviceAddr.addElement(DevAddr);
            isRTChanged = true;
        } else {
        }
 
    }
/**Suppression du noeud de la  table de routage
 * @param acto address du peripherique
 * @param reto address  du peripherique servant de fourniseur de route
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
    /**renvoit s nom du peripherique
     * @param Addr adresse du peripherique distant
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
/**Ajout du peripherique dans la  table de routage
 * @param actualto adress du peripherique a ajouter
 * @param relativeto address du peripherique servant de fournisseur de route
 * @param Hopcount nombre de saut pour le noeud
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
 
    /**construit les donnees du chemin dans le paquet a transmettre
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