// Authenticator.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;

import crc.pia.GenericAgent;
import crc.pia.Transaction;
import crc.pia.HTTPRequest;
import crc.pia.HTTPResponse;
import crc.pia.Machine;
import crc.ds.Table;

import crc.util.Utilities;
import java.io.Reader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;

import misc.Jcrypt;

/**
 * implements the authentication mechanism for agent transactions.
 * typically uses basic authentication to verify user name and password
 */

public class Authenticator implements java.io.Serializable{

  // stores the type of authentication
  private String authType;

  // initial implementation does only basic authentication
  // should expand to other types such as digest

  // keep table of secret data e.g. file names or password table
  private Table attributes = new Table();
  
  public Authenticator(String type){
    authType = type;
    initialize();
  }

  /**
   * return a new authenticator of type using password file for verification
   */

  public Authenticator(String type, String passwordFile){
    authType = type;
    attributes.at("passwordFile", passwordFile);
    initialize();
  }


  private void initialize(){
    attributes.at("users", new Table());// hold passwords
  }

  /**
   * authenticate the given request -- eventually this will be complicated 
   * by different authentication methods and levels of service
   * for now simply require a username and password
   */

  public boolean authenticateRequest( Transaction request, String requestedFile, Agent agent){

    // dispatch should be based on authType --  for now basic is only type
     return authenticateRequestBasic(request,requestedFile, agent);
  }

  public boolean authenticateRequestBasic(Transaction request, String requestedFile, Agent agent){
    String authorization = request.header("authorization");
    if(authorization == null) return false;
    if( authorization.startsWith("Basic ") || authorization.startsWith("basic ") )  
      authorization =  authorization.substring(6); 
    else System.out.println(" unknown authentication method " + authorization);
      Pia.debug(this, "checking authorization");
    if ( authorization == null) return false;
	
    String decoded = Utilities.decodeBase64(authorization);

    if (decoded == null) return false;
    String user=null, password=null; 
    int icolon = decoded.indexOf (':') ;
    if ( (icolon > 0) && (icolon+1 <= decoded.length()) ) {
      user     = decoded.substring (0, icolon) ;
      if(icolon+1 < decoded.length()) 
	password = decoded.substring (icolon+1) ;
    } else if (icolon < 0 ) {
       user = decoded;
    }

    if(verifyPassword(user, password)){
      request.assert("AuthenticatedUser", user);	
      request.assert("AuthenticationMethod", "Basic");	
      return true;
    }  else {
      // should return403 e.g.  throw authentication exception
      // for now just return false
      request.assert("UnauthenticatedUser", user);  
      return false;
    }
    // should not get here
  }

  private boolean verifyPassword (String user, String password){
    Table users =(Table) attributes.at("users");
    String secret =(String) users.at(user);
    if(secret != null){
      String salt =  secret.substring(0, 2);
      if(secret.equals(Jcrypt.crypt(salt, password))) return true;
    }
    // try to load from password file
    String file = (String) attributes.at("passwordFile");
    if(file != null){
      Reader pwfile = null;

      try {
	pwfile = new FileReader(file);
      } catch (IOException e) {
	System.out.println(" failed to open file");
	return false;
      }
    
      LineNumberReader rd = new LineNumberReader(pwfile);
      String line  = null;
      String entry = null;

      for ( ; ; ) {
	try {
	  line = rd.readLine();
	} catch (IOException e) {
	  line = null;
	}
	if (line == null) return false;
	if (line.startsWith(user+":")) break;
      }
      line = line.substring(user.length() + 1);
      if (line.indexOf(":") >= 0) {
	entry = line.substring(0, line.indexOf(":"));
      } else {
	entry = line;
      }
      
      if (entry.length() == 0 && password.length() == 0) return true;
      if (entry.length() < 3) return false;
      
      String salt = entry.substring(0, 2);
      if(!entry.equals(Jcrypt.crypt(salt, password))){
	return false;
      } else {
	users.at(user,entry);  //cache for next time
	 return true;
      }
    }
    return false;
  }

  /**
   * set headers of the response
   */
  public void setResponseHeaders(Transaction response, Agent agent){
    // should dispatch on type for now assume basic
     response.setHeader("WWW-Authenticate", "Basic realm=\"" + agent.name() +"\"");
  }
			//WWW-Authenticate: Basic realm="AgentName"

}


