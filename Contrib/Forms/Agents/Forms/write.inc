<if><test match="-" exact>&FORM.name;</test><then>
<h1> NO Username entered</h1> Please use the BACK button on your browser to
go back to the form and enter your name.</then>
<else>
<write file="&dir;/&year;/&month;/&day;/&timesec;.route.html">&routeobject;</write> 
<set name=path>&dir;/&year;/&month;/&day;/&timesec;</set>
<write append file="&path;.html">
   <body bgcolor="#ffffff">
   <center><a href="http://&piaHOST;:&piaPORT;/&agentName;/forms.if"><img src="http://&piaHOST;:&piaPORT;/&agentName;/formsbutton.gif"></a><a href="http://&piaHOST;:&piaPORT;/&agentName;/users.if"><img src="http://&piaHOST;:&piaPORT;/&agentName;/userbutton.gif"></a><a href="http://&piaHOST;:&piaPORT;/&agentName;/reports.if"><img src="http://&piaHOST;:&piaPORT;/&agentName;/reportsbutton.gif"></a><a href="/Forms/feedback/feedback.if"><img src="http://&piaHOST;:&piaPORT;/&agentName;/feedbackbutton.gif"></a></center><br>
   Please sign the following document.<p>
   <h2>&dir; form</h2><p>
   &dataobject;<p>
   <!--  put a form for each signer -->
   <h3> Signatures</h3>
   Please enter your digital signature (Unix password) in the box provided.
   <set name=signers><get index=routeobject keys></set>
   <repeat list=&signers; entity="signer">
   <!-- hack to prevent repeats -->
   <if><get name="signedby&signer;"><then>
   <else>
        <set name="signedby&signer;">true</set>    
	<form method=POST action="/Forms/sign.if">
	<input type=hidden name=routepath value="&path;.route.html">
        <input type=hidden name=filepath value="&path;.html">
        <input type=hidden name=dir value="&dir;">
        <input type=hidden name=manager value="&manager;">
        <set name=temp>&FORM.name;</set> 
        <b>&signer;:</b> <input type=hidden name="name" value="&signer;"> 
        <input type=password name=signature>
        <input type=submit name=process value=SIGN>
	</form> 
   </else></if>
  </repeat>
  <p>Signed by:
</write>

<write append file="users/&FORM.name;.html">
<li><a href="../&path;.html"> &dir; form --- &FORM.name; &dateString;</a></li></write>


<write append line file="&dir;/&dir;_report.html"><li><a href="http://&piaHOST;:&piaPORT;/My/&agentName;/&path;.html">
&FORM.name; &dateString;</a><br></write>
      
<write append line file="&dir;/&year;/&dir;_report.html"><li><a href="http://&piaHOST;:&piaPORT;/My/&agentName;/&path;.html">
&FORM.name; &dateString;</a><br></write>   
      
<write append line file="&dir;/&year;/&month;/&dir;_report.html"><li><a href="http://&piaHOST;:&piaPORT;/My/&agentName;/&path;.html">
&FORM.name; &dateString;</a><br></write> 

<read file="&path;.html" process>
</else></if>
