### Makefile for Doc/Papers/LinuxWorld1999
#	$Id$	

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


PIADIR=../../..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=LinuxWorld1999
MYPATH=Doc/Papers/LinuxWorld1999
include $(MF_DIR)/file.make
include $(MF_DIR)/graphics.make
include $(MF_DIR)/text.make
include $(MF_DIR)/subdir.make

SUBDIRS= Images

EXCLUDE = $(wildcard *.slides.html) HEADER.html index.html

FILES = $(filter-out $(EXCLUDE), $(wildcard *.html))

XFILES =\
	history.html manifesto.html technical.html \
	lw99.html architecture.html who-what.html \
	open-source.html oss-bkgnd.html appendices.html \
	open-source-and-ricoh.html appliances.html

SLIDES = $(FILES:.html=.slides.html)

all:: $(SLIDES)

*.slides.html: config.inc

lw99.slides.html: technical.html
