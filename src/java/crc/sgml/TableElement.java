//  TableElement.java:  InterForm (SGML)  table
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Element;
import crc.sgml.Tokens;

import crc.ds.List;
import crc.ds.Table;
import crc.interform.Util;
import crc.ds.Index;

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
    Table tags=content.tagTable();
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
  
  /** Retrieve an attribute by index. 
       If no attribute is explicitly defined, treat index
       row.col or r1-r2.c1-c2
       return TableElement or td Element as appropriate
   */

  public SGML attr(Index name) {
    // is name ="-attr-val"?
    if(name.isExpression()){
      return attrExpression(name);
    }
    int rows[],cols[];
    
    if(name.isRange()){
      //set rows
      //currently non-numeric ranges not supported
      rows=name.range(nRows);
    } else{
        // assume single row specified
        rows=new int[1];

      //numeric?
      if(name.isNumeric()){
        rows[0]=name.numeric();
      }else{
	String s=name.string();
        Integer r=(Integer)rowHeaders.at(s);
        if(r == null){
	  return null; //unspecified row
         //TO specify all rows, use table.0-.foo
	}
	rows[0] =r.intValue();
      }
    }
    //now  get columns
    String c=name.next();
    if(c == null){
     // no columns specified, just return tokens
      if(rows.length == 1){
	return content.itemAt(rows[0]);
      } else if(rows.length == 2){
	return content.copy(rows[0],rows[1]);
      } else{
	return content.copy(rows);
      }
    }
    
    //name index should be advanced by call above to next
    if(name.isExpression()){
      return attrExpression(rows,name);
    }
    
    if(name.isRange()){
      cols=name.range(nCols);
    }else{
      //assume singleton
      cols= new int[1];
      if(name.isNumeric()){
	cols[0]=name.numeric();
      } else{
	String s=name.string();
        Integer r=(Integer)columnHeaders.at(s);
        if(r == null){
	  return null; //unspecified column
         //TO specify all columns, use table.row.0-
	}
	cols[0] =r.intValue();
      }
    }
    
      //now should have rows[],cols[]      
      if(rows.length == 1 && cols.length == 1){
	// return element
	return data[rows[0]][cols[0]];
      }
      return  copy(rows,cols);
  }
  
    
  
/**  retrieve items specified by expression
 */

 SGML attrExpression(Index expression)
  {
    SGML result = super.attrExpression(expression);
    // add any name keywords
    return result;
  }


/**  retrieve items specified by expression in context of rows
 */
private SGML attrExpression(int[] rows, Index expression)
  {
    return attrExpression(expression);
    
    // check for keywords   rows, cols, etc
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

  /** clone all or part of this table */
public TableElement copy(int[] rows, int[] cols)
  {
    int rowstart,rowstop,colstart,colstop;
    // currently only works for lengths of 1 or 2
    if(rows.length < 1 || cols.length < 1) {
      return null;
    }
    rowstart=rows[0];
    rowstop=(rows.length > 1)?rows[1]:rowstart;
    colstart=cols[0];
    colstop=(cols.length > 1)?cols[1]:colstart;
    TableElement result=new TableElement(this.tag());
    // push relevant items into table-- inefficient,but works
    // eventually copy items into data
    for(int r=rowstart;r<rowstop;r++){
      Element tr = new Element("tr");
      for(int c=colstart;c<colstop;c++){
	tr.append(data[r][c]);
      }
      result.append(tr);
    }
    return result;
    
  }


    

}
