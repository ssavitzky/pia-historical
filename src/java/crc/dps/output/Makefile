###### dps/input/Makefile:	InterForm Token input sources
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


PACKAGE=crc.dps.output
TOPDIR=../../..

# The following Output classes construct trees, so the required state
# is minimal -- only the current node.

TO_NODES = \
	ActiveOutput.java \
	ToAttributeList.java \
	ToDocument.java \
	ToNodeList.java \
	ToParseTree.java

#	AbstractOutput.java \

# The following Output classes do NOT have access to a constructed tree,
# so the have to keep their state in a stack.

TO_TEXT = \
	ToExternalForm.java \
	ToCharData.java \
	ToHTTPClient.java \
	ToString.java \
	ToWriter.java 

# The following Output classes proxy to another output, so they need to
# keep no state at all except for depth (which is really needed only if 
# the target output is missing).

TO_PROXY = \
	DiscardOutput.java \
	FilterMarkup.java \
	FilterText.java \
	OutputTrace.java \
	Proxy.java

FILES = $(TO_NODES) $(TO_TEXT) $(TO_PROXY)

include $(TOPDIR)/makefiles/file.make


### Dependencies:

AbstractOutput.class: ../Output.java ../util/CurrentNode.java
ActiveOutput.class: ../Output.java ../util/CurrentActive.java

ToAttributeList.class: ../Output.java ../util/CurrentActive.java \
	ActiveOutput.java
ToNodeList.class: ../Output.java ../util/CurrentActive.java \
	ActiveOutput.java
ToParseTree.class: ../Output.java ../util/CurrentActive.java \
	ActiveOutput.java

ToString.class: ../Output.java ../util/CursorStack.java
ToWriter.class: ../Output.java ../util/CursorStack.java

DiscardOutput.class: ../Output.java
Proxy.class: ../Output.java
FilterText.class: ../Output.java Proxy.java
OutputTrace.class: ../Output.java Proxy.java
