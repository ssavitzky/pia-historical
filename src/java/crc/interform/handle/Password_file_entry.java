////// Password_file_entry.java:  Handler for <password-file-entry>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.sgml.SGML;
import crc.sgml.Token;

import java.io.Reader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;

import misc.Jcrypt;

/** Handler class for &lt;password-file-entry&gt tag. 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;password-file-entry user=ident password="string" [file="fn"]&gt;
 *	additional-content&lt;/password-file-entry&gt;
 * <dt>Dscr:<dd>
 *	Make password file entry for USER with given PASSWORD
 * </dl>
 */
public class Password_file_entry extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<password-file-entry user=ident password=\"string\" [file=\"fn\"]>\n" +
    "additional-content</password-file-entry>" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Make password file entry for USER with given PASSWORD  " +
"";
 
  /** Compute the entry.  The password file has lines of the form:
   *	<pre><em>username</em>:<em>password</em>[:.*]</pre>
   *	Authentication is done using a Java translation of the Unix
   *	<code>crypt</code>(3) routine. The first two characters of the
   *	password field are the "salt".  <p>
   *  Eventually other authentication methods should be supported.
   */
  public String pwfEntry(String user, String password, String extra) {
    if (user == null || password == null) return null;

    /* Compute a 12-bit random "salt" and convert it to a string */

    int salt1 = (int)Math.rint(2047*Math.random());
    int salt0 = salt1 & 63;
    salt1 = 63 & (salt1 >> 6);

    String salt = "" + (char)cov_2char[salt0] + (char)cov_2char[salt1];
    String entry = user + ":" + Jcrypt.crypt(salt, password);

    //System.err.println("salt = " + salt + " entry = " + entry);

    if (extra != null && ! "".equals(extra)) {
      entry += ":" + extra;
    }
    return entry;
  }

  public void handle(Actor ia, SGML it, Interp ii) {

    /* Start out by deleting the token to be returned.  That way, if
     *	something goes wrong, we reduce the chances that a password
     *	will be exposed in the output. */
    ii.deleteIt();

    String user = Util.getString(it, "user", null);
    String password = Util.getString(it, "password", null);
    if (ii.missing(ia, "user", user) 
	|| ii.missing(ia, "password", password)) {
      return;
    }

    String extra = it.contentString();
    
    ii.replaceIt(pwfEntry(user, password, extra));
  }

  /** The conversion from 6-bit int to char for the salt */
  private static final int cov_2char[] =
  {
      0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 
      0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43, 0x44, 
      0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C, 
      0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0x53, 0x54, 
      0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x61, 0x62, 
      0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 
      0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70, 0x71, 0x72, 
      0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
  };


}
