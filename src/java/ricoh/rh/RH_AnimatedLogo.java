/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 7.12.97 - revised 02-06-98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

class RH_AnimatedLogo extends Canvas implements Runnable, ImageObserver {
  private String gifsPath="", animationStr="/logo-n.gif", stillStr="/r-logo-n.gif";
  private Image mainLogo, animatedImage, stillImage;
  //private JCLabel mainLogo;
  private RH_MainToolbar parent;
  private Thread logoThread;
  private boolean running=false;
  private int width=23, height=23; //width=23, height=23;
  private int strValue=0;
  
  public RH_AnimatedLogo(RH_MainToolbar par, String path) {
    parent=par;
    gifsPath=path;
    animationStr=gifsPath+"/logo-n.gif"; 
    stillStr=gifsPath+"/r-logo-n.gif";
    animatedImage=Toolkit.getDefaultToolkit().getImage(animationStr);
    stillImage=Toolkit.getDefaultToolkit().getImage(stillStr);
    mainLogo=stillImage;
    System.out.println("Logo:" + mainLogo + " W=" + mainLogo.getWidth(this) + " H=" + mainLogo.getHeight(this));
    //setBackground(Color.red);
    
    setVisible(true);
    setSize(width,height);
    //repaint();
  }

  public void startLogo() {
    logoThread=new Thread(this);
    logoThread.setPriority(Thread.MIN_PRIORITY);
    
    strValue=0;
    running=true;
    mainLogo=animatedImage;
    Graphics gc=getGraphics();
    if (gc!=null) update(gc);
    logoThread.start();

    //?? Not sure why i'm doing this yet... done in ice browser 03-02-98
    synchronized(this) {notify();}
  }

  public void run() {
      /*
      StringTokenizer progress=null;
      String content=null;
      int charsRead=0, totalChars=0;
      float percent=0;
      */
      while (running) {
	  /*
	  content=parent.commBus.getParsingProgress();
	  progress=new StringTokenizer(content);
	  progress.nextToken(); // dummy: frame name
	  charsRead=Integer.parseInt((String)progress.nextToken());
	  totalChars=Integer.parseInt((String)progress.nextToken());
	  percent=(float)((float)charsRead/(float)totalChars)*100;
	  parent.commBus.updateURLLoadProgress((int)percent);
	  System.out.println(">> {"+content+": "+charsRead+" of "+totalChars);
	  */

	  //Graphics gc=getGraphics();
	  //if (gc!=null) update(gc);
	  //System.out.println(":*:");
      }
  }

  /**
   * Added this method because as of JDK 1.2beta2 the Thread.stop() method is being deprecated because
   * it causes problems.  They recommend setting a flag that is checked in the thread.
   */
  public void stopLogo() {
      //System.out.println("Logo is done running");
    running=false;
    Graphics gc=getGraphics();
    running=false;
    mainLogo.flush();
    mainLogo=stillImage;
    strValue=-1;
    logoThread = null;
    if (gc!=null) update(gc);
  }

  /*
  public void stop() {
    Graphics gc=getGraphics();
    running=false;
    mainLogo.flush();
    mainLogo=stillImage;
    strValue=-1;
    logoThread = null;
    if (gc!=null) update(gc);
    System.out.println("Logo is done running");
  }*/

  /*
    public void update (Graphics gc){ 
    //if (mainLogo.getGraphics()!=null) paint(mainLogo.getGraphics());
    paint(gc);
  }
  */
  public void paint(Graphics gc) {
    Dimension size = getSize();
    gc.setColor(Color.white);
    gc.drawLine(1,1,size.width,1); 
    gc.setColor(Color.black);
    gc.drawLine(1,2,size.width,2); 
    
    gc.drawImage(mainLogo,0,0,width,height,this);
  }

}
