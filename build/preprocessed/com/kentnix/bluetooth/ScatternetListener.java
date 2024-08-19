/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kentnix.bluetooth;
 
import java.util.*;
import java.io.*;
import javax.microedition.io.*;
import javax.bluetooth.*;
 
/**
 *
 * @author kentnix
 */
/**Handles Client s request
 */
public class ScatternetListener implements Runnable {
 
    static boolean isBusy;  
    private Vector queue = new Vector();
    private boolean isClosed = false;
    RemoteDevice currentrdev;
    Router routingtable = new Router();
 
    API dh;
    QualityOfService btstate = new QualityOfService();
/**
 * Starts new thread which handles all client requests
 */
    ScatternetListener(API dh) {
 
        this.dh = dh;
 
     
    }
 
    public void run() {
        while (!isClosed) {
 
            synchronized (this) {
                if (queue.size() == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.err.println("Exception inconnu: " + e);
 
                        return;
                    }
                }
            }
 
 
            StreamConnection conn;
 
            synchronized (this) {
 
                if (isClosed) {
                    return;
                }
 
                conn = (StreamConnection) queue.firstElement();
                queue.removeElementAt(0);
                serviceClient(conn);
            }
            System.out.println("master actif");
        }
 
        System.out.println("master inaticf");
    }
/**
 * stops current thread freeing its resources
 */
    void destroy() {
        synchronized (this) {
            isClosed = true;
            queue.removeAllElements();
            notify();
         
        }
    }
 
    /** Adds the connection to queue and notifies the thread. */
    void addConnection(StreamConnection conn) {
        synchronized (this) {
            queue.addElement(conn);
            notify();
        }
    }
