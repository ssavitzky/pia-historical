<!-- Template actors:				 -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley			   -->
<!-- === This file should really be a tagset ===		   -->

<!-- definitions for empty tags must preceed their use.  -->
<actor tag=t-early-processing empty dscr="perform any special processing
    needed for NAME inside t-data">
  <if><test match=extend>&type;</test>
      <then>
	<set name=tExtensions><get form name=tExtensions></set>
  	<if><get form name="&name;-n">
            <then><set local name=new><get form name="&name;-n"></set>
              <set name=tExtensions><get name=tExtensions> &new;</set>
	      <set name="&new;-type"><get form name="&name;-t"></set>
	      <set name="&new;-label"><get form name="&name;-l"></set>
	      <set name="&new;-doc"><get form name="&name;-d"></set>
	      <set name="&new;"><get form name="&name;-v"></set>
	</if>
        <set name=tFieldNames>&tFieldNames; &tExtensions;</set>
  <else><if><test match=root>&type;</test>
      <then>
  	<set local name=r><get name="&name;"></set>
  	<set name="&name;-path"><if><get name="&r;">
				    <then><get name="&r;"></then>
			            <else>&r;</else></if></set>
	<!-- === The following is bogus and needs to be fixed === -->
  	<set name="&name;-DOFS"><if>
  	    <test match="usrDIR">&r;</test><then>/My</then>
  	    <else><if><test match="piaDIR">&r;</test><then>/PIA</then>
  	    <else><if><agent-running name=Root><then>/Root&r;</then>
  	    <else></else></if></if></if></set>
  <else><if><test match=file>&type;</test>
      <then>
  	<set local name=fn><get name="&name;-label"></set>
  	<set name="t-file-names"><get name="t-file-names"> &fn;</set>
  </if></if></if>
</actor>

<!-- Initializing entities: ====================================== -->
<set name=tFieldNames></set>
<set name=t-file-names></set>
<set name=t-required-fields></set>

<!-- Actors used in template data files. ========================= -->
<actor tag=t-data quoted dscr="set value and TYPE strings for NAME.
    Optionally LABEL, REQUIRED.
    Value is set from form if present, else from CONTENT.">
  <set name=name><get element name=name></set>
  <set name=type><get element name=type></set>
  <set name="&name;-type">&type;</set>
  <if><get element name=required>
      <then><set name="&name;-required"><get element name=required></set>
	    <set name=t-required-fields>&t-required-fields; &name;</set>
  </if>
  <set name="&name;"><protect-result><get name=content></protect-result></set>
  <if><protect-result><get form name="&name;"></protect-result>
      <get form name="&name;-x">
    <then><set name="&name;"><protect-result><get form name="&name;"></set>
  </if>
  <if><get element name=label>
      <then><set name="&name;-label"><get element name=label></set></if>
  <t-early-processing>
  <set name=tFieldNames><get name=tFieldNames> &name;</set>
</actor>

<actor tag=t-form quoted dscr="set documentation and LABEL strings for NAME.
	Optionally add HELP link.  Optionally NOBREAK.">
  <if><get element name=name>
      <then><set name=name><get element name=name></set></if>
  <if><get element name=label>
      <then><set name="&name;-label"><get element name=label></set></if>
  <set name="&name;-help"><get element name=help></set>
  <if><get element name=cluster>
      <then><set name="&name;-cluster"><get element name=cluster></set></if>
  <if><get element name=nobreak>
      <then><set name="&name;-nobreak">1</set></if>
  <set name="&name;-doc">&content;</set>
  <if><test match=data><get name="&name;-type">
      <then><if><expand>&content;</expand></if></if>
</actor>

<!-- Special hacks for constructing form elements ===================== -->
<actor tag=t-radio-btn empty dscr="template radio button element">
  <set local name=nn><get element name=name></set>
  <set local name=vv><get element name=value></set>
  <set local name=tt><get element name=test></set>
  <if><test exact match="&tt;">&vv;</test>
      <then><input type=radio name="&nn;" value="&vv;" checked></then>
      <else><input type=radio name="&nn;" value="&vv;"></else>
  </if>
</actor>

<!-- Form elements ==================================================== -->
<actor tag=t-input empty dscr="Input tag with default from template data">
  <set local name=name><get element name=name></set>
  <set local name=value><protect-result><get name="&name;"></set>
  <input name="&name;" value="&value;" size=50>
</actor>

<actor tag=t-ident empty dscr="Input an identifier.">
  <set local name=name><get element name=name></set>
  <set local name=value><protect-result><get name="&name;"></set>
  <input name="&name;" value="&value;" size=20>
  <i><a href="&agentPath;/help.if?topic=id">[id]</a></i>
