<!-- This heading is meant to be included using read. -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley -->
<if><test match=toolbar>&FORM.topic;</test>
    <then>
	You got here by clicking the 
	``<a href="&pia;&agentPath;/help.if?topic=toolbar">[help]</a>'' link
	on a toolbar generated by the <a href="&agentPath;">&agentName;</a>
	agent, which is a sub-agent of the <a
	href="&agentType;">&agentType;</a> agent.  . 
    <else>
	You got here by by following the <a href="help.if">Help Page</a> link
	on one of <a href="&agentPath;">&agentPath;</a>'s InterForm pages.
</if><p>
<a href="/&agentType;">&agentType;</a> agents have a great deal more
information on their <a href="/&agentPath;">home pages</a>, including
information about how to customize their toolbars.<p>

The standard items on the toolbar generated by a
<a href="/&agentType;">&agentType;</a> agent include:
<dl compact>
  <dt> [X]
  <dd> -&gt; <a href="&agentPath;/hide.if">&agentPath;/hide.if?hide=true</a>
       <br>hides the agent's toolbar.
  <dt> help
  <dd> -&gt; <a href="&agentPath/help.if">&agentPath/help.if</a>, this help page
  <dt> [~]
  <dd>  -&gt; <a href="/My">/My</a>, your personal PIA data directory.
</dl>

<if><agent-running name=History>
    <then><p>
	You should also
	see the <a href="/History">History agent's home page</a> for more
	information about the portion of the toolbar that starts out
	``[<a href="/History">Hist</a>...]''
</if>

<!-- $Id$ -->