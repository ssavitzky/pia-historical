<!-- Forms actors:				 -*-html-helper-*- -->
<!-- Copyright 1997, 1998 Ricoh Silicon Valley			   -->
<!-- === This file should really be a tagset ===		   -->
<tagset name=Form-agent-actors base=Standard><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=Form-agent-actors>

<!-- be careful of implicit li -- not LOCAL so clobbers existing... -->
<!-- actors for handling group lookup -->
<actor tag=is-in-list dscr="returns TRUE if VALUE is an element of CONTENT">
 <set  name=result local></set>
 <set  name=value local><get element name=value></set>
 <repeat list=&content; entity=isinlistli>
   <if><test match=&value; exact>&isinlistli;</test>
    <then><set  name=result local>&value;</set></then>
   </if></repeat>
  <get name=result local>
 </actor>

<actor tag=get-keys dscr="returns a list (ul) of keys in CONTENT containing
VALUE -- CONTENT should be a dl">
 <set  name=mykeys local><get name=content keys></set>
 <set  name=value local><get element name=value></set>
 <set  name=result local><ul> </ul></set>
 <repeat list=&mykeys; entity=getkeysli>
 <if><is-in-list value=&value;><get name=content key=&getkeysli;></is-in-list>
    <then><set  name=result local insert=-1><li>&getkeysli;</li></set></then>
   </if></repeat>
  <get name=result local>
 </actor>


<actor tag=if-in-group quoted dscr=" do CONTENT only if USER (default TRANS.authenticateduser) is a member of GROUP
">

 <set  name=value local><if><get element name=user><then><get element
 name=user></then><else><get trans name=AuthenticatedUser></else></if></set>

 <set name=reporterrors><get element name=reporterrors></set>
 <set  name=mylist local><get element name=group></set>
 <set  name=myc local><get name=content></set>
 <set local name=grp><get agent name=groups key=&mylist;></set>

  <if><is-in-list value=&value;><get index=grp.li.Text></is-in-list>
	  <then><expand>&myc;</expand></then>
	  <else> <if><get name=reporterrors>
	         <then>
	         &value; not authorized for &mylist;.
	         Authorized members are: <get agent name=groups key=&mylist;>
	         </then></if>
	 </else>
	 </if> 

 </actor>

	 
<!-- this is ugly because we want no spaces in the result -->	 
<actor tag=manager  empty dscr=" return manager of USER"><set  name=value local><if><get element name=user><then><get element name=user></then><else><get trans name=AuthenticatedUser></else></set><set local name=userdat><get agent name=users key=&value;></set><get index=userdat.usermanager.Text></actor>	 

<actor tag=set-manager   dscr=" sets manager of USER">
 <set  name=value local><get element name=user></set>
  <set local name=userdat><get agent name=users key=&value;></set>
 <set replace index=userdat.usermanager><expand>&content;</expand></set>
 </actor>

<actor tag=full-name empty dscr=" return full-name of USER"><set  name=value
local><if><get name=content><then><get name=content></then><else><get trans
name=AuthenticatedUser></else></set><set local name=userdat><get agent
name=users key=&value;></set><if><get index=userdat.username.Text><then><get
index=userdat.username.Text></then><else><get name=value></else></if></actor>	 

	 
<actor tag=group-remove-user empty  dscr=" remove USER from GROUP">
 <set  name=value local><get element name=user></set>
 <set name=group local><get element name=group></set>
 <set name=mylist local><get agent name=groups key=&group;></set>
  <if> <get name="group">
       <then> <set agent name=groups key=&group;><ul foreach list=&mylist;
               entity=gruid><if><test match=&value;>&gruid;</test><then></then><else><li>&gruid;</li></else></if>
	        </ul></set>
	</then>
    </if> 
 </actor>

<actor tag=group-add-user empty  dscr=" add USER to GROUP">
 <set  name=value local><get element name=user></set>
 <set name=group local><get element name=group></set>
 <set name=mylist local><get agent name=groups key=&group;></set>
  <if><get name=mylist><then>
	<set name=mylist insert=-1><li>&value;</li></set>
       </then><else>
	<set agent name=groups key=&group;><ul><li>&value;</li></set>
      </else>
    </if> 
 </actor>	


