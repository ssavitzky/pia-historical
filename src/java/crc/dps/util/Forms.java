////// Forms.java:  Form utilities
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.dps.util;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
 
import crc.dps.*;
import crc.dps.active.*;
import crc.dps.output.*;
import crc.dps.input.*;

import crc.pia.FileAccess;

import crc.ds.Table;
import crc.ds.List;
import crc.ds.Association;

import crc.util.*;

import java.util.Enumeration;
import crc.util.Utilities;

import java.io.*;

/** Form-processing utilities.
 */
public class Forms  {
  public static void debug(String message) {
    if (debugging) {
      System.err.println(message);
    }
  }
  public static boolean debugging = false;

  /**
   * Boundary required for multipart form encoding.
   *	It has to be unique in a file, but that's easy because we're not 
   *	nesting multipart content (we hope).
   */
  public final static String multipartBoundary
    = "----------------------------7841312236133";

  /** Convert time attributes to a Table. */
  public static Table getTimes(ActiveAttrList itt) {
    Table t = new Table();
    for (int i = 0; i < timeAttrs.length; ++i)
      if (itt.hasTrueAttribute(timeAttrs[i]))
	{ t.put(timeAttrs[i], itt.getAttributeString(timeAttrs[i])); }
    return t;
  }


  /** Convert a form or an element of a form to a query string.
   */
  public static String formToQuery(ActiveElement it) {
    String query = "";

    if ("input".equalsIgnoreCase(it.getTagName())) {
      // generate query string for input
      query = it.getAttributeString("name");
      if (query == null || "".equals(query)) return "";
      query += "=";
      query += java.net.URLEncoder.encode(it.getAttributeString("value"));
      query += "&";		// in case there's a next one.
    } else if ("select".equalsIgnoreCase(it.getTagName())) {
      query = it.getAttributeString("name");
      if (query == null || "".equals(query)) return "";
      String selected = getSelected(it.getChildren());
      if (selected == null) return "";
      query += "=";
      query += java.net.URLEncoder.encode(getSelected(it.getChildren()));
      query += "&";		// in case there's a next one.
    } else if ("textarea".equalsIgnoreCase(it.getTagName())) {
      query = it.getAttributeString("name");
      if (query == null || "".equals(query)) return "";
      query += "=";
      query += java.net.URLEncoder.encode(it.contentString());
      query += "&";		// in case there's a next one.
    } else if (it.hasChildren()) {
      NodeList content = it.getChildren();
      NodeEnumerator nodes = content.getEnumerator();
      for (Node n = nodes.getFirst(); n != null; n = nodes.getNext()) {
	if (n instanceof ActiveElement) {
	  query += formToQuery((ActiveElement)n);
	  query += "&";
	}
      }
    }
    
    return trimQuery(query);
  }

  /** Return the value of the selected value, if any. */
  public static String getSelected(NodeList items) {
    if (items == null) return null;
    NodeEnumerator nodes = items.getEnumerator();
    for (Node n = nodes.getFirst(); n != null; n = nodes.getNext()) {
      if (n instanceof ActiveElement) {
	ActiveElement e = (ActiveElement)n;
	if (e.getTagName().equalsIgnoreCase("option")
	    && e.hasTrueAttribute("selected"))
	  return e.hasChildren()? e.getChildren().toString() : "";
      }
    }
    return null;
  }

  /** Convert a form to a multipart encoding
   * Currently can send only a single file
   *
   * As specified in RFC1867
   * http://info.internet.isi.edu:80/in-notes/rfc/files/rfc1867.txt
   */
  public static ByteArrayOutputStream formToMultipart(ActiveElement it) {

    // Initialize a stream to allow sending bytes directly,
    ByteArrayOutputStream partsByte = new ByteArrayOutputStream();

    // Add data from form into the stream
    partsByte = accumulateFormToMultipart(it, partsByte);

    // Finish off with a multipart boundary
    // (Note the end boundary has an extra -- at the end)
    PrintWriter partsString = new PrintWriter((OutputStream)partsByte);
    partsString.write("\r\n--" + multipartBoundary + "--\r\n");
    partsString.close();

    try {
      partsByte.flush();
    } catch (IOException e) {
      debug("Failed to flush");
    }

    return partsByte;
  }

