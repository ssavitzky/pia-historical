// AbstractNamedList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import w3c.dom.AttributeList;
import w3c.dom.NoSuchNodeException;
import java.util.Hashtable;
import java.util.Vector;

public abstract class AbstractNamedList {

  public Object getItem(String name){return null;}
  public Object setItem(String name, Object attr){return null;}
  
  public Object removeItem(String name) 
       throws NoSuchNodeException{return null;}
       
  public Object itemAt(long index)
       throws NoSuchNodeException{return null;}
       
  public long getItemListLength(){return 0;}

  protected Vector itemList;
  protected Hashtable namedItemSpace;
}

