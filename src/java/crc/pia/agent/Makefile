###### pia/agent/Makefile:	Agent handlers and related classes
#	$Id$

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
