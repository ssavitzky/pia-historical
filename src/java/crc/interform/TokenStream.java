////// TokenStream.java:  stream of InterForm Tokens
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import java.io.OutputStream;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Tokens;

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

  OutputStream ostream;
  public boolean blocked = false;


  /************************************************************************
  ** Operations:
  ************************************************************************/

  /** 
   * basic routine to output a string.
   */
  protected final void write(String s) {
    try {
      ostream.write(s.getBytes());
    } catch (java.io.IOException e) {}
  }

  /** Append some tokens.  
   *	The tokens are simply shipped to the output stream unless we're
   *	blocked for some reason or the output stream is null.
   */
  public SGML append(SGML sgml) {
    if (sgml == null) return this;
    if (nItems() == 0 && ostream != null && !blocked) {
      write(sgml.toString());
    } else {
      super.append(sgml);
      if (ostream != null && !blocked) {
	for (int i = 0; i < nItems(); ++i) {
	  write(at(i).toString());
	}
	items.removeAllElements();
      }
    }
    return this;
  }

  /** Append a token.  
   *	The tokens are simply shipped to the output stream unless we're
   *	blocked for some reason or the output stream is null.
   */
  public crc.ds.Stuff push(Object v) {
    if (v == null) return this;
    if (nItems() == 0 && ostream != null && !blocked) {
      write(v.toString());
    } else {
      super.push(v);
      if (ostream != null && !blocked) {
	for (int i = 0; i < nItems(); ++i) {
	  write(at(i).toString());
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
 	write(t.toString());
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
    try {
      if (ostream != null) ostream.flush();
    } catch (java.io.IOException e) {}
  }
    
  /** Close the output stream.  Sets it to null. */
  public void close() {
    try {
      if (ostream != null) ostream.close();
    } catch (java.io.IOException e) {}
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
    ostream = s;
  }


}
