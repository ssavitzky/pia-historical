<!doctype tagset system "tagset.dtd">
<tagset name=Swifty-xhtml parent=pia-xhtml recursive>

<h1>Swifty-XHTML Tagset</h1>

<doc> This tagset is local to the Swifty agent.
</doc>


<define element=checklink>
	<define attribute=link required></define>
	<doc>
		Goes through the existing list of links and checks
		whether the attribute link is on the list.  If it is,
		that link is returned.
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
</define>


<define element=appendlink>
  <doc>
	If a link selection has been made and the link is not already on
	the list of links, add it.
  </doc>		
  <action>
	<set entity name=weaURL><A HREF="http://www.nws.noaa.gov" TARGET=_blank>Weather</A></set>
	<set entity name=sjmnURL><A HREF="http://www.sjmercury.com" TARGET=_blank>Silicon Valley News</A></set>
	<set entity name=yahURL><A HREF="http://www.yahoo.com" TARGET=_blank>Yahoo</A></set>
	<set entity name=dejURL><A HREF="http://www.dejanews.com" TARGET=_blank>DejaNews</A></set>
	<set entity name=hisURL><a href="/History">History</a></set>

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
</define>

<define element=connectto>
	<define attribute=url required></define>
	<define attribute=link required></define>
	<doc>
		Connects to a url and displays the link from that
		url specified by link; for example, the News link
		in the yahoo url.  Only displays links that have absolute
		urls.  Links are displayed as unordered lists.
	</doc>
	<action>
		<set entity name=doc><connect src=&attributes:url; result=content /></set>
		<set entity name=selanchors><select><from>&doc;<content></from><name recursive>a</name></select></set>

		<ul>
		<repeat><foreach entity=x>&selanchors;</foreach>
			<if><test match=&attributes:link;>&x;</test><then>
				<set entity name=sresult><text split>&x;</text></set>
					<repeat><foreach entity=i>&sresult;</foreach>
						<if><test match="http">&i;</test><then>
							<li> &x;
						</then>
						</if>
					</repeat>
				</set>
			</then>
			</if>
		</repeat>
		</ul>
	</action>
</define>


</tagset>

