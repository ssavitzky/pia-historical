package PIA::Content; ###### content of a PIA::Transaction
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
###
###	content objects sit between inputs and outputs
###	and act as interfaces for different types of content


sub new {
    my($class)=@_;
    my $self={};
    bless($self,$class);
    $self->initialize;
    
    return $self;
}

##setup arrays, flags
sub initialize{
    my($self)=@_;
    $$self{_start_hooks}={};    # called start of stream
    $$self{_end_hooks}={};	# called at end of stream
    $$self{_hooks}={};		# called foreachpiece (arbitrary )
    $$self{_started}=0;
    $$self{_ended}=0;
    $$self{_make_copy}=1;	# create copy of bytes?
    $$self{_buffer}=[];    
    return $self;
}

sub  source{
    my($self,$source)=@_;
    $$self{_source}=$source if $source;
    return $$self{_source};
}

## add function to process content as it comes in in  chunks
## hook should return 1 if it wants to process future  chunks
##  hook is run  immediately if content has already been retrieved

sub add_hook{
    my($self,$hook,$key,$start)=@_;
    $start='_hooks' unless $start;
    my $hooks=$$self{$start};
    if(! $key){
	$key=1;
	while(exists $hooks{$key}){
	    $key+=1;
	}
    }
    ##run hook if alreadyhave content
    if($$self{_ended} && $start ne '_start_hooks'){
	my $newhooks;
	$newhooks{$key}=$hook;
	my $data;
	for $data (@{$$self{_buffer}}) {
	    $self->run_hooks(\$newhooks,$data);
	}
    }
    $hooks{$key}=$hook;
    return $key;
}

#actually run the hooks.Order not specified at this time 
# 0 return means to delete hook, 1 means run next time
sub run_hooks{
    my($self,$hooks,@arguments)=@_;
    $hooks=$$self{'_hooks'} unless $hooks;
    my $flag,$key,$hook;
    while(($key,$hook) = each %{$hooks}) {
	$flag=&$hook(@arguments);
	delete $$hooks{$key} unless $flag;
    }
}


sub add_start_hook{
    my($self,$hook,$key)=@_;
    return $self->add_hook($hook,$key,'_start_hooks');
}

sub add_end_hook{
    my($self,$hook,$key)=@_;
    return $self->add_hook($hook,$key,'_end_hooks');
}

sub string{
    my($self,$string)=@_;
    $$self{_bytes}.=$string if $string;
    return $$self{_bytes};
}

sub as_string{
    my($self,$string)=@_;
    return $self->string($string) if $string;
    while(! $self->is_at_end){
	$self->pull;
   }
    return $self->string;
}

sub is_at_end{
    my($self)=@_;
    return $$self{_ended} || ! $self->source;
}
sub end{
    my($self,$argument)=@_;
    	$self->run_hooks($$self{_end_hooks});
	$$self{_ended}=1;
}

##incoming data
## no data signals end
sub push{
    my($self,$data)=@_;

    if(!$$self{_started}){
	$self->run_hooks($$self{_start_hooks});
	$$self{_started}=1;
    }
    if(!$data && !$$self{_ended}){
	$self->end;

	return;
    }
    $self->run_hooks($$self{_hooks},$data);
    push(@{$$self{_buffer}},$data);
    $self->string($data) if $$self{_make_copy};
}

sub pull {
    my($self,$argument)=@_;
    return unless $self->source;
    my $more=$self->source->has_more_data;

    return $self->end unless $more;

    ## === This point never appears to be reached! 

    my $bytes=$self->source->read_chunk($self);
    print "read $bytes content\n" if $main::debugging;
}

#outgoing data
sub shift {
    my($self)=@_;
    my $foo=shift(@{$$self{_buffer}});
    return $foo if $foo;
    ##get more content if not at end
    if(!$self->is_at_end && $self->source){
	my $status=$self->pull;
	$foo=shift(@{$$self{_buffer}}) if $status;
    }
    return $foo;
}
1;
