<!-- Expand Template:				 -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley			   -->

<if><get name="t-expanding"><then><h4>Starting Expansion</h4></if>

<set name=srcDir>&tDir;/proto</set>
<set name=dstDir>&dDir;</set>

<table cellpadding=0 cellspacing=0 border=0>

<tr><th align=right> Data file:
    <td> <a href="file:&data-file;"><get name=data-file></a>
<tr><th align=right> Template dir:
    <td> <a href="file:&tDir;/"><get name=tDir>/</a>
<tr><th align=right> Source dir:
    <td> <a href="file:&srcDir;/"><get name=srcDir>/</a>
<tr><th align=right> Target dir:
    <td> <a href="file:&dstDir;/"><get name=dstDir>/</a>
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
</table>

<actor tag=t-report-file empty dscr="Report on FILE">
  <set local name=file><get element name=file></set>
  <code><b><if>
	<test match=data.inc>&file;</test><then>data</then>
	<else><if><get name="x-&file;"><then>xpnd</then>
	<else><if><get name="c-&file;"><then>copy</then>
	<else><if><get name="m-&file;"><then>make</then>
	<else>skip</else>
  </if></if></if></if>:</b></code>
  <a href="file:&srcDir;/&file;">[src]</a> <b>-&gt;</b>
  <a href="file:&dstDir;/&file;">[dst]</a>
  <code>&file;</code>
</actor>

<actor tag=t-write-backup empty dscr="Write backup for FILE">
  <if><get name=t-expanding><then>
      <if><read info=p file="&dstDir;/&file;">
          <then>
		<file copy file="&dstDir;/&file;" dst="&dstDir;/&file;.bak>
		<a href="&dstDir;/&file;.bak">[backup] written.</a>  </if></if>
</actor>

<actor tag=t-handle-file empty dscr="Handle FILE">
  <set local name=file><get element name=file></set>
  <if> <test match=data.inc>&file;</test><then> template data
	  <if><get name=t-expanding><then><br><t-write-backup file="&file;">
	      <write file="&dstDir;/&file;"
		><protect-result><t-data-file></protect-result></write>
	  <else> ... to be </if> rebuilt
	  </then>
	<else><if><get name="x-&file;"><then>
	  <if><get name=t-expanding><then><br><t-write-backup file="&file;">
	      <file copy subst="&FORM;" file="&srcDir;/&file;"
		    dst="&dstDir;/&file;">
	  <else> ... to be </if> expanded
	  </then>
	<else><if><get name="c-&file;"><then>
	  <if><get name=t-expanding><then><br><t-write-backup file="&file;">
	      <file copy file="&srcDir;/&file;" dst="&dstDir;/&file;">
	  <else> ... to be </if> copied
	  </then>
	<else><if><get name="m-&file;"><then>
	  <if><get name=t-expanding><then><br><t-write-backup file="&file;">
		<em>unimplemented, so can't be </em>
	  <else> ... to be </if> made
	  </then>
	<else>
	  <if><get name=t-expanding><then><br>
	  <else> ... to be </if> skipped
  </if></if></if></if> 
</actor>

<ol>
  <li> <t-report-file file=data.inc>
       <t-handle-file file=data.inc>
  <repeat list="&t-file-names; ">
  <li> <t-report-file file="&li;">
       <t-handle-file file="&li;">
  </repeat>
</ol>

<if><get name="t-expanding"><then><h4>Expansion Complete</h4></if>

<!-- $Id$ -->
