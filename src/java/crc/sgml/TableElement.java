//  TableElement.java:  InterForm (SGML)  table
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Element;
import crc.sgml.Tokens;

import crc.ds.List;
import crc.ds.Table;
import crc.interform.Util;

import java.lang.Integer;  //primitives are not objects...causes us pain
import java.util.Enumeration;

/**
 * The representation of an SGML <em> table element</em>. 
 *  overrides the attr method of Element. Parses the content to construct
 * corresponding attr data structure.
 */

public class TableElement extends crc.sgml.Element {

/**  construct table data structure from content
     This is really ugly, partly because the table specification in
      html never intended to treat table objects as data structures.
 */

  private Table  rowHeaders=null;   
  private Table columnHeaders=null;
  private SGML[][] data=null;  // should be populated by <td> elements
  private int nRows=0;
  private int nCols=0;
  /**  populate the data structures given the current content
  */
  private void buildTable()
  {
    Table tags=content.tagLocations();
    List rowLocations = content.tagLocations("tr");
   
    nRows = rowLocations.nItems();
    nCols=0;
    for (int i = 0; i < nRows; ++i) {
      int j = ((Integer) rowLocations.at(i)).intValue();
      SGML row = content.itemAt(j);
      List headers=row.content().tagLocations("th");
      List columns=row.content().tagLocations("td");
//go  through the table twice, once to make sure we get the size right
// and again to populate it
      int c = 0; //start at column 0
      Enumeration e = headers.elements();
      while(e.hasMoreElements()){
	SGML datum=content.itemAt(((Integer) e.nextElement()).intValue());
      //populate headers
      //note,  assumes col headers found only in first row
      // everything else is header for a row, clearly not correct but
      // any other interpretation is ambiguous
	if(i==0){
          columnHeaders.at(datum.contentString(),new Integer(c));  //header for column c
	}else{
	  rowHeaders.at(datum.contentString(),new Integer(i));  //header for row i
	}
	
	if(datum.hasAttr("colspan")){
	  c = c + (int) datum.attr("colspan").numValue();
	}else{
	  c++;
	}
	  
      }
      e = columns.elements();
      while(e.hasMoreElements()){
	SGML datum=content.itemAt(((Integer) e.nextElement()).intValue());
	if(datum.hasAttr("colspan")){
	  c = c + (int) datum.attr("colspan").numValue();
	  
	}else{
	  c++;
	}
      }
      if(c>nCols){
	nCols = c;
      }
    }
    //create the datastructure
    data = new SGML[nRows][nCols];
    // now populate the structure    
    for (int i = 0; i < nRows; ++i) {
      int j = ((Integer) rowLocations.at(i)).intValue();
      SGML row = content.itemAt(j);
      List headers=row.content().tagLocations("th");
      List columns=row.content().tagLocations("td");
      int c = 0; //start at column 0
      
      while(!columns.isEmpty() || !headers.isEmpty()){
      // if  this column is full, skip to the next one
	while(data[i][c] != null){
	  c++;
          // real trouble if c exceeds boundary     
          if(c >= nCols){
	    nCols++;  //expand table
	    System.err.println("table boundary exceeded\n");
	    SGML old[][]=data;
	    data= new SGML[nRows][nCols];
            for(int ri=0;ri<nRows;ri++){
	     for(int ci=0;ci<nCols-1;ci++){
	       data[ri][ci]=old[ri][ci];
	     }
	    }
	  }
	}
	Integer item = (Integer)columns.shift();
        Integer itemh = (Integer)headers.shift();
	if(itemh != null){
	  if(itemh.intValue()<item.intValue()){
	    if(item != null) {columns.unshift(item);} 
	    item = itemh;
	  } else {
	    headers.unshift(itemh);
	  }
	}
	SGML datum=content.itemAt(item.intValue());
        int cspan;
	int rspan;
	
	cspan= (datum.hasAttr("colspan"))? (int) datum.attr("colspan").numValue() :1;
        rspan= (datum.hasAttr("rowspan"))? (int) datum.attr("rowspan").numValue() :1;
	for(int ri=i;ri<i+rspan;ri++){
	  for(int ci=c;ci<c+cspan;ci++){
	    data[ri][ci]=datum;
	  }
	}
      }
    }
  }
  
  /************************************************************************
  ** Access to attributes:
  ************************************************************************/
  
  /** Retrieve an attribute by name. 
       If no attribute is explicitly defined, treat name as an index
        into the table, either row, column, or "row,column" and return  
      TableElement or td Element as appropriate
   */
  

