// AttrModels.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * This object stores a collection of attribute definitions
 */

package crc.dom;

import java.io.*;
import java.util.TreeMap;





public class AttrModels extends AbstractNamedList{

  public AttrModels();

  public AttrNodeDef getAttribute(String name);
  public AttrNodeDef setAttribute(String name, AttrNodeDef attr);
  
  public AttrNodeDef remove(String name) 
       throws NoSuchNodeException;
       
  public AttrNodeDef item(long index)
       throws NoSuchNodeException;
       
}




