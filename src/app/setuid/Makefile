### Makefile for pia/src/app/setuid
###	$Id$
###	All are owned by root so it can set the real user ID.

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
