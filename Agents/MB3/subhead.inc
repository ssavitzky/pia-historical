<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<set entity name="subs"><agent-list subs type="&agentType;"></set>
<if><test exact match="&agentType">&agentName;</test>
    <then><set name=agentPath>&agentName;</set></then>
    <else><set name=agentPath>&agentType;/&agentName;</set></else>
</if>
<set entity name="subs"><agent-list subs type="&agentType;"></set>
<table cellspacing=0 cellpadding=0 border=0>

<tr>
  <td width=170>
    <read quiet interform file="insert.if" process>
  <td>
    <table cellspacing=0 cellpadding=0 border=0>
    <tr><th width=170><img src="/Agency/Icons/blank170x1.gif"
	                   width=170 height=1></th>
        <td><br>
    <tr><th align=right>
		<if><test match=home.if>&fileName;</test>
		    <test match=index.if>&fileName;</test>
		    <test match=help.if>&fileName;</test>
		    <then><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*"></if>
	    <td><if><test match=home.if>&fileName;</test>
		    <then><b href="home.if">Home</b>
		    <else><a href="home.if">Home</a></if>
    		<if><test match=index.if>&fileName;</test>
		    <then><b href="index.if">Index</b>
		    <else><a href="index.if">Index</a></if>
    		<if><test match=help.if>&fileName;</test>
		    <then><b href="help.if">Help</b>
		    <else><a href="help.if">Help</a></if>
<if><read info interform file="initialize.if"><then>
    <tr><th><td><a href="options.if">Options</a>
	    	<a href="initialize.if">[re-initialize]</a></then>
</if>
    <tr><th><td><a href="http://internal/cgi-bin/people/CRC/WebEvent/WebEvent">CRC calendar</a>
	        
    <tr><th align=right>
		<if><test match=form.if>&fileName;</test>
		    <then><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*"></if>
	    <td><a href="view_day.if">View Day</a>
    <tr><th><td><a href="doc.html">Documentation</a>
    <if>&subs;<then>
    <tr><th align=right>
           <if><test not exact match="&agentType;">&agentName;</test>
	        <then><a href="/&agentType;/home.if">&agentType;/</a></then>
                <else>&agentType;/</else></if>
        <td><repeat list="&subs;">
               <a href="/&li;">&li;</a> <a href="/&li;/"><b> / </b></a>
           </repeat></then></if>
    </table>
</table>
<!-- $Id$ -->
