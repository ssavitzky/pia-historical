<!doctype tagset system "tagset.dtd">
<tagset name=Agency-xhtml parent=pia-xhtml recursive>

<h1>Agency-XHTML Tagset</h1>

<doc> This tagset is local to the Agency agent.  It is worth noting that the
      Agency agent also handles pages in the ``root'' directory, with no
      agent; these are kept in the <code>Agency/ROOT</code> directory,
</doc>

<h2>Legacy operations</h2>

<note author=steve> Note the use of <code>handler=legacy:xxx</code> in the
   following definitions.  It is not necessary; most of these can be defined
   directly, and if any remain they represent places where work needs to be
   done.  It is, however, expedient (i.e. a temporary hack).
</note>

<define element=agent-home empty handler=legacy:agent-home>
   <doc> Determine the home directory of an agent.  Prefixes the agent's name
	 with its type, if necessary, to produce a complete path.
   </doc>
   <define attribute=agent optional>
      <doc> specifies the name of the agent being queried.  Defaults to the
	    name of the current agent.
      </doc>
   </define>
   <define attribute=link optional>
      <doc> If present, the result is a link to the agent's home. 
      </doc>
   </define>
</define>

<define element=agent-running empty handler=legacy:agent-running>
   <doc> Determine whether a given agent is currently running (installed in
	 the PIA).
   </doc>
   <define attribute=name required>
      <doc> specifies the name of the agent being queried.
      </doc>
      <note author=steve> Should almost certainly be changed to "agent".
      </note>
   </define>
</define>



<h2>Headers and Footers</h2>

<define element=nav-bar>
  <doc> A navigation bar, usually placed just above the copyright notice in
	the footer.  Usually fits in a single line.  Content is whatever you
	want to put after the standard start.
  </doc>
  <action>
<a href="/">PIA</a> || <a href="/Agency">Agency</a>:
<a href="/&agentName;/">index</a>
<a href="/&agentName;/agents">agents</a>
  </action>
</define>

<define element=footer empty>
  <doc> This expands into a standard footer.  Go to some lengths to extract
	the year the file was modified from the cvs id. 
  </doc>
  <define attribute=cvsid>
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<hr>
<nav-bar/>
<hr>
<a href="/&AGENT:name;">&AGENT:name;</a> agent on
<if><test exact  match='pia'>&piaUSER;</test>
    <then> the </then>
    <else> &piaUSER;'s</else></if>
<if><test exact match='pia'>&piaUSER;</test>
    <then> information appliance </then>
    <else>Personal Information Agency</else></if><br>
<b>URL:</b> &lt;<a href="&url;">&url;</a>&gt;
<hr>
<set name=myear><subst match="/.* " result=", "><select>
    &attributes;<name>cvsid<eval/><text split>&selected;</text> 3
    </select> </set>
<b>Copyright &copy; &myear; Ricoh Silicon Valley</b><br>
<em><select>&attributes;<name>cvsid<eval/></em>

  </action>
</define>

</tagset>

