##############################################################################
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 

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

sub form_start {
##show table elements...
    1;
}
