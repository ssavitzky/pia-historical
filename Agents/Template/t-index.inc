<!-- Template index sub-form:			 -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley			   -->

<actor tag=template-link empty>
  <set name=t-path>&path;/&li;</set>
  <a href="form.if?tName=&li;&amp;tDir=&t-path;">&li;</a>
</actor>

<h3>System Templates</h3>

<set entity name=path><read directory interform info=p file="Masters"></set>
in: <code>&path;</code>
<set entity name=dir><read directory file="&path"></set>
<ul foreach list="&dir;">
   <if><read info file="&path;/&li;/template.inc"><then>
   <li><template-link type=system name="&li;"></if>
</ul>

<h3>User-Defined Templates</h3>

<set entity name=path><read directory info=p file="Masters"></set>
in: <code>&path;</code>
<set entity name=dir><read directory file="&path"></set>
<ul foreach list="&dir;">
   <if><read info file="&path;/&li;/template.inc"><then>
   <li><template-link type=user name="&li;"></if>
</ul>


<!-- $Id$ -->
