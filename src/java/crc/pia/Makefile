###### pia/Makefile:	Platform for Information Appliances
#	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.pia
TOPDIR=../../..

PACKAGES = \
	agent

FILES = \
	Accepter.java \
	Transaction.java \
	Machine.java \
	Resolver.java \
	Content.java \
	Agent.java \
	ByteStreamContent.java \
	ContentFactory.java \
	GenericAgent.java \
	Logger.java \
	Pia.java \
	PiaInitException.java \
	PiaRuntimeException.java \
	FormContent.java \
	HeaderFactory.java \
	Headers.java \
	ThreadPool.java \
	HTTPRequest.java \
	HTTPResponse.java

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:

Transaction.class: Transaction.java Machine.java Content.java Resolver.java \
	Athread.java ../tf/Registry.java ../util/Utilities.java \
	../ds/Features.java ../ds/Queue.java ../ds/UnaryFunctor.java \
	../sgml/SGML.java ../sgml/AttrBase.java

HTTPRequest.class: HTTPRequest.java Transaction.java

HTTPResponse.class: HTTPResponse.java Transaction.java

