<!-- Forms actors:				 -*-html-helper-*- -->
<!-- Copyright 1997, 1998 Ricoh Silicon Valley			   -->
<!-- === Tagset for the Forms agent	 ===		   -->
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


<!-- tags for saving and loading databases -- users, groups,
departments,... -->
<!-- by convention, databases are dl's with descriptive objects (eg. "user")
as dd value -->
<actor tag=load-data empty desc="load data into agent from file DATA.dat">
<set local name=dataname><get element name=data></set>
<if><read file=&dataname;.dat info> <then>
    <set agent name=&dataname;><read file=&dataname;.dat process></set>
    </then> <else><!-- initialize data as dl -->
    <set agent name=&dataname;><dl> </dl></set>
    </else></if>
</actor>

<actor tag=save-data empty desc="save DATA from agent into file DATA.dat">
<set local name=dataname><get element name=data></set>
<if><read file=&dataname;.lck info> <then> <h2> &dataname; file is LOCKED
</h2>Please  report this error</then> <else>
<write file=&dataname;.lck>Lock file for &dataname; on &date;</write>
<file name=&dataname;.dat dst=&dataname;.bak rename></file>
<write file=&dataname;.dat><get agent name=&dataname;></write>
<file file=&dataname;.lck remove></file>
</else> </if>
</actor>

<!-- tags for dealing with departments -->	 
 <!-- NOTE these operations do not automatically save results to a file  -->
<actor tag=dept-add-member empty desc="Adds MEMBER to DEPARTMENT"> 
 <set local name=dept><get element name=department></set>
 <set local name=mem><get element name=member></set>
 <set local name=d><get agent name=departments key=&dept;></set>
 <set local name=dmems><get index=d.deptmembers></set>
 <set index=dmems insert=-1><deptmember><get name=mem></deptmember></set>
</actor>

<actor tag=dept-remove-member empty desc="removes MEMBER from DEPARTMENT"> 
 <set local name=dept><get element name=department></set>
 <set local name=mem><get element name=member></set>
 <set local name=d><get agent name=departments key=&dept;></set>
 <set local name=newd><department> </department></set>
  <repeat list=&d.-; entity=myel> <if><test match=deptmembers><get name=myel attr=tag></test> <then></then> <else><set name=newd insert=-1><get name=myel></else> </if> </repeat>
 <set name=newm><deptmembers> </deptmembers></set>
 <repeat list=&d.deptmembers.-; entity=myel><if><test match=&mem; exact><get index=myel.Text></test> <then></then> <else><set name=newm insert=-1>&myel;</else> </if> </repeat>
 <set name=newd insert=-1><get name=newm></else>
 <set agent name=departments key=&dept;><get name=newd></set>
</actor>

<actor tag=dept-set-manager empty desc="sets MANAGER for DEPARTMENT"> 
 <set local name=dept><get element name=department></set>
 <set local name=mem><get element name=manager></set>
 <set local name=d><get agent name=departments key=&dept;></set>
 <set index=d.deptmanager insert=0 replace><get name=mem></set>
</actor>

<actor tag=dept-set-secondmanager empty desc="sets 2nd level MANAGER for DEPARTMENT"> 
 <set local name=dept><get element name=department></set>
 <set local name=mem><get element name=manager></set>
 <set local name=d><get agent name=departments key=&dept;></set>
 <set index=d.deptsecondmanager insert=0 replace><get name=mem></set>
</actor>

<actor tag=dept-set-admin empty desc="sets ADMINistrator for DEPARTMENT"> 
 <set local name=dept><get element name=department></set>
 <set local name=mem><get element name=admin></set>
 <set local name=d><get agent name=departments key=&dept;></set>
 <set index=d.deptadmin insert=0 replace><get name=mem></set>
</actor>


<actor tag=dept-set-description desc="sets  Content as description for DEPARTMENT"> 
 <set local name=dept><get element name=department></set>
 <set local name=description>&content;</set>
 <set local name=d><get agent name=departments key=&dept;></set>
 <set index=d.deptdesc insert=0 replace><get name=description></set>
</actor>




<!-- this is ugly because we want no spaces in the result -->	 
<actor tag=dept-get-manager empty desc="gets MANAGER for DEPARTMENT"><set local name=dept><get element name=department></set><set local name=mem><get element name=member></set><set local name=d><get agent name=departments key=&dept;></set><get index=d.deptmanager.Text></actor>

<actor tag=dept-get-secondmanager empty desc="gets 2nd level MANAGER for DEPARTMENT"><set local name=dept><get element name=department></set><set local name=mem><get element name=member></set><set local name=d><get agent name=departments key=&dept;></set><get index=d.deptsecondmanager.Text></actor>

