<!-- Template actors:				 -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley			   -->
<!-- === This file should really be a tagset ===		   -->

<!-- Actors used in template data files. ========================= -->
<actor tag=t-data quoted dscr="set value, FORMAT, and TYPE strings for NAME.
    Value is set from form if present, else from CONTENT.">
  <set name=name><get element name=name></set>
  <set name="&name;-type"><get element name=type></set>
  <if><get element name=format>
      <then><set name="&name;-format"><get element name=format></set></if>
  <set name="&name;"><protect-result><get name=content></protect-result></set>
  <set name=tFieldNames><get name=tFieldNames> &name;</set>
</actor>

<actor tag=t-doc quoted dscr="set documentation and LABEL strings for NAME">
  <set name=name><get element name=name></set>
  <set name="&name;-label"><get element name=label></set>
  <set name="&name;-doc">&content;</set>
</actor>

<!-- Form elements constructed from entities set in data file. -->
<actor tag=t-input empty dscr="Input tag with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <input name="&name;" value="&value;" size=50>
</actor>

<actor tag=t-text empty dscr="Text area with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <textarea name="&name;" rows=5 cols=50>&value;</textarea>
</actor>

<actor tag=t-check empty dscr="Checkbox with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;">
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <if>&value;
      <then><input type=checkbox name="&name;" checked>
      <else><input type=checkbox name="&name;"></if>
  <get name="&name;-label">
  <input type=hidden name="&name;-d" value=true>
</actor>

<actor tag=t-submit empty dscr="Submit button with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <input type=submit name="&name;-d" value=&value;>
</actor>

<actor tag=t-hidden empty dscr="Hidden input with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <input name="&name;" value="&value;" type=hidden>
</actor>

<actor tag=t-select empty dscr="Select with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <set local name=values><get name="&name;-vlist"></set>
  <select name="&name;">
    <repeat list="&vlist;"><option>&li;</option>
    </repeat></select>
</actor>

<actor tag=t-alt-select empty dscr="Select with default from template data;
    alternative from a text input item.">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;-alt">
		      <then><get form name=&name;-alt"></then>
		  <else><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></if></set>
  <set name="&name;">&value;</set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <select name="&name;">
    <repeat list="&vlist;"><option>&li;</option>
    </repeat></select> <br>
  <input name="&name;-alt" size=50>
</actor>

<!-- special format:  extending a template. -->
<actor tag=t-extend empty dscr="Add a new item to template data">
  <set name=name><get element name=name></set>
  <if><get form name="&name;-n">
      <then><set name=new><get form name="&name;-n"></set>
	    <set name=tFieldNames>&tFieldNames; &new;</set>
	    <set name="&new;-type"><get form name="&name;-t"></set>
	    <set name="&new;-label"><get form name="&name;-l"></set>
	    <set name="&new;-doc"><get form name="&name;-d"></set>
	    <set name="&new;"><get form name="&name;-v"></set>
      </then>
<!-- unimplemented -->		 
  </if>

  <set name="&name;"></set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <b>Name:</b> <input name="&name;-n"> Must be a single word<br>
  <b>Label:</b> <input name="&name;-l">
  <b>Type:</b> <select name="&name;-t">
	<option>input</option>
	<option>checkbox</option>
	<option>textarea</option>
	<option>hidden</option>
	<option>data</option>
  </select>
  <strong><a href="&agentPath/help.if?topic=extend">[help]</a></strong>
  <br>
  <input name="&name;-v" size=50 value="default value"> <br>
  <textarea name="&name;-d" rows=5 cols=50> documentation
  </textarea>
</actor>

<!-- special format:  expanding the template. -->
<actor tag=t-expand empty dscr="Extra stuff associated with an Expand button">
  <set name=name><get element name=name></set>
  <set name=tFormFields><get name=tFormFields> &name;</set>
  <if><get name=debug-label><then><t-check name=debug>
  <input type=hidden name=tDir  value="&tDir;">
</actor>

<!-- Actors for constructing form data fields and labels  -->

<actor tag=t-field empty dscr="generate a field data entry">
  <set name=name><get element name=name></set>
  <set name=type><get name="&name;-type"></set>
  <if><test match=hidden>&type;</test>
      <then><t-hidden name="&name;"></then>
  <else><if><test match=checkbox>&type;</test>
      <then><t-check name="&name;"></then>
  <else><if><test match=textarea>&type;</test>
      <then><t-text name="&name;"></then>
  <else><if><test match=select>&type;</test>
      <then><t-select name="&name;"></then>
  <else><if><test match=alt-select>&type;</test>
      <then><t-alt-select name="&name;"></then>
  <else><if><test match=extend>&type;</test>
      <then><t-extend name="&name;"></then>
  <else><if><test match=submit>&type;</test>
      <then><t-submit name="&name;"></then>
  <else><if><test match=expand>&type;</test>
      <then><t-expand name="&name;"></then>
  <else><t-input name="&name;"></else>
  </if></if></if></if></if></if></if></if>
</actor>

<actor tag=t-labeled empty dscr="return true if TYPE requires no label">
  <set name=type><get element name=type></set>
  <test match=hidden>&type;</test>
  <test match=check>&type;</test>
  <test match=submit>&type;</test>
</actor>

<!-- this returns the actual label.  Extend uses a button. -->
<actor tag=t-label empty dscr="return NAME-label if needed">
  <set name=name><get element name=name></set>
  <set name=type><get name="&name;-type"></set>
  <set name=label><if><get name="&name;-label">
	<then><get name="&name;-label"><else>&name;</set>
  <if><test match=extend>&type;</test>
      <then><input type=submit name="&name;"  value="&label;"></then>
  <else><if><test match=expand>&type;</test>
      <then><input type=submit name="&name;" value="&label;"></then>
  <else><if><t-labeled type="&type;"><else>&label;</if></if></if>
