/** 
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 6.20.97 - revised 02-06-98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.BorderFactory;

import jclass.bwt.BWTEnum;

class RH_StatusControl extends JPanel {
  public RH_CommBus commBus;

  private int width, height;
  private Image logo;

  private JLabel status1, status2, durationLabel;
  private RH_ProgressMeter meter;
   private JProgressBar metalmeter;
  private String yaaImageFile = "anoh.gif"; //"../gifs/yaa1.gif";

  //private Color statusBackColor=Color.gray, statusForeColor=Color.green;
  private Color statusBackColor=Color.gray, statusForeColor=Color.white, labelColor=Color.black, meterBackColor=Color.lightGray;
  //private Color statusBackColor=Color.lightGray, statusForeColor=Color.blue,
  private Color defaultBackColor=Color.lightGray, defaultLoadingColor=Color.blue, loadingColor=defaultLoadingColor, idleColor=Color.gray;

  private Color backColor=Color.gray, textColor=Color.white, highlightColor=Color.white, shadowColor=Color.black,
      shadowColor2=Color.gray;
  //private Color statusBackColor=Color.black, statusForeColor=Color.lightGray;
  //loadingColor=Color.yellow, idleColor=Color.gray;
  //private Color statusBackColor=Color.gray, statusForeColor=Color.black;
  private int top=3,bottom=2,left=1,right=1;
    private String laf=null;
    private boolean windowsPMeter=false;
  
  public RH_StatusControl (RH_CommBus bus,int h) {
    super();
    setDoubleBuffered(true);

    commBus=bus;
    backColor=commBus.getMainBackColor();
    textColor=commBus.getMainTextColor();
    highlightColor=commBus.getMainHighlightColor();
    shadowColor=commBus.getMainShadowColor();
    shadowColor2=commBus.getMainShadowColor2();
    int motif=commBus.getMotifNumber();
    yaaImageFile=commBus.getGifsPath()+"/"+yaaImageFile;
    laf=commBus.getLAF();
    //** 5.18.98 i don't link the swing progressbar because it works worst than the jclass component
    //if (laf==RH_GlobalVars.windowsLAF) windowsPMeter=true;
    //    else windowsPMeter=false;
    windowsPMeter=true;

    if (motif==1) {
      labelColor=Color.lightGray; //commBus.getModeTextColor();
      defaultLoadingColor=new Color(255,255,64); //Color.yellow;
      statusBackColor=Color.gray; 
      statusForeColor=Color.white;
      loadingColor=Color.white;
      idleColor=Color.darkGray;
      meterBackColor=Color.gray;
    }
    else {
      labelColor=Color.blue;
      //defaultLoadingColor=Color.blue;
      defaultLoadingColor=new Color(255,255,64); //Color.yellow;
      statusBackColor=Color.lightGray;
      statusForeColor=Color.black;
      loadingColor=Color.blue;
      idleColor=Color.darkGray;
      meterBackColor=Color.gray; //statusBackColor;
    }
    int xpad=15;

    //Font font=new Font("Arial", Font.PLAIN, 10);
    //Font font=new Font("MS Sans Serif",Font.PLAIN,11);
    Font font=new Font("MS Sans Serif",Font.PLAIN,11);
    setFont(font);
    height=h;

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gbl);
    //setLayout(new GridLayout());
    setSize(width,height);
    Color shadowBorderColor=backColor.darker(), hliteBorderColor=statusBackColor;
    
    status1 = new JLabel("",JLabel.LEFT);
    status1.setText(commBus.mainFrame.getHomeURL());
    status1.setBackground(statusBackColor);
    status1.setForeground(commBus.getLocationTextColor()); //statusForeColor);
    status1.setFont(font);
    status1.setBorder(new BevelBorder(BevelBorder.LOWERED,backColor,Color.lightGray,Color.black,shadowBorderColor)); //hliteBorderColor,shadowBorderColor));
    //status1.setHighlightColors(highlightColor,shadowColor);
    buildConstraints(gbc,0,0,1,1,85,100);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.ipadx = xpad; gbc.ipady = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbl.setConstraints(status1,gbc);
    add(status1);

    String sstr="Status: ";
    JLabel label2 = new JLabel(sstr,JLabel.RIGHT);
    label2.setFont(font);
    label2.setMinimumSize(new Dimension(40,12));
    buildConstraints(gbc,2,0,1,1,1,100);
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.ipadx = 0; gbc.ipady = 0;
    gbl.setConstraints(label2,gbc);
    label2.setForeground(labelColor);
    add(label2);

    status2 = new JLabel("",JLabel.LEFT);
    status2.setBackground(statusBackColor);
    status2.setForeground(commBus.getLocationTextColor()); //statusForeColor);
    //status2.setHighlightColors(highlightColor,shadowColor);
    status2.setBorder(new BevelBorder(BevelBorder.LOWERED,backColor,Color.lightGray,Color.black,shadowBorderColor));
    status2.setFont(font);
    buildConstraints(gbc,3,0,1,1,11,100);
    gbc.ipadx = xpad; gbc.ipady = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    //gbc.insets=new Insets(0,0,0,1);
    gbl.setConstraints(status2,gbc);
    add(status2);
    durationLabel= new JLabel(RH_GlobalVars.defaultDurationStr,JLabel.CENTER);
    durationLabel.setIcon(new ImageIcon(commBus.getGifsPath()+"/clock.gif"));
    durationLabel.setBackground(backColor);
    durationLabel.setForeground(commBus.getLocationTextColor());
    durationLabel.setFont(font);
    durationLabel.setBorder(new BevelBorder(BevelBorder.LOWERED,backColor,Color.lightGray,Color.black,shadowBorderColor));
    //durationLabel.setHighlightColors(highlightColor,shadowColor);
    //durationLabel.setSize(30,10);
    gbc.insets=new Insets(0,2,0,0);
    gbc.anchor = GridBagConstraints.CENTER;
    buildConstraints(gbc,4,0,1,1,4,100);
    gbl.setConstraints(durationLabel,gbc);
    add(durationLabel);
    
    /*
      StringBuffer buf=new StringBuffer().append(commBus.getHistorySize());
      anohLabel= new JLabel(buf.toString(),JLabel.CENTER);
      anohLabel.setIcon(new ImageIcon(commBus.getGifsPath()+"/anohflag-d.gif"));
      anohLabel.setFont(font);
      anohLabel.setIconTextGap(5);
      anohLabel.setBackground(backColor);
      anohLabel.setForeground(commBus.getLocationTextColor());
      anohLabel.setBorder(new BevelBorder(BevelBorder.LOWERED,backColor,Color.lightGray,Color.black,shadowBorderColor));
      anohLabel.setOpaque(true);
      //anohLabel.setHighlightColors(highlightColor,shadowColor);
      //anohLabel.setInsets(0,0,0,0);
      //anohLabel.setSize(8,10);
      //anohLabel.setIconXY(6,2);
      gbc.insets=new Insets(0,2,0,0);
      gbc.anchor = GridBagConstraints.CENTER;
      buildConstraints(gbc,5,0,1,1,2,100);
      gbl.setConstraints(anohLabel,gbc);
      add(anohLabel);
    */

    buildConstraints(gbc,6,0,1,1,7,100);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.ipadx = 0; gbc.ipady = 0;
    gbc.insets=new Insets(0,2,0,0);
    if (windowsPMeter) {
	meter = new RH_ProgressMeter(0, 0, 100);
	meter.setBarCount(0);
	meter.setAutoLabel(false);
	meter.setBarColor(loadingColor);
	meter.setLabelPosition(BWTEnum.STRING_CENTER);
	meter.setValue(60);
	meter.setPreferredSize(70,25);
	meter.setFont(font);
	meter.setBackground(meterBackColor);
	meter.setForeground(statusForeColor);
	meter.setHighlightColor(highlightColor);
	meter.setShadowThickness(1);
	meter.setInsets(new Insets(0,0,0,0));
	gbl.setConstraints(meter,gbc);
	add(meter);
    }
    else {
	metalmeter = new JProgressBar();
	metalmeter.setValue(0);
	metalmeter.setBackground(meterBackColor);
	metalmeter.setForeground(statusForeColor);
	metalmeter.setOrientation(JProgressBar.HORIZONTAL);
	metalmeter.setBorder(new BevelBorder(BevelBorder.LOWERED,backColor,Color.lightGray,Color.black,shadowBorderColor));
	gbl.setConstraints(metalmeter,gbc);
	add(metalmeter);
    }

    setInsets(top,left,bottom,right);    
    setBackground(backColor);
    setForeground(textColor);
  }

  public Insets getInsets() {
    return new Insets(top,left,bottom,right);
  }
  public void setInsets (int newtop, int newleft, int newbottom, int newright) {
    top=newtop; bottom=newbottom;
    left=newleft; right=newright;
    getInsets();
  }

 private void buildConstraints (GridBagConstraints constraints, int gx, int gy, int gw, int gh, int wx, int wy) {
    constraints.gridx = gx;
    constraints.gridy = gy;
    constraints.gridwidth = gw;
    constraints.gridheight = gh;
    constraints.weightx = wx;
    constraints.weighty = wy;
  }
  public void message1(String msg) {
    status1.setText("  " + msg);
  }
  public void message2(String msg) {
    status2.setText("  " + msg);  // the spacing is a hack because i can't get the insets to work properly
  }
  public void updateMemLabel(long mem) {
    //memLabel.setText(" : "+mem);
  }
  public void updateMemLabel(long mem, String str) {
    //memLabel.setText(" : "+mem+" "+str);
  }
  public Dimension getPreferredSize() {
    return new Dimension(width,height);
  }
  public void updateProgress (int val) {
    //if (val<2) System.out.println("-->ProgressColor="+meter.getBarColor());
      if (windowsPMeter) meter.setValue(val);
      else {
	  metalmeter.setValue(val);
	  metalmeter.repaint();
      }
  }
  public void resetProgress () {
      if (windowsPMeter) {
	  meter.setValue(0);
	  meter.setBarColor(loadingColor=defaultLoadingColor);
	  meter.setBackground(meterBackColor); //statusBackColor=backColor);
      }
      else {
	  metalmeter.setValue(0);
	  metalmeter.setForeground(loadingColor=defaultLoadingColor);
	  metalmeter.setBackground(meterBackColor); //statusBackColor=backColor);
      }
      repaint();
  }
  public void setBarColor (Color cl) {
      if (windowsPMeter) meter.setBarColor(loadingColor=cl);
      else metalmeter.setForeground(loadingColor=cl);
  }
  public void setBarBackColor(Color cl) {
      if (windowsPMeter) meter.setBackground(statusBackColor=cl);
      else metalmeter.setBackground(statusBackColor=cl);
  }
  public void setToDone () {
      if (windowsPMeter) {
	  meter.setBarColor(idleColor);
	  meter.setValue(100);
      }
      else {
	  metalmeter.setForeground(idleColor);
	  metalmeter.setValue(100);
	  //metalmeter.repaint();
      }
      message1("");
      message2("Done");
      repaint();
    //System.out.println("STATUS: SET TO DONE: bar color="+meter.getBarColor());
  }

    /*
  public void update (Graphics gc) {
    paint(gc);
  }
  public void paint (Graphics gc) {
    Dimension size = getSize();
    gc.setColor(highlightColor);
    gc.drawLine(0,0,size.width,0);  // top
    gc.drawLine(0,0,0,size.height); // left
    gc.setColor(shadowColor);
    gc.drawLine(0,size.height-1,size.width-1,size.height-1);  // bottom
    gc.drawLine(size.width-1,0,size.width-1,size.height-1);  //right
    
  }
    */

  public void setDurationLabel(String str) {
    durationLabel.setText(str);
  }

    public void updateHistorySizeLabel() {
	//StringBuffer buf=new StringBuffer().append(commBus.getHistorySize());
	//anohLabel.setText(buf.toString());
    }
	

  /**
   * turns an icon on when the file the user just pointed the browser to is a file that was once previously annotated and
   * is in the user's hsitory database
   */
  public void setPreviouslyAnnotatedIcon(boolean set) {
      /*
	if (set) {
	//anohLabel.setImage(commBus.getGifsPath()+"/anohflag.gif");
	StringBuffer buf=new StringBuffer().append(commBus.getHistorySize());
	anohLabel.setText(buf.toString());
	anohLabel.setIcon(new ImageIcon(commBus.getGifsPath()+"/anohflag.gif"));
	//anohLabel.setIcon(new ImageIcon(commBus.getGifsPath()+"/anohflag.gif"));
	anohLabel.setBackground(Color.red);
	}
	else {
	//StringBuffer buf=new StringBuffer().append(commBus.getHistorySize());
	//anohLabel.setText(buf.toString());
	//anohLabel.setImage(commBus.getGifsPath()+"/anohflag-d.gif");
	anohLabel.setIcon(new ImageIcon(commBus.getGifsPath()+"/anohflag-d.gif"));
	//anohLabel.setIcon(new ImageIcon(commBus.getGifsPath()+"/anohflag-d.gif"));
	anohLabel.setBackground(backColor);
	}
	repaint();
      */
  }

  public void setWaitCursor() {
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
  }
  public void setDefaultCursor() {
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

}
