
<if><test match="timecard">&FORM.dir;</test><then>
    <if><test exact match="FrontOffice">&FORM.name;</test><then>
    <write append line file="timecard_reports/&FORM.manager;_timecards.html">&FORM.dataobject;<hr><hr></write>
    <else></else></if><else></else></if>
