<write file="&dir;/&year;/&month;/&timesec;.route.html">&routeobject;</write> 
   
<write append file="&dir;/&year;/&month;/&timesec;.html">
   <body bgcolor="#ffffff">
   <center><a href="http://rhine:8888/Forms/forms.if"><img src="../formsbutton.gif"></a><a href="http://rhine:8888/Forms/users.if"><img src="../userbutton.gif"></a><a href="http://rhine:8888/Forms/reports.if"><img src="../reportsbutton.gif"></a><a href="mailto:dgillen@crc.ricoh.com"><img src="../feedbackbutton.gif"></a></center><br>
   Please sign the following document.<p>
   <h2>&dir; form</h2><p>
   &dataobject;<p>
   <form method=POST action="/Forms/sign.if">
   <input type=hidden name=routepath value="&dir;/&year;/&month;/&timesec;.route.html">
   <input type=hidden name=filepath value="&dir;/&year;/&month;/&timesec;.html">
   <input type=hidden name=dir value="&dir;">
   <input type=hidden name=dataobject value="&dataobject;">
   <input type=hidden name=manager value="&manager;">

   <set name=temp>&FORM.name;</set> 
   Username: <select name="name"><option>&temp;
  <if><test match="NULL"><get name="routeobject.&temp;"></test><then>
  <else><option><get name="routeobject.&temp;">
        <read interform file="recurse.inc" process>
  </else>
  </if></select>
 
   Signature (UNIX password): <input type=password name=signature>
  <input type=submit name=process value=submit><p>Signed by: </form>
</write>

<write append file="users/&FORM.name;.html">
<li><a href="../&dir;/&year;/&month;/&timesec;.html">
&dir; form --- &FORM.name; &dateString;</a><br></write>

<write append line file="&dir;/&dir;_report.html"><li><a href="http://rhine:8888/My/Forms/&dir;/&year;/&month;/&timesec;.html">
&FORM.name; &dateString;</a><br></write>
      
<write append line file="&dir;/&year;/&dir;_report.html"><li><a href="http://rhine:8888/My/Forms/&dir;/&year;/&month;/&timesec;.html">
&FORM.name; &dateString;</a><br></write>   
      
<write append line file="&dir;/&year;/&month;/&dir;_report.html"><li><a href="http://rhine:8888/My/Forms/&dir;/&year;/&month;/&timesec;.html">
&FORM.name; &dateString;</a><br></write> 

<read file="&dir;/&year;/&month;/&timesec;.html" process>
