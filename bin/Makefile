### Makefile for bin
#	$Id$
#	COPYRIGHT 1997, Ricoh Silicon Valley

PIADIR=..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=bin
MYPATH=bin

include $(MF_DIR)/file.make

all:: jpia
	@echo "scripts made"

### Executables 


# This constructs a shell script to run the Java PIA in this directory. 
#	If there is no PIA_DIR in the environment, it overrides it.

jpia: Makefile 
	echo '#!/bin/sh' 					   > jpia
	echo test '"x$$PIA_DIR" != "x" ||' PIA_DIR=`(cd ..; pwd)` >> jpia
	echo export PIA_DIR 					  >> jpia
	echo exec '$$'PIA_DIR/bin/pia '$$*' 	  		  >> jpia
	chmod +x jpia

## This actually constructs a shell script to run the PERL PIA. 
##	If there is no PIA_DIR in the environment, it overrides it.

ppia: Makefile 
	echo '#!/bin/sh' 					   > ppia
	echo test '"x$$PIA_DIR" != "x" ||' PIA_DIR=`(cd ..; pwd)` >> ppia
	echo export PIA_DIR 					  >> ppia
	echo exec perl '$$'PIA_DIR/lib/perl/pia.pl '$$*' 	  >> ppia
	chmod +x ppia
