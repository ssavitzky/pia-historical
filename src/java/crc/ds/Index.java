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

/** An index object is used for indexing SGML objects.
 *       Semantics depend upon SGML type.
 *	 indexes behave somewhat like enumerations-- asking for the next
 *       element moves a pointer up one.
 *       this class also implements the recursive lookup by delegating
 *       to each SGML object in secession.
 *Syntax for accessing html elements.
 *
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
 * To retrieve all of dts'text, xxx should be "keys"
 * To retrieve all of dds'text, xxx should be "values"
 *
 *Examples:
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
 */
public class Index {

  /************************************************************************
  ** Components:
  ************************************************************************/
  public static final String ANY="empty";
  public static final String TEXT="text";
  public static final String KEYS="keys";
  public static final String VALS="values";

  static final int INVALIDNUMBER = -100;
  static final int FIRST=1;
  public static final int LAST=-1;

  static final int EOFINPUT=-1;
  static final int DOTS=-2;
  static final int NORMAL=0;
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

  protected boolean defaultStart = false;

  protected int processState = NORMAL;

  /** The actual items. */
  protected  List  items;
  protected List positionParam;

  class InvalidInput extends Exception{
    public InvalidInput() { super(); }
    public InvalidInput(String s) {super(s);}
  }

  /**********************************************************************
   * Accessors to tag, start and end Index
   **********************************************************************/
  public String getTag(){
    return tag;
  }

  public int getStart(){
    return start;
  }

  public int getEnd(){
    return end;
  }

  public void setTag(String t){
    tag = t;
  }

  public void setStart(int s){
   start = s;
  }

  public void setEnd(int e){
    end = e;
  }
  
  public boolean isNormal(){
    return processState == NORMAL;
  }

  public boolean isDots(){
    return processState == DOTS;
  }

  public boolean isNumeric(String n)
  {
    try {
      int number = java.lang.Integer.valueOf(n).intValue();
      return true;
    } catch (Exception e) {
      return false;  //not legal index
    }
  }
    
  /** return just the current item as string
   */
  
  public String myString()
  {
    return (String)items.at(currentItem);
  }
  
  /**  returns the first string
        usually the caller uses this to do the first level table lookup
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
	System.out.println("Index-->pushing a blank, end in dot");
      }
    }
  }
  
  public Index(List l) {
    items = l; // we don't manipulate items, so no need to copy
  }
  
  /************************************************************************
  /**  operations  to provide index positioning and determine tag, start and end */
  /************************************************************************/

  /** The number of indexed items. */
  public int size() {
    return items == null? 0 : items.size();
  }

  public int nextPositions() {
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

    //System.out.println("nextPosition's item: "+item+ " length-->"+ Integer.toString(item.length()));

    StringTokenizer ts = new StringTokenizer(item, "-", true);
    try{
      while( ts.hasMoreElements() )
	positionParam.push( ts.nextToken() );
    }catch(Exception e){}

    processState = NORMAL;

    return 1;
  }


  public String nextToken(){
    Object o = positionParam.shift();
    if( o == null ) return null;
    return (String) o;
  }

  public void pushBackToken( String s ){
    positionParam.unshift( s );
  }

  protected boolean isDash(String s){
    if( s == null ) return false;
    return ( s.length() == 1 && s.indexOf("-") != -1 ) ? true : false;
  }

  protected void resetPositionAttr(){
    processState = NORMAL;
    tag = ANY;
    setStart( INVALIDNUMBER );
    setEnd( LAST );
    defaultStart = false;
  }

  protected void dumpPositionParam(){
    String s;
    Object o;

    List tmp = (List)positionParam.clone();
    while( !tmp.isEmpty() ){
      o = tmp.shift();
      if( o != null )
	System.out.println("[" + (String) o + "]");
    }
  }
  
  protected boolean isTextOrDigit(String s){
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
  protected int parseNumber(String s){
    if( s == null ) return INVALIDNUMBER;

    try{
      int startPos = Integer.parseInt( s );
      return startPos;
    }catch(NumberFormatException e){
      return INVALIDNUMBER;
    }
  }

  protected void parseTag(){
    //System.out.println( "Entering parseTag...");

    String t = nextToken();
    //System.out.println("Token-->"+t);

    // missing tag and dash, tag is ANY.  current token is number
    if( parseNumber( t ) != INVALIDNUMBER ){
      tag = ANY;
      pushBackToken( t );
      return;
    }
      
    // tag is not specified could be a dash
    if( !isTextOrDigit(t) ){
      tag = ANY;
      pushBackToken( t );
    }else{
      tag = t;
    }

    //System.out.println("Exiting parseTag...");
  }

  protected void parseStart() throws InvalidInput{
    int number;
    //System.out.println("Entering parseStart...");

    //dumpPositionParam();
    // getting dash
    String t = nextToken();
    //System.out.println("Starting token-->"+t);
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
      //System.out.println("Token-->"+t);
      
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
	throw new InvalidInput("Invalid starting index.");
      }
    } else if ( number == INVALIDNUMBER ){
      throw new InvalidInput("Invalid starting index.");
    }

