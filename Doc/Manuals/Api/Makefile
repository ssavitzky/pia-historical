### Makefile for Doc/Manuals/Api
#	$Id$	

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
 
PIADIR=../../..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=Api
MYPATH=Doc/Manuals/Api
include $(MF_DIR)/file.make

include $(MF_DIR)/text.make

# This is normally done via "make doc" at the top level instead.
#
java_docs: 
	cd $(PIADIR)/src/java; make alldoc PIADIR=../..
