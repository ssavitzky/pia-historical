//  DescriptionList.java:  InterForm (SGML)  descriptionlist
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Element;

import crc.ds.List;
import crc.ds.Table;
import crc.ds.Index;

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
  
  /** Retrieve an attribute by index. */
  public SGML attr(Index index) {
    SGML result = super.attr(index);
    if (result != null){
      return result;
    }
    String name=index.string();
    
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

 SGML attrExpression(Index expression)
  {
    //check for keywords keys,values
    SGML result = super.attrExpression(expression);
    // add any name keywords
    return result;
  }
  

  /************************************************************************
  ** Construction:
  ************************************************************************/
  // inherit everything
  public DescriptionList(Element e) {
    super(e);
  }
  
}
