// AppNamedNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import w3c.dom.NamedNodeList;
import w3c.dom.Node;
import w3c.dom.NoSuchNodeException;
import w3c.dom.NodeEnumerator;

public class AppNamedNodeList extends AbstractNamedList implements NamedNodeList {

  public AppNamedNodeList(){
    itemList = null;
    namedItemSpace = null;
  }
  public AppNamedNodeList(NamedNodeList l){
    itemList = null;
    namedItemSpace = null;
  }


  // Core get and set public interface. Note that implementations may
  // build the list lazily

  public Node getNode(String name){return null;}
  public Node setNode(String name, Node node){return null;}
 
  public Node remove(String  name)
       throws NoSuchNodeException{return null;}

  public Node item(long index)
       throws NoSuchNodeException{return null;}

  public long getLength(){return 0;}

  public NodeEnumerator getEnumerator(){return null;}
}
