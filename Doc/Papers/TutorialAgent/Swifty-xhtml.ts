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


<tagset name=Swifty-xhtml parent=pia-xhtml recursive>

<h1>Swifty-XHTML Tagset</h1>

<doc>   This tagset is local to the Swifty agent.  It provides some special
	capabilities that are not already provided in the PIA tagset. 
	Two of the tags have one main purpose: to prevent adding duplicate
	links to the list that is displayed in the home page. To do this, 
	two tags have been defined. 

	    1. checklink checks to see whether the URL passed in is already 
	       on the list of links. If it is, it does nothing; if it is not 
	       already on the list, that link is returned.  This signals to
	       appendlink that the link is already on the list.
            2. appendlink checks the value of each checkbox that was selected 
	       by the user. It then calls checklink to see whether it is 
	       already on the list. If checklink returns a value, appendlink
	       does not add that value to the existing AGENT:links list. 

	The third tag, connectto, illustrates how to use the PIA basic tag 
	connect. 
</doc>


<define element=checklink>
	<define attribute=link required></define>
	<doc>
		checklink has one attribute:  the URL to be checked.  
		The argument, or attribute value, passed into the 
		element is set to the attribute local variable. 
		Processing for this element is defined between the
		action start and end tags.  In the action code, the 
		current contents of the global variable AGENT:links 
		is selected. This means that the current list of links 
		is captured in variable "selected".  Using the repeat 
		tag, we iterate through the list of links. At 
		each iteration, we use the test tag to see whether 
		the list member for that iteration matches against the 
		argument that we passed in. If there is a match then 
		we return the list member, otherwise do nothing. 
	</doc>
	<action>
	<select>&AGENT:links;<content>
		<repeat list="&selected;">
			<if><test match=&attributes:link;>&li;</test><then>
				&li;
			</then>
			</if>
	</repeat>
	</select>
	</action>
</define> <!-- end of checklink -->

<define element=appendlink>
  <doc>
	appendlink is self-contained and consequently needs no arguments.
	Like <checklink> this element is selecting the contents of AGENT:links
	as the selected set of links. Note that the contents of AGENT:links
	is being expanded within an unordered list environment. Then, for each 
	checkbox button on the agent editing page, it checks to see whether it 
	has been selected. If so, it calls checklink to see if it is already 
	on the list. If checklink returns that link, nothing further needs to 
	be done. If the link is not already on the list, a list item is created 
	for that link and it is added to the unordered list of links. Each 
	checkbox is checked in exactly the same manner. 

  </doc>		
  <action>
	<set name=weaURL><A HREF="http://www.nws.noaa.gov" 
		TARGET=_blank>Weather</A></set>
	<set name=sjmnURL><A HREF="http://www.sjmercury.com" 
		TARGET=_blank>Silicon Valley News</A></set>
	<set name=yahURL><A HREF="http://www.yahoo.com" 
		TARGET=_blank>Yahoo</A></set>
	<set name=dejURL><A HREF="http://www.dejanews.com" 
		TARGET=_blank>DejaNews</A></set>
	<set name=hisURL><a href="/History">History</a></set>

	<ul>
	<select>&AGENT:links; <content></select>
		<if>&FORM:wea;<then>
			<if><checklink link=&weaURL;></checklink>
			<else>
				<li> &weaURL;
			</else></if>
		
		</if>
		<if>&FORM:sjmn;<then>
			<if><checklink link=&sjmnURL;></checklink>
			<else>
				<li> &sjmnURL;
			</else></if>
		</if>
		<if>&FORM:yah;<then>
			<if><checklink link=&yahURL;></checklink>
			<else>
				<li> &yahURL;
			</else></if>
		</if>
		<if>&FORM:dej;<then>
			<if><checklink link=&dejURL;></checklink>
			<else>
				<li> &dejURL;
			</else></if>
		</if>
		<if>&FORM:his;<then>
			<if><checklink link=&hisURL;></checklink>
			<else>
				<li> &hisURL;
			</else></if>
		</if>
	</ul>
  </action>
</define> <!-- end of appendlink -->

<define element=connectto>
	<define attribute=url required></define>
	<define attribute=link required></define>
	<doc>
		The <connectto> element makes a connection to a 
		given URL and displays a link from within the page 
		returned for that URL. This element was defined 
		mainly to give an example of the kinds of things 
		you can do with the connect tag. Two attributes 
		are required: the url you are going to connect to, 
		and a word that appears in a link within that page. 

       		First, a connection is made to the named url, which 
		is set to the local variable doc. The result 
		attribute is set to content, which means that the
		contents of the connected document should be
		returned.  Next, a recursive select is done to 
		retrieve all nodes that are anchors within the 
		page. The list of anchors is captured in 
		the local variable selanchors. The main processing 
		loop iterates through the list of anchors from Yahoo, 
		testing each one to see whether its text matches
		the link attribute that was passed into the element. 
		There will be a match if the link attribute string 
		appears anywhere inside the anchor. If there is a 
		match, a second test is made to determine whether this 
		link has an absolute url. If it does, it is returned 
		as an unordered list item. 
	</doc>
	<action>
		<set name=doc><connect src=&attributes:url; result=content />
			</set>
		<set name=selanchors><select>
			<from>&doc;<content></from><name recursive>a</name>
			</select></set>

		<ul>
		<repeat><foreach entity=x>&selanchors;</foreach>
			<if><test match=&attributes:link;>&x;</test><then>
				<if><test match="http">&x;</test><then>
					<li> &x;
				</then>
				</if>
			</then>
			</if>
		</repeat>
		</ul>
	</action>
</define> <!-- end of connectto -->


</tagset> <!-- end of tagset -->