<actor tag=dept-get-admin empty desc="gets Administrator for DEPARTMENT"><set local name=dept><get element name=department></set><set local name=d><get agent name=departments key=&dept;></set><get index=d.deptadmin.Text></actor>

<actor tag=dept-get-description empty desc="gets Administrator for DEPARTMENT"><set local name=dept><get element name=department></set><set local name=d><get agent name=departments key=&dept;></set><get index=d.deptdesc.Text></actor>


<actor tag=dept-get-members empty desc="gets members for DEPARTMENT"><set local name=dept><get element name=department></set><set local name=d><get agent name=departments key=&dept;></set><set local name=mem><get index=d.deptmembers.deptmember></set>
 <repeat list=&mem; entity=m>
      <set name=mid><get index=m.Text></set>
      <if><get name=mid><else><set name=mid><get index=m></set> </else> </if>
       <a href=edit_user.if?euser=&mid;><get name=mid></a><br>
      </repeat>
</actor>
	 
<!-- this is ugly because we want no spaces in the result -->	 
<actor tag=manager  empty dscr="return manager of USER"><set  name=value
local><if><get element name=user><then><get element
name=user></then><else><get trans name=AuthenticatedUser></else></set><set name=myman local><user-info  user=&value; attribute=Manager></set><get index=myman.Text></actor>	 

<actor tag=user-dept  empty dscr="return department number of USER"><set  name=value local><if><get element name=user><then><get element name=user></then><else><get trans name=AuthenticatedUser></else></set><user-info  user=&value; attribute=Department></actor>	 


<actor tag=user-info  empty desc=" get the current binding for ATTRIBUTE for
  USER"><set local name=myuser><get name=user element></set><set local
  name=myrole><get name=attribute element></set><set local name=replacements><get agent name=cachedroutes key=&myuser;></set><get name=replacements key=&myrole;></actor>


<actor tag=set-manager   dscr=" sets manager of USER">
 <set  name=value local><get element name=user></set>
  <set local name=userdat><get agent name=users key=&value;></set>
 <set replace index=userdat.usermanager><expand>&content;</expand></set>
 </actor>

<actor tag=full-name empty dscr=" return full-name of USER"><set  name=value
local><if><get element name=user><then><get element name=user></then><else><get trans
name=AuthenticatedUser></else></set><set local name=userdat><get agent
name=users key=&value;></set><if><get index=userdat.username.Text><then><get
index=userdat.username.Text></then><else><get name=value></else></if></actor>	 

	 
<actor tag=group-remove-user empty  dscr=" remove USER from GROUP">
 <set  name=value local><get element name=user></set>
 <set name=group local><get element name=group></set>
 <set name=mylist local><get agent name=groups key=&group;></set>
  <if> <get name="group">
       <then> <set agent name=groups key=&group;><ul foreach list=&mylist;
               entity=gruid><if><test match=&value; exact><get index=gruid.Text></test><then></then><else>&gruid;</else></if>
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

<actor tag="add-user-to-authlist" description="adds USER to list of users for
AUTHLIST" empty>
<set name="newuser" local><get name=user element></set>
<set name="myalist" local><get name=authlist element></set>
<if><get name=myalist attr=tag>
 <else><set name="myalist"><find-tag-with-id tag=authlist idattr=&myalist;
 agentname=authlist></find-tag-with-id></set> </else></if>
<set local name=myusers><get index=myalist.users></set>
     <if> <get name=myusers> <then>
     <set name=mycount>0</set>
    <repeat list="&myusers;" entity=myu>
   <if><get name=myu attr=tag><then>
              </then>
       <else>
      <set index=myalist.users insert=&mycount; replace><user><get
       name=myu></user></set>
       </else></if>
       <set name=mycount><sum digits=0> 1 &mycount;</sum></set>
    </repeat>
    </then>
     <else> <set name=alist insert=-1><users></users></set></else></if>
    <repeat list="&newuser;" entity=myu>
       <set index=myalist.users insert=-1><user><get name=myu></user></set>
    </repeat>
</actor>

 
<actor tag=show-pending empty  dscr="  show pending forms for USER">
 <set  name=value local><get element name=user></set>
 <show-inbox inbox=&value;>
   <set name=boxes><get name=inboxes key=&value; agent></set>
   <if><get name=boxes>
     <then>
       <repeat list=&boxes; entity=mbox>
	  <if> <read file="users/&mbox;.&value;.html" info>
	    <then><show-inbox inbox=&mbox;.&value; signas=&mbox;></then>
	    <else><show-inbox inbox=&mbox; signas=&mbox;></else>
	  </if>
       </repeat></then>
     <else></else> </if>
</actor>

