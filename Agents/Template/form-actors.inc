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
		      <then><get form name="&name;">
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFields><get name=tFields> &name;</set>
  <input name="&name;" value="&value;" size=50>
</actor>

<actor tag=t-text empty dscr="Text area with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;">
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

<actor tag=t-hidden empty dscr="Hidden input with default from template data">
  <set name=name><get element name=name></set>
  <set name=value><if><get form name="&name;"><get form name="&name;-d">
		      <then><get form name="&name;">
		      <else><get name="&name;"></else>
		  </if></set>
  <set name="&name;">&value;</set>
  <set name=tFields><get name=tFields> &name;</set>
  <input name="&name;" value="&value;" type=hidden>
</actor>

<!-- The following actors generate the appropriate kind of data item
     for their type.
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
  <else><t-input name="&name;">
  </if></if></if>
</actor>

<actor tag=t-labeled empty dscr="return true if TYPE requires no label">
  <set name=type><get element name=type></set>
  <test match=hidden>&type;</test>
  <test match=checkbox>&type;</test>
  <test match=submit>&type;</test>
</actor>

<actor tag=t-label empty dscr="return NAME-label if needed">
  <set name=name><get element name=name></set>
  <set name=type><get name="&name;-type"></set>
  <if><t-labeled type="&type;"><then><get name="&name;-label"></if>
</actor>

<!-- The following actors are used to construct template forms.
     A suitable invocation should be the value of "t-format"
  -->
<actor tag=t-tabular-form empty dscr="construct a tabular form">
  <form method=POST target="&tFormURL;">
    <table>
      <repeat list="&tFieldNames;">
	<if><test match=hidden><get name="&li;-type"></test>
	  <then><t-hidden name="&li;">
	  <else><tr><th valign=top align=right><t-label name="&li;"></th>
		    <td><t-field name="&li;"> <get name="&li;-doc"></td>
	</if>
      </repeat>
    </table>
  </form>
</actor>

<actor tag=t-list-form empty dscr="construct a description-list form">
  <form method=POST target="&tFormURL;">
    <dl>
      <repeat list="&tFieldNames;">
	<if><test match=hidden><get name="&li;-type"></test>
	  <then><t-hidden name="&li;">
	  <else><dt><t-label name="&li;"></dt>
		<dd><t-field name="&li;"> <br> <get name="&li;-doc"></dd>
	</if>
      </repeat>
    </dl>
  </form>
</actor>

<!-- The following actors are used in template data files.
     The field names are tracked in tFieldNames.
  -->

<actor tag=t-doc dscr="set documentation and LABEL strings for NAME">
  <set name=name><get element name=name></set>
  <set name="&name;-label"><get element name=label></set>
  <set name="&name;-doc">&content;</set>
</actor>

<actor tag=t-set dscr="set value and TYPE strings for NAME">
  <set name=name><get element name=name></set>
  <set name="&name;-type"><get element name=type></set>
  <set name="&name;">&content;</set>
  <set name=tFieldNames><get name=tFieldNames> &name;</set>
</actor>

<actor tag=t-data-file empty dscr="construct a data.inc file">
<h4>data for &tName;</h4>
    <set local name=lt><</set><set local name=gt>></set>
<repeat list="&tFieldNames;">
&lt;t-set name=&li; type=<get name="&li;-type">&gt;&li;&lt;/t-set&gt;
&lt;t-doc name=&li; label="<get name="&li;-label">"&gt;<get name="&li;-doc">
&lt;/t-doc&gt;
</repeat>
<h4>Made by &piaUSER; on &date;</h4>    
</actor>

<!-- $Id$ -->
