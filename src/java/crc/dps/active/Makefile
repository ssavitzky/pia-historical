###### dps/active/Makefile:	DPS active document classes
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
	ParseTreeDocument.java \
	ParseTreeElement.java \
	ParseTreeEntity.java \
	ParseTreeExternal.java \
	ParseTreeGeneric.java \
	ParseTreePI.java \
	ParseTreeText.java

OTHER_CLASSES = \
	NotActiveNodeException.java \
	ParseTreeAttrs.java \
	ParseNodeArray.java \
	ParseNodeTable.java \
	ParseNodeMap.java \
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

ParseNodeMap.class: ParseNodeArray.java

ParseNodeList.class: ParseNodeArray.java
ParseNodeArrayEnumerator.class: ParseNodeArray.java
