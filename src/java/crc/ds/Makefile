###### ds/Makefile:	Data Structure package.
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


PACKAGE=crc.ds
TOPDIR=../..

FILES = \
	Association.java \
	BalancedTree.java \
	Criterion.java \
	ValueCriterion.java \
	Criteria.java \
	Features.java \
	HasFeatures.java \
	InvalidIndex.java \
	List.java \
	Nil.java \
	Queue.java \
	Registered.java \
	Sorter.java \
	SortTree.java \
	Table.java \
	Tabular.java \
	UnaryFunctor.java \
	TernFunc.java \
	Stuff.java 

#	Thing.java \

include $(TOPDIR)/makefiles/file.make

### Dependencies

BalancedTree.class: Sorter.java

SortTree.class: Sorter.java

List.class: Stuff.java

Table.class: Stuff.java Tabular.java

Thing.class: Stuff.java

Queue.class: List.java Stuff.java

Features.class: Criteria.java HasFeatures.java 

HasFeatures.class: 

Criteria.class: Criterion.java Features.java List.java Stuff.java

Criterion.class: Criterion.java ValueCriterion.java

ValueCriterion.class: ValueCriterion.java Criterion.java
