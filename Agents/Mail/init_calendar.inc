<set name=calname>&year;/&month;/cal.html</set>
<if><read file=&calname; info> <then>
 <set agent name=currentcal><read file=&calname; process></set>
</then> <else>
     <set agent name=currentcal><calendar month=&month; year=&year;></calendar></set>
     
     <repeat start=01 stop=31 entity=i>
          <set name=indexFile>&year;/&month;/&i;/index.html</set>
	  <if><read file=&indexFile; info> <then>
	       <set name=dls><read file=&indexFile; process findall=dl></set>
	       <foreach list=&dls; entity=entry>
	           <get index="entry.keys">
		   <get index="entry.values">
	           <set name=dummy><calendar-day cal=&AGENT.currentcal; day=&i;>&entry;</calendar-day></set>
	       </foreach>
	  </then>
	  </if>
     </repeat>
     &AGENT.currentcal;

</else>
</if>


