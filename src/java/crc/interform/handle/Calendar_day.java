////// Calendar.java:  Handler for <os-command>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.Date;
import java.text.DateFormat;

import crc.ds.Index;
import crc.ds.List;
import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Element;
import crc.sgml.TableElement;
import crc.sgml.Tokens;

import java.lang.Process;
import java.lang.Runtime;


/** Handler class for &lt;os-command&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;calendar-day cal=&calendar day=[1-31]&gt;string;/calendar-day&gt;
 * <dt>Dscr:<dd>
 *	Insert string into table indicated by the day
 *  </dl>
 */
public class Calendar_day extends crc.interform.Handler {
  // Calendar related 
  String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday",
                   "Thursday", "Friday", "Saturday"};

  String months[] = {"January", "February", "March", "April",
                     "May", "June", "July", "August", "September",
                     "October", "November", "December"};

  int DaysInMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  // Month and year entered by user.
  int userMonth = 0;
  int userYear = 1997;
  GregorianCalendar calendar = new GregorianCalendar();

  //===================================
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<calendar cal=\"calendar-table\" day=\"day\">\n" +
    "string\n" +
    "</calendar-day>" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Execute CONTENT as an operating system command \n" +
    "in the background with proxies set to PIA.  \n" +
    "Optionally BYPASS proxies.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    SGML tb = null;
    String day = null;
    
    if (it.hasAttr("cal")){
      tb = it.attr("cal");
    }else{
      ii.replaceIt("Missing calendar.");
      return;
    }
    
    if (it.hasAttr("day")){
      day = it.attrString("day");
    }else{
      ii.replaceIt("Missing day.");
      return;
    }

    SGML content = Util.removeSpaces(it.content());
    SGML result = addEntryToTable(tb, day, content);
    ii.replaceIt( result );
  }

  protected SGML addEntryToTable(SGML table, String day, SGML content){
    System.out.println("\n\nInside addEntryToTable\n");
    Index foobar = new Index("5");
   try{
    SGML r = foobar.lookup(table);
   return r;  
     
   }
   catch(Exception e){
     
   }
   return null;
   
  }

}


