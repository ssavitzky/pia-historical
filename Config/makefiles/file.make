### file.make -- makefile template for ordinary files
# $Id$
# COPYRIGHT 1997, Ricoh California Research Center

########################################################################
#
# This makefile contains rules for ordinary files 
#
########################################################################

### Usage:
#	PIADIR=../../....
#	MF_DIR=$(PIADIR)/Config/makefiles
#	MYNAME=<name of this directory>
#	MYPATH=<path from PIADIR to this directory>
#	include $(MF_DIR)/file.make
#


### Targets:
#	all	build executables, etc.
#	doc	build documentation
#	clean	remove trash
#	setup	initialize Makefile and other essential files 

all::
	@echo "Building in" $(MYNAME)

doc::
	@echo "Documenting in" $(MYNAME)

clean::
	@echo "Cleaning in" $(MYNAME)
	rm -f *~ *.bak *.log *.o *.obj

setup::
	@echo "Setup in" $(MYNAME)
	@echo "     You will probably need to edit the Makefile"
