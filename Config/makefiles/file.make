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

HEADER.html:
	echo "<h1>$(MYPATH)</h1>"					 > $@
	echo "<a href=\"../\">[..]</a>"					>> $@
	echo "<a href=\"#files\">[files]</a><p>"			>> $@
	echo "<!-- brief description goes here -->"			>> $@
	echo "<h2>Annotated Contents</h2>"				>> $@
	echo "<h3>Directories</h3>"					>> $@
	echo "<dl>"							>> $@
	echo "  <dt> <a href=\"\"></a>"					>> $@
	echo "  <dd> "							>> $@
	echo "</dl>"							>> $@
	echo "<h3>Files</h3>"						>> $@
	echo "<dl>"							>> $@
	echo "  <dt> <a href=\"\"></a>"					>> $@
	echo "  <dd> "							>> $@
	echo "</dl>"							>> $@
	echo "<hr>"							>> $@
	echo "<b>Copyright &copy; 1997 Ricoh Silicon Valley</b><br>"	>> $@
	echo "<b>$$"Id"$$</b>"						>> $@
	echo "<a name=\"files\"><hr></a>"				>> $@
