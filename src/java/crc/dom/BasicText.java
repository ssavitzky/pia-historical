// BasicText.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;
import java.util.Hashtable;

public class BasicText extends AbstractNode implements Text {

  public BasicText(){
    ignoreWhiteSpc = false;
  }

  public BasicText(String d){
    data = d;
    ignoreWhiteSpc = false;
  }


  public BasicText(String d, boolean iws)
  {
    data = d;
    ignoreWhiteSpc = iws; 
  }

  public BasicText(BasicText bt)
  {
    if( bt != null ){
      setParent( null );
      setPrevious( null );
      setNext( null );
      setIsIgnorableWhitespace( false );
      setData( bt.getData() );
      copyChildren( bt );
    }
  }

  public Object clone(){
    BasicText n = (BasicText)super.clone();
    n.setIsIgnorableWhitespace( false );
    n.setData( getData() );
    n.copyChildren( this );
    return n;
  }
  
  public void setData(String data){ this.data = data; }
  public String getData()
  {
    String d     = subEntity("&amp;", data);
    String dd    = subEntity("&lt;", d);
    String ddd   = subEntity("&gt;", dd);
    String dddd  = subEntity("&apos;", ddd);
    String ddddd = subEntity("&quot;", dddd);
    return ddddd; 
  }

  public void setIsIgnorableWhitespace(boolean isIgnorableWhitespace){ ignoreWhiteSpc = isIgnorableWhitespace;}

  public boolean getIsIgnorableWhitespace(){ return ignoreWhiteSpc; }

  public int getNodeType(){ return NodeType.TEXT; }

  /** Return an empty string
   * 
   */
  public String startString(){
    return "";
  }
  
  /** Return the String equivalent of this node type.
   *  Subclasses are suppose to override this function
   *  to return appropriate content string.
   */
  public String contentString(){
    return getData();
  }

  /** Return an empty string
   *	
   */
  public String endString(){
    return "";
  }

  /** Substitute basic entity within data string.
   *
   */
  protected String subEntity(String ent, String s){
    int spos=0;
    int epos=0;
    int fpos=0;
    boolean found = false;
    StringBuffer sb = new StringBuffer();

    fpos = s.indexOf( ent );
    while( fpos != -1 ){
      found = true;
      Report.debug("spos is-->"+Integer.toString(spos));
      Report.debug("fpos is-->"+Integer.toString(fpos));
      Report.debug("The substring is-->"+s.substring(spos, fpos));
      sb.append(s.substring(spos, fpos));
      spos = fpos + ent.length();
      sb.append((String)predefEntityTab.get(ent));
      fpos = s.indexOf( ent, fpos+1 );
    }
    if( !found ) return s;
    
    if( spos != 0 )
      sb.append(s.substring(spos, s.length()));

    return new String( sb );
  }

  protected String data = "";
  protected boolean ignoreWhiteSpc;
  protected static Hashtable predefEntityTab;

  static{
    predefEntityTab = new Hashtable();
    predefEntityTab.put("&lt;", "<");
    predefEntityTab.put("&gt;", ">");
    predefEntityTab.put("&amp;", "&");
    predefEntityTab.put("&apos;", "'");
    predefEntityTab.put("&quot;", "\"");
  }


};
