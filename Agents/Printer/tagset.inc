<!-- Printer actors:				 -*-html-*- -->
<!-- Copyright 1997, 1998 Ricoh Silicon Valley			   -->
<!-- === Tagset for the Printer agent	 ===		   -->
<tagset name=Printer-agent-actors base=Standard><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=Printer-agent-actors>

<!-- Java handled tags -->
<actor tag=url-parse empty handle> </actor>

<!-- naming convention for the spool files -->
<actor tag=new-file-name empty desc=" returns a unique filename">spool/&year;/&month;/&day;/&hour;:&minute;:&second;</actor>


<!-- tags for dealing with styles -->

<actor tag=save-styles empty desc=" check the styles directory and load any
 identifier's">
<write file=styles/styles.dat><get name="styles" agent></write>
</actor>

<actor tag=load-styles empty desc=" check the styles directory and load any
 identifier's">
   <if>
   <read file="styles/styles.dat" info>
   <then><set name=styles agent><read file="styles/styles.dat" process></set> </then> 
     <else>
     <set name=styles agent><dl> </dl></set>
    <set name=files local><dummy><read directory file=styles
    match="\.sty"></dummy></set>
    <repeat list=&files; entity=f>
    <set name=style local><subst match="\.sty" result="">&f;</set>
    <set name="styles" agent key=&style;><style name=&style;>
    <style-name>&style;</style-name>
    <if>
    <read info file=styles/&style;.ts>
    <then><stylets>Printer-styles-&style;</stylets> </then> 
    </if>
    <if>
    <read info file=styles/&style;.html2psrc>
    <then><stylepsrc>&usrDIR;/&agentName;/styles/&style;.html2psrc</stylepsrc> </then> 
    </if>
    <if>
    <read info file=styles/&style;.pre>
    <then><stylepreproc>styles/&style;.pre</stylepre> </then> 
    </if>
    <if>
    <read info file=styles/&style;.post>
    <then><stylepostproc>styles/&style;.post</stylepost> </then> 
    </if>
   </style></set>
  </repeat>
 </else>
   
   </if>
   <!-- elude the corresponding tagged sets -->
<set name=mystys><get agent name=styles values></set>
<repeat list=&mystys; entity=mysty>
<set name=myts><get index=mysty.styletsfile.Text></set>
<if><get name=myts>
<then>
  <if><read info file=&myts;>
   <then><read process file=&myts;> </then>
   </if>
  </then>
</if>
</repeat>

</actor>

<actor tag="style-option-list" empty desc="listing of styles">
<set local name=myurl><get name=url element></set>
<set local name="mystyles"><dummy><get agent name=styles keys></dummy></set>
 <option><default-style url=&myurl;></set></option>
 <repeat list="&mystyles;" entity=s>
 <option><get name="s"></option>
 </repeat>
</actor>

<actor tag="default-style" empty desc=" defaul style for url"><set local
 name=myurl><get name=url element></set><set name=purl local><url-parse
 url=&myurl;></set><set name=host local><get name=purl key=host></set><if><get
 name=defaultstyles agent><else><load-data
 data=defaultstyles></else></if><if><get name=host><else><set name=host>default</set></else></if><if><get name=defaultstyles key=&host; agent>
   <then><get name=defaultstyles key=&host; agent></then>
   <else>default</else></if></actor>


<actor tag="set-default-style" empty desc="sets default style for this URL">
<set local name=myurl><get name=url element></set>
<set local name="mystyle"><get name=style element></set>
<if><get name=defaultstyles agent><else>
    <load-data data=defaultstyles></else></if>
 <set name=purl local><url-parse url=&myurl;></set>
 <set name=host local><get name=purl key=host></set>
<if><get name=mystyle attr=tag><then><set name=mystyle><get name=mystyle attr=name></set> </then></if>
 <set name=defaultstyles key=&host; agent><get name=mystyle></set>
<save-data data=defaultstyles>
</actor>
    

<actor tag="fetch-url" empty desc="retrieve the specified url into the output
  file and make an entry in the cache using the appropriate style tags and pre-processing">
  <set local name=inurl><get name=input element></set>
  <set local name=mystyle><get name=style element></set>
  <set local name=myout><get name=output element></set>
  <set local name=baseurl><get name=base element></set>
  <if><test match=style> <get name="mystyle" attr=tag> </test>
  <else><set name="mystyle"><get name=styles agent key=&mystyle;></set> </else>
  </if>


  <if><get index="mystyle.stylepreproc.Text">
  <then><read file=&mystyle.stylepreproc.Text; process> </then> 
  </if>

  <if><tagset-exists name=&mystyle.stylets.Text;>
   <then>
   <set name="document"><read href=&inurl; process
     tagset=&mystyle.stylets.Text; base=&baseurl;></set>
   </then> 
   <else> 
   <set name="document"><read href=&inurl; process  base=&baseurl;></set> 
   </else>
  </if>
  <if><get index="mystyle.stylepostproc.Text"><then>
  <read file=&mystyle.stylepostproc.Text; process  base=&baseurl;> 
   </then> <else> </else></if>

 <set local name=mystylename><get name=mystyle attr=name></set>
  <cache-entry url=&inurl; style=&mystylename; output=&myout; >

  <write file=&myout;.html><get name=document></write>

