<!doctype tagset system "tagset.dtd">
<tagset name=RHServer-xhtml parent=pia-xhtml recursive>

<h1>RHServer Tagset</h1>

<h2>Data Extraction Tags</h2>


<define element=get-all-concepts>
	<doc>
		Retrieves all nodes of type rhconcept and lists
		them by name; i.e. returns their name attribute.
	</doc>
	<action>
		<select><from>&AGENT:all-concepts;</from><name recursive>rhconcept</name>
			<repeat list=&selected;>
				<OPTION><select><from>&li;</from><name recursive>rhconcept-name</name><content>
				</select>
			</repeat>
		</select>
	</action>
</define>

<define element=get-concept-by-name>
	<doc> Given a concept name, returns the concept.
	</doc>
	<define attribute=name required></define>
	<action>
		<select><from>&AGENT:all-concepts;</from><name recursive>rhconcept</name>
			<repeat> <foreach entity=x>&selected;></foreach>
				<select><from>&x;</from><name recursive>rhconcept-name</name><content>
					<if><test match=&attributes:name;>&selected;</test><then>
						&x;
					</if>
				</select>
			</repeat>
	</action>
</define>

<define element=get-concept-field>
	<doc>Given a concept name and a concept field name,
		returns the value for that field.
	</doc>
	<define attribute=name required></define>
	<define attribute=field required></define>
	<action>
		<set name=cpt>
			<get-concept-by-name name=&attributes:name;>
			</get-concept-by-name>
		</set>
		<select><from>&cpt;</from><name recursive>&attributes:field;</name><content>
		</select>
	</action>
</define>


<define element=get-all-phrases>
	<doc> Given a concept name, retrieves all phrases
		associated with that concept.
	</doc>
	<define attribute=concept required></define>
	<action>
		<set name=cpt>
			<get-concept-by-name name=&attributes:concept;>
			</get-concept-by-name>
		</set>
		<select><from>&cpt;</from><name recursive>rhconcept-phraselist</name>
			<repeat><foreach entity=x> &selected;></foreach>
				<select><from>&x;</from><name recursive>rhconcept-phrase</name><content>
				</select>
			</repeat>
	</action>
</define>

<define element=remove-concept-group>
	<doc>Given a concept name, and a group name,
		 removes this group from the concept
	</doc>
	<define attribute=concept required></define>
	<define attribute=group required></define>
	<action>
		<set name=cpt>
			<get-concept-by-name name=&attributes:concept;>
			</get-concept-by-name>
		</set>
		<select><from>&cpt;</from><name recursive>rhconcept-group</name>
			<repeat><foreach entity=x>&selected;</foreach>
				<set name=gname><select>&x;<content></select></set>
				<if><test match=&attributes:group;>&gname;</test><then>
					<select>&x;
						<remove>&selected;</remove>
					</select>
			
				</if>
			</repeat>
		</select>
	</action>
</define>


<define element=get-all-concept-groups>
	<doc> Given a concept name, retrieves all group names
		associated with that concept.
	</doc>
	<define attribute=concept required></define>
	<action>
		<set name=cpt>
			<get-concept-by-name name=&attributes:concept;>
			</get-concept-by-name>
		</set>
		<select><from>&cpt;</from><name recursive>rhconcept-grouplist</name>
			<repeat><foreach entity=x> &selected;></foreach>
				<select><from>&x;</from><name recursive>rhconcept-group</name><content>
				</select>
			</repeat>
	</action>
</define>

<define element=get-all-groups>
	<doc> Extracts all group strings from the rhconcept-group fields
		in the piaconcept data structure.  Returns each group in
		a menu op;tion format.
	</doc>
	<action>
	<select><from>&AGENT:all-concepts;</from><name recursive>rhconcept-group</name><content>
		<unique>&selected;</unique>
		<repeat><foreach entity=x>&selected;</foreach>
			<OPTION>&x;
		</repeat>
	</select>
	</action>
</define>


<define element=get-group-by-name>
	<doc>Given a group name, returns that group. This may fail
		 to match where a group-list contains several items.
	</doc>
	<define attribute=group required></define>
	<action>
		<select><from>&AGENT:all-concepts;</from><name recursive>rhconcept-group</name><content>
			<repeat><foreach entity=x>&selected;</foreach>
				<if><test match=&attributes:group;>&x;</test><then>
					&x;
				</if>
			</repeat>
		</select>

	</action>
</define>


<define element=remove-group>
	<doc>Given a group name, removes this group from
		all concepts that list this group as a
		member.
	</doc>
	<define attribute=group required></define>
	<action>
		<select><from>&AGENT:all-concepts;</from><name recursive>rhconcept-group</name>
			<repeat><foreach entity=x>&selected;</foreach>
				<set name=gname><select>&x;<content></select></set>
				<if><test match=&attributes:group;>&gname;</test><then>
					<select>&x;
						<remove>&selected;</remove>
					</select>
				</if>
			</repeat>
		</select>
	</action>
</define>

