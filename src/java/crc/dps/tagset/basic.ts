<!doctype tagset system "tagset.dtd">
<tagset name=basic tagset=tagset>
<title>Basic Tagset</title>
<cvs-id>$Id$</cvs-id>

This file contains the XML definition for the Basic tagset.  It is essentially
an XML representation of a DTD, with extensions for describing the semantics
of the objects being defined, and intermixed documentation in HTML.  The best
reference on how to <em>read</em> such a representation is <a
href="tagset.html"><code>tagset.html</code></a>, the HTML version of the <a
href="tagset.ts"><code>tagset</code></a> tagset.

<doc> This tagset consists of the primitive operations <em>only</em>.
	It is mainly intended as a base for more extensive tagsets, which in
	turn are usually layered on top of HTML.
</doc>

<dl>
  <dt> <b>Note:</b>
  <dd> It is already possible to process a tagset file into HTML using the
       <a href="tsdoc.html">tsdoc</a> tagset. It will eventually be possible
       to process tagset files into DTD's as well.  It's conceivable that we
       could even process them into Java or C.

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

<h2>Definition Elements</h2>

<blockquote><em>
  These must already be defined for bootstrapping, but they will not be in the
  tagset we are defining unless we put them there.  This is a feature, not a
  bug.  Note that because the tagset is not recursive, we cannot use its tags
  as actions.  This is also a feature.  We may have to relax it if some
  actions turn out to be non-primitive.
</em></blockquote>

<h3>Define and its components</h3>

