<!-- template.inc for Agent -->

<t-data name=tName type=hidden>Agent</t-data>
<t-form label="Template for a new Agent">
This form lets you create a new PIA Agent.  When you are done you will
need to edit many of the new agent's InterForm files, but at least you will
have a good start.<p>

When your agent has been built, you will find its files in
<a href="&dDOFS;/">its home directory</a>.  The header file in that
directory, <a href="&dDOFS;/HEADER.html">HEADER.html</a>, will have
a form for installing the agent.

<if><get form name=dName>
    <then>Alternatively, you can use the following form:

	  <form action="/Agency/install_agent.if" method="POST">
	  <input type="submit" value="Install">
	  Name:<input name="agent" value="&dName;">
	  Type:<input name="type" value="&dType;">
	  </form>

	  After the agent is installed, its home page will be accessible from
	  this PIA as <a href="/&dName;">/&dName;</a>
</if>
</t-form>


<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<!-- $Id$ -->
