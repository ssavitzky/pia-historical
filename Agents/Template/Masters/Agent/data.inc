<!-- data.inc for Agent template -->
<t-data name=dExpand type=expand></t-data>
<t-form label="Build Agent" nobreak=1 cluster=1>
	Click ``&label;'' when you're ready to expand the template.  This
	will create a new or modified Agent in the specified location.  Click
	``Continue'' to verify entries or view details without actually doing
	the expansion.  </t-form>
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
	>Copyright &copy; 1997 Ricoh Silicon Valley</t-data>
<t-form>A suitable copyright notice.  This will appear in all of your new
	agent's files. </t-form>

<t-data name=dCriteria></t-data>
<t-form label="Criteria">
	The transaction match criteria for this agent. </t-form>

<t-data name=dAbout type=textarea label="About"></t-data>
<t-form>A brief description of your agent, which will appear on its home
	page underneath the heading. </t-form>

<t-data name=dCVS type=checkbox label="Using CVS?">y</t-data>
<t-form>Check this if you are using CVS or RCS for source control, and the
	appropriate ID strings will be included in your files. </t-form>

<t-data name=dNavBar type=checkbox label="Navigation Bar?">y</t-data>
<t-form>Check this if you want your agent's pages to have a standard
	navigation bar at the top and bottom. </t-form>

<t-data name=dNavBarText type=textarea label="Nav-Bar Text">
<a href="/">PIA</a> || <a href="/Agency">Agency</a> ||
<if><test not exact match="&agentType;">&agentName;</test>
    <then><a href="/&agentType;/home.if">&agentType;</a>
          <a href="/&agentType;/"> / </a>
          <a href="/&agentType;/&agentName;">&agentName;</a>:
          <a href="/&agentType;/&agentName;/">index</a>
          <a href="/&agentType;/&agentName;/">help</a>
	  <a href="/&agentType;/&agentName;/options.if">options</a>
    <else><a href="/&agentName;/home.if">&agentName;</a>:
	  <a href="/&agentName;/">index</a>
	  <a href="/&agentName;/help.if">help</a>
	  <a href="/&agentName;/options.if">options</a>
</if>
<hr>
</t-data>
<t-form>The text of the navigation bar.</t-form>

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
<t-data name=x-initialize.if type=file label=initialize.if>y</t-data>
<t-form nobreak=1>
	-- the agent's initialization form.  You will eventually be able to
	   customize this form with a template of its own. </t-form>

<t-data name=x-sample.if type=file label=sample.if>y</t-data>
<t-form nobreak=1>
	-- a sample InterForm file.  Its title and some sample content can be
	   entered below:</t-form>
<t-data name=sampleTitle>Sample Form</t-data>

<t-data name=sampleText type=text>Content for sample Form</t-data>

<t-data name=dIncFiles type=void nobreak=1 label="Include Files:"></t-data>

<t-data name=x-help.inc type=file label=help.inc>y</t-data>
<t-form>-- the agent's help file.</t-form>

<t-data name=m-about.inc type=file label=about.inc>y</t-data>
<t-form>-- the agent's ``about'' file.</t-form>

<t-data name=m-about.inc-recipe type=data>
<set local name=lt><</set><set local name=gt>></set>
<write file="&dstDir/&file;"
>&lt;!-- Read by &dName; home.if  -*-html-helper-*- --&gt;
&dAbout;
&lt;!-- &dCopyright; --&gt;
<if>&dUseCVS;<then><!-- $Id$ --></if>
</write>
</t-data>

<t-data name=x-subhead.inc type=file label=subhead.inc>y</t-data>
<t-form>-- the sub-heading under the heading on each page.</t-form>

<t-data name=x-althead.inc type=file label=althead.inc>y</t-data>
<t-form>-- An alternate sub-heading for the Agent's home page.</t-form>

<t-data name=m-insert.inc type=file label=insert.inc>y</t-data>
<t-form>-- Text to be inserted into the headings.</t-form>

<t-data name=m-insert.inc-recipe type=data>
<set local name=lt><</set><set local name=gt>></set>
<write file="&dstDir/&file;"
>&lt;!-- Read by &dName; home.if  -*-html-helper-*- --&gt;
&dInsert;
&lt;!-- &dCopyright; --&gt;
<if>&dUseCVS;<then><!-- $Id$ --></if>
</write>
</t-data>

<t-data name=dHTMLFiles type=void nobreak=1 label="HTML Files:"></t-data>

<t-data name=x-HEADER.html type=file label=HEADER.html>y</t-data>
<t-form>-- A heading for a directory listing, which will be seen if anyone
	   looks at your agent's home directory with a DOFS.</t-form>

<t-data name=x-doc.html type=file label=doc.html>y</t-data>
<t-form>-- a sample document.  Its title and some sample content can be
	   entered below:</t-form>

<t-data name=docTitle>Documentation</t-data>

<t-data name=docContent type=text>Sample content</t-data>

<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<!-- $Id$ -->

