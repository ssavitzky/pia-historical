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

<doc> This tagset consists of the primitive operations <em>only</em>.	It is
      mainly intended as a base for more extensive tagsets, which in turn are
      usually layered on top of HTML.
</doc>

<dl>
  <dt> <b>Note:</b>
  <dd> It is already possible to process a tagset file into HTML using the
       <a href="tsdoc.html">tsdoc</a> tagset. It will eventually be possible
       to process tagset files into DTD's as well.  It's conceivable that we
       could even process them into Java or C, perhaps using embedded
       <code>&lt;code&gt;</code> tags with a <code>language</code> attribute.

  <dt> <b>Note:</b>
  <dd> Also observe the SGML requirement that the element named in the
       &lt;!doctype...&gt; declaration must be the outermost element in the
       document. 

  <dt> <b>Note:</b>
  <dd> Finally, observe that this tagset includes documentation in HTML, in
       spite of the fact that HTML is not a superset of the tagset being
       defined.  The syntax of the document that defines a tagset and the
       syntax defined <em>by</em> the tagset are, potentially, completely
       disjoint.  Because of the included HTML, this document does
       <em>not</em>  qualify as XML.  It could, however, be described in SGML
       or converted to XML by outputting empty HTML tags with the XML
       empty-tag delimiter. 
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

