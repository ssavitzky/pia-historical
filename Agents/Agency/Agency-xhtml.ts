<!doctype tagset system "tagset.dtd">
<tagset name=Agency-xhtml parent=pia-xhtml recursive>

<h1>Agency-XHTML Tagset</h1>

<doc> This tagset is local to the Agency agent.  It is worth noting that the
      Agency agent also handles pages in the ``root'' directory, with no
      agent; these are kept in the <code>Agency/ROOT</code> directory,
</doc>

<h2>Legacy operations</h2>

<define element=agent-home empty handler=legacy:agent-home>
   <doc> determine the home directory of an agent.  Doesn't appear to work.
   </doc>
</define>

<h2>Headers and Footers</h2>

<define element=nav-bar>
  <doc> A navigation bar, usually placed just above the copyright notice in
	the footer.  Usually fits in a single line.  Content is whatever you
	want to put after the standard start.
  </doc>
  <action>
<a href="/">PIA</a> || <a href="/Agency">Agency</a>:
<a href="&agentName;/">index</a>
<a href="&agentName;/agents">agents</a>
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