<actor tag=show-pending empty  dscr="  show pending forms for USER">
 <set  name=value local><get element name=user></set>
 <if><read file="users/&value;.html" info>
 <then>
   <set name=pending><read file="users/&value;.html"  process></set>
	     <set name=pending><b><get name=pending findall=a></b></set>
	    
	<if> <test positive> <get name=pending size></test> <then>
	<h3><a href=edit_pending.if?uid=&value;>Pending forms for &value;</a></h3>
	     <ul foreach list="&pending;" entity=myli>
<set local name=myurl><get name=myli attr=href></set>
	     <set local name=mydesc><get name=myli text></set>
	     <li><a href=sign_form.if?formurl=&myurl;>&mydesc.Text;</a></li></ul>
	</then> <else><h3>No Pending forms for &value;</h3> </else></if>
 </then><else><h3>No Pending forms for &value;</h3> </else></if>
</actor>


 <actor tag=notify-next-recipient empty desc="add FORM to pending list for
 recipient after USER">
<set  name=myuser local><get element name=user></set>
<set  name=myform local><get element name=form></set>
<!--  see if  current route is a user or a group -->

 <set name=myroute local><get element name=route></set>	     
 <if><get name=myroute> <then></then><else>
  <set local name=rfile><subst result=".route.html"
  match=".html"><get name=myform></subst></set>
 <set name=myroute><read file=&rfile; process></set>
 </else>
 </if>

 <set name=next local>NoMoreRoute</set>
 <if><get name=myroute >
     <then>
     <!-- old style route is easier   user is key -->
     <if> <get name=myroute key=&myuser;>
         <then> <set name=toremove>&myuser;</set>
          <set name=next><get name=myroute key=&myuser;></set>
          <if> <test match=NULL>&next;</test> <then>
	       <set name=next>NoMoreRoute</set></then>
          </if>
     </then>
     <else>
          <!-- loop to get at next user on list -->
          <set name=myflag local>start</set>
	   <set name=steps><get name=myroute findall=routestep></set>
          <repeat list=&steps; entity=step>
	    <set name=rentry><get index=step.routeuser.Text></set>
	      <if><test match=next><get name=myflag></test> <then>
	          <if> <get  name=rentry> <then>
	           <set  name=next><get  name=rentry></set>
	          </then>
	          </if>
	      <set name=myflag>done</set>
	     </then>
	     <else>
                 <if><test match=start><get name=myflag></test> <then>
	             <if> <test match=&myuser;><get name=rentry></test><then>
	              <set name=myflag>next</set>
	              <set name=toremove>&myuser;</set>
	             </then>
	             <!--  check for group membership -->
	             <else>
   	                 <if> <test match=&myuser;><get agent name=groups
			   key=&rentry;></test>
	                 <then>
	                  <set name=myflag>next</set>
                          <set name=toremove>&rentry;</set>
	                 </then>
                         </if>
	             </else>
	             </if>
	        </then>
	        </if>
              </else>
	      </if>
	  </repeat>
     </else>
     </if>


     <if> <test match=NoMoreRoute><get name=next></test> <then>
          No more steps for &myform; </then>
	<else>
	  <notify-user form=&myform; user=&next;>
	  Routing &myform;  to &next;
        </else>
     </if>
     <if> <get name=toremove> <then>
          <remove-notify-user user=&toremove; form=&myform;>
     </then>
     <else>  user not identified in route... no pending forms removed</else>
     </if>
 </then>
 <else>
  NO Route for &myform; specified, <form action="panic.if" method=post>
<input name="problem" type=hidden value="no route found for &myform; for &myuser;">
Please hit the <input type="SUBMIT" value="Panic Button"> 
</form>
   </else></if>	     
</actor>

	     
 <actor tag=notify-user empty desc="add FORM to pending list for USER">
<set  name=myuser local><get element name=user></set>
<set  name=myform local><get element name=form></set>
 <if> <test match=printer>&myuser;</test> <then>
  <!-- put up a print dialog -->
 The next step for this <a href="&myform;"> form</a> is to be printed out,
 combined with supporting materials, and submitted to the front desk.  Please
 specify a printer to generate the printout automatically or follow <a
 href="/&agentName;/show_signatures.if?formurl=&myform;"> this link</a> and print the resulting page using your browsers print
 function.
 <form action="/&agentName;/print_form.if" method=post>
  <input type="hidden" name="formurl" value="&myform;">
  <input type="SUBMIT" value="Print using IMP">
  on: <select name="printer">
  <option>ps3
  <option>ps4
  <option>ps5
  <option>ps6
  <option>ps2
  <option>ps1
  <option>psfile
  <option>other
 </select>
