
package HTML::Format_PS;
#A subclass to do all the thing that are broken in library

require HTML::FormatPS;
@ISA=qw(HTML::FormatPS);
push (@ISA,qw(HTML::Formatter));



sub adjust_lm
{
    my $self = shift;
    $self->showline;
    $self->{lm} += $_[0] * $self->{en};
    $self->{lm} = 0 if $self->{lm}<0;
    return $self->{lm};
}