</actor>

<actor tag=t-void empty dscr="no input: just put documentation into the form.">
  <set local name=name><get element name=name></set>
  <get name=&name;>
</actor>

<actor tag=t-text empty dscr="Text area with default from template data">
  <set local name=name><get element name=name></set>
  <set local name=value><protect-result><get name="&name;"
   			                ></protect-result></set>
  <textarea name="&name;" rows=5 cols=50>&value;</textarea>
</actor>

<actor tag=t-check empty dscr="Checkbox with default from template data">
  <set local name=name><get element name=name></set>
  <set local name=value><protect-result><get name="&name;"></set>
  <if>&value;
      <then><input type=checkbox name="&name;" checked>
      <else><input type=checkbox name="&name;"></if>
  <get name="&name;-label">
  <input type=hidden name="&name;-x" value=true>
</actor>

<actor tag=t-file-select empty dscr="Checkbox to select a file.">
  <set local name=name><get element name=name></set>
  <set local name=value><get name="&name;"></set>
  <if>&value;
      <then><input type=checkbox name="&name;" checked>
      <else><input type=checkbox name="&name;"></if>
  <code><get name="&name;-label"></code>
  <set name="&name;-nobreak">1</set>
  <input type=hidden name="&name;-x" value=true>
</actor>

<actor tag=t-submit empty dscr="Submit button with default from template data">
  <set local name=name><get element name=name></set>
  <set local name=value><protect-result><get name="&name;"></set>
  <input type=submit name="&name;-d" value=&value;>
</actor>

<actor tag=t-hidden empty dscr="Hidden input with default from template data">
  <set local name=name><get element name=name></set>
  <set local name=value><protect-result><get name="&name;"></set>
  <input name="&name;" value="&value;" type=hidden>
</actor>

<actor tag=t-hidden-binding empty dscr="Hide a binding for NAME">
  <set local name=name><get element name=name></set>
  <if><protect-result><get name="&name;">
      <set local name=value><protect-result markup><get name="&name;"></set>
      <input name="&name;" value="&value;" type=hidden>
  </if>
</actor>

<actor tag=t-select empty dscr="Select with default from template data">
  <set local name=name><get element name=name></set>
  <set local name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set local name=values><get name="&name;-vlist"></set>
  <select name="&name;">
    <repeat list="&vlist;"><option>&li;</option>
    </repeat></select>
</actor>

<actor tag=t-alt-select empty dscr="Select with default from template data;
    alternative from a text input item.">
  <set local name=name><get element name=name></set>
  <set local name=value><if><get form name="&name;-alt">
		      <then><get form name=&name;-alt"></then>
		  <else><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></if></set>
  <set name="&name;">&value;</set>
  <select name="&name;">
    <repeat list="&vlist;"><option>&li;</option>
    </repeat></select> <br>
  <input name="&name;-alt" size=50>
</actor>

<!-- special format:  extending a template. -->
<actor tag=t-extend empty dscr="Add a new item to template data">
  <set local name=name><get element name=name></set>
  <set name="&name;"></set>
  <b>Name:</b> <input name="&name;-n" size=20>
  <i><a href="&agentPath/help.if?topic=id">[id]</a></i>
  <b>Type:</b> <select name="&name;-t">
	<option>input</option>
	<option>checkbox</option>
	<option>textarea</option>
	<option>hidden</option>
	<option>data</option>
	<option>void</option>
  </select>
  <strong><a href="&agentPath/help.if?topic=extend">[help]</a></strong>
  <br>
  <b>Label:</b> <input name="&name;-l" size=40> <br>
  <input name="&name;-v" size=50 value="default value"> <br>
  <textarea name="&name;-d" rows=5 cols=50> documentation
  </textarea>
  <if><get name=tExtensions>
      <then><!-- put all data for extensions into the form. -->
	<input type=hidden name=tExtensions value="&tExtensions;">
	<repeat list="&tExtensions;">
	  <t-hidden-binding name="&li;">
	  <t-hidden-binding name="&li;-type">
	  <t-hidden-binding name="&li;-label">
	  <t-hidden-binding name="&li;-doc">
	</repeat>
  </if>
</actor>

<!-- special format:  expanding the template. -->
<actor tag=t-expand empty dscr="Extra stuff associated with an Expand button">
  <set local name=name><get element name=name></set>
  <input type=submit name="expand-c" value="Continue">
  <input type=hidden name=tDir  value="&tDir;">
</actor>

