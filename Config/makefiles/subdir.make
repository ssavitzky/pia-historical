### subdir.make -- makefile template for subdirectories
# 	$Id$
# 	COPYRIGHT 1997, Ricoh Silicon Valley

########################################################################
#
# This makefile contains rules for subdirectories
#
########################################################################

### Usage:
#	PIADIR=../../....
#	MF_DIR=$(PIADIR)/Config/makefiles
#	MYNAME=<name of this directory>
#	MYPATH=<path from PIADIR to this directory>
#	include $(MF_DIR)/file.make
#  (up to this point it's standard boilerplate)
#	SUBDIRS = x y z
#	include $(MF_DIR)/subdir.make
#

### Targets:
#	all	build executables, etc.
#	doc	build documentation
#	clean	remove trash
#	setup	initialize Makefile and other essential files

all::
	for p in `ls -d $(SUBDIRS)`; do ( cd $$p; test -f Makefile \
		&& $(MAKE) PIADIR=../$(PIADIR) VPATH=$(VPATH)/$$p ); \
	done

doc::
	@@for p in `ls -d $(SUBDIRS)`; do ( cd $$p; test -f Makefile \
		&& $(MAKE) PIADIR=../$(PIADIR) doc); \
	done

clean::
	@@for p in `ls -d $(SUBDIRS)`; do ( cd $$p; test -f Makefile \
		&& $(MAKE) PIADIR=../$(PIADIR) clean); \
	done

setup::
	@@for p in $(SUBDIRS); do \
		echo 'setting up ' $$p; \
		test -d $$p || mkdir $$p; \
		test -f $$p/Makefile || (cd $$p; \
		  make -f ../Makefile PIADIR=../$(PIADIR) \
			MYPATH=$(MYPATH)/$$p MYNAME=$$p setupSub); \
		(cd $$p; $(MAKE) PIADIR=../$(PIADIR) setup); \
	done

setupSub: 
	echo   '### Makefile for' $(MYPATH) 			 > Makefile
	echo   '#	$$Id$$	'				>> Makefile
	echo   '# 	COPYRIGHT 1997, Ricoh Silicon Valley' 	>> Makefile
	echo   ' '						>> Makefile
	echo   'PIADIR=$(PIADIR)'				>> Makefile
	echo   'MF_DIR=$$(PIADIR)/Config/makefiles'		>> Makefile
	echo   'MYNAME=$(MYNAME)'				>> Makefile
	echo   'MYPATH=$(MYPATH)'				>> Makefile
	echo   'include $$(MF_DIR)/file.make'			>> Makefile
	-grep '^include ' ../Makefile \
		| grep -v file.make | grep -v subdir.make 	>> Makefile
	echo   ' '						>> Makefile
