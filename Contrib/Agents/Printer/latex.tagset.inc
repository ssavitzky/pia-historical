<!-- MB3 actors:				 -*-html-helper-*- -->
<!-- Copyright 1998 Ricoh Silicon Valley			   -->
<!-- tags to convert forms of into latex  -->

<!-- after conversion, still need to remove comments, $ into \$ and some & into \& -->

<tagset name=latex-actors base=Standard><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=latex-actors>

 <actor tag=body desc="">
\begin{document} 
&content;
\end{document} </actor>

<!-- should do a find all and remove comments and SGML declarations -->
 <actor tag=html desc="">\documentclass{article}
\usepackage{psfig}
&content;
</actor>

 <actor tag=head desc=""> </actor>

 <actor tag=abstract desc="">\begin{abstract}&content;\end{abstract}</actor>

 <actor tag=h1 desc="">\section{&content;} </actor>
 <actor tag=h2 desc="">\section{&content;} </actor>
 <actor tag=h3 desc="">\section{&content;} </actor>
 <actor tag=h4 desc="">\subsection{&content;} </actor>
 <actor tag=h5 desc="">\subsection{&content;} </actor>
 <actor tag=h6 desc="">\subsection{&content;} </actor>
 
<actor tag=p desc="">

&content; 

</actor>

 <actor tag=dl desc="">\begin{description}
&content;
\end{description} </actor>


 <actor tag=ul desc="">\begin{itemize}
&content;
\end{itemize} </actor>


 <actor tag=ol desc="">\begin{enumerate}
&content;
\end{enumerate} </actor>

 <actor tag=li desc="" not-inside="li">\item &content; </actor>
 <actor tag=dt desc="" not-inside="dt dd">\item[<subst match="\n" result="">&content;</subst>]</actor>
 <actor tag=dd desc="" not-inside="dt dd">&content;</actor>

<!-- should  footnote  and be tricky with labels.-->
 <actor tag=a desc=""><set name=foo local>&content;</set><set name=f><subst match="#.* " result=""><get name=href element> </subst></set><if><get element name=include> <then>
\section{&foo;}
%% DOC <read file=&base;&f; process tagset=latex-actors-subdoc base=&base;>
</then> <else>{\sf &foo;<if><get name=f><then>\footnote{&f;}</then></if>}</else></if></actor>


 <actor tag=b desc="">{\bf &content;}</actor>
 <actor tag=strong desc="">{\bf &content;}</actor>
 <actor tag=em desc="">{\em &content;}</actor>
 <actor tag=i desc="">{\it &content;}</actor>
 <actor tag=it desc="">{\it &content;}</actor>
 <actor tag=dfn desc="">{\sc &content;}</actor>
<!-- cite is wrong... -->
 <actor tag=cite desc="">\cite{&content;}</actor>
 <actor tag=br empty desc=""> \\</actor>
 <actor tag=hr empty desc="">\hrule</actor>
 <actor tag=address desc=""></actor>

 <actor tag=blockquote desc="">\begin{quote}&content;\end{quote}</actor>
 <actor tag=code desc="">{\tt &content;}</actor>
 <actor tag=tt desc="">{\tt &content;}</actor>
 <actor tag=tt desc="">{\tt &content;}</actor>
 <actor tag=caption desc="">\begin{figure}\caption{&content;}\end{figure}</actor>

 <actor tag=img empty desc=""> 
%%IMAGE GOES HERE <get name=src element></actor>

<actor tag=form desc="">\begin{verbatim}
&content;
\end{verbatim} </actor>
<actor tag=textarea desc="">\begin{verbatim}
&content;
\end{verbatim} </actor>



<actor tag=pre desc="">\begin{verbatim}
&content;
\end{verbatim} </actor>

<actor tag=textarea desc="">\begin{verbatim}
&content;
\end{verbatim} </actor>

 <actor tag=input desc="">________________</actor>

<actor tag=center desc="">\begin{center}&content;\end{center} </actor>

<!-- must deal with rows and columns here -->
<!-- embedding does not work -->
<actor tag=table desc="">
 <set name=stuff local>&content;</set>
 <set name=myrows local><get name=stuff findall=tr></set>
<set name=maxcol>1</set>
<repeat list=&myrows; entity=r> <set name=mycols><get index=r.td></set>
 <set name=myheads><get index=r.th></set>
 <set name=myel ><sum digits=0> <get name=myheads size> <get name=mycols size> </sum></set>
 <if><test positive> <difference>  &myel; &maxcols;</difference> </test>
 <then><set name=maxcol>&myel;</set></then> </if>
</repeat>
 <set name=fraction local><quotient digits=2> 1 &maxcol; </quotient></set>
 <if><get name=border element> <then><set name=cols>{<repeat start=1
 stop=&maxcol;>|p{&fraction;\textwidth}</repeat>|l}</set></then>
<else><set name=cols>{<repeat start=1 stop=&maxcol;>p{&fraction;\textwidth}</repeat>l}</set></else> </if>
%% cols &maxcol;, rows <get name=myrows size>
\begin{table}
\begin{tabular}&cols;
<repeat list=&myrows; entity=r><show-row row=&r;> \\</repeat>
\end{tabular}
\end{table} 
</actor>

 <actor tag=show-row empty desc=""><set name=amp>&</set> <set name=row
 local><get element name=row></set> <repeat list=&row; entity=td> <if> <get
 name=td attr=tag>
 <then> <if><get name=td attr=colspan> <then> <set name=mcols local><get name=td attr=colspan></set>\multicolumn{&mcols;}{c}{<get index=td.1- >}</then> <else><get index=td.1- ></else> </if></then> </if>&amp;</repeat></actor>


<!-- 
  static String phraseTags = "cite code em kbd samp strong var b i u tt " +
  "a img br hr wbr nobr center blink small big font basefont table";
  
  static String listTags 	= "ul ol dir menu";
  static String tableTags 	= "tr td th caption";
  static String tableRowTags 	= "td th caption";
  static String formTags 	= "input select option textarea";
-->