<actor tag=show-inbox empty  dscr="  show inbox of forms for USER">
  <set  name=myin local><get element name=inbox></set>
  <set  name=mysignas local><get element name=signas></set>
   <if><get  name=mysignas>
     <then><set name=mysignas>&signas=<get name=mysignas></set></then>
       <else><set name=mysignas> </set> </else>
   </if>
   <if><read file="users/&myin;.html" info>
     <then>
       <set name=pending><read file="users/&myin;.html"  process></set>
       <set name=pending><b><get name=pending findall=a></b></set>
	    
       <if> <test positive> <get name=pending size></test> <then>
	 <h3><a href=edit_pending.if?uid=&myin;>Pending forms for &myin;</a></h3>
	 <ul foreach list="&pending;" entity=myli>
	   <set local name=myurl><get name=myli attr=href></set>
	   <set local name=mydesc><get name=myli text></set>
	   <li><a href=sign_form.if?formurl=&myurl;&mysignas;>&mydesc.Text;</a></li></ul>
	 </then>
	 <else><h3>No Pending forms for &myin;</h3> </else>
       </if>
     </then>
     <else><h3>No Pending forms for &myin;</h3> </else>
   </if>
</actor>



 <!-- main routing function -- needs get parent and get sibling-->


<actor tag=notify-next-recipient empty desc="add FORM to pending list for
  recipient after USER">
  <set  name=myuser local><get element name=user></set>
  <set  name=myform local><get element name=form></set>
  <set  name=mysignas local><get element name=signedas></set>
  <if><get name=mysignas> <then> <set local name=lastsigner><get name=mysignas></set></then> <else><set local name=lastsigner><get name=myuser></set></else> </if>
  <!-- load the route  -->

  <set name=myoriginalsigner><get name=myuser></set>
  <set name=myroute local><get element name=route></set>	     
  <if><get name=myroute>
    <else>
    <set local name=rfile><subst result=".route.html"
    match=".html"><get name=myform></subst></set>
    <set name=myroute><read file=&rfile; process></set>
    </else>
  </if>

  <set name=next local>NoMoreRoute</set>
  <if><get name=myroute >
    <then>
      <!-- loop to get at next user on list -->
      <set name=steps><dummy><get index=myroute.routestep></dummy></set>
      <repeat list=&steps; entity=step>
	<set name=rauthorizor><get index=step.routeuser.1></set>
	<set name=rentry><authorizor-canonical-name authorizor=&rauthorizor;></set>
	<if><test match=next><get name=myflag></test>
	  <then>
	    <if><test match=notify><get index=step.routewhen.Text></test>
	      <then>
		<notify-user form=&myform; user=&rauthorizor;>
	      </then>
	      <else>
		<set  name=next><get  name=rauthorizor></set>
		<set name=myflag>found</set>
		</else> </if>
	  </then>
	  <else>
	    <if> <test match=&lastsigner; exact><get name=rentry></test>
	      <then>
		<set name=myflag>next</set>
		<set name=next>NoMoreRoute</set>
	      </then>
	    </if>
	  </else>
	</if>
      </repeat>
    
      <if> <get name=lastsigner>
	<then>
	  <remove-notify-user user=&myoriginalsigner; signedas=&mysignas; form=&myform;>
	</then>
	<else>  PROBLEM: no user identified ... no pending forms removed</else>
      </if>

      <if> <get name=next>
	<then>
	  <if> <test match=NoMoreRoute><get name=next></test>
	    <then>
	      No more steps for &myform; 
	      <append-form-history form=&myform;><b>Completed</b></append-form-history>
	      <write file=&myform;.completed>&date;</write>
	    </then>
	    <else>
	      <notify-user form=&myform; user=&next;>
	      Routing &myform;  to <authorizor-canonical-name authorizor=&next;>
	    </else>
	  </if>
	</then>
	<else> 
	  <append-form-history form=&myform;>Routing error -- blank name</append-form-history>
	</else>
      </if>
    </then>
    <else>
      NO Route for &myform; specified, <form action="panic.if" method=post>
      <input name="problem" type=hidden value="no route found for &myform; for &myuser; signedas &signedas;">
      Please hit the <input type="SUBMIT" value="Panic Button"> 
      </form>
    </else>
  </if>	     
</actor>


 <actor tag=notify-originator empty desc=" change status of FORM for originator">
<set  name=myform local><get element name=form></set>
<set  name=mysigner local><get element name=signer></set>
<set  name=myaction local><get element name=action></set>
<set  name=myroute local><get element name=route></set>
 <set name=myoriginator><get name=originator></set>
 <if><get name=myoriginator> <else>
  <set name=parsedform><read file=&myform; process></set>
 <set name=myoriginator><get name=parsedform
 findall=formoriginator></set>
 <set name=myoriginator>&myoriginator.Text;</set></else> </if>

 <if><test match=create> <get name=myaction> </test> <then>
