<!-- data.inc for Agent/home-pg template -->
<t-data name=tHeading type=data>Home Page</t-data>
<t-form label="Agent Home-Page Template">
	This sub-form is a quick way of creating or modifying an agent's home
	page without having to specify the entire agent.  See the main
	template for more documentation.<p>

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

<t-data name=dExpand type=expand></t-data>
<t-form label="Build" nobreak=1 cluster=1>
	Click ``&label;'' when you're ready to expand the template.  </t-form>
<t-data name=details type=checkbox label="Details"></t-data>
<t-form>Check ``&label;'' to see exactly what is in the form you're submitting
	and the data file that will be built.</t-form>

<!-- Required fields: -->
<t-data name=dReq type=void label="Required:"><hr></t-data>

<t-data name=dName type=ident required=1></t-data>
<t-form label="Agent Name" nobreak=1 cluster=1>
	The name and type of the new Agent.  Must be valid directory names.
	By convention, the names of agents are capitalized unless they
	are sub-agents of some other agent, called their ``type''. </t-form>
<t-data name=dType type=ident></t-data>
<t-form label="Type">
	The type of the new Agent, if non-blank, must be the name of some
	other Agent.  </t-form>
<t-data name=dRoot type=root required=1>usrDIR</t-data>
<t-form label="Agent Root" nobreak=1 cluster=1>
	Set the root directory for the new agent.  This is normally
	your PIA user-data directory. </t-form>
<t-data name=dPath type=path required=1>Agents</t-data>
<t-form label="Path">
	The path from the root to the parent directory of your new
	Agent is currently <code>&dPath;</code>.  If your agent has another
	agent as its Type, you may need to add the type agent's name to the
	path.</t-form> 
<!-- Optional fields: -->
<t-data name=dOpt type=void label="Optional:"><hr></t-data>

<t-data name=dTitle label="Title"></t-data>
<t-form>The title of the new Agent.  This will appear as the heading on the
	new Agent's home page. </t-form>

<t-data name=dCopyright label="Copyright"
	>Copyright &copy; 1998 Ricoh Silicon Valley</t-data>
<t-form>A suitable copyright notice.  This will appear in all of your new
	agent's files. </t-form>

<t-data name=dCVS type=checkbox label="Using CVS?">y</t-data>
<t-form>Check this if you are using CVS or RCS for source control, and the
	appropriate ID strings will be included in your files. </t-form>

<t-data name=dNavBar type=checkbox label="Navigation Bar?">y</t-data>
<t-form>Check this if you want your agent's pages to have a standard
	navigation bar at the top and bottom. </t-form>


<!-- Files to expand.  Eventually this should use t-file -->
<t-data name=dFiles type=void label="Files to Expand:"><hr></t-data>
<t-form nobreak=1>
	This section lets you select the files that will be included in your
	new Agent.  If you are using this template to customize an existing
	agent, you should unselect those files you wish to leave alone.</t-form>

<t-data name=dIfFiles type=void nobreak=1 label="InterForms:"></t-data>

<t-data name=x-home.if type=file label=home.if>y</t-data>
<t-form nobreak=1>
	-- the agent's home page.</t-form>

<t-data name=x-subhead.inc type=file label=subhead.inc>y</t-data>
<t-form>-- the sub-heading under the heading on each page.</t-form>

<t-data name=x-althead.inc type=file label=althead.inc>y</t-data>
<t-form>-- An alternate sub-heading for the Agent's home page.</t-form>

<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<!-- $Id$ -->

