<!-- MB3 actors:				 -*-html-helper-*- -->
<!-- Copyright 1998 Ricoh Silicon Valley			   -->
<!-- tags to convert forms of into latex  -->

<tagset name=latex-actors base=Standard><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=latex-actors>

 <actor tag=body desc="">
\begin{document} 
&content;
\end{document} </actor>

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

 <actor tag=li desc="">\item &content; </actor>
 <actor tag=dt desc="">\item[&content;]</actor>
 <actor tag=dd desc="">&content;</actor>

<!-- should  footnote -->
 <actor tag=a desc="">
<set name=foo local>&content;</set>
 
<if><get element name=include> <then>
<set name=f><get name=href element></set>
\section{&foo;}
reading &f; given &base;<read file=&base;&f; process tagset=latex-actors-subdoc base=&base;>
</then> <else>&foo;</else> </if></actor>


 <actor tag=b desc="">\bold{&content;}</actor>
 <actor tag=strong desc="">\bold{&content;}</actor>
 <actor tag=br desc="">\\</actor>
 <actor tag=hr desc="">\hrule</actor>
 <actor tag=address desc=""></actor>

 <actor tag=blockquote desc="">\quote{&content;}</actor>
 <actor tag=code desc="">\tt{&content;}</actor>
 <actor tag=caption desc="">\caption{&content;}</actor>

 <actor tag=img desc=""> 

IMAGE GOES HERE <get name=src element></actor>

<actor tag=form desc="">\begin{verbatim}
&content;
\end{verbatim} </actor>
<actor tag=textarea desc="">\begin{verbatim}
&content;
\end{verbatim} </actor>

 <actor tag=input desc="">________________</actor>
 <actor tag=table desc=""> 
\begin{table}
&content;
\end{table} 
</actor>
 <actor tag=center desc="">\begin{center}
&content;
\end{center} </actor>

<!-- 
  static String phraseTags = "cite code em kbd samp strong var b i u tt " +
  "a img br hr wbr nobr center blink small big font basefont table";
  
  static String listTags 	= "ul ol dir menu";
  static String tableTags 	= "tr td th caption";
  static String tableRowTags 	= "td th caption";
  static String formTags 	= "input select option textarea";
-->
