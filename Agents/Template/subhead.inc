<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<set entity name="subs"><agent-list subs type="&agentType;"></set>
<if><test exact match="&agentType">&agentName;</test>
    <then><set name=agentPath>&agentName;</set></then>
    <else><set name=agentPath>&agentType;/&agentName;</set></else>
</if>
<set entity name="subs"><agent-list subs type="&agentType;"></set>
<table cellspacing=0 cellpadding=0 border=0>
<tr>
  <th width=170>
    <read quiet interform file="insert.if" process>
  <td>
    <table cellspacing=0 cellpadding=0 border=0>
    <tr><th width=170><img src="/Agency/Icons/blank170x1.gif"
	                   width=170 height=1></th>
        <td><br>
    <tr><th align=right>
	    <td> <a href="home.if">Home</a>
    		 <a href="index.if">Index</a>
    		 <a href="help.if">Help</a>
<if><read info interform file="initialize.if"><then>
    <tr><th><td><a href="options.if">Options</a>
	    	<a href="initialize.if">[re-initialize]</a></then>
</if>
    <tr><th><td><a href="/PIA/Agents/&agentPath;/">InterForms</a>
	        <a href="/My/Agents/&agentPath;/">(Customized)</a>
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
