package PIA_AGENT::FILTER;

push(@ISA,PIA_AGENT);

# this is an agent that filters (responses)

sub initialize {
    my $self = shift;
#by default filter only responses
    $self->match_criterion('response',1,\&FEATURES::is_response);
    $self->match_criterion('agent_response',0,\&FEATURES::is_agent_response);

#need factory for translators
    my $translators={};
    $$self{translators}=$translators;
    
    return $self;
}


############################################################################

#handles are done by super class-- things like processing interform

# here  we translate response
sub  new_requests{
    print "translate request \n" if $main::debugging;
    my($self,$request)=@_;
    my $type=$request->content_type();
    my $translator=$self->translator($type);
    my $code=ref($translator);
    print "translator is $code\n"  if $main::debugging;
    &{$translator}($request) if ref($translator) eq 'CODE';
#usually nothing to return    
    return ();
}

sub translator{
    my($self,$type,$code)=@_;
    my $translators=$$self{translators};
    $$translators{$type}=$code if defined $code;
    return $$translators{$type} if exists $$translators{$type};
    #default is *
    return $$translators{'*'};
    
}
sub remove_translator{
    my($self,$type)=@_;
    my $translators=$$self{translators};
    delete $$translators{$type};
}

1;
