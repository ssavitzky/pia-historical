<!-- Forms actors:				 -*-html-helper-*- -->
<!-- Copyright 1997, 1998 Ricoh Silicon Valley			   -->
<!-- === This file should really be a tagset ===		   -->
<tagset name=Photo-agent-actors base=Standard><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=Photo-agent-actors>

<actor tag=set-current-user empty desc="sets the name of the current USER">
 <set local name=myuser><get name=user element></set>
 <if><get name=myuser> <else><set local name=myuser>unknown</set></else></if>
 <set name=currentuser agent><get name=myuser></set>
 <write file=currentuser><get name=myuser></write>
</actor>

<!-- ugly because no spaces in result -->
<actor tag=get-current-user empty desc="return the name of the current user or
unknown"><set local name=myuser><get agent name=currentuser></set><if><get name=myuser> <else><set local name=myuser><read file=currentuser></set></else></if><get name=myuser></actor>


<actor tag=users-dialogue empty desc=" return form elements for getting
username">
 <if> <get agent name=users> <then><set name=myusers local><get agent
      name=users></set></then> <else>
 <if> <get agent name=userdir> <then>
 <set name=myusers local><read file=&AGENT.userdir; process></set>
 </then></if>
 </else></if>
 <if> <get name=myusers> <then>
  <select name="user">
 <if> <get element name=selected> <then>
 <option selected><get element name=selected></option>
 </then></if>
 <repeat list=&myusers;><option>&li;</option></repeat>
  </select>
 </then>
 <else>
 <input name="user" size="14">
 </else>
</if>
</actor>

<actor tag=date-dialogue empty desc=" return form elements for getting date
variables will be PREFIXmonth, PREFIXday, PREFIXyear">
<set local name=prefix><get element name=prefix></set>
 <if><get element name=prefix> <else><set local name=prefix>form</set></else></if>
Month:
 <select name=&prefix;month>
 <option>&monthName;</option>
 <repeat list="January February March April May June July August September
 November December">
 <option>&li;</option>
</repeat>
</select>

Day:
<select name=&prefix;day>
 <repeat start=&day; stop=31>
 <option>&li;</option>
</repeat>
 <repeat start=1 stop=&day;>
 <option>&li;</option>
</repeat>
</select>

</actor>

<actor tag=show-photo desc="shows a photo"  empty>
<set name=myphoto local><get name=photo element></set>
<set name=mydirectory> <get  name=myphoto attr=directory></set>
<set name=myfile> <get  name=myphoto attr=file></set>
<set name=mythumb> <get  index=myphoto.photothumbnail.Text></set>
<if><get name=mythumb> <then>
  <set name=mythumbimg local><img
  src=/~&agentName;/&mydirectory;/&mythumb;></set></then> <else><set
  name=mythumbimg local><img src=/~&agentName;/&mydirectory;/&myfile; width=60></set>
 </else> </if>
<if><get name=thumbnail element><then>
<a href=/~&agentName;/&mydirectory;/&myfile;><get name=mythumbimg></a>
 </then><else>
<img src=/~&agentName;/&mydirectory;/&myfile;>
 </else></if>
<if><get index=myphoto.photocaption.Text><then>
 <br> <a href=/~&agentName;/&mydirectory;/&myfile;><get index=myphoto.photocaption></a> 
</then>
<else> 
 <br><input name=caption&myphoto.photoindex.Text; size=8>
</else>
 </if>
</actor>

<actor tag=create-photo-captions empty desc="createa photo caption object from a
directory">
<set name=mydirectory local><get name=directory element></set>
<set name="files" local><read directory file="&mydirectory;"
match="\.jpg$"></set>
<set name=mytitle>Photos </set>
 <if><read info file=&mydirectory;/captions.html> <then>
  <set name=caps local><dummy><read file=&mydirectory;/captions.html process></dummy></set>
<set name=mytitle><get index=caps.h1.Text></set>
 <set name=mycaps><dummy><get name=caps findall=a></dummy></set>
  </then> </if>
<if><get name=files><then>
<photoscaption>
<photosauthor>unknown</photosauthor>
<photostitle><get name=mytitle></photostitle>
<photosdirectory>&mydirectory;</photosdirectory>
<photosdate>&mydirectory;</photosdate>
<photos>
<repeat list=&files; entity=jpg><set name=count><sum digits=0>1 <get name=count></sum></set>
<photo directory=&mydirectory; file=&jpg;>
<photothumbnail><subst match="\.jpg " result=".small.gif">&jpg; </subst></photothumbnail>
<photoindex>&count;</photoindex>
  <repeat list=&mycaps; entity=aref>  <if> <test match=&jpg;><get name=aref attr=href></test> <then><photocaption><get index=aref.Text></photocaption></then></if></repeat>
