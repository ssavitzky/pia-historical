###### dps/aux/Makefile:	DPS auxiliary classes
# 	$Id$

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


PACKAGE=crc.dps.util
TOPDIR=../../..

FILES = $(CLASSES) $(UTILITIES)

CLASSES = \
	BasicNamespace.java \
	NamespaceWrap.java \
	EntityInput.java \
	EntityWrap.java \
	BasicEntityTable.java \
	ContextStack.java \
	CurrentActive.java \
	CursorStack.java

#	CurrentNode.java \

UTILITIES = \
	Copy.java \
	Create.java \
	Expand.java \
	Forms.java \
	Index.java \
	Log.java \
	Status.java \
	Test.java \
	ListUtil.java \
	MathUtil.java \
	TextUtil.java

include $(TOPDIR)/makefiles/file.make

### Dependencies

BasicEntityTable.class: BasicNamespace.java \
	../Namespace.java ../EntityTable.java

BasicNamespace.class: ../Namespace.java ../EntityTable.java
