<!-- data.inc for Template -->
<t-data name=dName type=ident label="Template Name"></t-data>
<t-form>The name of the new template.  Must be a valid directory name. </t-form>

<t-data name=dRoot type=root label="Template Root">usrDIR</t-data>
<t-form>Set the root directory for the new template.  This is normally
	your PIA user-data directory. </t-form>

<t-data name=dPath type=path label="Template Base">Template/Masters</t-data>
<t-form>Set the path from the root to the parent directory of your new
	template.   Currently <code>&dRoot-path;/&dPath;</code></t-form> 

<t-data name=dExpand type=expand label="Expand Template"></t-data>
<t-form>Click ``Expand'' when you're ready to expand the template.  This will
	create a new or modified template in the specified location.  Click
	``continue'' to verify entries or view debugging information without
	actually doing the expansion.  </t-form>

<t-data name=dTitle label="Template Title"></t-data>
<t-form>The title of the new template.  This will appear as the heading above
	the new template's form. </t-form>

<t-data name=dDoc type=text label="Documentation"></t-data>
<t-form>This text will appear above the new template's form.</t-form>

<t-data name=dExtend type=extend label="Extend"></t-data>
<t-form>Click the Extend button to add a field to the template.</t-form>

<t-data name=debug type=checkbox label="Debug" format=special></t-data>

<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<!-- $Id$ -->

