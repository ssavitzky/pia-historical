import java.util.*; 
import java.io.*; 
import installshield.jshield.runtime.*; 

/** 
* This is an example Action which is used in conjunction with 
* the PropertyPanel. While the PropertyPanel obtains some information 
* from the user, the PropertyAction actually creates information (in 
* this case, a file with the collected information) which is presumably 
* used by the actual application. 
* 
* Note that PropertyAction may be run in "console" mode, which means 
* it ought to have a "default" behavior that works reasonably when 
* there is no user interaction. 
*/ 

public class PropertyAction implements Action 
{ 
/** 
* Called when it is time to run this action. 
*/ 

  public boolean execute(InstallContext ctx) 
  { 
    // Check if the context has a properties instance loaded 
    // by the PropertyPanel 

    Properties prop = (Properties) ctx.getData("example.PropertyPanel"); 


    if (prop == null) 
      { 
	// no information, so revert to 
	// default behavior 

	prop = new Properties(); 
	prop.put("Port Number", "80"); 
	prop.put("Document Root", ctx.getInstallRoot()); 
	prop.put("CGI Bin", ctx.getInstallRoot()); 
      } 

    // now save the information into a file. 

    File f = new File(ctx.getInstallRoot(), "www.prop"); 

    try 
      { 
	FileOutputStream fout = new FileOutputStream(f); 
	prop.save(fout, "My Properties Version 1.0"); 
	//doAgent( (String)prop.get("uhome"), ctx.getInstallRoot(), fout );

	fout.close(); 

	String slash = System.getProperty("file.separator");
	File src = new File( ctx.getInstallRoot(), "Config"+slash+(String)prop.get("agentname")+ slash+ "my");
	File des = new File((String)prop.get("uhome"));

	if( des.exists() == false ){
	  System.out.println("Making target directory..." + des.getAbsolutePath());
	  des.mkdir();
	}

	copyDir( src,  des );
	doBatchFile( ctx.getInstallRoot(), (String)prop.get("uhome") );

	
	// save the name of the file in the 
	// undo database in order to remember what 
	// to delete later on. 

	DataOutput log = ctx.getLog(); 

	log.writeUTF(f.getAbsolutePath()); 
      } 
    catch(IOException ex) 
      { 
	ctx.error("Creating Properties", 
		  "Error creating properties " + ex); 
	return false; 
      } 
    ctx.addSummary("Created web server profile."); 

    return true; 
} 


public void doBatchFile(String piaHomeName, String userHome){
  File piahome = new File( piaHomeName, "bin" );
  File batchFile = new File( piahome, "testbatch.bat" );
  FileOutputStream fo = null;
  String slash = System.getProperty("file.separator");

  try{
    fo = new FileOutputStream( batchFile );
    shipOutput(fo, "REM the path", true);
    shipOutput(fo, "SET CPATH=" + piaHomeName + slash + "lib" + slash + "java" + slash + "jigsaw.zip;" + piaHomeName + slash + "lib" + slash + "java" + slash + "crc.zip;" + piaHomeName + slash + "src" + slash + "java", true );
    shipOutput(fo, "jre -cp %CPATH% crc.pia.Pia -root " + piaHomeName + " -u " + userHome, true );
    fo.close();
  }catch(IOException e){
  }
}


  /**
   * Ship a string to the OutputStream.
   */
  private void shipOutput(OutputStream out, String s, boolean withnewline)
  {
    byte[] bytestring = null;

    if( s == null )
      return;
    
    bytestring = s.getBytes();

    try{
      if( bytestring != null ){
	out.write( bytestring, 0, bytestring.length  );
      } else 
	{System.out.print("bytestring is null");
	}
      
      if( withnewline ){
	out.write( '\n' );
      }
      
    } catch(IOException e) {
      System.out.println("Can not write...\n");
    }
  }


  
public void copyFile( File src, File des){
  System.out.println("Inside copyFile");
  //if( !des.exists() )
  //  des.mkdir();
  FileInputStream source = null;
  FileOutputStream destination = null;

  try{
    source = new FileInputStream( src );
    destination = new FileOutputStream( des );
    byte[] buffer = new byte[1024];
    int bytes_read;
    
    
    while(true){
      bytes_read = source.read( buffer );
      if( bytes_read == -1 ) break;
      destination.write( buffer,0,bytes_read );
    }
  }catch(IOException e){
  }finally{
    if( source != null )
      try{
           source.close();
      }catch(IOException ee){}

    if( destination != null)
      try{
           destination.close();
      }catch(IOException ee){}
  }
  System.out.println("getting out of copyFile...");
}

public void copyDir(File source, File target){
String[] children;
String childName;
File fileChild;


System.out.println("Inside copyDir");
System.out.println("source is -->"+source.getAbsolutePath());
System.out.println("destination is -->"+target.getAbsolutePath());

  if( source.isDirectory() &&
      (children = source.list()) != null ){

    if( target.exists() == false ){
      System.out.println("Making target directory..."+target.getAbsolutePath());
      target.mkdir();
    }
      

    for(int i=0; i < children.length; i++){
      childName = children[i];
      System.out.println("Working on child-->"+childName);

      fileChild = new File(source, childName );

      if( fileChild.isFile() ){
	System.out.println("Copying file..."+childName);
	copyFile( fileChild, new File( target, childName ) );
      }else{
	File adir = new File( target, childName );
	if( adir != null ){
	  if( adir.exists() == false ){
	    System.out.println("Making directory..."+childName);
	    adir.mkdir();
	  }
	  copyDir( fileChild, adir );
	}
      } //else
    } // for
  } // if
}

  
/** 
* And this one is called during uninstallation. 
*/ 

public boolean undo(InstallContext ctx, DataInput undoLog) 
{ 

  // read back the file name stored in the undo log. 

  try 
    { 
      String fname = undoLog.readUTF(); 
      File f = new File(fname); 

      if (!f.delete()) 

	{ ctx.addSummary("Failed to remove " + fname); } 
      else 
	{ ctx.addSummary("Removed web server profile."); } 

      return true; 
    } 
  catch(IOException ex) 
    { 
      ctx.error("Properties error", 
		"Error removing web server file " + ex); 

      // Returning false actually terminates the installation. 
      // you might choose to return true if an error performing 
      // this action is not important. 

      return false; 
    } 
} 

/** 
* Remove any temporary resources created by this Action 
*/ 

public void cleanup(InstallContext ctx) 
{ } 

/** 
* Return a short description of this Object 
*/ 

public String describe() 
{ 
  return "Creating web server profile."; } 
} 



