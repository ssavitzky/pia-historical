<!-- form.inc for Templates/Template -->

This form will create a new template, with the standard files.

<form action="&url;" method=POST>
  <input type=hidden name=t-name value="&t-name;">
  <input type=hidden name=t-suffix value="&t-suffix;">
  <input type=hidden name=t-dir  value="&t-dir;">

<table>
  <tr><th align=right>New &tName; Name:
      <td><t-input name=dName>

  <tr><th align=right>
      <td><t-check name=dSystem>
	  (sets default directory appropriately)

  <tr><th align=right>Destination directory:
      <td><t-input name=dDir>

  <tr><th> <t-check name=debug>
      <td> <input type=submit value="Expand Template">
</table>
</form>
<hr>
<b>Copyright &copy; 1997 Ricoh Silicon Valley</b><br>
<b>$Id$</b><br>
