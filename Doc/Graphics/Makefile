### Makefile for Doc/Graphics
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

 
PIADIR=../..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=Graphics
MYPATH=Doc/Graphics
include $(MF_DIR)/file.make
include $(MF_DIR)/graphics.make
include $(MF_DIR)/text.make
 
### Files whose originals are xfig *.fig files 
FIGS = on_web.fig pentagon.fig sm_pent.fig to_life.fig web.fig

PPMS = pent16.ppm pia16.ppm

### Derived files 

FIG_GIF = $(patsubst %.fig,%.gif,$(FIGS))
FIG_PS  = $(patsubst %.fig,%.ps,$(FIGS))
FIG_EPS = $(patsubst %.fig,%.eps,$(FIGS))
FIG_XBM = $(patsubst %.fig,%.xbm,$(FIGS))

PPM_GIF = $(patsubst %.ppm,%.gif,$(PPMS))

### All GIF files; original postscripts 

GIF = $(FIG_GIF) $(PPM_GIF)
EPS = $(FIG_EPS)

all:: $(GIF) $(EPS)