/**
 * Service s client request
    */
    public void serviceClient(StreamConnection con) {
        try {
            currentrdev = RemoteDevice.getRemoteDevice(con);
            btstate.addDev(currentrdev, currentrdev.getBluetoothAddress());
 
            System.out.println("\n............\nmaster " + currentrdev.getBluetoothAddress());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            int bytes_read;
 
 
 
            System.out.println("Connection en cour.....");
             
           OutputStream os = con.openOutputStream();
            InputStream in = con.openInputStream();
           //os.write(greeting.getBytes());
            byte buffer[] = new byte[4000];
            System.out.println("step2");
            while ((bytes_read = in.read(buffer)) <= -1);
            String recieved = new String(buffer, 0, bytes_read);
            System.out.println("recus du  client : " + recieved);
            
            con.close();
 
            checkHeader(recieved);
 
        } catch (IOException e) {
            System.err.print(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
 
 
 
 
 
 
    }
/**Check s header of message and processes client request accordingly
 */
    public void checkHeader(String s) {
        int ptr = 4, count = 0;
 
        String DevName = null, DevAddr = null, RDevName = null, RDevAddr = null;
        int hcount = 7;
        String hct;
        String Head = s.substring(0, 4);
        System.out.println("Header " + Head);
        if (Head.equals("NWPT")) {
            String Dev = extractInfo(s);
            if (routingtable.isChanged()) {
                addNewDevice();
            } else {
 
                String FromDevAddr = null;
                for (int i = 5; i < s.length(); i++) {
                    if (ptr < s.length()) {
                        if (s.charAt(i) == '|') {
 
 
                            if (count == 0) {
                                DevName = s.substring(ptr, i);                              
                                ptr = i + 1;
 
                                count++;
                            } else if (count == 1) {
 
 
                                FromDevAddr = s.substring(ptr, i);
                                break;
                            }
                        }
                    }
                }
                System.out.println("fromdev " + FromDevAddr);
                slave bc = new slave("NWPT", 3, routingtable.getLocalAddr(), FromDevAddr, null);
 
            }
 
        } else if (Head.equals("MESG")) {
            String toDevAddr = checkRecieved(s);
        } else if (Head.equals("UPDT")) {
            String toDevAddr = removeDevice(s);
       } else if (Head.equals("NWPU")) {
            extractInfo(s);
        }
 
    }
/**Retrives conntents of message been recieved
 */
    public String checkRecieved(String s) {
 
        int ptr = 4, count = 0;
 
        String fromDevAddr = null, Mesg = null, toDevAddr = null, RDevName = null, RDevAddr = null;
        int hcount = 7;
        String hct;
 
        for (int i = 5; i < s.length(); i++) {
            if (ptr < s.length()) {
                if (s.charAt(i) == '|') {
 
 
                    if (count == 0) {
                        fromDevAddr = s.substring(ptr + 1, i);
 
                        ptr = i + 1;

                        count++;
                    } else if (count == 1) {
 
 
                        toDevAddr = s.substring(ptr, i);
 
                        ptr = i + 1;
 
 
                        count++;
                    } else if (count == 2) {
                        Mesg = s.substring(ptr, i);
                        ptr = i + 1;
                        count++;
 
 
                    }
                }
            }
        }
        if (toDevAddr.equals(routingtable.getLocalAddr())) {
            System.out.println("Message recus " + Mesg);
            String devname = routingtable.getName(fromDevAddr);
            dh.addMessage(devname, Mesg);
        } else {
 
            synchronized (this) {
                if (btstate.isClientRunning()) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                btstate.setClientRunning(true);

                slave bt = new slave("MESG", 2, fromDevAddr, toDevAddr, s);
 
            }
        }
       return toDevAddr;
    }
/**Removes un available remote device from list
. */
    public String removeDevice(String s) {
 
        int ptr = 4, count = 0;
        boolean isfirst = false;
        String fromDevAddr = null, Mesg = null, toDevAddr = null, RDevName = null, RDevAddr = null;
        int hcount = 7;
        String hct;

          for (int i = 5; i < s.length(); i++) {
            if (ptr < s.length()) {
                if (s.charAt(i) == '|') {
 
 
                    if (count == 0) {
                        fromDevAddr = s.substring(ptr + 1, i);
                        //  System.out.print("Devname "+DevName+"    ");
                        ptr = i + 1;
 
                        count++;
                    } else if (count == 1) {
 
 
                        toDevAddr = s.substring(ptr, i);
                        //  System.out.println("DevnAddr "+DevAddr);
                        ptr = i + 1;
 
                        //routingtable.addDevice(DevName, DevAddr);
                        System.out.println("to dev " + toDevAddr);
                        count++;
                        btstate.removeDev(toDevAddr);
                        if (routingtable.removeDevice(toDevAddr, fromDevAddr) == 0) {
                            System.out.println("updt client ");
                           slave btc = new slave("UPDT", 2, null, toDevAddr, null);
                        }
                        break;
                    }
                 
 
 
                }
            }
        }
        return toDevAddr;
    }
 
    /**Extract s routing information fromm message being recieved
     */
    public String extractInfo(String s) {
 
        int ptr = 4, count = 0;
        boolean isfirst = false;
        String DevName = null, DevAddr = null, RDevName = null, RDevAddr = null;
        int hcount = 7;
        String hct;
        routingtable.setChanged(false);
 
 
        for (int i = 5; i < s.length(); i++) {
            if (ptr < s.length()) {
                if (s.charAt(i) == '|') {
 
 
                    if (count == 0) {
                        DevName = s.substring(ptr, i);
                         ptr = i + 1;
 
                        count++;
                    } else if (count == 1) {
 
 
                        DevAddr = s.substring(ptr, i);
                         ptr = i + 1;
 
                        routingtable.addDevice(DevName, DevAddr);
                        routingtable.addNewDevice(DevAddr, DevAddr, "1");

                        
                        count++;
                    } else if (count == 2) {
                        s.substring(ptr, i);
                        ptr = i + 1;
                        count++;
 
 
                    } else if (count == 3) {
                        RDevName = s.substring(ptr, i);
                        ptr = i + 1;
                        count++;
                    } else if (count == 4) {
 
                        RDevAddr = s.substring(ptr, i);
                        ptr = i + 1;
                        count++;
                    } else if (count == 5) {
                      hct = (s.substring(ptr, i));
                        ptr = i + 1;
                        count = 3;
 
                        String ss = Integer.toString(Integer.parseInt(hct) + 1);
 
                        if (!routingtable.getLocalAddr().equals(RDevAddr)) {
                            routingtable.addDevice(RDevName, RDevAddr);
                            routingtable.addNewDevice(RDevAddr, DevAddr, ss);//hcount);
                        }
                        System.out.println("DevAddr:" + DevAddr + " RdevAddr: " + RDevAddr + " HCT: " + hct);
                        
                        routingtable.DisplayInfo();
                        routingtable.DisplayRoutingTable();
 
                    }
 
 
 
                }
            }
        }
        return DevAddr;
    }
/**Add s new device which requested at served.
 */
    public void addNewDevice() {
 
 
       slave bc = new slave("NWPT", 2, null, null, null);
 
 
    }
}