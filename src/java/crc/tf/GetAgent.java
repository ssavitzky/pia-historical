// GetAgent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
  /**
   *Feature Computers.
   *All take a transaction as their argument, and most return a
   *boolean.  Feature computers may use the utility method
   *transaction->assert(name,value) to set additional features. 
   *
   *By convention, a feature computer "is_foo" computes a feature
   *named "foo". 
   *
   *Default Features: 
   *	These are computed by default when a transaction is created;
   *	they may have to be recomputed if the transaction is modified.
   *
   */
package crc.tf;

import crc.ds.UnaryFunctor;

public final class GetAgent implements UnaryFunctor{

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object trans ){
      String name = "agency";

      String url = trans.getRequestURL();
      if( !url ) return null;
      String path = url.getFile();
      if( !path ) return null;

      if( path.startsWith("/") ){
	StreamParser sp = new StreamParser( new StringBufferInputStream(path) );
	try{
	  Object o = sp.nextToken();  // first "/"
	  o = sp.nextToken();         
	  if( o instanceof String ){
	    name = (String)o;         // \w+ equivalent
	  }
	}catch(IOException e1){
	  e.printStackTrace();
	}catch(NoSuchElementException e2){
	  
	}
      }
      return name;
    }
}





