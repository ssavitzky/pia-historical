<!--  the form to add a url -->

<form  action=&url; method=post id="addurl" copy>
<input type=submit value="AddURL">:<input name="url" size=30>
Description:<textarea name="title" rows=1 cols=20></textarea>
<process copy>
 <set name=formurl><get  form name=url></set>
 <set name=formtitle><get  form name=title></set>
 <read interform process file="add_url.inc">
</process>
</form>
