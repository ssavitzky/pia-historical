### Makefile for Doc/Graphics
#	$Id$	
# 	COPYRIGHT 1997, Ricoh Silicon Valley
 
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