</photo>
</repeat>
</photos>
</photoscaption>
</then></if>
</actor>

 <actor tag=show-photos empty desc="Shows all the photos referenced by
 CAPTION">
 <set name=mycap local><get name=caption element></set>
 <set name=myphotos local><get index=mycap.photos></set>
 <if><get index=mycap.photostitle.Text> <then><center><h1>
 &mycap.photostitle.Text;</h1></center></then>  </if>
 <center>
  <if><get index=mycap.photosauthor.Text> <then>Photos by &mycap.photosauthor.Text;<br></then>  </if>
  <if><get index=mycap.photossrc.Text> <then> Originally from <a
  href="&mycap.photossrc.Text;">&mycap.photossrc.Text;</a></then>
  </if>
  <if><get index=mycap.photosdate.Text> <then> on &mycap.photosdate.Text;<br></then>
  </if>

 <create-photo-table cols=6 photos=&myphotos;>
 </center> 
 </actor>

 <actor tag=create-photo-table empty desc=" create a table element containing the
 thumbnail's for these photos -- along with CONTROLS if specified">
<set name=myphotos local><get name=photos element></set>
<set name=mycols local><get name=cols element></set>
  <set name=mycontrols><get name=controls element></set>
<if><get name=mycols> <else><set name=mycols local>6</set></else> </if>
 <set name=psize local><get index=myphotos.photo size></set>

 <set name=prows local><sum digits=0> 0.49 <quotient> &psize; &mycols; </quotient></sum></set>

<table>
 <repeat start=1 stop=&prows; entity=r>
 <tr>
 <repeat start=1 stop=&mycols; entity=c>
 <set name=myindex local><sum digits=0><product> <difference> &r; 1
 </difference> &mycols; </product> &c; </sum></set>
 
 <set name=myphoto local><get index=myphotos.photo-&myindex;></set>
 <td> <if> <get name=myphoto> <then>
           <if> <get name=mycontrols> <then>
           <set name=pindex><get index=myphoto.photoindex.Text></set>
           <img src=r.gif alt=R width=16 height=16> <input type="checkbox" name=rotate&pindex; value=270>
           <img src=rcc.gif alt=r width=16 height=16> <input type="checkbox" name=rotate&pindex; value=90>
           <!-- img src=del.gif alt=X no proc yet--> <!-- input
           type="checkbox" name=delete&pindex; value=delete --><br>
           </then> </if>
    <show-photo photo=&myphoto; thumbnail=true>
       
</then> <else><!-- No photo: myphotos.photo-&myindex; --> </else> </if>
</td>
 
</repeat>
</tr>
</repeat>
</table>
 </actor>

 <actor tag=edit-photo-process empty desc="Apply designated operations to photo">
 <set name=myphoto local> <get name=photo element></set>
 <set name=pindex local><get index=myphoto.photoindex.Text></set>
 <if><get form name=rotate&pindex;><then>
 <set local name=deg><get form name=rotate&pindex;></set>
 <rotate-photo photo=&myphoto; degrees=&deg;>
 </then>  </if>
  <if><get form name=caption&pindex;><then>
 <set index=photo.photocaption insert=0 replace><get name=caption&pindex;
 form></set>
 </then></if>
</actor>



 <actor tag=list-rolls empty desc="List of all photo directories">
 <read file=".catalog">
 </actor>

 <actor tag=get-image empty desc="Gets all the images referred to in a
 file"> 
 <set local name=base><get name=base element></set>
 <set local name=directory><get name=destination element></set>
 <set local name=captions> <get name=captions element></set>
 <set local name=myimg> <get name=img element></set>
 <set local name=myurl> <get name=myimg attr=src></set>
 <set local name=mythumb> <get name=thumbnail element></set>
<if><get index=captions.lastindex>  <else> <set name=captions
insert=-1><lastindex>0</lastindex></set></else> </if>
  <set local name=ext><if><test match="\.jpg$">&myurl;</test> <then>jpg</then>
  <else>gif</else> </if></set>
  <set index=captions.lastindex><sum digits=0> 1 <get index=captions.lastindex.Text> </sum></set>
  <set local name=index><get index=captions.lastindex.Text></set>
 <set local name=readstatus><read href=&myurl; base=&base; into=&directory;/&index;.&ext;></set>
 <set name=captions insert=-1>
