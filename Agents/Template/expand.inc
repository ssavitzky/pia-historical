<!-- Expand Template:				 -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley			   -->

<if><get name="t-expanding"><then><h4>Starting Expansion</h4></if>

<set name=srcDir>&tDir;/proto</set>
<set name=dstDir>&dDir;</set>
<set name=srcDOFS>&tDOFS;/proto</set>
<set name=dstDOFS>&dDOFS;</set>

<table cellpadding=0 cellspacing=0 border=0>

<tr><th align=right> Data file:
    <td> <a href="file:&data-file;"><get name=data-file></a>
<tr><th align=right> Template 
    <td> <a href="file:&tDOFS;/">[dofs]</a>
	 <a href="file:&tDir;/">[dir]</a> <get name=tDir>/
<tr><th align=right>
    <td> <b>base:</b> <get name=tBase>
	 <b>name:</b> <get name=tName>
<tr><th align=right> Source
    <td> <a href="file:&srcDOFS;/">[dofs]</a>
	 <a href="file:&srcDir;/">[dir]</a> <get name=srcDir>/
<if>&dstDir;<then>
<tr><th align=right> Target
    <td> <a href="file:&dstDOFS;/">[dofs]</a>
	 <a href="file:&dstDir;/">[dir]</a> <get name=dstDir>/
	 <if><read info=p file="&dstDir;">
	 <else><if><get name=t-expanding>
		   <then><b>did not exist:</b>
			 <write directory file="&dstDir;"> </write>
			 <b>created.</b>
		   <else><b>does not exist</b>
	 </if></if>
<tr><th align=right>
    <td> <b>root:</b> <get name=dRoot> (<get name=dRoot-path>)
	 <b>path:</b> <get name=dPath>
	 <b>name:</b> <get name=dName>
</if>
</table>


<ol>
  <repeat list="data.inc &t-file-names; ">
  <li> <t-report-file file="&li;">
       <t-handle-file file="&li;">
  </repeat>
</ol>

<if><get name="t-expanding"><then><h4>Expansion Complete</h4></if>

<!-- $Id$ -->
