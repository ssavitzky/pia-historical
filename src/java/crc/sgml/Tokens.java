////// Tokens.java:  List of InterForm Tokens
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.ds.List;
import crc.ds.Table;
import crc.ds.Index;
import crc.ds.SortTree;

import crc.sgml.SGML;
import crc.sgml.Element;

import java.util.Enumeration;
import java.lang.Integer;


/**
 * A List (sequence) of SGML Token's.  
 *	Unlike a simple List, Strings and Lists are merged when appended.
 */
public class Tokens extends List implements SGML {

  
  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  public String itemSeparator;
  
  /************************************************************************
  ** Object operations:
  ************************************************************************/

  public String toString() {
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < nItems(); ++i) {
      if (itemSeparator != null && i != 0) s.append(itemSeparator);
      s.append(at(i).toString());
    }
    return s.toString();
  }

  /************************************************************************
  ** SGML list interface:
  ************************************************************************/

  public SGML itemAt(int i) {
    return (SGML)at(i);
  }

  public SGML itemAt(int i, SGML v) {
    at(i, v);
    return this;
  }

  /************************************************************************
  ** SGML interface:
  ************************************************************************/

  /** Return true if the object is an individual SGML token. */
  public boolean isToken() {
    return false;
  }

  /** Return true if the object is an individual SGML element. */
  public boolean isElement() {
    return false;
  }

  /** Parser state:  0 for a complete element. */
  public byte incomplete() {
    return 0;
  }

  /** Set parser state.  Ignored for all but Token. */
  public void incomplete(byte i) {
  }

  /** Return true for a list of tokens. */
  public boolean isList() {
    return true;
  }

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() {
    return nItems() == 0;
  }

  /** Return true if the SGML is pure text, or a 
   * 	singleton list containing a Text. */
  public boolean isText() {
    return nItems() == 1 && itemAt(0).isText();
  }

  /** Return true if the object implements the Attrs interface */
  public boolean isAttrs() { return false; }

  /** A string ``tag'' that is guaranteed to be null if isList(),
   *	and "" if isText(). */
  public String tag() {
    return null;
  }

  /** Convert the entire object to text. */
  public Text toText() {
    if (isText()) {
      return itemAt(0).toText();
    }
    return new Text(toString());
  }

  /** Convert to a number (double, being the most general form available). */
  public double numValue() {
    return contentText().numValue();
  }

  /** Convert to a single token if it's a singleton. */
  public SGML simplify() {
    return (nItems() == 1) ? itemAt(0) : this;
  }

  /** The object's content.  This is the same as this if isList(); 
   *	it is null if isEmpty(). */
  public Tokens content() {
    return this;
  }

  /** The object's content converted to a string. */
  public String contentString() {
    return toString();
  }

  /** The result of appending some SGML tokens.  Same as this if isList(). */
  public SGML append(SGML sgml) {
    if (sgml.isList()) {
      sgml.appendContentTo(this);
    } else if (sgml.isText() && nItems() > 0 && itemAt(nItems()-1).isText()) {
      itemAt(nItems()-1).appendText(sgml.toText());
    } else {
      push(sgml);
    }
    return this;
  }

  /** The result of appending a single item.  No merging is done. */
  public SGML addItem(SGML sgml) {
    push(sgml);
    return this;
  }

  /** The result of appending some text.  */
  public SGML appendText(Text t) {
    return append(t);
  }

  /** The result of appending a string. */
  public SGML append(String s) {
    return append(new Text(s));
  }

  /** Append this as text. */
  public void appendTextTo(SGML t) {
    for (int i = 0; i < nItems(); ++i) {
      itemAt(i).appendTextTo(t);
    }
  }

  /** Append contents to a Tokens list. */
  public void appendContentTo(Tokens list) {
    for (int i = 0; i < nItems(); ++i) {
      list.append(itemAt(i));
    }
  }

