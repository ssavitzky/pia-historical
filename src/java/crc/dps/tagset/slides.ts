<!doctype tagset system "tagset.dtd">
<!-- -------------------------------------------------------------------------- -->
<!-- The contents of this file are subject to the Ricoh Source Code Public      -->
<!-- License Version 1.0 (the "License"); you may not use this file except in   -->
<!-- compliance with the License.  You may obtain a copy of the License at      -->
<!-- http://www.risource.org/RPL                                                -->
<!--                                                                            -->
<!-- Software distributed under the License is distributed on an "AS IS" basis, -->
<!-- WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License  -->
<!-- for the specific language governing rights and limitations under the       -->
<!-- License.                                                                   -->
<!--                                                                            -->
<!-- This code was initially developed by Ricoh Silicon Valley, Inc.  Portions  -->
<!-- created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All    -->
<!-- Rights Reserved.                                                           -->
<!--                                                                            -->
<!-- Contributor(s):                                                            -->
<!-- -------------------------------------------------------------------------- -->


<tagset name=slides parent=HTML include=xxml recursive>
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
  <li> &lt;slide&gt;&lt;h2&gt;slide caption&lt;/h2&gt; content &lt;/slide&gt;
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
<define entity=hh>
  <doc> The height of the main portion of a table.  This needs to be tweeked
	for the screen size of the presentation device; the default is 300,
	which is correct for a large-screen TV pretending to be a 640x480
	monitor.
    <p> For 640x480, e.g. a Magio laptop, use <br>
	<code>&lt;set name=DOC:hh&gt;445&lt;/set&gt;</code>
  </doc>
  <value>300</define>

<h3>Colors</h3>
<doc> These define the colors of various portions in the table that represents
      a slide.
</doc>

<define entity=topBg><value>lightblue</define>
<define entity=topFg><value>black</define>
<define entity=leftBg><value>#c40026</define>
<define entity=leftFg><value>blue</define>
<define entity=ulBg><value>lightblue</define>
<define entity=ulFg><value>#c40026</define>
<define entity=mainBg><value>white</define>
<define entity=mainFg><value>black</define>

<h3>Logos and Buttons</h3>

<define entity=icons>
  <doc> This is the leading path for graphics.  It should begin and end with
	a slash if necessary.
  </doc>
  <value>/Icon/</value>
</define>

<define entity=logo>
  <doc> This appears in the upper-left-corner of each slide.  It needs to be
	almost exactly the same height as the text, because it is used as the
	anchor for the ``next slide'' button.
    <p> This presently assumes you're presenting via a PIA.  If you want to
	produce stand-alone HTML you'll have to move it into the same
	directory as your slides. 
  </doc>
  <value><img src="&icons;pent16.gif" alt="&nbsp;"></value>
</define>
<define entity=toPrev><value>&lt;&lt;</define>
<define entity=toNext><value>&gt;&gt;</define>
<define entity=noPrev><value>&nbsp;&nbsp;</define>
<define entity=noNext><value>&nbsp;&nbsp;</define>
<define entity=toToc><value>^^</define>

<h3>Default text</h3>
<define entity=subCaption>
  <doc> the ``subCaption'' is the text along the <em>bottom</em> line of each
	slide.  If your presentation is long, you may want to put your section
	caption in here. 
    <p> Usage: <br>
	<code>&lt;set name=subCaption&gt;text for bottom line&lt;/set&gt;</code>
  </doc>
  <value>Ricoh Silicon Valley</value>
</define>

<h2>&lt;Slide&gt;</h2>

<define element=xxx>
  <doc> This is a sample row using a 1x1 transparent gif.  Unfortunately it
	works correctly only if cellpadding=0, which introduces other problems.
  </doc>
<action>
<tr height=1 nowrap nobr cellpadding=0 bgcolor="&topBg;" 
       ><td colspan=3><a
        name="&slide;"><img src="&icons;/trans1x1.gif"></a></td></tr>
</action>
</define>

<define element=slide parent=body>
  <doc>	This is the element that defines a ``slide''.   Usage is something
	like:
	<pre>
	&lt;slide&gt; &lt;h2&gt;Document Processing&lt;/h2&gt;
	   &lt;ul&gt;
	      &lt;li&gt; Input: a document
	   &lt;/ul&gt;
	&lt;/slide&gt;
	</pre>
	The &lt;h2&gt; element is <em>required</em>, since it provides the
	caption for the slide.  
  </doc>
<action>
<hide><!-- first time through we initialize the variables -->
  <if>&DOC:slide;<else><set name=DOC:slide>0</set></if>
  <if>&DOC:next;<else><set name=DOC:next><numeric sum>1 &slide;</set></if>
  <if>&DOC:prev;<else><set name=DOC:prev> </set></if>
  <if>&DOC:slidelist;<else><set name=DOC:slidelist> </set></if>
</hide>
<table width="100%" cellspacing=0 cellpadding=5 border=0>
<tr><th bgcolor="&ulBg;" fgcolor="&ulFg;" align=center
       ><a name="&slide;">&slide;</a></td>
    <th align=left bgcolor="&topBg;" fgcolor="&topFg;" width="100%"><if>
        <get name=label>
	<then><a name="&label;">&nbsp;<get name=caption></then>
	<else>&nbsp;<get name=caption></else></if>
    <th align=right nobreak bgcolor="&topBg;"><expand>&logo;</expand></th>

<tr><td bgcolor="&leftBg;" fgcolor="&leftFg;" height="&hh;" valign=top>
        <if>&next;<then><a href="#&next;">&toNext;</a><else>&noNext;</if><br>
	<a href="#TOC">toc</a><br>
	<if>&prev;<then><a href="#&prev;">&toPrev;</a><else>&noPrev;</if><br>
    </td>

    <td bgcolor="&mainBg;" fgcolor="&mainFg;" valign=top colspan=2>
&content;
<tr><td bgcolor="&leftBg;" fgcolor="&leftFg;" width=25>&nbsp;</td>
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
  <table width='100%'><tr>
    <td align=left><a href="#TOC">TOC</a>
    <td align=right><a href="#0">&content; &toNext;</a>
</define>

<define element=h1>
  <doc> The top-level heading in the file (typically there is only one)
	becomes the default subCaption.  This may not be exactly what you
	want, but is easily overridden.  It also generates a link with the
	text ``Start here'', linked to the top of the first slide.  
  </doc>
<action>
<h1>&content;</h1>
<hide><!-- first time through we initialize the variables -->
  <set name=DOC:subCaption>&content;</set>
  <if>&DOC:slide;<else><set name=DOC:slide>0</set></if>
  <if>&DOC:next;<else><set name=DOC:next><numeric sum>1 &slide;</set></if>
  <if>&DOC:prev;<else><set name=DOC:prev> </set></if>
  <if>&DOC:slidelist;<else><set name=DOC:slidelist> </set></if>
</hide>
<p> <start>Start here</start>
</define>
     
<define element=h2><action><set name=DOC:caption>&content;</set></define>
	  
<define element=toc><action><!-- table of contents: -->
<set name=label>TOC</set>
<slide><!-- not working: tagset doesn't seem to be properly recursive. -->
<h2>&content;</h2>
<ol>
  &slidelist;
</ol>
</slide></action>
</define>

<em>$Id$</em>
</tagset>
