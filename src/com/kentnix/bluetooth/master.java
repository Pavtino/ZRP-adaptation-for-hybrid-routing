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

import javax.microedition.midlet.*;


public class master implements Runnable {

    UUID uuid = new UUID("0123456789AB", false);
    ServiceRecord btServiceRecord;
    LocalDevice myLocalDevice;
    int scatternetAttributeID = 0x1234;
    StreamConnectionNotifier streamconnNotifier;
    StreamConnection con;
    OutputStream os;
    InputStream in;
    Thread st;
    MIDlet m;
    String recieved;
    Router rt;
    API dh;
    boolean isStopped = false;
    QualityOfService bs = new QualityOfService();
    ScatternetListener clientprocessor;
    Thread clienth;
/**Initializes du maitre 
 */
    public void initializeMaster() {
        try {
            //creation de l'uuid..

            String url = "btspp://localhost:" + uuid.toString() + ";name=ScatternetServer";
            streamconnNotifier = (StreamConnectionNotifier) Connector.open(url);

            System.out.println("Connexion ouverte");
            //set service 
            myLocalDevice = LocalDevice.getLocalDevice();
            btServiceRecord = myLocalDevice.getRecord(streamconnNotifier);
            DataElement base = new DataElement(DataElement.DATSEQ);
            btServiceRecord.setAttributeValue(scatternetAttributeID, base);
       } catch (IOException ioe) {
        }

    }
/**
 * Demarrage du maitre
. */
    public void startServer() {

        isStopped = false;
        clientprocessor = new ScatternetListener(dh);
        clienth = new Thread(clientprocessor);
        clienth.start();
        {
            while (!isStopped) {



                try {
                    con = (StreamConnection) streamconnNotifier.acceptAndOpen();
                    clientprocessor.addConnection(con);

                } catch (IOException e) {
                    System.err.print(e.toString());
                }

            }
        }


    }
/**
 * Arret du  maitre
 */
    master(MIDlet m, API dh) {

        this.m = m;
        this.dh = dh;
        st = new Thread(this);
        st.start();

    }

    public void stop() {


        isStopped = true;
        clientprocessor.destroy();
        try {
            clienth.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        try {
            if (con != null) {
                con.close();
                streamconnNotifier.close();
            }

            bs.setServerState(false);
        } catch (IOException ex) {
            ex.printStackTrace();

        }


    }

    public void run() {
        bs.setServerState(true);
        initializeMaster();
        startServer();

   }
}
