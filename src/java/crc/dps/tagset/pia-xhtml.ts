<!doctype tagset system "tagset.dtd">
<tagset name=pia-xhtml parent=xhtml recursive>

<h1>PIA XHTML Tagset</h1>

<doc> This the version of the XHTML tagset used by default in the PIA.
</doc>

<h2>Form-Processing Tags</h2>

Note that we only need these inside the PIA.

<h3>Submit-Forms</h3>
<define element=submit-forms handler=submit >
  <doc> Submit each form in the content to its target.  Results, if any,
	are discarded.
  </doc>
</undefine>

<h3>Form and its components</h3>
<define element=form >
  <doc>
  </doc>
  <define attribute= optional>
    <doc> 
    </doc>
  </define>
</define>

<h4>Sub-elements of <tag>form</tag></h4>
<ul>
  <li> <define element=process parent=form quoted handler>
         <doc> The content of a <tag>process</tag> element is expanded only
	       when the <tag>form</tag> that contains it is being processed as
	       a result of a <code>post</code> or <code>query</code>
	       submission. 
         </doc>
       </define>
</ul>

<h2>Legacy Tags</h2>

<note author=steve> The use of <code>handler=legacy:xxx</code> was a
   convenient temporary expedient but is no longer necessary. The cannonical
   location for agent-specific handlers is in the <code>crc.pia.agent</code>
   package.  More generic but PIA-specific handlers are in 
   <code>crc.pia.handle</code>. 
</note>

<define element=agent-list handler=crc.pia.handle.agentList empty>
  <doc> List agents, possibly those with a given type.
  </doc>
   <define attribute=type optional>
      <doc> specifies the type of the agents to be listed.
      </doc>
   </define>
   <define attribute=subs boolean optional>
      <doc> If present, specifies that only sub-agents of the given type will
	    be listed. 
      </doc>
   </define>
</define>

<define element=agent-running empty handler=crc.pia.handle.agentRunning>
   <doc> Determine whether a given agent is currently running (installed in
	 the PIA).
   </doc>
   <define attribute=name required>
      <doc> specifies the name of the agent being queried.
      </doc>
      <note author=steve> It was once thought that this should be renamed
	``agent'', but <em>nothing</em> else uses that, so it would be a
	mistake. 
      </note>
   </define>
</define>

<define element=user-message handler=crc.pia.handle.userMessage>
  <doc> Output a message to the user.
  </doc>
</define>

<define element=trans-control handler=crc.pia.handle.transControl>
  <doc> Make content into a transaction control.
  </doc>
</define>

<h2>Page Components</h2>

<h3>Graphics</h3>

<define entity=blank-170x1>
  <value><img src="/Icon/white170x1.gif" width=170 height=1
		alt=" "></value>
</define>
<define entity=blue-dot>
  <value><img src="/Icon/dot-blue.gif"
		height=20 width=20 alt="*"></value>
</define>


<h2>Page Components</h2>

<h3>Utility tags for use in page components</h3>
<define element=xa>
  <doc> Either an anchor link or a bold name.  Used on lines that
	contain links to any of several different pages.
  <doc>
  <define attribute=page>
    <doc> The base name of the current page, matched against the URL (href
	  attribute).
    </doc>
  </define>
  <define attribute=href>
    <doc> The URL of the page.
    </doc>
  </define>
  <action><if><test match="&attributes:page;">&attributes:href; </test>
		<then><b>&content;</b></then>
		<else><a href="&attributes:href;">&content;</a></else>
	  </if>
  </action>
</define> 

<define element=xopt>
  <doc> Output the content if the page attribute matches one of the
	names listed in the pages attribute.  Used for an indicator
	(e.g, a blue dot) on lines that contain links to several
	different pages.
  <doc>
  <define attribute=page required>
    <doc> the base name of the current page, matched against the
	  <code>pages</code> attribute.
    </doc>
  </define>
  <define attribute=pages required>
    <doc> space-separated names of the pages represented on this line.
    </doc>
  </define>
  <action><if><test match="&attributes:page;">&attributes:pages; </test>
		<then>&content;</then>
	  </if>
  </action>
</define> 

<h3>Headers and Footers</h3>

<define element=header>
  <doc> This expands into a standard header.  The content is the
	title, and ends up assigned to the entity <code>title</code>.  This
	element also initializes some common entities.
  </doc>
  <define attribute=show-date optional>
    <doc> If present, show the date underneath the title.
    </doc>
  </define>
  <action>
<if><get name=content><then><set name=title>&content;</set></if>
<set name=agentNames><text sort case>&agentNames;</set>
<if><get name=ltitle>
    <else><set name=ltitle><a href="/&agentName;">&AGENT:name;</a>:</if>

<table cellspacing=0 cellpadding=0 border=0>
<tr nowrap nobr><th align=left width=170 valign=bottom nowrap><a
	href="http://rsv.ricoh.com/"><img src="/Icon/ricoh.gif"
	border=0 width=170 height=48 alt="R I C O H"></a></th>
    <th width=170 nowrap></th>
    <th align=left nowrap nobr valign=center width=170>
        <a href="/"><img width=85 height=45 border=0
	    	         src="/Icon/pia45.gif" alt=PIA></a></th></tr>