<define element=define handler no-text default-content=value>
  <doc> Defines an element, attribute, entity, or word.  It is meaningful for
	for <tag>define</tag> to occur outside of a <tag>namespace</tag> or
	<tag>tagset</tag> element because there is always a ``current''
	namespace and tagset in effect.

	<p>A <tag>define</tag> element that contains neither a
	<tag>value</tag> nor an <tag>action</tag> sub-element defines only
	syntax.  The defined construct is simply passed through to the output
	by the processor, with its contents and attributes (if any) also being
	processed in turn. 

	<p>A construct can be ``defined'' more than once; the attributes
	are effectively merged; the value and/or action are replaced.  The
	main use of this is to associate a new value with a construct, and to
	associate an action with a construct that has already been defined. 
  </doc>
  <doc> <strong>Construct Specification Attributes:</strong>

	<p>The following attributes specify the type of construct being
	defined, and its name (expressed as the value of the attribute).  It
	is an error for more than one of these attributes to be present in a
	single <tag>define</tag> tag.
  </doc>

  <define attribute=element implied>
    <doc> Specifies that an element (tag) is being defined.  The value of the
	  attribute is the tagname of the element being defined.  If the
	  <code>handler</code> attribute or the <code><tag>action</tag></code>
	  sub-element is present, the element will be active.  If a
	  <code>value</code> attribute or sub-element is present, the element
	  will be passively replaced by its value when the document is
	  processed.
    </doc>
  </define>
  <define attribute=attribute implied>
    <doc> Specifies that an attribute is being defined.    The value of the
	  attribute is the name of the attribute being defined.  If the
	  <code>handler</code> attribute or the <code><tag>action</tag></code>
	  sub-element is present, the attribute will be active.  If a
	  <code>value</code> attribute or sub-element is present, the
	  attribute will be passively replaced by its value unless a value is
	  explicitly specified in the tag where the attribute occurs.

	  <p>It is usual for attributes to be defined <em>inside</em> an
	  element's definition.
    </doc>
  </define>
  <define attribute=entity implied>
    <doc> Specifies that an entity is being defined.    The value of the
	  attribute is the name of the entity being defined.  If the
	  <code>handler</code> attribute or the <code><tag>action</tag></code>
	  sub-element is present, the entity will be active.  If a
	  <code>value</code> attribute or sub-element is present, the entity
	  will be passively replaced by its value when it is referenced in a
	  document.
    </doc>
  </define>
  <define attribute=notation implied>
    <doc> Specifies that a notation is being defined.    The value of the
	  attribute is the name of the notation being defined.  The associated
	  value, if any, should be the MIME type of the data.
    </doc>
  </define>

  <doc> <strong>General modifiers:</strong>
  </doc>

  <define attribute=handler implied>
    <doc> Specifies the action handler class for the node being defined.

	  <p> If no value is specified, the handler class name is assumed to
	  be the same as the element's tag, possibly with <code>Handler</code>
	  appended.  If the value contains periods, it is assumed to be a
	  complete Java class name and is used unmodified.

	  <p><strong>Note</strong> that if the Node being defined is an
	  <em>attribute</em>, things get a little complicated.  What we would
	  really like to happen is that the parser selects a handler for the
	  containing <em>element</em> based on the presence of a ``handled''
	  attribute.  Probably only one such attribute should be permitted.
    </doc>
    <note author=steve>
	=== the implementation needs to give an error for missing handlers!
    </note>
  </define>

  <doc> <strong>Modifiers for <code><tag>define element</tag></code>:</strong>
	<blockquote><em>
	  The following attributes are only meaningful when defining an
	  Element.  It is impossible to represent this constraint in SGML.
	</em></blockquote>
  </doc>

  <define attribute=quoted implied>
    <doc> Indicates that the content of the element being defined should be
	  parsed, but not expanded before invoking the action.
    </doc>
  </define>
  <define attribute=literal implied>
    <doc> Indicates that the content of the element is unparsed (#CDATA).
    </doc>
  </define>
  <define attribute=empty implied>
    <doc> Indicates that the element being defined will never have any
	  content. 
    </doc>
  </define>

  <doc> <strong>Modifiers for <code><tag>define attribute</tag></code>:</strong>
  </doc>

  <define attribute=implied implied>
    <doc> Only meaningful for attributes.  Specifies that the attribute is
	  implied (optional).
    </doc>
  </define>
  <define attribute=required implied>
    <doc> Only meaningful for attributes.  Specifies that the attribute is
	  required.
    </doc>
  </define>
  <define attribute=fixed implied>
    <doc> Only meaningful for attributes.  Specifies that the attribute's
	  value is invariant (and must be given in its definition).
    </doc>
  </define>

  <doc> <strong>Modifiers for <code><tag>define entity</tag></code>:</strong>
  </doc>

  <define attribute=system implied>
    <doc> The value of this attribute is the ``system identifier'' (URI) of
	  an ``external entity''.  Usually it will be a filename relative to
	  the document containing the definition being processed.
    </doc>
  </define>
  <define attribute=public implied>
    <doc> The value of this attribute is the ``public identifier'' of an
	  external entity.  In a DTD, an alternative system identifier must be
	  provided; that should be specified via the <code>system</code>
	  attribute. 
    </doc>
  </define>
  <define attribute=NDATA implied>
    <doc> This specifies that the entity contains non-parsed data; the value
	  specifies the name of the data's <em>notation</em>.
    </doc>
  </define>
  <define attribute=retain implied>
    <doc> This specifies that the a reference to the entity should be replaced
	  by its value when conversion to a string is desired, but otherwise
	  should be retained in the tree and passed through to the output.
	  This is used, e.g., for the predefined character entities.
    </doc>
  </define>

</define>

<dl>
  <dt> Note:
  <dd> The <code>no-text</code> attribute specifies that the element, in this
       case <tag>define</tag>, does not contain text.  All whitespace in its
       content is marked ignorable.  The <code>default-content=</code>
       attribute specifies the tag to use to ``wrap'' any content which is not
       one of the defined children.
</dl>

<h4>Sub-elements of <tag>define</tag></h4>
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
  <li> <define element=value parent=define quoted handler>
         <doc> The <tag>value</tag> sub-element defines a value for the node
	       being defined.  The node will be replaced by its value whenever
	       it appears in a document being processed.

       	       <p>The contents of the <tag>value</tag> element are processed
       	       at the point in the document where the <tag>define</tag> occurs
       	       in the document unless the <code>quoted</code> attribute is
       	       present.  They are <em>not</em> processed again during
       	       expansion unless the <code>active</code> attribute is present.
         </doc>
       	 <define attribute=active implicit>
           <doc> If present, causes the defined value to be reprocessed
		 at the point where it is expanded.  Unlike an
		 <tag>action</tag>, however, no local namespace is created
		 during the expansion.
           </doc>
         </define>
       	 <define attribute=quoted implicit>
           <doc> If present, suppresses processing of the defined value
		 at the point where it is defined.  
           </doc>
         </define>
       	 <define attribute=passive implicit>
           <doc> If present, suppresses replacement of the defined construct
		 with its value during normal processing.  The construct,
		 usually an entity, will be passed through all the way to the
		 output.  Passive values are normally used for things like
		 character entities (<code>&amp;amp;</code> and so on).  They
		 <em>are</em> expanded when converting an object to a string.
           </doc>
         </define>
       </define>

  <li> <define element=action parent=define quoted>
         <doc> The <tag>action</tag> sub-element defines an action for the
	       node being defined.  Note that it is possible for a node to
	       have both an action and a value.

       	       <p>The contents of the <tag>action</tag> element are processed
       	       at the point in the document where the defined construct is
       	       expanded. They are <em>not</em> processed in the definition.

       	       <p>Expanding an action associated with an Element or Attribute
       	       implicitly defines a local <tag>namespace</tag> containing the
       	       following entities:

       		<dl>
		  <dt> <code>&amp;content;</code>
		  <dd> the content of the element being expanded.
		  <dt> <code>&amp;element;</code>
		  <dd> the ``start tag'' of the element being expanded: the
		       element without its content.
		  <dt> <code>&amp;attributes;</code>
		  <dd> the attribute list of the element being expanded.
		  <dt> <code>&amp;value;</code>
		  <dd> the defined <tag>value</tag> associated with the
		       definition being expanded.
		</dl>
       	       <p>Expanding an action associated with an Entity or Word
       	       implicitly defines a local <tag>namespace</tag> containing the
       	       following entities:

       		<dl>
		  <dt> <code>&amp;content;</code>
		  <dd> the children of the node being expanded, if any.
		  <dt> <code>&amp;node;</code>
		  <dd> the Text or EntityReference node being expanded
		  <dt> <code>&amp;value;</code>
		  <dd> the defined <tag>value</tag> associated with the
		       definition being expanded.
		</dl>
       </doc>
       </define>
</ul>

<h3>Tagset and Namespace</h3>

<blockquote><em>
  The <tag>tagset</tag> and <tag>namespace</tag> elements provide the context
  in which <tag>define</tag> operates, i.e., in which elements, entities, and
  so on are defined.

  <p>It is, however, meaningful for <tag>define</tag> to occur outside of
  a <tag>namespace</tag> or <tag>tagset</tag> element because there is always
  a ``current'' namespace and tagset in effect.
</em></blockquote>

<define element=tagset handler>
  <doc> This element defines a ``<em>tagset</em>'' -- roughly the equivalent
	of a DTD or database schema.  Having an XML representation allows
	tagsets to be processed using ``normal'' methods, rather than devising
	special machinery for parsing and processing DTD's.
  </doc>
  <note author=steve>
	Eventually it will be possible to process XML tagsets into DTD's.
  </note>
  <note author=steve>
	Eventually we must make it possible to ``include'' one tagset
	inside another by using <tag>select</tag>.
  </note>

  <define attribute=name required/>
  <define attribute=context implied>
    <doc> The <code>context</code> attribute specifies the tagset in which
	  names not defined in the current tagset will be looked up.  It is
	  effectively <em>included</em> in the tagset being defined.
    </doc>
  </define>
  <define attribute=include implied>
    <doc> The <code>include</code> attribute specifies a list of tagsets,
	  by name, whose contents are to be <em>copied into</em> the current
	  <tag>tagset</tag>.  This is different from <tag>context</tag>, which
	  effectively includes by reference.
    </doc>
  </define>
  <define attribute=tagset implied>
    <doc> The <code>tagset</code> attribute specifies the tagset to be used
	  when <em>parsing</em> the tagset being defined.  It specifies the
	  syntax of documentation elements and the operations permitted in
	  <tag>value</tag> and <tag>action</tag> elements.
    </doc>
  </define>
  <define attribute=recursive implied>
    <doc> If present, this attribute indicates that elements defined in the
	  tagset can be used in the <tag>action</tag> and <tag>value</tag>
	  definitions of other elements.  The default is to restrict
	  definitions to be in terms of elements and entities defined in the
	  <em>enclosing document's</em> tagset, which allows the language used
	  to define tagsets to differ from the language being defined.

	  <p><strong>Note</strong> that it may be almost impossible (totally
	  impossible, in some cases) to come up with a DTD that will
	  accurately describe a recursive tagset definition file.  It will
	  still be reducible to a valid DTD, however, so that the documents
	  <em>it describes</em> will be valid SGML.
    </doc>
</define>

<define element=namespace handler>
  <doc> This defines a namespace for entities. 
  </doc>
  <define attribute=name implied>
    <doc> Note that the <code>name</code> attribute is optional; it is
	  perfectly meaningful to have an anonymous <tag>namespace</tag>.
    </doc>
  </define>
  <define attribute=context implied>
    <doc> The <code>context</code> attribute specifies the namespace in which
	  names not defined in the current tagset will be looked up.  It is
	  effectively <em>included</em> in the tagset being defined.  If no
	  value is specified, the innermost namespace is assumed.
    </doc>
  </define>
  <define attribute=include implied>
    <doc> The <code>include</code> attribute specifies a list of namespaces,
	  by name, whose contents are to be <em>copied into</em> the current
	  <tag>namespace</tag>.  This is different from <tag>context</tag>,
	  which effectively includes by reference.
    </doc>
  </define>
</define>


<h3>Documentation Elements</h3>

<ul>
  <li> <define element=doc parent="define namespace tagset">
         <doc> This sub-element contains documentation for the node being
	       defined.  It may be either retained or stripped out depending
	       on how the enclosing namespace is being processed. 
         </doc>
       </define>

  <li> <define element=note parent="define namespace tagset">
         <doc> This sub-element contains attributed annotation for the node
	       being defined.  It may be either retained or stripped out
	       depending on how the enclosing namespace is being processed.
         </doc>
         <define attribute=author required>
       	   <doc> The value of this attribute should be the author's initials,
		 login name, or e-mail address.
           </doc>
         </define>
       </define>
</ul>

<h2>Control Structure Elements</h2>

<blockquote><em>
  Control structure elements modify the control flow of an expansion, by
  selectively including, skipping, or repeating some content.
</em></blockquote>

<h3>If and its components</h3>
<define element=if handler PCDATA>
  <doc> If any non-whitespace text, or any defined entity, is present before
	the first ``official'' child element, the <em>condition</em> of the
	<tag>if</tag> is considered to be <code>true</code>.  Otherwise it is
	false.  (This implies that comments are ignored.)
  </doc>
</define>
<dl>
  <dt> Note:
  <dd> === need a way to specify that anything can be a child of
       <tag>if</tag>, not just the specified children.  PCDATA?  ANY?
  <dt> Note:
  <dd> We could have specified <tag>test</tag> as the default child, but the
       implementation is cleaner and more efficient without doing so.
</dl>

<h4>Sub-elements of <tag>if</tag></h4>
<ul>
  <li> <define element=then parent="if else-if elsif" handler quoted>
         <doc> the <tag>then</tag> component is expanded if its parent's
	       condition is <code>true</code>.
         </doc>
       </define>
  <li> <define element=else parent=if handler quoted>
         <doc> the <tag>else</tag> component is expanded if its parent's
	       condition is <code>false</code>.
         </doc>
       </define>
  <li> <define element=else-if parent=if handler=elsf>
         <doc> the <tag>else-if</tag> component is expanded if its parent's
	       condition is <code>false</code>.  Its contents consist of a
	       <em>condition</em> and a <tag>then</tag> element which is
	       expanded if the condition is <code>true</code>.  If the
	       condition is <code>false</code>, expansion resumes with the
	       following <tag>else-if</tag> or <tag>else</tag> in the
	       surrounding element.
         </doc>
       </define>
  <li> <define element=elsf parent=if handler>
         <doc> This is a compact synonym for <tag>else-if</tag>, chosen because
	       it is the same length as <tag>then</tag> and <tag>else</tag>.
         </doc>
       </define>
</ul>

<h3>Repeat and its components</h3>
<define element=repeat handler quoted PCDATA>
  <doc> The content of this element is repeatedly expanded until one of the
	defined sub-elements reaches its specified ``stop'' condition.  An
	implicit local namespace is created in which the iteration variables
	are defined.
  </doc>
  <define attribute=list implied>
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>foreach</tag></code> sub-element.
    </doc>
  </define>
  <define attribute=start implied>
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> sub-element.
    </doc>
  </define>
  <define attribute=stop implied>
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> sub-element.
    </doc>
  </define>
  <define attribute=step implied>
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> sub-element.
    </doc>
  </define>
  <define attribute=entity implied>
    <doc> If present, this specifies the name of the iteration variable for an
	  implied <code><tag>foreach</tag></code> or
	  <code><tag>for</tag></code> sub-element.
    </doc>
  </define>
</define>

<h4>Sub-elements of <tag>repeat</tag></h4>
<ul>
  <li> <define element=yyy parent=repeat handler>
         <doc>
         </doc>
       </define>
</ul>

<h3>Test</h3>
<define element=test handler>
  <doc> This element performs a test on its content.  If no attributes are
	specified, the test is the same as that performed by <tag>if</tag>.
	If the tested condition is <code>true</code>, the <tag>test</tag>
	element expands to ``<code>1</code>'', otherwise it ``expands'' to
	nothing at all.

	<p><tag>test</tag> is not, strictly speaking, a control-flow
	operation, but it is used almost exclusively inside control-flow
	operations for computing conditions.
  </doc>
</define>

<h3>Logical</h3>
<define element=logical handler>
  <doc> This element is essentially a convenient shorthand for a nested set of
	<tag>if</tag> elements.  It performs functions that are equivalent to
	the LISP functions <code>AND</code> and <code>OR</code>.
  </doc>
  <define attribute=and implied>
    <doc> If present, this specifies that a ``logical AND'' operations will be
	  performed.  Each child of the <tag>logical</tag> element is expanded
	  in turn.  Declarations, comments, processing instructions, and
	  whitespace are ignored.  If <em>every</em> other child expands to
	  something <tag>if</tag> would consider a ``<code>true</code>''
	  condition, the expansion of the <em>last</em> such child is passed
	  to the output.  Otherwise no output is generated.
    </doc>
  </define>
  <define attribute=or implied>
    <doc> If present, this specifies that a ``logical OR operations will be
	  performed.  Each child of the <tag>logical</tag> element is expanded
	  in turn.  Declarations, comments, processing instructions, and
	  whitespace are ignored.  If <em>any</em> other child expands to
	  something <tag>if</tag> would consider a ``<code>true</code>''
	  condition, the expansion of the <em>that</em> child is passed
	  to the output, and expansion stops at that point.  Otherwise no
	  output is generated. 
    </doc>
  </define>
</define>


<h2>Document Structure Elements</h2>

<blockquote><em>
  Document structure elements select Nodes or sets of Nodes from a parse tree,
  and perform structural modifications on trees.
</em></blockquote>

<h3>Select and its components</h3>
<define element=select>
  <doc>
  </doc>
</define>

<h4>Sub-elements of <tag>select</tag></h4>
<ul>
  <li> 
</ul>

<h2>Data Manipulation Elements</h2>

<blockquote><em>
  Data manipulation elements perform operations on data, typically text, that
  depend on some non-structural features of its content (e.g. its value as a
  number).
</em></blockquote>

<h2>Data Structure Elements</h2>

<blockquote><em>
  Data structure elements perform no operations; they exist to represent
  common forms of complex structured data.  Strictly speaking,
  <code><tag>tagset</tag></code> and <code><tag>namespace</tag></code> are
  data structure elements.  Often a data structure element will have a
  representation that is a <em>subclass</em> of the representation of an
  ordinary Element.  (Currently <code>crc.dps.active.ParseTreeElement</code>).
</em></blockquote>

<!--template -->
<h3>Xxx and its components</h3>
<define element=xxx >
  <doc>
  </doc>
  <define attribute= implied>
    <doc> 
    </doc>
  </define>
</define>

<h4>Sub-elements of <tag>xxx</tag></h4>
<ul>
  <li> <define element=yyy parent=xxx handler>
         <doc>
         </doc>
       </define>
</ul>
<!--/template -->

<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