<define element=define handler no-text>
  <doc> Defines an element, attribute, entity, or word.  It is meaningful for
	for <tag>define</tag> to occur outside of a <tag>namespace</tag> or
	<tag>tagset</tag> element because there is always a ``current''
	namespace and tagset in effect.

	<p>A <tag>define</tag> element that contains neither a
	<tag>value</tag> nor an <tag>action</tag> sub-element defines only
	syntax.  The defined construct is simply passed through to the output
	by the processor, with its contents and attributes (if any) also being
	processed in turn. 

	<p>Note that a <tag>define</tag> can contain anything at all in its
	content; everything but the <tag>value</tag>, <tag>action</tag>, and
	possibly <tag>doc</tag> elements are thrown away.  This means that a
	definition can contain arbitrary decorative markup, and that arbitrary
	computation can be done in the course of processing a definition.

	<p>A construct can be ``defined'' more than once; the attributes
	are effectively merged; the value and/or action are replaced.  The
	main use of this is to associate a new value with a construct, and to
	associate an action with a construct that has already been defined. 
  </doc>

  <h4>Construct Specification Attributes:</h4>

  <blockquote><em>
	<p>The following attributes specify the type of construct being
	defined, and its name (expressed as the value of the attribute).  It
	is an error for more than one of these attributes to be present in a
	single <tag>define</tag> tag.
  </em></blockquote>

  <define attribute=element optional>
    <doc> Specifies that an element (tag) is being defined.  The value of the
	  attribute is the tagname of the element being defined.  If the
	  <code>handler</code> attribute or the <code><tag>action</tag></code>
	  sub-element is present, the element will be active.  If a
	  <code>value</code> attribute or sub-element is present, the element
	  will be passively replaced by its value when the document is
	  processed.
    </doc>
  </define>
  <define attribute=attribute optional>
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
  <define attribute=entity optional>
    <doc> Specifies that an entity is being defined.    The value of the
	  attribute is the name of the entity being defined.  If the
	  <code>handler</code> attribute or the <code><tag>action</tag></code>
	  sub-element is present, the entity will be active.  If a
	  <code>value</code> attribute or sub-element is present, the entity
	  will be passively replaced by its value when it is referenced in a
	  document.
    </doc>
  </define>
  <define attribute=notation optional>
    <doc> Specifies that a notation is being defined.    The value of the
	  attribute is the name of the notation being defined.  The associated
	  value, if any, should be the MIME type of the data.
    </doc>
  </define>

  <h4>General modifiers:</h4>

  <define attribute=handler optional>
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
  </define>

  <h4>Modifiers for <code><tag>define element</tag></code>:</h4>
  <blockquote><em>
	  The following attributes are only meaningful when defining an
	  Element.  It is impossible to represent this constraint in SGML.
  </em></blockquote>

  <define attribute=quoted optional>
    <doc> Indicates that the content of the element being defined should be
	  parsed, but not expanded before invoking the action.
    </doc>
  </define>
  <define attribute=literal optional>
    <doc> Indicates that the content of the element is unparsed (#CDATA).
    </doc>
  </define>
  <define attribute=empty optional>
    <doc> Indicates that the element being defined will never have any
	  content. 
    </doc>
  </define>

  <h4>Modifiers for <code><tag>define attribute</tag></code>:</h4>


  <define attribute=optional optional>
    <doc> Only meaningful for attributes.  Specifies that the attribute is
	  implied (optional).
    </doc>
    <note author=steve> === I'm not certain of the semantics of SGML
	  ``implied'' attributes, and in any case ``optional'' is unambiguous.
    </note>
  </define>
  <define attribute=required optional>
    <doc> Only meaningful for attributes.  Specifies that the attribute is
	  required.
    </doc>
  </define>
  <define attribute=fixed optional>
    <doc> Only meaningful for attributes.  Specifies that the attribute's
	  value is invariant (and must be given in its definition).
    </doc>
  </define>

  <h4>Modifiers for <code><tag>define entity</tag></code>:</h4>

  <define attribute=system optional>
    <doc> The value of this attribute is the ``system identifier'' (URI) of
	  an ``external entity''.  Usually it will be a filename relative to
	  the document containing the definition being processed.
    </doc>
  </define>
  <define attribute=public optional>
    <doc> The value of this attribute is the ``public identifier'' of an
	  external entity.  In a DTD, an alternative system identifier must be
	  provided; that should be specified via the <code>system</code>
	  attribute. 
    </doc>
  </define>
  <define attribute=NDATA optional>
    <doc> This specifies that the entity contains non-parsed data; the value
	  specifies the name of the data's <em>notation</em>.
    </doc>
  </define>
  <define attribute=retain optional>
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

  <li> <define element=action parent=define quoted handler>
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
  <define attribute=parent optional>
    <doc> The <code>parent</code> attribute specifies the tagset in which
	  names not defined in the current tagset will be looked up.  It is
	  effectively <em>included</em> in the tagset being defined.
    </doc>
  </define>
  <define attribute=include optional>
    <doc> The <code>include</code> attribute specifies a list of tagsets,
	  by name, whose contents are to be <em>copied into</em> the current
	  <tag>tagset</tag>.  This is different from <tag>context</tag>, which
	  effectively includes by reference.
    </doc>
  </define>
  <define attribute=tagset optional>
    <doc> The <code>tagset</code> attribute specifies the tagset to be used
	  when <em>parsing</em> the tagset being defined.  It specifies the
	  syntax of documentation elements and the operations permitted in
	  <tag>value</tag> and <tag>action</tag> elements.
    </doc>
  </define>
  <define attribute=recursive optional>
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
  <define attribute=name optional>
    <doc> Note that the <code>name</code> attribute is optional; it is
	  perfectly meaningful to have an anonymous <tag>namespace</tag>.
    </doc>
  </define>
  <define attribute=context optional>
    <doc> The <code>context</code> attribute specifies the namespace in which
	  names not defined in the current tagset will be looked up.  It is
	  effectively <em>included</em> in the tagset being defined.  If no
	  value is specified, the innermost namespace is assumed.
    </doc>
  </define>
  <define attribute=include optional>
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
  <define attribute=list optional>
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>foreach</tag></code> sub-element.
    </doc>
  </define>
  <define attribute=start optional>
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> sub-element.
    </doc>
  </define>
  <define attribute=stop optional>
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> sub-element.
    </doc>
  </define>
  <define attribute=step optional>
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> sub-element.
    </doc>
  </define>
  <define attribute=entity optional>
    <doc> If present, this specifies the name of the iteration variable for an
	  implied <code><tag>foreach</tag></code> or
	  <code><tag>for</tag></code> sub-element.
    </doc>
  </define>
</define>

<h4>Sub-elements of <tag>repeat</tag></h4>

The contents of a <tag>repeat</tag> is repeatedly expanded; all of the
following sub-elements are effectively iterating in parallel, which makes it
easy to go through multiple lists and number the corresponding elements (for
example). 

<ul>
  <li> <define element=foreach parent=repeat handler>
         <doc> The contents of this sub-element are a list; at each iteration
	       the specified element (default <code>&amp;li;</code>) is
	       replaced by an item from the list.
         </doc>
         <define attribute=entity optional>
           <doc> If present, this specifies the name of the iteration variable
		 used by the <tag>foreach</tag> sub-element.  
    	   </doc>
         </define>
       </define>
  <li> <define element=for parent=repeat handler>
         <doc> The iteration variable (default <code>&amp;n;</code>) is
	       incremented from the starting value (default 1) by the step
	       value (default 1) until it reaches the final value (no
	       default).  Iteration is stopped when the iteration variable
	       exceeds the final value.
         </doc>
         <define attribute=entity optional>
           <doc> If present, this specifies the name of the iteration variable
		 used by the <tag>for</tag> sub-element.  
    	   </doc>
         </define>
         <define attribute=start optional>
           <doc> If present, this specifies the starting value for the
		 iteration variable, replacing a <tag>start</tag> sub-element.  
    	   </doc>
         </define>
         <define attribute=stop optional>
           <doc> If present, this specifies the final value for the
		 iteration variable, replacing a <tag>stop</tag> sub-element.  
    	   </doc>
         </define>
         <define attribute=step optional>
           <doc> If present, this specifies the step value for the
		 iteration variable, replacing a <tag>step</tag> sub-element.  
    	   </doc>
         </define>
       </define>
       <ul>
	 <li> <define element=start parent=for handler>
	        <doc> The content must evaluate to a number, which is used as
		      the starting value for the iteration variable.
	        </doc>
	      </define>
	 <li> <define element=stop parent=for handler>
	        <doc> The content must evaluate to a number, which is used as
		      the final value for the iteration variable.
	        </doc>
	      </define>
	 <li> <define element=step parent=for handler>
	        <doc> The content must evaluate to a number, which is used as
		      the step value for the iteration variable.
	        </doc>
	      </define>
       </ul>
  <li> <define element=while parent=repeat handler>
         <doc> At each iteration the content is evaluated as a
	       <em>condition</em> as in <tag>if</tag>; if the condition is
	       <code>false</code> the <tag>repeat</tag> is terminated.
         </doc>
       </define>
  <li> <define element=until parent=repeat handler>
         <doc> At each iteration the content is evaluated as a
	       <em>condition</em> as in <tag>if</tag>; if the condition is
	       <code>true</code> the <tag>repeat</tag> is terminated.
         </doc>
       </define>
  <li> <define element=first parent=repeat handler>
         <doc> The contents are expanded exactly once, before the first
	       iteration. 
         </doc>
       </define>
  <li> <define element=finally parent=repeat handler>
         <doc> The contents are expanded exactly once, after the last
	       iteration. 
         </doc>
       </define>
</ul>

<h3>Logical</h3>
<define element=logical handler>
  <doc> This element is essentially a convenient shorthand for a nested set of
	<tag>if</tag> elements.  It performs functions that are equivalent to
	the LISP functions <code>AND</code> and <code>OR</code>.

	<p>With no attributes, this simply returns the value of every
	component that has a true value.  This can be used, for example, to
	remove whitespace from the content.
  </doc>
  <define attribute=and optional>
    <doc> If present, this specifies that a ``logical AND'' operations will be
	  performed.  Each child of the <tag>logical</tag> element is expanded
	  in turn.  Declarations, comments, processing instructions, and
	  whitespace are ignored.  If <em>every</em> other child expands to
	  something <tag>if</tag> would consider a ``<code>true</code>''
	  condition, the expansion of the <em>last</em> such child is passed
	  to the output.  Otherwise no output is generated.
    </doc>
  </define>
  <define attribute=or optional>
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
  <define attribute=text optional>
    <doc> Performs the indicated test on the text portion of the content,
	  discarding the markup.  In some cases, this has no effect.
    </doc>
  </define>
  <define attribute=not optional>
    <doc> Inverts the sense of the indicated test.
    </doc>
  </define>
  <define attribute=zero optional>
    <doc> Tests for the content being numerically equal to zero.  Whitespace
	  is considered to be zero, but nonblank text that cannot be converted
	  to a number is not.
    </doc>
  </define>
  <define attribute=positive optional>
    <doc> Tests for the content being numerically greater than zero.
    </doc>
  </define>
  <define attribute=negative optional>
    <doc> Tests for the content being numerically less than zero.
    </doc>
  </define>
  <define attribute=numeric optional>
    <doc> Tests for the content being convertable to a number.
    </doc>
  </define>
  <define attribute=match optional>
    <doc> The value of the attribute is a regular expression which is matched
	  against the content, converted to a string.   
    </doc>
  </define>
  <define attribute=exact optional>
    <doc> With the <code>match</code> attribute, performs an exact match. 
    </doc>
  </define>
  <define attribute=case optional>
    <doc> With the <code>match</code> attribute, performs a case-sensitive
	  match.  
    </doc>
  </define>
  <define attribute=null optional>
    <doc> Tests for the content being totally empty, even of whitespace.
    </doc>
  </define>
</define>


<h2>Document Structure Elements</h2>

<blockquote><em>
  Document structure elements select Nodes or sets of Nodes from a parse tree,
  and perform structural modifications on trees.  Note that the tree being
  operated on need not be the Input document; it might be a Namespace.
</em></blockquote>

<h3>Select and its components</h3>
<define element=select>
  <doc>
  </doc>
</define>

<h4>Sub-elements of <tag>select</tag></h4>
<ul>
  <li> <define element=from parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=in parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=child parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=tag parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=attr parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=iref parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=name parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=key parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=xptr parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=binding parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=parent parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=next parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=prev parent=select handler>
         <doc>
         </doc>
       </define>
  <li> <define element=replace parent=select handler>
         <doc>
         </doc>
       </define>
</ul>

<h2>Expansion Control Elements</h2>

<blockquote><em>
  Expansion control elements modify the processing of their contents, but are
  not conditional in the same way that control-structure operations are.  No
  tests are performed.
</em></blockquote>

<h3>Expand</h3>
<define element=expand handler>
  <doc> The content is actually expanded (processed) <em>twice</em>.  This is
	done in order to compute some active content and then expand it as if
	it had been in the input document.
  </doc>
  <define attribute=hide optional>
    <doc> The content is processed as usual, but the results are not passed to
	the output.  The effect is to process the content for side-effects
	only. 
    </doc>
  </define>
</define>

<h3>Protect</h3>
<define element=protect handler>
  <doc> The content is not expanded (unless the <code>result</code> attribute
	is present), but passed to the output as-is. 
  </doc>
  <define attribute=result optional>
    <doc> The content is expanded (once); this makes <tag>protect</tag> a
	  no-op unless the <code>markup</code> attribute is present.
    </doc>
  </define>
  <define attribute=markup optional>
    <doc> Markup in the content is ``protected'' from further expansion by
	  converting it to text, replacing markup-specific characters with the
	  corresponding entity references.
    </doc>
  </define>
</define>

<h3>Hide</h3>
<define element=hide handler>
  <doc> The content is processed as usual, but the results are not passed to
	the output.  The effect is to process the content for side-effects
	only. 
  </doc>
  <define attribute=markup optional>
    <doc> Only markup in the content is ``hidden'' -- text is passed through
	  to the output.  References to passive entities (including character
	  entities) are expanded.
    </doc>
  </define>
  <define attribute=text optional>
    <doc> Only text in the content is ``hidden'' -- markup is passed through
	  to the output.  References to passive entities (including character
	  entities) are expanded.
    </doc>
  </define>
</define>

<h2>Data Manipulation Elements</h2>

<blockquote><em>
  Data manipulation elements perform operations on data, typically text, that
  depend on some non-structural features of its content (e.g. its value as a
  number).
</em></blockquote>

<h3>Numeric operations</h3>
<define element=numeric handler>
  <doc> All passive markup in the content is ignored in most cases; the
	content is taken to be a sequence of numbers <em>n<sub>0</sub>,
	n<sub>1</sub>, ...</em> separated by whitespace.  Elements are
	represented by their first non-whitespace text.  If no operation is
	specified in the attributes, this sequence is returned as a
	space-separated list of Text nodes.
  </doc>
  <define attribute=sum optional>
    <doc> The numbers in the content are added.
    </doc>
  </define>
  <define attribute=difference optional handler>
    <doc> The difference  <em>n<sub>0</sub> - n<sub>1</sub> - ...</em> is
	  computed. 
    </doc>
  </define>
  <define attribute=product optional handler>
    <doc> The numbers in the content are multiplied.
    </doc>
  </define>
  <define attribute=quotient optional handler>
    <doc> The quotient <em>n<sub>0</sub> / n<sub>1</sub> / ...</em> is
	  computed. 
    </doc>
  </define>
  <define attribute=remainder optional handler>
    <doc> The remainder <em>n<sub>0</sub> % n<sub>1</sub> % ...</em> is
	  computed.  
    </doc>
  </define>
  <define attribute=power optional handler>
    <doc> The power <em>n<sub>0</sub> ^ n<sub>1</sub> ^ ...</em> is
	  computed. 
    </doc>
  </define>
  <define attribute=sort optional handler>
    <doc> The content is taken to be a list of items each of which must
	  contain a numeric value in its text. 
    </doc>
  </define>
  <define attribute=reverse optional>
    <doc> Causes a sort to be done in reverse order.
    </doc>
  </define>
  <define attribute=pairs optional>
    <doc> Sorts a list of key and value pairs according to the keys.
	  Typically this is used for the contents of <code>&lt;dl&gt;</code>
	  lists, in which the <code>&lt;dt&gt;</code> and
	  <code>&lt;dd&gt;</code> elements follow one another rather than
	  being hierarchical as they are in a table.
    </doc>
  </define>
  <define attribute=sep optional>
    <doc> Specifies the separator to be used between sorted or split (no
	  operation specified) numbers.
    </doc>
  </define>
  <define attribute=digits optional>
    <doc> The value is the number of digits to the right of the decimal point
	  to preserve in the output.  The default is zero if the result is an
	  integer, the maximum possible otherwise.
    </doc>
  </define>
  <define attribute=integer optional>
    <doc> All computation is done with (64-bit, signed) integer arithmetic.
	  Results of division operations are truncated.
    </doc>
  </define>
  <define attribute=extended optional>
    <doc> All computation is done with extended-precision integer arithmetic.  
    </doc>
  </define>
  <define attribute=modulus optional>
    <doc> All computation is done with modular arithmetic with the specified
	  modulus.  Combined with extended-precision, this can be used for
	  cryptographic calculations.
    </doc>
  </define>
</define>

<h3>Text operations</h3>
<define element=text handler>
  <doc> Passive markup in the content is ignored in most cases, but preserved
	in the output when possible.  For sorting purposes, the content is
	taken to be a sequence of text nodes separated by whitespace.
	Elements are represented by their first text content.  If no operation
	is specified in the attributes, this sequence is returned as a
	space-separated list of Text nodes.
  </doc>
  <define attribute=pad optional handler>
    <doc> Pad the text to the specified <code>width</code> with the specified
	  <code>align</code>ment (default is <code>left</code>).
    </doc>
  </define>
  <define attribute=trim optional handler>
    <doc> Trim the text to the specified <code>width</code> with the specified
	  <code>align</code>ment.  The default is <code>left</code>, meaning
	  that characters are trimmed from the right.  If no
	  <code>width</code> is specified, leading and trailing whitespace are
	  trimmed.
    </doc>
  </define>
  <define attribute=width optional>
    <doc> Specifies a width to pad or trim to.  If neither <code>pad</code>
	  nor <code>trim</code> is specified, padding or trimming is done as
	  needed. 
    </doc>
  </define>
  <define attribute=align optional>
    <doc> Specifies alignment when padding.  Permissible values are
	  <code>left</code>, <code>right</code>, <code>center</code>
    </doc>
  </define>
  <define attribute=sort optional handler>
    <doc> The list of items in the content is sorted according to their text
	  content.  Markup is ignored for sorting, but retained for output.
    </doc>
  </define>
  <define attribute=reverse optional>
    <doc> Causes a sort to be done in reverse order.
    </doc>
  </define>
  <define attribute=pairs optional>
    <doc> Sorts a list of key and value pairs according to the keys.
	  Typically this is used for the contents of <code>&lt;dl&gt;</code>
	  lists, in which the <code>&lt;dt&gt;</code> and
	  <code>&lt;dd&gt;</code> elements follow one another rather than
	  being hierarchical as they are in a table.
    </doc>
  </define>
  <define attribute=sep optional>
    <doc> Specifies the separator to be used between sorted or split items.
	  If not specified, the implied separator is a single space.
    </doc>
  </define>
  <define attribute=split handler optional>
    <doc> Splits the text into ``tokens'' (words) using the specified
	  separator.  
    </doc>
  </define>
  <define attribute=join handler optional>
    <doc> Joins items in the content by separating them with the given
	  separator.  The items, and the separators, remain distinct Text
	  nodes. 
    </doc>
  </define>
  <define attribute=merge handler optional>
    <doc> Merges items in the content by separating them with the given
	  separator, then concatenating them into a single Text node.
    </doc>
  </define>
</define>

<note author=steve>
  At some point we'll want to deal with <code>measure</code> and font
  metrics.
</note>

<h3>To-markup</h3>
<define element=to-markup handler>
  <doc> Strips passive markup (assumed to be decorative) from the text in the
	content to convert it to a single string.  Character and other passive
	entities are expanded.  Runs of consecutive whitespace characters are
	converted to single spaces.

	<p>If no other operation is specified in an attribute, special
	characters [<code>&amp;&lt;&gt;</code>] are replaced with entities,
	and line breaks are inserted to limit line lengths to 72 characters.
  </doc>
  <define attribute=usenet optional>
    <doc> Markup is added according to the conventions of Usenet mail and news
	  articles:  <code>_<i>italics</i>_</code>,
	  <code>*<b>bold</b>*</code>, and so on, similar to the old
	  <code>&lt;add-markup&gt;</code> tag.  Runs of multiple uppercase
	  letters are lowercased and set monospaced, and values after an equal
	  sign are italicized.  
    </doc>
  </define>
  <define attribute= optional>
    <doc> 
    </doc>
  </define>
</define>

The set of attributes used in <tag>to-markup</tag> and <tag>to-string</tag> is
open-ended; tagset authors are free to define new ones as needed.

<h3>To-text</h3>
<define element=to-text handler>
  <doc> Converts the marked-up content into one or more Text nodes (strings).
	If no other operation is specified in an attribute, the marked-up
	content is simply converted to its external representation.
  </doc>
  <define attribute= optional>
    <doc> 
    </doc>
  </define>
</define>


<h2>Data Structure Elements</h2>

<blockquote><em>
  Data structure elements perform no operations; they exist to represent
  common forms of complex structured data.  Strictly speaking,
  <code><tag>tagset</tag></code> and <code><tag>namespace</tag></code> are
  data structure elements.  Often a data structure element will have a
  representation that is a <em>subclass</em> of the representation of an
  ordinary Element.  (Currently <code>crc.dps.active.ParseTreeElement</code>).
</em></blockquote>

<!--template 
<h3>Xxx and its components</h3>
<define element=xxx >
  <doc>
  </doc>
  <define attribute= optional>
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
 --/template -->

<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

