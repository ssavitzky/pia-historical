<set name=calname>&month;.&year;.html</set>
<if><read file="calendars/&calname;" info> <then>
 <set agent name=currentcal><read file="calendars/&calname;" process></set>
</then> <else>
<set agent name=currentcal><calendar month=&month; year=&year;></set>
</else>
</if>
