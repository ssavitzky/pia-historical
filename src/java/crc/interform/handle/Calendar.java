////// Calendar.java:  Handler for <calendar>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.Date;
import java.text.DateFormat;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Element;
import crc.sgml.AttrWrap;
import crc.sgml.Attrs;
import crc.sgml.TableElement;

import java.lang.Process;
import java.lang.Runtime;


/** Handler class for &lt;calendar&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#calendar">Manual
 *	Entry</a> for syntax and description.
 */
public class Calendar extends crc.interform.Handler {
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
    "<calendar month=\"month\" year=\"year\" >\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return a calendar table \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    if( getMonth(it, ii) < 0 ){
      ii.replaceIt( new Text("Illegal month.") );
      return;
    }
    
    //System.out.println("month: "+ Integer.toString(userMonth));

    if ( getYear(it, ii) < 0 ){
      ii.replaceIt( new Text("Illegal year.") );
      return;
    }
    //System.out.println("year: "+ Integer.toString(userYear));

    Element tb = makeCalendarTable("2", Integer.toString(userMonth+1)+ "/" +Integer.toString(userYear));
    
    ii.replaceIt( tb );
  }

    protected void addCalDays(Element tb){
    int rowsNeeded =  NumberRowsNeeded();
    Element arow;
    int dayNum = 0;
    int firstDay = CalcFirstOfMonth();

    for(int i = 0; i < rowsNeeded; i++){
      arow = createRow();
      if ( i == 0 ){
	for( int j = 0; j < days.length; j++ )
	  if( j >= firstDay ){
	    dayNum++;
	    addCellToRow(arow, Integer.toString( dayNum ));
	  }
	  else
	    addEmptyCellToRow( arow );
      }else{
	for( int k = 0; k < days.length; k++ ){
	  dayNum++;
	  if( dayNum <= DaysInMonth[userMonth] )
	    addCellToRow( arow, Integer.toString( dayNum ) );
	  else
	    addEmptyCellToRow( arow );
	}
      }
      addTableRow( tb, arow );
    }
    }



    protected Element makeCalendarTable(String border, String title){
      Element tb = createTable();
      tb.addAttr("BORDER", border);
      
      Element caption = createCaption();
      caption.addAttr("ALIGN", "TOP");
      caption.append( title );
      
      tb.addItem( caption );
      
      Element row = createRow();
      addDays( row );
      addTableRow( tb, row );
      addCalDays( tb );
      return tb;
    }



 protected int getMonth( SGML it, Interp ii ){
   String cmonth = null;

      if (it.hasAttr("month")){
	cmonth = it.attrString("month");
	if ( cmonth != null ){
	  try{
	    userMonth = Integer.parseInt( cmonth );
	  }catch( Exception e ){
	    return -1;
	  }
	  userMonth--;
	}
      }

      if( cmonth == null || checkMonth( userMonth ) < 0 )
	return -1;
      else
	return 0;
    }

   protected int getYear( SGML it, Interp ii ){
     String cyear  = null;
  
     if (it.hasAttr("year")){
       cyear = it.attrString("year");
       if ( cyear != null ){
	 try{
	   userYear = Integer.parseInt( cyear );
	 }catch( Exception e ){
	    return -1;
	 }
       }
     }
  
     if( cyear == null || checkYear( userYear ) < 0 )
       return -1;
     else
       return 0;
   }



  protected int checkMonth(int month){
    if ((month < 0) || (month > 11)) 
      return (-1);
    else
      return 0;
  }

  protected int checkYear(int year){
    /* Start at 1582, when modern calendar starts. */
    if (year < 1582) 
      return -1;
    else
      return 0;
  }



  protected void addDays(Element row){
    for(int i=0; i < days.length; i++)
      addCellToRow( row, days[i]);
  }

  protected Element createTable(){
    //    return createElement("TABLE");
    return new TableElement("TABLE");
  }

  protected Element createCaption(){
    return createElement("CAPTION");
  }

  protected Element createElement(String tag){
    Element e = new Element( tag );
    e.endTagRequired((byte)1);
    return e;
  }

  protected SGML addTableRow(Element tb, Element row){
    return tb.addItem( row );
  }

  protected Element createRow(){
    Element row = new Element("TR");
    row.endTagRequired((byte)1);
    return row;
  }

  protected void addCellToRow(Element row, String data){
    //    String startTag = "<TD VALIGN=TOP>";
    //    String endTag   = "</TD>";
    Element b = new Element("b");
    b.append( data );
    Element e = new Element("td");
    e.append( b );

    e.addAttr("ALIGN", "TOP");
    row.append( e );
  }


  protected void addEmptyCellToRow(Element row){
    //String startTag = "<TD>";
    //String endTag   = "</TD>";
    Element e = new Element( "TD", "" );
    row.append( e );
  }

  /**
   *  USE:  Calculates number of rows needed for calendar.
   *  IN:   year = given year after 1582 (start of the Gregorian calendar).
   *        month = 0 for January, 1 for February, etc.
   *  OUT:  Number of rows: 5 or 6, except for a 28 day February with
   *        the first of the month on Sunday, requiring only four rows.
   */
  
  int NumberRowsNeeded()
  {
    int firstDay;       // day of week for first day of month 
    int numCells;       // number of cells needed by the month 
    int result;

    firstDay = CalcFirstOfMonth();

    //System.out.println("February is-->"+ java.util.Calendar.FEBRUARY);
    // Non leap year February with 1st on Sunday: 4 rows. 
    if ((userMonth == java.util.Calendar.FEBRUARY) && (firstDay == 0) && !calendar.isLeapYear(userYear))
      return (4);

    // Number of cells needed = blanks on 1st row + days in month. 
    numCells = firstDay + DaysInMonth[userMonth];

    // One more cell needed for the Feb 29th in leap year. 
    if ((userMonth == java.util.Calendar.FEBRUARY) && (!calendar.isLeapYear(userYear))) numCells++;

    // 35 cells or less is 5 rows; more is 6. 
    result = ((numCells <= 35) ? 5 : 6);

    return result;

    } // NumberRowsNeeded


  int CalcFirstOfMonth()
  /*
     USE:  Calculates day of the week the first day of the month falls on.
     IN:   year = given year after 1582 (start of the Gregorian calendar).
           month = 0 for January, 1 for February, etc.
     OUT:  First day of month: 0 = Sunday, 1 = Monday, etc.
  */
    {
    int firstDay;       /* day of week for Jan 1, then first day of month */
    int i;              /* to traverse months before given month */
  
    /* Get day of week for Jan 1 of given year. */
    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
    Date dd = null;
    //System.out.println("before try");
    // the long format is January 1, 1997
    String s = months[userMonth] + " 1, " + Integer.toString( userYear );

    try{
      //System.out.println(s);
      dd = df.parse( s );
    }catch(Exception e){
      //System.out.println("crash");
      e.printStackTrace();
    }
    
    calendar.setTime( dd );
    //System.out.println("after set time");

    //firstDay = calendar.get((java.util.Calendar.DAY_OF_WEEK) - java.util.Calendar.SUNDAY + 7) % 7;
    firstDay = calendar.get(java.util.Calendar.DAY_OF_WEEK);
    //System.out.println("The first of the month is-->"+Integer.toString( firstDay ));
    return firstDay - 1; 
    } // CalcFirstOfMonth
  
    
}


