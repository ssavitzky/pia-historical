// streamParser.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.util;
import java.io.StreamTokenizer;
import java.io.IOException; 




public class streamParser{
  public Object nextToken() throws IOException, NoSuchElementException {
    try {
            int c;
	    switch (c=tokens.nextToken()) {
                case StreamTokenizer.TT_EOF:
                    throw new NoSuchElementException;
                case StreamTokenizer.TT_EOL:
                    throw new NoSuchElementException;
                case StreamTokenizer.TT_WORD:
		    return tokens.sval;
                default:
		    return new Character((char)c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
  }

  public streamParser(InputStream stream) {
      StreamTokenizer tokens = new StreamTokenizer(stream);
      tokens.resetSyntax();
      tokens.wordChars('0', '9');        // make digit chars word chars
      tokens.wordChars('a', 'z');
      tokens.wordChars('A', 'Z');
      tokens.wordChars('_','_');
      tokens.eolIsSignificant(true);
      tokens.whitespaceChars(0, ' ');
      tokens.lowerCaseMode(true);        // turn tokens to lowercase
    }
}
