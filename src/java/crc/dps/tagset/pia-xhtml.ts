<!doctype tagset system "tagset.dtd">
<tagset name=pia-xhtml parent=xhtml recursive>

<h1>PIA XHTML Tagset</h1>

<doc> This the version of the XHTML tagset used by default in the PIA.
</doc>

<h2>Form-Processing Tags</h2>

Note that we only need these inside the PIA.

<h3>Submit</h3>
<define element=submit handler=legacy:submit-forms >
  <doc>
  </doc>
  <define attribute= optional>
    <doc> 
    </doc>
  </define>
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


<h2>Page Components</h2>

<h3>Graphics</h3>

<define entity=blank-170x1>
  <value><img src="/Agency/Icons/white170x1.gif" width=170 height=1
		alt=" "></value>
</define>
<define entity=blue-dot>
  <value><img src="/Agency/Icons/dot-blue.gif"
		height=20 width=20 alt="*"></value>
</define>


<h2>Page Components</h2>

<h3>Utility tags for use in page components</h3>
<define element=xa>
  <doc> Either an anchor link or a bold name.  Used on lines that
	contain links to any of several different pages.
  <doc>
  <define attribute=page>
    <doc> the base name of the current page, matched against the URL (href
	  attribute).
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
  <define attribute=page>
    <doc> the base name of the current page, matched against the
	  <code>pages</code> attribute.
    </doc>
  </define>
  <define attribute=pages>
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
  <action>
<if><get name=content><then><set name=title>&content;</set></if>
<set name=agentNames><text sort case>&agentNames;</set>
<if><get name=ltitle>
    <else><set name=ltitle><a href="/&agentName;">&AGENT:name;</a>:</if>

<table cellspacing=0 cellpadding=0 border=0>
<tr nowrap nobr><th align=left width=170 valign=bottom nowrap><a
	href="http://rsv.ricoh.com/"><img src="/Agency/Icons/ricoh.gif"
	border=0 width=170 height=48 alt="R I C O H"></a></th>
    <th width=170 nowrap></th>
    <th align=left nowrap nobr valign=center width=170>
        <a href="/"><img width=85 height=45 border=0
	    	         src="/Agency/Icons/pia45.gif" alt=PIA></a></th></tr>
<tr height=2><td colspan=3  nowrap nobr
    ><img src="/Agency/Icons/rule.gif" height=6 width=469></td></tr>
<!--img src="/Agency/Icons/blackline425.gif" height=2 width=425-->
<tr nowrap nobr><th align=left valign=top><a href="http://rsv.ricoh.com/"><img
	src="/Agency/Icons/ricoh-silicon-valley.gif" alt="RICOH SILICON VALLEY"
	border=0 width=170 height=21></a></th>
    <th align=right valign=top width=170>&ltitle;&nbsp; </th>
    <th align=left colspan=2><if><get entity name=title>
	<then><get entity name=title></then>
	<else>&fileName;</else></if></th></tr>
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
<set name=myear><subst match="/.* " result=", "><select>
    &attributes;<name>cvsid<eval/><text split>&selected;</text> 3
    </select> </set>
<b>Copyright &copy; &myear; Ricoh Silicon Valley</b><br>
<em><select>&attributes;<name>cvsid<eval/></em>

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
<set name=myear><subst match="/.* " result=", "><select>
    &attributes;<name>cvsid<eval/><text split>&selected;</text> 3
    </select> </set>
<b>Copyright &copy; &myear; Ricoh Silicon Valley</b><br>
<em><select>&attributes;<name>cvsid<eval/></em>

  </action>
</define>

<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

