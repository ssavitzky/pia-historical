/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_MenuBar
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 3.28.98
 *
 */
package ricoh.rh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.util.*;

class RH_MenuBar extends MenuBar implements ActionListener, ItemListener {
  public RH_CommBus commBus;
  public RH_MainFrame mainFrame;
  public ReadersHelper parent;
    public RH_LocationItem[] userHistory;
  public Menu fileMenu, navigateMenu, anohMenu, locationsMenu, whateverMenu, optionsMenu, helpMenu;
    private MenuItem quitItem, addLocation, goFwd, goBack, reload, stop, anohGoBack, anohGoFwd, anohReload, about, anohBucket, addRawho, addIM3, showPortal,
      useLexicon, viewSource;
    private CheckboxMenuItem windowslaf, metallaf, motiflaf;
    private boolean portalFlag=false, okToProcess=false;;
    private final static String locationsLabel="loc", historyLabel="his";
    StringTokenizer menuTokens;

  RH_MenuBar(ReadersHelper newparent) {
    parent=newparent;
    commBus=parent.commBus;
    //menuBar = new MenuBar();
    fileMenu =(Menu) add(new Menu("File"));
    //fileMenu.setKeyAccelerator('F');
    navigateMenu=(Menu) add(new Menu("Navigate"));
    // navigateMenu.setKeyAccelerator('N');
    anohMenu= new Menu("Annotate",true);
    locationsMenu= new Menu("Locations",true);
    whateverMenu= new Menu("History",true);
    optionsMenu= new Menu("Options",true);
    helpMenu = new Menu("Help",true);    
    
    fileMenu.add("New ...");
    fileMenu.add("Open ...");
    fileMenu.add(addIM3= new MenuItem("Print to IM3"));
    addIM3.addActionListener(this);
    fileMenu.addSeparator();
    fileMenu.add(quitItem = new MenuItem("Exit"));
    quitItem.addActionListener(this);

    optionsMenu.add(showPortal= new MenuItem("Portal->   "));
    optionsMenu.add(useLexicon= new MenuItem("UseLex->   "));
    optionsMenu.add(viewSource= new MenuItem("ViewSource"));
    optionsMenu.add(windowslaf= new CheckboxMenuItem("Windows GUI"));
    optionsMenu.add(metallaf= new CheckboxMenuItem("Metal GUI"));
    optionsMenu.add(motiflaf= new CheckboxMenuItem("Motif GUI"));
    String laf=parent.getLAF();
    if (laf.equalsIgnoreCase(RH_GlobalVars.windowsLAF)) {
	windowslaf.setState(true);
	metallaf.setState(false);
	motiflaf.setState(false);
    }
    else if (laf.equalsIgnoreCase(RH_GlobalVars.metalLAF)) {
	windowslaf.setState(false);
	metallaf.setState(true);
	motiflaf.setState(false);
    }
    else if (laf.equalsIgnoreCase(RH_GlobalVars.motifLAF)) {
	windowslaf.setState(false);
	metallaf.setState(false);
	motiflaf.setState(true);
    }
    

    windowslaf.addItemListener(this);
    metallaf.addItemListener(this);
    motiflaf.addItemListener(this);
    showPortal.addActionListener(this);
    useLexicon.addActionListener(this);
    viewSource.addActionListener(this);
    locationsMenu.add(addLocation= new MenuItem("Add"));
    addLocation.addActionListener(this);
    //locationMenu.addActionListener(this);
    locationsMenu.addSeparator();
    
    navigateMenu.add(goBack= new MenuItem("Back"));
    goBack.addActionListener(this);
    navigateMenu.add(goFwd= new MenuItem("Forward"));
    goFwd.addActionListener(this);
    navigateMenu.add(reload= new MenuItem("Reload"));
    reload.addActionListener(this);
    navigateMenu.add(stop= new MenuItem("Stop"));
    stop.addActionListener(this);
    
    anohMenu.add(anohGoBack= new MenuItem("Back"));
    anohGoBack.addActionListener(this);
    anohMenu.add(anohGoFwd= new MenuItem("Forward"));
    anohGoFwd.addActionListener(this);
    anohMenu.add(anohReload= new MenuItem("Reload"));
    anohReload.addActionListener(this);
    anohMenu.add(anohBucket= new MenuItem("Add_to_Bucket"));
    anohBucket.addActionListener(this);
    anohMenu.add(addRawho= new MenuItem("Add_to_RAWHO"));
    addRawho.addActionListener(this);
    
    helpMenu.add(about=new MenuItem("About"));
    about.addActionListener(this);
    //menuBar.add(fileMenu);
    //menuBar.add(navigateMenu);
    add(anohMenu);
    add(locationsMenu);
    
    Menu separator=new Menu("",false);
    separator.addSeparator();
    add(separator);
    add(whateverMenu);
    add(optionsMenu);
    add(helpMenu);

    okToProcess=true;
    portalFlag=false;
    userHistory=new RH_LocationItem[0];
  }

