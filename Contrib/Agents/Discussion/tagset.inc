<!-- Disc actors:				 -*-html-helper-*- -->
<!-- Copyright 1998 Ricoh Silicon Valley			   -->
<!-- This file contains the set of active tags used by this agent.
     Normally the file will be read and the contained tags (actors) defined
     at initialization time.  These tags are in addition to the standard
     tagset for agents (e.g. InterForms). -->

<!-- === The tagset actor does not work quite right ===	   -->
<tagset name=DISC-actors base=Standard><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=DISC-actors>


<!-- by convention, topicbases are dl's with descriptive objects (eg. "topic")
as dd value -->
<actor tag=load-topic empty desc="load topic into agent from file TOPIC">
<set local name=topicname><get element name=topic></set>
<set agent name=topics insert=-1><li><get element name=topic></li></set>
<if><read file=topics/&topicname; info> <then>
    <set agent name=&topicname;><read file=topics/&topicname; process></set>
    </then> <else><!-- initialize topic as dl -->
    <set agent name=&topicname;><dl> </dl></set>
    </else></if>
</actor>

<actor tag=save-topic empty desc="save TOPIC from agent into file TOPIC">
<set local name=topicname><get element name=topic></set>
<if><read file=topics/&topicname;.lck info> <then> <h2> &topicname; file is LOCKED
</h2>Please  report this error</then> <else>
<write file=topics/&topicname;.lck>Lock file for &topicname; on &date;</write>
<file name=topics/&topicname; dst=topics/&topicname;.bak rename></file>
<write file=topics/&topicname;><get agent name=&topicname;></write>
<file file=topics/&topicname;.lck remove></file>
</else> </if>
</actor>

 <actor tag=add-topic empty desc="add a new topic">
<set local name=topicname><get element name=topic></set>
<set agent name=topics insert=-1><li><get element name=topic></li></set>
<set agent name=&topicname;><dl> </dl></set>
<save-topic topic=&topicname;>
 </actor>

<actor tag=add-topic-entry       desc="add a new entry for topic">
<set local name=topicname><get element name=topic></set>
<set local name=entryname><get element name=entry></set>
<set local name=entryurl><get element name=url></set>

<set agent name=&topicname; key=&entryname;>
 <entry> <entryname>&entryname;</entryname>
          <entryurl><get name=entryurl></entryurl>
 <entrydescription>&content;</entrydescription>
</entry>
</set>
<save-topic topic=&topicname;>
 </actor>


 <actor tag=show-topic  empty desc=" displays this topic">
<set local name=topicname><get element name=topic></set>
<set local name=brief><get element name=type></set>
<set local name=mytopic><get  agent name=&topicname;></set>
 <set local name=entries><dummy><get name=mytopic values></dummy></set>
 <if> <get name=entries> <then>
 <repeat list=&entries; entity=mye>
 <a href=&mye.entryurl.Text;>  <get  index=mye.entryname></a><br>
 <if> <get name=brief> <then></then> <else> <get  index=mye.entrydescription> <br></else> </if>
 </repeat>
</then> <else></else> </if>
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

<!-- load options and from content into agent name space -->
 <actor tag=load-options desc="load options and from content into agent name
 space ">
 <set name=options local><dummy><get name=content
 findall=agentoption></dummy></set>
 <repeat list=&options; entity=myoption>
 <set agent name=&myoption.optionname;><get index=myoption.optionvalue></set>
 </repeat>
</actor>