  protected static ByteArrayOutputStream
    accumulateFormToMultipart(ActiveElement it,
			      ByteArrayOutputStream partsByte) {
    // Initialize a PrintWriter to allow sending strings
    PrintWriter partsString = new PrintWriter((OutputStream)partsByte);
      
    if ("input".equalsIgnoreCase(it.getTagName())) {

      // Check what kind of input this is
      String inputType = it.getAttributeString("type");
	
      // If the input type is not specified the default
      // assumption is that it is text
      if (inputType == null) {
	inputType = "text";
      }

	
      // Get the name, value fields
      String name = it.getAttributeString("name");

      // Do not consider unnamed input tags
      if (name == null || "".equals(name)) {
	// Submit/Reset tags are often unnamed, but this is OK
	// as we can ignore them anyway in that instance
	return partsByte;
      }

      String value = it.getAttributeString("value");
	
      // The only type which needs to be treated
      // differently is "file"
      if (inputType.equalsIgnoreCase("file")) {
	  
	// Stream for reading in files
	BufferedInputStream B;

	debug("Trying to read file "+value);
	    
	// The filename is in the value field
	java.io.File UploadFile;
	try {
	  UploadFile = new java.io.File(value);
	} catch (NullPointerException e) {
	  // If no file name was supplied, do not write anything
	  debug("Could not find file name");
	  return partsByte;
	}
	if (UploadFile.exists() && UploadFile.canRead()) {

	  // Initialize a stream to read the file
	  try {
	    B = new BufferedInputStream(new FileInputStream(UploadFile));
	  } catch (IOException e) {
	    debug("Could not open file "+value);
	    return partsByte;
	  }
	} else {
	  debug("Could not read file "+value);
	  // Do not write anything
	  return partsByte;
	}
	  

	debug("Read the file "+value);
	  
	// Try to guess the MIME type
	// If cannot be determined, used default value of application/octet-stream
	// as specified in the RFC
	String mimeType = FileAccess.contentType(value,"application/octet-stream");
	debug("Mime type of file set to "+mimeType);
	  
	// Write out header
	partsString.write("--" + multipartBoundary + "\r\n");


	// Transmit the filename if supplied (but not the path, following
	// Netscape 3)
	String UploadFileName = UploadFile.getName();

	String ContentDispositionHeader = "form-data; name=\"" + name + "\"";
	if (UploadFileName != null) {
	  ContentDispositionHeader += "; filename=\"" + UploadFileName + "\"";
	}

	// Write these headers out explicitly as the programmers at PhotoNet
	// seem to depend on this particular ordering (!!)

	partsString.write("Content-Disposition: "+ContentDispositionHeader+"\r\n");
	partsString.write("Content-Type: " + mimeType +"\r\n\r\n");
	  
	// Then append file
	if (mimeType.startsWith("text/")) {
	  // as text
	  partsString.write("\n");
	  // TO DO
	    
	  partsString.close();

	} else {
	  // as binary
	  partsString.close();
	  int b;
	  while (true) {
	    try {
	      b = B.read();
	      if (b == -1) {
		break;
	      } else {
		partsByte.write(b);
	      }
	    } catch (IOException e) {
	      break;
	    }
	  }

	  // Close up the file
	  try {
	    B.close();
	  } catch (IOException e) {
	    debug("Could not close file "+value);
	    return partsByte;
	  }

	  debug("Written out binary file");
	}

      } else {
	 
	String inputTypeLower = inputType.toLowerCase();
	if (inputTypeLower.equals("text") ||
	    inputTypeLower.equals("checkbox") ||
	    inputTypeLower.equals("radio") ||
	    inputTypeLower.equals("submit") ||
	    inputTypeLower.equals("reset") ||
	    inputTypeLower.equals("hidden") ||
	    inputTypeLower.equals("password")) {
	      
	  // For the input types listed above, it makes sense
	  // to send the value field as text
	    
	  // Pia.debug("Writing out " + inputType + " field: "+name+" "+value);
	  partsString.write("--" + multipartBoundary + "\r\n");
	  String ContentDispositionHeader = "form-data; name=\"" + name + "\"";
	  partsString.write("Content-Disposition: "
			    + ContentDispositionHeader+"\r\n\r\n");
	  partsString.write(value + "\r\n");
	} else {
	  // Pia.error(this, "Cannot handle INPUT field of type "+inputType);
	  // Write nothing
	}	    
	partsString.close();
      }
    } else if (it.hasChildren()) {
      NodeList content = it.getChildren();
      NodeEnumerator nodes = content.getEnumerator();
      for (Node n = nodes.getFirst(); n != null; n = nodes.getNext()) {
	if (n instanceof ActiveElement) 
	  partsByte = accumulateFormToMultipart((ActiveElement)n, partsByte);
      }
    }
    return partsByte;
  }

  
  /** trim an extraneous &amp; from the end of a query string. */
  protected static String trimQuery(String query) {
    if (query.endsWith("&")) 
      query = query.substring(0, query.length()-1);
    return (query);
  }

  /** Attributes that determine a timed submission */
  protected static String timeAttrs[] = {
    "repeat", "until", "hour", "minute", "day", "month", "year", "weekday" 
  };
    
  /** Determine whether a form is timed. */
  public static boolean containsTimedSubmission(ActiveAttrList it) {
    for (int i = 0; i < timeAttrs.length; ++i) {
      if (it.hasTrueAttribute(timeAttrs[i])) return true;
    }
    return false;
  } 

}

