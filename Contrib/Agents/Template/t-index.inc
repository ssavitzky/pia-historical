<!-- Template index sub-form:			 -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley			   -->

<actor tag=template-link empty>
  <set name=base><get element name=type></set>
  <a href="form.if?tName=&li;&amp;tBase=&base;">&li;</a>
  <a href="form.if?tName=&li;&amp;tBase=&base;&amp;details=y">[details]</a>
  <if><read info=path file="&path;/&li;/subforms.inc"><then><br>
	<read process file="&path;/&li;/subforms.inc"></if>
</actor>

<actor tag=subform-link>
  <set name=file><get element name=file></set>
  <set name=t-url>form.if?tName=&li;&amp;tBase=&base;&amp;tDataFile=&file;</set>
  <a href="&t-url;">&content;</a>
</actor>

<!-- If a subdirectory has an subforms.inc, include it on the next line. -->

<h3>System Templates</h3>

<set entity name=path><read directory interform info=p file="Masters"></set>
in: <code>&path;</code>
<set entity name=dir><read directory file="&path"></set>
<ul foreach list="&dir;">
   <if><read info=path file="&path;/&li;/template.inc"><then>
   <li><template-link type=system name="&li;"></if>
</ul>

<h3>User-Defined Templates</h3>

<set entity name=path><read directory info=p file="Masters"></set>
in: <code>&path;</code>
<set entity name=dir><read directory file="&path"></set>
<ul foreach list="&dir;">
   <if><read info=path file="&path;/&li;/template.inc"><then>
   <li><template-link type=user name="&li;"></if>
</ul>


<!-- $Id$ -->