/** 
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 9.3.97
 *
 */
package ricoh.rh;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.*;
import java.lang.*;
import java.util.StringTokenizer;
import java.util.*;

import java.text.NumberFormat;
import java.awt.*;
import java.awt.event.*;

public class RH_Profile extends Component {
  public RH_CommBus commBus;
  public RH_MainFrame mainFrame;

  public final static String profileHeader="RH_Profile", conceptsHeader="RH_Concepts",
      locationsHeader="RH_Locations", groupsHeader="RH_Groups", 
    personalHeader=":Personal", thumbarHeader=":Thumbar", bakExtension=".bak",
      highlightHeader=":Highlight", meterHeader=":Meters", colorsHeader=":Colors", systemHeader=":System", piaHeader=":Pia";
  private final static int maxHighlightStyles=3, maxMeterStyles=2, defMeterID=0, compMeterID=1;
  public RH_HighlightStyle[] highlightStyles=new RH_HighlightStyle[maxHighlightStyles];
  public RH_MeterStyle[] meterStyles=new RH_MeterStyle[maxMeterStyles];

  private String parentPath="", gifsPath="", privateDir="", cacheDir="",userFName="", userLName="", lastModified="", 
    homeURL="", locationFontName="Arial", documentFontName="", defaultHomeURL="", userAccountName="", piaHost="", piaAgentName="",
      piaPrinterName="", proxyServerName="", proxyAgentName="", proxyUserName="";
  private int sensitivitySetting=0, numMeterStyles=0, numAllConcepts=0, locationFontSize=10, modeFontSize=10, motifNumber=0,
    useCacheDocuments=0, useCacheImages=0, useLoadImages=0, preferredWidth=0, preferredHeight=0, preferredX=0, preferredY=0, 
      lensViewFraction=6, animateLogo=0, autoLoadHomeURL=0, documentCacheSize=0, similarityThreshold=0, largeMeters=0,
      defaultHliteStyle=0, numGroups=0, defaultGroup=0, piaPort=0, populateConcepts=0, betterImageScaling=0, useLensLogo=0,
      useAnohDoubleLine, useLinkDoubleLine, proxyServerPort=0;
  private Color locationTextColor=Color.black, locationBackColor=Color.lightGray, modeTextColor=Color.black, modeBackColor=Color.lightGray,
      overviewWindowColor=Color.white, overviewLensColor=Color.white, overviewWindowLineColor=Color.gray,
      overviewANOHColor=Color.red, overviewLinkColor=Color.blue,  overviewLensLineColor=Color.black;
  private boolean locationUseBold=false, modeUseBold=false, anohWindowMoreInfo=false, makeBackupFiles=true;
  public int numHighlights=0, documentFontSize=12;
  private RH_ConceptGroup[] conceptGroups=null;
  private boolean success;

    public final static String profileFilename="profile.rh", conceptsFilename="concepts.rh",
      locationsFilename="locations.rh", groupsFilename="groups.rh", proxyProfileFilename="proxy.rh";

    public RH_Profile (RH_MainFrame parent) {
	mainFrame=parent;
	commBus=mainFrame.commBus;
	commBus.profile=this;
	
	success=readProxyProfile();
    }
    
    public void loadProfile(String profileString, String conceptsString, String groupsString, String locationsString) {
	if (success) {
	    //System.out.println("ProfileFile:"+profileFilename);
	    success=readProfileContents(profileString);
	    if (success) {
		boolean tmpSuccess=false;
		tmpSuccess=readLocationsContents(locationsString);
		if (!tmpSuccess) {
		    System.out.println("***ERROR: locations file can not be properly read");
		    success=tmpSuccess;
		}
		tmpSuccess=readConceptsContents(conceptsString);
		if (!tmpSuccess) {
		    System.out.println("***ERROR: concepts file can not be properly read");
		    success=tmpSuccess;
		}
		else {
		    mainFrame.numAllConcepts=numAllConcepts;
		    tmpSuccess=readGroupsContents(groupsString);
		    if (!tmpSuccess) {
			System.out.println("***ERROR: groups file can not be properly read");
			success=tmpSuccess;
		    }
		}
	    }
	}
	else System.out.println("***ERROR: proxy file can not be properly read");
    }
    

  public boolean successfulLoad() {
    return success;
  }

  private boolean readProfile() {
    File file=new File("./",profileFilename);
    BufferedReader dataInput=null;
    byte[] lineBuffer=new byte[4096];
    String lineString;
    boolean success=true;
    //System.out.println("File:"+file.getName());
    try { 
      dataInput=new BufferedReader(new FileReader(file));
    } catch (IOException ex) { 
      success=false; System.out.println("Could not open DataInputStream: path:"+file.getAbsolutePath()+" file:"+file.getName()); 
      return success;
    }

    // If everythiing is OK, continue ...
    if (success) {
      try {
	lineString=dataInput.readLine();
	if (profileHeader.equals(lineString)) {
	    //success=readProfileContents(dataInput);
	}
	else {
	  success=false;
	  System.out.println("Profile Error: header not found");
	}
      } catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }
    }
    else System.out.println("***Error: could not open profile file:"+profileFilename);