<!-- special format:  Base directory path from root.  Name should be dPath -->
<actor tag=t-path empty dscr="Standard base directory path setup.">
  <set local name=name><get element name=name></set>
  <set local name=value><if><get form name="&name;">
			    <then><get form name="&name;"></then>
		 	    <else><get name="&name;"></else>
			</if></set>
  <set name="&name;">&value;</set>
  <input name="&name;" size=30 value="&value;"> 
  <i><a href="&agentPath;/help.if?topic=path">[path]</a></i>
</actor>

<!-- special format:  Root directory.  Name should be dRoot -->
<actor tag=t-root empty dscr="Standard root directory setup.">
  <set local name=name><get element name=name></set>
  <set local name=r><get name="&name;"></set>
  <t-radio-btn type=radio name="&name;" value="usrDIR" test="&r;"> User
  <t-radio-btn type=radio name="&name;" value="piaDIR" test="&r;"> System
  <t-radio-btn type=radio name="&name;" value="/" test="&r;"> /
  <i><a href="&agentPath;/help.if?topic=root">[help]</a></i>
</actor>

<!-- Actors for constructing form data fields and labels  -->

<actor tag=t-field empty dscr="generate a field data entry">
  <set local name=name><get element name=name></set>
  <set local name=type><get name="&name;-type"></set>
  <if><test exact match=hidden>&type;</test>
      <then><t-hidden name="&name;"></then>
  <else><if><test match=check>&type;</test>
      <then><t-check name="&name;"></then>
  <else><if><test match=text>&type;</test>
      <then><t-text name="&name;"></then>
  <else><if><test match=ident>&type;</test>
      <then><t-ident name="&name;"></then>
  <else><if><test exact match=select>&type;</test>
      <then><t-select name="&name;"></then>
  <else><if><test exact match=alt-select>&type;</test>
      <then><t-alt-select name="&name;"></then>
  <else><if><test exact match=extend>&type;</test>
      <then><t-extend name="&name;"></then>
  <else><if><test exact match=void>&type;</test>
      <then><t-void name="&name;"></then>
  <else><if><test exact match=submit>&type;</test>
      <then><t-submit name="&name;"></then>
  <else><if><test exact match=expand>&type;</test>
      <then><t-expand name="&name;"></then>
  <else><if><test exact match=path>&type;</test>
      <then><t-path name="&name;"></then>
  <else><if><test exact match=file>&type;</test>
      <then><t-file-select name="&name;"></then>
  <else><if><test exact match=root>&type;</test>
      <then><t-root name="&name;"></then>
  <else><t-input name="&name;"></else>
  </if></if></if></if></if></if></if></if></if>
</actor>

<actor tag=t-unlabeled empty dscr="return true if TYPE requires no label">
  <set local name=type><get element name=type></set>
  <test exact match=hidden>&type;</test>
  <test match=check>&type;</test>
  <test exact match=submit>&type;</test>
  <test match=data>&type;</test>
  <test match=file>&type;</test>
</actor>

<!-- this returns the actual label.  Extend uses a button. -->
<actor tag=t-label empty dscr="return NAME-label if needed">
  <set local name=name><get element name=name></set>
  <set local name=type><get name="&name;-type"></set>
  <set local name=label><get name="&name;-label"></set>
  <if><test exact match=extend>&type;</test>
      <test exact match=expand>&type;</test>
      <then><a name="&name;"><input type=submit name="&type;"
				    value="&label;"></a></then>
  <else><if><test match=void>&type;</test>
      <then><a name="&name;"><em>&label;</em></a>
  <else><if><t-unlabeled type="&type;"><test not>&label;</test>
  <else><a name="&name;">&label;</a></if></if></if>
</actor>

<actor tag=t-field-link empty dscr="generate a link to a field data entry">
  <set local name=name><get element name=name></set>
  <set local name=type><get name="&name;-type"></set>
  <set local name=label><get name="&name;-label"></set>
  <if><t-unlabeled type="&type;"> <test not>&label;</test>
      <else> <a href="#&name;">&label;</a> </if>
</actor>

<actor tag=t-break empty dscr="Generate help link and optional break">
  <set local name=name><get element name=name></set>
  <get name="&name;-help">
  <if><get name="&name;-doc">
      <then><if><get name="&name;-nobreak"><else><br></if></if>
</actor>

<!-- Actors that format template forms.  Suitable for value of "tFormat"  -->