<set name=mysigners><dummy><get name=myroute findall=routeuser></dummy></set>
<write file=users/&myoriginator;.status append>
<formstatus form=&myform; originator=&myoriginator; date=&date;>
 <repeat list=&mysigners; entity=mys><formsigner user=&mys;>Pending</formsigner></repeat>
</formstatus>
</write>
 </then><else>
 <set local name=myflag></set>
 <set name=mystatus><read file=users/&myoriginator;.status process></set>
  <repeat list=&mystatus; entity=fstat>
  <if><test match=&myform;><get name=fstat attr=form></test>
   <then>
    <repeat list=&fstat; entity=mys>
    <if><test match=&mysigner;><get name=mys attr=user></test><then>
      <set name=mys insert=0 replace><get name=&myaction;></set>
       <set name=myflag>done</set></then></if>
   </repeat>
   <if><get name=myflag><else><set name=fstat insert=-1><formsigner
   user=&mysigner;><get name=&myaction;></formsigner></set></else></if>
   <file name=users/&myoriginator;.status dst=users/&myoriginator;.status.bak
   rename></file>
   <write file=users/&myoriginator;.status><get name=mystatus></write>
   </then>
  </if></repeat>
  <!-- should check for missing forms -->
 </else> </if>

</actor>

<actor tag=show-status empty desc=" showe the status of forms originated by this
 user">
<set local name=myoriginator><get element name=user></set>
<if><get element name=user><else><set local name=myoriginator><get trans
name=AuthenticatedUser></set></else></if>
 <if> <read file=users/&myoriginator;.status info> <then>
   <set name=myflag local> </set>
   <set name=mynewlist><ul> </ul> </set>
   <set name=mystatus><dummy><read file=users/&myoriginator;.status
   process></dummy></set>
   <if> <get index=mystatus.formstatus><then>
   <h3><a href=edit_status.if> Status of forms that you originated</a></h3>
  
   <table > 
   <repeat list=&mystatus; entity=fstat>
    <set name=myf><get name=fstat attr=form></set>
     <if> <get name=myf>
       <then>
	 <if><read info file=&myf;.completed>
	   <then> <set name=myflag >1</set></then>
	   <else><set name=mynewlist insert=-1><get name=fstat></set></else>
	 </if>
	 <tr >
	 <td ><a href=show_signatures.if?formurl=&myf;><get
	 name=myf></a></td >
    	 <td><show-form-history form=&myf;> </td>
	 </tr>
	 
       </then>
     </if>
 </repeat>
 </table >

 <if><get name=myflag>
   <then>
     <write file=users/&myoriginator;.status><get index=mynewlist.-1->
     </write>
   </then>
 </if>
</then> <else></else> </if>
</then>
<else></else> </if>

</actor>


 <actor tag=notify-user empty desc="add FORM to pending list for USER">
<set  name=myuser local><get element name=user></set>
<set  name=nextsigner local><get element name=user></set>
<set  name=myform local><get element name=form></set>
 <if> <test match=printer>&myuser;</test> <then>
  <!-- put up a print dialog -->
 The next step for this <a href="&myform;"> form</a> is to be printed out,
 combined with supporting materials, and submitted to the front desk.  Please
 specify a printer to generate the printout automatically or follow <a
 href="/&agentName;/show_signatures.if?formurl=&myform;"> this link</a> and print the resulting page using your browser's print
 function.
 <form action="/&agentName;/print_form.if" method=post>
  <input type="hidden" name="formurl" value="&myform;">
  <input type="SUBMIT" value="Print">
  on: <select name="printer">
  <option>ps3
  <option>ps4
  <option>ps5
  <option>ps6
  <option>ps2
  <option>ps1
  <option>psfile
     <option>DownloadPostscript
 </select>
(Other printer: <input name="otherprinter" size="20">)
</form>  

  </then>
  <else>
    <!-- handle authorizors  -->
    <if><get name=myuser attr=tag>
       <set name=nuser local><get index=myuser.1></set>
      <then>
	<if> <get name=&myuser; attr=group>
	  <then> <set name=inbox><get name=myuser attr=type></set>
	    <set name=nuser><subst match=" .*$" result=""><get name=nuser> </subst></set>
	  </then>
	  <else><set name=inbox><get name=myuser attr=type>.<get name=nuser></set></else>
	</if>
      </then>
      <else><set name=inbox><get name=myuser></set>
	<set name=nuser><get name=myuser></set>
      </else>
    </if>
    <if> <get agent name=notified key=&nuser;> <then></then>
       <else>
	 <set agent name=notified key=&nuser;>1</set>
	 <mail-user-notification user=&nuser;>
       </else>
     </if>
     <add-inbox-entry inbox=&inbox; form=&myform;>
   </else>
 </if>
