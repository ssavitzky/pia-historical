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

import java.util.Enumeration;
import java.util.Hashtable;


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

  protected TableOfSGML data = null;

  /**  populate the data structures given the current content
   * functions which can modify the contents should be overridden
   * to clear the data... currently once the table is built it does not change
   * ( although the cells contents may change)
  */
  protected void buildTable()
  {
    data = new TableOfSGML();
    List rowLocations = content.tagLocations("tr");
    int row=0;
    while(!rowLocations.isEmpty()){
      int j = ((Integer) rowLocations.shift()).intValue();
      SGML rowToken = content.itemAt(j);
       Enumeration e = rowToken.content().elements();
       int col=0;
       while(e.hasMoreElements()){
	 // find an empty column
	 while(data.getRowColumn(row,col) != null) col++;
	 SGML  token = (SGML)e.nextElement();
	 if(token.tag().equalsIgnoreCase("td") ||
	    token.tag().equalsIgnoreCase("th")){
	   int cspan = col + Util.getInt( token, "colspan", 1);
	   int rspan = Util.getInt( token, "colspan", 1);
	   // set all of the rows, columns that this item spans
	   for(int r = row; r < row + rspan; r++)
	     for(; col < cspan; col++)
	       data.setRowColumn( row,col, token);
	 }
       }
       row++;
    }
  }
 
 
  /************************************************************
  ** access functions
  ************************************************************/
  /**
   * return the <TD> token associated with this cell
   */

  public SGML getRowColumn(int row, int col){
    // always build the table because we never know when contents might change
    if(data == null) buildTable();
    return data.getRowColumn(row,col);
  }


  public SGML getTable(int rowstart,int rowend,int columnstart,int columnend){
     if(data == null) buildTable();
    if(rowend<0 || rowend > data.rows)rowend=data.rows;
    
    if(columnend<0 || columnend>data.cols)columnend=data.cols;
    int a[]={rowstart, rowend};
    int b[]={columnstart,columnend    };
     System.out.println(" getting table " + a[0] +","+ a[1] + " " + b[0] + ","+ b[1]);
     
    return copy(a,b);
  }

  /************************************************************************
  ** Access to attributes:
  ************************************************************************/
  
  /** Retrieve an attribute by index. 

  *** this method is not overridden
  *** the caller should find the row, column attributes and use those to
  *** call getRowColumn above
   */

  public SGML attr(Index name) {

    return super.attr(name);
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
    if(data == null) buildTable();
    
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

    // should check for span attributes and correct as needed
    // for now assumes that one version of each TD will be sufficient to
    // cover the appropriate cells
    Hashtable  cells=new Hashtable();
    for(int r=rowstart;r<=rowstop;r++){
      Element tr = new Element("tr");
      for(int c=colstart;c<=colstop;c++){
	SGML token =  data.getRowColumn(r,c);
	// null cell become empty cells
	if( token == null) token= new Element("td");
	if( ! cells.containsKey( token)){
	  tr.append(token);
	  // keep track of the original row a token is put in
	  cells.put( token, tr);
	}
      }
      result.append(tr);
    }
    return result;
  }
}

/************************************************************
** simple data structure for holding tables
************************************************************/

/**
 * simple structure for holding tables
 * maintains a pointer list indexing the cells associated with SGML object
 */

 class TableOfSGML extends Hashtable{

  /**
   * number of rows and columns -- actually maxim of each that has been set
   */
    public int rows=0,cols=0;

  /**
   * rows.get(i).get(j) returns element at row=i col=j
   * which should be a "TD" token
   * i & j are mapped to integer objects as used as keys in hash table
   */

  public SGML getRowColumn(int i, int j){
     return getRowColumn(new Integer(i), new Integer(j));
  }

  /**
   * set the value at i,j.   value will normally be a TD or TH  token
   */

  public void  setRowColumn(int i, int j, SGML value){
      setRowColumn(new Integer(i), new Integer(j), value);
  }

  public SGML getRowColumn(Integer i, Integer j){
    Hashtable h = (Hashtable)  get(i);
    if(h == null) return null;
    SGML result = (SGML) h.get(j);
     return result;
  }

  public  void setRowColumn(Integer i, Integer j, SGML value){
    if(i.intValue() >  rows) rows = i.intValue();
    if(j.intValue() >  cols) cols = j.intValue();
    Hashtable h = (Hashtable)  get(i);
    if(h == null) {
       h = new Hashtable();
       put( i, h);
    }
    h.put(j, value);
  }

  /**
   * header stuff could go here
   */



  /************************************************************
  ** constructor
  ************************************************************/

  public TableOfSGML() {
    super();
  }
  
}
