<!--  the form to add a url -->

<form  action=&url; method=post>
URL:<input name="url" size=40><input type=submit value="AddURL"><br>
Title/description:<textarea name="title" rows=2 cols=20></textarea>
<process>
<set name=urlid><get agent index="links.dl-2.&FORM.url;"><set>
<if><get name=urlid><then>
<set name=urlid><get agent index="links.dl-2.&FORM.url;"></set>
<set name=urlatag><get agent index="links.dl-1.&urlid;"></set>

</then><else>
 <set name=urlid><get agent name=nextid></set>
 <set agent name=nextid><sum><get name=urlid> 1</sum></set>
 <write file=nextid><get agent name=nextid></write>

<!-- set the text for the canonical link -->
 <if><get name=title form> <then> <set name=txt>&FORM.title;</set></then>
 <else><!-- could read url here and get title -->
<set name=txt>&FORM.url;</set></else></if>

<!--  create an anchor tag for use by caller -->
 <set name=urlatag> <a href="&FORM.url;" added="&month;/&day;/&year;" urlid="&urlid;">&txt;</a></set>

<!--   get features -->
<if><get form name=linkfeatures> <then>
 <set name="linkfeatures"><get form name=linkfeatures></set>
 </then>
 <else>
 <set name="linkfeatures"><dl>
 <repeat &AGENT.features.keys;>
  <dt> <get name=li> </dt>
  <dd> <get index="features.&li;..-1"> </dd>
 </repeat>
 </dl></set>
 </else>
</if>


<!--  create directory structure and files for canonical information -->
 <write file="links/urlid/index.html">
<html><head> 
 <title><get form name=url></title>
 <meta urlid=&urlid; month=&month; day=&day; year=&year; agent=&agentName;> </meta>
</head>
<body>
 <h1><get name=urlatag></h1>
  <dl>
   <dt>  url </dt>
   <dd><get form name=url>
   <dt> description  </dt>
   <dd><get name=txt>
   <dt> features </dt>
   <dd> <get name=linkfeatures>
   <dt> categories </dt>
   <dd><get form name=cats>
  </dl>
</body>
</html>
</write>

<!-- could retrieve url and cache it here -->

<!-- save the index -->
<set agent index="links.dl-2" insert=-1><dt>&FORM.url;</dt><dd>&urlid;</dd></set>
<set agent index="links.dl-1" insert=-1><dt>&urlid;</dt><dd>&urlatag;</dd></set>
<write file="links.html"><get agent name=links></write>
</else></if>
</process>
</form>