    //System.out.println(this, "Exiting parseStart...");
  }

  protected void parseEnd() throws InvalidInput{
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
	throw new InvalidInput("Missing dash before end.");
      else{
	t = nextToken();
	
	if( t == null )
	  setEnd( LAST );
	else  if( (number = parseNumber( t )) != INVALIDNUMBER )
	  setEnd( number );
	else throw new InvalidInput("Invalid ending index.");
      }
  }

  protected void parsePositions() throws InvalidInput{
    int s, e;

    parseTag();


    try{
      parseStart();
      parseEnd();

      System.out.println("The tag-->"+ getTag());
      System.out.println("The start-->"+ Integer.toString(getStart()));
      System.out.println("The end-->"+ Integer.toString(getEnd()));

      s = getStart();
      e = getEnd();
      if( s < 1 )
	throw new InvalidInput("Start index must start at 1 or larger.");
      if( s > e && e != LAST )
	throw new InvalidInput("Start index is larger than end index."); 

    }catch(InvalidInput ee){
      //System.out.println("The error is-->"+ee.toString());
      throw ee;
    }
  }

  protected boolean isAllSameTag(String myTag, Tokens ts){
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

  protected SGML merge( SGML s ){
    SGML result;
    String myTag = getTag();

    if( s.isToken() ) {
      System.out.println("In merge -- token case "+s.toString());
      return s;
    }
    else{
      if( !isAllSameTag( myTag, s.content() ) )
	result = new Element();
      else{
	if( s.content().nItems() > 0 ){
	  Class c = (s.content().itemAt( 0 )).getClass();
	  System.out.println("the class-->"+c.toString());
	  try{
	    result = (SGML)c.newInstance();
	  }catch(Exception e){e.toString();}
	}
	result = new Element( myTag );
      }

      result.append( s.content() );	

      System.out.println("In merge -- tokens case "+result.toString());
    }
    return result;
  }

  protected SGML doProcess(int what, SGML datum) throws InvalidInput{
    try{
      if( isDots() ){
	// either . or ..
	System.out.println("processing dots case.");
	datum = merge( datum );
      }else{
	// normal indexing case
	System.out.println("processing normal case.-->"+ datum.toString());
	parsePositions();
        datum = datum.attr( this );
      }
    }catch(InvalidInput e){
      //System.out.println(e.toString());
      throw e;
    }

    return datum;
  }
    

  /************************************************************************
  ** Lookups: instance methods
  ************************************************************************/
  
  public SGML lookup(SGML datum) throws InvalidInput, IllegalArgumentException{
    int what;

    if( datum == null ) throw new IllegalArgumentException("Datum is null.");
    System.out.println("lookup-->"+datum.toString());

    what = nextPositions();
    
    while ( what != EOFINPUT ){
      try{
	datum = doProcess( what, datum ); 
      }catch( InvalidInput e ){
	System.out.println(e.toString());
	throw e;
      }
      resetPositionAttr();
      what = nextPositions(); 
    }
    
    
    System.out.println("my class is-->"+datum.getClass().toString());
    System.out.println("my content is-->"+Integer.toString(datum.content().nItems()));

    if( datum instanceof Tokens && datum.content().nItems() == 1 ){
      // datum is a Tokens.  return a Token instead with the datum's content
      
      System.out.println("Datum is a Tokens with 1 content");
      
      SGML element = datum.content().itemAt(0);
      if( element.isToken())
	System.out.println("Datum is return as a Token");

      return element;
    }
    
    return datum;
  }

  public SGML lookup(Table datum) throws InvalidInput, IllegalArgumentException{
    if(datum == null){
      return null;
    }
    
    SGML data = (SGML)datum.at((String)items.shift());
    return lookup(data);
  }

  protected SGML putAt( SGML cs, int where ){
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

  SGML insertAt(int where, SGML item, SGML ts){
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

  /**  recursive function for setting path to value for SGML objects
    semantics of set, attr(name,value), depend on object
    same  inner loop as lookup except that missing objects get created
    */

  public SGML path(SGML datum) throws InvalidInput, IllegalArgumentException {
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

    System.out.println("In path=========================");

    if( datum == null ) throw new IllegalArgumentException("Datum is null.");
    System.out.println("original datum-->"+datum.toString());

    what = nextPositions();
    
    while ( what != EOFINPUT ){
      prev = cur;


      cur = doProcess( what, cur ); 

      if( cur.content().nItems() == 0 ){
	System.out.println(" not successfull "+prev.getClass().toString());
	
	if( prev.content().itemAt(0).toString().equalsIgnoreCase("") )
	  whereToPut = prev;
	else if( prev instanceof Element ){
	  System.out.println("************");
	  whereToPut = prev;
	}
	else{
	  cont    = prev.content();
	  System.out.println("The current cont is-->"+cont.toString());
	  
	  contNum = cont.nItems();
	  System.out.println("The contNum is-->"+Integer.toString(contNum));
	  
	  if( contNum == 1 )
	    whereToPut = prev.content().itemAt(0);
	  else if( contNum > 1 && isAllSameTag( cont.itemAt(0).tag(), cont ) )
	    whereToPut = putAt( cont, getStart() );
	  else if( contNum > 1 )
	    whereToPut = cont.itemAt(0);
	  else whereToPut = prev;  
        }
	
	System.out.println("where to put-->"+whereToPut.toString());
	System.out.println("tag-->"+getTag());
	System.out.println("start-->"+Integer.toString( getStart() ));
	
	System.out.println(" before get tag.");
	if ( getTag().equalsIgnoreCase( ANY ))
	  newTag = "";
	else
	  newTag = getTag();
	newElement = new Element( newTag, "" );
	System.out.println("after create element-->"+newElement.toString());

	if( getStart() >= whereToPut.content().nItems() ){
	  whereToPut.append( newElement );
	  System.out.println("In append case greater or equal");
	}
	else
	  whereToPut.append( newElement );
	
	cur = newElement;
	foobar.append( whereToPut );	
	System.out.println("the cur path is-->"+whereToPut.toString());
      }

      resetPositionAttr();
      what = nextPositions(); 
      }
    System.out.println("original datum-->"+datum.toString());
    return cur;
  }

  
  /************************************************************************
  ** Lookups class methods
  ************************************************************************/
  
  public static SGML get(String path, SGML data) throws InvalidInput, IllegalArgumentException{
    if(path == null)
      return data;

    //if(path.indexOf('.')>0 ||  path.indexOf('-')>=0){
      Index i = new Index(path);
      return i.lookup(data);
      //return i.path(data);
      //}else return data.attr(path);
  }
  
  
  public static SGML get(String path, Table data) throws InvalidInput, IllegalArgumentException{
    if(path == null){
      return null;
    }    
    //if(path.indexOf('.')>0 ||  path.indexOf('-')>=0){
      Index i = new Index(path);
      return i.lookup(data);
      //}
      //return (SGML)data.at(path);
  }

  public static SGML makeHtml0(){
    SGML foo = new Element();
    
    SGML u1 = new Element("ul");
    SGML u2 = new Element("ul"); 
    
    SGML c1 = new Element("li");
    c1.append("Hello world");
    
    SGML c2 = new Element("li");
    c2.append("I love u. U love me");
    
    u1.append( c1 );
    u2.append( c2 );
    foo.append( u1 );
    foo.append( u2 );

    return foo;
  }
  
  public static SGML makeHtml1(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "bbb"); 
    
    SGML dd1 = new Element("dd", "no sex");
    SGML dd2 = new Element("dd", "in nirvana");
    
    dl.append( dt1 );
    dl.append( dd1 );
    dl.append( dt2 );
    dl.append( dd2 );

    foo.append( dl );

    return foo;
  }

  public static SGML makeHtml2(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "bbb"); 
    
    SGML dd1 = new Element("dd", "no sex");
    SGML dd2 = new Element("dd", "in nirvana");
    
    //dl.append( dt1 );
    dl.append( dd1 );
    dl.append( dt2 );
    dl.append( dd2 );

    foo.append( dl );

    return foo;
  }

  public static SGML makeHtml3(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "bbb"); 
    
    SGML dd1 = new Element("dd", "no sex");
    SGML dd2 = new Element("dd", "in nirvana");
    
    dl.append( dt1 );
    //dl.append( dd1 );
    dl.append( dt2 );
    dl.append( dd2 );

    foo.append( dl );

    return foo;
  }


  public static SGML makeHtml4(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "bbb"); 
    
    SGML dd1 = new Element("dd", "no sex");
    SGML dd2 = new Element("dd", "in nirvana");
    SGML dd3 = new Element("dd", "Barney is a dork");
    
    dl.append( dt1 );
    dl.append( dd1 );
    dl.append( dd2 );
    dl.append( dt2 );
    dl.append( dd3 );

    foo.append( dl );

    return foo;
  }
  
  public static SGML makeHtml5(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "aaa"); 
    
    SGML dd1 = new Element("dd", "no sex");
    SGML dd2 = new Element("dd", "in nirvana");
    
    dl.append( dt1 );
    dl.append( dd1 );
    dl.append( dt2 );
    dl.append( dd2 );

    foo.append( dl );

    return foo;
  }

  

  public static void main(String argv[]){
    String s = null;

    if(argv.length <2 )
      System.out.println("need doc# and path.");

    SGML foo = new Text("");
    int what = 0;
    try{
      what = Integer.parseInt( argv[0] );
    }catch(Exception e){}

    switch( what ){
    case 0:
      foo = makeHtml0();
      break;
    case 1 :
      foo = makeHtml1();
      break;
    case 2:
      foo = makeHtml2();
      break;
    case 3:
      foo = makeHtml3();
      break;
    case 4:
      foo = makeHtml4();
      break;
    case 5:
      foo = makeHtml5();
      break;
    default:
      foo = new Element("table", "");
      break;
    }
	
    System.out.println("original foo-->"+foo.toString());

    try{
      //SGML result = Index.get(argv[1], foo);

      Index i = new Index( argv[1] );
      SGML result = i.path( foo );


      System.out.println("Here is the result-->"+result.toString());
    }catch(Exception e){
    } 
  }
}



