### Makefile for Config/cdrom
#	$Id$
#	COPYRIGHT 1997, Ricoh Silicon Valley

PIADIR=../..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=cdrom
MYPATH=Config/cdrom

include $(MF_DIR)/file.make

### Where to find the CD-ROM prep directory and the image:
#
CDROMDIR=$(PIADIR)/../cdrom
IMAGEDIR=/tmp/CDRW
IMAGE=$(IMAGEDIR)/pia.iso

### Release parameters:

PUBLISHER= "Ricoh Silicon Valley"
TAG=		PIA
RELEASE=	1
MAJOR=		1
MINOR=		1
SUFFIX=		1
VERSION_NUMBER=	$(TAG)-$(RELEASE).$(MAJOR).$(MINOR)$(SUFFIX)

### Files we copy from Files:

DOSTXTFILES= pia.bat piajdk.bat autorun.inf HEADER.htm README.txt
DOSBINFILES= pia.pif

### Files we copy in from elsewhere:
#	paths relative to $(TOPDIR)

MISCFILES= 

### Files we build directly in CDROMDIR:

BUILDFILES=	pia_src.tgz pia_bin.tgz


### Targets:

all::
	@echo nothing needs doing.  Try one of the following:
	@echo "    make joliet    -- prepare a Windows 95 CD-ROM"
	@echo "    make rockridge -- prepare a RockRidge CD-ROM"
	@echo "    make image     -- make an ISO9660 (RockRidge) image"
	@echo "    make coaster   -- use cdrecord to burn a disk"

# Prepare for making a Windows 95 (Joliet) format CD-ROM.
joliet: Files/README.txt
	for f in $(DOSTXTFILES) ; do \
	   cp_ascii < Files/$$f > $(CDROMDIR)/$$f ; done
	for f in $(DOSBINFILES) ; do \
	   cp Files/$$f  $(CDROMDIR)/$$f ; done

# Files we need to copy from various places and stick in Files.
#	Some of these have to be renamed or otherwise converted.
Files/README.txt:: $(PIADIR)/README
	cp $? $@

# Prepare for making a Unix (RockRidge) format CD-ROM.
rockridge:


# Make an ISO9660 image.
image::


# Make an actual CD.
coaster:: $(IMAGE)
