### Makefile for Doc/Papers/LinuxWorld1999
#	$Id$	
# 	COPYRIGHT 1997, Ricoh Silicon Valley

PIADIR=../../..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=LinuxWorld1999
MYPATH=Doc/Papers/LinuxWorld1999
include $(MF_DIR)/file.make
include $(MF_DIR)/graphics.make
include $(MF_DIR)/text.make
include $(MF_DIR)/subdir.make

SUBDIRS= Images

FILES = lw99.html architecture.html who-what.html \
	open-source.html oss-bkgnd.html appendices.html \
	open-source-and-ricoh.html appliances.html

SLIDES = $(FILES:.html=.slides.html)

all:: $(SLIDES)

*.slides.html: config.inc
