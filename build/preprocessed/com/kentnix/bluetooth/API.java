package com.kentnix.bluetooth;
 
/**
 *
 * @author kentnix
 */
import java.io.IOException;
import javax.bluetooth.RemoteDevice;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.*;
import java.io.*;
import javax.microedition.lcdui.Image;
/**interface to display
 */
public class API implements CommandListener {
 
    application m;
    List list;
    String[] element = new String[0];
    //{"Send New Message","Recieved Messages","Virtual Scatternet","Re-Scan Devices","Exit"};
    List menu = new List("Scatternet", List.IMPLICIT, element, null);
    Command select = new Command("Choisir", Command.OK, 2);
    Command back = new Command("Retour", Command.BACK, 1);
    Command EXIT_CMD = new Command("Fermer", Command.EXIT, 2);
    Command CMD = new Command("Test", Command.HELP, 2);
    Command OK_CMD = new Command("Ok", Command.SCREEN, 1);
    Command send = new Command("Envois", Command.SCREEN, 1);
    Command remove = new Command("Supprimer", Command.SCREEN, 1);
    Command back2 = new Command("Retour", Command.BACK, 1);
    Displayable prev = null;
    List devices = null, Sdevice = null;
    Router rt = new Router();
    slave Btc = null;
    Alert alert = null;
    Form message;
    master Bts = null;
    TextField textfield2 = new TextField("Msg", "", 1000, TextField.ANY);
    TextBox textfield = new TextBox("msg", "", 1000, TextField.ANY);
    String msg;
    QualityOfService bs = new QualityOfService();
    ChoiceGroup hdevices;
    static Vector from = new Vector();
    static Vector mesg = new Vector();
    String s[] = new String[0];
    List recieved = new List("Message recus", List.IMPLICIT, s, null);
    String[] devices_select = null;
    String[] devices_selectname = null;
 
    /**Construct s new object which handles display an UI of this application
     */
    API(application m) {
 
        this.m = m;
        /*Image msg, sendimg, user, virtual, reload, exitico;
        try {
            msg = Image.createImage(Class.class.getResourceAsStream("/icons/msg.png"));
            sendimg = Image.createImage(Class.class.getResourceAsStream("/icons/send.png"));
 
             reload = Image.createImage(Class.class.getResourceAsStream("/icons/reload.png"));
            exitico = Image.createImage(Class.class.getResourceAsStream("/icons/exit.png"));
 */
            menu.append("Envois de Messages",null);
            menu.append("Reception de  Messages", null);
 
            menu.append("Re-Scan des equipement", null);
            menu.append("Fermer", null);
       /* } catch (IOException ex) {
           ex.printStackTrace();
        }*/
        
 
 
 
        menu.addCommand(EXIT_CMD);
 
        menu.setCommandListener(this);
        Display.getDisplay(m).setCurrent(menu);
        Bts = new master(m, this);
         bs.setClientRunning(true);
        Btc = new slave("NWPT", 1, null, null, null);
       
        message = new Form("Saisissez votre Message");
        message.addCommand(send);
        message.setCommandListener(this);
        textfield.addCommand(send);
        textfield.addCommand(back);
   
        recieved.addCommand(back);
 
 
    }
/**Stores new message recieved in inbox
 */
    public void addMessage(String from, String msg) {
        this.from.addElement(from);
        this.mesg.addElement(msg);
 
        Image im;
       /* try {
            im = Image.createImage(Class.class.getResourceAsStream("icons/msg.jpg"));*/
            recieved.append(from, null);
            displayAlert("Nouveau message " +
                    "recus", "Nouveau message");
       /* } catch (IOException ex) {
           ex.printStackTrace();
        }*/
 
 
    }
/**
 * display alert on user screen
 * @param title title of alert
 * @param text Text to be displayed
 */
    public void displayAlert(String title, String text) {
        synchronized (this) {
            alert = new Alert(title);
            alert.setString(text);
            alert.setTimeout(3000);
            Display.getDisplay(m).setCurrent(alert);
            prev = Display.getDisplay(m).getCurrent();
 
 
        }
    }
/**Starts new server
 */
    public void serverOn() {
        Bts = new master(m, this);
    }
/**
 * Stops server
 */
    public void serverOff() {
        System.out.println("Fermer");
        if (Bts != null) {
            Bts.stop();
        }
    }
 