    // Close the data input stream
    if (success) {
      try {
	dataInput.close();
      } catch (IOException ex) { 
	success=false; System.out.println("Could not close profile file"); 
	return success;
      }
    }
    else System.out.println("***Error: could not close profile file:"+profileFilename);
    return success;
  }

    
    /**
     * For reading the new profile file for PIA proxy version.  this profile file contains only the proxy name and port number.
     * all other information is now obtained via the proxy server.
     */
    private boolean readProxyProfile() {
	File file=new File("./",proxyProfileFilename);
	BufferedReader dataInput=null;
	byte[] lineBuffer=new byte[4096];
	String lineString;
	boolean success=true;
	try { 
	    dataInput=new BufferedReader(new FileReader(file));
	    proxyServerName=dataInput.readLine();
	    proxyAgentName=dataInput.readLine();
	    lineString=dataInput.readLine();
	    proxyServerPort=Integer.parseInt(lineString);
	    proxyUserName=dataInput.readLine();
	    System.out.println("---> Proxy Server: "+proxyServerName+" agent:"+proxyAgentName+" port:"+ proxyServerPort+" user:"+proxyUserName);
	    dataInput.close();
	} catch (IOException ex) { 
	    success=false; System.out.println("Could not open proxy file: path:"+file.getAbsolutePath()+" file:"+file.getName()); 
	}
	return success;
    }

  private boolean readProfileContents(String profileString) {
    String lineString="", lastRead="init";
    boolean success=true;
    int interation=1;
    Vector headers=new Vector();

    /**
     * 1.6.99 - Converts string into a BufferedReader so i can read a line at a time
     */
    char[] chararray=new char[profileString.length()];
    profileString.getChars(0,profileString.length(),chararray,0);
    BufferedReader dataInput=new BufferedReader(new CharArrayReader(chararray));
    try {
	lineString=dataInput.readLine();
    } catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }

    while (success & lastRead!=null) {
      try {
	lineString=dataInput.readLine();
	//System.out.println("-->> Line:"+lineString);
      } catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }
      if (success) {
	if (personalHeader.equals(lineString)) {
	  lastRead=readPersonalHeader(dataInput);
	  headers.addElement(lineString);
	  interation++;
	}
	else if (thumbarHeader.equals(lineString)) {
	  lastRead=readThumbarHeader(dataInput);
	  /*
	    if (Integer.parseInt(lastRead)>0) anohWindowMoreInfo=true;
	    else anohWindowMoreInfo=false;
	  */
	  headers.addElement(lineString);
	  interation++;
	}
	else if (highlightHeader.equals(lineString)) {
	  lastRead=readHighlightHeader(dataInput);
	  headers.addElement(lineString);
	  interation++;
	}
	else if (meterHeader.equals(lineString)) {
	  lastRead=readMeterHeader(dataInput);
	  headers.addElement(lineString);
	  interation++;
	}
	else if (colorsHeader.equals(lineString)) {
	  lastRead=readColorsHeader(dataInput);
	  headers.addElement(lineString);
	  interation++;
	}
	else if (piaHeader.equals(lineString)) {
	  lastRead=readPIAHeader(dataInput);
	  headers.addElement(lineString);
	  interation++;
	}
	else if (systemHeader.equals(lineString)) {
	  lastRead=readSystemHeader(dataInput);
	  headers.addElement(lineString);
	  interation++;
	}
	else {
	  System.out.println("***Error: did not find the header I was expecting:"+systemHeader+" interation:"+interation);
	  System.out.println("***HeaderDump:");
	  Enumeration enum=headers.elements();
	  int j=0;
	  while (enum.hasMoreElements()) {
	    String str=(String)enum.nextElement();
	    System.out.println("..."+(j++)+": "+str);
	  }
	  success=false;
	}
      }
    }
    return success;
  }

  private String readPersonalHeader(BufferedReader dataInput) {
    String blank="";
    try {
      userFName=dataInput.readLine();
      userLName=dataInput.readLine();
      userAccountName=dataInput.readLine();
      blank=dataInput.readLine();
    } catch (IOException ex) {blank=null; System.out.println("Failed to readline in PersonalHeader"); }
    return blank;
  }

  private String readThumbarHeader(BufferedReader dataInput) {
    String moreinfo="", blank="";
    int wordsProcessed=0, i=0, j=0, k=0, linesToProcess=2;
    byte[] buffer, wordBuf=null; 
    String redBuf1=null, greenBuf1=null, blueBuf1=null, redBuf2=null, greenBuf2=null, blueBuf2=null, 
	redBuf3=null, greenBuf3=null, blueBuf3=null;
    try {
      lensViewFraction=Integer.parseInt(dataInput.readLine());
      useLensLogo=Integer.parseInt(dataInput.readLine());
      useAnohDoubleLine=Integer.parseInt(dataInput.readLine());
      useLinkDoubleLine=Integer.parseInt(dataInput.readLine());
      for (i=0;i<linesToProcess;i++) {
	blank=dataInput.readLine();
	buffer=new byte[blank.length()];
	buffer=blank.getBytes();
	wordBuf=new byte[blank.length()];  // initialize
	for (j=0,wordsProcessed=0;j<buffer.length && wordsProcessed<9;) {  
	  // separated by spaces
	  for (k=0;k<buffer.length && j<buffer.length && buffer[j]!=' ';k++)  wordBuf[k]=buffer[j++];
	  if (wordsProcessed==0) redBuf1=new String(wordBuf,0,k);
	  else if (wordsProcessed==1) greenBuf1=new String(wordBuf,0,k);
	  else if (wordsProcessed==2) blueBuf1=new String(wordBuf,0,k);
	  else if (wordsProcessed==3) redBuf2=new String(wordBuf,0,k);
	  else if (wordsProcessed==4) greenBuf2=new String(wordBuf,0,k);
	  else if (wordsProcessed==5) blueBuf2=new String(wordBuf,0,k);
	  else if (wordsProcessed==6) redBuf3=new String(wordBuf,0,k);
	  else if (wordsProcessed==7) greenBuf3=new String(wordBuf,0,k);
	  else if (wordsProcessed==8) blueBuf3=new String(wordBuf,0,k);
	  wordsProcessed++;
	  j++;
	  //System.out.println("Words="+wordsProcessed+" wordbuf="+new String(wordBuf,0,k));
	}
	// kludgey, i know but a low priority at the moment...
	if (i==0) {
	  overviewWindowColor=new Color(Integer.parseInt(redBuf1),Integer.parseInt(greenBuf1),Integer.parseInt(blueBuf1));
	  overviewLensColor=new Color(Integer.parseInt(redBuf2),Integer.parseInt(greenBuf2),Integer.parseInt(blueBuf2));
	  overviewWindowLineColor=new Color(Integer.parseInt(redBuf3),Integer.parseInt(greenBuf3),Integer.parseInt(blueBuf3));
	}
	else if (i==1) {
	  overviewANOHColor=new Color(Integer.parseInt(redBuf1),Integer.parseInt(greenBuf1),Integer.parseInt(blueBuf1));
	  overviewLinkColor=new Color(Integer.parseInt(redBuf2),Integer.parseInt(greenBuf2),Integer.parseInt(blueBuf2));
	  overviewLensLineColor=new Color(Integer.parseInt(redBuf3),Integer.parseInt(greenBuf3),Integer.parseInt(blueBuf3));
	}
      }
      blank=dataInput.readLine();
    } catch (IOException ex) {blank=null; System.out.println("Failed to readline in PersonalHeader"); }
    return moreinfo;
  }

  private String readHighlightHeader(BufferedReader dataInput) {
      String blank="";
      int i=0, j=0, k=0, numStyles=0, wordsProcessed=0;
      byte[] buffer, wordBuf=null; 
      String style=null,redBuf=null, greenBuf=null, blueBuf=null, tipstring=null, redBuf2=null, greenBuf2=null, blueBuf2=null,
	  whole=null, shadow=null, bold=null, under=null, box=null;
      numHighlights=0;
      try {
	  numStyles=Integer.parseInt(dataInput.readLine());
	  defaultHliteStyle=Integer.parseInt(dataInput.readLine());
      if (numStyles>0) {
	  highlightStyles=new RH_HighlightStyle[numStyles];
	  for (i=0;i<numStyles;i++) {
	      blank=dataInput.readLine();
	      tipstring=dataInput.readLine();
	      buffer=new byte[blank.length()];
	      buffer=blank.getBytes();
	      wordBuf=new byte[blank.length()];  // initialize
	      // Now pick out the highlight style and RGB values 
	      for (j=0,wordsProcessed=0;j<buffer.length && wordsProcessed<11;) {  
	    // separated by spaces
		  for (k=0;k<buffer.length && j<buffer.length && buffer[j]!=' ';k++)  wordBuf[k]=buffer[j++];
		  if (wordsProcessed==0) bold=new String(wordBuf,0,k);
		  else if (wordsProcessed==1) under=new String(wordBuf,0,k);
		  else if (wordsProcessed==2) box=new String(wordBuf,0,k);
		  else if (wordsProcessed==3) shadow=new String(wordBuf,0,k);
		  else if (wordsProcessed==4) whole=new String(wordBuf,0,k);
		  else if (wordsProcessed==5) redBuf=new String(wordBuf,0,k);
		  else if (wordsProcessed==6) greenBuf=new String(wordBuf,0,k);
		  else if (wordsProcessed==7) blueBuf=new String(wordBuf,0,k);
		  else if (wordsProcessed==8) redBuf2=new String(wordBuf,0,k);
		  else if (wordsProcessed==9) greenBuf2=new String(wordBuf,0,k);
		  else if (wordsProcessed==10) blueBuf2=new String(wordBuf,0,k);
		  wordsProcessed++;
		  j++;
	      }
	      highlightStyles[numHighlights++]=new RH_HighlightStyle(Integer.parseInt(bold),Integer.parseInt(under),Integer.parseInt(box),
								     Integer.parseInt(shadow),Integer.parseInt(whole),
								     Integer.parseInt(redBuf),Integer.parseInt(greenBuf),
								     Integer.parseInt(blueBuf),Integer.parseInt(redBuf2),Integer.parseInt(greenBuf2),
								     Integer.parseInt(blueBuf2),numHighlights-1,tipstring);
	  }
	  blank=dataInput.readLine();
      }
      } catch (IOException ex) {blank=null; System.out.println("Failed to readline in HighlightHeader"); }
      return blank;
  }
    
  private String readMeterHeader(BufferedReader dataInput) {
    boolean success=true;
    String blank="";
    int i=0, j=0, k=0, numStyles=0, wordsProcessed=0;
    byte[] buffer, wordBuf=null; 
    String style=null,redBuf=null, greenBuf=null, blueBuf=null;
    numMeterStyles=0;
    try {
	sensitivitySetting=Integer.parseInt(dataInput.readLine());
	largeMeters=Integer.parseInt(dataInput.readLine());
	blank=dataInput.readLine();
      /*
	if (numStyles>0) {
	for (i=0;i<numStyles;i++) {
	blank=dataInput.readLine();
	buffer=new byte[blank.length()];
	buffer=blank.getBytes();
	wordBuf=new byte[blank.length()];  // initialize
	// Now pick out the highlight style and RGB values 
	for (j=0,wordsProcessed=0;j<buffer.length && wordsProcessed<4;) {  // 4 is constant: style r g b
	// separated by spaces
	for (k=0;k<buffer.length && j<buffer.length && buffer[j]!=' ';k++)  wordBuf[k]=buffer[j++];
	if (wordsProcessed==0) style=new String(wordBuf,0,k);
	else if (wordsProcessed==1) redBuf=new String(wordBuf,0,k);
	else if (wordsProcessed==2) greenBuf=new String(wordBuf,0,k);
	else if (wordsProcessed==3) blueBuf=new String(wordBuf,0,k);
	wordsProcessed++;
	j++;
	}
	meterStyles[numMeterStyles++]=new RH_MeterStyle(style,Integer.parseInt(redBuf),Integer.parseInt(greenBuf),
	Integer.parseInt(blueBuf));
	}
	blank=dataInput.readLine();
	}
      */
    } catch (IOException ex) {blank=null; System.out.println("Failed to readline in MeterHeader"); }
    
    return blank;
  }

  private String readColorsHeader(BufferedReader dataInput) {
    boolean success=true;
    String blank="";
    int i=0, j=0, k=0,  wordsProcessed=0, numLines=2;
    byte[] buffer, wordBuf=null; 
    String redBuf1=null, greenBuf1=null, blueBuf1=null, redBuf2=null, greenBuf2=null, blueBuf2=null, useBold=null, fontSize=null;
    try {
	motifNumber=Integer.parseInt(dataInput.readLine());
      for (i=0; i<numLines; i++) {
	blank=dataInput.readLine();
	buffer=new byte[blank.length()];
	buffer=blank.getBytes();
	wordBuf=new byte[blank.length()];  // initialize
	// Now pick out the highlight style and RGB values 
	for (j=0,wordsProcessed=0;j<buffer.length && wordsProcessed<6;) {  
	  // separated by spaces
	  for (k=0;k<buffer.length && j<buffer.length && buffer[j]!=' ';k++)  wordBuf[k]=buffer[j++];
	  if (wordsProcessed==0) redBuf1=new String(wordBuf,0,k);
	  else if (wordsProcessed==1) greenBuf1=new String(wordBuf,0,k);
	  else if (wordsProcessed==2) blueBuf1=new String(wordBuf,0,k);
	  else if (wordsProcessed==3) redBuf2=new String(wordBuf,0,k);
	  else if (wordsProcessed==4) greenBuf2=new String(wordBuf,0,k);
	  else if (wordsProcessed==5) blueBuf2=new String(wordBuf,0,k);
	  wordsProcessed++;
	  j++;
	}
	if (i==0) {
	  locationTextColor=new Color(Integer.parseInt(redBuf1),Integer.parseInt(greenBuf1),Integer.parseInt(blueBuf1));
	  locationBackColor=new Color(Integer.parseInt(redBuf2),Integer.parseInt(greenBuf2),Integer.parseInt(blueBuf2));
	}
	else if (i==1) {
	  modeTextColor=new Color(Integer.parseInt(redBuf1),Integer.parseInt(greenBuf1),Integer.parseInt(blueBuf1));
	  modeBackColor=new Color(Integer.parseInt(redBuf2),Integer.parseInt(greenBuf2),Integer.parseInt(blueBuf2));
	}
	//System.out.println("Colors:"+redBuf1+" "+greenBuf1+" "+blueBuf1+" "+redBuf2+" "+greenBuf2+" "+blueBuf2);
      }
      blank=dataInput.readLine();
    } catch (IOException ex) {blank=null; System.out.println("Failed to readline in MeterHeader"); }
    
    return blank;
  }


    private String readPIAHeader(BufferedReader dataInput) {
	boolean success=true;
	String blank="";
	try {
	    piaHost=dataInput.readLine();
	    piaPort=Integer.parseInt(dataInput.readLine());
	    piaAgentName=dataInput.readLine();
	    piaPrinterName=dataInput.readLine();
	    blank=dataInput.readLine();
	} catch (IOException ex) {blank=null; System.out.println("Failed to readline in MeterHeader"); }
	
	return blank;
    }

  private String readSystemHeader(BufferedReader dataInput) {  
    String blank="";
    try {
      parentPath=dataInput.readLine();
      gifsPath=dataInput.readLine();
      homeURL=dataInput.readLine();
      privateDir=dataInput.readLine();
      cacheDir=dataInput.readLine();
      useCacheDocuments=Integer.parseInt(dataInput.readLine());
      useCacheImages=Integer.parseInt(dataInput.readLine());
      documentCacheSize=Integer.parseInt(dataInput.readLine());
      similarityThreshold=Integer.parseInt(dataInput.readLine());
      useLoadImages=Integer.parseInt(dataInput.readLine());
      preferredWidth=Integer.parseInt(dataInput.readLine());
      preferredHeight=Integer.parseInt(dataInput.readLine());
      preferredX=Integer.parseInt(dataInput.readLine());
      preferredY=Integer.parseInt(dataInput.readLine());
      documentFontName=dataInput.readLine();
      documentFontSize=Integer.parseInt(dataInput.readLine());
      locationFontName=dataInput.readLine();
      modeFontSize=locationFontSize=Integer.parseInt(dataInput.readLine());
      animateLogo=Integer.parseInt(dataInput.readLine());
      autoLoadHomeURL=Integer.parseInt(dataInput.readLine());
      populateConcepts=Integer.parseInt(dataInput.readLine());
      betterImageScaling=Integer.parseInt(dataInput.readLine());
      lastModified=dataInput.readLine();
      blank=dataInput.readLine();

      defaultHomeURL=RH_GlobalVars.httpFileTypeTag+parentPath+"index.html";
      System.out.println("***DefaultURL:"+defaultHomeURL);
    } catch (IOException ex) {blank=null; System.out.println("Failed to readline in PersonalHeader"); }
    
    return blank;
  }

  private boolean readConceptsContents(String conceptsString) {
    boolean success=true;
    String lineString="", conceptName="", conceptShortName="";
    int i=0, j=0, k=0, x=0, num=0, numStyles=0, wordsProcessed=0, maxKeywords=mainFrame.maxNumberKeywords, maxWordStrings=10,numKeywords=0, active=0;
    Double prior;
    byte[] buffer, wordBuf=null; 
    String[][] keywords=new String[maxKeywords][maxWordStrings];

    //System.out.println(conceptsString);

    /**
     * 1.6.99 - Converts string into a BufferedReader so i can read a line at a time
     */
    char[] chararray=new char[conceptsString.length()];
    conceptsString.getChars(0,conceptsString.length(),chararray,0);
    BufferedReader dataInput=new BufferedReader(new CharArrayReader(chararray));
    try {
	lineString=dataInput.readLine();
	System.out.println("-->> FirstLine:"+lineString);
	lineString=dataInput.readLine();
	System.out.println("-->> SecondLine:"+lineString);
	num=Integer.parseInt(lineString);
	numAllConcepts=num;
    } catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }

    try {
      if (num>0) {
	for (i=0;i<num;i++) {
	    //System.out.println("------------------------");
	    conceptName=dataInput.readLine();
	    conceptShortName=dataInput.readLine();
	    // Read prior probability
	    lineString=dataInput.readLine();
	    prior=new Double(lineString);
	    // Read state
	    lineString=dataInput.readLine();
	    active=Integer.parseInt(lineString);
	    // Read number keywords
	    lineString=dataInput.readLine();
	    numKeywords=Integer.parseInt(lineString);
	    //System.out.println("Name:"+conceptName+" phrases:"+numKeywords);
	    //System.out.println(i+"> Concept:"+conceptName+" - "+conceptShortName+" prior="+prior.doubleValue()+" topics="+numKeywords+" active="+active);
	    // Now pick out the highlight style and RGB values 
	    for (j=0;j<numKeywords && j<maxKeywords;j++) {  
		lineString=dataInput.readLine();
		buffer=new byte[lineString.length()];
		buffer=lineString.getBytes();
		wordBuf=new byte[lineString.length()]; 
		k=0;
		// Process line of text making individual keywords for each topic
		for (k=0, wordsProcessed=0;k<buffer.length && wordsProcessed<maxWordStrings;k++) {
		    for (x=0;x<buffer.length && k<buffer.length && buffer[k]!=' ';x++) wordBuf[x]=buffer[k++];
		    keywords[j][wordsProcessed]=new String(wordBuf,0,x);
		    //System.out.print(j+">...["+wordsProcessed+"]:");
		    //System.out.print(keywords[j][wordsProcessed]);
		    wordsProcessed++;
		}
		//System.out.println("");
		keywords[j][wordsProcessed]=null;
	    }
	    mainFrame.allConcepts[i] = new RH_Concept(conceptName,conceptShortName,prior.doubleValue());
	    //mainFrame.allConcepts[i].addKeywords(keywords,j);
	    if (active==1) mainFrame.allConcepts[i].setActive(true);
	    else mainFrame.allConcepts[i].setActive(false);
	    lineString=dataInput.readLine(); // read blank line that separates concepts
	}
	lineString=dataInput.readLine();
	success=true;
      }
    } catch (IOException ex) {lineString=null; System.out.println("Failed to readline in MeterHeader"); success=false;}
    
    return success;
  }

    public void addGroup(String name, int num, String tooltip, String[] g_concepts) {
	RH_ConceptGroup[] holder=new RH_ConceptGroup[conceptGroups.length];
	System.arraycopy(conceptGroups,0,holder,0,conceptGroups.length);
	conceptGroups=new RH_ConceptGroup[holder.length+1];
	System.arraycopy(holder,0,conceptGroups,0,holder.length-1);
	conceptGroups[conceptGroups.length-2]=new RH_ConceptGroup(name,tooltip,num);
	conceptGroups[conceptGroups.length-2].setConcepts(g_concepts);
	//** Make the Retro group the last group ALWAYS!
	conceptGroups[conceptGroups.length-1]=holder[holder.length-1];
	numGroups=conceptGroups.length;
    }
    public void removeGroup(int idx) {
	RH_ConceptGroup[] holder=new RH_ConceptGroup[conceptGroups.length-1];
	System.arraycopy(conceptGroups,0,holder,0,idx);
	for (int i=idx+1,j=idx;j<holder.length;i++,j++) holder[j]=conceptGroups[i];
	conceptGroups=new RH_ConceptGroup[holder.length];
	System.arraycopy(holder,0,conceptGroups,0,conceptGroups.length);
	numGroups=conceptGroups.length;
    }
    public void setGroupName(int idx, String newname) {
	if (idx<conceptGroups.length) conceptGroups[idx].setName(newname);
    }
    public void setGroupToolTipString(int idx, String tip) {
	if (idx<conceptGroups.length) conceptGroups[idx].setToolTipString(tip);
    }
    public void setGroupConcepts(int idx, String[] newconcepts) {
	if (idx<conceptGroups.length) conceptGroups[idx].setConcepts(newconcepts);
    }

    public boolean saveProfile() {
	File file=new File("./",profileFilename), bakFile=new File("./",profileFilename), 
	    bakbakFile=new File("./",profileFilename+bakExtension);
	StringBuffer buffer=null;
	String newline=mainFrame.getNewlineByte();
	String[] concepts=null, topics=null;
	boolean success=false;
	int i=0, j=0, active=-1, len=0;
	if (makeBackupFiles) {
	    try {
		if (bakbakFile.exists()) bakbakFile.delete();
	    } catch (SecurityException ex) { new RH_PopupError(commBus.parent,"You cannot make a backup of this file - permission denied"); }
	    try {
		bakFile.renameTo(new File("./",profileFilename+bakExtension));
	    } catch (SecurityException ex) { new RH_PopupError(commBus.parent,"Could not rename back up file:"+profileFilename+bakExtension+
							       " - permission denied"); }
	}
	try {
	    FileOutputStream fp=new FileOutputStream(file.toString()); 
	    //** Personal Header
	    buffer=new StringBuffer(profileHeader).append(newline).
		append(personalHeader).append(newline).append(userFName).append(newline).append(userLName).append(newline).append(userAccountName).
		append(newline).append(newline);
	    fp.write(buffer.toString().getBytes());
	    //** Thumbar
	    buffer=new StringBuffer(thumbarHeader).append(newline).append(lensViewFraction).append(newline).append(useLensLogo).append(newline).
		append(useAnohDoubleLine).append(newline).append(useLinkDoubleLine).append(newline).
		//**.....
		append(overviewWindowColor.getRed()+" ").append(overviewWindowColor.getGreen()+" ").append(overviewWindowColor.getBlue()+" ").
		//**.....
		append(overviewLensColor.getRed()+" ").append(overviewLensColor.getGreen()+" ").append(overviewLensColor.getBlue()+" ").
		//**.....
		append(overviewWindowLineColor.getRed()+" ").append(overviewWindowLineColor.getGreen()+" ").append(overviewWindowLineColor.getBlue()+" ").
		append(newline).
		//**.....
		append(overviewANOHColor.getRed()+" ").append(overviewANOHColor.getGreen()+" ").append(overviewANOHColor.getBlue()+" ").
		//**.....
		append(overviewLinkColor.getRed()+" ").append(overviewLinkColor.getGreen()+" ").append(overviewLinkColor.getBlue()+" ").
		//**.....
		append(overviewLensLineColor.getRed()+" ").append(overviewLensLineColor.getGreen()+" ").append(overviewLensLineColor.getBlue()+" ").
		append(newline).append(newline);
	    fp.write(buffer.toString().getBytes());
	    //** Highlight
	    buffer=new StringBuffer(highlightHeader).append(newline).append(highlightStyles.length).append(newline).append(defaultHliteStyle).
		append(newline);
	    for (i=0;i<highlightStyles.length;i++) {
		//System.out.print(i+"> Hi:"); 
		buffer.append(highlightStyles[i].getBold()+" ").append(highlightStyles[i].getUnder()+" ").append(highlightStyles[i].getBox()+" ").
		    append(highlightStyles[i].getShadow()+" ").
		    append(highlightStyles[i].getWhole()+" ").append(highlightStyles[i].getRed()+" ").
		    append(highlightStyles[i].getGreen()+" ").append(highlightStyles[i].getBlue()+" ").
		    append(highlightStyles[i].getForeRed()+" ").append(highlightStyles[i].getForeGreen()+" ").
		    append(highlightStyles[i].getForeBlue()+" ").append(newline).append(highlightStyles[i].getTip()).append(newline);
	    }
	    buffer.append(newline);
	    fp.write(buffer.toString().getBytes());
	    //** Meters
	    buffer = new StringBuffer(meterHeader).append(newline).append(sensitivitySetting).append(newline).
		append(largeMeters).append(newline).append(newline);
	    fp.write(buffer.toString().getBytes());
	    //** Colors
	    buffer = new StringBuffer(colorsHeader).append(newline).append(motifNumber).append(newline).
		append(locationTextColor.getRed()+" ").append(locationTextColor.getGreen()+" ").append(locationTextColor.getBlue()+" ").
		append(locationBackColor.getRed()+" ").append(locationBackColor.getGreen()+" ").append(locationBackColor.getBlue()+" ").
		append(newline).
		append(modeTextColor.getRed()+" ").append(modeTextColor.getGreen()+" ").append(modeTextColor.getBlue()+" ").
		append(modeBackColor.getRed()+" ").append(modeBackColor.getGreen()+" ").append(modeBackColor.getBlue()+" ").
		append(newline).append(newline);
	    fp.write(buffer.toString().getBytes());
	    //** PIA
	    buffer=new StringBuffer(piaHeader).append(newline).append(piaHost).append(newline).append(piaPort).append(newline).
		append(piaAgentName).append(newline).append(piaPrinterName).append(newline).append(newline);
	    fp.write(buffer.toString().getBytes());
	    //** System
	    lastModified=new Date().toString();
	    buffer=new StringBuffer(systemHeader).append(newline).append(parentPath).append(newline).append(gifsPath).append(newline).
		append(homeURL).append(newline).append(privateDir).append(newline).append(cacheDir).append(newline).
		append(useCacheDocuments).append(newline).append(useCacheImages).append(newline).
		append(documentCacheSize).append(newline).append(similarityThreshold).append(newline).
		append(useLoadImages).append(newline).append(preferredWidth).append(newline).append(preferredHeight).append(newline).
		append(preferredX).append(newline).append(preferredY).append(newline).append(documentFontName).append(newline).
		append(documentFontSize).append(newline).append(locationFontName).append(newline).append(locationFontSize).append(newline).
		append(animateLogo).append(newline).append(autoLoadHomeURL).append(newline).append(populateConcepts).append(newline).
		append(betterImageScaling).append(newline).append(lastModified);
	    fp.write(buffer.toString().getBytes());						   
	    
	    fp.close();
	    success=true;
	} catch (IOException ex) {
	    success=false;
	    System.out.println("Could not create save profile file:"+file.toString());
	    new RH_PopupError(commBus.parent,"Could not create profile file:"+file.toString()+" - permission denied"); 
	}
	return success;
    }
    
    public boolean saveConcepts() {
	/*
	  File file=new File("./",conceptsFilename), bakFile=new File("./",conceptsFilename), 
	  bakbakFile=new File("./",conceptsFilename+bakExtension);
	  StringBuffer buffer=null;
	  String newline=mainFrame.getNewlineByte();
	  String[] concepts=null, topics=null;
	  boolean success=false;
	  int i=0, j=0, active=-1, len=0;
	  if (makeBackupFiles) {
	  try {
	  if (bakbakFile.exists()) bakbakFile.delete();
	  } catch (SecurityException ex) { new RH_PopupError(commBus.parent,"You cannot make a backup of this file - permission denied"); }
	  try {
	  bakFile.renameTo(new File("./",conceptsFilename+bakExtension));
	  } catch (SecurityException ex) { new RH_PopupError(commBus.parent,"Could not rename back up file:"+conceptsFilename+bakExtension+
	  " - permission denied"); }
	  }
	  try {
	  FileOutputStream fp=new FileOutputStream(file.toString());   
	  len=mainFrame.numAllConcepts;
	  buffer=new StringBuffer(conceptsHeader).append(newline).append(len).append(newline);
	  fp.write(buffer.toString().getBytes());
	  for (i=0;i<len;i++) {
	  //topics=mainFrame.allConcepts[i].getTopicKeywordStrings();
	  if (mainFrame.allConcepts[i].isActive()) active=1;
	  else active=0;
	  buffer=new StringBuffer(mainFrame.allConcepts[i].getName()).append(newline).
	  append(mainFrame.allConcepts[i].getShortName()).append(newline).
	  append(mainFrame.allConcepts[i].getPrior()).append(newline).
	  append(active).append(newline).
	  append(topics.length).append(newline);
	  fp.write(buffer.toString().getBytes());
	  buffer=new StringBuffer();
	  for (j=0;j<topics.length;j++) buffer.append(topics[j]).append(newline);
	  buffer.append(newline);
	  fp.write(buffer.toString().getBytes());
	  }
	  
	  fp.close();
	  success=true;
	  } catch (IOException ex) {
	  success=false;
	  System.out.println("Could not create save concepts file:"+file.toString());
	  new RH_PopupError(commBus.parent,"Could not create concepts file:"+file.toString()+" - permission denied"); 
	  }
	*/
	return true; //success;
    }

    public boolean saveGroups() {
	File file=new File("./",groupsFilename), bakFile=new File("./",groupsFilename), bakbakFile=new File("./",groupsFilename+bakExtension);
	StringBuffer buffer=null;
	String newline=mainFrame.getNewlineByte();
	String[] concepts=null;
	boolean success=false;
	int i=0, j=0;
	if (makeBackupFiles) {
	    try {
		if (bakbakFile.exists()) bakbakFile.delete();
	    } catch (SecurityException ex) { new RH_PopupError(commBus.parent,"You cannot make a backup of this file - permission denied"); }
	    try {
		bakFile.renameTo(new File("./",groupsFilename+bakExtension));
	    } catch (SecurityException ex) { new RH_PopupError(commBus.parent,"Could not rename back up file:"+groupsFilename+bakExtension+
							   " - permission denied"); }
	}
	try {
	    FileOutputStream fp=new FileOutputStream(file.toString());   
	    buffer=new StringBuffer(groupsHeader).append(newline).append(numGroups-1).append(newline).append(defaultGroup).append(newline);
	    fp.write(buffer.toString().getBytes());
	    for (i=0;i<numGroups-1;i++) {
		concepts=conceptGroups[i].getConcepts();
		buffer=new StringBuffer(conceptGroups[i].getName()).append(newline).append(conceptGroups[i].getToolTipString()).
		    append(newline).append(concepts.length).append(newline);
		for (j=0;j<concepts.length;j++) buffer.append(concepts[j]).append(newline);
		buffer.append(newline);
		fp.write(buffer.toString().getBytes());
	    }
	    fp.close();
	    success=true;
	} catch (IOException ex) {
	    success=false;
	    System.out.println("Could not create save groups file:"+file.toString());
	    new RH_PopupError(commBus.parent,"Could not create groups file:"+file.toString()+" - permission denied"); 
	}
	return success;
    }


    private boolean readGroupsContents(String groupsString) {
	boolean success=true; 
	String lineString="", groupname="", tooltip="";
	int i=0, j=0, numconcepts=0;
	
	/**
	 * 1.6.99 - Converts string into a BufferedReader so i can read a line at a time
	 */
	char[] chararray=new char[groupsString.length()];
	groupsString.getChars(0,groupsString.length(),chararray,0);
	BufferedReader dataInput=new BufferedReader(new CharArrayReader(chararray));
	try {
	    lineString=dataInput.readLine();
	    //System.out.println("-->> groups FirstLine:"+lineString);
	    lineString=dataInput.readLine();
	    //System.out.println("-->> groups SecondLine:"+lineString);
	    numGroups=Integer.parseInt(lineString);
	    numGroups++;  // add one for the system's retro group
	    lineString=dataInput.readLine();
	    defaultGroup=Integer.parseInt(lineString);
	} catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }
	
	
	try {
	    if (numGroups>0) {
		conceptGroups=new RH_ConceptGroup[numGroups];
		// Use num-1 here because num is 1 greater than the number of groups in the file because of retro group
		for (i=0;i<numGroups-1;i++) {
		    groupname=dataInput.readLine();
		    tooltip=dataInput.readLine();
		    //System.out.print(i+"> Group:"+groupname+" tip:"+tooltip);
		    lineString=dataInput.readLine();
		    numconcepts=Integer.parseInt(lineString);
		    //System.out.println(" concepts="+numconcepts);
		    conceptGroups[i]=new RH_ConceptGroup(groupname,tooltip,numconcepts);
		    for (j=0;j<numconcepts;j++) {
			lineString=dataInput.readLine();
			conceptGroups[i].addConcept(lineString);
			//System.out.println(j+">...:"+lineString);
		    }
		    lineString=dataInput.readLine(); // read blank between groups
		}
		conceptGroups[numGroups-1]=new RH_ConceptGroup("Retro","Retrofit group: tailored to fit old versions of RH documents",0);
		lineString=dataInput.readLine();
	    }
	    else System.out.println("***ERROR: concept groups number invalid:"+numGroups);
	} catch (IOException ex) {lineString=null; System.out.println("Failed to readline in groups header"); success=false; }
	
	return success;
    }
    
    public String getUserFirstName() {
	return userFName;
    }
    public void setUserFirstName(String n) {
	userFName=n;
    }
    public String getUserLastName() {
	return userLName;
    }
    public void setUserLastName(String n) {
	userLName=n;
    }
    public String getUserAccountName() {
	return userAccountName;
    }
    public void setUserAccountName(String n) {
	userAccountName=n;
    }
    public String getPath() {
	return parentPath;
    }
    public void setPath(String n) {
	parentPath=n;
    }
    public String getGifsPath() {
	return gifsPath;
    }
    public void setGifsPath(String n) {
	gifsPath=n;
    }
    public String getPrivateDir() {
	return privateDir;
    }
    public void setPrivateDir(String n) {
	privateDir=n;
    }
    public String getCacheDir() {
	return cacheDir;
    }
    public void setCacheDir(String n) {
	cacheDir=n;
    }
    public boolean getUseCacheDocuments() {
	if (useCacheDocuments==1) return true;
	else return false;
    }
    public void setUseCacheDocuments(int n) {
	useCacheDocuments=n;
    }
    public boolean getUseCacheImages() {
	if (useCacheImages==1) return true;
	else return false;
    }
    public void setUseCacheImages(int n) {
	useCacheImages=n;
    }
    public int getDocumentCacheSize() {
	return documentCacheSize;
    }
    public void setDocumentCacheSize(int n) {
	documentCacheSize=n;
	System.out.println("**Setting document cache size:"+documentCacheSize);
    }
    public boolean getAnimateLogo() {
	if (animateLogo==1) return true;
	else return false;
    }
    public void setAnimateLogo(int n) {
	animateLogo=n;
    }
    public boolean getAutoLoadHomeURL() {
	if (autoLoadHomeURL==1) return true;
	else return false;
    }
    public void setAutoLoadHomeURL(int n) {
	autoLoadHomeURL=n;
    }
    public boolean getUseLoadImages() {
	if (useLoadImages==1) return true;
	else return false;
    }
    public void setLoadImages(boolean set) {
	if (set) useLoadImages=1;
	else useLoadImages=0;
    }
    public String getLastModified() {
	return lastModified;
    }
    public void setLastModified(String date) {
	lastModified=date;
    }
    public boolean getPopulateConcepts() {
	return (populateConcepts==1 ? true : false);
    }
    public void setPopulateConcepts(int n) {
	populateConcepts=n;
    }
    public boolean getUseBetterImageScalingMethod() {
	return (betterImageScaling==1 ? true : false);
    }
    public void setUseBetterImageScalingMethod(int n) {
	betterImageScaling=n;
    }

    public boolean getANOHWindowMoreInfo() {
	return anohWindowMoreInfo;
    }
    public RH_HighlightStyle[] getHighlightStyles() {
	return highlightStyles;
    }
    public void setHighlightStyles(RH_HighlightStyle[] n) {
	System.out.println("**new styls:"+n);
	highlightStyles=n;
    }
    public int getDefaultHliteStyle() {
	return defaultHliteStyle;
    }
    public void setDefaultHliteStyle(int n) {
	defaultHliteStyle=n;
    }
    public RH_MeterStyle[] getMeterStyles() {
	return meterStyles;
    }
    public int getSensitivitySetting() {
	return sensitivitySetting;
    }
    public void setSensitivitySetting(int n) {
	sensitivitySetting=n; 
    }
    public boolean getLargeMeters() {
	return (largeMeters==1 ? true : false);
    }
    public void setLargeMeters(int n) {
	largeMeters=n;
    }
    
    public Color getDefaultMeterColor() {
	if (numMeterStyles>0) return new Color(meterStyles[defMeterID].getRed(),meterStyles[defMeterID].getGreen(),meterStyles[defMeterID].getBlue());
	else return null;
    }
    public Color getCompositeMeterColor() {
	if (numMeterStyles>0) return new Color(meterStyles[compMeterID].getRed(),meterStyles[compMeterID].getGreen(),meterStyles[compMeterID].getBlue());
	return null;
    }
    public Color getLocationTextColor() {
	return locationTextColor;
    }
    public void setLocationTextColor(Color n) {
	locationTextColor=n;
    }
    public Color getLocationBackColor() {
	return locationBackColor;
    }
    public void setLocationBackColor(Color n) {
	locationBackColor=n;
    }
    public Color getModeTextColor() {
	return modeTextColor;
    }
    public void setModeTextColor(Color n) {
	modeTextColor=n;
    }
    public Color getModeBackColor() {
	return modeBackColor;
    }
    public void setModeBackColor(Color n) {
	modeBackColor=n;
    }
    public boolean getLocationUseBold() {
	return locationUseBold;
    }
    public boolean getModeUseBold() {
	return modeUseBold;
    }
    public int getLocationFontSize() {
	return locationFontSize;
    }
    public void setLocationFontSize(int n) {
	modeFontSize=locationFontSize=n;
    }
    public int getModeFontSize() {
	return modeFontSize;
    }
    public String getLocationFontName() {
	return locationFontName;
    }
    public void setLocationFontName(String n) {
	locationFontName=n;
    }
    public int getMotifNumber() {
	return motifNumber;
    }
    public void setMotifName(int n) {
	motifNumber=n;
    }
    public void setHomeURL(String url) {
	homeURL=url;
    }
    public String getHomeURL() {
	//System.out.println("HOME:"+homeURL+" default:"+defaultHomeURL);
	if (getAutoLoadHomeURL()) return homeURL;
	else return defaultHomeURL;
    }
    public Color getOverviewWindowColor() {
	return overviewWindowColor;
    }
    public void setOverviewWindowColor(Color n) {
	overviewWindowColor=n;
    }
    public Color getOverviewWindowLineColor() {
	return overviewWindowLineColor;
    }
    public void setOverviewWindowLineColor(Color n) {
	overviewWindowLineColor=n;
    }
    public Color getOverviewLensColor() {
	return overviewLensColor;
    }
    public void setOverviewLensColor(Color n) {
	overviewLensColor=n;
    }
    public Color getOverviewANOHColor() {
	return overviewANOHColor;
    }
    public void setOverviewANOHColor(Color n) {
	overviewANOHColor=n;
    }
    public Color getOverviewLinkColor() {
	return overviewLinkColor;
    }
    public void setOverviewLinkColor(Color n) {
	overviewLinkColor=n;
    }
    public Color getOverviewLensLineColor() {
	return overviewLensLineColor;
    }
    public void setOverviewLensLineColor(Color n) {
	overviewLensLineColor=n;
    }

    public int getLensViewFraction() {
	return lensViewFraction;
    }
    public void setLensViewFraction(int n) {
	lensViewFraction=n;
    }
    public int getSimilarityThreshold() {
	return similarityThreshold;
    }
    public void setSimilarityThreshold(int n) {
	similarityThreshold=n;
    }
    
    public int getPreferredWidth() {
	return preferredWidth;
    }
    public void setPreferredWidth(int n) {
	preferredWidth=n;
    }
    public int getPreferredHeight() {
	return preferredHeight;
    }
    public void setPreferredHeight(int n) {
	preferredHeight=n;
    }
    public int getPreferredX() {
	return preferredX;
    }
    public void setPreferredX(int n) {
	preferredX=n;
    }
    public int getPreferredY() {
	return preferredY;
    }
    public void setPreferredY(int n) {
	preferredY=n;
    }
    public String getDocumentFontName() {
	return documentFontName;
    }
    public void setDocumentFontName(String n) {
	documentFontName=n;
    }
    public int getDocumentFontSize() {
	return documentFontSize;
    }
    public void setDocumentFontSize(int n) {
	documentFontSize=n;
    }

    public String getPiaHost() {
	return piaHost;
    }
    public void setPiaHost(String n) {
	piaHost=n;
    }
    public int getPiaPort() {
	return piaPort;
    }
    public void setPiaPort(int n) {
	piaPort=n;
    }
    public String getPiaAgentName() {
	return piaAgentName;
    }
    public void setPiaAgentName(String n) {
	piaAgentName=n;
    }
    public String getPiaPrinterName() {
	return piaPrinterName;
    }
    public void setPiaPrinterName(String n) {
	piaPrinterName=n;
    }

    public String  getProxyServerName() {
	return proxyServerName;
    }
    public int getProxyServerPort() {
	return proxyServerPort;
    }
    public String getProxyAgentName() {
	return proxyAgentName;
    }
    public String getProxyUserName() {
	return proxyUserName;
    }
    
    public RH_ConceptGroup getGroup(int num) {
	return (numGroups>0 ? conceptGroups[num] : null);
    } 
    
    public RH_ConceptGroup[] getConceptGroups() {
	return conceptGroups;
    }
    public int getDefaultGroup() {
	return defaultGroup;
    }
    public void setDefaultGroup(int idx) {
	if (idx<conceptGroups.length) defaultGroup=idx;
    }

    public int getNumberGroups() {
	return numGroups;
    }

    public int getUseLensLogo() {
	return useLensLogo;
    }
    public void setUseLensLogo(int newval) {
	useLensLogo=newval;
    }

    public void setUseAnohDoubleLine(int n) {
	useAnohDoubleLine=n;
    }
    public int getUseAnohDoubleLine() {
	return useAnohDoubleLine;
    }
    public void setUseLinkDoubleLine(int n) {
	useLinkDoubleLine=n;
    }
    public int getUseLinkDoubleLine() {
	return useLinkDoubleLine;
    }


  private boolean readConcepts() {
    File file=new File("./",conceptsFilename);
    BufferedReader dataInput=null;
    byte[] lineBuffer=new byte[4096];
    String lineString;
    boolean success=true;
    int numURLS=0;
    //System.out.println("File:"+file.getName());
    try { 
      dataInput=new BufferedReader(new FileReader(file));
      lineString=dataInput.readLine();
      if (conceptsHeader.equals(lineString)) {
	lineString=dataInput.readLine();
	numAllConcepts=Integer.parseInt(lineString);
	//lineString=readConceptsHeader(dataInput,numAllConcepts);
      }
      else {
	success=false;
	System.out.println("Profile Error: header not found");
      }
    } catch (IOException ex) { success=false; System.out.println("Could not open Concept Stream"); }
    // Close the data input stream
    if (success)
      try {
      dataInput.close();
    } catch (IOException ex) { success=false; System.out.println("Could not close concepts file"); }
    return success;
  }

  private boolean readGroups() {
    File file=new File("./",groupsFilename);
    BufferedReader dataInput=null;
    byte[] lineBuffer=new byte[4096];
    String lineString;
    boolean success=true;
    //System.out.println("File:"+file.getName());
    try { 
      dataInput=new BufferedReader(new FileReader(file));
      lineString=dataInput.readLine();
      if (groupsHeader.equals(lineString)) {
	lineString=dataInput.readLine();
	numGroups=Integer.parseInt(lineString);
	numGroups++;  // add one for the system's retro group
	lineString=dataInput.readLine();
	defaultGroup=Integer.parseInt(lineString);
	//System.out.println("***Num:"+numGroups+" def:"+defaultGroup);
	//lineString=readGroupsHeader(dataInput,numGroups);
      }
      else {
	success=false;
	System.out.println("Profile Error: group header not found");
      }
    } catch (IOException ex) { success=false; System.out.println("Could not open Groups Stream"); }
    // Close the data input stream
    if (success)
      try {
      dataInput.close();
    } catch (IOException ex) { success=false; System.out.println("Could not close Groups file"); }
    return success;
  }

  private boolean readLocations() {
    File file=new File("./",locationsFilename);
    BufferedReader dataInput=null;
    byte[] lineBuffer=new byte[4096];
    String lineString;
    boolean success=true;
    int numURLS=0;
    //System.out.println("File:"+file.getName());
    try { 
      dataInput=new BufferedReader(new FileReader(file));
    } catch (IOException ex) { success=false; System.out.println("Could not open DataInputStream"); }

    // If everythiing is OK, continue ...
    if (success) {
      try {
	lineString=dataInput.readLine();
	if (locationsHeader.equals(lineString)) {
	  lineString=dataInput.readLine();
	  numURLS=Integer.parseInt(lineString);
	  //success=readLocationsContents(dataInput,file.length(),numURLS);
	}
	else {
	  success=false;
	  System.out.println("Profile Error: header not found");
	}
      } catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }
    }

    // Close the data input stream
    if (success)
      try {
      dataInput.close();
    } catch (IOException ex) { success=false; System.out.println("Could not close profile file"); }
    return success;
  }

  private boolean readLocationsContents(String locationsString) {
    String urlString="", titleString="", lastVisited="", lastRead="init", lineString=null;
    boolean success=true;
    int processed=0, numURLS=0;
    Long visited=null;

    /**
     * 1.6.99 - Converts string into a BufferedReader so i can read a line at a time
     */
    char[] chararray=new char[locationsString.length()];
    locationsString.getChars(0,locationsString.length(),chararray,0);
    BufferedReader dataInput=new BufferedReader(new CharArrayReader(chararray));
    try {
	lineString=dataInput.readLine();
	lineString=dataInput.readLine();
	numURLS=Integer.parseInt(lineString);
    } catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }

    mainFrame.locationList=new RH_LocationItem[numURLS];
    while (processed<numURLS && success & lastRead!=null) {
      try {
	urlString=dataInput.readLine();
	titleString=dataInput.readLine();
	lastVisited=dataInput.readLine();
	visited=new Long(lastVisited);
      } catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }
      if (success) {
	mainFrame.locationList[processed++]=new RH_LocationItem(urlString,titleString,visited.longValue());
      }
    }
    return success;
  }

  /**
   * Shudown method
   */
  public void shutdown() {
    writeLocationList();
  }

  public boolean writeLocationList() {
    File file=new File("./",locationsFilename);
    BufferedWriter dataOutput=null;
    String lineString, urlStr="";
    boolean success=true;
    try { 
      dataOutput=new BufferedWriter(new FileWriter(file));
    } catch (IOException ex) { success=false; System.out.println("Could not open DataInputStream"); }

    urlStr=new String(""+mainFrame.locationList.length);
    // If everythiing is OK, continue ...
    if (success) {
      try {
	dataOutput.write(locationsHeader,0,locationsHeader.length());
	dataOutput.newLine();
	dataOutput.write(urlStr,0,urlStr.length());
	dataOutput.newLine();
	for (int i=0;i<mainFrame.locationList.length;i++) {
	  long visited=mainFrame.locationList[i].getLastVisited();
	  String tmp=new String(""+visited);
	  dataOutput.write(mainFrame.locationList[i].getURL(),0,mainFrame.locationList[i].getURL().length());
	  dataOutput.newLine();
	  dataOutput.write(mainFrame.locationList[i].getTitle(),0,mainFrame.locationList[i].getTitle().length());
	  dataOutput.newLine();
	  dataOutput.write(tmp,0,tmp.length());
	  dataOutput.newLine();
	}
      } catch (IOException ex) { success=false; System.out.println("Could not write to output stream: "+locationsFilename); }
    }
    if (success)
      try {
      dataOutput.close();
    } catch (IOException ex) { success=false; System.out.println("Could not close locations file when writing"); }
    return success;
  }
}
