// ContentFactory.java
// $Id$
// COPYRIGHT 1997 Ricoh Silicon Valley.


package crc.pia;
import crc.ds.Table;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import crc.content.ByteStreamContent;
import java.net.ContentHandler;

/**  ContentFactory
 * creates an appropriate content object according to given Mime Type.
 * This factory has a table that maps mime types to content class names.
 * Given a mime type this factory will look for an associate content class
 * name, creates, and returns it.  If no entry is found for a mime type,
 * the content class will be searched according to a package stated in
 * the base-mime type of the given mime type.  Thus, "text/html" would lead
 * to a search for html class in text package.  If no base-mime type is 
 * specified, a search for the content class will be done in the 
 * crc.pia.content package 
 */
public class ContentFactory extends ContentHandler
{
 /**  creates a new content factory with default list of possible content objects
 */

  public static String TEXT_HTML           = "text/html";
  public static String TEXT_PLAIN          = "text/plain";
  public static String IMAGE_GIF           = "image/gif";

  protected static String CONTPACKPREFIX = "crc.content";
  protected static String TEXTPACKPREFIX = CONTPACKPREFIX + ".text";
  protected static String IMGPACKPREFIX  = CONTPACKPREFIX + ".img";
  protected static String DEFAULT        = "Default";
 /**
  * Associations of mime-types to content-type names
  */
  protected static Table contentTypeTable = new Table();

  /**
   * cache entry result from loading from package
   */
  protected String cacheEntry = null;

  static{
    contentTypeTable.at( TEXT_HTML, TEXTPACKPREFIX + ".html");
    contentTypeTable.at( TEXT_PLAIN, TEXTPACKPREFIX + ".plain");
    contentTypeTable.at( IMAGE_GIF, IMGPACKPREFIX + ".gif");
  }
  
  public ContentFactory()
  {
  }

  public Object getContent(java.net.URLConnection url){
    return null;
  }

  String getParentName( String mimeType )
  {
    // search for / and return whatever previous from
    // the /
    int pos;
    String parent;

    if ((pos = mimeType.indexOf('/')) == -1 )
      return null;
    else
      parent = mimeType.substring(0, pos);
    return parent.toLowerCase();
	
  }

  String getName( String mimeType )
  {
    // search for / and return whatever after from
    // the /
    int pos;
    String name;
    
    if ((pos = mimeType.indexOf('/')) == -1 )
      return null;
    else
      name = mimeType.substring( pos+1 );
    return name.toLowerCase();
  }


  Object factory( String p ) 
       throws ClassNotFoundException,
    InstantiationException,
    IllegalAccessException
  {
    Class c;
    Object o = null;

    Pia.debug(this, "Inside factory");

    try {
      c = Class.forName(p);// get class def
      o = c.newInstance(); // make a new one
      Pia.debug(this, "Creating " + p + " is successful.");
    } catch (ClassNotFoundException nfe) {
      // either class not found,
      
      //nfe.printStackTrace();
      
      throw nfe;
    } catch( InstantiationException ie ){
      // class is interface/abstract
      // class or initializer is not accessible.
      //ie.printStackTrace();
      
      throw ie;
      
    } catch( IllegalAccessException ile ){
      // class or initializer is not accessible.
      //ile.printStackTrace();
      
      throw ile;
     
    } 
    return o;
}

  /**
   * Create content from parent mime type
   */
  public Content makeContentFromDef( String mimeType )
  {
    Content c = null;
    String className;

    Pia.debug(this, "Inside makeContentFromDef");

    String parent = getParentName( mimeType );
    if ( parent == null ){
      Pia.debug(this, "parent name is null ... returning");
      return null;
    }
    else{
      className = CONTPACKPREFIX + "." + parent + "." + DEFAULT;
      Pia.debug(this, "Loading base on a default content-->"+ className);
      try{
	c = (Content) factory( className );

	Pia.debug(this, "Loading is successfull");
  
	return c;
      }catch(Exception e){
	//e.printStackTrace();
	return null;
      }
    }
  }


  /**
   * Create content from package
   */
  public Content makeContentFromPackage( String mimeType )
  {

    Pia.debug(this, "Inside makeContentFromPackage");

    Content c = null;
    String name = getName( mimeType );
    String parent = getParentName( mimeType );

    if ( parent == null || name == null )
      return null;
    else{
      String className = CONTPACKPREFIX + "." + parent + "." + name;
      Pia.debug(this, "Loading base on a package content-->"+ className);

      try{
	c = (Content)factory( className );
	cacheEntry = className;

	Pia.debug(this, "Loading is successfull");

      }catch(Exception e){
	e.printStackTrace();
	c = makeContentFromDef( mimeType );
      }
      finally{
	return c;
      }
    }


  }



  /**
   * create a content from table
   *
   */
  public Content makeContentFromTable( String className, String mimeType )
  {
    Content c = null;

    if( className == null || mimeType == null )
      return null;
    
    Pia.debug(this, "Inside makeContentFromTable");
    Pia.debug(this, "Class name is -->"+className);
    
    try{
      c = (Content)factory( className );
    }catch(ClassNotFoundException nfe){
      //something is wrong. .class for the 
      //mime type is not found.  Now try to
      //load Default class

      c = makeContentFromDef( mimeType );
      
    }catch(InstantiationException ie){
      
      c = makeContentFromDef( mimeType );
      
    }finally{
      return c;
    }
  }
  
  /** create a content object base on mime type.
   * If the association between mime type and its content class name does not exist, 
   * try to load from a package base on the given mime type's parent.
   */
  public Content createContent(String mimeType, InputStream input)
  {
    Content c = null;
    String className = null;
    
    Pia.debug(this, "Inside createContent");

    Object o = contentTypeTable.at( mimeType );
    if( o != null ){
      className = (String) o;
      c = makeContentFromTable( className, mimeType );
    }else{
      // class mapping is not in the table. make from package
      c = makeContentFromPackage( mimeType );
      if ( c != null )
	// cache new entry
	contentTypeTable.at( mimeType, cacheEntry );
    }
    if ( c == null ){
      Pia.debug(this, "creating ByteStreamContent");
      c = new ByteStreamContent( input );
    }
    else{
      Pia.debug(this, "Setting input stream...");
      try{
	c.source( input );
      }catch(ContentOperationUnavailable e){
	e.printStackTrace();
	return null;
      }
    }
    return c;
  }

}