  public void actionPerformed (ActionEvent event) {
    Object source = event.getSource();
    menuTokens=new StringTokenizer((String)event.getActionCommand());
    if (source==quitItem)  commBus.exitSystem();
    else if (menuTokens!=null && menuTokens.countTokens()>1) {
	// Check to see if a location bookmark or history has been selected
	//System.out.println("****About to call CHECK MENU LISTS");
	checkMenuLists(); //(String)event.getActionCommand());
	//System.out.println("****DONE calling CHECK MENU LISTS");
	//System.out.println("--------------------------------------------");
    }
    else if (source == addLocation) {
      commBus.addCurrentLocation();
    }
    else if (source == showPortal) {
	System.out.print("**Setting portal view:");
	if (portalFlag) {
	    portalFlag=false;
	    showPortal.setLabel("Portal->Off");
	}
	else {
	    portalFlag=true;
	    showPortal.setLabel("Portal->ON");
	}
	System.out.println("=="+portalFlag);
	commBus.setShowPortal(portalFlag);
    }
    else if (source == viewSource) {
	RH_ViewSource view=new RH_ViewSource(parent,commBus.getAnnotationBuffer());
    }
    else if (source == useLexicon) {
	if (commBus.getUseLexicon()) {
	    commBus.setUseLexicon(false);
	    useLexicon.setLabel("UseLex->Off");
	}
	else {
	    commBus.setUseLexicon(true);
	    useLexicon.setLabel("UseLex->ON");
	}
    }
    else if (source == goBack) {
      commBus.browserGoBack();
    }
    else if (source == goFwd) {
      commBus.browserGoForward();
    }
    else if (source == reload) {
      commBus.reloadURL();
    }
    else if (source == stop) {
      commBus.browserURL_Stop();
    }
    else if (source == anohGoBack) {
    }
    else if (source == anohGoFwd) {
    }
    else if (source == anohReload) {
    }
    else if (source == anohBucket) {
      commBus.addBucketLink();
    }
    else if (source == addRawho) {
      commBus.addRAWHOLink();
    }
    else if (source == addIM3) {
      commBus.addIM3Link();
    }
    else if (source == about) {
      System.out.println("About");
      RH_AboutRH about= new RH_AboutRH(parent,parent.getVersion(),mainFrame.getOtherInfoString());
    }
  }

    private void checkMenuLists() {
	//System.out.println("=====CALLING CHECK MENU LISTS:");
	String label=menuTokens.nextToken();
	int num=Integer.parseInt(menuTokens.nextToken());
	if (label.equalsIgnoreCase(historyLabel)) {
	    if (mainFrame.historyList!=null) {
		commBus.gotoHistoryURL(mainFrame.historyList[num].getURL(),num);
	    }
	}
	else if (label.equalsIgnoreCase(locationsLabel)) {
	    String newurl="";
	    if (mainFrame.locationList!=null) {
		commBus.URL_Process(mainFrame.locationList[num].getURL());
	    }
	}
    }