<actor tag=t-tabular-form empty dscr="construct a tabular form">
  <form method=POST target="&tFormURL;">
    <table>
      <tr><th valign=top><em>field index:</em>
	  <td> <repeat list="&tFieldNames;"><t-field-link name="&li;"></repeat>
      <tr><th><hr>
      <get name=tFirstItems>
      <repeat list="&tFieldNames;">
	<if><test match=hidden><get name="&li;-type"></test><then>
		<if><expand><get name="&li;-doc"></expand></if>
		<t-hidden name="&li;">
	  <else><if><test match=data><get name="&li;-type"></test>
		    <get name="&li;-format"><then>
	  <else><if><get name="&li;-cluster"><then>
		<set name=cluster-fields><get name=cluster-fields>
		    <if><get name=cluster-label>
			<then>&nbsp;<b><t-label name="&li;"></b></if>
		    <t-field name="&li;"> <t-break name="&li;"></set>
		<if><get name=cluster-label><else>
		    <set name=cluster-label><t-label name="&li;"></set></if>
		<set name=cluster-docs><get name=cluster-docs>
		    <expand><get name="&li;-doc"></expand></set>
	  <else><tr><th valign=top align=right>
			<if><get name=cluster-label>
			    <then><get name=cluster-label>
			    <else><t-label name="&li;">
		    </th>
		    <td><get name=cluster-fields>
			<if><get name=cluster-label>
			    <then>&nbsp;<b><t-label name="&li;"></b></if>
			<t-field name="&li;"> <t-break name="&li;">
			<get name=cluster-docs>
			<expand><get name="&li;-doc"></expand>
			<set name=cluster-label></set>
			<set name=cluster-fields></set>
			<set name=cluster-docs></set>
		    </td>
	</if></if>
      </repeat>
      <get name=tLastItems>
    </table>
  </form>
</actor>

<actor tag=t-list-form empty dscr="construct a description-list form">
  <form method=POST target="&tFormURL;">
    <dl>
      <dt>field index:
      <dd> <repeat list="&tFieldNames;"><t-field-link name="&li;"></repeat>
      <get name=tFirstItems>
      <repeat list="&tFieldNames;">
	<if><test match=hidden><get name="&li;-type"></test><then>
		<t-hidden name="&li;">
	  <else><if><test match=data><get name="&li;-type"></test>
		    <get name="&li;-format">
	  <then></then>
	  <else><if><get name="&li;-cluster"><then>
		<t-field name="&li;"> <t-break name="&li;">
		<expand><get name="&li;-doc"></expand></td>
	  <else><dt><t-label name="&li;"></dt>
		<dd><t-field name="&li;">  <t-break name="&li;">
		    <expand><get name="&li;-doc"></expand></dd>
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
	<set name=t-data-path>&tDir;/&tDataFile;</set>
    
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
	    <else></else>
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

<actor tag=t-missing-fields empty dscr="Return a list of names of fields
	that are required to have values, but do not.  Also returned in
	&amp;t-missing-fields;.">
  <set name=t-missing-fields></set>
  <repeat list="&t-required-fields">
	<if><get name="&li;">
	    <else><set name=t-missing-fields>&t-missing-fields; &li;</set></if>
  </repeat>
  &t-missing-fields;
</actor>

<actor tag=t-data-dump empty dscr="dump all the data">
<dl><repeat list="&tFieldNames;">
  <dt> &li; = ``<protect-result markup><get name="&li;"></protect-result>''
  <dd> type=<get name="&li;-type"><br>
       <if><get name="&li;-format">
           <then>format=<get name="&li;-format"><br></if>
       <if><get name="&li;-nobreak">
           <then>nobreak=<get name="&li;-nobreak"><br></if>
       label=``<get name="&li;-label">''<br>
       doc=``<get name="&li;-doc">''
</repeat></dl>
</actor>

<!-- Reconstruct the data file.  Very contorted to preserve layout. -->
<actor tag=t-data-file empty dscr="construct a data.inc file"
><set local name=lt><</set><set local name=gt>></set
>&lt;!-- From &tBase;/&tPath;/&tName; --&gt;
&lt;!--   in &dBase;/&dPath;/&dName; --&gt;
&lt;!-- AUTOMATICALLY REBUILT WHEN TEMPLATE EXPANDED!  EDIT WITH CARE! --&gt;
<repeat list="&tFieldNames;">
&lt;t-data name=&li; type=<get name="&li;-type"><if><get name="&li;-format">
    <then> format=<get name="&li;-format"></then>
</if><if><get name="&li;-label">
    <then> label=<get name="&li;-label"></then>
</if>&gt;<protect-result><get name="&li;"></protect-result>&lt;/t-data&gt;
<if><get name="&li;-doc">
    <then>&lt;t-doc&gt;<protect-result><get name="&li;-doc"></protect-result
	 >&lt;/t-doc&gt;
</repeat>
&lt;!-- Made by &piaUSER; on &year;/&month;/&day; at &time; --&gt;
</actor>

<!-- $Id$ -->