    public void commandAction(Command c, Displayable d) {
 
        if (c == back) {
 
            System.out.println("Retour ");
            Display.getDisplay(m).setCurrent(menu);
            return;
        }
        if (c == back2) {
 
 
            Display.getDisplay(m).setCurrent(recieved);
            return;
       }
 
 
        if (d == recieved) {
            int index = recieved.getSelectedIndex();
            String from = (String) this.from.elementAt(index);
            String Mesg = (String) this.mesg.elementAt(index);
            StringItem sitem = new StringItem("De : " + from, "\n\n" + Mesg);
 
            Form f = new Form("Boite de reception");
            f.append(sitem);
            f.addCommand(back2);
            f.setCommandListener(this);
           Display.getDisplay(m).setCurrent(f);
            return;
        }
 
        if (c == remove) {
            boolean b[] = new boolean[hdevices.size()];
            hdevices.getSelectedFlags(b);
            Vector vv = bs.getDevaddr();
            Vector vvv = bs.getDevices();
            for (int i = 0; i < hdevices.size(); i++) {
 
                if (b[i]) {
                    int index = vv.indexOf(hdevices.getString(i));
                    vv.removeElementAt(index);
                    vvv.removeElementAt(index);
                }
 
            }
            synchronized (Btc) {
                bs.putDevAddr(vv);
                bs.putDevices(vvv);
                 
                Btc.notify();
            }
            System.out.println("choix");
 
//            Btc.setDeviceSelected(true);
            return;
        }
        if (c == send) {
 
            msg = textfield.getString();
            System.out.println("Envois " + msg);
            devices_select = new String[rt.Acto.size()];
            devices_selectname = new String[rt.Acto.size()];
            for (int i = 0; i < rt.Acto.size(); i++) {
                // try {
                devices_select[i] = ((String) rt.Acto.elementAt(i));
                devices_selectname[i] = rt.getName(devices_select[i]);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
            }
            Sdevice = new List("Scatternet formation", List.IMPLICIT, devices_selectname, null);
 
            Sdevice.addCommand(back);
 
            Sdevice.setCommandListener(this);
 
            Display.getDisplay(m).setCurrent(Sdevice);
 
            System.out.println("Envois");
            return;
 
 
 
 
 
        }
        if (c == EXIT_CMD) {
            // serverOff();
 
            m.destroyApp(false);
            m.notifyDestroyed();
 
            return;
        }
 
 
 
        if (d == Sdevice) {
            int index = Sdevice.getSelectedIndex();
            menu.setCommandListener(this);
            Display.getDisplay(m).setCurrent(menu);
            synchronized (this) {
                if (bs.isClientRunning()) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                bs.setClientRunning(true);
            }
 
            slave bt2 = new slave("MESG", 1, null, devices_select[index], msg);
            
            return;
 
 
        }
 
 
 
        switch (menu.getSelectedIndex()) {
            case 0:
                textfield.setCommandListener(this);
                Display.getDisplay(m).setCurrent(textfield);
 
 
                break;
 
 
 
            case 1:
                recieved.setCommandListener(this);
                Display.getDisplay(m).setCurrent(recieved);
                break;
 
            case 2:
                bs.setClientRunning(true);
                Btc = new slave("NWPT", 1, null, null, null);
 
                break;
 
 
            case 3:
                serverOff();
                m.destroyApp(true);
                m.notifyDestroyed();
                break;
 
 
 
            default:
                System.err.println("choix inconnu...");
 
                break;
        }
 
    }
}     