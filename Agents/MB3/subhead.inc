<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<!--  Load the tagset -->
<tagset-load name=MB3-actors>

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
		    <test match=help.if>&fileName;</test>
		    <then><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*"></if>
	    <td><if><test match=home.if>&fileName;</test>
		    <then><b href="home.if">Home</b>
		    <else><a href="home.if">Home</a></if>
    		<if><test match=help.if>&fileName;</test>
		    <then><b href="help.if">Help</b>
		    <else><a href="help.if">Help</a></if>

    <tr><th><td><a href="options.if">Configuration</a>

    <if> <get name=calendarURL agent> <then>
    <tr><th><td><a href="&AGENT.calendarURL;">Group calendar</a>
    </then> </if>     

    <tr><th align=right>
		<if><test match=view_day.if>&fileName;</test>
		    <then><img src="/Agency/Icons/dot-blue.gif"
			     height=20 width=20 alt="*"></if>
	    <td><a href="view_day.if">View Day</a>
    <tr><th><td><a href="doc.html">Documentation</a>
    <if><get name=subs><then>
    <tr><th align=right>
           
        <td><repeat list="&subs;">
               <get name=li> <b> / </b>
           </repeat></then></if>
    </table>
</table>
<!-- $Id$ -->
