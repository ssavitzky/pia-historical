/**
 *
 * Copyright (C) 1997, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * ReadersHelper Class: The main frame container
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.24.97 - revised 02-06-98
 *
 */
package ricoh.rh;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;

import javax.swing.*;
import ice.htmlbrowser.Document;

public class ReadersHelper extends Frame
implements WindowListener, ComponentListener {

  public final static String RH_Version="v.0.2pia";

  //***SELECT THE APPROPRIATE OS BEFORE COMPILING
  //public final static String RH_OS_Version="Win95";
  public final static String RH_OS_Version="WinNT";
  //public final static String RH_OS_Version="Linux";
  //public final static String RH_OS_Version="Solaris";

  //***UNCOMMENT WHEN COMPILING UNDER JDK 1.1.5
    public String RH_JDK_Version="JDK1.2";
    //public final static String RH_JDK_Version="JDK1.1.5";
    //public final static String RH_SWING_Version="Swing-1.0.2";
    public final static String RH_SWING_Version="Swing-1.1";
  public final static String RH_JCLASS_Version="BWT209";

  //***UNCOMMENT WHEN COMPILING UNDER JDK 1.2beta2
  //public final static String RH_JDK_Version="JDK1.2beta2";
  //public final static String RH_SWING_Version="Swing-0.7";
  //public final static String RH_JCLASS_Version="BWT208";
  
  //***UNCOMMENT WHEN COMPILING UNDER JBUILDER!!!
    //public final static String RH_JDK_Version="JB_JDK1.1.x"; // JBuilder is currently 1.1.3
  //public final static String RH_SWING_Version="Swing-0.7";
  //public final static String RH_JCLASS_Version="JB_BWT209"; // JBUilder uses BWT204

  //** Create the System Version String (will be available in the about dialog)
  public StringBuffer rhSystemVersion=new StringBuffer();

  private String signatureName="Main";
  public RH_CommBus commBus;
  private RH_MenuBar menuBar;
    private final static String hostArg="-host";
    private final static String rmiDefaultHostname="ohio.crc.ricoh.com";
    private final static String rmiDefaultServerName="HelloServer";
    private final static int rmiDefaultPort=1099;
    private String laf=null, rmiHostname=rmiDefaultHostname, rmiServerName=rmiDefaultServerName;
    private int rmiPort=rmiDefaultPort;

  public RH_MainFrame mainFrame;
  private int width=800,height=800;
  
  public static void main (String args[]) {
    ReadersHelper RH_Main = new ReadersHelper(args);
  }

  public ReadersHelper(String args[]) {
    super ("Reader's Helper Application");

    if (args.length==2 && args[0].equalsIgnoreCase(hostArg)) {
	rmiHostname=args[1];
	System.out.println("***HostName: "+ rmiHostname+" port: "+rmiPort);
    }
    else rmiHostname=rmiDefaultHostname;

    Properties props = new Properties(System.getProperties());

    RH_JDK_Version=new String("Java "+props.getProperty("java.version")+" Comp:"+props.getProperty("java.compiler")+
			      "; "+props.getProperty("os.name")+" "
			      +props.getProperty("os.version")+" "+props.getProperty("os.arch")+")");
    rhSystemVersion.append(RH_SWING_Version).append("|").append(RH_JCLASS_Version);
    //props.list(System.out);

    //* Swing 1.0
    System.out.println("L&F:"+UIManager.getSystemLookAndFeelClassName());
    laf=UIManager.getSystemLookAndFeelClassName();
    if (laf!=RH_GlobalVars.windowsLAF) laf=RH_GlobalVars.motifLAF;//laf=RH_GlobalVars.metalLAF;  
    try {
      UIManager.setLookAndFeel(laf);
    } catch (Exception exc) {
      System.out.println("Error loading L&F: " + exc);
    }

    RH_SplashScreen splash=new RH_SplashScreen(this,getVersion(),getSystemVersion());

    commBus= new RH_CommBus();
    commBus.parent=this;
    menuBar=new RH_MenuBar(this);
    mainFrame = new RH_MainFrame (commBus, this, splash, true);
    
    // -DproxySet=true -DproxyHost=SOMEHOST -DproxyPort=SOMENUM
    System.out.println("<<..setting up proxy information: "+ commBus.profile.getProxyServerName()+":"+ commBus.profile.getProxyServerPort()+"...>>");
    System.getProperties().put( "proxySet", "true" );
    System.getProperties().put( "proxyHost", commBus.profile.getProxyServerName());
    System.getProperties().put( "proxyPort", new String(commBus.profile.getProxyServerPort()+"") );
    
    commBus.setupLocationList(mainFrame.locationList);  // sets up bookmark list
    menuBar.mainFrame=mainFrame;
    menuBar.updateIt();

    // show the frame
    setSize(width,height);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(screenSize.width/2 - width/2, screenSize.height/2 - height/2);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    removeAll();
    setLayout(new BorderLayout());

    addWindowListener(this);
    addComponentListener(this);
    setBackground(mainFrame.mainBackColor);
    setForeground(mainFrame.mainTextColor);
    setMenuBar(menuBar);

    add("Center",mainFrame);

    setBounds(commBus.getPreferredX(),commBus.getPreferredY(),width=commBus.getPreferredWidth(),height=commBus.getPreferredHeight());
    setSize(width=commBus.getPreferredWidth(),height=commBus.getPreferredHeight());
    setLocation(commBus.getPreferredX(),commBus.getPreferredY());
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    splash.newMsg("Done");
    setVisible(true);	
    //pack();
    invalidate();
    width-=1;
    height-=1;
    commBus.frameResized();
    validate();
    show();
    repaint();
    System.out.println("System started...");
    //mainFrame.requestDefaultFocus();
    splash.dispose();
    splash=null;
  }

  public void componentResized(ComponentEvent ev) {
    Dimension size = getSize();
    // this prevents resizing when the user selects the resize area but does not actualy change the size
    if (width!=size.width || height!=size.height) {
      invalidate();
      System.out.println(">>>... Resized: w=" + size.width + " h=" + size.height);
      width=size.width;
      height=size.height;
      commBus.frameResized();
      validate();
    }
    else {
      System.out.println("READERSHELPER RESIZED BUT SIZE STAYED TH SAME");
    }
  }
  
  public String getVersion() {
    return RH_Version;
  }
  public String getSystemVersion() {
    return rhSystemVersion.toString();
  }

  public void componentMoved(ComponentEvent ev) {
  }
  public void componentHidden(ComponentEvent ev) {
  }
  public void componentShown(ComponentEvent ev) {
  }

  public void windowClosing(WindowEvent event) {
    System.runFinalization();
    System.exit(0);
  }
  public void windowClosed(WindowEvent event) {
  }
  public void windowDeiconified(WindowEvent event) {
  }
  public void windowIconified(WindowEvent event) {
  }
  public void windowActivated(WindowEvent event) {
  }
  public void windowDeactivated(WindowEvent event) {
  }
  public void windowOpened(WindowEvent event) {
  }

  public void addLocationsMenuItem(RH_LocationItem item) {
    menuBar.addLocationsMenuItem(item);
  }
  public void addCurrentLocation(String url, String title) {
    menuBar.addCurrentLocation(url,title);
  }
  public void setupLocationList(RH_LocationItem[] items) {
    menuBar.setupLocationList(items);
  }
  public void updateHistory(String url, String title) {
      //System.out.println("****RH: UPDATE HISTORY");
      menuBar.updateHistory(url,title);
  }
    public void updateHistoryMenu(int pastIdx, int currentIdx) {
	menuBar.updateHistoryMenu(pastIdx, currentIdx);
    }
    /*
      public void addHistoryMenuItem(RH_LocationItem item) {
      menuBar.addHistoryMenuItem(item);
      }
    */
    public boolean getShowPortal() {
	return menuBar.getShowPortal();
    }

    public String getRMIHostname() {
	return rmiHostname;
    }
    public String getRMIServerName() {
	return rmiServerName;
    }
    public int getRMIPortName() {
	return rmiPort;
    }

    public boolean okToProcess() {
	return (menuBar.mainFrame!=null ? true : false);
    }

    public void setLAF(String newlaf) {
	String oldlaf=laf;
	laf=newlaf;
	try {
	    invalidate();
	    UIManager.setLookAndFeel(laf);
	    SwingUtilities.updateComponentTreeUI(this);
	    validate();
	    repaint();
	} catch (Exception exc) {
	    System.out.println("Error loading L&F: " + exc);
	    laf=oldlaf;
	}
    }
    /**
     * return the pluggable look and feel currently being used by the system
     */
    public String getLAF() {
	return laf;
    }
}

