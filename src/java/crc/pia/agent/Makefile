###### pia/agent/Makefile:	Agent handlers and related classes
#	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.pia.agent
TOPDIR=../../..


FILES = \
	Agency.java \
	AgentMachine.java \
	Dofs.java \
	Fallback.java \
	Logo.java \
	AgentInstallException.java

include $(TOPDIR)/makefiles/file.make

### Dependencies:

Agency.class: ../Agent.java ../GenericAgent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPRequest.java

AgentMachine.java:  ../Agent.java ../GenericAgent.java ../Resolver.java \
	 ../Pia.java ../Content.java  ../ByteStreamContent.java  \
	../Transaction.java ../Machine.java ../HTTPResponse.java

Dofs.java: ../Agent.java ../GenericAgent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java 

Fallback.class: Agency.java

Logo.java: ../Agent.java ../GenericAgent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPResponse.java
