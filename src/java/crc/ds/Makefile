###### ds/Makefile:	Data Structure package.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.ds
TOPDIR=../..

FILES = \
	Association.java \
	BalancedTree.java \
	Criteria.java \
	Criterion.java \
	ValueCriterion.java \
	Features.java \
	HasFeatures.java \
	Index.java \
	List.java \
	Nil.java \
	Queue.java \
	Sorter.java \
	SortTree.java \
	Table.java \
	UnaryFunctor.java \
	TernFunc.java \
	Stuff.java 

#	Thing.java \

include $(TOPDIR)/makefiles/file.make

### Dependencies

BalancedTree.class: Sorter.java

SortTree.class: Sorter.java

List.class: Stuff.java

Table.class: Stuff.java

Thing.class: Stuff.java

Queue.class: List.java Stuff.java

Features.class: Criteria.java HasFeatures.java 

HasFeatures.class: 

Criteria.class: Criterion.java Features.java List.java Stuff.java

Criterion.class: Criterion.java ValueCriterion.java

ValueCriterion.class: ValueCriterion.java Criterion.java
