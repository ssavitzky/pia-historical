<set name=urlid><get agent index="links..dl-2.&formurl;"></set>
<if><get name=urlid><then>
<set name=urlid><get agent index="links..dl-2.&formurl;"></set>
<set name=urlatag><get agent index="links..dl-1.&urlid;"></set>
&formurl; already added as &urlid;.<br>
</then><else>
 <set name=urlid><sum digits=0><get agent name=nextid></sum></set>
 <set agent name=nextid><sum digits=0><get name=urlid> 1</sum></set>
 <write file=nextid><get agent name=nextid></write>

<!-- set the text for the canonical link -->
 <if><get name=formtitle> <then> <set name=txt>&formtitle;</set></then>
 <else><!-- could read url here and get title -->
<set name=txt>&formurl;</set></else></if>

<!--  create an anchor tag for use by caller -->
 <set name=urlatag> <a href="&formurl;" added="&month;/&day;/&year;" urlid="&urlid;">&txt;</a></set>

<!--   get features --> 
<!-- each feature has a list associated with it.  The first item of the list
is taken as the default value -->
<if><get form name=linkfeatures> <then>
 <set name="linkfeatures"><get form name=linkfeatures></set>
 </then>
 <else>
 <set name="linkfeatures"><dl>
 <repeat list=&AGENT.features.keys;>
  <dt> <get name=li> </dt>
  <dd> <get index="AGENT.features.&li;.li-1.-1"> </dd>
 </repeat>
 </dl></set>
 </else>
</if>


<!--  create directory structure and files for canonical information -->
 <write file="links/&urlid;/index.html">
<html><head> 
 <title><get name=formurl></title>
 <meta urlid=&urlid; month=&month; day=&day; year=&year; agent=&agentName;>
 </meta>
  <if><get name=formbase><then>
       <base href=&formbase;>
  </then></if>
</head>
<body>
 <h1><get name=urlatag></h1>
  <dl>
   <dt>  url </dt>
   <dd><get name=formurl>
   <dt> description  </dt>
   <dd> <if><get name=txt><then><get name=txt></then><else><get name=formurl></else></if></dd>
   <dt> features </dt>
   <dd> <get name=linkfeatures>
   <dt> category </dt>
   <dd><get name=formcats> </dd>
  </dl>
</body>
</html>
</write>

<!-- add to any categories -->
 <if> <get name=formcats> <then>
 <repeat list=&formcats;>
  <set name=clnks><get agent index=cats.&li; key=links></set>
  <set index="clnks" insert=-1><li><get name=urlatag></li></set>
 </repeat>
</then></if>

<!-- could retrieve url and cache it here -->

<!-- save the index -->
<set name=codes><get agent index="links..dl-2"></set><set name=codes insert=-1><dt>&formurl;</dt><dd>&urlid;</dd></set>
<set name=codes><get agent index="links..dl-1"></set><set name=codes insert=-1><dt><a href="/&agentName;/show_link.if?urlid=&urlid;">&urlid;</a></dt><dd>&urlatag;</dd></set>
<write file="links.html"><get agent name=links></write>

<!-- put in calendar -->

 <set name=dummy><calendar-day cal=&AGENT.currentcal; day=&day;><a href="/&agentName;/show_link.if?urlid=&urlid;"><get name=urlid></a></calendar-day></set>

</else></if>
