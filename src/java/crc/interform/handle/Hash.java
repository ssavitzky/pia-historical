// Hash.java:  Handler for <hash>
// $Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;
import java.security.*;

/** Handler class for &lt;hash&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#hash">Manual
 *	Entry</a> for syntax and description.
 */
public class Hash extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<hash>content</hash>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Replace CONTENT with the value of a one way hash on the string representation of CONTENT.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String toHash = it.contentString();
    byte hash[];
    
    try{
      
    MessageDigest sha = MessageDigest.getInstance("SHA-1");

    hash = sha.digest(toHash.getBytes());
    } catch (Exception e) {
      hash = new byte[0];
    }
         
    ii.replaceIt(crc.util.Utilities.encodeBase64(hash));
  }

  /** Legacy action.  */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    byte hash[];
    
    try{
      MessageDigest sha = MessageDigest.getInstance("SHA-1");
      hash = sha.digest(cstring.getBytes());
    } catch (Exception e) {
      hash = new byte[0];
    }
    return putText(out, crc.util.Utilities.encodeBase64(hash));
  }

}
