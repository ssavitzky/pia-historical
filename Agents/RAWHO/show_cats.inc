 <set name=allcats><get agent index=cats keys></set>
<!-- create a td for each top level category -->
 <set name=cols>2</set>
 <set name=cur>1</set>
 <set name=tbl><table align=center> <caption><h3> Top level Categories</h3></caption> </table></set>
<set name=row><tr> </tr></set>
<set name=tbl insert=-1><get name=row></set>

<repeat list=&allcats; entity=catk>
<set name=cat><get agent index=cats..dl key=&catk;></set>
  <if><get index=cat.superCategory> <then><!-- skip it --></then>
   <else>
    <set name=row insert=-1><td align=top> <b><a
    href="show_category.if?catname=&cat.name;"><get index=cat.name></a> </b>
    <if><get index=cat.subCategory><then>
    <br><font size=-1><repeat list=&cat.subCategory; entity=c><a href="show_category.if?catname=&c;"><get index=c></a>
        </repeat></font></then></if>

</td></set>
   <set name=cur> <sum> &cur; 1</sum></set> 
    <if> <test  positive> <difference> &cur; &cols;</difference></test>
 <then><set name=cur>1</set> <set name=row><tr> </tr></set>
<set name=tbl insert=-1><get name=row></set></then></if>
  </else></if>
 </repeat>
<center><get name=tbl></center>
