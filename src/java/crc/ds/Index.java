// Index.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

import crc.ds.List; 
import crc.ds.Table;
import crc.sgml.SGML;
import crc.sgml.Util;
import crc.sgml.AttrWrap;
import crc.sgml.Text;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Element;

import crc.sgml.DescriptionList;

import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;

import crc.pia.Pia;

/** An index object is used for finding SGML objects.
 *  A path, consisting of, tag name, start, and end positions,
 *  is used to find the searched objects. Given a SGML object
 *  as a starting location for the search, this class implements a 
 *  recursive lookup by delegating to each SGML object (in the path) in 
 *  succession.
 *<strong>Syntax for the path is the following:</strong>
 *<pre>
 *SGML.xxx[[-]start#[-end#]]
 *	xxx	
 *		tag name		means tag name
 *		null			any tag
 *		Text			means text only
 *
 *	-	
 *	[start#-end#]
 *		start#			
 *			number		start at indicated index
 *			null		start at the first index
 *		end#
 *			number		end at indicated index (inclusive)
 *			null		end at end
 *
 *For "dl" SGML object,
 * To retrieve all of dts'text, xxx should be "keys"
 * To retrieve all of dds'text, xxx should be "values"
 *</pre>
 *<strong>Examples:</strong>
 *<pre>
 *		xxx == xxx- == xxx-- == xxx-1-	means all tags whose name are xxx
 *		xxx-start#-end#			means a range of element
 *		xxx-start#			means an element at start#
 *		xxx--end#                       means first element to end
 *		xxx-start#-			means a range of elements from the start index to the end
 *
 *		-				means all tags of any kind
 *		xxx..				means a token with the current SGML contents if current token
 *                                              isinstance of Tokens.  Otherwise, current token
 *					       
 *Examples for dl:
 *            foo
 *             |
 *             dl
 *        dt   dd   dt     dd
 *      "aaa" "car" "bbb"  "train"
 *  
 * foo.dl.aaa                         returns "car"
 * foo.dl.keys                        returns "aaabbb"
 * foo.dl.values                      returns "cartrain" 
 *</pre>
 */
public class Index {



 /**
  * The delimiter between tag start end
  */
  public static final String SEPARATOR="-";


 /**
  * Attribute index - constant representing tag-name for getting any tag
  */
  public static final String ANY="empty";

  /**
  * Attribute index - constant representing tag-name for getting text
  */
  public static final String TEXT="text";

 /**
  * Attribute index - constant representing tag-name for getting all dts' content
  */
  public static final String KEYS="keys";

  /**
  * Attribute index - constant representing tag-name for getting all dds' content
  */
  public static final String VALS="values";

  /**
  * Attribute index - constant for not numeric or illegal index position
  */
  static final int INVALIDNUMBER = -100;

  /**
  * Attribute index - default index position
  */
  static final int FIRST=1;

  /**
  * Attribute index - indicating the last element wanted
  */
  public static final int LAST=-1;

  /**
  * Attribute index - end of path
  */
  static final int EOFINPUT=-1;

  /**
  * Attribute index - state indicating currently processing "." or ".."
  */
  static final int DOTS=-2;

  /**
  * Attribute index - state indicating currently processing searching range of tags
  */
  static final int NORMAL=0;

  /**
  * Attribute index - index of path
  */
  int currentItem = -1;

  /**
   * original path
   */
  String path;
  
  /**
   * tag name, could be null, tag-name or Text
   */
  protected String tag = ANY;
  
  /**
   * start index
   */
  protected int start = INVALIDNUMBER;
  
  /**
   * end index
   */
  protected int end = LAST;

  /**
   * true if starting position is not given
   */
  protected boolean defaultStart = false;

  /**
   * state indicating either ".", "..", or range
   */
  protected int processState = NORMAL;

  /**
   * storing path split using "."
   */
  protected  List  items;

  /**
   * storing one element of a path i.e. ul-1-5
   */
  protected List positionParam;

 /**
  * the tag-name indicating the wanted SGML objects 
  */
  public String getTag(){
    return tag;
  }

  /** 
   * the position indicating first SGML object to retrieve 
   */
  public int getStart(){
    return start;
  }

  /** 
   * return the position indicating last SGML object to retrieve 
   */
  public int getEnd(){
    return end;
  }

  private void setTag(String t){
    tag = t;
  }

  private void setStart(int s){
   start = s;
  }

  private void setEnd(int e){
    end = e;
  }
  
  private boolean isDots(){
    return processState == DOTS;
  }

  /**
   * returns the first string
   * usually the caller uses this to do the first level table lookup
   */
 public String shift()
  {
    return (String)items.shift();
  }

