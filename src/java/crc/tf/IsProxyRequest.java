// IsProxyRequest.java
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
import crc.pia.Pia;
import crc.pia.Transaction;
import java.net.URL;

public final class IsProxyRequest implements UnaryFunctor{

  /**
   * Is this request a proxy request; not directed at the agency
   * @param o Transaction 
   * @return true if host part of the request does not start with "agency", equals to "", or
   * Pia's host and port are not the same as those of the request.
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      Object zfalse = new Boolean( false );
      Object ztrue  = new Boolean( true );
      String lhost = null;

      if( !trans.isRequest() ) return zfalse;
      URL url = trans.requestURL();
      if( url == null ) return zfalse;
      
      String host = url.getHost();
      if( host != null ) 
	lhost = host.toLowerCase();
      else
	lhost = "";
      
      if( lhost.startsWith("agency") || lhost.equals("") )
	return zfalse;
      
      // === Sometimes url.getPort() returns -1 --  probably means it's missing.

      int lport = url.getPort();
      if (lport == -1) lport = 80;

      if( (Pia.instance().portNumber() == lport
	   || Pia.instance().realPortNumber() == lport)
	  && (Pia.instance().host().startsWith( lhost )
	      || lhost.startsWith(Pia.instance().host())
	      || lhost.equals("localhost")))
	return zfalse;

      return ztrue;
    }
}


