/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_Calendar: calendar interface for managing a timeline of annotated documents
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.17.98 - revised 02-27-98
 *
 */
package ricoh.rhpm;

import java.util.*;
import java.io.*;

import ricoh.rh.RH_GlobalVars;

public class RHCalendar {
    
    private RHPMAgent parent;
    private final static String[] daysInWeekLong={"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private final static String[] daysInWeekShort={"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final static String[] monthsInYear={"January","February","March","April","May","June","July","August","September",
						"October","November","December"};
    private String tmpname="", tmpconcept="", path="", version="";
    private long tmpscore=0;
    private Hashtable hash;
    private int simThreshold;
    
    public RHCalendar(RHPMAgent p) {
	parent=p;
    }
    public void setup(String newpath, String ver, int thres) {
	path=newpath;
	version=ver;
	simThreshold=thres;
    }
  /**
   * Save the current information regarding this annotation to the data file for the current month and year;  then update
   * the calendar file by rewriting it.
   */
  public synchronized void saveLink (String name, String concept, long score, String path, String ver, int thres) {
      
      System.out.println("===> Saving Calendar Link...:"+path);
      //version=ver;
      //simThreshold=thres;
      Date date=new Date();
      Calendar cal=new GregorianCalendar();
      int month=cal.get(Calendar.MONTH), year=cal.get(Calendar.YEAR), day=cal.get(Calendar.DAY_OF_MONTH),
	  hour=cal.get(Calendar.HOUR), minute=cal.get(Calendar.MINUTE);
      String monthstr=getMonthString(month), ampm="", hourstr="", minstr="";

      //System.out.println("***>Year"+year);
      
      if (cal.get(Calendar.AM_PM)>0) ampm="PM";
      else ampm="AM";
      if (hour<10) hourstr=new StringBuffer().append("0"+hour).toString();
      else hourstr=new StringBuffer().append(hour).toString();
      if (minute<10) minstr=new StringBuffer().append("0"+minute).toString();
      else minstr=new StringBuffer().append(minute).toString();
      
      String timestr=new StringBuffer().append(hourstr+":").append(minstr).append(ampm).toString();
      String newline=parent.getNewlineByte();
      
      int idx=0;
      if (score<=0) concept=RH_GlobalVars.NoHighConcept;
      
      //*** Create filename based on today's date
      String filename=new StringBuffer(path).append(RH_GlobalVars.rhPathSeparator)
	  .append(RH_GlobalVars.rhCalendarDir).append(RH_GlobalVars.rhPathSeparator).append(year).append(RH_GlobalVars.rhPathSeparator)
	  .append(monthstr).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhCalendarDataFileName)
	  .append(RH_GlobalVars.rhCalendarExtension).toString();
      //System.out.println("=====> Filename:"+filename.toString());
      //System.out.println("CAL FILE:"+filename);
      //*** Create line of data to write to file
      name=new StringBuffer().append(day).append(RH_GlobalVars.rhFieldSeparator).append(name).append(RH_GlobalVars.rhFieldSeparator)
	  .append(concept).append(RH_GlobalVars.rhFieldSeparator).append(timestr).append(RH_GlobalVars.rhFieldSeparator).append(score)
	  .append(newline).toString();
      try {
	  //FileOutputStream fp=new FileOutputStream(filename);
	  BufferedWriter fp=new BufferedWriter(new FileWriter(filename,true));
	  //System.out.println("***Writing Cal data:"+ name);
	  fp.write(name,0,name.length());
	  fp.close(); 
	  
	  //*** Now update the calendar file; this should also requets removing this file from the cache so that the new version will be loaded
	  writeCalendarMonth(year,month+1);
      } catch (IOException ex) {
	  System.out.println("Could not create calendar file:"+filename);
      }
      //commBus.setCalendarButton(true);
      //System.out.println("===> DONE Saving Calendar Link...");
  }
    
