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
 <br><input name=caption&myphoto.photoindex.Text;>
</else>
 </if>
</actor>

<actor tag=create-photo-captions empty desc="createa photo caption object from a
directory">
<set name=mydirectory local><get name=directory element></set>
<set name="files" local><read directory file="&mydirectory;"
match="\.jpg$"></set>
 <if><read info file=&mydirectory;/captions.html> <then>
  <set name=caps local><read info file=&mydirectory;/captions.html
  process></set>
 <set name=mycaps><get name=caps findall=a></set>
 <set name=capdl local><dl>
 <repeat list=&mycaps; entity=aref>
 <dt><get name=aref attr=href></dt>
 <dd><get index=aref.Text></dd>
  </repeat>
 </dl></set>
  </then> </if>
<if><get name=files><then>
<photoscaption>
<photosauthor>unknown</photosauthor>
<photosdirectory>&mydirectory;</photosdirectory>
<photosdate>&mydirectory;</photosdate>
<photos>
<repeat list=&files; entity=jpg><set name=count><sum>1 <get name=count></sum></set>
<photo directory=&FORM.dir; file=&jpg;>
<photothumbnail><subst match="\.jpg$" result="small.gif">&jpg;</subst></photothumbnail>
<photoindex>&count;</photoindex>
 <if> <get name=capdl key=&jpg;> <then>
<photocaption><get name=capdl key=&jpg;></photocaption></then> </if>
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
 thumbnail's for these photos">
<set name=myphotos local><get name=photos element></set>
<set name=mycols local><get name=cols element></set>
<if><get name=mycols> <else><set name=mycols local>6</set></else> </if>
 <set name=psize local><get index=myphotos.photo size></set>

 <set name=prows local><sum digits=0> 0.49 <quotient> &psize; &mycols; </quotient></sum></set>

<table>
 <repeat start=1 stop=&prows; entity=r>
 <tr>
 <repeat start=1 stop=&mycols; entity=c>
 <set name=myindex local><sum digits=0><product> <difference> &r; 1
 </difference> &mycols; </product> &c; </sum></set>
 <td>
 <set name=myphoto local><get index=myphotos.photo-&myindex;></set>
  <if> <get name=myphoto> <then><show-photo photo=&myphoto;
       thumbnail=true></then> <else><!-- No photo: myphotos.photo-&myindex; --> </else> </if>
</td>
</repeat>
</tr>
</repeat>
</table>
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
 <set local name=readstatus><read href=&thumbsrc; base=&base; into=&directory;/&index;.small.&ext;></set>
<photothumbnail>&index;.small.&ext;</photothumbnail>
 </then> </if>
<photocaption><get name=myimg attr=alt></photocaption>
</photo></set>
</actor>


<!-- needed a way to convert relative URL's to base URL's -->
 <actor tag=full-url empty desc="fully qualifies a URL"><set name=mybase local><get name=base element></set><set name=myurl><get name=url element></set><if> <test match="^http://">&myurl;</test> <then>&myurl;</then> <else><if>   <test match="^/">&myurl;</test>    <then><set local name=leftover><subst   match="^http://[^/]*/" result="">&mybase;</subst></set><subst match="/&leftover;" result="">&mybase;</subst>&myurl;</then>   <else><subst match="[^/]* " result="">&mybase; </subst>&myurl;</else></if></else> </if></actor>
<!-- $Id$ -->