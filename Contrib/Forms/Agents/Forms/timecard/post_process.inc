<!-- add this to current department timecards listing -->

 <set name=myman><manager user=&auser;></set>
 <set name=mytimesfile>timecards/&year;/<get name=paymonth>/<get name=payday>/<get
 name=myman>.dat</set>

 <write file=&mytimesfile; append>
 <timecard user=&auser; month=&paymonth; paydays=&payday; form=&formurl;.html>
 <repeat start=1 stop=16 entity=myli><td>
<if><get name=personal&myli;> <then>H<get name=personal&myli;></then> </if>
<if><get name=vacation&myli;> <then>V<get name=vacation&myli;></then> </if>
<if><get name=sick&myli;> <then>S<get name=sick&myli;></then> </if>
<if><get name=other&myli;> <then>O<get name=other&myli;></then> </if>
</td></repeat> <td><get name=notes>  </td > </timecard >
 </write>
