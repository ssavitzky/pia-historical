

# return a pointer to actual book (postscript or images)
# maybe return pointer to show book.if

sub make_book{
    my($self,$url)=@_;
    
    my $book_depth=$self->option('book_depth');
    $book_depth=20 unless $book_depth;
    $self->option('book_depth',$book_depth);
    local @book_new_links;
    $book_base=URI::URL->new($url);
    local $book = BOOK->new($book_base,$book_depth);
    
    $self->current_book($book);
    
    my $element=IF::IT->new('a', href => "showbook.if");
    $element->push("link to book...");
#    my $element=book_toc($book);
    return $element;

}

sub end_book{

}

sub current_book{
    my($self,$book)=@_;
    if ($book){
	$self->end_book;
	$$self{_current_book}=$book;
    }
    
#     if (! $book){
#create initial element
#	$$self{_current_book}=BOOK->new;
#    }
     return $$self{_current_book};
 }

sub book_ps{

    require "html2latex.pl";

    my($self,$book)=@_;
    return "no book in progress\n" unless $book;
    #createpostscript for book
    my $html=$book->html;
#toc added in?
#$html->push($book->table_of_contents);
    
    my @files=file_names($self);
        my $image_file=shift @files;
    my $ps_file=shift @files;

    my $directory=$self->option('tempdirectory');
    $directory=$self->agent_directory . "/temp" unless $directory;
    
#    my $status=html2latex($string,$ps_file,$book->base,"011",$directory);
##011 is dummy docid
#    print "latex status is $status \n" if $main::debugging;
    if($self->option('render_method') =~ /latex/i){
	my $status=html2latex($html,$ps_file,$book->base,011,$directory);
    }else {
#    require HTML::FormatPS;
	require "Format_PS.pm";
	my $f = HTML::Format_PS->new;
	open(PSFILE,">$ps_file");
	print "putting ps in $ps_file\n";
#    print PSFILE $f->format_book($book);
	print PSFILE $f->format($html);
	close PSFILE;
    }

    return $self->generate_preview_element($image_file,$ps_file);
	

}

1;
