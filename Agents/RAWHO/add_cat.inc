<!--  the form to add a category -->

<form  action=&url; method=post copy id=addcat>
Category name:
<input name="catname" size=20>
<input type=submit value="AddCategory">
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

<!-- create new  category object -->
<set name=catobj><h1><get form name=catname> <a href=/&agentName;/edit_category.if?category=&FORM.catname;>(edit)</a></h1>
  <dl>
   <dt>  name </dt>
   <dd><get form name=catname></dd>
   <dt> description  </dt>
   <dd><get form name=catdesc>
   <dt> created </dt>
   <dd> &month; &day; &year; </dd>
   <dt> features </dt>
   <dd> <get name=catfeatures>
   <dt> links </dt>
   <dd><ul> </ul></dd>
  </dl>
</set>

<!-- add to catObjs -->
 <set agent index="cats" insert=-1><dt>&catname;</dt><dd>&catobj;</dd></set>
<!-- save to file -->
<read interform file=save_cat.inc process>

<!-- add a pointer to the category in categories -->
 <set agent index="categories" insert=-1> <dt> <a href="/~&agentName;/categories/&catname;/index.html" catname=&catname;><get form name=catname></a></dt>
<dd><get form name=catdesc> </dd>
</set>

<!--  checkpoint of categories -->
 <write file=categories.html><get agent name=categories></write>
</process>
</form>
