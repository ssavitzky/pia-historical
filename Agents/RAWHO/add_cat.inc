<!--  add a category -->
<if><get name=catfeatures> <then>
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
<set name=catobj><h1><get name=catname> <a href=/&agentName;/edit_category.if?category=&catname;>(edit)</a></h1>
  <dl>
   <dt>  name </dt>
   <dd><get name=catname></dd>
   <dt>  owner </dt>
   <dd>  <get name=owner>
   <dt> description  </dt>
   <dd><get name=catdesc>
   <dt> links </dt>
   <dd><ul> </ul></dd>
   <dt> created </dt>
   <dd> &month; &day; &year; </dd>
   <dt> features </dt>
   <dd> <get name=catfeatures>
  </dl>
</set>

<!-- add to catObjs -->
 <set agent index="cats" insert=-1><dt>&catname;</dt><dd>&catobj;</dd></set>
<!-- save to file -->
<read interform file=save_cat.inc process>

<!-- add a pointer to the category in categories -->
 <set agent index="categories" insert=-1> <dt> <a href="/~&agentName;/categories/&catname;/index.html" catname=&catname;><get name=catname></a></dt>
<dd><get name=catdesc> </dd>
</set>

<!--  checkpoint of categories -->
 <write file=cats.html><get agent name=cats></write>

<!-- put in calendar -->
<set name=dummy><calendar-day cal=&AGENT.currentcal; day=&day;><a href="/&agentName;/show_category.if?catname=&catname;"><get name=catname></a><br></calendar-day></set>