<tr height=2><td colspan=3  nowrap nobr
    ><img src="/Icon/rule.gif" height=6 width=469></td></tr>
<!--img src="/Icon/blackline425.gif" height=2 width=425-->
<tr nowrap nobr><th align=left valign=top><a href="http://rsv.ricoh.com/"><img
	src="/Icon/ricoh-silicon-valley.gif" alt="RICOH SILICON VALLEY"
	border=0 width=170 height=21></a></th>
    <th align=right valign=top width=170>&ltitle;&nbsp; </th>
    <th align=left colspan=2><if><get entity name=title>
	<then><get entity name=title></then>
	<else>&fileName;</else></if></th></tr>
<if>&attributes:show-date;<then>
<tr nowrap nobr>
    <td colspan=2><td>&dayName;, &year;-&month;-&day;, &time;:&second;</tr>
</if>
</table>
  </action>
</define>

<define element=sub-head quoted>
  <doc> A secondary table located immediately under the header.
	Content should consist of additional table rows.
  </doc>
  <define attribute=page>
    <doc> the base name of the page, e.g. <code>index</code> or
	  <code>home</code>.
    </doc>
  </define>
  <action>
<table cellspacing=0 cellpadding=0 border=0>
<tr><th align=center valign=center nowrap width=170><br><include src=insert>
    <td>
    <table cellspacing=0 cellpadding=0 border=0>
    <tr><th align=left nowrap width=170>&blank-170x1;<td><br>
    <tr><th align=right><xopt page="&attributes:page;"
			      pages="home index help options">&blue-dot;</xopt>
	<td> <xa href="home" page="&attributes:page;">Home</xa>
    	     <xa href="index" page="&attributes:page;">Index</xa>
    	     <xa href="help" page="&attributes:page;">Help</xa>
	     <xa href="options" page="&attributes:page;">Options</xa>
    </tr>
    <set name=page>&attributes:page;</set>
    <expand><get name=content></expand>
  </table>
</table>
  </action>
</define>

<define element=nav-bar>
  <doc> A navigation bar, usually placed just above the copyright notice in
	the footer.  Usually fits in a single line.  Content is whatever you
	want to put after the standard start.
  </doc>
  <action>
<a href="/">PIA</a> || <a href="/Agency">Agency</a> ||
<if><test not exact match="&agentType;">&agentName;</test>
    <then><a href="/&agentType;/home">&agentType;</a>
          <a href="/&agentType;/"> / </a>
          <a href="/&agentType;/&agentName;">&agentName;</a>:
          <a href="/&agentType;/&agentName;/">index</a>
	  <a href="/&agentType;/&agentName;/options">options</a>
          <a href="/&agentType;/&agentName;/help">help</a> (
	     <a href="/&agentType;/&agentName;/help#context-specific">specific</a>
	     <a href="/&agentType;/&agentName;/help#general">general</a> )
    <else><a href="/&agentName;/home">&agentName;</a>:
	  <a href="/&agentName;/">index</a>
	  <a href="/&agentName;/options">options</a>
	  <a href="/&agentName;/help">help</a> (
	     <a href="/&agentName;/help#context-specific">specific</a>
	     <a href="/&agentName;/help#general">general</a> )
</if>
  </action>
</define>

<define element=footer empty>
  <doc> This expands into a standard footer, including a ``navigation bar''.
	Go to some lengths to extract the year the file was modified from the
	cvs id.
  </doc>
  <define attribute=cvsid>
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<hr>
<nav-bar/>
<hr>
<set name=myear><subst match="/.* " result=", "><extract>
    &attributes;<name>cvsid<eval/><text split>&list;</text> 3
    </extract> </set>
<b>Copyright &copy; &myear; Ricoh Silicon Valley</b><br>
<em><extract>&attributes;<name>cvsid<eval/></extract></em>
  </action>
</define>

<define element=short-footer empty>
  <doc> This expands into a short-form footer: just the CVS id and copyright
	notice. 
  </doc>
  <define attribute=cvsid>
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<hr>
<set name=myear><subst match="/.* " result=", "><extract>
    &attributes;<name>cvsid<eval/><text split>&list;</text> 3
    </extract> </set>
<b>Copyright &copy; &myear; Ricoh Silicon Valley</b><br>
<em><extract>&attributes;<name>cvsid<eval/></em>
  </action>
</define>

<define element=inc-footer empty>
  <doc> This expands into a tiny footer for include files. 
  </doc>
  <define attribute=cvsid>
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<set name=myear><subst match="/.* " result=", "><extract>
    &attributes;<name>cvsid<eval/><text split>&list;</text> 3
    </extract> </set>
<set name=incfn><extract>&attributes;<name>cvsid<eval/>
    <text split>&list;</text> 1
    </extract><extract>&attributes;<name>cvsid<eval/>
    <text split>&list;</text> 2</extract></set>
<h6 align=right>&incfn; &copy; &myear; Ricoh Silicon Valley<h6>
  </action>
</define>

<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

