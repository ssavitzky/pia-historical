// FormContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;
import java.util.Properties;
import java.io.IOException;

public class FormContent extends Properties implements Content{
 
  //public ContentHeader header;

 /** 
  * Return as a string all existing header information for this
  * object.
  * @return String with HTTP style header <tt> name: value </tt><br>
  */
  public String header(){}
  
 /** 
  * Return the  value of the given header or void if none.
  * @param  field name of header field
  * @return String value of a header attribute.
  */
  public String header(String field){}
  
 /** 
  * Set a header field to value
  * throws exception if not allowed to set.
  */
  public void header(String field, String value) throws NoSuchFieldException{}

 /** 
  * Sets all the headers to values given in hash table
  * hash keys are field names
  * throws exception if not allowed to set.
  */
  public void header(Hashtable table) throws NoSuchFieldException{}

 /** 
  * Access functions 
  * machine objects read content as a stream
  * two primary uses: acting as a source and sink for machines,
  * and allowing processing by agents " in stream "
  */


 /** 
  * set a source stream for this object
  * usually this will come from a machine
  */
  public void source(InputStream stream){
  }

 /**  get the next chunk of data as bytes
  *  @return number of bytes read -1 means EOF
  */
  public int read(byte buffer[]) throws IOException{}
 
 /**
  * get the next chunk of data as bytes
  * @param offset position in buffer to start placing data
  * @param length number of bytes to read
  * @return number of bytes read
  */
  public int read(byte buffer[], int offset, int length) throws IOException{}

  /** 
   * add an output stream to "tap" the data before it is written
   * any taps will get data during a read operation
   * before the data "goes out the door"
   */
  public void addTap(InputStream tap){}
  
  /**  
   * specify an agent to be notified when a condition is satisfy  
   * for example the object is complete
   */
  public void notifyWhen(Agent interested, Object condition){}

  /**
   * Unescape a HTTP escaped string
   * @param s The string to be unescaped
   * @return the unescaped string.
   */

  public static String unescape (String s) {
	StringBuffer sbuf = new StringBuffer () ;
	int l  = s.length() ;
	int ch = -1 ;
	for (int i = 0 ; i < l ; i++) {
	    switch (ch = s.charAt(i)) {
	      case '%':
		ch = s.charAt (++i) ;
		int hb = (Character.isDigit ((char) ch) 
			  ? ch - '0'
			  : Character.toLowerCase ((char) ch) - 'a') & 0xF ;
		ch = s.charAt (++i) ;
		int lb = (Character.isDigit ((char) ch)
			  ? ch - '0'
			  : Character.toLowerCase ((char) ch) - 'a') & 0xF ;
		sbuf.append ((char) ((hb << 4) | lb)) ;
		break ;
	      case '+':
		sbuf.append (' ') ;
		break ;
	      default:
		sbuf.append ((char) ch) ;
	    }
	}
	return sbuf.toString() ;
    }


  public FormContent(String toSplit){
    StringTokenizer tokens;
    String token;
    String[] pairs;
    int count;
    int i = 0;
    int pos;

    if( toSplit != null )
      tokens = new StringTokenizer(toSplit,"&");
    count = tokens.countTokens();
    if( count > 0 )
      pairs = new String[ count ];

    while ( tokens.hasMoreElements() ){
      token = tokens.nextToken();
      pairs[i++] = token;
    }
    i = 0;
    for(; i < pairs.length(); i++){
      String s = pairs[i];
      pos = s.indexOf('=');
      String p = s.substring(0, pos);
      String param = unescape( p );
      String v = s.substring( pos+1 );
      String value = unescape( v );
      put(param, value);
    }
  }


}