</actor>



<actor tag=remove-notify-user empty empty desc="remove FORM from pending list for USER">
  <set  name=myuser local><get element name=user></set>
  <set  name=mysignas local><get element name=signedas></set>
  <set  name=myform local><get element name=form></set>
  <set agent name=notified delete key=&myuser;></set>
  <if> <get name=mysignas>
   <then>
     <set agent name=notified delete key=&mysignas;></set>
      <remove-inbox-entry inbox=&mysignas;.&myuser; form=&myform;>
      <remove-inbox-entry inbox=&mysignas; form=&myform;>
   </then>
 </if>
 <remove-inbox-entry inbox=&myuser; form=&myform;>

 </actor>	     

 
 <actor tag=add-inbox-entry  empty desc="adds FORM to INBOX">
   <set name=myf local><get name=form element></set>
   <set name=minefile local><get name=inbox element></set>
   <write file=users/&minefile;.html append><a href="&myf;"> &myf; </a></write>
   <append-form-history form=&myf;>Pending for <get name=minefile></append-form-history>
 </actor>
 
 <actor tag=remove-inbox-entry empty desc=" removes FORM from specified INBOX">
   <set name=myf local><get name=form element></set>
   <set name=minefile local><get name=inbox element></set>
   <set local name=flag>0</set>
   
   <if> <read file=users/&minefile;.html info>
     <then>
       <set name=mylist><read file=users/&minefile;.html process></set>
       <write file="users/.&minefile;.tmp">&mylist;</write>
       <set local name=oldpend><dummy><get name=mylist findall=a></dummy></set>
       <set local name=mynewlist>
       <repeat list=&oldpend; entity=myli>
	 <if> <test match="&myf;" exact><get name=myli attr=href></test>
	   <then> <set name=flag>1</set> </then>
	   <else><get name=myli></else> </if>
       </repeat>
       </set>
        <if> <get name=flag>
	  <then>
	    <write file=users/&minefile;.html><get name=mynewlist></write>
	    <write file="users/&minefile;_complete.html" append><a href="&myf;"> &myf; </a></write>
	    <append-form-history form=&myf;>Removed: <get name=minefile></append-form-history>       
	  </then>
	</if>
      </then> 
    </if>
</actor>

<!--  add tag for hash and signing -->

 <actor tag=hash handle></actor>
 <actor tag=sign handle></actor>

 
<!--  generate route object for this context -->
 <actor tag=process-route desc=" processes the content as a route object for
 this context --  e.g. replaces manager and such with appropriate values">
 <set name=routeobject><route> </route></set>
<set local name=mysteps><dummy><get index=content findall=routestep></dummy></set>
 <set local name=mydept><user-dept user=&myuser;></set>
 <set local name=replacements><get agent name=cachedroutes key=&myuser;></set>
 <set name=replacements key=user><get name=myuser></set>
 
 <repeat list=&mysteps; entity=myli>
 <!-- replace user or manager variables -->
 <if> <get name=replacements key=&myli.routeuser.Text;>
 <then> <set index=myli.routeuser insert=0 replace><get name=replacements key=&myli.routeuser.Text;></set>
 </then> <else> <if><get name=users agent key=&myli.routeuser.Text;>
	 <else> <if><test match=printer><get index=myli.routeuser.Text>
	   <else><!-- something is wrong -- send to admin -->
	     <set index=myli.routeuser insert=0 replace><get
	     name=administrator agent></set></else> </if> </else></if> </else></if>

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
<set local name=myauser><get trans name=AuthenticatedUser></set>

<write append line file="&dir;/&dir;_report.html"><li><a href="&myfile;.html">
<get trans name=AuthenticatedUser> &dayName; &monthName; &day;, &year; </a><br></write>
      
<write append line file="&dir;/&year;/&dir;_report.html"><li><a href="&myfile;.html">
<get trans name=AuthenticatedUser> &dayName; &monthName; &day;, &year; </a><br></write>   

<!-- save values in monthly report -->      
<write append line file="&dir;/&year;/&month;/&dir;_report.html"><li><a href="&myfile;.html">
<get trans name=AuthenticatedUser> &dayName; &monthName; &day;, &year; </a>
 <if><get name=vars> <then><dl foreach=&var; entity=myv>
  <dt><get name=myv attr=name></dt><dd><get index=myv.Text></dd>
 </dl></then> <else></else> </if>
</li><br>
</write> 

