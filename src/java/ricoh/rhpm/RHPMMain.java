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
package ricoh.rhpm;

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

public class RHPMMain {
    public static void main (String args[]) {
	RHPMMain maindude= new RHPMMain(args);
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
    public RHPMMain(String args[]) {
	//** Any HTML document you can point your system at;  this is the document you will be annotating so make sure it contains something interesting
	String documentFileName="/proxyserver/profiles/testdoc.html";
	//** If using windows, set this to the drive letter; otherwise set it to nada

	// String driveLetter="e:"; 
	// changed pg.  This was showing up and causing problems
	String driveLetter=""; 
	//** Simply the url string to match the above file;  this is not used explicitly; instead it is inserted in the newly created annotated file
	String urlstr="file:/"+documentFileName;

	/** 
	 * This is an IMPORTANT step: these concept names must match the concept "short" names read from the concepts file.
	 * Below I am using five concepts as the current set of "active" concepts (only their phrases will be searched for in the doc)
	 */
	String conceptsList="Agents Java Interface ExpSys NLP";
	//** I insert in the header of an annotated document the version of the program that generated it;  this is typically sent form the client
	String version="test version";
	//** Setup some stuff which would have been provided by the client

	// String currentUser="jamey";
	// changed pg
	String currentUser="pgage";

	//** Create the agent
	RHPMAgent agent=new RHPMAgent();

	//** Figure out if we have a requested file or not;  otherwise use the default file
	if (args.length>0) {
	    documentFileName=args[0];
	    urlstr="file:/"+documentFileName;
	    //** if you want to change the drive letter, include it as the second parameter
	    if (args.length>1) driveLetter=args[1];
	    System.out.println("***-Requested Filename: "+driveLetter+documentFileName);
	}
	else System.out.println("***-Using default Filename: "+documentFileName);
	System.out.println("***");

	//** Let's make sure the file exists...
	File file=new File(driveLetter+documentFileName);
	if (file.exists()) {
	//** Run the agent's test method
	    System.out.println("currentUser: " + currentUser);
	    System.out.println("docFile: " + driveLetter+documentFileName);
	    System.out.println("urlstr: " + urlstr);
	    System.out.println("conceptsList: " + conceptsList);
	    System.out.println("version: " + version);
	    // agent.testMethod(currentUser,driveLetter+documentFileName,urlstr,conceptsList,version);
	    // changed pg
	    byte[] resultBuf = agent.testMethod(currentUser,driveLetter+documentFileName,urlstr,conceptsList,version);
	}
	else System.out.println("***-Aborting... could not file file: "+file.toString());
    }
}
