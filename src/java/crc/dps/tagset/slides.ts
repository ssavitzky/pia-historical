<!doctype tagset system "tagset.dtd">
<tagset name=slides parent=xhtml recursive>
<doc>
<p> This tagset is used for generating ``slide'' presentations from ordinary
    HTML documents.  The original document contains &lt;slide&gt; elements,
    each of which may contain an &lt;h2&gt; element as its caption along with
    some text.  The document is readable as-is.

<p> This tagset reformats each ``slide'' as a table with suitable decoration,
    making it look like a slide in a PowerPoint presentation.  Each slide
    contains forward, backward, and table-of-contents links. 

<p> Tags Defined:
<ul>
  <li> &lt;slide&gt;<h2>slide caption</h2> content &lt;/slide&gt;
		The slide tag is designed so that the ``rough draft'' of
		a presentation is still a valid, readable HTML file.

  <li> &lt;start&gt;text&lt;/start&gt;
		A link to the first slide.  Use this to skip any unwanted
		front matter and get things lined up right.

  <li> &lt;end&gt;text&lt;/end&gt;
		An anchor for the last slide's ``next'' link.
  <li> &lt;toc&gt;text&lt;/toc&gt;
		Table of contents
</ul>
</doc>
<note author=steve>
<p> Things to do:
<ul>
  <li> &lt;slide&gt; needs an optional ID attribute.
  <li> Next/Prev links would probably work better if they referenced the table
       by a 1-pixel-high top row.  ID attr on the table doesn't work.
</ul>
</note>

<h2>Entity Definitions</h2>

<doc> These are easily overridden in the document itself. </doc>

<h3>Dimensions</h3>
<define entity=hh><value>300</define>

<h3>Colors</h3>
<define entity=topBg><value>lightblue</define>
<define entity=topFg><value>black</define>
<define entity=leftBg><value>#c40026</define>
<define entity=leftFg><value>blue</define>
<define entity=ulBg><value>lightblue</define>
<define entity=ulFg><value>#c40026</define>
<define entity=mainBg><value>white</define>
<define entity=mainFg><value>black</define>

<h3>Logos and Buttons</h3>
<define entity=logo>
  <value><img src="/PIA/Doc/Graphics/pent16.gif" alt="&nbsp;"></define>
<define entity=toPrev><value>&lt;&lt;</define>
<define entity=toNext><value>&gt;&gt;</define>
<define entity=noPrev><value>&nbsp;&nbsp;</define>
<define entity=noNext><value>&nbsp;&nbsp;</define>
<define entity=toToc><value>^^</define>

<h3>Default text</h3>
<define entity=subCaption><value>Ricoh Silicon Valley</define>

<h2>&lt;Slide&gt;</h2>

<define element=slide parent=body>
<action>
<hide><!-- first time through we initialize the variables -->
  <if>&DOC:slide;<else><set name=DOC:slide>0</set></if>
  <if>&DOC:next;<else><set name=DOC:next><numeric sum>1 &slide;</set></if>
  <if>&DOC:prev;<else><set name=DOC:prev> </set></if>
  <if>&DOC:slidelist;<else><set name=DOC:slidelist> </set></if>
</hide>
<table width="100%" cellspacing=0 cellpadding=5 border=0>
<tr>
   <td bgcolor="&ulBg;" fgcolor="&ulFg;" width=10
       ><a name="&slide;">&logo;</a></td>
   <th align=left bgcolor="&topBg;" fgcolor="&topFg;" width="100%"><if>
        <get name=label>
	<then><a name="&label;">&nbsp;<get name=caption></a>
	<else>&nbsp;<get name=caption></if>
   <th bgcolor="&topBg;" fgcolor="&topFg;" align=right nobreak><if>
        &prev;<then><a href="#&prev;">&toPrev;</a><else>&noPrev;</if><if>
	<test zero>&slide;</test>
              <then>&nbsp;<else><a href="#TOC">&nbsp;&slide;&nbsp;</a></if><if>
        &next;<then><a href="#&next;">&toNext;</a><else>&noNext;</if>
<tr><td bgcolor="&leftBg;" fgcolor="&leftFg;" height="&hh;" width=10
         valign=top>&nbsp;</td>
    <td bgcolor="&mainBg;" fgcolor="&mainFg;" valign=top colspan=2>
&content;
<tr><td bgcolor="&leftBg;" fgcolor="&leftFg;" width=10>&nbsp;</td>
  <td align=left valign=bottom bgcolor="&mainBg;" fgcolor="&mainFg"
      width='100%'><em>&subCaption;</em>
  <td align=right bgcolor=white nobreak><if><!-- bogus if to avoid linebreak -->
     </if><if>&prev;<then><a href="#&prev;">&toPrev;</a><else>&noPrev;</if><if>
     </if><if><test exact match=TOC>&label;></test>
	    <else><a href="#TOC">&nbsp;&slide;&nbsp;</a></if><if>
     </if><if>&next;<then><a href="#&next;">&toNext;</a><else>&noNext;</if></td>
</table>
<p> <hide>
    <if><test zero>&slide;</test><then>
        <else><set entity name=slidelist><get entity name=slidelist>
<li> <a href="#&slide;">&caption;</a></li></set></else></if>
    <set name=prev>&slide;</set>
    <set name=slide><numeric sum digits=0>1 &slide;</set>
    <set name=next><numeric sum digits=0>1 &slide;</set>
    <set name=label> </set>
    <set name=caption> </set>
</hide><!-- end slide -->
</action>
</define>

<dl>
  <dt><!-- this is here just to repair indentation -->
</dl>

<h2>Auxiliary Tags</h2>

<define element=end><action><a name="&slide;">&content;</a></define>
<define element=start>
<action>
  <table width='100%'><tr><td align=right><a href="#0">&content; &toNext;</a>
</define>

<define element=h1>
<action>
  <set name=DOC:subCaption>&content;</set>
<h1>&content;</h1>
<hide><!-- first time through we initialize the variables -->
  <if>&DOC:slide;<else><set name=DOC:slide>0</set></if>
  <if>&DOC:next;<else><set name=DOC:next><numeric sum>1 &slide;</set></if>
  <if>&DOC:prev;<else><set name=DOC:prev> </set></if>
  <if>&DOC:slidelist;<else><set name=DOC:slidelist> </set></if>
</hide>
<p> <start>Start here</start>
</define>
     
<define element=h2><action><set name=DOC:caption>&content;</set></define>
	  
<define element=toc><action>
<set name=label>TOC</set>
<slide>
<get name=content>
<ol>
  &slidelist;
</ol>
</define>

<em>$Id$</em>
</tagset>