<!-- make a list for each department -->
<set local name=myadept><user-dept user=&myauser;></set>
<write append line file="deptfiles/&myadept;/&year;/&month;/forms_filed.html"><li><a href="&myfile;.html">
<get trans name=AuthenticatedUser> &dayName; &monthName; &day;, &year; </a><br></write> 

<!-- start history of form -->
<append-form-history form=&myfile;.html>Form created</append-form-history>

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
      <set name=val1 local><subst match="\$|\," result="">&content;</subst></set>
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
 <if><test match="true"><get-option option=mailnotifications></test><then>
 <os-command> mail &myuser; < &usrDIR;/&agentName;/message.txt </os-command>
 </then></if>
 </actor>

<!-- given routeandsignaturesgenerateremaining route -->
<actor tag=route-remaining empty desc=" generate remaining ROUTE steps given SIGnature">
 <set name=myr local><get  element name=route></set>
 <set name=mysig local><get  element name=signatures></set>
 <set local name=newroute><route> </route></set>
 <set name=mysigners local><dl>
 <repeat list=&mysig; element=s> 
  <if><get index=s.sigblock.signas> <then>
	   <dt><get index=s.sigblock.signas.Text> </dt>
	   <dd><get index=s.sigblock.signer.Text> </dd>
  </then> <else>
  	   <dt><get index=s.sigblock.signer.Text> </dt>
	   <dd><get index=s.sigblock.signer.Text> </dd>
  </else> </if>
 </repeat>
 </dl>
 <repeat list=&myr.routesteps; element=r> 
  <set name=ru local><get index=r.routeuser.Text;></set>
  <if> <get name=mysigners key=&ru;> <then>
   <set name=mysigners key=&r; delete></set>
  </then> <else>
   <set name=newroute insert=-1><get name=r></set> 
 </else> </if>
 </repeat>
 <get name=newroute>
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

<!-- option handling tags -->

 <!-- load options from content into agent name space -->
 <actor tag=load-options desc="load options and from content into agent name
 space (existing options are removed)">
 <set local name=stuff>&content;</set>
 <set name=options local><dummy><get name=stuff
 findall=agentoption></dummy></set>
 
 <!-- creating formsoptions -->
  <set name=formsoptions agent><dl> </dl></set>
 <repeat list=&options; entity=myoption>
 <set agent name=formsoptions key=&myoption.optionname.Text;><get
 index=myoption.optionvalue.Text></set>
 </repeat>
</actor>
<!-- get specified option from agent name space -->
 <actor tag=get-option empty desc="get option from agent name space "><set
 local name=myoption><get element name=option></set><get
 agent name=formsoptions key=&myoption;></actor>


<!-- showing signatures -->
 <actor tag=show-sig-table empty desc="Given a SIGNATURE object, prints table of sigs, optionally verifies with BLOCK">
  <set local name=sigo><get element name=signatures></set>
  <set local name=myhash><get element name=hash></set>
  <set local  name=mysigs><dummy><get name=sigo findall=signature></dummy></set>

  <if> <get name=mysigs> <then>
  <table>
  <repeat list=&mysigs; entity=sig>
  <TR>
  <TD>  Signed by:<b><full-name user=&sig.sigblock.signer.Text;></b> (&sig.sigblock.signer.Text;)
       <if><get index=sig.sigblock.signas><then><td> signing as <get index=sig.sigblock.signas></td></then><else><td> </td></else></if>
  <TD>&sig.sigblock.sigdate; </TD>

   <if><get name=myhash><then><td>
   <if><test match="&myhash;" exact><get index=sig.sigblock.sighash.Text></test><then>
       <if><sign user=&sig.sigblock.signer.Text; verify=&sig.sig.Text;>&sig.sigblock;</sign> <then>
        <b>verified</b></then> <else>
        <b> does NOT verify</b></else>
       </if></then>
       <else>&sig.sigblock.signer.Text; Signature NOT for THIS Document!
       <get index=sig.sigblock.sighash.Text> != &myhash;
</else></if>
     </td>
       </then></if>
  </TR>
   <if><get index=sig.sigcomment><then><tr><th><full-name user=&sig.sigblock.signer.Text;> Notes:</th><td colspan=2> <get index=sig.sigcomment></td></tr></then></if>
  </repeat>
  </table>
 </then> <else>
 <!-- no signatures found -->
 </else>
 </if>
 </actor>

<!-- keeping log of events  for each form -->
 <actor tag=append-form-history desc="appends CONTENT to history of FORM"> 
  <set name=histfile local><get name=form element>.history</set>
   <write file=&histfile; append><formevent>
&content;
<eventdate>&dateString;</eventdate>
</formevent> <br>
 </write>
