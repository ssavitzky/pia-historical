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
  protected int numOfdts = 0;
  protected int numOfdds = 0;

  /************************************************************************
  ** Access to attributes:
  ************************************************************************/
  /** return a table with tag names as keys and locations as values
   */
  public Table tagTable() {
    Table result = new Table();

    List locations ;
    for (int i = 0; i < nItems(); ++i) {
      String name =  itemAt(i).tag().toLowerCase();
      //System.out.println("tagTable-->"+name);
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
  
  protected int[] scanTag(String myTag){
    int k;

    List loc = tagLocations( myTag );
    int [] indices  = new int[ loc.nItems() ];

    for(int i=0;!loc.isEmpty();i++ ){
      k = ((Integer)loc.shift()).intValue();
      indices[i] = k;
      //System.out.println("scanTag -->"+myTag+" ["+Integer.toString( k )+"]");
    }

    return indices;
  }

  protected int[] listToInt(List v){
    int len = v.size();
    int[] myList = new int[ len ];
    for(int i = 0; i < len; i++){
      try{
	//System.out.println("boundaries-->["+(String)v.at(i)+"]"); 
	myList[i] = Integer.parseInt( (String)v.at(i) );
      }catch(Exception e){}
    }
    return myList;
  }

  protected int[] dtBoundaries( int[] indices ){
    List myList = new List();
    int len = indices.length;

    if( len == 0 ) return indices;
    
    for( int i = 0; i < len; i++ ){
      if( i == 0 ){
	if( indices[i] != 0 )
	  myList.push( "-1" );
	  myList.push( Integer.toString( indices[i] ) );
      }
      else
	myList.push( Integer.toString( indices[i] ) );
    }
    myList.push( Integer.toString( nItems() ) );
    return listToInt( myList );
  }


  protected Tokens grabDd(int[] dds, int s, int e){
    Tokens ts = new Tokens();
    int len = dds.length;
    int loc;

    for(int i=0; i < len; i++){
      loc = dds[i];
      if( loc > s && loc < e ){
	SGML item = itemAt( loc );
	ts.append( (SGML)Util.removeSpaces(item.content()) );
      }
    }
    //System.out.println("grabDd-->"+ts.toString());
    return ( ts.nItems() == 0 ) ? null : ts;
  }

  protected List smashDl(int[] poles, int[]dts, int[]dds){
    int start, end;
    int len = poles.length;
    SGML ddTokens = null;
    List myList = new List();
    
    start = poles[0];
    for( int i = 1; i < len; i++ ){
      end = poles[i];
      if( start == -1 )
	myList.push( new Text("") );
      else
	myList.push( itemAt(start).contentText() );
      ddTokens = grabDd(dds, start, end );
      if( ddTokens == null )
	myList.push( Token.empty );
      else
	myList.push( ddTokens );

      start = end;
    }
    return myList;

  }

  protected Tokens getRangeWKeyVal( List l, String myTag, int s, int e ){
    crc.sgml.Text dtText;
    SGML dd;
    int howmany = 1;
    Tokens result = new Tokens();
    boolean isKey;
    

    // true looking for dts' text
    isKey = myTag.equalsIgnoreCase("keys");

    // if out of range return
    if( isKey && numOfdts < s )
      return result;
    
    if( !isKey && numOfdds < s )
      return result;

    if( e == Index.LAST )
      e = 10000;

    int len = l.size();

    for( int j = 0 ; j < len ; j++ ){
      if( j <= len - 2 ){
	j++;

	if( howmany >= s && howmany <= e ){
	  
	  if( isKey ){
	    dtText = ((SGML)l.at( j - 1 )).contentText();
	    if( dtText != null && dtText.contentString() != "" ){
	      result.append( dtText );
	      howmany++;
	    }
	  }else{
	    dd = (SGML)l.at( j );
	    if( dd != Token.empty ){
	      result.append( dd );
	      howmany++;
	    }
	  }

	}
      } 

    }
    return result;
  }


  protected Tokens getRange( List l, String myTag, int s, int e ){
    crc.sgml.Text dtText;
    SGML dd;
    int howmany = 1;
    Tokens result = new Tokens();

    //System.out.println("In getRange");
    //System.out.println("myTag-->"+myTag);

    int len = l.size();

    if( e == Index.LAST )
      e = 10000;

    //System.out.println("start-->"+s);
    //System.out.println("end-->"+e);

    for( int j = 0 ; j < len ; j++ ){
      dtText = ((SGML)l.at( j )).contentText();
      //System.out.println("dt text-->["+dtText+"]");
      if( j <= len - 2 ){
	j++;

	if( myTag.equalsIgnoreCase( dtText.contentString().trim() ) ){
	  //System.out.println("In equal");
	  if( howmany >= s && howmany <= e ){
	    //System.out.println("howmany-->"+Integer.toString(howmany));
	    dd = (SGML)l.at( j );
	    //System.out.println("My dd-->"+dd.toString());
	    result.append( dd );
	  }
	  howmany++;
	}


      } 
    }
    return result;
  }

  protected void printDl( List l ){
    //System.out.println("----smash dl list----\n");
    for(int i = 0 ; i < l.nItems(); i++){
      SGML item = (SGML)l.at( i );
      //System.out.println("item-->"+item.toString()+"\n");
      /*
      if( i % 2  == 0 )
	System.out.println("dt-->"+item.toString());
      else
	System.out.println("dd-->"+item.toString());
	*/
    }
    //System.out.println("\n");
  }

  /** Retrieve an attribute by index. */
  public SGML attr(Index i) {
    String myTag = i.getTag();

    if( myTag.equalsIgnoreCase( Index.TEXT ) ||
	myTag.equalsIgnoreCase( Index.ANY ) ||
	myTag.equalsIgnoreCase( "dt" ) || 
	myTag.equalsIgnoreCase( "dd" ) )
      return super.attr( i );


    List dtddList;
    int s        = i.getStart();
    int e        = i.getEnd();

    int[] dts   = scanTag( "dt" );
    numOfdts = dts.length;
    int[] poles = dtBoundaries( dts );

    int[] dds   = scanTag( "dd" );
    numOfdds = dds.length;

    if( poles.length == 0 ) return new Tokens();

    dtddList = smashDl( poles, dts, dds );
    printDl( dtddList );
    if( myTag.equalsIgnoreCase( Index.VALS ) ||
	myTag.equalsIgnoreCase( Index.KEYS ) )
      return getRangeWKeyVal(dtddList, myTag, s, e);
    else
      return getRange(dtddList, myTag, s, e );
    
  }

  /************************************************************
  ** hash like functions -- note the internally structure remains a tokens
  ** so these are not efficient
  ************************************************************/
  
  /**
   * remove the dt and dd elements associated with key
   */

  public void removeKey(String key){
     if(content == null) return;

    // create a new content without the offending material
    Tokens  replacement = new Tokens();
    boolean remove = false;
    for( int i=0;i <content.nItems();i++){
      SGML  token =content.itemAt(i);
      String tag =  token.tag();
      if( tag.equalsIgnoreCase("dt")){
	if(Util.textEquals( key, token)){
	  // start removing things
	  remove = true;
	} else {
	  // stop removing things
	  remove = false;
	}
      }
      // always keep things which are not dd  or dt -- they should not be here
      if( remove && (tag.equalsIgnoreCase("dd") || tag.equalsIgnoreCase("dt"))){
	//  do not copy into new content
      } else {
	 replacement.push(token);
      }
    }
     content = replacement;
     
  }
	
  public void at(String key, SGML value){
    append(new Element("dt", key));
    append(new Element("dd", value));
  }
  

  /************************************************************************
  ** Construction:
  ************************************************************************/
  // inherit everything
  public DescriptionList(Element e) {
    super(e);
  }
  public DescriptionList(String tag, Attrs tbl) {
    super(tag,tbl);
  }
  
}

