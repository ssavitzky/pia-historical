<!doctype tagset system "tagset.dtd">
<tagset name=tsdoc parent=HTML tagset=standard>

<h1>Tagset Documentation Tagset</h1>

<doc> This file contains the XML definition for the a Tagset that can be used
      for formatting Tagset files.
</doc>

Tagset definition files, their syntax, and their usual representation, are
documented in <a href="tagset.html">tagset.html</a>.

<h2>Definition Elements</h2>

<define element=tagset>
  <doc> &lt;tagset&gt; is the top-level element.  We have to transform it into
	an &lt;html&gt; element with appropriate head and body content. 
  </doc>
  <action><html><head>
    <title>&lt;tagset <repeat list="&attributes;"> &li; </repeat>&gt;</title>
    </head><body>
      <h1>&lt;tagset <repeat list="&attributes;"> &li; </repeat>&gt;</h1>
	<blockquote>
	  This documentation generated from &DPS:inputFile; using
	  &DPS:tagsetName; 
	</blockquote>
      <if>&content;<then>&content;</if>
    </body></html>
  </action>
</define>

<define element=define>
  <action> <strong>&lt;define</strong>
	   <repeat list="&attributes;"> &li; </repeat><strong>&gt;</strong> <br>
	<if>&content;<then><blockquote>&content;</blockquote></if>
	<p>
  </action>
  <doc> Basically we're turning the declaration itself into a sort of header,
	except that we're assuming that headers already exist, so we just
	emphasize it.
  </doc>
</define>

<define element=action quoted>
  <action> &lt;action&gt;
	   <pre><protect markup>&content;</protect></pre>
	   &lt;/action&gt;
  </action>
</define>

<define element=value quoted>
  <action> &lt;value&gt;
	   <pre><protect markup>&content;</protect></pre>
	   &lt;/value&gt;
  </action>
</define>

<define element=doc>
  <action> <blockquote><em>&content;</em></blockquote> </action>
</define>

<define element=note>
  <action>
  <dl>
    <dt> <strong>Note: </strong> (&attributes;)
    <dd> <em>&content;</em>
  </dl></action>
</define>

<h2>Additional Constructs:</h2>

<define element=cvs-id>
  <action><h5>&content;</h5></action>
</define>

<define element=tag>
  <action><code>&lt;&content;&gt;</code></action>
</define>


<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

