### Makefile for Doc/Manuals/Api
#	$Id$	
# 	COPYRIGHT 1997, Ricoh Silicon Valley
 
PIADIR=../../..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=Api
MYPATH=Doc/Manuals/Api
include $(MF_DIR)/file.make

include $(MF_DIR)/text.make

all:: java_docs

# === This should really be done by "make doc" in src ===
java_docs: 
	cd $(PIADIR)/src/java; make alldoc PIADIR=../..