  public SGML attr(String name) {
    SGML result = (attrs == null)? null : (SGML)attrs.at(name.toLowerCase());
    if (result != null){
      return result;
    }
    // is name="row,column"?
    if (name.indexOf(',') != -1){
      return attrRxC(name);
    }
    // is name a row or column header?
    if(rowHeaders.has(name)){
     return attrRxC(name + ","); //return a column
    }
    if(columnHeaders.has(name)){
      return attrRxC("," + name);  //return a row
    }
    //is name a number?
    try {
      int index1=java.lang.Integer.valueOf(name).intValue();
      //treat as a row or let concept handle it
      return (index1 < nRows)? attrRxC(name + ",") : content.attr(name);
      
    } catch (Exception e) {
    //not a number
    }
    //all else fails
    return null;
  }
  

  /**  process row,col accesses e.g. 8,4 returns element from row 8 col 4
       8 or 8, returns row8, 4 returns col 4
       "r1-r2,c1-c2" is most general case
        returns a new table, or a td element for "r1,c1" accesses
       very ugly code
   */
  private SGML attrRxC(String name){
    List indexes = Util.split(name,',');
    String rows=(String)indexes.shift();
    int rowstart,rowstop;
    Integer tmpI;
    
    String st;

    //getstart and stop for rows
    List startstop=Util.split(rows,'-');
    if(startstop.isEmpty()){ // ",c"
       rowstart=0;
       rowstop=nRows;
    } else{                 // "r1-r2,c"
      st=(String) startstop.shift();
      tmpI=(Integer) rowHeaders.at(st);

      if(tmpI==null){    
       try {
        rowstart=java.lang.Integer.valueOf(st).intValue();
       } catch (Exception e) {
	 rowstart=0;       // "r1" not interpreted, default 0
       }
      } else {
        rowstart=tmpI.intValue();
      }
      
      if(startstop.isEmpty()){ //"r1,c"
	rowstop=rowstart + 1;
      } else{                  //"r1-r2,c"
	st=(String) startstop.shift();
        tmpI=(Integer) rowHeaders.at(st);
	
        if(tmpI==null){
        try {
          rowstop=java.lang.Integer.valueOf(st).intValue();
         } catch (Exception e) {
	   rowstop=nRows;  // "r1-,c"
	 }
	} else {
	  rowstop=tmpI.intValue();
	}
	
      }
    }
    //getstart and stop for  columns
    String columns= (String)indexes.shift();
    startstop=Util.split(columns,'-');
    int colstart,colstop;

    if(startstop.isEmpty()){  // "r,"
      colstart=0;
      colstop=nCols;
    } else{
      st=(String)startstop.shift();
      tmpI=(Integer) columnHeaders.at(st);
      
      if(tmpI==null){
       try {
        colstart=java.lang.Integer.valueOf(st).intValue();
       } catch (Exception e) {
	 colstart=0;  // not interpret c1,  default to 0
       } 
      } else {
	colstart=tmpI.intValue();
      }
      
      if(startstop.isEmpty()){ //"r,c1"
	colstop=colstart + 1;
      } else{                  //"r,c1-c2"
	st=(String)startstop.shift();
	tmpI=(Integer) columnHeaders.at(st);
        if(tmpI==null){
        try {
          colstop=java.lang.Integer.valueOf(st).intValue();
         } catch (Exception e) {
	   colstop=nCols;
	 }
	} else {
	  colstop=tmpI.intValue();
	}
	
      }
    }
  // if only one element, return it
    if(rowstop - rowstart == 1  && colstop - colstart == 1){
      return  data[rowstart][colstart];
    }
    //else construct new table
    TableElement result=new TableElement(this.tag());
    // push relevant items into table-- inefficient,but works
    for(int r=rowstart;r<rowstop;r++){
      Element tr = new Element("tr");
      for(int c=colstart;c<colstop;c++){
	tr.append(data[r][c]);
      }
      result.append(tr);
    }
    return result;
  }
  


  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    SGML result = attr(name);
    return (result == null)? null : result.toString();
    
  }

  /** Test whether an attribute exists. -- should this check for implicit attr? */
  public boolean hasAttr(String name) {
    return (attr(name) == null)? false : true;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  // inherit everything
  public TableElement(String tag) {
    super(tag);
  }
  public TableElement(Element e) {
    super(e);
  }

}