 public void unshift(String s)
  {
    items.unshift(s);
    
  }
  

  /************************************************************************
  ** Construction and copying:
  ************************************************************************/
  
  public Index( String path) {
    if( path.indexOf(".") == -1 && path.length() != 0 ){
      items = new List();
      items.push( path );
    }else{
      items = Util.split(path,'.');
      if( path.endsWith(".") && !path.endsWith("..") ){
	items.push("");
	Pia.debug(this, "Index-->pushing a blank, end in dot");
      }
    }
    this.path = path;
  }

  /************************************************************************
  /**  operations  to provide index positioning and determine tag, start and end */
  /************************************************************************/

  /** The number of elements in path */
  public int size() {
    return items == null? 0 : items.size();
  }

  /** Getting the next element in the path and split it base on "-" */
  private int nextPositions() {
    positionParam = new List();

    currentItem += 1;

    if( currentItem >= size() ){
      resetPositionAttr();
      return EOFINPUT;
    }

    String item = (String)items.at( currentItem );
    if( item.equals("") ){
	processState = DOTS;
	return DOTS;
    }

    StringTokenizer ts = new StringTokenizer(item, SEPARATOR, true);
    try{
      while( ts.hasMoreElements() ){
	String s = ts.nextToken() ;
//	System.out.println("pushing "+s);
	positionParam.push(s );
      }
      
    }catch(Exception e){}

    processState = NORMAL;

    return 1;
  }

  /** next current path element's token i.e. tag, start or end position */
  private String nextToken(){
    Object o = positionParam.shift();
    if( o == null ) return null;
    return (String) o;
  }

  /**  push back current path element's token onto positionParam list */
  private void pushBackToken( String s ){
    positionParam.unshift( s );
  }

  private boolean isDash(String s){
    if( s == null ) return false;
    return ( s.length() == 1 && s.indexOf(SEPARATOR) != -1 ) ? true : false;
  }

  /** reset state tag and start and end positions */
  private void resetPositionAttr(){
    processState = NORMAL;
    tag = ANY;
    setStart( INVALIDNUMBER );
    setEnd( LAST );
    defaultStart = false;
  }

  /** echoing current path's element list */ 
  private void dumpPositionParam(){
    String s;
    Object o;

    List tmp = (List)positionParam.clone();
    while( !tmp.isEmpty() ){
      o = tmp.shift();
      if( o != null )
	Pia.debug(this, "dumpPositionParam-->[" + (String) o + "]");
    }
  }
  
  private boolean isTextOrDigit(String s){
    boolean result = true;

    if( s == null ) return false;
    int len = s.length();

    if( !Character.isLetter(s.charAt(0) ) )
      return false;
      
    for(int i=1; i < len; i++){
      if( !Character.isLetterOrDigit( s.charAt(i) ) ){
	result = false;
	break;
      }
    }

    return result;
  }

  /* return INVALIDNUMBER if not a number */
  private int parseNumber(String s){
    if( s == null ) return INVALIDNUMBER;

    try{
      int startPos = Integer.parseInt( s );
      return startPos;
    }catch(NumberFormatException e){
      return INVALIDNUMBER;
    }
  }

  /** parse tag */
  private void parseTag(){
    //Pia.debug( "Entering parseTag...");

    String t = nextToken();
    //Pia.debug("Token-->"+t);

    // missing tag and dash, tag is ANY.  current token is number
    if( parseNumber( t ) != INVALIDNUMBER ){
      tag = ANY;
      pushBackToken( t );
      return;
    }
      
    // tag is not specified could be a dash
   // if( !isTextOrDigit(t) ){
    if(t.equals(SEPARATOR)){
      tag = ANY;
      pushBackToken( t );
    }else{
      tag = t;
    }

    //Pia.debug("Exiting parseTag...");
  }

  /** get start position */
  private void parseStart() throws InvalidIndex{
    int number;
    //Pia.debug("Entering parseStart...");

    //dumpPositionParam();
    // getting dash
    String t = nextToken();
    //Pia.debug("Starting token-->"+t);
    // no positions are specified
    if( t == null ){
      setStart( FIRST );
      defaultStart = true;
      return;
    }

    if( (number = parseNumber( t )) != INVALIDNUMBER  ){
      setStart( number );
      return;
    }

    if( isDash( t ) ){
      // get start position
      t = nextToken();
      //Pia.debug("Token-->"+t);
      
      // this should be number or empty
      if ( (number = parseNumber( t )) != INVALIDNUMBER ){
	setStart( number );
	return;
      }
      if ( t == null || isDash( t ) ){
	// empty case; no start and end are given
	// just a dash
	setStart( FIRST );
	defaultStart = true;
	if ( isDash(t) ) pushBackToken( t );
      }else if ( number == INVALIDNUMBER ){
	// bad input
	throw new InvalidIndex("Invalid starting index.");
      }
    } else if ( number == INVALIDNUMBER ){
      throw new InvalidIndex("Invalid starting index.");
    }

    //Pia.debug(this, "Exiting parseStart...");
  }

