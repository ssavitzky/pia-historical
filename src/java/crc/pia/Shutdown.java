package crc.pia;
import java.io.*;
import crc.pia.Pia;


public class Shutdown implements Runnable{

  public void run(){
    String line;
    DataInputStream in = new DataInputStream( System.in );
    
    try{
      for(;;){
        System.out.print( "-->" );
        System.out.flush();
        line = in.readLine();
        if( line == null || line.startsWith("break") ){
	  Pia.instance().shutdown( false );
	  break;
	}
        try{
          Thread.sleep(1000);
        }catch( Exception e ){}
        //System.out.println( line );
      }
    }catch(Exception e){
    }
  }

}












