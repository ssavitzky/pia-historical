###### dom/test/Makefile:	DOM tests
# 	$Id$
#	Copyright 1998, Ricoh California Research Center
PACKAGE=crc.test.dom
TOPDIR=../../..

PACKAGES = 


FILES = \
	T_Node.java \
	T_ChildNodeList.java \
	T_LinkedList.java \
	T_BasicNamedNodeList.java \
	T_AttrList.java \
	T_LinkedNodeList.java \
	T_ArrayNodeList.java \
	T_BasicElement.java \
	T_DOM.java \
	T_OtherElement.java \
	T_Entity.java


include $(TOPDIR)/makefiles/package.make
include $(TOPDIR)/makefiles/file.make

CASEFILES = \
	TestNode1.case \
	TestNode2.case \
	TestNode3.case \
	TestNode4.case \
	TestLinkedList1.case \
	TestBasicNamedNodeList1.case \
	TestAttrList1.case \
	TestLinkedNodeList1.case \
	TestChildNodeList1.case \
	TestArrayNodeList1.case \
	TestBasicElement1.case \
	TestBasicElement2.case \
	TestDOM1.case \
	TestOtherElement1.case \
	TestEntity1.case \
	TestEntity2.case \
	TestEntity3.case

.SUFFIXES: .case .txt .html .if .out .ok .diff .run .java .class

DEBUG=-d
CLASS=crc.interform.Filter

.case.out:

	$< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

all:: $(FILES:.java=.class)
cases:: $(CASEFILES:.case=.out)


clean::
	rm -f *.out *.diff *.log

































































