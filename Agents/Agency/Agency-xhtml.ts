<!doctype tagset system "tagset.dtd">
<tagset name=Agency-xhtml parent=pia-xhtml recursive>

<h1>Agency-XHTML Tagset</h1>

<doc> This tagset is local to the Agency agent.
</doc>

<h2>Headers and Footers</h2>


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
<a href="/&agentName;">&agentName;</a> agent on
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

