// IsInterForm.java
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

public final class IsInterform implements UnaryFunctor{

  /**
   * Is this transaction's request a request for interform
   * @param o Transaction 
   * @return true if the URL's file portion ends in ".if"
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      URL url = trans.requestURL();
      if( url != null ){
	String path = url.getFile();
	String lpath = path.toLowerCase();
	if( path.endsWith(".if") )
	  return new Boolean( true );
	else
	  return new Boolean( false );
      }else return new Boolean( false );



    }
}



