// Sign.java:  Handler for <sign>
// $Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.sgml.SGML;

import java.io.InputStream;
import java.util.Hashtable;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.*;


/** Handler class for &lt;sign&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#hash">Manual
 *	Entry</a> for syntax and description.
 * For now, the keystore is shared across all agents and associated with this class.
 * versions of JDK prior to 1.2 cannot use this class thus the sign actor will
 * not be available, but everything else should work.
 */

public class Sign extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<sign user=user [[password=password [create]] | [verify=signature] | [exists]] [keyfile=filename [kp=kp]]>content</sign>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Replace CONTENT with a digital signature of CONTENT using the private key for USER verified with PASSWORD.\n" +
  " if CREATE creates a key pair for user and replaces CONTENT with public key.\n" +
  " if VERIFY verifies  SIGNATURE of CONTENT by USER -- replaces CONTENT with false if signature does not verify, true otherwise. \n" +
  " if EXISTS replaces content with true if USER keys exists \n" +
  " FILENAME specifies location for keyfile -- defaults to usrDIR/.keys\n" +
  " KP specifies password for private key file\n" +
"";


  /**
   * the private key storage
   */
  protected static KeyStore privatekeys = null;

/**
   * the public key storage
   */
  protected static Hashtable publickeys = null;
  /**
   * the file for keys
   */
 protected static String currentkeyfile = null;

  /**
   * utility function for managing files associated with keys
   */
  protected static void checkKeys(String file, String pass){
    String pub = file;
    pub += ".public";
    String privvy = file + ".private";
    try{
      if(privatekeys == null ) privatekeys = KeyStore.getInstance();
    } catch(Exception e){
      System.out.println("cannot initialize keystore");
      publickeys = null;
      currentkeyfile = null;
      return;
    }

   try{
     if(!file.equals(currentkeyfile) | publickeys == null){
	publickeys = (Hashtable) crc.util.Utilities.readObjectFrom(pub);
      }
      InputStream is = new FileInputStream(privvy);
      privatekeys.load(is,pass);
      is.close();
    } catch(FileNotFoundException e){
      System.out.println("initializing key database");
      publickeys = new Hashtable();
    } catch(Exception e){
      // set FormUser to null and abort
      publickeys = null;
      currentkeyfile = null;
       return;
    }
    currentkeyfile = file;
  }

  // put keys in files
  protected static void checkpointKeys(String file, String pass){
    String pub = file + ".public";
    String privvy = file + ".private";
    try{
      crc.util.Utilities.writeObjectTo(pub,publickeys);
      
      OutputStream os = new FileOutputStream(privvy);
      privatekeys.store(os,pass);
      os.close();
    } catch(Exception e){
      // set FormUser to null and abort
      // should give user message...
      crc.pia.Pia.warningMsg( " checkpoint of key files failed "+e.toString());
      return;
    }
    currentkeyfile = file;
  }


  public void handle(Actor ia, SGML it, Interp ii) {
    String toSign = it.contentString();
      // don't LET ANYONE CHANGE KEYS while we use them
    String result = "";
    String user=Util.getString(it,"user",null);
    if(user == null){
      if( ii != null){ 
	ii.error(ia,"no user specified in Sign");
	ii.replaceIt("");
      }
      return;
    }
    String dflt = ".keys";
    if(ii != null) dflt = crc.pia.Pia.instance().usrRoot() + "/.keys";
    
    String keyfile=Util.getString(it,"keyfile",dflt);
    String keypass=Util.getString(it,"kp","AgencyKeys");
    //name+.private or +.public
      
    checkKeys(keyfile,keypass);
    
    if(publickeys == null) {
      if(ii != null){ ii.error(ia,"no public keys");
      ii.replaceIt("");
      }
      return;
    }

    synchronized(privatekeys){

      try{
	if (it.hasAttr("create")) {
	  String password=Util.getString(it,"password",user);
	  KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
	  keyGen.initialize(1024);
	  KeyPair keys = keyGen.genKeyPair();
	  
	  Identity publicI = (Identity) publickeys.get(user);
	  if(publicI == null) {
	    publicI = new  FormUser(user);
	    publickeys.put(user,publicI);
	  }
	  publicI.setPublicKey(keys.getPublic());
	  privatekeys.setKeyEntry(user,keys.getPrivate(),password,new java.security.cert.Certificate[0]);
	  checkpointKeys(keyfile,keypass);
	  byte[] buf = keys.getPublic().getEncoded();
	  result = crc.util.Utilities.encodeBase64(buf); 
	  
	}else if (it.hasAttr("exists")) {
	  if(privatekeys.containsAlias(user))
	    result="true";
	}else if (it.hasAttr("verify")) {
	  String sigString = Util.getString(it,"verify",null);
	  Signature dsa = Signature.getInstance("SHA/DSA"); 
	  Identity i = (Identity) publickeys.get(user);
	  if(i != null){
	    PublicKey key= i.getPublicKey();
	    dsa.initVerify(key);
	    byte[] sig = crc.util.Utilities.decodeBase64(sigString); 
	    dsa.update(toSign.getBytes());
	    if(dsa.verify(sig))
	      result = "true";
	  }
	} else {
	  //sign the Content
	  String password=Util.getString(it,"password",user);
	  
	  PrivateKey key = privatekeys.getPrivateKey(user,password);
	  Signature dsa = Signature.getInstance("SHA/DSA"); 
	  dsa.initSign(key);
	  dsa.update(toSign.getBytes());
	  byte hash[] = dsa.sign();
	  result = crc.util.Utilities.encodeBase64(hash);
	}
      } catch(Exception e){
	if(ii != null) ii.error(ia,"Exception in Sign"+e.toString());
	else {
	  System.out.println("Exception in Sign"+e.toString());
	  e.printStackTrace();
	}
      }
    }
    
    if(ii != null) ii.replaceIt(result); else System.out.println(result);

  }

  public static void main(String[] args){
    // keyfile keypass user [passwd | -s sigtoverify] [message]
    Sign s = new Sign();
      System.out.println("args length is" + args.length);
    crc.sgml.Element it = new crc.sgml.Element();
    it.attr("keyfile",args[0]);
    it.attr("kp",args[1]);
    it.attr("user",args[2]);
    if(args.length == 3){
      it.attr("exists","");
    }
    if(args.length == 4){
      it.attr("password",args[3]);
      it.attr("create","");
    } else if(args.length == 6){
      it.attr("verify",args[4]);
      it.content(new crc.sgml.Text(args[5]));
    } else if(args.length == 5){
      it.attr("password",args[3]);
      it.content(new crc.sgml.Text(args[4]));
    }
          System.out.println("handling" + it);
    s.handle(null,it,null);
    System.out.println("result:" + it);
  }
}


/**
 * instantiate an  identity class
 */


class FormUser extends java.security.Signer
{

				//  inherit everything for now
  // currently private keys maintained separately
  public FormUser(){
  }
  public FormUser(String  name) {
    super(name);
  }

}