<define element=get-all-group-concepts>
	<doc> Given a group name, retrieves all concepts
		associated with that group.  Returns the
		concept name.
	</doc>
	<define attribute=group required></define>
	<action>
		<set name=cpt>
			<select><from>&AGENT:all-concepts;</from><name recursive>rhconcept</name>
			</select>
		</set>
		<select>
			<repeat><foreach entity=x>&cpt;</foreach>
				<!-- Find group by name within individual concept x -->
				<select><from>&x;</from><name recursive>rhconcept-group</name><content>
					<repeat><foreach entity=y>&selected;</foreach>
						<!-- If group matches name, return the concept name -->
						<if><test match=&y;>&attributes:group;</test><then>
							<OPTION><select><from>&x;</from><name recursive>rhconcept-name</name><content></OPTION>
							</select>
						</if>
					</repeat>
				</select>
			</repeat>
		</select>
	</action>
</define>


<!-- Probably obsolete -->
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

<!-- Probably obsolete -->
<define element=concepts>
	<doc> Extracts all concept strings from the rhconcept-phrase fields
		in the piaconcept data structure.
	</doc>
	<define attribute=path required></define>
	<action>
	<select><from>&AGENT:all-concepts;</from><name recursive>rhconcept-phrase</name>
		<repeat list=&selected;>
			<OPTION><text split sep=" "><extract-content entity=&li;></extract-content></text>
		</repeat>
	</select>

	</action>
</define>

<h2>Layout and Appearance Tags</h2>

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
	<if><test match=&attributes:page;>home</test<then>
	    <tr><th align=right><xopt page="&attributes:page;"
			      pages="home profiles groups concepts">&blue-dot;</xopt>
		<td> <xa href="home" page="&attributes:page;">Home</xa><!--Home Page-->
    	     	<xa href="profiles" page="&attributes:page;">Profiles</xa>
    	     	<xa href="groups" page="&attributes:page;">Groups</xa>
	     	<xa href="concepts" page="&attributes:page;">Concepts</xa>
		</td>
	    </tr>
	</then>
	</if>
	<if><test match=&attributes:page;>profiles</test><then>
	    <tr><th align=right><xopt page="&attributes:page;"
			      pages="profiles home">&blue-dot;</xopt>
		<td> <xa href="profiles" page="&attributes:page;">Profiles</xa><!--Profiles page-->
	     	<xa href="home" page="&attributes:page;">Home</xa>
		</td>
	    </tr>	
	</then>
	</if>
	<if><test match=&attributes:page;>groups</test><then>
	    <tr><th align=right><xopt page="&attributes:page;"
			      pages="groups profiles home">&blue-dot;</xopt>
		<td> <xa href="groups" page="&attributes:page;">Groups</xa><!--Groups page-->
	     	     <xa href="home" page="&attributes:page;">Home</xa>
                     <xa href="profiles" page="&attributes:page;">Profiles</xa>
		</td>
	    </tr>
	</then>
	</if>
	<if><test match=&attributes:page;>concepts</test><then>
	    <tr><th align=right><xopt page="&attributes:page;"
			      pages="concepts profiles home">&blue-dot;</xopt>
	    <td> <xa href="concepts" page="&attributes:page;">Concepts</xa><!--Concepts page-->
             <xa href="home" page="&attributes:page;">Home</xa>
             <xa href="profiles" page="&attributes:page;">Profiles</xa>
	    </td>
            </tr>	
	</then>
	</if>
	<if><test match=&attributes:page;>group_edit</test><then>
	    <tr><th align=right><xopt page="&attributes:page;"
			      pages="group_edit profiles home">&blue-dot;</xopt>
		<td> <xa href="group_edit" page="&attributes:page;">Group_Edit</xa><!--Group_Edit page-->
        	     <xa href="groups" page="&attributes:page;">Groups</xa>
	     	     <xa href="home" page="&attributes:page;">Home</xa>
		</td>
	    </tr>	
	</then>
	</if>
	<if><test match=&attributes:page;>concept_edit</test><then>
   	 <tr><th align=right><xopt page="&attributes:page;"
			      pages="concept_edit profiles home">&blue-dot;</xopt>
		<td> <xa href="concept_edit" page="&attributes:page;">Concept_Edit</xa><!--Concept_Edit page-->
           	  <xa href="concepts" page="&attributes:page;">Concepts</xa>
	    	 <xa href="home" page="&attributes:page;">Home</xa>
		</td>
    	</tr>
	</then>
	</if>	
   <get name=content>
  </table>
</table>
  </action>
</define>

<define element=add-concept-group>
	<doc>Given a concept name, and a group name,
		 adds this group to the concept
	</doc>
	<define attribute=concept required></define>
	<define attribute=group required></define>
	<action>
		<set name=cpt>
			<get-concept-by-name name=&attributes:concept;>
			</get-concept-by-name>
		</set>
		<select><from>&cpt;</from><name recursive>rhconcept-grouplist</name>
			<child>-1</child>
				<append><protect markup><rhconcept-group>&attributes:group;</rhconcept-group></protect></append>
		</select>
	</action>
</define>

<define element=save-group-name>
	<doc>Saves the group name to a file.  Need to have
		this name persist across transactions.  There must
		be a better way...
	</doc>
	<define attribute=name required></define>
	<action>
		<connect src="persist.out" method=put>
			&attributes:name;
		</connect>
	</action>
</define>

<define element=get-group-name>
	<doc>Gets the group name saved to file.
	</doc>
	<define attribute=name required></define>
	<action>
		<connect src="persist.out" entity=AGENT:groupName>
		</connect>
	</action>
</define>


</tagset>
