###  class for content of transactions
# content objects sit between inputs and outputs
# actsas interface for different types of content

package CONTENT;


sub  new{
    my($class)=@_;
    my $self={};
    bless($self,$class);
    $self->initialize;
    
    return $self;
    
}

##setup arrays, flags
sub initialize{
    my($self)=@_;
    $$self{_start_hooks}={};    ##called start of stream
    $$self{_end_hooks}={};    ##called at end of stream
    $$self{_hooks}={};    ##called foreachpiece (arbitrary )
    $$self{_started}=0;
    $$self{_ended}=0;
    $$self{_make_copy}=1;  #create copy of bytes?
$$self{_buffer}=[];    
    return $self;
    
}

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
    $hooks{$key}=$hook;
    return $key;
}

#actually run the hooks.Order not specified at this time 

sub run_hooks{
    my($self,$hooks,@arguments)=@_;
    $hooks=$$self{'_hooks'} unless $hooks;

    foreach $hook (values %{$hooks}){
	&$hook(@arguments);
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


##incoming data
## no data signals end
sub push{
    my($self,$data)=@_;
    if(!$$self{_started}){
	$self->run_hooks($$self{_start_hooks});
	$$self{_started}=1;
    }
    if(!$data && $$self{_ended}){
	$self->run_hooks($$self{_end_hooks});
	$$self{_ended}=1;
	return;
    }
    $self->run_hooks($$self{_hooks},$data);
    push(@{$$self{_buffer}},$data);
    $self->string($data) if $$self{_make_copy};
    
}

#outgoing data
sub shift{
    my($self)=@_;
    return shift(@{$$self{_buffer}});
    
}
1;