  public void addCurrentLocation(String url,String title) {
    int i=0;
    RH_LocationItem[] holder=new RH_LocationItem[mainFrame.locationList.length];
    System.arraycopy(mainFrame.locationList,0,holder,0,mainFrame.locationList.length);

    //for (i=0;i<mainFrame.locationList.length;i++) holder[i]=mainFrame.locationList[i];
    mainFrame.locationList=new RH_LocationItem[mainFrame.locationList.length+1];
    System.arraycopy(holder,0,mainFrame.locationList,0,holder.length);

    //for (i=0;i<holder.length;i++) mainFrame.locationList[i]=holder[i];
    i=mainFrame.locationList.length-1;
    mainFrame.locationList[i]=new RH_LocationItem(url,title);
    addLocationsMenuItem(mainFrame.locationList[i]);
    mainFrame.locationList[i].addActionListener(this);
    mainFrame.locationListChanged=true;
  }


  /**
   * Given a RH_LocationItem list, search it for the title string and return the url of found
   */
  public String locationItemSelected(String label, RH_LocationItem[] list) {
    int i=0;
    for (i=0;i<list.length && !list[i].getTitle().equals(label);i++);
    if (i<list.length) return list[i].getURL();
    else return "";
  }

  public void setupLocationList(RH_LocationItem[] items) {
    int i=0;
    for (i=0; i<items.length; i++) {
      locationsMenu.add(items[i]);
      items[i].setActionCommand(locationsLabel+" "+i);
      items[i].addActionListener(this);
    }
  }

  public void addLocationsMenuItem(RH_LocationItem item) {
    locationsMenu.add(item);
    item.addActionListener(this);
  }
    /*
      public void addHistoryMenuItem(RH_LocationItem item) {
      historyMenu.add(item);
      item.addActionListener(this);
      }
    */
    public void updateHistoryMenu(int pastIdx, int currentIdx) {
	if (pastIdx>=0) mainFrame.historyList[pastIdx].setBold(false);
	if (currentIdx>=0) mainFrame.historyList[currentIdx].setBold(true);
    }