<photo directory=&directory; file=&index;.&ext;><photoindex>&index;</photoindex>
<photourl base=&base;>&myurl;</photourl>
<if> <get name=mythumb> <then>
 <set local name=thumbsrc><get index=mythumb attr=src></set>
  <set local name=ext><if><test match="\.jpg$">&thumbsrc;</test> <then>jpg</then>
  <else>gif</else> </if></set>
 <set local name=readstatus><read href=&thumbsrc; base=&base; into=&directory;/&index;.small.&ext;></set>
<photothumbnail>&index;.small.&ext;</photothumbnail>
 </then> </if>
<photocaption><get name=myimg attr=alt></photocaption>
</photo></set>
</actor>


<!-- needed a way to convert relative URL's to base URL's -->
 <actor tag=full-url empty desc="fully qualifies a URL"><set name=mybase local><get name=base element></set><set name=myurl><get name=url element></set><if> <test match="^http://">&myurl;</test> <then>&myurl;</then> <else><if>   <test match="^/">&myurl;</test>    <then><set local name=leftover><subst   match="^http://[^/]*/" result="">&mybase;</subst></set><subst match="/&leftover;" result="">&mybase;</subst>&myurl;</then>   <else><subst match="[^/]* " result="">&mybase; </subst>&myurl;</else></if></else> </if></actor>

<actor tag=rotate-photo empty desc=" rotates a PHOTO by DEGREES, 270 is clockwise,
90 counterclockwise -- other angles won't work right">
 <set local name=myphoto><get name=photo element></set>
 <set local name=mydeg><get name=degrees element></set>
 <set local name=directory>&usrDIR;/&agentName;/<get name=photo attr=directory></set>
 <set local name=file><get name=photo attr=file></set>
 <set local name=thumbnail><get index=photo.photothumbnail.Text></set>
 <set local name=newfile>r.&file;</set>
<set name=myphoto attr=file><get name=newfile></set>

<if><test zero><difference><get name=mydeg> 90</difference> </test> <test
zero><difference><get name=mydeg> 270</difference> </test>
 <then>
 <os-command-output>djpeg &directory;/&file; | pnmflip -r&mydeg; | cjpeg -quality 85 > &directory;/&newfile; </os-command-output>
 </then>
 <else>
 <os-command-output>djpeg &directory;/&file;.orig | pnmrotate &mydeg; | cjpeg -quality 85 > &directory;/&newfile; </os-command-output>
 </else></if>

<if> <get name=thumbnail> <then>
 <set local name=newthumb>r.&thumbnail;</set>
 <set index=photo.photothumbnail insert=0 replace>&newthumb;</set>
<if> <test match="\.jpg$"><get name=thumbnail>  </test><then>
<os-command>djpeg &directory;/&thumbnail; | pnmflip -r&mydeg; | cjpeg -quality 85 > &directory;/&newthumb; </os-command>
 </then><else>
 <os-command>giftopnm &directory;/&thumbnail;| pnmflip -r&mydeg; | ppmquant 256 | ppmtogif > &directory;/&newthumb; </os-command>
 </else></if>
 </then>  </if>

</actor>

 <actor tag=generic-edit-form desc="Puts to edit fields for each tag in Content
 except EXCEPTIONS">
 <set  local name=object>&content;</set>
 <set  local name=exceptions><get name=exceptions element></set>
 <repeat list=&object; entity=foo>
 <set name=mytag local><get name=foo attr=tag></set>
 <if> <get name=mytag> <then>
 <if> <test match="&mytag;"> <get name=exceptions> </test> <else>
 <if> <get index=foo.Text> <then><br><get name=mytag> <input name="&mytag;" value=&foo.Text;></then> <else><br><get name=mytag> <input name="&mytag;"></else> </if>


 </else> </if>
 </then> </if>

 </repeat>

</actor>

<actor tag=generic-edit-form-process empty desc="updates edit fields for each tag in Content
 except EXCEPTIONS">
 <set  local name=object><get element name=object></set>
 <set  local name=exceptions><get name=exceptions element></set>
 <repeat list=&object; entity=foo>
 <set name=mytag local><get name=foo attr=tag></set>
 <if> <get name=mytag> <then>
 <if> <test match="&mytag;"> <get name=exceptions> </test> <else>
    <if><get name=foo><then>
      <set index=object.&mytag; insert=0 replace><get name=&mytag; form></set>
    </then>
    <else>
     <set index=object.&mytag; insert=0><get name=&mytag; form></set>
    </else></if>
 </else> </if>
 </then> </if>
 </repeat>
</actor>

<!-- $Id$ -->