</actor>

<actor tag="fetch-file" empty desc="retrieve the specified file into the output
  file and make an entry in the cache using the appropriate style tags and pre-processing">
  <set local name=inurl><get name=input element></set>
  <set local name=mystyle><get name=style element></set>
  <set local name=myout><get name=output element></set>
  <set local name=baseurl><get name=base element></set>
  <if><test match=style> <get name="mystyle" attr=tag> </test>
  <else><set name="mystyle"><get name=styles agent key=&mystyle;></set> </else>
  </if>


  <if><get index="mystyle.stylepreproc.Text">
  <then><read file=&mystyle.stylepreproc.Text; process> </then> 
  </if>

  <if><tagset-exists name=&mystyle.stylets.Text;>
   <then>
   <set name="document"><read file=&inurl; process
     tagset=&mystyle.stylets.Text;></set>
   </then> 
   <else> 
   <set name="document"><read file=&inurl; process></set> 
   </else>
  </if>
  <if><get index="mystyle.stylepostproc.Text"><then>
  <read file=&mystyle.stylepostproc.Text; process  base=&baseurl;> 
   </then> <else> </else></if>

 <set local name=mystylename><get name=mystyle attr=name></set>
  <cache-entry url=&baseurl; style=&mystylename; output=&myout; >

  <write file=&myout;.html><get name=document></write>

</actor>



<actor tag="fetch-url-old" empty desc="retrieve the specified url into the output
  file and make an entry in the cache using the appropriate style tags and pre-processing">
  <set local name=inurl><get name=input element></set>
  <set local name=mystyle><get name=style element></set>
  <set local name=myout><get name=output element></set>
  <set local name=baseurl><get name=base element></set>
  <if><test match=style> <get name="mystyle" attr=tag> </test>
  <else><set name="mystyle"><get name=styles agent key=&mystyle;></set> </else>
  </if>


<if> <test match="\?">&inurl;</test>
 <then>
  <set local name=qs><subst match="http://[^\?]*\?"
  result="">&inurl;</subst></set>
  <set name=inurl><subst match="\?&qs; " result="">&inurl; </subst></set>
 </then>
 <else>
  <set local name=qs></set>
 </else>
 </if>

Fetching &inurl;, qs is &qs;
  <if><get index="mystyle.stylepreproc.Text">
  <then><read file=&mystyle.stylepreproc.Text; process> </then> 
  </if>

 <if><get index="mystyle.stylepostproc.Text"> TRUE ALWAYS READ SO DOC is AVAILABLE
  <then>

  <if><tagset-exists name=&mystyle.stylets.Text;>
   <then>
   <set name="document"><read href=&inurl; query="&qs;" process
     tagset=&mystyle.stylets.Text; base=&baseurl;></set>
   </then> 
   <else> 
   <set name="document"><read href=&inurl; query="&qs;" process  base=&baseurl;></set> 
   </else>
  </if>
  <if><get index="mystyle.stylepostproc.Text"><then>
  <read file=&mystyle.stylepostproc.Text; process  base=&baseurl;> 
   </then> <else> </else></if>

 <set local name=mystylename><get name=mystyle attr=name></set>
  <cache-entry url=&inurl; style=&mystylename; output=&myout; >

  <write file=&myout;.html><get name=document></write>

 </then> 
 <else> <!-- not used currently -- too slow -->
  <set name=commandout> 
  <if><tagset-exists name=&mystyle.stylets.Text;>
   <then><read href=&inurl; query="&qs;" process tagset=&mystyle.stylets.Text;
   into=&myout;.html  base=&baseurl;></then> 
   <else><read href=&inurl; query="&qs;" into=&myout;.html  base=&baseurl;> </else>
  </if>
  </set>
 </else>
 </if>



</actor>


<actor tag="cache-entry" empty>
 <set local name=inurl><get name=url element></set>
  <set local name=mystyle><get name=style element></set>
  <set local name=myout><get name=output element></subst></set>
