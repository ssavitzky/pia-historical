// AttrList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import w3c.dom.Attribute;
import w3c.dom.AttributeList;
import w3c.dom.Node;
import w3c.dom.NoSuchNodeException;
import w3c.dom.NodeEnumerator;

public class AttrList extends AbstractNamedList implements AttributeList {

  public AttrList(){
    itemList = null;
    namedItemSpace = null;
  }
  public AttrList(AttrList l){
    itemList = null;
    namedItemSpace = null;
  }

  public Attribute getAttribute(String name){ return null; }
  public Attribute setAttribute(String name, Attribute attr){ return null; }
  
  public Attribute remove(String name) 
       throws NoSuchNodeException{ return null; }
       
  public Node item(long index)
       throws NoSuchNodeException{ return null; }
       
  public long getLength(){ return 0; }
}