 /**
   * Updates the history stack after each new document is loaded
   */
  public void updateHistory(String url, String title) {
      //System.out.println("****MENUBAR: UPDATE HISTORY:"+mainFrame);
    int i=0, j=0, newLength=0;
    RH_LocationItem[] holder=null;
    // if this document is the same as the last, do not record it
    if (mainFrame!=null && mainFrame.historyList!=null && mainFrame.historyList.length>0 && mainFrame.historyPointer>=0 && 
	!mainFrame.historyList[mainFrame.historyPointer].getURL().equals(url)) {
	//** Follow netscape method: when looking at url from history, and then select new url, current history url
	//** becomes the new position 1 and the new url becomes position 0.  this means shifting the list up
	if (mainFrame.historyPointer>0) {
	    whateverMenu.removeAll();
	    holder=new RH_LocationItem[mainFrame.historyList.length-mainFrame.historyPointer+1];
	    //** Add the new entry
	    holder[0]=new RH_LocationItem(url,title);
	    holder[0].setNumber(0);
	    holder[0].setBold(true);
	    whateverMenu.insert(holder[0],0);
	    holder[0].setActionCommand(historyLabel+" "+0);
	    holder[0].addActionListener(this);
	    //** Add allother entries starting at the point in the list where we just were (i.e. historyPointer)
	    for (i=1, j=mainFrame.historyPointer;i<holder.length;i++,j++) {
		holder[i]=mainFrame.historyList[j];
		holder[i].setNumber(i);
		whateverMenu.insert(holder[i],i);
		holder[i].setActionCommand(historyLabel+" "+i);
		//holder[i].addActionListener(this);
		holder[i].setBold(false);
	    }
	    mainFrame.historyList=new RH_LocationItem[holder.length];
	    System.arraycopy(holder,0,mainFrame.historyList,0,holder.length);
	    mainFrame.historyPointer=0;
	}
	else {
	    if (mainFrame.historyList.length+1<RH_MainFrame.MaxHistorySize) {
		//System.out.println("-----Adding history url:"+url+" len="+mainFrame.historyList.length);
		whateverMenu.removeAll();
		// make a tmp holder and allocate a new position in the history stack
		holder=new RH_LocationItem[mainFrame.historyList.length+1];
		//System.out.println("Holder len="+holder.length);
		System.arraycopy(mainFrame.historyList,0,holder,1,mainFrame.historyList.length);
		mainFrame.historyList=new RH_LocationItem[holder.length];
		//** Leave idx 0 open for new entry
		for (i=1;i<holder.length;i++) {
		    holder[i].setNumber(i);
		    holder[i].setBold(false);
		    whateverMenu.insert(holder[i],i);
		    holder[i].setActionCommand(historyLabel+" "+i);
		    //holder[i].addActionListener(this);
		}
		holder[0]=new RH_LocationItem(url,title);
		holder[0].setNumber(0);
		holder[0].setBold(true);
		whateverMenu.insert(holder[0],0);
		holder[0].setActionCommand(historyLabel+" "+0);
		holder[0].addActionListener(this);
		System.arraycopy(holder,0,mainFrame.historyList,0,holder.length);
		holder=null;
	    }
	    // push new url on the stack and dump the last item
	    else {  // this is untested as of 8-26-97
		System.out.println("MaxHistory:"+RH_MainFrame.MaxHistorySize+" historyLength="+mainFrame.historyList.length);
		holder=new RH_LocationItem[mainFrame.historyList.length];
		System.arraycopy(mainFrame.historyList,0,holder,0,mainFrame.historyList.length-1);
		whateverMenu.removeAll();
		for (i=0, j=1;i<mainFrame.historyList.length-1;i++,j++) {
		    //System.out.println("--Moving Holder["+i+"] to history["+j+"] Count:"+ whateverMenu.getItemCount());
		    mainFrame.historyList[j]=holder[i];
		    mainFrame.historyList[j].setNumber(j);
		    whateverMenu.insert(mainFrame.historyList[j],j);
		}
		mainFrame.historyList[0]=new RH_LocationItem(url,title);
		mainFrame.historyList[0].setNumber(0);
		whateverMenu.insert(mainFrame.historyList[0],0);
		mainFrame.historyList[0].setActionCommand(historyLabel+" "+0);
		mainFrame.historyList[0].addActionListener(this);
	    }
	    mainFrame.historyPointer=0;  // reset to top since we have a new item on the list
	    
	}
    }
    //** First entry at startup
    else if (mainFrame.historyList==null || mainFrame.historyList.length==0) {
	System.out.println("*-*-*-*Adding first history item:"+url+" title:"+title);
	mainFrame.historyList=new RH_LocationItem[1];
	mainFrame.historyList[0]=new RH_LocationItem(url,title);
	mainFrame.historyList[0].setNumber(0);
	mainFrame.historyList[0].setBold(true);
	mainFrame.historyList[0].setActionCommand(historyLabel+" "+0);
	mainFrame.historyList[0].addActionListener(this);
	whateverMenu.insert(mainFrame.historyList[0],0);
	mainFrame.historyPointer=0;
    }
    okToProcess=true;
  }

  /**
   * I use this to enable some menubar options when i'm using the system
   */
  public void updateIt() {
    boolean enabled=false;
    if (mainFrame.profile.getUserAccountName().equals("jamey")) enabled=true;
    else enabled=false;
    addIM3.setEnabled(enabled);
    anohBucket.setEnabled(enabled);
    addRawho.setEnabled(enabled);
    showPortal.setEnabled(enabled);
  }

    public boolean getShowPortal() {
	return portalFlag;
    }


    public void itemStateChanged(ItemEvent ev) {
	Object source = ev.getSource();
	if (source == windowslaf) {
	    parent.setLAF(RH_GlobalVars.windowsLAF);
	    metallaf.setState(false);
	    motiflaf.setState(false);
	}
	else if (source == metallaf) {
	    parent.setLAF(RH_GlobalVars.metalLAF);
	    windowslaf.setState(false);
	    motiflaf.setState(false);
	}
	else if (source == motiflaf) {
	    parent.setLAF(RH_GlobalVars.motifLAF);
	    windowslaf.setState(false);
	    metallaf.setState(false);
	}
    }

}
