// IsHtml.java
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

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsHtml implements UnaryFunctor{

  /**
   * Is this transaction's content type "text/html". 
   * @param o  Transaction 
   * @return true if  content type is "text/html"
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      String s = trans.contentType();
      if( s != null ){
	String ls = s.toLowerCase();
	if( ls.startsWith("text/html") )
	  return new Boolean( true );
      }
      return new Boolean( false );
     
    }
}



