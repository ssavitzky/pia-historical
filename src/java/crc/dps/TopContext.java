////// TopContext.java: Top Context interface
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


package crc.dps;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URL;

/**
 * The interface for a top context.
 *
 *	A top context is the root of a document-processing Context stack. 
 *	As such, it contains the tagset, global entity table, and other
 *	global information. <p>
 *
 *	Note that there may be more than one top context in a stack; this may
 *	be done, for example, in order to insert a sub-document into the
 *	processing stream.  Even the ``root'' context may have a parent.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public interface TopContext extends Processor {

  /************************************************************************
  ** State accessors:
  ***********************************************************************/

  /** Obtain the current Tagset. */
  public Tagset getTagset();

  /** Set the current Tagset. */
  public void setTagset(Tagset bindings);

  /** Obtain the current ProcessorInput.  
   *
   *	When processing a stream, this will be a Parser, and it is possible
   *	(though slightly risky) to change the tagset being used to parse the
   *	document.  When processing a parse tree this may return null.
   */
  public ProcessorInput getProcessorInput();

  /************************************************************************
  ** Input and Output:
  ************************************************************************/

  /** Registers an Input object for the Processor.  
   */
  public void setInput(Input anInput);

  /** Registers an Output object for the Processor.  
   */
  public void setOutput(Output anOutput);

  /************************************************************************
  ** External Entities:
  ************************************************************************/

  /** The root URL of the document -- the server where it is located. 
   *
   * <p> This is null if the document is located on the same host as the
   *	 DPS process, i.e. the document is accessible as a file.
   */
  public URL getDocumentLocation();

  /** The file path of the current document relative to its location
   *	(i.e., the result of <code>getDocumentLocation</code>).
   *
   * <p> This will always be a file path (starting with ``<code>/</code>'') 
   *	 following the URL convention of forward slashes for separators.
   *	 In all cases it will end with a ``<code>/</code>''. 
   *
   * <p> Inside a PIA or other server the base path is need not be a path 
   *	 from the filesystem root, but may be relative to the server's
   *	 document root instead.
   */
  public String getDocumentBase();

  /** The file name of the current document. 
   *	May be null if the current document is a string. 
   */
  public String getDocumentName();

  /** Read from a resource. 
   *	The given path always uses ordinary (forward) slashes as file
   *	separators, because it is really a URI.  If a protocol and host
   *	are not specified, the path is relative to some implementation-
   *	dependent origin if it starts with '<code>/</code>', otherwise
   *	it is relative to the start of the document being processed.
   */
  public InputStream readExternalResource(String path)
    throws IOException;

  /** Write to a resource. 
   * @param path a path.
   * @param append append to an existing resource. 
   * @param createIfAbsent if no resource of the given name exists, create one
   * @param doNotOverwrite do not overwrite an existing resource
   */
  public OutputStream writeExternalResource(String path, boolean append,
					    boolean createIfAbsent,
					    boolean doNotOverwrite)
    throws IOException;

  /** Locate a resource accessible as a file. */
  public File locateSystemResource(String path, boolean forWriting);

  /** Locate a resource accessible as a URL. */
  public URL locateRemoteResource(String path, boolean forWriting);


  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  /** Load a Tagset by name. 
   * @param tsname the tagset name.  If null, returns the current tagset. 
   */
  public Tagset loadTagset(String tsname);

  /** Process a new subdocument. 
   * 
   * @param in the input.
   * @param cxt the parent context. 
   * @param out the output.  If null, the parent context's output is used.
   * @param ts the tagset.  If null, the current tagset is used.
   */
  public TopContext subDocument(Input in, Context cxt, Output out, Tagset ts);

  /************************************************************************
  ** Message Reporting:
  ************************************************************************/

  public void setLog(PrintStream log);

}
