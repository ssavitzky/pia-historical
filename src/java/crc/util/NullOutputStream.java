
package crc.util;
import java.io.OutputStream;

 public class NullOutputStream extends OutputStream{
  public void write(byte[] b){
    // do nothing -- bits onto floor
  }
  public void write(int b){
    // do nothing -- bits onto floor
  }
  public void write(byte[] b, int i, int j){
    // do nothing -- bits onto floor
  }
}