(Other printer: <input name="otherprinter" size="20">)
</form>  

  </then>
  <else>
  <if> <get agent name=notified key=&myuser;> <then></then>
       <else><set agent name=notified key=&myuser;>1</set>
       <if><get agent name=groups key=&myuser;> <then>
       <set local name=members><get agent name=groups key=&myuser;></set>
       <set name="tonotify"><get index=members.li-1.Text></set>
       <set local name=mygroup>&user;</set>
        </then>
        <else><set name="tonotify"><get name=myuser></set></else>
       </if>
          <mail-user-notification user=&tonotify;>

  </else></if>
 <write file=users/&myuser;.html append><a href="&myform;"> &myform; </a></write>
</else></if>
</actor>

<actor tag=remove-notify-user empty desc="remove FORM from pending list for USER">
<set  name=myuser local><get element name=user></set>
<set  name=myform local><get element name=form></set>
 <set agent name=notified delete key=&myuser;></set>
 <set local name=mylist><read file=users/&myuser;.html  process></set>
 <write file="users/.&myuser;.html.tmp">&mylist;</write>
<set local name=mynewlist><ul> </ul></set>
 <repeat list=&mylist; entity=myli>
 <if> <test match="&myform;" >&myli;</test> <then> </then>
 <else><set name=mynewlist insert=-1><get name=myli></set></else> </if>
</repeat>
 <write file=users/&myuser;.html><get name=mynewlist></write>
 <write file="users/&myuser;_complete.html" append><a href="&myform;"> &myform; </a></write>
 </actor>	     



<!--  add tag for hash and signing -->

 <actor tag=hash handle></actor>
 <actor tag=sign handle></actor>


<!--  generate route object for this context -->
 <actor tag=process-route desc=" processes the content as a route object for
 this context --  e.g. replaces manager and such with appropriate values">
 <set name=routeobject><route> </route></set>
<set local name=mysteps><get index=content findall=routestep></set>
 <repeat list=&mysteps; entity=myli>
 <!-- replace user or manager variables -->
 <if> <test match=user><get index=myli.routeuser.Text></test>
 <then> <set index=myli.routeuser insert=0 replace><get
	name=myuser></set></then></if>
 <if> <test match=manager><get index=myli.routeuser.Text></test>
 <then> <set index=myli.routeuser insert=0 replace><manager user=&myuser;></set></then></if>

 <!--  add to robject if condition is met or always -->
<if> <test match=onlyif><get index=myli.routewhen.Text></test>
 <then> <set  name=conditions><get index=myli.routecond></set>
 <if><test-conditions  conditions=&conditions;> <then>
 <set index=routeobject insert=-1><get index=myli></set>
        </then></if>
 </then>
 <else>
 <!-- always add -->
 <set index=routeobject insert=-1><get index=myli></set>
 </else>
 </if>
 </repeat>
</actor>

 <actor tag=save-form desc="save the  CONTENT of FORM to a FILE">
 
 <if> <get element name=form>
  <then><set local name=myform><get element name=form></set></then>
  <else><set local name=myform>&dir;</set></else>
 </if>

 <if> <get  element name=file>
  <then><set local name=myfile><get element name=file></set></then>
  <else><set local
  name=myfile>&dir;/&year;/&month;/&day;/&timesec;</set></else>
  </if>
 
  <if> <get  element name=route>
 <then> <set local name=myroute><get element name=route></set></then>
 <else><set local name=myroute>&routeobject;</set></else>
 </if>

  <if> <get  element name=formnum>
 <then> <set local name=formnum><get element name=formnum></set></then>
 <else><set local name=formnum><sum digits=0> 1 <read file=&dir;.dat></sum></set>
 <write file=&dir;.dat>&formnum;</write>
 </else>
 </if>

<write append file="&myfile;.route.html">&myroute;</write>
<write append file="&myfile;.html">
<formhead>
<formtype><get name=myform></formtype>
<formnumber><get name=formnum></formnumber>
<formoriginator><get trans name=AuthenticatedUser></formoriginator>
<formoriginationdate>&dayName; &monthName; &day;, &year;</formoriginationdate>
<formurl>&myfile;.html</formurl>
</formhead>
<body bgcolor="#ffffff">
&content;
</body>
</write>
<!-- add to list of reports -->
<write append line file="&dir;/&dir;_report.html"><li><a href="&myfile;.html">
<get trans name=AuthenticatedUser> &dayName; &monthName; &day;, &year; </a><br></write>
      
