###### pia/agent/Makefile:	Agent handlers and related classes
#	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.pia.agent
TOPDIR=../../..


OTHER = \
	AgentMachine.java \
	AgentInstallException.java

AGENTS = \
	Admin.java \
	Root.java \
	Dofs.java \
	Logo.java \
	Cache.java \
	Camera.java

HANDLERS = \
	agentHome.java \
	agentInstall.java \
	agentRemove.java \
	agentRestore.java \
	agentSave.java

FILES= $(OTHER) $(AGENTS) $(HANDLERS)

include $(TOPDIR)/makefiles/file.make

### Dependencies:

Agency.class: ../Agent.java ../GenericAgent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPRequest.java

Admin.class: ../Agent.java ../GenericAgent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPRequest.java

Root.class: ../Agent.java ../GenericAgent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPRequest.java

AgentMachine.java:  ../Agent.java ../GenericAgent.java ../Resolver.java \
	 ../Pia.java ../Content.java  ../../content/ByteStreamContent.java  \
	../Transaction.java ../Machine.java ../HTTPResponse.java

Dofs.java: ../Agent.java ../GenericAgent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java 

Fallback.class: Agency.java

Logo.java: ../Agent.java ../GenericAgent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPResponse.java
