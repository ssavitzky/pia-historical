<!--  add &urlatag to the specified category cat -->

 <set name=catobj"><read file="categories/&cat;" process></set>
 <!-- new category? -->
 <if><get name=catobj> <then></then> <else>
<!-- should add category -->
 <set name=catobj"><h1> Category: &cat;</h1> <ul linksin=&cat;></ul></set>
  </else></if>
  <set name=alist><get name=catobj findall=ul></set>
<!--  just add to any ul for now -->
   <set index=alist insert=-1><li>&urlatag; <get form name=annotation></li></set>
 <write file="categories/&cat;><get name=catobj></write>
