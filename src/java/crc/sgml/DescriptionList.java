//  DescriptionList.java:  InterForm (SGML)  descriptionlist
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Element;

import crc.ds.List;
import crc.ds.Table;

import java.util.Enumeration;

/**
 * The representation of an SGML <em> description list element</em>. 
 *  overrides the attr method of Element. Parses the content to construct
 * corresponding attr data structure.
 */

public class  DescriptionList extends crc.sgml.Element {

  /************************************************************************
  ** Access to attributes:
  ************************************************************************/
  
  /** Retrieve an attribute by name. */
  public SGML attr(String name) {
    SGML result = super.attr(name);
    if (result != null){
      return result;
    }
    // check for dt tag with name
    Enumeration elements = content.elements();
    while(elements.hasMoreElements()){
      SGML mytag = (SGML) elements.nextElement();
   // tag equals dt and content equals name -- what operator appropriate?
      if(mytag.tag().equals("dt") && mytag.contentString().equals(name)){
	result = (SGML) elements.nextElement(); //could just return
      }
    }
    
    return result;
  }
  

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    SGML result = attr(name);
    return (result == null)? null : result.toString();
    
  }

  /** Test whether an attribute exists. */
  public boolean hasAttr(String name) {
    return (attr(name) == null)? false : true;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  // inherit everything
  public DescriptionList(Element e) {
    super(e);
  }
  
}
