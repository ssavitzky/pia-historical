// Title.java
// $Id$

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


 
package crc.tf;

import java.net.URL;
import crc.ds.UnaryFunctor;
import crc.pia.Transaction;
import crc.pia.Content;

import crc.tf.TFComputer;

public final class Title extends TFComputer {
  private String getPage(Transaction trans){
    Content c = trans.contentObj();
    if( c != null )
      return c.toString();
    else
      return null;
  }

  /**
   * Returns the title element of this transaction's content document.
   *	Returns null if the transaction is not a response, and the
   *	document's path if it is not text.
   */ 
  public Object computeFeature(Transaction trans) {

      if(!trans.isResponse()) return "";

      /* we will use the path as a fallback. */

      URL url = trans.requestURL();
      if( url == null ) return "";
      String path = url.getFile();
      if( path == null ) path = "";

      String type = trans.contentType();
      if( type == null ) return "";

      String ltype = type.toLowerCase();
      if (! ltype.startsWith("text/html")) return path;

      String mypage = getPage( trans );
      if( mypage == null ) return path;

      String page = mypage.toLowerCase();

      String title = null;
      int pos = page.indexOf("<title>");
      int endPos = page.indexOf("</title>");
      if( pos != -1 && endPos != -1 )
	title = mypage.substring(pos+"<title>".length(), endPos);
      return title;
      
    }

}













