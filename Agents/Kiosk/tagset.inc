<!-- Kiosk actors:				 -*-html-helper-*- -->
<!-- Copyright 1997, 1998 Ricoh Silicon Valley			   -->
<!-- === Tagset for the kiosk agent	 ===		   -->
<tagset name=Kiosk-agent-actors base=Standard><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=Kiosk-agent-actors>




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

<!-- tags for putting the common elements on the editing pages -->
<actor tag="page-header" desc="">
<read interform file=heading.inc process>
<get element name=&content;>
</actor>

<actor tag="page-footer" empty>
<br>
<hr>
<a href=main.if>View Site (as seen on &AGENT.mb;)</a> || 
<a href=initialize.if>Restart</a> || 
</actor>

<actor tag="control-button">
<set name=act local><get name="action" element></set>
<form action="&act;.if" method="GET">
<input type="submit" value="&content;">
</form>
</actor>

<actor tag="info"  desc="get specified attribute from any elements in content"><set local
name=attr><get element name=attr></set><repeat list="&content;"
entity=el><if><test match=&attr;><get name=el attr=name></test>
 <then><get index=el.Text></then> 
 <else> </else></if></repeat></actor>

<actor tag="show-department" empty desc="show this department's information">
<set local name=mykey><get element name=dept></set>
<set local name=mydept><get agent name=departments key=&mykey;></set>
<set local name=myfull><get name="full" element></set>
<a href="show_dept.if?dept=&mykey;"><info attr=name><get name=mydept></info></a>
<if><get name=myfull>
 <then><repeat list="&mydept;"> <br><a href=more_info.if?dept=&mykey;> &li.Text;</a></repeat> </then> 
 <else> </else></if>
</actor>

<actor tag="dept-map" empty desc="shows a map for this department">
<set local name=mykey><get element name=dept></set>
<if><read file=maps/&mykey;.gif info>
 <then><img src=/~&agentName;/maps/&mykey;.gif> </then> 
 <else><img src=/~&agentName;/floorplan.gif width=550> </else></if>
</actor>


<actor tag="dept-icon" empty desc="shows icon for this department">
<set local name=mykey><get element name=dept></set>
<set local name=myfile><get name=icons key=&mykey; agent></set>
<if><get name=myfile>
 <then> </then> 
 <else> <set local name=myfile>default.gif</set></else></if>
<if><read file=icons/&myfile; info>
 <then><img src=/~&agentName;/icons/&myfile;> </then> 
 <else><img src=/~&agentName;/icons/default.gif> </else></if>
</actor>

<actor tag="mark-image" desc="adds a marker to image" empty>
<set local name=myimage><get element name=image></set>
<set local name=myx><get element name=x></set>
<set local name=myy><get element name=y></set>
<set local name=mydept><get element name=dept></set>
<set name=prefix>&usrDIR;/&agentName;/</set>
<set name=marker>&prefix;/marker.pnm</set>
<set name="output">&prefix;/maps/&mydept;.gif</set>
<os-command-output> anytopnm &prefix;/&image; | pnmpaste &marker; &myx; &myy; | ppmtogif > &output; </os-command-output>
</actor>



 <actor tag=create-icon-table empty desc=" create a table element containing the
 thumbnail's for these photos -- along with CONTROLS if specified">
<set name=myphotos local><get name=icons element></set>
<set name=mycols local><get name=cols element></set>

<if><get name=mycols> <else><set name=mycols local>6</set></else> </if>
 <set name=psize local><get index=myphotos size></set>

 <set name=prows local><sum digits=0> 0.49 <quotient> &psize; &mycols; </quotient></sum></set>

<table border=1> <caption>Icons available for use<br> There could be several mechanisms to import new items, file upload, select from a Web repository, select from an arbitrary URL, ...</caption>
 <repeat start=1 stop=&prows; entity=r>
 <tr>
 <repeat start=1 stop=&mycols; entity=c>
 <set name=myindex local><sum digits=0><product> <difference> &r; 1
 </difference> &mycols; </product> &c; </sum></set>
 
 <set name=myphoto local><get index=myphotos.&myindex;></set>

 <td align=center> <if> <get name=myphoto> <then>
    <img src=/~&agentName;/icons/&myphoto;> <br>
    <code>&myphoto;</code>

</then> <else><!-- No photo: myphotos.photo-&myindex; --> </else> </if>
</td>
 
</repeat>
</tr>
</repeat>
</table>
 </actor>


