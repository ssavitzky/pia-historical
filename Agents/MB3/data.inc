<!-- From system/&tPath;/Agent -->
<!--   in &dBase;/Agents/MB3 -->
<!-- AUTOMATICALLY REBUILT WHEN TEMPLATE EXPANDED!  EDIT WITH CARE! -->

<t-data name=tBase type=hidden>system</t-data>

<t-data name=tName type=hidden label=Template for a new Agent>Agent</t-data>
<t-doc>
This form lets you create a new PIA Agent.  When you are done you will
need to edit many of the new agent's InterForm files, but at least you will
have a good start.<p>

When your agent has been built, you will find its files in
<a href="&dDOFS;/">its home directory</a>.  The header file in that
directory, <a href="&dDOFS;/HEADER.html">HEADER.html</a>, will have
a form for installing the agent.

Alternatively, you can use the following form:

	  <form action="/Agency/install_agent.if" method="POST">
	  <input type="submit" value="Install">
	  Name:<input name="agent" value="&dName;">
	  Type:<input name="type" value="&dType;">
	  </form>

	  After the agent is installed, its home page will be accessible from
	  this PIA as <a href="/&dName;">/&dName;</a></t-doc>

<t-data name=dExpand type=expand label=Build Agent></t-data>
<t-doc>
	Click ``&label;'' when you're ready to expand the template.  This
	will create a new or modified Agent in the specified location.  Click
	``Continue'' to verify entries or view details without actually doing
	the expansion.  </t-doc>

<t-data name=details type=checkbox label=Details></t-data>
<t-doc>Check ``&label;'' to see exactly what is in the form you're submitting
	and the data file that will be built.</t-doc>

<t-data name=dReq type=void label=Required:><hr></t-data>

<t-data name=dName type=ident label=Agent Name>MB3</t-data>
<t-doc>
	The name and type of the new Agent.  Must be valid directory names.
	By convention, the names of agents are capitalized unless they
	are sub-agents of some other agent, called their ``type''. </t-doc>

<t-data name=dType type=ident label=Type></t-data>
<t-doc>
	The type of the new Agent, if non-blank, must be the name of some
	other Agent.  </t-doc>

<t-data name=dRoot type=root label=Agent Root>piaDIR</t-data>
<t-doc>
	Set the root directory for the new agent.  This is normally
	your PIA user-data directory. </t-doc>

<t-data name=dPath type=path label=Path>Agents</t-data>
<t-doc>
	The path from the root to the parent directory of your new
	Agent is currently <code>Agents</code>.  If your agent has another
	agent as its Type, you may need to add the type agent's name to the
	path.</t-doc>

<t-data name=dOpt type=void label=Optional:><hr></t-data>

<t-data name=dTitle type= label=Title>MediaBoard 3</t-data>
<t-doc>The title of the new Agent.  This will appear as the heading on the
	new Agent's home page. </t-doc>

<t-data name=dCopyright type= label=Copyright>Copyright © 1998 Ricoh Silicon Valley</t-data>
<t-doc>A suitable copyright notice.  This will appear in all of your new
	agent's files. </t-doc>

<t-data name=dCriteria type= label=Criteria></t-data>
<t-doc>
	The transaction match criteria for this agent. </t-doc>

<t-data name=dAbout type=textarea label=About>This agent is used for scheduling and controlling
the Ricoh MB-3 Media Board.</t-data>
<t-doc>A brief description of your agent, which will appear on its home
	page underneath the heading. </t-doc>

<t-data name=dCVS type=checkbox label=Using CVS?>on</t-data>
<t-doc>Check this if you are using CVS or RCS for source control, and the
	appropriate ID strings will be included in your files. </t-doc>

<t-data name=dNavBar type=checkbox label=Navigation Bar?>on</t-data>
<t-doc>Check this if you want your agent's pages to have a standard
	navigation bar at the top and bottom. </t-doc>

<t-data name=dNavBarText type=textarea label=Nav-Bar Text><a href="/">PIA</a> || <a href="/Agency">Agency</a> ||
<a href="/Template/home.if">Template</a>:
	  <a href="/Template/">index</a>
	  <a href="/Template/help.if">help</a>
	  <a href="/Template/options.if">options</a>

<hr></t-data>
<t-doc>The text of the navigation bar.</t-doc>

<t-data name=dFiles type=void label=Files to Expand:><hr></t-data>
<t-doc>
	This section lets you select the files that will be included in your
	new Agent.  If you are using this template to customize an existing
	agent, you should unselect those files you wish to leave alone.</t-doc>

<t-data name=dIfFiles type=void label=InterForms:></t-data>

<t-data name=x-home.if type=file label=home.if>on</t-data>
<t-doc>
	-- the agent's home page.</t-doc>

<t-data name=x-initialize.if type=file label=initialize.if>on</t-data>
<t-doc>
	-- the agent's initialization form.  You will eventually be able to
	   customize this form with a template of its own. </t-doc>

<t-data name=x-sample.if type=file label=sample.if>on</t-data>
<t-doc>
	-- a sample InterForm file.  Its title and some sample content can be
	   entered below:</t-doc>

<t-data name=sampleTitle type=>Sample Form</t-data>

<t-data name=sampleText type=text>Content for sample Form</t-data>

<t-data name=dIncFiles type=void label=Include Files:></t-data>

<t-data name=x-help.inc type=file label=help.inc>on</t-data>
<t-doc>-- the agent's help file.</t-doc>

<t-data name=m-about.inc type=file label=about.inc>on</t-data>
<t-doc>-- the agent's ``about'' file.</t-doc>

<t-data name=m-about.inc-recipe type=data><set local name="lt"><</set><set local name="gt">></set>
<write file="&dstDir/&file;">&lt;!-- Read by &dName; home.if  -*-html-helper-*- --&gt;
&dAbout;
&lt;!-- &dCopyright; --&gt;
<if>&dUseCVS;<then><!-- $Id$ --></then></if>
</write></t-data>

<t-data name=x-subhead.inc type=file label=subhead.inc>on</t-data>
<t-doc>-- the sub-heading under the heading on each page.</t-doc>

<t-data name=x-althead.inc type=file label=althead.inc>on</t-data>
<t-doc>-- An alternate sub-heading for the Agent's home page.</t-doc>

<t-data name=m-insert.inc type=file label=insert.inc>on</t-data>
<t-doc>-- Text to be inserted into the headings.</t-doc>

<t-data name=m-insert.inc-recipe type=data><set local name="lt"><</set><set local name="gt">></set>
<write file="&dstDir/&file;">&lt;!-- Read by &dName; home.if  -*-html-helper-*- --&gt;
&dInsert;
&lt;!-- &dCopyright; --&gt;
<if>&dUseCVS;<then><!-- $Id$ --></then></if>
</write></t-data>

<t-data name=dHTMLFiles type=void label=HTML Files:></t-data>

<t-data name=x-HEADER.html type=file label=HEADER.html>on</t-data>
<t-doc>-- A heading for a directory listing, which will be seen if anyone
	   looks at your agent's home directory with a DOFS.</t-doc>

<t-data name=x-doc.html type=file label=doc.html>on</t-data>
<t-doc>-- a sample document.  Its title and some sample content can be
	   entered below:</t-doc>

<t-data name=docTitle type=>Documentation</t-data>

<t-data name=docContent type=text>Sample content</t-data>

<!-- Made by steve on 1998/01/12 at 16:46 -->
