/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.09.98 - revised 02-26-98
 *
 */
package ricoh.rhpm;

import java.io.*;
import java.lang.*;
import java.util.*;

import ricoh.rh.RH_GlobalVars;


public class RHHistoryDB  {
    private String privatePath="", historyFilename="";
    private int nextID=0;
    private Hashtable historyHash;
    
    public RHHistoryDB(String filename, String path) {
	privatePath=path;
	historyFilename=new StringBuffer().append(privatePath+RH_GlobalVars.rhPathSeparator).append(filename).toString();
	//System.out.println("---HistoryFile:"+historyFilename.toString());
	File file=new File(historyFilename);
	BufferedReader dataInput=null;
	createHistoryList();
    }

  /**
   * Check the database to see if the URL is present
   */
  public boolean createHistoryList() {
    File file=new File(historyFilename);
    BufferedReader dataInput=null;
    String line="",line3="";
    int count=0, idnumber;
    System.out.println(":::HistoryFile:"+historyFilename);
    try { 
      dataInput=new BufferedReader(new FileReader(file));
      line=dataInput.readLine(); // this is the ID value line
      int next=Integer.parseInt(line);
      if (next>0) {
	nextID=next;
	historyHash=new Hashtable(nextID);
	while ((line=dataInput.readLine())!=null && count<nextID) {
	  idnumber=Integer.parseInt(dataInput.readLine());  // read ID
	  line3=dataInput.readLine();  // read date
	  Long tmp=new Long(line3);
	  Date tmpdate=new Date(tmp.longValue());
	  addRecord(line,idnumber,tmpdate);
	  //System.out.println(count+1+">>"+line+" id:"+idnumber+" d:"+line3);
	  count++;
	}
      }
      //** else we have no history yet
      else {
	historyHash=new Hashtable();
	nextID=0;
      }
      
      dataInput.close();
      return true;
    } catch (IOException ex) { 
      System.out.println("=====Could not open history db DataInputStream:"+file.getName()); 
      return false; 
    }
  }

  /**
   * Saves history as a background process after each annotation;  i do this because I have so many crashes 
  public void saveHistory() {
    //System.out.println("***STARTING HISTORY THREAD...");
    Thread thread=new Thread(this);
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();
  }
  */

  /*
  public void run() {
    System.out.println("***RUNNING HISTORY THREAD...");
    writeHistoryDB();
  }
  */

  /**
   * Write the history DB file upon shutting down RH
   */
  public synchronized void writeHistoryDB() {
      //System.out.println("***Writing history database...");
    FileOutputStream fp;
    byte nl=(byte)'\n';
    byte[] newLine=new byte[1];
    newLine[0]=nl; 
    Date date=new Date();
    String newline=new String(newLine);
    StringBuffer buffer=null;
    try {
      File file=new File(historyFilename);
      if (file.exists()) {
	  //System.out.println("---Writing history file:"+historyFilename);
	fp=new FileOutputStream(historyFilename);
	buffer=new StringBuffer().append(nextID).append(newline);
	fp.write(buffer.toString().getBytes());
	Enumeration enum = historyHash.elements();
	while (enum.hasMoreElements()) {
	  RHHistoryItem item=(RHHistoryItem)enum.nextElement();
	  //*** Write three lines: url, id & date 
	  buffer= new StringBuffer().append(item.getURL()).append(newline).append(item.getID()).append(newline).
	    append(item.getDate()).append(newline);
	  //System.out.println("...>"+buffer.toString());
	  fp.write(buffer.toString().getBytes());
	}
	fp.close();
      }
      else {
	System.out.println("***ERROR: History file does not exist:"+historyFilename);
      }
    } catch (IOException ex) {
      System.out.println("***ERROR: could not write to history file:"+historyFilename);
    }
    //System.out.println("***DONE writing history file");
  }

  /**
   * Check the history hash to see if this url is a place we have annotated in the past; return index number if we have
   * seen this document before or a -1 if not.
   */
  public int checkHistory(String url) {
      RHHistoryItem item=(RHHistoryItem)historyHash.get(url);
      //if (item!=null) System.out.println("***History Results:"+item.getURL()+" key:"+item.getID());
      if (item!=null) return item.getID();
      else return -1;
  }

  /**
   * Add record when reading from history file and already know all information about history item
   */
  public void addRecord(String urlstr, int newid, Date newdate) {
    try {
      historyHash.put(urlstr,new RHHistoryItem(urlstr,newid,newdate));
    } catch (NullPointerException ex) {
      System.out.println("***Error: could not add new history record:"+urlstr);
    }
  }

  /**
   * Adds a new record to the history list; returns new key string if valid; "" otherwise
   */
  public String addRecord(String urlstr) {
    int key=-1;
    try {
      Date date=new Date();
      historyHash.put(urlstr,new RHHistoryItem(urlstr,key=getNextID(),date));
      return generateNewDocumentKey(key);
    } catch (NullPointerException ex) {
      System.out.println("***Error: could not add new history record:"+urlstr);
      return "";
    }
  }
 
  /**
   * updates the record if the document is annotated again
   */
  public void updateRecord(String urlstr) {
    RHHistoryItem item=(RHHistoryItem)historyHash.get(urlstr);
    if (item!=null) {
      //** Currently, i only update the date we reannotate the document but more can be done
      Date date=new Date();
      item.setDate(date);
    }
  }
  
  /**
   * Returns the next available ID plus it increments the next value 
   */
  public int getNextID() {
    return ++nextID;
  }


  /**
   * This method generates the unique id used as the directory name for the current annotated document;
   * I use a method here just in case i want to make the path more descriptive sometime in the future
   */
  public String generateNewDocumentKey(int key) {
    return new StringBuffer().append(RH_GlobalVars.RH_HistoryDirectoryName).append(key).toString();
  }


  public Hashtable getHash() {
    return historyHash;
  }

    public int getHistorySize() {
	return historyHash.size();
    }
}

