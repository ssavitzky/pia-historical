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
  <set name=qsm>month=&mymonth;</set>
  <set name=qsy>year=&myyear;</set>
  <repeat start=2 stop=7 entity=r>
   <repeat start=1 stop=8 entity=c>
    <set name=d><get name=calendar row=&r; col=&c;></set>
    <if><get index="d.b.text"><then>
        <set index=d.b insert=0 replace><a href="view_day.if?d=&d.b.text;&&qsm;&&qsy;">&d.b.text;</a></set></then></if>
   </repeat>
  </repeat>
<!-- add any events to the calendar -->
  <set name=events local> <read file=events/&myyear;/&mymonth; directory></set>
  <repeat list=&events; entity=m>
   <set name=event local><read file=events/&myyear;/&mymonth;/&m;
    process></set>
    <calendar-insert calendar=&calendar; day=&event.date.day;> <a event=&event.eventfile;
    href=view_event.if?event=&event.eventfile;>&event.eventtitle; </a>
  </calendar-insert>
</repeat>

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
   <set name=d insert=-1>&content; <br></set>
  </then> </if>
 </repeat>
</actor>

<actor tag=events-for desc="List of events for DAY MONTH YEAR">
<set name=mymonth local><get element name=month></set>
 <set name=myyear local><get element name=year></set>
 <set name=myyear local><get element name=day></set>
 <if> <get name=mymonth> <else><set name=mymonth local><get
  name=month></set></else> </if>
 <if> <get name=myyear> <else><set name=myyear local><get
  name=year></set></else> </if>
 <if> <get name=myday> <else><set name=myday local><get
  name=day></set></else> </if>

 <!-- use calendars for some efficiency -->
 <set name=calendar local><calendar-for month=&mymonth; year=&myyear;</set>
 <set name=days local><get name=calendar findall=td></set>
 <repeat list=&days; entity=d>
  <if><test match=&myday; exact><get index=d.b.a.text></test> <then>
    <repeat list=&d.a; entity=a>
     <if> <get name=a attr=event> <then>
      <set name=file><get name=a attr=event></set>
       <read file=&file; process>
     </then>
     </if> 
    </repeat>
  </then> </if>
 </repeat>
</actor>



<!-- needed a way to convert relative URL's to base URL's -->
<actor tag=full-url empty desc="fully qualifies a URL"><set name=mybase local><get name=base element></set><set name=myurl><get name=url element></set><if> <test match="^http://">&myurl;</test> <then>&myurl;</then> <else><if>   <test match="^/">&myurl;</test>    <then><set local name=leftover><subst   match="^http://[^/]*/" result="">&mybase;</subst></set><subst match="/&leftover;" result="">&mybase;</subst>&myurl;</then>   <else><subst match="[^/]* " result="">&mybase; </subst>&myurl;</else></if></else> </if></actor>

