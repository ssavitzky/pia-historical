<write file="&dir;/&year;/&month;/&day;/&timesec;.route.html">&routeobject;</write> 
<set name=path>&dir;/&year;/&month;/&day;/&timesec;</set>
<write append file="&path;.html">
   <body bgcolor="#ffffff">
   <center><a href="http://&piaHOST;:&piaPORT;/&agentName;/forms.if"><img src="http://&piaHOST;:&piaPORT;/&agentName;/formsbutton.gif"></a><a href="http://&piaHOST;:&piaPORT;/&agentName;/users.if"><img src="http://&piaHOST;:&piaPORT;/&agentName;/userbutton.gif"></a><a href="http://&piaHOST;:&piaPORT;/&agentName;/reports.if"><img src="http://&piaHOST;:&piaPORT;/&agentName;/reportsbutton.gif"></a><a href="/Forms/feedback/feedback.if"><img src="http://&piaHOST;:&piaPORT;/&agentName;/feedbackbutton.gif"></a></center><br>
   Please sign the following document.<p>
   <h2>&dir; form</h2><p>
   &dataobject;<p>
   <form method=POST action="/Forms/sign.if">
   <input type=hidden name=routepath value="&path;.route.html">
   <input type=hidden name=filepath value="&path;.html">
   <input type=hidden name=dir value="&dir;">
   <input type=hidden name=manager value="&manager;">
   <set name=temp>&FORM.name;</set> 
   Username: <select name="name"><option>&temp;
  <if><test match="NULL"><get index="routeobject.&temp;"></test><then>
  <else><option><get index="routeobject.&temp;">
        <read interform file="recurse.inc" process>
  </else>
  </if></select>
 
   Signature (UNIX password): <input type=password name=signature>
  <input type=submit name=process value=submit><p>Signed by: </form>
</write>

<write append file="users/&FORM.name;.html">
<li><a href="../&path;.html">
&dir; form --- &FORM.name; &dateString;</a><br></write>

<write append line file="&dir;/&dir;_report.html"><li><a href="http://&piaHOST;:&piaPORT;/My/&agentName;/&path;.html">
&FORM.name; &dateString;</a><br></write>
      
<write append line file="&dir;/&year;/&dir;_report.html"><li><a href="http://&piaHOST;:&piaPORT;/My/&agentName;/&path;.html">
&FORM.name; &dateString;</a><br></write>   
      
<write append line file="&dir;/&year;/&month;/&dir;_report.html"><li><a href="http://&piaHOST;:&piaPORT;/My/&agentName;/&path;.html">
&FORM.name; &dateString;</a><br></write> 

<read file="&path;.html" process>
