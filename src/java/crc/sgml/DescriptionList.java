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
    SGML myElement;
    
    SGML myNext = null;
    int results = 0;
    Tokens myresult = new Tokens();
    
    while(myNext != null || elements.hasMoreElements()){
      myElement = (myNext == null) ? (SGML) elements.nextElement() : myNext;
       
       // clear the look ahead variable
       myNext=null;
       
   // tag equals dt and content equals name -- what operator appropriate?
      if(myElement.tag().equals("dt") && myElement.contentString().trim().equals(name))
        while(elements.hasMoreElements()) {
	 myNext = (SGML) elements.nextElement(); 
	 if(myNext.tag().equals("dt")) break;
	 if(myNext.tag().equals("dd")){
	   myresult.append( (SGML)myNext.content());
	   results++;
	 }
	}
    }

    if(results==1) result = Util.removeSpaces(myresult).itemAt(0);
    if(results>1) result = myresult;
    
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
