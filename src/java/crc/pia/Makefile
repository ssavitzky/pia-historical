###### pia/Makefile:	Platform for Information Appliances
#	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.pia
TOPDIR=../..

PACKAGES = \
	agent

FILES = \
	Accepter.java \
	Transaction.java \
	Machine.java \
	Resolver.java \
	Configuration.java \
	Content.java \
	Agent.java \
	ByteStreamContent.java \
	ContentFactory.java \
	GenericAgent.java \
	Logger.java \
	Pia.java \
	Setup.java \
	Piaproperties.java \
	PiaInitException.java \
	PiaRuntimeException.java \
	FormContent.java \
	HeaderFactory.java \
	Headers.java \
	ThreadPool.java \
	HTTPRequest.java \
	HTTPResponse.java \
	FileAccess.java

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:

Accepter.class: Transaction.java Machine.java Pia.java HTTPRequest.java \
	../ds/Criteria.java ../ds/Criterion.java \
	../sgml/SGML.java ../sgml/Attrs.java

Agent.class: Transaction.java Machine.java Resolver.java Content.java Pia.java

GenericAgent.class: Agent.java agent/AgentMachine.java Transaction.java \
	HTTPRequest.java HTTPResponse.java Machine.java Resolver.java \
	Content.java ByteStreamContent.java Pia.java \
	../ds/Criteria.java ../ds/Criterion.java \
	../sgml/SGML.java ../sgml/Attrs.java \
	../sgml/AttrBase.java ../sgml/AttrTable.java

Machine.class: Content.java HTTPResponse.java Transaction.java Resolver.java \
	../util/Timer.java

Pia.class: PiaInitException.java Machine.java agent/Agency.java \
	Setup.java Resolver.java Logger.java Transaction.java \
	agent/AgentInstallException.java 

Setup.class: Configuration.java

Resolver.class: Transaction.java Agent.java ../ds/Queue.java

Transaction.class: Transaction.java Machine.java Content.java Resolver.java \
	Athread.java ../tf/Registry.java ../util/Utilities.java \
	../ds/Features.java ../ds/Queue.java ../ds/UnaryFunctor.java \
	../sgml/SGML.java ../sgml/AttrBase.java ../tf/Registry.java

HTTPRequest.class: HTTPRequest.java HTTPResponse.java Machine.java \
	Content.java Transaction.java

HTTPResponse.class: HTTPResponse.java Machine.java Content.java Transaction.java