  /** get end position */
  private void parseEnd() throws InvalidIndex{
    int number;

    //dumpPositionParam();

    // getting dash
    String t = nextToken();
    // just one item only
    if( t == null ){
      setEnd( ( defaultStart ) ? LAST : getStart() );
    }
    else 
      if( !isDash(t) )
	throw new InvalidIndex("Missing dash before end.");
      else{
	t = nextToken();
	
	if( t == null )
	  setEnd( LAST );
	else  if( (number = parseNumber( t )) != INVALIDNUMBER )
	  setEnd( number );
	else throw new InvalidIndex("Invalid ending index.");
      }
  }

  /** get tag, start, and end position and check range */
  private void parsePositions() throws InvalidIndex{
    int s, e;

    parseTag();
//    System.out.println(getTag());
    

    try{
      parseStart();
      parseEnd();

      Pia.debug("The tag-->"+ getTag());
      Pia.debug("The start-->"+ Integer.toString(getStart()));
      Pia.debug("The end-->"+ Integer.toString(getEnd()));

      s = getStart();
      e = getEnd();
      if( s < 1 )
	throw new InvalidIndex("Start index must start at 1 or larger.");
      if( s > e && e != LAST )
	throw new InvalidIndex("Start index is larger than end index."); 

    }catch(InvalidIndex ee){
      //Pia.debug("The error is-->"+ee.toString());
      throw ee;
    }
  }

  private SGML putAt( SGML cs, int where ){
    Tokens conts = cs.content();
    SGML e = cs;
    SGML target;
    int len = cs.content().nItems();

    for(int i = 0; i < len; i++){
      e = conts.itemAt( i );
      target = e.content().itemAt( where-1 );
      if( target == null )
	return e;
    }
    return e;
  }

  private SGML insertAt(int where, SGML item, SGML ts){
      Vector l = new Vector();
      SGML t, saveItem;
      int len;
      Tokens cont = ts.content();
      len       = ts.content().nItems();
      Tokens result;
	
      where--;
      if( len == 0 ) return ts;
      if( where >= 0 && where <= len ){
	for(int i=0; i < len; i++){
	    if( where != i ){
	        t = cont.itemAt(i);
		l.addElement(t);
    	    }
	    else{
		saveItem = cont.itemAt( i );
	   	l.addElement( item );
		l.addElement( saveItem );
            }
        }

	result = new Tokens();
	int size = l.size();
	for( int j=0; j < size; j++ )
	    result.append((SGML)l.elementAt( j ));
	return result;

      }
      else return ts;
  }   




  /** does tokens has all same tag */
  private boolean isAllSameTag(String myTag, Tokens ts){
    if( tag == null || ts == null ) return false;
    
    SGML item;
    end = ts.nItems();

    for ( int i = 0 ; i < end; i++ ){
      item = ts.itemAt( i );
      if( !item.tag().equalsIgnoreCase( myTag ) )
	return false;
    }

    return true;
  }

  /** process result.  If a token just return it.
   *  otherwise, if tokens and contents and all have the
   *  same tag, get the first element's class, create
   *  a new instance and let it reference the tokens.
   *  If tokens has content with different tag, create
   *  and element and let it reference the tokens
   */
  private SGML merge( SGML s ){
    SGML result;
    String myTag = getTag();

    if( s.isToken() ) {
      //Pia.debug("In merge -- token case "+s.toString());
      return s;
    }
    else{
      if( !isAllSameTag( myTag, s.content() ) )
	result = new Element();
      else{
	if( s.content().nItems() > 0 ){
	  Class c = (s.content().itemAt( 0 )).getClass();
	  //Pia.debug("the class-->"+c.toString());
	  try{
	    result = (SGML)c.newInstance();
	  }catch(Exception e){e.toString();}
	}
	result = new Element( myTag );
      }

      result.append( s.content() );	

      //Pia.debug("In merge -- tokens case "+result.toString());
    }
    return result;
  }

  /** walk through each element in the path and process it */
  private SGML doProcess(int what, SGML datum) throws InvalidIndex{
    try{
      if( isDots() ){
	// either . or ..
	Pia.debug("processing dots case.");
	datum = merge( datum );
      }else{
	// normal indexing case
	Pia.debug("processing normal case.");
	parsePositions();
        datum = datum.attr( this );
      }
    }catch(InvalidIndex e){
      //Pia.debug(e.toString());
      throw e;
    }

    return datum;
  }
    

