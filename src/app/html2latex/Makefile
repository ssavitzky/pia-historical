#
# Adapted from NCSA Xmosaic Makefile

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

# ----------------------------------------------------------------------------
# For normal machines with normal compilers:
#CC = cc
# For testing:
# CC = gcc
# For Sun's and other non-at-least-pseudo-ANSI-C platforms:
 CC = gcc
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# For SGI's:
#CFLAGS = -g -cckr  -DXMOSAIC
# For testing:
# CFLAGS = -g -Wall -DXMOSAIC
# For HP's:
# CFLAGS = -g -DXMOSAIC -I/usr/include/Motif1.1 -I/usr/include/X11R4
# For everyone else: -I/rhine1/src/gnu/glibc-1.08.1/posix
 CFLAGS = -g -DXMOSAIC
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# For SGI's:
#RANLIB = /bin/true
# For everyone else:
 RANLIB = ranlib
# ----------------------------------------------------------------------------

# You shouldn't need to edit below here.

EXETARGET = html2latex
LIBTARGET = libhtmlp.a
DOCTARGET = html2latex.tex

all: $(LIBTARGET) $(EXETARGET) $(DOCTARGET)

CFILES = HTMLparse.c HTMLplist.c

OBJS = $(CFILES:.c=.o)

$(LIBTARGET): $(OBJS)
	-rm -f $(LIBTARGET)
	ar rv $(LIBTARGET) $(OBJS)
	$(RANLIB) $(LIBTARGET)

$(EXETARGET): $(LIBTARGET) $(EXETARGET).c
	$(CC) $(CFLAGS) -o $(EXETARGET) $(EXETARGET).c $(LIBTARGET)

$(DOCTARGET): $(EXETARGET) html2latex.html
	./$(EXETARGET) html2latex.html

$(OBJS): HTMLparse.h

clean:
	-rm $(EXETARGET) $(OBJS) $(LIBTARGET) $(DOCTARGET)
