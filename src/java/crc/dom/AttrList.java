// AttrList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

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


