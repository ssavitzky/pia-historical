// IsAgentRequest.java
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
import crc.pia.Transaction;
import crc.pia.Pia;
import crc.tf.TFComputer;

public final class IsAgentRequest extends TFComputer {
  /**
   * Is this transaction a request for the agency
   * @param object A transaction 
   * @return Boolean true if the host part of a request url starts with "agency" or
   * host part and port  equals those of the Pia's hostname and port number otherwise false.  
   */

  public Object computeFeature(Transaction trans){

    String lhost = null;
    
    if( !trans.isRequest() ) return False;
    URL url = trans.requestURL();
    if( url == null ) return False;
    
    String host = url.getHost();
    if( host!= null ) 
      lhost = host.toLowerCase();
    else
      lhost = "";

    if( lhost.startsWith("agency") || lhost.equals(""))
      return True;

    // === Sometimes url.getPort() returns -1 --  probably means it's missing.

    int lport = url.getPort();
    if (lport == -1) lport = 80;

    if( (Pia.instance().portNumber() == lport
	 || Pia.instance().realPortNumber() == lport)
	&& (Pia.instance().host().startsWith( lhost )
	    || lhost.startsWith(Pia.instance().host())
	    || lhost.equals("localhost"))){
      return True;
    }
    
    return False;
  }
}












