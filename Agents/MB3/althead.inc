<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<!--  Load the tagset -->
<tagset-load name=MB3-actors>

<set entity name="subs"><agent-list subs type="&agentType;"></set>
<if><test exact match="&agentType">&agentName;</test>
    <then><set name=agentPath>&agentName;</set></then>
    <else><set name=agentPath>&agentType;/&agentName;</set></else>
</if>
<set entity name="subs"><agent-list subs type="&agentType;"></set>
<table cellspacing=0 cellpadding=0 border=0>

<tr>
  <td width=150><!-- may have to be edited if icons are used. -->
    <read quiet interform file="insert.if" process>
  <td>
    <table cellspacing=0 cellpadding=0 border=0>
    <tr><th width=170><img src="/Agency/Icons/blank170x1.gif"
	                   width=170 height=1></th>
        <td><br>
    <tr><td><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*">
 	    <if><test match=index.if>&fileName;</test>
		    <then><b href="index.if">Index</b>
		    <else><a href="index.if">Index</a></if>
	<td><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*">
    	    <if><test match=help.if>&fileName;</test>
		    <then><b href="help.if">Help</b>
		    <else><a href="help.if">Help</a></if>
<if><read info interform file="initialize.if"><then>
    <tr><td><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*">
 	    <a href="options.if">Options</a>
	<td><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*">
	    <a href="initialize.if">[re-initialize]</a></then>
</if>
    <tr><td><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*">
	    <a href="suggestions.if"> Suggestions Form</a>
	<td><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*">
	    <a href="doc.html">Documentation</a>
    </table>
</table>
<!-- $Id$ -->
