//  html.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.content.text;

/**
 * class for html which is not parsed. only difference with plain text is
 * that inserts of text happen after the body tag.
 * more sophisticated processing should use the ParsedContent class which uses
 * the interpreter.
 */

public class html extends Default
{

/**   changes meaning of location 0 in additions to be just after the body tag
 */

    protected void insertAddition(int position){
      String k =  new Integer( position).toString();
      if(additions == null || !additions.has(k)) return ;

      String add = (String) additions.at(k);
    crc.pia.Pia.debug(" inserting "+add + " at "+  position);

      if(position == 0){
	// insert after body tag
	// this is a hack -- eventually use html parser
	 String s =  new String(buf,0,nextIn);
	 int bstart = s.indexOf("<body");
	 if(bstart == -1) bstart = s.indexOf("<Body");
	 if(bstart == -1) bstart = s.indexOf("<BODY");
	 if(bstart > -1) {
	   bstart = -1; //s.indexOf(">", bstart);
	 }
	 if(bstart > -1) {
	   ++bstart;
    crc.pia.Pia.debug(" putting at"+  bstart);
	   insert(add,bstart);
	   return;
	 }
      }
      // else
    crc.pia.Pia.debug(" super insert");
      super.insertAddition(position);
    }

  public html() {
    super();
  }

  public html(java.io.InputStream in){
    super(in);
  }

  public html(java.io.Reader in){
    super(in);
  }

}
