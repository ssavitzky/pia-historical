<!-- data.inc for Template -->
<t-data name=dName type=ident></t-data>
<t-doc name=dName label="Template Name">
	The name of the new template.  Must be a valid directory name. </t-doc>
<t-data name=dRoot type=root>usrDIR</t-data>
<t-doc name=dRoot label="Template Root">
	Set the root directory for the new template.  This is normally
	your PIA user-data directory. </t-doc>
<t-data name=dPath type=path>Template/Masters</t-data>
<t-doc name=dPath label="Template Base">
	Set the path from the root to the parent directory of your new
	template.   Currently <code>&dRoot-path;/&dPath;</code></t-doc> 
<t-data name=dExpand type=expand></t-data>
<t-doc name=dExpand label="Expand Template">
	Click ``Expand'' when you're ready to expand the template.  This will
	create a new or modified template in the specified location.  Click
	``continue'' to verify entries or view debugging information without
	actually doing the expansion.  </t-doc>
<t-data name=dTitle></t-data>
<t-doc name=dTitle label="Template Title">
	The title of the new template.  This will appear as the heading above
	the new template's form. </t-doc>
<t-data name=dDoc type=text></t-data>
<t-doc name=dDoc label="Documentation">
	This text will appear above the new template's form.</t-doc>
<t-data name=dExtend type=extend></t-data>
<t-doc name=dExtend label="Extend">
	Click the Extend button to add a field to the template.</t-doc>
<t-data name=debug type=checkbox format=special>true</t-data>
<t-doc name=debug label="Debug"></t-doc>
<!-- Copyright &copy; 1997 Ricoh Silicon Valley -->
<!-- $Id$ -->

