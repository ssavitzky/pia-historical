<!doctype tagset system "tagset.dtd">
<tagset name=basic>


<h1>Basic Tagset</h1>

This file contains the XML definition for the Basic (new) Tagset.  This tagset
consists of the primitive operations <em>only</em>.

<dl>
  <dt> <b>Note:</b>
  <dd> It should be possible to process this file into either documentation or
       a DTD. It's concievable that we could even process it into Java.

  <dt> <b>Note:</b>
  <dd> Also observe the SGML requirement that the element named in the
       &lt;!doctype...&gt; declaration be the outermost element in the
       document. 

  <dt> <b>Note:</b>
  <dd> Finally, observe that this tagset includes documentation in HTML, in
       spite of the fact that HTML is not a superset of the tagset being
       defined.  The syntax of the document that defines a tagset and the
       syntax defined <em>by</em> the tagset are, potentially, completely
       disjoint.  Because of the included HTML, this document does
       <em>not</em>  qualify as XML.
</dl>

<h2>Definition Tags</h2>

<blockquote><em>
  These must already be defined for bootstrapping, but they will not be in the
  tagset we are defining unless we put them there.  This is a feature, not a
  bug.  Note that because the tagset is not recursive, we cannot use its tags
  as actions.  This is also a feature.  We may have to relax it if some
  actions turn out to be non-primitive.
</em></blockquote>

<h3>Tagset and Namespace</h3>

<define element tag=tagset handler>
  <define attribute name=name required/>
  <define attribute name=context implicit/>
  <define attribute name=recursive implicit>
    <doc> If present, this attribute indicates that elements defined in the
	  tagset can be used in the definitions of other elements. 
    </doc>
  </define>
</define>

<define element tag=namespace handler>
  <define attribute name=name implicit>
    <doc> Note that the <code>name</code> attribute is optional; it is
	  perfectly meaningful to have an anonymous &lt;namespace&gt;.
    </doc>
  <define attribute name=context implicit/>
</define>

<h3>Define and its components</h3>

<define element tag=define handler no-text default-content=value>
  <doc> Defines an element, attribute, or entity.  It is meaningful for
	for &lt;define&gt; to occur outside of a &lt;namespace&gt; or
	&lt;tagset&gt; element because there is always a ``current'' namespace
	and tagset in effect. 
  </doc>
</define>

<dl>
  <dt> Note:
  <dd> The <code>no-text</code> attribute specifies that the element, in this
       case &lt;define&gt;, does not contain text.  All whitespace in its
       content is marked ignorable.  The <code>default-content=</code>
       attribute specifies the tag to use to ``wrap'' any content which is not
       one of the defined children.
</dl>

<h4>Sub-elements of &lt;define&gt;</h4>
<dl>
  <dt> Note:
  <dd> The use of <code>parent=</code> specifies that these elements only
       occur inside the given parent element; in this case,
       <code>define</code>.  The value of the <code>parent</code> attribute is
       actually a list which is appended to with each use, allowing the DTD to
       be incrementally extended.<p>

       The use of <code>parent</code> greatly simplifies the construction of
       content models and the parser.  An element with a parent implicitly
       terminates any unclosed elements between it and its innermost parent.
</dl>

<ul>
  <li> <define element parent=define tag=value>
         <doc> The &lt;value&gt; sub-element defines a value for the node
	       being defined. 
         </doc>
       </define>

  <li> <define element parent=define tag=action quoted>
         <doc> The &lt;action&gt; sub-element defines an action for the node
	       being defined.  
         </doc>
       </define>

  <li> <define element parent=define tag=doc quoted>
         <doc> This sub-element contains documentation for the node being
	       defined.  It may be either retained or stripped out depending
	       on how the enclosing namespace is being processed. 
         </doc>
       </define>

</ul>

<h2>Control Structure Tags</h2>

<h3>If and its components</h3>
<define element tag=if handler>
  <doc> If any non-whitespace text, or any defined entity, is present before
	the first ``official'' child element, the <em>condition</em> of the
	&lt;if&gt; is considered to be <code>true</code>.  Otherwise it is
	false.  (This implies that comments are ignored.)
  </doc>
</define>
<dl>
  <dt> Note:
  <dd> === need a way to specify that anything can be a child of &lt;if&gt;,
       not just the specified children.
  <dt> Note:
  <dd> We could have specified &lt;test&gt; as the default child, but the
       implementation is cleaner and more efficient without doing so.
</dl>

<h4>Sub-elements of &lt;if&gt;</h4>
<ul>
  <li> <define element tag=then parent="if else-if elsif" handler quoted>

       </define>
  <li> <define element tag=else parent=if handler quoted>

       </define>
  <li> <define element tag=else-if parent=if handler=elsf>

       </define>
  <li> <define element tag=elsf parent=if handler>
         <doc> This is a compact synonym for &lt;else-if&gt;, chosen because
	       it is the same length as &lt;then&gt; and &lt;else&gt;.
         </doc>
       </define>
</ul>

<h3>Repeat and its components</h3>
<define element tag=repeat handler quoted>
  <doc>
  </doc>
</define>

<h4>Sub-elements of &lt;repeat&gt;</h4>
<ul>
  <li> 
</ul>

<h3>Test</h3>
<define element tag=test handler>
  <doc>
  </doc>
</define>

<h3>Logical</h3>
<define element tag=logical>
  <doc>
  </doc>
</define>


<h2>Document Structure Tags</h2>

<h3>Select and its components</h3>
<define element tag=select>
  <doc>
  </doc>
</define>

<h4>Sub-elements of &lt;select&gt;</h4>
<ul>
  <li> 
</ul>

<h2>Data Manipulation Tags</h2>


<h2>Data Structure Tags</h2>


<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

