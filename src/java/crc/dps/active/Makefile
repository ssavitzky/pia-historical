###### dps/active/Makefile:	DPS active document classes
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.active
TOPDIR=../../..

INTERFACES = \
	ActiveNode.java \
	ActiveNodeList.java \
	ActiveAttrList.java \
	ActiveAttribute.java \
	ActiveComment.java \
	ActiveDeclaration.java \
	ActiveDocument.java \
	ActiveElement.java \
	ActiveEntity.java \
	ActivePI.java \
	ActiveText.java

BASE_CLASSES = \
	ParseTreeNode.java \
	ParseTreeNamed.java

NODE_CLASSES = \
	ParseTreeAttribute.java \
	ParseTreeComment.java \
	ParseTreeDecl.java \
	ParseTreeElement.java \
	ParseTreeEntity.java \
	ParseTreeExternal.java \
	ParseTreeGeneric.java \
	ParseTreePI.java \
	ParseTreeText.java

OTHER_CLASSES = \
	NotActiveNodeException.java \
	ParseTreeAttrs.java \
	ParseNodeList.java \
	ParseChildList.java \
	ParseChildEnum.java

CLASSES = $(BASE_CLASSES) $(NODE_CLASSES) $(OTHER_CLASSES)

FILES = $(INTERFACES) $(CLASSES)

include $(TOPDIR)/makefiles/file.make

### Dependencies

ParseTreeAttribute.class: ParseTreeNode.java ParseTreeNamed.java
ParseTreeEntity.class: ParseTreeNode.java ParseTreeNamed.java
ParseTreePI.class: ParseTreeNode.java ParseTreeNamed.java

ParseTreeComment.class: ParseTreeNode.java
ParseTreeElement.class: ParseTreeNode.java
ParseTreeText.class: ParseTreeNode.java
