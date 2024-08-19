/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.kentnix.bluetooth;

/**
 *
 * @author kentnix
 */
import java.io.*;
import javax.microedition.io.*;

import javax.bluetooth.*;
import java.util.*;



/**implementation d'un peripherique bluetooth esclave
 */
public class slave implements DiscoveryListener, Runnable {

    private static Vector connectionURL = new Vector();
    private static Vector RemoteDevice = new Vector();
    private static Vector RemoteDevFriendlyName = new Vector();
    private static Vector RemoteDevAddress = new Vector();
    private static Vector RemoteDevList = new Vector();
    private static Vector serviceRecords = new Vector();
    private static Vector DevAddr = new Vector();
    private String currentdevice = null;
    //   private String msg = null;
    private String url = null;
    private OutputStream os;
    private InputStream in;
    private int messagetype;
    //private RemoteDevice selectedDevice;
    private DiscoveryAgent discoveryagent;
    private LocalDevice localdevice;
    private UUID uuidset[] = new UUID[2];
    private int attrset[] = new int[1];
    private StreamConnection conn;
    private Thread ClientThread;
    String LDName, LDAddr;
    Router rt = new Router();

    String head, toDev, toMsg;
    QualityOfService btstate = new QualityOfService();
    RemoteDevice currentr;
    String currentname, from;
    int devicecount = 0;

    /***
     *Creer un nouveau client/esclave bluetooth et transfert les donnees a un autre client,base
     * sur le header
     * @param from current local device address
     * @param head Header du paquet devant etre envoye
     * @param toDev adrress du noeud destinataire
     * @param tomsg message
     * @param type type du message message a envoyer 1 pour  1 saut,
     * 2 pour multi-saut
     *
.     */
    public slave(String head, int type, String from, String toDev, String toMsg) {
        this.head = head;
        this.toDev = toDev;
        this.toMsg = toMsg;
        this.from = from;
        this.messagetype = type;

        uuidset[0] = new UUID(0x1101);
        uuidset[1] = new UUID("0123456789AB", false);
        attrset[0] = 0x1234;
        ClientThread = new Thread(this);
        ClientThread.start();



    }