</actor>

 <actor tag=show-form-history empty desc="Shows history of FORM"> 
  <set name=histfile local><get name=form element>.history</set>
   <read file=&histfile; process>
   <!-- should check here for brief listing -->
</actor>


 <actor tag=show-forms-list empty desc="Shows a list of FORM (optionally with SIGNATURES)"> 
  <set name=flist local><get name=forms element></set>
  <set name=showsigs local><get name=signatures element></set>
   <set name=mylist><dummy><get name=flist findall=a></dummy></set>
 <table><tr><th>Forms </th>
  <if><get name=showsigs> <then><th>Signatures </th></then> </if> </tr>
   <repeat list=&mylist; entity=myf>
   <set local name=myfile><get name=myf attr=href></set>
   <tr><td><a href=show_signatures.if?formurl=&myfile;>&myfile;</a> </td>
       <if><get name=showsigs> <then>
        <set local name=sigs> <read file=&myfile;.sig process></set>
        <td><get name=sigs findall=signer> </td> 
	</then> </if>
   </tr>
   </repeat>
 </table>
</actor>

<actor tag=show-form-options empty desc=" shows table of options for this form">
 <set name=f local><get name=form element></set>
 <table border=1>
 <caption >Handling options for form: <get name=f> </caption >
 <tr>
   <td><form action="/&agentName;/show_route.if" method=post>
  <input name="formurl" type="hidden" value="&f;">
  View the history of this form and/or send it to someone else for approval.
  <input type="SUBMIT" value="Status / Send">
  </form>
 </td>
 <td><form action="/&agentName;/sign_form.if" method=post>
  <input name="formurl" type="hidden" value="&f;">
   Add a  note to this form (and sign it).
  <input type="SUBMIT" value="Add Note">
  </form>
 </td>
<td><form action="/&agentName;/reject_form.if" method=post>
  <input name="formurl" type="hidden" value="&f;">
    <input name="signas" type="hidden" value="&signas;">
   Reject this form (remove from pending list and do not
   forward to anyone else.)
  <input type="SUBMIT" value="Reject">
  </form>
 </td>
<td><form action="/&agentName;/print_form.if" method=post>
  <input name="formurl" type="hidden" value="&f;">
  Print this form.
  
  on: <select name="printer">
  <option>ps3
  <option>ps4
  <option>ps5
  <option>ps6
  <option>ps2
  <option>ps1
  <option>psfile
  <option>DownloadPostscript
 </select><br>
(or fill in the name of another printer: <input name="otherprinter" size="8">)
<br><input type="SUBMIT" value="Print">
</form>  

 </td> 
 </tr>
 </table >
</actor>


<!-- another no space in result tag -->
<actor tag=find-tag-with-id desc="Finds the element with TAG that has IDATTR
 as value of ID attribute"><set name=myid local><get name=idattr
  element></set><set name=mytag local><get name=tag element></set><set name=myc local><get name=agentname element></set><if><get
    name=myc><then><set name=myc local><get name=&myc;
      agent></set></then><else><set name=myc local>&content;</set></else> </if><set name=tags local><dummy><get name=myc findall=&mytag;></dummy></set><repeat list=&tags entity=myli><if><test match=&myid; exact><get name=myli attr=id></test> <then><get name=myli></then> <else></else> </if></repeat></actor>


<!-- More routing stuff for complex system -  -->
 <!-- no space In result  -->
 <actor tag=authorizor-canonical-name empty desc="get standard form of
   AUTHORIZOR"><set name=mya local><get  element name=authorizor></set><if>
     <get name=mya attr=tag>
     <then><get name=mya attr=type></then>
     <else><get name=mya></else> </if></actor>
 
<!-- this would be easier if we could walk up a list  -->
<actor tag=create-route-cache empty desc=" for every user, create a binding for authorizors">
 <set local name=thecash><dl>
 </dl></set>
 <set local name=alist><get name=authlist agent></set>
 <set local name=currentbindings><dl>
 </dl></set>
 <set name=inboxes agent><dl> </dl></set>
 <set agent name=authorizors><dl> </dl></set>
   <if><get name=alist> <then>
 <internal-create-route bindings=&currentbindings; cache=&thecash;
     authlist=&alist;>
     </then> </if> 
<set agent name=cachedroutes>&thecash;</set>
 </actor>

