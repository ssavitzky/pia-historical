<!doctype tagset system "tagset.dtd">
<tagset name=standard context=HTML include=basic recursive>

<h1>Standard Tagset</h1>

<doc> This tagset consists of the primitive operations from the <a
      href="basic.html">basic</a> tagset, the additional non-primitive
      convenience functions of <a href="standalone.html">standalone</a>, and
      HTML syntax.  It is intended for use in active HTML documents.
</doc>

This file contains the XML definition for the Standard (new) Tagset.  It
should be possible to process this file into either documentation or a DTD.
It's concievable that we could even process it into Java. <p>

Note the SGML requirement that the element named in the &lt;!doctype...&gt;
declaration be the outermost element in the document.

<h2>Definition Tags</h2>

<blockquote><em>
  These must already be defined for bootstrapping, but they will not be in the
  tagset we are defining unless we put them there.  This is a feature, not a
  bug.  Note that because the tagset is recursive, we are free to define new
  tags in terms of old ones.  This means, however, that we have to be careful
  not to use a tag until it has been defined.
</em></blockquote>

<h2>Control Structure Tags</h2>


<h2>Data Manipulation Tags</h2>


<h2>Data Structure Tags</h2>


<h2>Form-Processing Tags</h2>

<h3>Submit</h3>
<define element=submit handler >
  <doc>
  </doc>
  <define attribute= implied>
    <doc> 
    </doc>
  </define>
</define>

<h3>Form and its components</h3>
<define element=form >
  <doc>
  </doc>
  <define attribute= implied>
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


<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

