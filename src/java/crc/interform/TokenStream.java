////// TokenStream.java:  stream of InterForm Tokens
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Tokens;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A stream of SGML Token's.  
 *	This is really just a Tokens list attached to an OutputStream.
 *	As soon as a token is pushed into it, it gets converted to a 
 *	string and shoved out the other end.
 */
public class TokenStream extends Tokens {

  /************************************************************************
  ** Components:
  ************************************************************************/

  PrintStream ostream;
  public boolean blocked = false;


  /************************************************************************
  ** Operations:
  ************************************************************************/

  /** Append some tokens.  
   *	The tokens are simply shipped to the output stream unless we're
   *	blocked for some reason or the output stream is null.
   */
  public SGML append(SGML sgml) {
    if (sgml == null) return this;
    if (nItems() == 0 && ostream != null && !blocked) {
      ostream.print(sgml.toString());
    } else {
      super.append(sgml);
      if (ostream != null && !blocked) {
	for (int i = 0; i < nItems(); ++i) {
	  ostream.print(at(i).toString());
	}
	items.removeAllElements();
      }
    }
    return this;
  }

  /** Append some text.  */
  public SGML appendText(Text t) {
    if (t == null) return this;
    if (ostream != null && !blocked) {
 	ostream.print(t.toString());
	return this;
    } else {
      return append(t);
    }
  }

  /** Block the output stream. */
  public void block() {
    blocked = true;
  }

  /** Unblock the output stream. */
  public void unblock() {
    blocked = false;
  }

  /** Flush the output stream. */
  public void flush() {
    if (ostream != null) ostream.flush();
  }
    
  /** Close the output stream.  Sets it to null. */
  public void close() {
    if (ostream != null) ostream.close();
    ostream = null;
  }
    
  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TokenStream() {
    super();
  }

  public TokenStream(OutputStream s) {
    this();
    try {
      ostream = (PrintStream)s;
    } catch (Exception e) {
      ostream = new PrintStream(s);
    }
  }


}
