<!-- MB3 actors:				 -*-html-helper-*- -->
<!-- Copyright 1998 Ricoh Silicon Valley			   -->
<!-- This file contains the set of active tags used by this agent.
     Normally the file will be read and the contained tags (actors) defined
     at initialization time.  These tags are in addition to the standard
     tagset for agents (e.g. InterForms). -->

<!-- === The tagset actor does not work quite right ===	   -->
<tagset name=MB3-actors base=Standard><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=MB3-actors>



 <!-- actors to manipulate an event calendar -->

<actor tag=calendar-for empty desc="shows events for the specified MONTH in calendar format"> 
 <set name=mymonth local><get element name=month></set>
 <set name=myyear local><get element name=year></set>
 <if> <get name=mymonth> <else><set name=mymonth local><get
  name=month></set></else> </if>
 <if> <get name=myyear> <else><set name=myyear local><get
  name=year></set></else> </if>
 <!-- calendars get cached for efficiency -->
 <if> <get name=calendars agent key=&mymonth;&myyear;> <else>
  <set name=calendar local>
  <calendar month=&mymonth; year=&myyear;></set>
  <set name=d>0</set>
  <set name=qsm local>month=&mymonth;</set>
  <set name=qsy local>year=&myyear;</set>

  <repeat start=2 stop=7 entity=r>
   <repeat start=1 stop=8 entity=c>
    <set name=d><get name=calendar row=&r; col=&c;></set>
    <if><get index="d.b.text"><then>
  <set name=qsd local>day=&d.b.text;</set>
        <set index=d.b insert=0 replace><a href="view_day.if?&qsd;&&qsm;&&qsy;">&d.b.text;</a></set></then></if>
   </repeat>
  </repeat>
<!-- add any events to the calendar -->
<if> <read file=events&AGENT.fseparator;&myyear;&AGENT.fseparator;&mymonth; info> <then>
  <set name=files local> <read file=events&AGENT.fseparator;&myyear;&AGENT.fseparator;&mymonth; directory></set>
  <repeat list=&files; entity=m>
   <set name=file local><read file=events&AGENT.fseparator;&myyear;&AGENT.fseparator;&mymonth;&AGENT.fseparator;&m;
    process></set>
   <set name=myevents local><dummy><get name=file findall=event></dummy></set>
 <repeat list=&myevents; entity=event>
  <calendar-insert calendar=&calendar; day=&event.date.day.text;>
   <view-event-link event=&event;>
  </calendar-insert>
 </repeat>
</repeat>
</then> </if>

<set name=calendars agent key=&mymonth;&myyear;><get name=calendar></set>
</else> </if>
<get name=calendars agent key=&mymonth;&myyear;>
</actor>

<actor tag=calendar-insert desc="Add content as event on DAY to CALENDAR">
 <set name=calendar local><get name=calendar element></set>
 <set name=myday local><get name=day element></set>
 <!-- cannot use built-in handler because we modify the numbers -->
 <set name=days local><get name=calendar findall=td></set>
 <repeat list=&days; entity=d>
  <if><test match=&myday; exact><get index=d.b.a.text></test> <then>
   <set name=d insert=-1><br> &content;</set>
  </then> </if>
 </repeat>
</actor>

<actor tag=events-for empty desc="List of events for DAY MONTH YEAR">
<set name=mymonth local><get element name=month></set>
 <set name=myyear local><get element name=year></set>
 <set name=myday local><get element name=day></set>
 <if> <get name=mymonth> <else><set name=mymonth local><get
  name=month></set></else> </if>
 <if> <get name=myyear> <else><set name=myyear local><get
  name=year></set></else> </if>
 <if> <get name=myday> <else><set name=myday local><get
  name=day></set></else> </if>

 <set name=myfile>events&AGENT.fseparator;&myyear;&AGENT.fseparator;&mymonth;&AGENT.fseparator;&myday;</set>
 <if> <read file=&myfile; info> <then> <read file=&myfile; process></then> </if>

</actor>

 <!-- load options and from content into agent name space -->
 <actor tag=load-options desc="load options and from content into agent name
 space ">
 <set name=options local><dummy><get name=content
 findall=agentoption></dummy></set>
 <repeat list=&options; entity=myoption>
 <set agent name=&myoption.optionname;><get index=myoption.optionvalue></set>
 </repeat>
</actor>

<actor tag=view-day-link desc="insert a link tag to view this DAY MONTH YEAR">
  <set name=qsm local>month=<get element name=month></set>
  <set name=qsy local>year=<get element name=year></set>
  <set name=qsd local><get element name=day></set>
  <set name=sep local>&</set>
 <a href=/&agentName;/view_day.if?day=&qsd;&sep;&qsm;&sep;&qsy;> &content;</a>
</actor>


<actor tag=view-event-link empty desc="insert a link tag to view this EVENT">
  <set name=myevent local><get element name=event></set>
  <set name=qsf local><get  index=myevent.eventfile.text></set>
  <set name=qsi local>&eid=<get index=myevent attr=id></set>
 <a href=/&agentName;/view_event.if?file=&qsf;&qsi;> <get index=myevent.eventtitle.Text></a>
</actor>


 <actor tag=retrieve-event  empty desc="Retrieve event specified by ID from FILE">
 <set name=file local><get element name=file></set>
 <set name=id local><get element name=id></set>
 <set name=events><read file=&file; process></set>
 <set name=events><dummy><get name=events findall=event></dummy></set>
 <repeat list=&events; entity=e>
 <if><test match=&id; exact><get name=e attr=id></test> <then>
  <get name=e>
 </then> </if>
 </repeat>
 </actor>


<!-- object store should eventually take care of this... for now file manip -->
 <actor tag=save-event  empty desc="Save EVENT -- overwrite existing event of
 same id">
 <set name=event local><get element name=event></set>
 <set name=file local><get index=event.eventfile.text></set>
 <set name=id local><get name=event attr=id></set>
 <if> <read file=&file; info> <then>
  <set name=events><read file=&file; process></set>
  <set name=events><get name=events findall=event></set>
  <!-- remove existing entries -->
  <write file=&file;> </write>
  <repeat list=&events; entity=e>
  <if><test match=&id; exact><get name=e attr=id></test> <then>
      </then>
      <else> <!-- add to file -->
      <write file=&file; append><get name=e>
      </write>
      </else>
   </if>
  </repeat>
 </then></if>
 <write file=&file; append><get name=event></write>
 </actor>


<!-- needed a way to convert relative URL's to base URL's -->
<actor tag=full-url empty desc="fully qualifies a URL"><set name=mybase local><get name=base element></set><set name=myurl><get name=url element></set><if> <test match="^http://">&myurl;</test> <then>&myurl;</then> <else><if>   <test match="^/">&myurl;</test>    <then><set local name=leftover><subst   match="^http://[^/]*/" result="">&mybase;</subst></set><subst match="/&leftover;" result="">&mybase;</subst>&myurl;</then>   <else><subst match="[^/]* " result="">&mybase; </subst>&myurl;</else></if></else> </if></actor>