<if><get name=cachecounter agent>
 <else><set name=cachecounter agent><read file=spool/cache.dat></set> </else></if>
 <set name=cachecounter agent><sum digits=0><get
  name=cachecounter agent> 1 </sum></set>
 <write file=cache.dat><get name=cachecounter agent></write>
 <write file=cache/&AGENT.cachecounter; append><cacheentry style=&mystyle; href=&inurl;
  spoolfile=&myout; date=&date; cachenum=&AGENT.cachecounter;>&inurl;</cacheentry>
 </write>
</actor>

<actor tag="get-cache-entry" empty><set local name=cnum><get name=num
element></set><if><read file=cache/&centry; info><then><read
file=cache/&centry; process></then></if></actor>


<actor tag=create-ps-file-simple empty desc="create a  postscript file given a URL">
  <set local name=inurl><get name=input element></set>
    <set local name=htmlfile><get name=file element></set>
  <set local name=output><get name=output element></set>
  <set local name=mystyle><get name=style element></set>

  <if><test match=style> <get name="mystyle" attr=tag> </test>
   <else><set name="mystyle"><get name=styles agent key=&mystyle;></set> </else>
   </if>

  <set name=comoptions> </set>
  <if><get name=inurl> <then><set name=comoptions> -b "&inurl;"</set></then> </if>
  
  <if><get name=htmlfile> 
  <else>
    NO htmlfile
  </else> </if>

<set name="fpre">&usrDIR;/&agentName;/</set>

  <if> <get index=mystyle.stylepsrc.Text>
    <then> <set name=comoptions> -f :&mystyle.stylepsrc.Text; <get
      name=comoptions></set></then>
  </if>
  <set name=cat local>cat  &fpre;&htmlfile; </set>
  <set name=commandout>command: &cat; | html2ps &comoptions; -o &fpre;&output;.ps
  <os-command-output>  &cat; | html2ps &comoptions; -o &fpre;&output;.ps  </os-command-output>
  </set>
</actor>

  
<actor tag=create-ps-preview-simple empty desc="create a  postscript file given a URL">
  <set local name=output><get name=psfile element></set>
  <set local name=mystyle><get name=style element></set>

  <if><test match=style> <get name="mystyle" attr=tag> </test>
   <else><set name="mystyle"><get name=styles agent key=&mystyle;></set> </else>
   </if>

<set name=fpre>&usrDIR;/&agentName;/</set>
<if><get name=gsdevice agent><then><set name=gsdevice><get agent name=gsdevice></set><set name=gsext><get agent name=gsext></set></then><else><set name=gsdevice>jpeg</set><set name=gsext>jpg</set></else></if>
<set name="carguments" local> -sDEVICE=&gsdevice; -sOutputFile=&fpre;&output;%d.&gsext; </set>
 <if><get index=mystyle.stylegsargs.Text> 
  <then><set name="carguments" local> <get name=carguments> <get index=mystyle.stylegsargs.Text> </set> </then> 
   </if>

<set name=commandout> command:  cat /dev/null | gs &carguments; &fpre;&output;.ps</set>
<set name=commandout> <get name=commandout> <os-command-output> cat /dev/null | gs &carguments; &fpre;&output;.ps </os-command-output></set>
  </actor>

  
<actor tag=print-ps-file empty desc=" print the postscript file given">
  <set local name=myfile><get name=psfile element></set>
 <set local name=mylp><get name=device agent></set>
 <if><get name=device agent> <then></then> <else><set name=mylp>lp</set></else> </if>
  <if><read file=&myfile; info> <then></then> <else><set
  name=myfile>&usrDIR;/&agentName;/&myfile;</set></else> </if>

  <if><read file=&myfile; info>
    <then>
    <os-command>lpr -P&mylp; &myfile;  </os-command>
    </then> <else> FILE is MISSING (&myfile;)</else> </if>
</actor>


<actor tag="show-summary" description="shows a summary for the given spool file">
 <set local name=myspool><get name=spoolfile element></set>
 <set local name=mydir><subst match="[^/]* " result=""><get name=myspool> </subst></set>
 <set local name=myfile><subst match="&mydir;" result=""><get name=myspool></subst></set>
 <set local name=inurl><get name=url element></set>
 <set local name=doc><get name=parseddoc element></set>
 <set local name=iwidth><get name=imgwidth element></set>
 <if><get name=iwidth><else><set name=iwidth>100</set> </else> </if>
  <set local name=otherstuff>&content;</set>
 <set name="gifs" local><dummy><read directory file=&mydir;
    match="&myfile;.*&gsext;"></dummy></set>
    <table border="1" width="100%">
  <tr><th>URL: </td><td colspan=2><get name=inurl></td></tr>
   <if><get name="doc">  
  <then>  <tr><th> Title </th> <td><get index=doc.html.head.title.Text> </td> </tr>
  <set name="anchors" local><anchors><get name=doc findall=a></anchors></set>
  <tr><th rowspan=3>Info:</th><td> Number of links:</td><td> <get name=anchors size > </td>
   <set name=base local><get name=doc findall=meta></set>
   <if><get name=base><else><set name=base><base href=&inurl;></else></if>
   <write file=&myspool;.links><get name=base>
  <get name=anchors></write>
