// Admin.java
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


/**
 * This is the class for the ``Admin'' agent; i.e. the one that
 *	performs PIA system administration.  It contains the specialized
 *	code that installs and removes agents.
 */

package crc.pia.agent;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Enumeration;

import java.net.URL;

import crc.ds.Table;
import crc.ds.List;
import crc.ds.Criterion;

import crc.pia.GenericAgent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPRequest;

public class Admin extends GenericAgent {
  /**
   * Uninstall (unRegister) an agent.
   */
  public void unInstallAgent(String name){
    Pia.instance().resolver().unRegisterAgent( name );
  }

  /**
   * Install (register) an agent.
   */
  public void installAgent(Agent newAgent){
    Pia.instance().resolver().registerAgent( newAgent );
  }

  /**
   * Create and install a named agent.
   *	Automatically loads the class if necessary.
   *	@param ht initial options.
   *	@exception crc.pia.agent.AgentInstallException if problems are
   *	  found, for example a null table or missing parameter.
   */
  public void install(Table ht)
       throws NullPointerException, AgentInstallException {

    if( ht == null ) throw new NullPointerException("bad parameter Table ht\n");
    String name      = (String)ht.get("agent");
    String type      = (String)ht.get("type");
    String className = (String)ht.get("class");

    if (name == null || name.equals(""))
      throw new AgentInstallException("No agent name");

    if (type == null || type.equals(""))
      type = name;

    /* Compute a plausible class name from the type. */

    if (className == null){
      char[] foo = new char[1]; 
      foo[0] = type.charAt(0);

      // Capitalize name.  
      //	=== Should preserve case in rest of agent name ===
      //	=== should use interform.Util.javaName ===
      String zname = (new String( foo )).toUpperCase();
      if (type.length() > 1) zname += type.substring(1).toLowerCase();
      className = "crc.pia.agent." + zname; 
    } else if (className.length() > 0 && className.indexOf('.') < 0) {
      className = "crc.pia.agent." + className; 
    }

    /* Load the class, if it exists. */

    Agent newAgent = null;
    if( className != null && className.length() > 0){
      try{
	newAgent = (Agent) (Class.forName(className).newInstance()) ;
	newAgent.name( name );
	newAgent.type( type );
      }catch(Exception ex){
      }
    }

    /* If the class doesn't exist, use GenericAgent. */

    if (newAgent == null) newAgent = new GenericAgent(name, type);

    /* Install and initialize the new agent.  The Resolver actually 
       does the intialization, after installing the agent.
     */

    newAgent.parseOptions(ht);
    installAgent( newAgent );
  }
  /**
   * Constructor.
   */
  public Admin(String name, String type){
    super(name, type);
  }

  /** Default constructor. */
  public Admin() {
    super();
  }

  /**
   * initialize 
   */
  public void initialize() {
    if (initialized) return;
    super.initialize();
  }

}





















