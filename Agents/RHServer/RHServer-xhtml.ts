<!doctype tagset system "tagset.dtd">
<tagset name=RHServer-xhtml parent=pia-xhtml recursive>

<h1>Simple-xhtml Tagset</h1>

<define element=extract-content>
	<doc> Extracts a phrase from a node.  The parent node is
		of type rhconcept-phrase.  This tag first calculates
		the number of children, then loops through the children
		and returns them.
	</doc>
	<define attribute=entity required></define>
		<action>
		<set name="num">0</set>
			<select><from>&attributes:entity;</from> #TEXT </select>
				<repeat> <foreach entity=x>&selected;</foreach>
					<for><start>0</start><stop>9</stop></for>
						<set name=num>&n;</set>
				</repeat>
			<repeat start=0 stop=&num;>
				<select><from>&attributes:entity;</from>
					<child>&n;</child> 
				</select>
			</repeat>
		</action>
	</define>
</define>


<define element=concepts>
	<doc> Extracts all concept strings from the rhconcept-phrase fields
		in the piaconcept data structure.
	</doc>
	<define attribute=path required></define>
	<action>
	<set name=consrc><connect src=&attributes:path; entity=rhcon></connect></set>
	<select><from>&rhcon;</from><name recursive>rhconcept-phrase</name>
		<repeat list=&selected;>
			<OPTION><text split sep=" "><extract-content entity=&li;></extract-content></text>
		</repeat>
	</select>

	</action>
</define>


<define entity=blue-dot>
  <value><img src="/Agency/Icons/dot-blue.gif"
		height=20 width=20 alt="*"></value>
</define>

<define entity=blank-170x1>
  <value><img src="/Agency/Icons/white170x1.gif" width=170 height=1
		alt=" "></value>
</define>

<define entity=A100>
  <doc> Large pentagonal A, which serves as an identifying logo for the 
	Agency agent.
  </doc>
  <value><img src="Logo/A100.gif" height=100 width=111 
		alt="AGENCY"></value>
</define>


<define element=sub-head>
  <doc> A secondary table located immediately under the header.
	Content should consist of additional table rows.
  </doc>
  <define attribute=page>
    <doc> the base name of the page, e.g. <code>index</code> or
	  <code>home</code>.
    </doc>
  </define>
  <action>
<table cellspacing=0 cellpadding=0 border=0>
<tr><th align=center valign=center nowrap width=170>&A100;
    <td>
    <table cellspacing=0 cellpadding=0 border=0>
    <tr><th align=left nowrap width=170>&blank-170x1;<td><br>
    <tr><th align=right><xopt page="&attributes:page;"
			      pages="home profiles groups concepts">&blue-dot;</xopt>
	<td> <xa href="home" page="&attributes:page;">Home</xa>
    	     <xa href="profiles" page="&attributes:page;">Profiles</xa>
    	     <xa href="groups" page="&attributes:page;">Groups</xa>
	     <xa href="concepts" page="&attributes:page;">Concepts</xa>
   <get name=content>
  </table>
</table>
  </action>
</define>


</tagset>
