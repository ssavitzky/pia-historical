////// Authenticate.java:  Handler for <authenticate>
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

/** Handler class for &lt;authenticate&gt tag. 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;authenticate user=ident password="string" [file="fn"]&gt;
 * <dt>Dscr:<dd>
 *	Authenticate USER with given PASSWORD in FILE (default /etc/passwd).
 *	Return USER if authentication succeeds.
 * </dl>
 */
public class Authenticate extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<authenticate user=ident password=\"string\" [file=\"fn\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Authenticate USER with given PASSWORD in FILE (default /etc/passwd)." +
    "Return USER if authentication succeeds.\n" +
"";
 
  /** Authenticate a user.  The password file has lines of the form:
   *	<pre><em>username</em>:<em>password</em>[:.*]</pre>
   *	Authentication is done using a Java translation of the Unix
   *	<code>crypt</code>(3) routine. The first two characters of the
   *	password field are the "salt".  <p>
   *  Eventually other authentication methods should be supported.
   */
  public boolean authenticate(String user, String password, Reader pwfile) {
    if (user == null || password == null || pwfile == null) return false;

    LineNumberReader rd = new LineNumberReader(pwfile);
    String line  = null;
    String entry = null;

    for ( ; ; ) {
      try {
	line = rd.readLine();
      } catch (IOException e) {
	line = null;
      }
      if (line == null) return false;
      if (line.startsWith(user+":")) break;
    }
    line = line.substring(user.length() + 1);
    if (line.indexOf(":") >= 0) {
      entry = line.substring(0, line.indexOf(":"));
    } else {
      entry = line;
    }

    if (entry.length() == 0 && password.length() == 0) return true;
    if (entry.length() < 3) return false;

    String salt = entry.substring(0, 2);
    String pass = entry.substring(2);

    return entry.equals(Jcrypt.crypt(salt, password));
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

    // === should really get password file path from a property ===
    String file = Util.getString(it, "file", "/etc/passwd");
    Reader pwfile = null;

    try {
      pwfile = new FileReader(file);
    } catch (IOException e) {
      ii.error(ia, "Password file " + file);
      return;
    }
    
    if (authenticate(user, password, pwfile)) ii.replaceIt(user);
  }
}