  public synchronized void writeCalendarMonth(int year, int month) {
    Date date=new Date();
    int daysInMonth=getDaysInMonth(month,year), count=0, datumPtr=0;

    //System.out.println("******CAL NEW MONTH: "+ month +", "+ year);
    /*
    byte nl=(byte)'\n';
    byte[] newLine=new byte[1];
    newLine[0]=nl; 
    String newline=new String(newLine);
    */
    String newline=parent.getNewlineByte();
    String line="", filename="";

    Calendar cal=new GregorianCalendar();
    //System.out.println("***CAL:"+cal);
    //System.out.println("***Date:"+date.toString());
    //** Setup calendar to first day in this month
    cal.set(year,month-1,0);
    cal.setTime(cal.getTime());
    int today=cal.get(Calendar.DAY_OF_WEEK);
    //System.out.println("***Today="+today+" year="+year);
    String monthstr=monthsInYear[month-1];
    String yearurl=new StringBuffer().append("<a href=\"..").append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhIndexFileName).
      append(".html\">"+year+"</a>").toString();
    String title=RH_GlobalVars.RH_TitleString+" Calendar:<br>"+monthstr+" "+yearurl, titleString="";
    String previousMonthStr="", previousMonthUrl="", previousMonthUrlTL="", nextMonthStr="", nextMonthUrl="", nextMonthUrlTL="",datumString="";
    RHCalendarDatum[] data=null;
 
