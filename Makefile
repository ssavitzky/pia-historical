### Makefile for pia
#	$Id$
#	COPYRIGHT 1997, Ricoh Silicon Valley

PIADIR=.
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=pia
MYPATH=

SUBDIRS= src bin lib Doc

include $(MF_DIR)/file.make
include $(MF_DIR)/subdir.make

### The following is unique to the top level ###

### Commands:

# === GNU tar required ===
TAR=/usr/local/bin/tar

### Common cvs operations 

cvs_tag::
	cvs tag $(VERSION_ID)

cvs_rtag::
	cvs rtag $(VERSION_ID) rest

### Stuff for making a CD-ROM

CD_ROM_SRC_DIR	= cd_rom_src_dir
CD_ROM_DEST_DIR	= cd_rom_dest_dir
CD_ROM_PUB      = Ricoh Silicon Valley

VENDOR_TAG  = PIA
RELEASE         = 1
MAJOR           = 1
MINOR           = 1
SUFFIX          = 
VERSION_ID = $(VENDOR_TAG)$(RELEASE)_$(MAJOR)_$(MINOR)$(SUFFIX)
VERSION    = $(VENDOR_TAG)$(RELEASE).$(MAJOR).$(MINOR)$(SUFFIX)

version_id::
	echo $(VERSION) `date` > version_id

cd_rom:		$(CD_ROM_SRC_DIR) $(CD_ROM_DEST_DIR) version_id
	mkisofs -f -R -T \
	    -P "$(CD_ROM_PUB)" -A $(VERSION_ID) -V $(VERSION_ID) \
	    -o $(CD_ROM_DEST_DIR)/pia.iso $(CD_ROM_SRC_DIR)



### Put all CVS files into a tar file for safekeeping.

SRCDIR=src/java/crc
CLASSDIR=src/java
PIALIBDIR=lib/java
INTERFORM=Doc/Manuals/InterForm

cvs.tar::
	find $(SUBDIRS) -name CVS -print | tar -cT - -f cvs.tar

### Prepare release

rm_bin_tar:: 
	 rm -f pia_bin.tar.gz
	 rm -f $(PIALIBDIR)/crc.zip
rm_pia_tar::
	rm -f pia.tar.gz
	rm -f $(PIALIBDIR)/crc.zip

prep_rel::

	cd $(SRCDIR); make clean ; make
	cd $(INTERFORM); make
	cd $(CLASSDIR);make crc.zip; make alldoc

### Binary release

pia_bin.toc:: rm_bin_tar prep_rel
	cd ..; find pia \! -type d -print \
	    | grep -v CVS | grep -v InternalDoc \
	    | grep -v src > pia/pia_bin.toc 

pia_bin.tar:	pia_bin.toc
	cd ..; $(TAR) cfT pia/pia_bin.tar pia/pia_bin.toc ;  /bin/gzip pia/pia_bin.tar

### Source release

pia.toc:: rm_pia_tar prep_rel
	cd ..;	find pia \! -type d -print \
	    | grep -v CVS | grep -v InternalDoc > pia/pia.toc 

pia.tar:	pia.toc
	cd ..; $(TAR) cfT pia/pia.tar pia/pia.toc ;	/bin/gzip pia/pia.tar

