////// NodeType.java: Document Processor basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

/** Node types and their names.
 *	
 * <p> This class mainly exists to compensate for the lack of enumerated
 *	types in Java.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 */

public class NodeType {
  public static final int DOCUMENT   = 0;
  public static final int ELEMENT    = 1;
  public static final int ATTRIBUTE  = 2;
  public static final int PI         = 3;
  public static final int COMMENT    = 4;
  public static final int TEXT       = 5;
  public static final int ENTITY     = 6;

  public static final int ENDTAG = -1;
  public static final int NODELIST = -2;
  public static final int DECLARATION = -3;
  public static final int ALL = -4;
  public static final int UNDEFINED = -5;

  public static final int MIN_TYPE = -4;
  public static final int MAX_TYPE = ENTITY;

  public static final String names[] = {
    "ALL",	"DECLARATION", 	"NODELIST",	"ENDTAG", 
    /* 0... */ 	"DOCUMENT",	"ELEMENT", 	"ATTRIBUTE",
    "PI",	"COMMENT",	"TEXT",		"ENTITY",
  };

  public static String getName(int type) {
    return names[type-MIN_TYPE];
  }
  public static int getType(String name) {
    for (int i = 0; i < names.length; ++i) 
      if (name.equalsIgnoreCase(names[i])) return (i + MIN_TYPE);
    return UNDEFINED;
  }
}
