/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 1.15.99
 *
 */
package ricoh.rhed;

import crc.pia.PiaRuntimeException;
import crc.pia.GenericAgent;
import crc.pia.FormContent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.Content;
import crc.pia.FileAccess;
import crc.pia.HTTPResponse;

import java.net.*;
import java.net.MalformedURLException;

import java.io.*;

import w3c.www.http.HTTP;

import ricoh.rh.RH_GlobalVars;

public class RHEditor extends JFrame {
    public static void main (String args[]) {
	RHEditor maindude= new RHEditor(args);
    }
    
    /**
     * Test class for running RHPMAgent;
     * Syntax: java ricoh.rhpm.RHPMMain [filename [driveletter]]
     *
     * Ex1: java ricoh.rhpm.RHPMMain
     * --- this simply runs the system with the default values specified below
     * Ex2: java ricoh.rhpm.RHPMMain /proxyserver/profiles/foobarbaz.html
     * --- this runs the system using a different HTML file in the pattern matcher
     * Ex3: java ricoh.rhpm.RHPMMain /proxyserver/profiles/foobarbaz.html c:
     * --- this runs the system using a different HTML file in the pattern matcher on a different drive
     */
    public RHEditor(String args[]) {

	//** Create the agent
	RHPMAgent agent=new RHPMAgent();

	//** Figure out if we have a requested file or not;  otherwise use the default file
	if (args.length>0) {
	}
    }
}
