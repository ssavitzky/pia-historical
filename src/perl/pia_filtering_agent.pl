package PIA_AGENT::FILTER;

push(@ISA,PIA_AGENT);

# this is an agent that filters (responses)

sub initialize {
    my $self = shift;
#by default filter only responses
    $self->match_criterion('response',1);
    $self->match_criterion('agent_response',0);

#need factory for translators
    my $translators={};
    $$self{translators}=$translators;


    &PIA_AGENT::initialize($self);
    
    return $self;
}


############################################################################

#handles are done by super class-- things like processing interform

# here  we translate response
sub  act_on {
    print "translate request \n" if $main::debugging;
    my($self, $request, $resolver)=@_;
    my $type=$request->content_type();
    my $translator=$self->translator($type);
    my $code=ref($translator);
    print "translator is $code\n"  if $main::debugging;
    &{$translator}($request) if ref($translator) eq 'CODE';
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