  /**
   * Lookup for a SGML object indicated by path starting with datum 
   */  
  public SGML lookup(SGML datum) throws InvalidIndex, IllegalArgumentException{
    int what;

    if( datum == null ) throw new IllegalArgumentException("Datum is null.");
    //Pia.debug("lookup-->"+datum.toString());

    what = nextPositions();
    
    while ( what != EOFINPUT ){
      try{
	datum = doProcess( what, datum ); 
      }catch( InvalidIndex e ){
	//Pia.debug(e.toString());
	throw e;
      }
      resetPositionAttr();
      what = nextPositions(); 
    }
    
    
    //Pia.debug(this,"my class is-->"+datum.getClass().toString());
    //Pia.debug(this,"my content is-->"+datum.toString());
    //Pia.debug(this,"my content is-->"+Integer.toString(datum.content().nItems()));

    if( datum instanceof Tokens && datum.content().nItems() == 1 ){
      // datum is a Tokens.  return a Token instead with the datum's content
      
      Pia.debug("Datum is a Tokens with 1 content");
      
      SGML element = datum.content().itemAt(0);
      if( element.isToken())
	Pia.debug("Datum is return as a Token");

      return element;
    }
    
    return datum;
  }

  /**  
   * return the searched SGML object, if it is not found, it will be created
   */

  public SGML path(SGML datum) throws InvalidIndex, IllegalArgumentException {
    int what;
    SGML  prev      = datum;
    SGML  cur       = datum;
    SGML  newElement;
    SGML  whereToPut;
    SGML  origDatum = datum;
    Tokens  cont;
    int contNum;
    String newTag;

    SGML foobar = new Element();

    //Pia.debug("In path=========================");

    int isDotDot = path.indexOf("..");

    // we don't want ..
    if( isDotDot != -1 ) throw new InvalidIndex("dot dot is not accepted.");
    if( datum == null ) throw new IllegalArgumentException("Datum is null.");
    //Pia.debug("original datum-->"+datum.toString());

    what = nextPositions();
    
    while ( what != EOFINPUT ){
      prev = cur;


      cur = doProcess( what, cur ); 

      if( cur == null || cur.content().nItems() == 0 ){
	Pia.debug(" not successfull "+prev.getClass().toString());
	
	if( prev.content() == null ||
	    prev.content().itemAt(0).toString().equalsIgnoreCase("") )
	  whereToPut = prev;
	else if( !(prev instanceof Tokens) ){
	  //Pia.debug("************");
	  whereToPut = prev;
	}
	else{
	  cont    = prev.content();
	  Pia.debug("The current cont is-->"+cont.toString());
	  
	  contNum = cont.nItems();
	  Pia.debug("The contNum is-->"+Integer.toString(contNum));
	  
	  if( contNum == 1 )
	    whereToPut = prev.content().itemAt(0);
	  else if( contNum > 1 && isAllSameTag( cont.itemAt(0).tag(), cont ) )
	    whereToPut = putAt( cont, getStart() );
	  else if( contNum > 1 )
	    whereToPut = cont.itemAt(0);
	  else whereToPut = prev;  
        }
	
	Pia.debug("where to put-->"+whereToPut.toString());
	Pia.debug("tag-->"+getTag());
	Pia.debug("start-->"+Integer.toString( getStart() ));
	
	Pia.debug(" before get tag.");
	if ( getTag().equalsIgnoreCase( ANY ))
	  newTag = "";
	else
	  newTag = getTag();
	newElement = new Element( newTag, "" );
	Pia.debug("after create element-->"+newElement.toString());

	if( whereToPut.content() != null && getStart() >= whereToPut.content().nItems() ){
	  whereToPut.append( newElement );
	  Pia.debug("In append case greater or equal");
	}
	else
	  whereToPut.append( newElement );
	
	cur = newElement;
	foobar.append( whereToPut );	
	Pia.debug("the cur path is-->"+whereToPut.toString());
      }

      resetPositionAttr();
      what = nextPositions(); 
      }
    Pia.debug("original datum-->"+datum.toString());
    return cur;
  }

  /**
   * Lookup for a SGML object indicated by path starting with data object 
   */  
  
  public static SGML get(String path, SGML data)
       throws InvalidIndex, IllegalArgumentException
  {
    if(path == null)
      return data;

    //if(path.indexOf('.')>0 ||  path.indexOf('-')>=0){
      Index i = new Index(path);
      return i.lookup(data);
      //return i.path(data);
      //}else return data.attr(path);
  }
  
}