<write append line file="&dir;/&year;/&dir;_report.html"><li><a href="&myfile;.html">
<get trans name=AuthenticatedUser> &dayName; &monthName; &day;, &year; </a><br></write>   
      
<write append line file="&dir;/&year;/&month;/&dir;_report.html"><li><a href="&myfile;.html">
<get trans name=AuthenticatedUser> &dayName; &monthName; &day;, &year; </a><br></write> 

</actor>

<actor tag=for-every quoted desc="Repeat CONTENT once for every occurrence
of TAG in OBJECT, ENTITY will be bound to each instance">
<set local name=mytag><get element name=tag></set>
<set local name=myobj><get element name=object></set>
<set local name=mytags><get name=myobj findall=&mytag;></set>
<set local  name=myentity><get element name=entity></set>

 <if> <get name=mytags> <then>
 <if> <test match=mytag exact><get name=mytags attr=tag></test> <then>
   <!-- do once only -->
    <set name=&myentity;><get name=mytags></set>
     <expand>&content;</expand>
   </then>
   <else>
  <!-- repeat --><set local name=foo>&content;</set>
 <repeat list=&mytags; entity=&myentity;>
   <expand>&foo;</expand>
 </repeat>
   </else></if>
 </then></if>
</actor>

<actor tag=formprotect quoted desc=" protect form processing instructions">
<set local name=li><get element name=livalue></set>
<formexpression livalue=&li;><dummy>&content;</dummy></formexpression>
</actor>

<actor tag=test-conditions empty desc="true if all CONDITIONS hold">
 <set name=conditions local><get element name=conditions></set>
     <set name=myvalue local>true</set>
     <repeat list=&conditions; entity=mycond>
 <if><my-test value=&mycond.condval.Text;
 comparison=&mycond.condtest.Text;><get
 name=&mycond.condvar.Text;></my-test> <then></then>
      <else><set name=myvalue> </set></else></if> 
     </repeat>
      <if> <test match=true>&myvalue;</test> <then>&myvalue;</then></if>
</actor>
     
<actor tag=my-test desc="compare VALUE to CONTENT using comparison">
      <set name=val2 local><get element name=value></set>
      <set name=val1 local>&content;</set>
 <if> <test match="equalTo" exact><get element name=comparison></test> <then>
      <test match=&val2;>&val1;</test></then>
      <else>
      <if> <test match="lessThan" exact><get element name=comparison></test>
             <then>
             <test numeric  negative> <difference>&val1;
             &val2;</difference></test>
             </then>
             <else>
             <if> <test match="greaterThan" exact><get element name=comparison></test> <then>
             <test numeric positive> <difference>&val1; &val2;</difference></test>
             </then></if>
             </else>
             </if>
     </else>
     </if>
 </else></if>
 </actor>
     
 <actor tag=mail-user-notification empty desc="Mail standard message to USER">
 <set name="myuser" local><get element name=user></set>
 <os-command> mail &myuser; < &usrDIR;/&agentName;/message.txt </os-command>
 </actor>

<!-- utilities for reports -->
<actor tag=set-previous-month empty desc="sets yctr and mctr as appropriate">
 <set local name=mctr><get element  name=mctr></set>
 <set local name=yctr><get element  name=yctr></set>
 <set name=&mctr;><difference digits=0><get name=&mctr;> 1 </difference></set>
<if> <test positive><get name=&mctr;></test> <then>
  <if><test negative><difference><get name=&mctr;> 10</difference></test>
  <then><set name=&mctr;><text>0<get name=&mctr;></text></set>
</then></if></then>
 <else>
       <get name=&mctr;> tested negative.
       <set name=&mctr;>12</set>
       <set name=&yctr;><difference digits=0><get name=&yctr;> 1</difference></set>
 </else></if>
</actor>

<actor tag=set-next-month empty desc="sets yctr and mctr as appropriate">
 <set local name=mctr><get element  name=mctr></set>
 <set local name=yctr><get element  name=yctr></set>
 <set name=&mctr;><sum digits=0><get name=&mctr;> 1</sum></set>
 <if> <test positive><difference><get name=&mctr;> 12</difference></test>
      <then>
      <set name=&mctr;>1</set>
      <set name=&yctr;><sum digits=0><get name=&yctr;> 1</sum></set>
 </then></if>
  <if><test negative><difference><get name=&mctr;> 10</difference></test>
  <then><set name=&mctr;><text>0<get name=&mctr;></text></set></then></if>

</actor>

<!-- $Id$ -->
