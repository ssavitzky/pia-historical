###### ds/Makefile:	Data Structure package.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.ds
TOPDIR=../../..

FILES = \
	Criteria.java \
	Criterion.java \
	ValueCriterion.java \
	Features.java \
	List.java \
	Queue.java \
	Table.java \
	UnaryFunctor.java \
	TernFunc.java \
	Stuff.java 

#	Thing.java \

include $(TOPDIR)/makefiles/file.make

### Dependencies

List.class: Stuff.java

Table.class: Stuff.java

Thing.class: Stuff.java

Queue.class: List.java

Criteria.class: Criteria.java Criterion.java

Criterion.class: Criterion.java ValueCriterion.java

ValueCriterion.class: ValueCriterion.java Criterion.java