<!-- does not to proper scoping of names because we are changing internal data -->
<!-- need to figure out how do todo ay copy -->
<actor empty tag=internal-create-route desc="recursively descend authorization
list computing route bindings">
 <set name=parentbindings local><get name=bindings element></set>
 <set name=myalist local><get name=authlist element></set>
 <set name=myc local><get name=cache element></set>
  <set name=mybindings local><dl> </dl></set>

 
  <if><get name=parentbindings keys>
   <then>
      
   <set name=parentkeys><dummy><get name=parentbindings keys></dummy></set>
   <repeat list=&parentkeys; entity=k><set name=mybindings key=&k;><get
   name=parentbindings key=&k;></set> </repeat>
   </then></if>
   <set name=mybindings key=authlistid><get name=myalist attr=id></set>
   <if><get  index=myalist.authorizor>
     <then>
       <set name=myzors><dummy><get  index=myalist.authorizor></dummy></set>
       <repeat list=&myzors; entity=myz><set local name=mk><get name=myz
	 attr=type></set><set name=mybindings key=&mk;><get name=myz></set>
	 
	 <!-- add this type to the global list  -->
	 <set agent name=authorizors key=&mk;><get name=myz></set>
	 <if><get name=myz	attr=group>
	   <then>
	     <repeat list=&myz; entity=u> 
	       <add-inbox-for inbox=&mk; user=&u;>
	     </repeat>
	   </then>
	   <else><add-inbox-for inbox=&mk; user=&myz.Text;></else>
	 </if>
     </repeat>
   </then></if>
   
   <if><get  index=myalist.users><then>
     <set name=myusers><get  index=myalist.users></set>
     <repeat list=&myusers; entity=u>
     <if><get name=u attr=tag><then><set name=myu><get index=u.Text></set></then>
      <else><set name=myu><get name=u></set></else></if>
       <set name=myc key=&myu;><get name=mybindings></set></repeat>
     </then></if>
       
     <if><get  index=myalist.authlist>
       <then>
	 <set name=next><dummy><get  index=myalist.authlist></dummy></set>
	 <repeat list=&next; entity=myl>
	  
	   <internal-create-route bindings=&mybindings;
	   authlist=&myl; cache=&myc;></repeat>
	 </then>
       </if>
 </actor>

 <actor tag=add-inbox-for empty desc="adds INBOX for USER">
   <set name=inb local><get name=inbox element></set>
   <set name=myu local><get name=user element></set>
    <if> <get name=inboxes agent key=&myu;>
      <else> <set name=inboxes agent key=&myu;><ul> </ul></set></else>
    </if>
    <set name=myboxes><get name=inboxes agent key=&myu;></set>
    <set name=myboxes insert=-1><get name=inb></set>
</actor>

<actor tag=show-auth-list empty desc="">
  <set name=myalist local><get name=authlist element></set>
    <set name=mytable local><get name=table element></set>
  <set name=mymaxdepth local><get name=maxdepth element></set>

  <set name=mynextlists local><dummy><get index=myalist.authlist></dummy></set>
  <set name=myspan local><sum digits=0><get name=mynextlists size></sum></set>
  <set name=mylistid local><get name=myalist attr=id></set>
  <set name=mydepth local><get name=myalist attr=level></set>
  <set name=mysubcols local><dummy><get name=mynextlists
  findall=authlist></dummy></set>
  
   <set name=kidcols local>0</set>
  <repeat list=&mysubcols; entity=myl>
   <if><get index=myl.authlist> <else><set name=kidcols ><sum digits=0> 1
   <get name=kidcols></sum ></set></else> </if></repeat>

   <set name=mynewmaxdepth local><difference digits=0><get name=mymaxdepth>
   <get name=mydepth> </difference></set>

    <set index=mytable.tr-&mydepth; insert=-1><td colspan=&kidcols;  align=center>
  <a href=edit_auth_list.if?authid=&mylistid;><get index=myalist.authdescription></a>
  <set name=authorizors><dummy><get index=myalist.authorizor></dummy></set>
  <repeat list=&authorizors; entity=a><br> <set name=thistype><get name=a
    attr=type></set><b><get name=thistype>: <get name=a></b>
  </repeat>
  <if> <get index=myalist.users><then>
    <br>  Employees:   <repeat list=&myalist.users; entity=u>
     <if><get name=u attr=tag><then><set name=myu><get index=u.Text></set></then>
      <else><set name=myu><get name=u></set></else></if>

      <a href=edit_user.if?euser=&myu;><get name=myu></a></repeat>
    </then>
  </if>
  </td>
  </set>
  
  <if><get index=myalist.authlist> <then>
    <repeat list=&mynextlists;  entity=l>
      <show-auth-list authlist=&l;
      table=&mytable; maxdepth=&mymaxdepth;>
      
    </repeat>
  </then>
  <else><set name=mydeeper><sum digits=0>1 <get name=mydepth></set>
    <set index=mytable.tr-&mydeeper; insert=-1><td
    colspan=1 rowspan=&mynewmaxdepth;> </td>
  </else></if>
 

</actor>

<!-- $Id$ -->
