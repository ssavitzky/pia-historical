////// TokenWriter.java:  Write a stream of InterForm Tokens
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import java.io.Writer;
import java.io.IOException;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Tokens;

/**
 * A stream of SGML Token's attached to a Writer.  
 *	As soon as a token is pushed into it, it gets converted to a 
 *	string and shoved out the other end.
 */
public class TokenWriter extends Tokens {

  /************************************************************************
  ** Components:
  ************************************************************************/

  Writer out;
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
    if (nItems() == 0 && out != null && !blocked) {
      try {
	out.write(sgml.toString());
      } catch (IOException e) {
	// === really not clear what to do if there's a problem writing ===
      }
    } else {
      super.append(sgml);
      if (out != null && !blocked) {
	for (int i = 0; i < nItems(); ++i) {
	  try {
	    out.write(at(i).toString());
	  } catch (IOException e) {
	    // === really not clear what to do if there's a problem writing ===
	  }
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
    if (nItems() == 0 && out != null && !blocked) {
      try {
	out.write(v.toString());
      } catch (IOException e) {
	// === really not clear what to do if there's a problem writing ===
      }
    } else {
      super.push(v);
      if (out != null && !blocked) {
	for (int i = 0; i < nItems(); ++i) {
	  try {
	    out.write(at(i).toString());
	  } catch (IOException e) {
	    // === really not clear what to do if there's a problem writing ===
	  }
	}
	items.removeAllElements();
      }
    }
    return this;
  }

  /** Append some text.  */
  public SGML appendText(Text t) {
    if (t == null) return this;
    if (out != null && !blocked) {
      try {
	out.write(t.toString());
      } catch (IOException e) {
	// === really not clear what to do if there's a problem writing ===
      }
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
      if (out != null) out.flush();
    } catch (IOException e) {
      // === really not clear what to do if there's a problem flushing ===
    }
  }
    
  /** Close the output stream.  Sets it to null. */
  public void close() {
    try {
      if (out != null) out.close();
    } catch (IOException e) {
      // === really not clear what to do if there's a problem flushing ===
    }
    out = null;
  }
    
  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TokenWriter() {
    super();
  }

  public TokenWriter(Writer s) {
    this();
    out = s;
  }


}
