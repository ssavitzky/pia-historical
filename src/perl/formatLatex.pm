
package HTML::FormatLatex;
#format html into latex

require HTML::Formatter;
push (@ISA,qw(HTML::Formatter));

$main::latex_column_width=3.5;


# 4 images attach source file for postscript...
 # everything else should format itself
sub new{
    my( $class,$self,$argument)=@_;
    $self={} unless $self;
    bless $self,$class;
    $self->initialize;
    
    return $self;
    
}


    $main::format_options{_columns}=1;
    $main::format_options{_footnotes}=0;
    $main::format_options{_table_contents}=0;
    $main::format_options{_font_size}=11;
    $main::format_options{_type}="article";
    $main::format_options{_column_width}="7";
$main::format_options{_logo}="/home/pia/pia/src/Agents/printer/logo.ps";

sub initialize{
    my($self,$argument)=@_;
    for(keys(%main::format_options)){
	$$self{$_}=$main::format_options{$_};
    }
#domargins...
}
#return latex string
sub format{
    my($self,$html)=@_;

    ##setup locals for various things
    local $footnotes=$$self{_footnotes};
    local $tableofcontents=$$self{_table_contents};
    

    if(ref($html) eq "BOOK"){
	return $self->format_book($html);
    }
    $self->preprocess($html);
    
    my $string=$self->header;
    $string.=latex($html);
    $string.=$self->footer;
    return $string;
}

sub format_book{
    my($self,$book)=@_;
    my $referencecounter=1;
    local %latex_start=%latex_start;
    $latex_start{title}="\\section{";
 
#review link references   
    my $html=$book->html;
    
    $self->preprocess($html);
    
    my $string=$self->header;
    $string.=latex($html);
    $string.=$self->footer;
#    $latex_start{title}=$title_action;
    return $string;
}


sub preprocess{
    my($self,$argument)=@_;
    #do images here,referencesandlinks
}
sub header{
    my $self=shift;
    
    my $string="\\documentstyle[psfig";
    $string.="," . $$self{_font_size} . "pt";
    $string.=",twocolumn" if $$self{_columns} == 2;

    $string.="]{" . $$self{_type} . "}\n";
$string.=" \\addtolength{\\textwidth}{3.5cm}\\addtolength{\\textheight}{4.2cm}\\addtolength{\\topmargin}{-1.5cm}\\setlength{\\oddsidemargin}{-.75cm}\\setlength{\\evensidemargin}{-.75cm}";
    $string.="\\begin{document}\n"; 

    return $string;

}

sub footer{
    my $self=shift;
    my $string;
    $string="\\psfig{file=" . $$self{_logo} .",height=.25in}\n" if -e $$self{_logo};
    $string.="\n\\end{document}\n";
    return $string;
    

}


##realwork done here
####LATEX
sub latex_string{
    #protect from latex
    s/<|>/\$$&\$/g;
    s/\^/\\\^\{\}/g;
    s/\\/\$\\backslash\$/g;
    s/\$|\{|\}|\%|\&|\#|\_|\~/\\$&/g;
    return $_;
}

