<!-- This heading is meant to be included using read. -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley -->
<if><test match=customize>&FORM.topic;</test>
    <then>
	You got here by clicking a ``<a
	href="&pia;&agentPath;/help.if?topic=customize">[help]</a>'' link near
	a ``customize'' link from an Agent or document created using the <a
	href="&agentPath;">&agentName;</a> agent.
    <else><if><test match=extend>&FORM.topic;</test><then>
	You got here from clicking the ``<a
	href="&pia;&agentPath;/help.if?topic=extend">[help]</a>'' link
	in an ``extend'' item in a template form.  This indicates an
	extensible template; you can use the corresponding button to
	add a field to the template's form.  
    <else>
	You got here by by following the <a href="help.if">Help Page</a> link
	on one of <a href="&agentPath;">&agentPath;</a>'s InterForm pages.
</if><p>

The <a href="/&agentName;">&agentName;</a> agent is used for creating and
customizing Agents, NOA's, documents, InterForms, and other files.  You can
get more information from:

<dl>
  <dt> <a href="usage.html">usage.html</a> 
  <dd> how to use the  <a href="/&agentName;">&agentName;</a> agent.
  <dt> <a href="impl.html">impl.html</a> 
  <dd> how the  <a href="/&agentName;">&agentName;</a> agent is implemented
       (which includes information necessary for constructing a template of
       your own).
</dl>

<!-- $Id$ -->
