////// Msg.java: Error or debugging message
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.util;

/**
 * A message to the user.
 *
 *	Ideally there should be a package-local BasicMessage class, a
 *	MessageFactory interface, and a GenericMessageFactory class.
 *	It seems likely that this class will be used a lot, however. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.util.Report
 */

public class Msg extends Severity implements Message {

  /************************************************************************
  ** State variables and access functions.
  ************************************************************************/

  protected String message = null;
  public String getMessage() { return message; }
  public void setMessage( String aString ) { message = aString; }

  protected String fileName = null;
  public String getFileName() { return fileName; }
  public void setFileName( String value ) { fileName = value; }

  protected int line = 0;
  public int getLine() { return line; }
  public void setLine(int anInt) { line = anInt; }

  protected int severity = 0;
  public int getSeverity() { return severity; }
  public void setSeverity(int anInt) { severity = anInt; }

  protected int indent = 0;
  public int getIndent() { return indent; }
  public int setIndent(int anInt) { indent = anInt; }

  protected boolean noNewline = false;
  public boolean noNewline() { return noNewline; }
  public void setNoNewline( boolean value );

  /************************************************************************
  ** Basic message reporting:
  ************************************************************************/

  public String toString() {
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    if (fileName != null) {
      s += fileName + ":" + line + ": ";
    }
    s += message;
    if (! noNewline) s += "\n";
    return s;
  }

  /************************************************************************
  ** Basic Construction:
  ************************************************************************/

  public Msg() {}

  public Msg(String message) { this.message = message; }

  public Msg(int severity, String message) {
    this.severity = severity;
    this.message  = message;
  }

  public Msg(int severity, String message, int indent, boolean noNewline ) {
    this.severity = severity;
    this.message  = message;
    this.indent   = indent;
    this.noNewline= noNewline;
  }

  public Msg(int severity, String message, String fileName, int line ) {
    this.severity = severity;
    this.message  = message;
    this.fileName = fileName;
    this.line     = line;
  }

  /************************************************************************
  ** Factory Methods:
  ************************************************************************/

  public Message fatal(String message) {
    return new Msg(FATAL, message);
  }

  public Message internal(String message) {
    return new Msg(INTERNAL, message);
  }

  public Message internal(Exception e) {
    // === need to put out the traceback, too. ===
    return new Msg(INTERNAL, e.getMessage());
  }

  public Message error(String message) {
    return new Msg(ERROR, message);
  }
  public Message error(String message, String fileName, int line) {
    return new Msg(ERROR, message, fileName, line);
  }

  public Message warning(String message) {
    return new Msg(WARNING, message);
  }
  public Message warning(String message, String fileName, int line) {
    return new Msg(WARNING, message, fileName, line);
  }

  public Message message(String message) {
    return new Msg(MESSAGE, message);
  }
  public Message verbose(String message) {
    return new Msg(VERBOSE, message);
  }


  public Message debug(String message) {
    return new Msg(DEBUG, message, 0, true);
  }

  public Message debug(String message, int indent) {
    return new Msg(DEBUG, message, indent, true);
  }

  public Message debug(String message, String fileName, int line) {
    return new Msg(DEBUG, message, fileName, line);
  }

}


