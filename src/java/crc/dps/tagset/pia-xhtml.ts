<!doctype tagset system "tagset.dtd">
<tagset name=pia-xhtml parent=xhtml recursive>

<h1>PIA XHTML Tagset</h1>

<doc> This the version of the XHTML tagset used by default in the PIA.
</doc>

<h2>Definition Tags</h2>

<h2>Control Structure Tags</h2>


<h2>Data Manipulation Tags</h2>


<h2>Data Structure Tags</h2>


<h2>Form-Processing Tags</h2>

Note that we only need these inside the PIA.

<h3>Submit</h3>
<undefine element=submit handler >
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

<h2>Headers and Footers</h2>

<define element=header>
  <doc> This expands into a standard header.  The content is the title.  This
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
    <th align=right valign=top width=170>&ltitle; </th>
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
<a href="/">PIA</a> || <a href="/Agency">Agency</a>:
<a href="&agentName;/">index</a>
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
<nav-bar/>
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

