### Makefile for pia/src/app/setuid
###	$Id$

############################################################################### 
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


###	All apps are owned by root so they can set the real user ID.

PIADIR=../../..

PROGRAMS = be_pia run_pia shutdown
BINDIR = $(PIADIR)/bin
INSTALL = /usr/bin/install

### Install flags:
###	ALLFLAGS: executable by anyone .
###	PIAFLAGS: executable only by pia group

ALLFLAGS = -g pia -o root -m 6555
PIAFLAGS = -g pia -o root -m 6554

all::	$(PROGRAMS)
	@echo "compiled"

install::
	@[ `whoami` = 'root' ] || (echo "install must be done by root" ; false)

install:: be_pia
	$(INSTALL) $(ALLFLAGS) be_pia $(BINDIR)

install:: run_pia
	$(INSTALL) $(ALLFLAGS) run_pia $(BINDIR)

install:: shutdown
	$(INSTALL) $(PIAFLAGS) shutdown $(BINDIR)

be_pia:		be_pia.c

run_pia:	run_pia.c

shutdown:	shutdown.c
