/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_ConceptGroup: class for holding a collection of concepts in a group, e.g. "work" or "play" or ???
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 03.09.98
 *
 */
package ricoh.rh;

public class RH_ConceptGroup {
    private int len;
    private String name="", toolTipString="";
    private String[] concepts;
    
    public RH_ConceptGroup(String title, String tip, int num) {
	name=title;
	toolTipString=tip;
	if (num>0) concepts=new String[num];
	len=0;
    }
    public String getName() {
	return name;
    }
    public void setName(String str) {
	name=str;
    }
    /*  public int getSize() {
	return len;
	}*/
    public String[] getConcepts() {
	return concepts;
    }
    public void setConcepts(String[] cons) {
	if (cons!=null) {
	    concepts=new String[cons.length];
	    System.arraycopy(cons,0,concepts,0,cons.length);
	}
	else concepts=new String[0];
	len=concepts.length;
    }
    public void addConcept(String str) {
	if (len<concepts.length) concepts[len++]=str;
    }
    public String getToolTipString() {
	return toolTipString;
    }
    public void setToolTipString (String tip) {
	toolTipString=tip;
    }
}
