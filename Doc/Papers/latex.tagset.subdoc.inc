<!-- MB3 actors:				 -*-html-helper-*- -->
<!-- Copyright 1998 Ricoh Silicon Valley			   -->
<!-- tags to convert forms of into latex  -->

<tagset name=latex-actors-subdoc base=latex-actors><!-- ignored: buggy --></tagset>
<!-- now load the tagset so that definitions are added to it -->
<tagset-load name=latex-actors-subdoc>

<!-- redefine top tags -->
 <actor tag=html desc="">&content;</actor>
 <actor tag=body desc="">&content;</actor>


 <actor tag=h1 desc="">\subsection{&content;} </actor>
 <actor tag=h2 desc="">\subsection{&content;} </actor>
 <actor tag=h3 desc="">\subsection{&content;} </actor>
 <actor tag=h4 desc="">\subsubsection{&content;} </actor>
 <actor tag=h5 desc="">\subsubsection{&content;} </actor>
 <actor tag=h6 desc="">\subsubsection{&content;} </actor>
 
