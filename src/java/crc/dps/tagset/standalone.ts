<!doctype tagset system "tagset.dtd">
<tagset name=standalone include=basic recursive>

<h1>Standard Tagset</h1>

<doc> This tagset consists of the primitive operations from the <a
      href="basic.html">basic</a> tagset, plus additional non-primitive
      convenience functions.  It is intended to ``stand alone'' as a basis for
      active XML.
</doc>

<note author=steve>
  Unlike the legacy ``Standalone'' tagset, which was meant for HTML document
  processing outside the PIA, this tagset is meant to ``stand alone'' without
  HTML, and so is intended as the basis for XML document processing.
</note>

<h2>Definition Tags</h2>


<h3>Get and Set</h3>

<blockquote><em>
  These are convenience functions that mainly substitute for an entity
  references or <tag>select</tag>.  The main differences are that they are
  more efficient, and are not limited to identifier syntax.  This allows
  names that are not entities to be kept in the same namespace as
  entities.
</em></blockquote>

<define element=get empty handler >
  <doc> The main advantage of <tag>get</tag> over the equivalent entity
	reference is that the name can be computed by entity substitution, and
	is not limited to strict entity syntax.  
  </doc>
  <define attribute=entity required>
    <doc> Specifies the name of the entity to be retrieved.
    </doc>
  </define>
</define>

<define element=set handler >
  <doc> The main advantages of <tag>set</tag> over the equivalent
	<tag>select</tag> are compactness and simplicity.  These also
	translate into a considerable increase in run-time efficiency.
  </doc>
  <define attribute=entity required>
    <doc> Specifies the name of the entity to be set.
    </doc>
  </define>
</define>


<h2>Control Structure Tags</h2>


<h2>Data Manipulation Tags</h2>


<h2>Data Structure Tags</h2>


<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