sub latex_start{
    my($self,$argument)=@_;

    return $latex_start{$self->tag} if exists $latex_start{$self->tag};
    #default nothing
#    print "no latex for " . $self->tag;
    return "";
}
sub latex_end{
    my($self,$argument)=@_;
    return $$self{_latex_end} if exists $$self{_latex_end};
    
    return $latex_end{$self->tag} if exists $latex_end{$self->tag};
    #default close { if start
    if( exists $latex_start{$self->tag}){
	my $start=$latex_start{$self->tag};
	my $end;
#	print "start is $start ";
	if ($start =~ /\\begin\{(.+)\}/){ #begins
	    $end="\\end{$1}" ;
	} else {
	    $end="}" if $start=~/(\{$)|(^\{\\[^\}]*$)/;  #uneven braces
	    $end="]" if $start=~/\[$/;  #uneven braces
				    }
	return $end if $end;
    }
    return "";

}

 # latex stuff
sub latex{
    my($self,$ignore_functions)=@_;
    #print "latexing ".$self->tag ."\n" if $main::debugging;
    return latex_function($self) if !$ignore_functions && exists $latex_functions{$self->tag};
    my $string;
    $string=latex_start($self);
    ## Convert a token to latex
    

    my $content = $self->content;
    if (defined $content) {
	for (@$content) {
	    if (ref($_)) { 
		if(ref($_) eq 'HTML::Element') {
		    $string .= $_ -> as_HTML; } 
		else {$string .= latex($_)}
	    }else { $string .= latex_string($_); 
	    ## Note that we need as_HTML because legacy code is still
	    ## generating HTML::Element's
		}
	}
    }
    $string .= latex_end($self);
    return $string;
}

sub latex_function{
    my($self,$argument)=@_;
    my $function=$latex_functions{$self->tag};
    return unless ref($function) eq 'CODE';
    my $result=&$function($self);
    return $result;
}


sub latex_a{
##need to add ref code for anchors
    my($self)=shift;
#    my $string="\\htmladdnormallink{" ;
    my $string;
    $string.=latex($self,1);
    my $url=$self->attr('href');
    my $book_reference=$self->attr('book_reference');
    if($book_reference){
	$string.="[\\ref{" . $book_reference . "}]";
    }
    if($url){
	my $count=$footnote{$url};
	$count=$footnotecounter++ unless $count;
	$footnote{$url}=$count;
	$string.= "\\footnotemark[$count]" if $footnotes;
    }
    
    return $string;
    
}

sub latex_title{
    my $tag=shift;
    my $string;
    $string.=latex($tag,1);
    my $book_reference=$tag->attr('book_reference');
    if($book_reference > 1){
	$string="\\section{" . $string . "}\n \\label{" . $book_reference . "}";

    }else {
	$string="\\title{" . $string . "}\n";
	$string.="\\maketitle \n";
	$string.="\\tableofcontents \n" if $tableofcontents;
    }
    return $string;
	
}
sub latex_table{
    my($tag)=shift;
#count max number of columns
    my $max=0;
    my (@rows)=@{$tag->content};
    my $colcount;
    for(@rows){
	$colcount = 0;
	if( ref($_)){
	    for(@{$_->content}){
		if(ref($_) eq "IF::IT" && $_->tag eq 'td'){
		    $colcount++;
		}
	    }
	}
	print "columns =" . $colcount ."\n";
	
	$max=$colcount if $max<$colcount;
    }
    my $border="";
    $border="|" if $tag->attr('border');
    my $align=$tag->attr('align');
    my $character="c";
    $character = "l" if $align eq 'left';
    $character = "r" if $align eq 'right';
    my $tab=$border;
    for($count=0;$count<$max;$count++){
	$tab.=$character . $border;
    }
    
    my $string="\\begin{tabular}{$tab}";
    $string.=latex($tag,1) . "\\end{tabular}";
    return $string;
}
sub latex_table_cell{
    $self=shift;
    my $string;
##do spanshere  \multicolumn{n}{c}{...}
    $string.=latex($self,1);
    $string.= "&";
    return $string;
    
}
sub latex_image{
    $self=shift;
    my $width=$self->attr('width');
    my $height=$self->attr('height');
    my $source=$self->attr('src');
    my $file=$self->attr('psfile');
    my $string="\\psfig{figure=$file";
    if(!$file){
	my $alternate=$self->attr('alt');
	$alternate="[IMAGE]" unless $alternate;
	return $alternate;
    }
    my $column_width=$main::format_options{_column_width};
    
    if($width > $column_width*72){
	$string.=",width=" . $column_width . "in" ;
    }
    #need to scale ... $string.=" width=$width" if $width;
    #$string.=" height=$height" if $height;
    $string.="}";
    return $string;
}


#mapping from tags to latex

# Elements that might contain links and the name of the link attribute
#needtodo something about functions that use attributes
 %latex_functions =
(
title => \&latex_title ,
 a => \&latex_a ,
 img => \&latex_image ,
 table => \&latex_table ,
th => \&latex_table_cell ,
td => \&latex_table_cell 
 );


 %latex_start = (
 base   => "\\author{",
 form   => "",
 input  => "",
 frame  => "",
 applet => "",
 area   => "",
 h1 => "\\section{",
 h2 => "\\subsection{",
 h3 => "\\subsection{",
 h4 => "{\\subsubsection{",
 h5 => "{\\large ",
 h6 => "{\\bold ",
 p => "\n\n",
address => "{\\small ",
ul => "\\begin{itemize}",
ol => "\\begin{enumerate}",
li => "\\item ",
dl => "\\begin{description}",
dt => "\\item[",
dd => " ",
pre => "\\begin{verbatim}",
tt => "{\\tt ",
b => "{\\bf ",
 tr => "",
em => "{\\em ",
strong => "{\\sc ",
code => "{\\tt ",
blockquote => "\\begin{quote}",
center => "\\begin{center}",
hr => "\n\n\\hrule\n",
br => "\\\\",
'!' => '%',
'!--' => '%'
 );
 
%latex_end=(
 tr => "\\\\\n"
	    );



1;