</tr>
<tr><td>    Headings: </td><td> <set name=h><dummy><get name=doc
findall=h1></dummy></set>
<repeat list=&h; entity=myh><get index=myh.-1-><br></repeat>
   </td> </tr>
 </then> </if>
   <tr><td colspan=3>
   <if> <get index=gifs.><then>
   <repeat list="&gifs;" entity=i>
   <a href=/~&agentName;/&mydir;&i;><img
	  src=http://&piaHOST;:&piaPORT;/~&agentName;/&mydir;&i; width=&iwidth;></a>
   </repeat>
    </then></if>
   </td> </tr>
  </table>
  <if><get name="otherstuff">  <then><get name="otherstuff">  </then> 
   </if>

</actor>

<actor tag=print-bar empty desc="puts in all of the possible options for printing a
spool file">
<set name=file local><get element name=spoolfile></set>
<set name=myurl local><get element name=url></set>
<set name=mystyle local><get element name=style></set>
<table><tr>
<td>
<form action="http://&piaHOST;:&piaPORT;/&agentName;/print.if" method=post>
<input name="URL" value="&myurl;" type=hidden>
<input name="spoolfile" value="&file;.ps" type=hidden>
<input type="SUBMIT" value="Print">
</form>
</td>
<td><form action="http://&piaHOST;:&piaPORT;/&agentName;/previous.if" method=post>
<input name="URL" value="&myurl;" type=hidden>
<input name="spoolfile" value="&file;.ps" type=hidden>
<input type="SUBMIT" value="change style to:"><select name=newstyle><option> </option>
 <style-option-list url=&myurl;>
</select>

</form>
 </td>
<td>
<form action="http://&piaHOST;:&piaPORT;/&agentName;/append.if" method=post>
<input name="URL" value="&myurl;" type=hidden>
<input name="spoolfile" value="&file;.ps" type=hidden>
<input name="style" value="&mystyle;" type=hidden>

<input type="SUBMIT" value="add referenced pages">
</form>
 </td>

</table>
</actor>


<actor tag=page-footer desc="footer shown at the bottom of pages">
&content;
<table width=100%>
<tr><td align=center>  <a href="/&agentName;/preview.if">
        <img src="/&agentName;/preview.gif"
                   height=32 width=32 border=0 alt=""></a> <br>
    <a href="/&agentName;/preview.if">Preview</a> </td>
 <td align=center>    <a href="/&agentName;/print.if">
        <img src="/&agentName;/printer.gif"  height=32 width=32 border=0 alt=""></a><br>
    <a href="/&agentName;/print.if">Print</a> </td>
<td align=center><a href="/&agentName;/previous.if"><img src="/&agentName;/previews.gif"
        height=32 width=32 border=0 alt=""></a><br>
        <a href="/&agentName;/previous.if">Previous </a> </td>
<td align=center>        <a href="/&agentName;/mkbook.if">
        <img src="/&agentName;/bookmark.gif"  height=32 width=32 border=0></a>
<br>    <a href="/&agentName;/mkbook.if">Create book</a><br>
</td>
<td><img src="/Agency/Icons/dot-blue.gif" border=0 height=15 width=15>
<a href=status.if>Status</a><br>
<img src="/Agency/Icons/dot-blue.gif" border=0 height=15 width=15>
<a href=options.if>options</a><br>
<img src="/Agency/Icons/dot-blue.gif" border=0 height=15 width=15>
  <a href="/&agentName;/toggle_page_controls.if"> Toggle  proxy bar</a><br>
<!-- a href=test.if test a !--><br>
</td></tr></table>

  </actor>



<actor tag=create-thumbnail desc="">
  <set local name=myfile><get name=psfile element></set>

 
<os-command>gs -sDEVICE=gif8 -r72 -sOutputFile=&myfile;%d.gif &myfile; </os-command>
  

</actor>


<actor tag="">

</actor>


<actor tag="set-lastlink" empty desc="sets the value of last link to whatever the
  history agent says was last">
  <if>
  <read file="&usrDIR;/History/last.html" info>
  <then><set name="lastfile"><read file="&usrDIR;/History/last.html"
  process></set> 
  <set name="lastlink"><get name=lastfile findall=a></set>
  </then></if>
</actor>

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