/**  retrieve attribute by name-- list don't have  name attributes
 */
  public SGML attr(String name) {
    return null;
  }
  
  /** Retrieve an attribute by index.  
      but specifying number or range gets that item or list of items
      useful for get in interforms */
  public SGML attr(Index name) {
    if(name.isRange()){
      int[] indices=name.range(nItems());
      if(indices.length == 2){
        //start-stop
	return copy(indices[0],indices[1]);
      } else{
	//a-b-c
	return copy(indices);
      }
    }
    if(name.isNumeric()){
      return itemAt(name.numeric());
    }
    //otherwise null
    return null;
  }
  
  /**  set an attribute by name.  Lists don't have any names,so interpret
        name as number to insert at.*/
  public  void attr(String name, SGML value) {
    int start;
    
     try {
       start=java.lang.Integer.valueOf(name).intValue();
      } catch (Exception e) {
       start =0;
      }
      itemAt(start,value);
      
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    return null;
  }

  /** Test whether an attribute exists.  It doesn't unless name is a number. */
  public boolean hasAttr(String name) {
    return false;
    
  }


  /************************************************************************
  ** Access to parts of content:
  ************************************************************************/

  /** Return only the text portions of the content */
  public Text contentText() {
    Text t = new Text();
    for (int i = 0; i < nItems(); ++i) {
      t.append(itemAt(i).contentText());
    }
    return t;
  }

  /** Return only the content inside of markup (including text content). */
  public Tokens contentMarkup() {
    Tokens t = new Tokens();
    for (int i = 0; i < nItems(); ++i) {
      if (! itemAt(i).isText()) t.append(itemAt(i));
    }
    return t;
  }

  /** Return only the text inside the given tag */
  public Text linkText(String tag) {
    Text t = new Text();
    for (int i = 0; i < nItems(); ++i) {
      if (itemAt(i).tag().equals(tag)) t.append(itemAt(i).contentText());
    }
    return t;
  }

  /** Return the content with leading and trailing whitespace removed. */
  public Tokens contentTrim() {
    Tokens t = new Tokens();
    for (int i = 0; i < nItems(); ++i) {
      if (i == 0 && itemAt(i).isText()) {
	// === We really ought to treat first and last differently.
	String s = itemAt(i).toString().trim();
	if (s != "") t.append(new Text(s));
      } else if (i == nItems() && itemAt(i).isText()) {
	// === We really ought to treat first and last differently.
	String s = itemAt(i).toString().trim();
	if (s != "") t.append(new Text(s));
      } else {
	t.append(itemAt(i));
      }
    }
    return t;
  }

  /** return a table with tag names as keys and locations as values
   */
  public Table tagTable() {
    Table result = new Table();
    Enumeration elements = elements();
    List locations ;
    for (int i = 0; i < nItems(); ++i) {
      String name =  itemAt(i).tag();
      if(result.has(name)){
	locations = (List) result.at(name);
      } else {
	locations = new List();
        result.at(name,locations);
	}
      locations.push(new Integer(i));
    }
    return result;
  }
  
  /**  return a list of the locations of a given tag type
   */
  public List tagLocations(String name) {
    List result = (List) tagTable().at(name);
    if(result == null){
      result = new List();
    }
    return result;
  }
  
  

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Tokens() {
    super();
  }

  public Tokens(SGML s) {
    this();
    this.append(s);
  }

  public Tokens(Tokens s) {
    this();
    copyContentFrom(s);
    itemSeparator = s.itemSeparator;
  }

  public Tokens(java.util.Enumeration e) {
    this();
    while (e.hasMoreElements()) {
      push(Util.toSGML(e.nextElement()));
    }
  }

  public Tokens(java.util.Enumeration e, String sep) {
    this();
    itemSeparator = sep;
    while (e.hasMoreElements()) {
      push(Util.toSGML(e.nextElement()));
    }
  }

  public Tokens(String sep) {
    this();
    itemSeparator = sep;
  }

  public Tokens(Object[] sa) {
    this();
    for (int i = 0; i < sa.length; ++i) push(Util.toSGML(sa[i]));
  }

  public Tokens(String[] sa) {
    this();
    for (int i = 0; i < sa.length; ++i) push(new Text(sa[i]));
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Copy a token's content. */
  void copyContentFrom(Tokens it) {
    for (int i = 0; i < it.nItems(); ++i) 
      addItem(it.itemAt(i));
  }

  /** copy specified items into a new tokens */
   public Tokens copy(int[] indices) {
     Tokens result = new Tokens();
     
    for (int i = 0; i <  indices.length; ++i) 
      result.addItem(itemAt(indices[i]));

   return result;
 }

  /** copy specified items into a new tokens */
   public Tokens copy(int  start, int stop) {
     Tokens result = new Tokens();
     
    for (int i =  start; i <   stop; ++i) 
      result.addItem(itemAt(i));
   return result;
 }


  /** Return a new Tokens object with the same content.  Shallow copying 
   *	is used.  */
  public Object clone() {
    return new Tokens(this);
  }

  /** Convert the argument to a list if it isn't one already. */
  public static Tokens valueOf(SGML t) {
    if (t == null) return null;
    else if (t.isList()) return t.content();
    else return new Tokens(t);
  }

  /************************************************************************
  ** Sorting:
  ************************************************************************/

  /** Return a new Tokens containing the contents sorted in ascending 
   *	lexicographic order. */
  public Tokens sortAscending() {
    SortTree sorter = new SortTree();
    Tokens results = new Tokens();
    results.itemSeparator = itemSeparator;
    sorter.append(this.elements());
    sorter.ascendingValues(results);
    return results;
  }

  /** Return a new Tokens containing the contents sorted in descending 
   *	lexicographic order. */
  public Tokens sortDescending() {
    SortTree sorter = new SortTree();
    Tokens results = new Tokens();
    results.itemSeparator = itemSeparator;
    sorter.append(this.elements());
    sorter.descendingValues(results);
    return results;
  }

  /** Return a new Tokens containing the contents sorted in ascending 
   *	numerical order. */
  public Tokens sortAscendingNumeric() {
    SortTree sorter = new SortTree();
    Tokens results = new Tokens();
    results.itemSeparator = itemSeparator;
    sorter.appendNumeric(this.elements());
    sorter.ascendingValues(results);
    return results;
  }

  /** Return a new Tokens containing the contents sorted in descending 
   *	numerical order. */
  public Tokens sortDescendingNumeric() {
    SortTree sorter = new SortTree();
    Tokens results = new Tokens();
    results.itemSeparator = itemSeparator;
    sorter.appendNumeric(this.elements());
    sorter.descendingValues(results);
    return results;
  }

  /** Return a new Tokens containing the contents sorted as specified
   *	by the two boolean parameters <code>reverse</code> and
   *	<code>numeric</code>
   */
  public Tokens sort(boolean reverse, boolean numeric) {
    if (reverse) {
      return numeric? sortDescendingNumeric() : sortDescending(); 
    } else {
      return numeric? sortAscendingNumeric() : sortAscending(); 
    }
  }

 }
