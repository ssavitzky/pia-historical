<!doctype tagset system "tagset.dtd">
<tagset name=xxml parent=basic recursive>

<h1>XXML Tagset</h1>

<doc> This tagset consists of the primitive operations from the <a
      href="basic.html">basic</a> tagset, plus additional non-primitive
      convenience functions.  It is intended for use with eXtended XML.
</doc>


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
	is not limited to strict entity syntax.  A further advantage is that
	it works in situations (e.g. ECMAscript and other, mainly client-side
	DOM Level 1 applications) where entities are expanded automatically
	before the document processor can see them.
  </doc>
  <define attribute=name required>
    <doc> Specifies the name of the entity to be retrieved.
    </doc>
  </define>
</define>

<define element=set handler >
  <doc> The main advantages of <tag>set</tag> over the equivalent
	<tag>select</tag> are compactness and simplicity.  These also
	translate into a considerable increase in run-time efficiency.
  </doc>
  <define attribute=name required>
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

