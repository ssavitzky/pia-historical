###### dps/input/Makefile:	InterForm Token input sources
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.input
TOPDIR=../../..

# The following Output classes construct trees, so the required state
# is minimal -- only the current node.

TO_NODES = \
	AbstractOutput.java \
	ActiveOutput.java \
	ToAttributeList.java \
	ToDocument.java \
	ToNodeList.java \
	ToParseTree.java

# The following Output classes do NOT have access to a constructed tree,
# so the have to keep their state in a stack.

TO_TEXT = \
	ToExternalForm.java \
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

AbstractOutput.class: ../Output.java ../aux/CurrentNode.java
ActiveOutput.class: ../Output.java ../aux/CurrentActive.java

ToAttributeList.class: ../Output.java ../aux/CurrentActive.java \
	ActiveOutput.java
ToNodeList.class: ../Output.java ../aux/CurrentActive.java \
	ActiveOutput.java
ToParseTree.class: ../Output.java ../aux/CurrentActive.java \
	ActiveOutput.java

ToString.class: ../Output.java ../aux/CursorStack.java
ToWriter.class: ../Output.java ../aux/CursorStack.java

DiscardOutput.class: ../Output.java
Proxy.class: ../Output.java
FilterText.class: ../Output.java Proxy.java
OutputTrace.class: ../Output.java Proxy.java