    public void run() {

        try {
            localdevice = LocalDevice.getLocalDevice();
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        }

        System.out.println(" Address du peripherique local: :" + localdevice.getBluetoothAddress());

        discoveryagent = localdevice.getDiscoveryAgent();



        try {

            btstate.setClientState(true);
            if ((head.equals("NWPT") && messagetype == 1)) {
                newSearch();
            } else if ((head.equals("NWPT") && messagetype == 2)) {

                //   connectionURL=btstate.getURLs();
                synchronized (this) {
                    btstate.setServiceSearchCompleted(false);
                }
                searchServices();
                sendPathInfo();

            } else if ((head.equals("NWPT") && messagetype == 3)) {

                replyPathInfo();

            } else if (head.equals("MESG") && messagetype == 1) {
                sendMessage();
            } else if (head.equals("MESG") && messagetype == 2) {
                forwardMessage();
            } else if (head.equals("UPDT") && messagetype == 2) {
                reportError();
            } else if (head.equals("SRCH") && messagetype == 1) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * envoit message typer a l'interface utilisateur a ToAddress
 *
 */
    public void sendMessage() {
        try {

            System.out.println("Message envoye");
            String Mesg = "Message vide";
            if (toMsg.length() != 0) {
               Mesg = toMsg;
            }

            String acto = null;
            if (toDev != null) {
                acto = toDev;
            }
            String toDev = rt.getRelativeTo(acto);
            String packet = "MESG|" + rt.getLocalAddr() + "|" + acto + "|" + Mesg + "|";

            System.out.println("mesg :" + packet + " toDev " + toDev);
            ServiceRecord sr = null;

            String relto = rt.getRelativeTo(toDev);
            System.out.println("acto " + toDev + " reto " + relto);


            for (int i = 0; i < RemoteDevice.size(); i++) {

                if (((RemoteDevice) RemoteDevice.elementAt(i)).getBluetoothAddress().equals(relto)) {
                    sr = (ServiceRecord) serviceRecords.elementAt(i);
                    url = (String) connectionURL.elementAt(i);
                    break;
                }
            }
            try {

               url = sr.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                conn = (StreamConnection) Connector.open(url);

                os = conn.openOutputStream();

                byte buffer[] = new byte[2000];

                os.write(packet.getBytes());
                os.close();
                conn.close();
                btstate.setClientState(false);
                System.out.println("Message sent from client : " + packet);


            } catch (IOException ioe) {
                try {
                    ioe.printStackTrace();
                    os.close();
                    conn.close();
                //  sendMessage();
                } catch (IOException ex) {
                    ex.printStackTrace();


                } catch (NullPointerException ne) {
                    try {
                        ne.printStackTrace();
                        ioe.printStackTrace();
                        os.close();
                        conn.close();
                    //  sendMessage();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
            btstate.setClientRunning(false);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
 * envoit le chemin au nouveau peripherique ayant la requet s ,pour enregistrer le chemin
 */
    public void replyPathInfo() {
        try {

            LDName = localdevice.getFriendlyName();
            LDAddr = localdevice.getBluetoothAddress();
            String Mesg = "NWPU" + LDName + "|" + LDAddr + "|0|";
            Mesg = Mesg + rt.getRoutin();

           String acto = null;
           if (toDev != null) {
                acto = toDev;
            }
            String toDev = rt.getRelativeTo(acto);
            String packet = Mesg;

            System.out.println("mesg :" + packet + " toDev " + toDev);
            ServiceRecord sr = null;

            String relto = rt.getRelativeTo(toDev);
            System.out.println("acto " + toDev + " reto " + relto);


            for (int i = 0; i < RemoteDevice.size(); i++) {

                if (((RemoteDevice) RemoteDevice.elementAt(i)).getBluetoothAddress().equals(relto)) {
                    sr = (ServiceRecord) serviceRecords.elementAt(i);
                    url = (String) connectionURL.elementAt(i);
                    break;
                }
            }
            try {

               url = sr.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                conn = (StreamConnection) Connector.open(url);

                os = conn.openOutputStream();

                byte buffer[] = new byte[2000];

                os.write(packet.getBytes());
                os.close();
                conn.close();
                btstate.setClientState(false);
                System.out.println("Message provenant d'un esclave : " + packet);


            } catch (IOException ioe) {
                try {
                    ioe.printStackTrace();
                    os.close();
                    conn.close();
                //  sendMessage();
                } catch (IOException ex) {
                    ex.printStackTrace();


                } catch (NullPointerException ne) {
                    try {
                        ne.printStackTrace();
                       ioe.printStackTrace();
                        os.close();
                        conn.close();
                    //  sendMessage();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
            btstate.setClientRunning(false);


        } catch (Exception e) {
            e.printStackTrace();
       }
    }

    /**
     * achemine le message vers une autre destination
     */
    public void forwardMessage() {
        try {
            System.out.println("........................................................\n MESSAGE 2 !!!!!!\n........................................ ");
            String Mesg = this.toMsg;
            String packet = Mesg;
            String toDev = rt.getRelativeTo(this.toDev);
            ServiceRecord sr = null;
            for (int i = 0; i < this.RemoteDevice.size(); i++) {
                if (((RemoteDevice) RemoteDevice.elementAt(i)).getBluetoothAddress().equals(toDev)) {


                    sr = (ServiceRecord) serviceRecords.elementAt(i);
                    url = (String) connectionURL.elementAt(i);


                }
            }
            try {
                url = sr.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                conn = (StreamConnection) Connector.open(url);
                // in=conn.openInputStream();
                os = conn.openOutputStream();

                byte buffer[] = new byte[2000];
               os.write(packet.getBytes());
                System.out.println("envoit " + packet.getBytes());

                os.close();
                conn.close();
                btstate.setClientState(false);



            } catch (IOException ioe) {
                btstate.removeDev(toDev);
                rt.removeDevice(toDev, toDev);
                reportError();
                if (conn != null) {
                    os.close();
                    conn.close();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        btstate.setClientRunning(false);
    }
/**
 * Cherche des noeud voisin pour joindre le reseau
 */
    public void newSearch() {

        System.out.println("Demarrage de l'esclave....");


        searchRemoteDevices();
        LDName = localdevice.getFriendlyName();
        LDAddr = localdevice.getBluetoothAddress();
        rt.setLocalDevice(LDAddr, LDName);
        synchronized (this) {
            if (!btstate.isDeviceSearchCompleted()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            btstate.setServiceSearchCompleted(false);
            searchServices();

            if (!btstate.isServiceSearchCompleted()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            sendPathInfo();

        }

    }
/**
 * Procedure de recherche
 */
    void searchRemoteDevices() {

        btstate.setDeviceSearchCompleted(false);
        try {
//            try {
//                Thread.sleep(10000);
            discoveryagent.startInquiry(DiscoveryAgent.GIAC, this);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }

            synchronized (this) {
            }

        } catch (BluetoothStateException be) {
            be.printStackTrace();
        }
    }

  /**ajout de nouveau peripherique
   */
    synchronized public void deviceDiscovered(RemoteDevice dev, DeviceClass cod) {

        if (RemoteDevList.indexOf(dev) == -1) {
            try {
                RemoteDevList.addElement(dev);
                RemoteDevAddress.addElement(dev.getBluetoothAddress());
                RemoteDevFriendlyName.addElement(dev.getFriendlyName(true));



            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**cette fonction est appelé une fois la recherche terminé
     */
    public void inquiryCompleted(int type) {

        synchronized (this) {
            btstate.setDeviceSearchCompleted(true);
            btstate.putDevices(RemoteDevList);
            btstate.putDevAddr(RemoteDevAddress);
            notify();
        }
        System.out.println("Recherche terminer");
    }

    /**
     * Ajoute le nouveau peripherique ayant un service,pour eviter les parket device
     */
    synchronized public void servicesDiscovered(int transactionid, ServiceRecord[] srv) {
        for (int i = 0; i < srv.length; i++) {
            serviceRecords.addElement(srv[i]);
            RemoteDevice.addElement(currentr);
           DevAddr.addElement(currentdevice);
            connectionURL.addElement(srv[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
            break;

        }
    }
/**envoit le chemin du peripherique courant a tout ses voisins
 */
    public void sendPathInfo() {
        synchronized (this) {
            if (!btstate.isServiceSearchCompleted()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
        LDName = localdevice.getFriendlyName();
        LDAddr = localdevice.getBluetoothAddress();



        for (int i = 0; i < RemoteDevice.size(); i++) {
            try {

                String RName, RAddr;


                RName = ((RemoteDevice) RemoteDevice.elementAt(i)).getFriendlyName(true);
                RAddr = ((RemoteDevice) RemoteDevice.elementAt(i)).getBluetoothAddress();

                rt.addDevice(RName, RAddr);
                rt.addNewDevice(RAddr, RAddr, "1");

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        System.out.println("taille" + serviceRecords.size());
        for (int i = 0; i < serviceRecords.size(); i++) {
            String mmsg = new String();

            mmsg = "NWPT" + LDName + "|" + LDAddr + "|0|";

            ServiceRecord sr = (ServiceRecord) serviceRecords.elementAt(i);
            try {
                url = (String) connectionURL.elementAt(i);// sr.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                conn = (StreamConnection) Connector.open(url);

                os = conn.openOutputStream();
                byte buffer[] = new byte[4000];


                mmsg = mmsg + rt.getRoutin();
                os.write(mmsg.getBytes());
                os.close();
                conn.close();
                System.out.println("imprimer");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
        rt.DisplayInfo();
        rt.DisplayRoutingTable();
        btstate.setClientRunning(false);
    }

  
    public void reportError() {
        synchronized (this) {
            if (!btstate.isServiceSearchCompleted()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
               }
           }

       }
        LDName = localdevice.getFriendlyName();
       LDAddr = localdevice.getBluetoothAddress();





        System.out.println("taille" + serviceRecords.size());

        for (int i = 0; i < serviceRecords.size(); i++) {
            String mmsg = new String();

            mmsg = "UPDT" + "|" + LDAddr + "|" + toDev + "|";


            ServiceRecord sr = (ServiceRecord) serviceRecords.elementAt(i);
            try {
                url = (String) connectionURL.elementAt(i);// sr.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                conn = (StreamConnection) Connector.open(url);

                os = conn.openOutputStream();
                byte buffer[] = new byte[4000];



                os.write(mmsg.getBytes());
                os.close();
                conn.close();
                System.out.println("This is printing at client side");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
        rt.DisplayInfo();
        rt.DisplayRoutingTable();
        btstate.setClientRunning(false);
    }

  /**appeler si service complet
    */
    public void serviceSearchCompleted(int transactionid, int respectivecode) {

        synchronized (this) {
            btstate.setDeviceReady(true);
            notify();

        }
        devicecount++;
        if (devicecount >= RemoteDevList.size()) {


            // btstate.putDevices(RemoteDevList);
            // btstate.putURLs(connectionURL);
            synchronized (this) {
                btstate.setServiceSearchCompleted(true);
                notify();
            }
            System.out.println("service search completed");


        }




    }
/**Recherche de service sur peripherique distant
 */
    public void searchServices() {

        System.out.println("searcing service");
        RemoteDevice.removeAllElements();
        serviceRecords.removeAllElements();
        connectionURL.removeAllElements();

        synchronized (this) {

            RemoteDevList = btstate.getDevices();
            RemoteDevAddress = btstate.getDevaddr();

        }

        for (int i = 0; i < RemoteDevList.size(); i++) {
            RemoteDevice rd = (RemoteDevice) RemoteDevList.elementAt(i);

            synchronized (this) {
                if (!btstate.isDeviceReady()) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }
                try {
                    currentdevice = (String) RemoteDevAddress.elementAt(i);
                    currentr = rd;
                    try {
                        currentname = (String) rd.getFriendlyName(true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    discoveryagent.searchServices(null, uuidset, rd, this);

//
                    synchronized (this) {
                        btstate.setDeviceReady(false);
                    }


                    System.out.println("Searching in remote device");
                } catch (BluetoothStateException e) {
                    RemoteDevList.removeElementAt(i);
                    e.printStackTrace();
                }
            }
        }

    }
}