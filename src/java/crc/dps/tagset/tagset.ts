<!doctype tagset system "tagset.dtd">
<tagset name=tagset parent=HTML>
<title>Tagset for Tagset Definition Files</title>
<cvs-id>$Id$</cvs-id>

<h2>Introduction:</h2>

<p> This file contains the <a href="#XHML">eXtended HTML (XHML)</a> definition
    for the <code>tagset</code> <a href="#tagset">tagset</a>, or in other
    words, the formal definition of the language in which tagsets are defined.
    (Yes, this is a <a href="#circular">circular definition</a>.)  You are
    probably reading it in HTML form, as <a href="tagset.html"
    ><code>tagset.html</code></a>; this was derived automatically from the
    XHML original file, <a href="tagset.ts"><code>tagset.ts</code></a>.

<p> A tagset definition file such as this is essentially a mapping into
    XHML of an SGML Document Type Definition (DTD), and in fact can be
    automatically processed into one.  The associated document-processing
    semantics are, however, lost in the translation process, along with some
    of the associated documentation, which is represented in HTML.  The
    developers of this system hope that you will find the resulting documents
    easier to read than the equivalent DTD's. 

<p> (Another way of looking at a tagset definition file is as an HTML file
    that documents a markup language, augmented with a handful of XML
    constructs that actually <em>define</em> the language.  Because both
    the formal and informal descriptions are mixed in the same file, it is
    easy to derive some of the documentation directly from the formal
    components, thus ensuring its correctness.  This allows the developer to
    confine the <em>informal</em> parts of the documentation to descriptive
    material, without having to worry about keeping two equivalent versions of
    every definition in sync.)

<h3>How to Read Tagset Descriptions</h3>

<p> A tagset definition file consists of a sequence of <tag>define</tag>
    elements, some of which are nested inside others.  In particular, the
    outermost <tag>define</tag> elements define SGML ``<a
    href="#elements">elements</a>'' and ``<a href="#entities">entities</a>''.
    Nested inside each element definition are the definitions of its
    ``attributes''.  Any definition can also contain documentation.

<p> When converted to HTML for documentation, the nesting of attribute
    definitions inside of element definitions is rendered as indentation.
    Documentation is nested one more level and typeset in italics.  The
    <code>&lt;/define&gt;</code>, <code>&lt;doc&gt;</code>, and
    <code>&lt;/doc&gt;</code> tags are omitted.

<p> In some cases, certain elements are only meaningful inside of other
    elements; for example, <tag>then</tag> elements only occur inside of
    <tag>if</tag> elements.  By convention, the definitions of these elements
    follow that of their parent in a subsection containing an unordered
    (bulleted) list. 

<p> Other constructs, mainly involving attributes of <tag>define</tag>, are
    documented below at the point where they are first used. 

<h3>How Resources are Located and Loaded</h3>

<p> Java's resource mechanism allows arbitrary data files to exist in the same
    namespace as classes.  They can be shipped around in JAR files, downloaded
    from the Net, and (in most cases) obtained from directories in the
    <code>CLASSPATH</code>.  In particular, this means that common tagsets can
    be defined in the same directory as the classes of the
    <code>crc.dps.tagset</code> package.

<p> Tagsets and other resources (including external entities) can exist either
    as Java resources or as files located relative to the document being
    processed.  They can also be located in the DPS, in an internal namespace.

<p> When a tagset is specified in a document's DTD, it will be a system
    identifier with the extension ``<code>.dtd</code>''.  This is stripped off
    to facilitate searching for a corresponding ``<code>.ts</code>'' file
    instead.  A tagset specified on a command line takes precedence over one
    specified in the document's <code>doctype</code> declaration.  This allows
    documents to be processed with tagsets other than the one they were
    originally written for.

<p> The search order for tagsets is as follows:
<ol>
  <li> First, the namespace is identified.  An identifier followed by a colon
       indicates an explicit namespace.  Otherwise, if the name contains
       slashes it is considered to be a file located relative to the current
       document; otherwise if it contains periods it is considered to be a
       resource.  Tagsets with neither are looked for first in the current
       directory (the explicit namespace <code>file:</code>), then in the
       package <code>crc.dps.tagset</code> in the explicit namespace
       <code>DPS:</code>.

  <li> If the namespace is specified explicitly, it performs the lookup.

  <li> The tagset's name is first looked up along the internal processing
       context chain defined by the <code>topProcessor</code> links in
       <code>Context</code> objects.  

  <li> Class <code><em>tsname</em>.class</code> is searched for provided the
       name could be in the package/resource namespace.  If it exists and
       implements the <code>Tagset</code> interface, it is loaded and
       instantiated.  
       
  <li> <code><em>tsname</em>.ts</code> is searched for, first in the current
       directory, then as a resource, to identify the correct namespace.

  <li> <code><em>tsname</em>.obj</code> is loaded as a serialized
       object, if it exists in the same location and is newer than 
       <code><em>tsname</em>.ts</code>
       
  <li> <code><em>tsname</em>.tss</code> is processed using the minimal
       bootstrap tagset containing <tag>tagset</tag>, <tag>define</tag>, and
       their sub-elements, if it exists in the same location and is newer than
       <code><em>tsname</em>.ts</code>.  This is assumed to be a tagset
       ``stripped'' of its documentation.

  <li> <code><em>tsname</em>.ts</code> is processed using the
       <code>tagset</code> tagset. 
