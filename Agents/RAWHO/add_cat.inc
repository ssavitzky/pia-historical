<!--  the form to add a category -->

<form  action=&url; method=post copy id=addcat>
Category name:
<input name="catname" size=20>
<input type=submit value="AddCategory"><br>
Description:<textarea name="catdesc" rows=2 cols=20></textarea>

<process copy>
 <set  name=catname><get form name=catname></set>
<!--   get features -->
<if><get form name=catfeatures> <then>
 <set name="catfeatures"><get form name=catfeatures></set>
 </then>
 <else>
 <set name="catfeatures"><dl>
 <repeat list=&AGENT.features.keys;>
  <dt> <get name=li> </dt>
  <dd>  <get index="AGENT.features.&li;.li-1.-1" > </dd>
 </repeat>
 </dl></set>
 </else>
</if>

<!-- create new directory and index for this category -->
<write file=categories/&catname;/index.html>
<html><head> 
 <title><get form name=catname></title>
 <meta catname=&catname; month=&month; day=&day; year=&year; agent=&agentName;> </meta>
</head>
<body>
 <h1><get form name=catname></h1>
  <dl>
   <dt>  name </dt>
   <dd><get form name=catname>
   <dt> description  </dt>
   <dd><get form name=catdesc>
   <dt> superCategories </dt>
   <dd><get form name=supercats>
   <dt> features </dt>
   <dd> <get name=catfeatures>
  </dl>
<h2> Links </h2>
<ul>
</ul>
</body>
</html>
</write>

<!-- add a pointer to the category in categories -->
 <set agent index="categories" insert=-1> <dt> <a href="/~&agentName;/categories/&catname;/index.html" catname=&catname;><get form name=catname></a></dt>
<dd><get form name=catdesc> </dd>
</set>

<!--  checkpoint of categories -->
 <write file=categories.html><get agent name=categories></write>
</process>
</form>
