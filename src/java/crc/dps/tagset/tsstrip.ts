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


<tagset name=tsstrip parent=HTML tagset=xhtml>

<h1>Tagset Stripping Tagset</h1>

<doc> This file contains the XML definition for the a Tagset that can be used
      for stripping documentation out of Tagset files.
</doc>

Tagset definition files, their syntax, and their usual representation, are
documented in <a href="tagset.html">tagset.html</a>.

<h2>Definition Elements</h2>

<define element=tagset>
  <doc> &lt;tagset&gt; is the top-level element.  We have to transform it into
	an &lt;html&gt; element with appropriate head and body content. 
  </doc>
</define>

<define element=define>
  <doc> Define is simply passed through.
  </doc>
</define>

<define element=undefine>
  <action>  </action>
  <doc> Just by changing a <tag>define</tag> to <tag>undefine</tag> one can
	make it disappear from the documentation.
  </doc>
</define>

<define element=action quoted>
</define>

<define element=value quoted>
</define>

<define element=doc>
  <action> </action>
  <doc> Documentation is simply skipped. </doc>
</define>

<define element=note>
  <action> </action>
</define>

<h2>Additional Constructs:</h2>

<define element=cvs-id>
  <action><h5>&content;</h5></action>
</define>

<define element=tag>
</define>

<define element=ul><action>&content;</action></define>
<define element=ol><action>&content;</action></define>
<define element=li><action>&content;</action></define>
<define element=dl><action> </action>
  <doc>dl's are only used for documentation, so skip them.</doc>
</define>
<define element=p><action> </action></define>
<define element=blockquote><action> </action></define>
<define element=h2><action> </action></define>
<define element=h3><action> </action></define>
<define element=h4><action> </action></define>
<define element=h5><action> </action></define>

<hr>
<b>Copyright &copy; 1998 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
</tagset>

