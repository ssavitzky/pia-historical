<!-- This heading is meant to be included using read. -*-html-helper-*- -->
<!-- Copyright 1998 Ricoh Silicon Valley -->

<!-- Demo mode set user -->
<set trans name=AuthenticatedUser>demo</set>


<table cellspacing=0 cellpadding=0 border=0>
<tr nowrap nobr><th align=left width=170 valign=bottom nowrap><a
	href="http://rsv.ricoh.com/"><img src="/Agency/Icons/ricoh.gif"
	border=0 width=170 height=48 alt="R I C O H"></a></th>
    <th width=170 nowrap align=right></th>:
    <th align=left nowrap nobr valign=center width=170>
      <if><get entity name=title>
	<then><get entity name=title></then>
	<else>&fileName;</else></if>   </th></tr>
<tr height=2><td colspan=3  nowrap nobr
    ><img src="/Agency/Icons/rule.gif" height=6 width=469></td></tr>
<!--img src="/Agency/Icons/blackline425.gif" height=2 width=425-->
<tr nowrap nobr><th align=left valign=top><a href="http://rsv.ricoh.com/"><img
	src="/Agency/Icons/ricoh-silicon-valley.gif" alt="RICOH SILICON VALLEY"
	border=0 width=170 height=21></a></th>
    <th align=right valign=top width=170><a
	href="/&agentName;/home.if">&agentName; home</a>: </th>
    <th align=left colspan=2><a href="/&agentName;/logon.if"><b><get trans name=AuthenticatedUser></a></th></tr>
</table>
<!-- $Id$ -->