</ol>

<p> Once a tagset is loaded, its name is added to the appropriate namespace. 

<p> The search order for entities is similar, except that entities cannot be
    represented as classes, and external entity names <em>are</em> expected to
    have an extension. 

<p> While processing a tagset's start tag, the tagset mentioned in its
    <code>context</code> is also loaded, followed by any tagsets mentioned in
    its <code>include</code> attribute.  Tagsets and entities mentioned in
    resources are not looked for relative to the current document, but only
    relative to the current resource.


<h2>Definition Elements</h2>

<h3>Define and its components</h3>

<define element=define handler no-text default-content=value>
  <doc> Defines an element, attribute, entity, or word.  It is meaningful for
	for <tag>define</tag> to occur outside of a <tag>namespace</tag> or
	<tag>tagset</tag> element because there is always a ``current''
	namespace and tagset in effect.
  </doc>
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
	  value, if any, should be the MIME type of the data.  A ``notation''
	  is the identifier used in a DTD to describe the data in an unparsed
	  external entity.
    </doc>
  </define>

  <doc> <strong>General modifiers:</strong>
  </doc>

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

  <doc> <strong>Modifiers for <code><tag>define attribute</tag></code>:</strong>
  </doc>

  <define attribute=optional optional>
    <doc> Only meaningful for attributes.  Specifies that the attribute is
	  optional (optional).
    </doc>
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

  <doc> <strong>Modifiers for <code><tag>define entity</tag></code>:</strong>
  </doc>

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
  <li> <define element=value parent=define>
         <doc> The <tag>value</tag> sub-element defines a value for the node
	       being defined.  The node will be replaced by its value whenever
	       it appears in a document being processed.
         </doc>
       </define>

  <li> <define element=action parent=define quoted>
         <doc> The <tag>action</tag> sub-element defines an action for the
	       node being defined.  Note that it is possible for a node to
	       have both an action and a value.
         </doc>
       </define>
</ul>

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

<h3>Tagset and Namespace</h3>

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

  <define attribute=context optional>
    <doc> The <code>context</code> attribute specifies the tagset in which
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
  <define attribute=recursive optional>
    <doc> If present, this attribute indicates that elements defined in the
	  tagset can be used in the definitions of other elements.  The
	  default is for definitions to be in terms of elements and entities
	  defined in the <em>enclosing document's</em> tagset.  This allows
	  restricted tagsets to be defined.
    </doc>
  </define>
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


<h2>Formal documentation comment</h2>

<doc> This tagset defines the syntax and semantics of files that define
      tagsets.  It exists only for documentation purposes; the actual
      machinery has to be present during initialization.
</doc>

<h2>Notes and Glossary</h2>

<dl>
  <dt> <a name="XXML">XXML</a> (eXtended, or eXecutable, XML)
  <dd> XML (Extensible Markup Language) augmented with semantic descriptions,
       also XML, that specify <em>actions</em> to be taken by a suitable
       document processor such as the DPS.  XML is an SGML ``application''.  A
       document in XXML is called an ``active document''.

  <dt> <a name="XHML">XHML</a> (eXtended Hypertext Markup Language)
  <dd> HTML (HyperText Markup Language) extended with <a href="#XXML">XXML</a>
       constructs.  Because HTML includes SGML constructs that are not
       permitted in XML, XXML strictly speaking does not conform to the XML
       specification.  It does, however, conform to the <em>SGML</em>
       specification.

  <dt> <a name="tagset">Tagset</a>
  <dd> A Tagset is the XHML representation of an SGML Document Type
       Definition (DTD), augmented with additional semantic information that
       specifies the actions to be taken by a document processing system when
       it encounters certain ``active'' constructs in the document.

  <dt> <a name="circular">Circular Definition</a>
  <dd> See ``Circular Definition'' -- something defined in terms of itself.
       The key to unwinding the circularity involved in the Tagset called
       ``<code>tagset</code>'' (which defines the syntax and semantics of
       tagset files) is to observe that the definitions <em>really</em>
       required to get started, namely <tag>tagset</tag>, <tag>define</tag>,
       and their components, are ``compiled in'' to the document processor.
       These, plus the fact that XML and almost all of HTML can be parsed with
       the default XML syntax, are sufficient for reading in stripped-down
       versions of the HTML and Basic tagsets.
  <dt> <a name="elements">Elements</a>
  <dd> those SGML constructs that look like:
       <pre>
       &lt;<em>tagname</em> <em>attribute</em>=<em>value</em>&gt;
       <em>... content ...</em>
       &lt;/<em>tagname</em>&gt;
       </pre>

       The construct in angle brackets in at the start of the element is
       called the ``start tag'', and the construct at the end is called the
       ``end tag''.  An Element which can  never have any content is called
       ``empty'', and its end tag can be omitted.

       <p>In the Document Processing System, elements can be defined to have
       associated ``actions'' that operate like macros or functions.  These
       elements are called ``active''.

  <dt> <a name="entities">Entities</a>
  <dd> Those SGML constructs that look like
       ``<code>&amp;<em>name</em>;</code>'' and which represent a named piece
       of content to be substituted into the document.  In the Document
       Processing System, entities can be used similarly to variables in
       programming languages.

</dl>

<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