    //*** Read calendar data into data structure for creating calendar
    try {
      filename=new StringBuffer().append(path).append(RH_GlobalVars.rhPathSeparator).
	  append(RH_GlobalVars.rhCalendarDir).append(RH_GlobalVars.rhPathSeparator).append(year).append(RH_GlobalVars.rhPathSeparator).
	  append(monthstr).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhCalendarDataFileName).
	  append(RH_GlobalVars.rhCalendarExtension).toString();
      //System.out.println("=======> Filename:"+filename.toString());
      BufferedReader dataInput=new BufferedReader(new FileReader(filename));
      Vector list=new Vector(); // i'm using a vector because i don't know how many items i will read (& thus cannot use an array)
      //*** Since we do notknow how many datums there are, i use a dynamic container and red the lines of data into it keeping a count
      while ((line=dataInput.readLine())!=null) {
	count++;
	list.addElement(line);
      }
      dataInput.close();
      //** Now i create the calendar datum container 
      data=new RHCalendarDatum[count];
      int newday=0, newscore=0, startidx=0, endidx=0;
      String newname="", newconcept="", newtime="";
      //*** NOW Parse each line and extract the components to make individual RHCalendarDatum object
      for (int j=0;j<count;j++,startidx=0, endidx=0) {
	line=(String)list.elementAt(j);
	//System.out.println("==>"+line);
	//*** Make day
	endidx=line.indexOf(RH_GlobalVars.rhFieldSeparator);
	newday=Integer.parseInt(line.substring(startidx,endidx));
	//*** Make name
	startidx=endidx+1; 
	endidx=line.indexOf(RH_GlobalVars.rhFieldSeparator,startidx);
	newname=line.substring(startidx,endidx);
	//*** Make concept
	startidx=endidx+1;
	endidx=line.indexOf(RH_GlobalVars.rhFieldSeparator,startidx);
	newconcept=line.substring(startidx,endidx);
	//*** Make time
	startidx=endidx+1;
	endidx=line.indexOf(RH_GlobalVars.rhFieldSeparator,startidx);
	newtime=line.substring(startidx,endidx);
	//*** Make score
	//startidx=endidx+1;
	//endidx=line.indexOf(mainFrame.rhFieldSeparator,startidx);
	newscore=Integer.parseInt(line.substring(endidx+1,line.length()));
	//System.out.println(j+">>"+newday+" "+newname+" "+newconcept+" "+newscore);
	data[j]=new RHCalendarDatum(newday,newname,newconcept,newtime,newscore);
      }
    } catch (IOException ex) {
      // typically means file does not exist because user never annotated during that month
	System.out.println("Could not read calendar datum file:"+filename);
    }

    //*** Create link to previous month
    if (month>1) {
      previousMonthStr=monthsInYear[month-2];
      previousMonthUrl=new StringBuffer().append("..").append(RH_GlobalVars.rhPathSeparator)
	  .append(previousMonthStr).append(RH_GlobalVars.rhPathSeparator)
	  .append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
      previousMonthUrlTL=new StringBuffer().append("..").append(RH_GlobalVars.rhPathSeparator)
	  .append(previousMonthStr).append(RH_GlobalVars.rhPathSeparator)
	  .append(RH_GlobalVars.rhTimeLineFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
    }
    else { 
      previousMonthStr=monthsInYear[monthsInYear.length-1];
      previousMonthUrl=new StringBuffer().append("..").append(RH_GlobalVars.rhPathSeparator)
	  .append("..").append(RH_GlobalVars.rhPathSeparator)
	  .append(year-1).append(RH_GlobalVars.rhPathSeparator)
	  .append(previousMonthStr).append(RH_GlobalVars.rhPathSeparator)
	  .append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
      previousMonthUrlTL=new StringBuffer().append("..").append(RH_GlobalVars.rhPathSeparator).append("..")
	  .append(RH_GlobalVars.rhPathSeparator)
	  .append(year-1).append(RH_GlobalVars.rhPathSeparator)
	  .append(previousMonthStr).append(RH_GlobalVars.rhPathSeparator)
	  .append(RH_GlobalVars.rhTimeLineFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
    }
    //*** Create link to next month
    if (month<12) {
      nextMonthStr=monthsInYear[month];
      nextMonthUrl=new StringBuffer().append("..").append(RH_GlobalVars.rhPathSeparator).append(nextMonthStr).append(RH_GlobalVars.rhPathSeparator)
	.append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
    }
    else {
      nextMonthStr=monthsInYear[0];
      nextMonthUrl=new StringBuffer().append("..").append(RH_GlobalVars.rhPathSeparator).append("..").append(RH_GlobalVars.rhPathSeparator).
	append(year+1).append(RH_GlobalVars.rhPathSeparator).append(nextMonthStr).append(RH_GlobalVars.rhPathSeparator)
	.append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
    }
    //System.out.println(">>>Previous:"+previousMonthUrl);
    //System.out.println(">>>Next:"+nextMonthUrl);

    //*** Write the Standard Calendar file
    try {
      //** Open calendar file
      filename=new StringBuffer(path).append(RH_GlobalVars.rhPathSeparator).
	append(RH_GlobalVars.rhCalendarDir).append(RH_GlobalVars.rhPathSeparator).append(year).append(RH_GlobalVars.rhPathSeparator).
	append(monthstr).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
      //System.out.println("***WRITING CAL FILE:"+filename);
      FileOutputStream fp=new FileOutputStream(filename);    
      //*** Open timeline file
      filename=new StringBuffer(path).append(RH_GlobalVars.rhPathSeparator).
	append(RH_GlobalVars.rhCalendarDir).append(RH_GlobalVars.rhPathSeparator).append(year).append(RH_GlobalVars.rhPathSeparator).
	append(monthstr).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhTimeLineFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
      FileOutputStream tlfp=new FileOutputStream(filename);    

      //** Create and write title for calendar
      titleString= new StringBuffer().append(RH_GlobalVars.RH_HTML_Doctype_Header).append(newline).append(RH_GlobalVars.HTML_HTML_Tag)
	.append(RH_GlobalVars.HTML_HEAD_Tag).append(newline).append("<").append(RH_GlobalVars.RH_CalendarHeader_BeginTag)
	.append(" ").append(RH_GlobalVars.RH_DocumentHeader_VERTag).append("=\"").append(version).append("\">")
	.append("<"+RH_GlobalVars.RH_CalendarHeader_EndTag+">").append(newline).append(RH_GlobalVars.HTML_TITLE_Tag).append(monthstr+" "+year)
	.append(RH_GlobalVars.HTML_TITLE_Tag_End).append(RH_GlobalVars.HTML_HEAD_Tag_End).append(newline)
	.append(RH_GlobalVars.HTML_BODY_Tag).append(newline)
	.append("<center><h3>").append(title).append("</h3></center>").append(newline).toString();
      fp.write(titleString.getBytes());
      //** Create and write title for timeline
      titleString= new StringBuffer().append(RH_GlobalVars.RH_HTML_Doctype_Header).append(newline).append(RH_GlobalVars.HTML_HTML_Tag)
	.append(RH_GlobalVars.HTML_HEAD_Tag).append(newline).append("<").append(RH_GlobalVars.RH_CalendarHeader_BeginTag)
	.append(" ").append(RH_GlobalVars.RH_DocumentHeader_VERTag).append("=\"").append(version).append("\">")
	.append("<"+RH_GlobalVars.RH_CalendarHeader_EndTag+">").append(newline).append(RH_GlobalVars.HTML_TITLE_Tag).append(monthstr+" Timeline "+year)
	.append(RH_GlobalVars.HTML_TITLE_Tag_End).append(RH_GlobalVars.HTML_HEAD_Tag_End).append(newline)
	.append(RH_GlobalVars.HTML_BODY_Tag).append(newline)
	.append("<center><h3>").append(RH_GlobalVars.RH_TitleString+" Timeline:<br>"+monthstr+" "+yearurl).append("</h3></center>").append(newline).toString();
      tlfp.write(titleString.getBytes());

      //*** Write calendar table for navigation
      line=new StringBuffer().append(newline).append("<TABLE border=0 frame=void rules=none width=100%>").append(newline)
	.append("<TR>"+newline).append("<TD align=left><a href=\"").append(previousMonthUrl).append("\">")
	.append(previousMonthStr).append("</a></TD>"+newline)
	.append("<TD align=center>[ <a href=\"").append(RH_GlobalVars.rhTimeLineFileName).append(RH_GlobalVars.rhHTMLExtension)
	.append("\">Timeline</a> | ")
	.append("<a href=\"").append(RH_GlobalVars.rhWeekViewFileName).append(RH_GlobalVars.rhHTMLExtension)
	.append("\">WeekView</a> ]</TD>"+newline)
	.append("<TD align=right><a href=\"").append(nextMonthUrl).append("\">")
	.append(nextMonthStr).append("</a></TD>"+newline+"</TR>"+newline+"</TABLE>").toString();
      fp.write(line.getBytes());

      //*** Write calendar table for navigation
      line=new StringBuffer().append(newline).append("<TABLE border=0 frame=void rules=none width=100%>").append(newline)
	.append("<TR>"+newline).append("<TD align=left><a href=\"").append(previousMonthUrlTL).append("\">")
	.append(previousMonthStr).append("</a></TD>"+newline)
	.append("<TD align=center>[ <a href=\"").append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension)
	.append("\">Calendar</a> | ")
	.append("<a href=\"").append(RH_GlobalVars.rhWeekViewFileName).append(RH_GlobalVars.rhHTMLExtension)
	.append("\">WeekView</a> ]</TD>"+newline)
	.append("<TD align=right><a href=\"").append(nextMonthUrlTL).append("\">")
	.append(nextMonthStr).append("</a></TD>"+newline+"</TR>"+newline+"</TABLE>").toString();
      tlfp.write(line.getBytes());


      //*** Write Table for entries in Calendar
      int currentDay=1;
      String headerColor="white", bodyColor="silver";
      line=new StringBuffer().append("<p><CENTER>"+newline+"<TABLE border=1 width=100% cellpadding=3 cellspacing=3>").append(newline)
	.append("<TR><TD align=center bgcolor="+headerColor+"><strong>").append(daysInWeekShort[currentDay++]).append("</strong></TD>")
	.append("<TD align=center bgcolor="+headerColor+"><strong>").append(daysInWeekShort[currentDay++]).append("</strong></TD>").append(newline)
	.append("<TD align=center bgcolor="+headerColor+"><strong>").append(daysInWeekShort[currentDay++]).append("</strong></TD>").append(newline)
	.append("<TD align=center bgcolor="+headerColor+"><strong>").append(daysInWeekShort[currentDay++]).append("</strong></TD>").append(newline)
	.append("<TD align=center bgcolor="+headerColor+"><strong>").append(daysInWeekShort[currentDay++]).append("</strong></TD>").append(newline)
	.append("<TD align=center bgcolor="+headerColor+"><strong>").append(daysInWeekShort[currentDay++]).append("</strong></TD>").append(newline)
	.append("<TD align=center bgcolor="+headerColor+"><strong>").append(daysInWeekShort[0]).append("</strong></TD></TR>").append(newline).toString();
      fp.write(line.getBytes());
      //*** Write Table for entries in Timeline
      line=new StringBuffer().append("<p><CENTER>"+newline+"<TABLE border=3 width=100% cellpadding=3 cellspacing=3>").append(newline).toString();
      tlfp.write(line.getBytes());
      
      line="<TR>";
      fp.write(line.getBytes());
      for(int tabs=1;tabs<today;tabs++) {
	line=new StringBuffer().append("<TD></TD>").toString();
	fp.write(line.getBytes());
      }
      int whatday=today;
      //*** Write datum objects in appropriate days of month for Calendar
      for (int i=1; i<=daysInMonth; i++,whatday++) {
	//*** Create and write entry for Calendar
	datumString=generateDatumString(i,data,count,newline);
	line=new StringBuffer().append(newline+"<TD VALIGN=TOP>").append("<a href=\"").append(i)
	    .append(RH_GlobalVars.rhHTMLExtension).append("\">"+i+"</a><br>")
	    .append(datumString).append("</TD>").toString();
	fp.write(line.getBytes());
	if (whatday>=7) {
	  whatday=0;
	  line=new StringBuffer().append("</TR>").append(newline).append("<TR>").toString();
	  fp.write(line.getBytes());
	}
      }
      //*** Write datum objects in appropriate days of month for Timeline
      if (today>=7) whatday=0;  // since 0 is sunday in my string array
      else whatday=today;
      
      //System.out.println(">>>>>WhatDAY:"+whatday);
      //for(int tabs=1;tabs<today;tabs++) whatday++;
      //headerColor="Silver";  
      for (int i=1; i<=daysInMonth; i++) {
	//System.out.println(i+">>>>>DAY "+whatday+"="+daysInWeekShort[whatday]);
	if (whatday>=1 && whatday<=5) bodyColor="Silver";
	else bodyColor="66CCCC"; //"CCFFFF";
	datumString=generateTitleDatumString(i,data,count,newline);
	line=new StringBuffer().append(newline+"<TR><TD WIDTH=25 ALIGN=CENTER VALIGN=TOP BGCOLOR=\"").append(headerColor).append("\">")
	  .append("<strong>").append(daysInWeekShort[whatday]).append("</strong><br>")
	  .append("<a href=\"").append(i).append(RH_GlobalVars.rhHTMLExtension).append("\">"+i+"</a><br></TD>")
	  .append("<TD BGCOLOR=\"").append(bodyColor).append("\">").append(datumString).append("</TD></TR>").toString();
	tlfp.write(line.getBytes());
	if (whatday>=6) {
	  whatday=0;
	}
	else whatday++;
      }
      //** End tables for both views
      line=new StringBuffer().append(newline).append("</TABLE></CENTER>").append(newline).toString();
      fp.write(line.getBytes());
      tlfp.write(line.getBytes());

      line= new StringBuffer().append("<hr><center>This file automatically generated by the Reader's Helper")
	.append("<img src=\"..").append(RH_GlobalVars.rhPathSeparator).append("..")
	.append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhLocalGIFPath).append(RH_GlobalVars.rhPathSeparator)
	.append(RH_GlobalVars.rhANOHGIFFileName).append("\"> Agent using a "+simThreshold+"% document threshold.<br>")
	.append("<font size=-1>Copyright (c) 1998 Ricoh Silicon Valley, Inc.<br>").append(newline).append("All rights reserved</font></center><p>")
	.append(newline).append(RH_GlobalVars.HTML_BODY_Tag_End).append(RH_GlobalVars.HTML_HTML_Tag_End).append(newline).toString();
      fp.write(line.getBytes());
      tlfp.write(line.getBytes());
      fp.close();
      tlfp.close();
      data=null;
    } catch (IOException ex) {
	System.out.println("Could not create calendar file:"+filename);
    }

  }

  private String generateDatumString(int day, RHCalendarDatum[] data, int len, String newline) {
    boolean done=false;
    String entry="", doctitle="";
    Hashtable hash=new Hashtable();
    int count=0;
    for (int i=0;i<len;i++) {
      //*** Find entries of the same day;  hash table prevents duplicating entries;  also, if there is no high concept, write nothing; score
      //*** must also be greater than the current similarity threshold.  note that if the threshold changes (e.g. is reduced), this future
      //*** writing of the celndar file will reveal lower scoring entries whcih did not appear before when the threshold was high.  this is
      //*** because the data.rhc file keeps everything
      if (data[i].getDay()==day && hash.get(data[i].getName())==null && 
	  !data[i].getConcept().equalsIgnoreCase(RH_GlobalVars.NoHighConcept) &&
	  data[i].getScore()>=simThreshold) {
	  doctitle=parent.getPrivateDocumentTitle(data[i].getName());
	entry=new StringBuffer().append(newline).append(entry).
	    append("<a href=\".."+RH_GlobalVars.rhPathSeparator+".."+RH_GlobalVars.rhPathSeparator+".."+RH_GlobalVars.rhPathSeparator).
	    append(RH_GlobalVars.rhDocumentDir).append(RH_GlobalVars.rhPathSeparator).
	    append(data[i].getName()).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhIndexFileName).
	    append(RH_GlobalVars.rhHTMLExtension).
	    append("\">").
	    append("<img src=\"..").append(RH_GlobalVars.rhPathSeparator).append("..")
	    .append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhLocalGIFPath).append(RH_GlobalVars.rhPathSeparator)
	    .append("blue-ball-small.gif\" BORDER=0 ALT=\"").append(doctitle).append("\">").
	    append(++count+": "+data[i].getConcept()+" ["+data[i].getScore()+"%]</a> ").
	    append(data[i].getTime()).append("<br>"+newline).toString();
	hash.put(data[i].getName(),data[i]);
      }
    }
    hash.clear(); hash=null;
    return entry;
  }

  private String generateTitleDatumString(int day, RHCalendarDatum[] data, int len, String newline) {
    boolean done=false;
    String entry="", infofile="", doctitle="";
    Hashtable hash=new Hashtable();
    int count=0;
    for (int i=0;i<len;i++) {
      //*** Find entries of the same day;  hash table prevents duplicating entries;  also, if there is no high concept, write nothing; score
      //*** must also be greater than the current similarity threshold.  note that if the threshold changes (e.g. is reduced), this future
      //*** writing of the celndar file will reveal lower scoring entries whcih did not appear before when the threshold was high.  this is
      //*** because the data.rhc file keeps everything
      if (data[i].getDay()==day && hash.get(data[i].getName())==null && !data[i].getConcept().equalsIgnoreCase(RH_GlobalVars.NoHighConcept) &&
	  data[i].getScore()>=simThreshold) {
	RHActiveConcept concept=parent.findConcept(data[i].getConcept());
	if (concept!=null) {
	  doctitle=parent.getPrivateDocumentTitle(data[i].getName());
	  entry=new StringBuffer().append(newline).append(entry).
	    append("<a href=\".."+RH_GlobalVars.rhPathSeparator+".."+RH_GlobalVars.rhPathSeparator+".."+RH_GlobalVars.rhPathSeparator).
	    append(RH_GlobalVars.rhDocumentDir).append(RH_GlobalVars.rhPathSeparator).
	    append(data[i].getName()).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension).
	    append("\">").append(++count+": ").append(doctitle).append("</a> ").append("<strong>[").append(concept.getName()).
	    append(", Score:"+data[i].getScore()+"%]</strong> -- ").
	    append(data[i].getTime()).append("<br>"+newline).toString();
	  hash.put(data[i].getName(),data[i]);
	}
	else System.out.println("Concept:"+data[i].getName()+" not found");
      }
    }
    hash.clear(); hash=null;
    return entry;
  }

  private int getDaysInMonth(int month,int year) {
    switch (month) {
    case 1:
    case 3:
    case 5:
    case 7:
    case 8:
    case 10:
    case 12:
      return (31);
    case 4:
    case 6:
    case 11:
      return 30;
    default:
      if (year%4!=0) return (28);
      else if (year%100 !=0) return(29);
      else if (year%400 !=0)  return (28);
      else return (29);
    }
  }

  public String getMonthString(int month) {
    return (month<monthsInYear.length ? monthsInYear[month] : "");
  }

}