</actor>

<!-- Actors that format template forms.  Suitable for value of "tFormat"  -->

<actor tag=t-tabular-form empty dscr="construct a tabular form">
  <form method=POST target="&tFormURL;">
    <table>
      <get name=tFirstItems>
      <repeat list="&tFieldNames;">
	<if><test match=hidden><get name="&li;-type"></test>
	  <then><t-hidden name="&li;">
	  <else><if><test match=data><get name="&li;-type"></test>
		    <get name="&li;-format">
	  <then></then>
	  <else><tr><th valign=top align=right><t-label name="&li;"></th>
		    <td><t-field name="&li;"> <br><get name="&li;-doc"></td>
	</if></if>
      </repeat>
      <get name=tLastItems>
    </table>
  </form>
</actor>

<actor tag=t-list-form empty dscr="construct a description-list form">
  <form method=POST target="&tFormURL;">
    <dl>
      <get name=tFirstItems>
      <repeat list="&tFieldNames;">
	<if><test match=hidden><get name="&li;-type"></test>
	  <then><t-hidden name="&li;">
	  <else><if><test match=data><get name="&li;-type"></test>
		    <get name="&li;-format">
	  <then></then>
	  <else><dt><t-label name="&li;"></dt>
		<dd><t-field name="&li;"> <br> <get name="&li;-doc"></dd>
	</if>
      </repeat>
      <get name=tLastItems>
    </dl>
  </form>
</actor>

<!-- The following actors are used in the template's form file -->

<actor tag=t-form-setup empty dscr="Set up local entities in form.">
<set name=tName></set>         <!-- if we don't know the template's name... -->
<set name=tForm>Selection</set><!-- ...we obviously will want to select one. -->
<set entity name=title>&tName; &tForm;</set>
<if><get form name=tName>
    <then>
	<set name=tName><get form name=tName></set>
	<set name=tForm><get form name=tForm></set>
	<if>&tForm;<else><set name=tForm></set></if>

	<!-- set base directory. -->
	<if><test match=user><get form name=tBase></test>
    	    <then><set name=t-base><read info=p file="Masters"></set>
        <else><if><test match=system><get form name=tBase></test>
    	    <then><set name=t-base><read info=p interform file="Masters"></set>
	<else><if><get form name=tBase>
    	    <then><set name=t-base><get form name=tBase></set>
    	<else><set name=t-base><read info=p interform file="Masters"></set>
	</if></if></if>

    	<set name=tDir>&t-base;/&tName;</set>

	<!-- template-wide constants are in template.inc -->
	<set name=tTemplateFilePath>&tDir;/template.inc</set>
	<set name=foo><read process quiet file="&tTemplateFilePath;"> </set>

	<if><get name=tDataFile><else><set name=tDataFile>data.inc</if>
	<set name=t-data-path>&tDir;/&tDataFile</set>
    
	<!-- now the destination directory and destination form data -->
	<if><test match=user><get form name=dBase></test>
    	    <then><set name=d-base><get name=tUserBase></set>
        <else><if><test match=system><get form name=tBase></test>
    	    <then><set name=d-base><get name=tSystemBase></set>
	<else><if><get form name=dBase>
    	    <then><set name=d-base><get form name=dBase></set>
    	<else><set name=d-base></set>
	</if></if></if>

	<if><get form name=dDirPath>
	    <then><set name=dDirPath><get form name=dDirPath></set>
		  <set name=d-data-path>&dDirPath;/&tDataFile;</set>
		  <set name=d-data-present><read info=p file=d-data-path></set>
	    <else><set name=dDirPath></set>
		  <set name=d-data-path></set>
		  <set name=d-data-present></set>
	</if>

	<!-- Now the form-specific data -->
	<set name=t-form-path><if><get name=tFormFile>
	    <then><read info=p file="&tDir/&tFormFile;"></then>
	    <else>form.inc</else>
	</set>
	<if><get name=tTitle>
	    <then><set entity name=title>for &tTitle;</set>
	    <else><set entity name=title>for &tName; &tForm;</set>
	</if>

	<!-- if a data file is present in the dest. directory, use it. -->
	<set name=data-file><if>&d-data-present;
				<then>&d-data-path;</then>
				<else>&t-data-path;</else></if></set>

    <else>
	<set name=tFormFile></set>
	<set name=t-data-file></set>
	<set name=tTemplateFilePath></set>
</if>
</actor>

<actor tag=t-data-file empty dscr="construct a data.inc file">
    <set local name=lt><</set><set local name=gt>></set>
&lt;!-- template data for &tName; --&gt;
<repeat list="&tFieldNames;">
&lt;t-data name=&li; type=<get name="&li;-type"><if><get name="&li;-format">
    <then> format=<get name="&li;-format"></if>&gt;<protect-result><get name="&li;"></protect-result>&lt;/t-data&gt;
&lt;t-doc name=&li; label="<get name="&li;-label">"&gt;<get name="&li;-doc">
&lt;/t-doc&gt;
</repeat>
&lt;!-- Made by &piaUSER; on &date; --&gt;
</actor>

<actor tag=t-data-dump empty dscr="dump all the data">
<dl>
<repeat list="&tFieldNames;">
  <dt> &li; = `<protect-result><get name="&li;"></protect-result>'
  <dd> type=<get name="&li;-type"><br>
       <if><get name="&li;-format"><then>format=<get name="&li;-format"><br></if>
       label="<get name="&li;-label">"<br>
       doc=<get name="&li;-doc"><br>
</repeat>
</dl>
</actor>

<!-- $Id$ -->
