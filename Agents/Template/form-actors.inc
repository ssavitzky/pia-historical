<!-- Template form actors:			 -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Silicon Valley			   -->

<!-- These actors are used to define form content.  They get their default
     values from a previously-submitted form or the corresponding data.inc
     file.  Along the way they construct the entities required to make the
     new data.inc file.  The names are collected in tFields.
  -->

<actor tag=t-input empty dscr="Input tag with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFields><get name=tFields> &name;</set>
  <input name="&name;" value="&value;" size=50>
</actor>

<actor tag=t-text empty dscr="Text area with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFields><get name=tFields> &name;</set>
  <textarea name="&name;" rows=5 cols=50>&value;</textarea>
</actor>

<actor tag=t-check empty dscr="Checkbox with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;">
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFields><get name=tFields> &name;</set>
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
  <set name=tFields><get name=tFields> &name;</set>
  <input type=submit name="&name;-d" value=&value;>
</actor>

<actor tag=t-hidden empty dscr="Hidden input with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFields><get name=tFields> &name;</set>
  <input name="&name;" value="&value;" type=hidden>
</actor>

<actor tag=t-select empty dscr="Select with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;"></then>
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFields><get name=tFields> &name;</set>
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
  <set name=tFields><get name=tFields> &name;</set>
  <select name="&name;">
    <repeat list="&vlist;"><option>&li;</option>
    </repeat></select> <br>
  <input name="&name;-alt" size=50>
</actor>

<actor tag=t-extend empty dscr="Add a new item to template data">
  <set name=name><get element name=name></set>
  <if><get form name="&name;">
	<then><get form name="&name;"></then>
<!-- unimplemented -->		 
  </if>

  <set name="&name;">&value;</set>
  <set name=tFields><get name=tFields> &name;</set>
  <b>Name:</b> <input name="&name;-n">
  <b>Type:</b> <input name="&name;-t"><!-- should be select -->
  <b>Label:</b> <input name="&name;-l"> <br>
  <input name="&name;-v" size=50 value="default value"> <br>
  <textarea name="&name;-d" rows=5 cols=50> documentation
  </textarea>

</actor>

<!-- The following actors generate the appropriate kind of data item
     and label for their type.
  -->

<actor tag=t-item empty dscr="generate a field data entry">
  <set name=name><get element name=name></set>
  <set name=type><get name="&name;-type"></set>
  <if><test match=hidden>&type;</test>
      <then><t-hidden name="&name;">
  <else><if><test match=checkbox>&type;</test>
      <then><t-check name="&name;">
  <else><if><test match=textarea>&type;</test>
      <then><t-text name="&name;">
  <else><if><test match=select>&type;</test>
      <then><t-select name="&name;">
  <else><if><test match=alt-select>&type;</test>
      <then><t-alt-select name="&name;">
  <else><if><test match=extend>&type;</test>
      <then><t-extend name="&name;">
  <else><if><test match=submit>&type;</test>
      <then><t-submit name="&name;">
  <else><t-input name="&name;">
  </if></if></if>
</actor>

<actor tag=t-labeled empty dscr="return true if TYPE requires no label">
  <set name=type><get element name=type></set>
  <test match=hidden>&type;</test>
  <test match=checkbox>&type;</test>
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
  <else><if><t-labeled type="&type;"><then>&label;</if></if>
</actor>

<!-- The following actors are used to construct template forms.
     A suitable invocation should be the value of "t-format"
  -->
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
		    <td><t-field name="&li;"> <get name="&li;-doc"></td>
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

<!-- The following actors are used in template data files.
     The field names are tracked in tFieldNames.
  -->

<actor tag=t-data dscr="set value, FORMAT, and TYPE strings for NAME.
    Value is set from form if present, else from CONTENT.">
  <set name=name><get element name=name></set>
  <set name="&name;-type"><get element name=type></set>
  <if><get element name=format>
      <then><set name="&name;-format"><get element name=format></set></if>
  <set name="&name;">&content;</set>
  <set name=tFieldNames><get name=tFieldNames> &name;</set>
</actor>

<actor tag=t-doc dscr="set documentation and LABEL strings for NAME">
  <set name=name><get element name=name></set>
  <set name="&name;-label"><get element name=label></set>
  <set name="&name;-doc">&content;</set>
</actor>

<actor tag=t-data-file empty dscr="construct a data.inc file">
<h4>data for &tName;</h4>
    <set local name=lt><</set><set local name=gt>></set>
<repeat list="&tFieldNames;">
&lt;t-data name=&li; type=<get name="&li;-type"><if><get name="&li;-format">
    <then>format=<get name="&li;-format"></if>&gt;&li;&lt;/t-data&gt;
&lt;t-doc name=&li; label="<get name="&li;-label">"&gt;<get name="&li;-doc">
&lt;/t-doc&gt;
</repeat>
<h5>Made by &piaUSER; on &date;</h5>    
</actor>

<!-- $Id$ -->
