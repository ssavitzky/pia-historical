<!-- template.inc for Agent -->

<t-data name=tName type=hidden>Agent</t-data>
<t-form label="Template for new Agent">
This form lets you create a new PIA Agent.  When you are done you will
need to edit many of the new agent's InterForm files, but at least you will
have a good start.<p>

<if>1<get name=dDOFS>
    <then>When your agent has been built, you will find its files in
	  <a href="&dDOFS;/">&dDOFS;/</a>.  The header file in that directory,
	  <a href="&dDOFS;/HEADER.html">HEADER.html</a>, will have a form for
	  installing the agent.<p>
</if>
</t-form>


<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<!-- $Id$ -->
