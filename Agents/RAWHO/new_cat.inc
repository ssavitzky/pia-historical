<!--  the form to add a category -->

<form  action=&url; method=post copy id="addcat">
<input type=submit value="AddCategory">
:
<input name="catname" size=20>
Description:<textarea name="catdesc" rows=1 cols=20></textarea>

<process copy>
 <set  name=catname><get form name=catname></set>
 <set  name=catdesc><get form name=catdesc></set>
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

<read interform process file="add_cat.inc">

</process>
</form>
