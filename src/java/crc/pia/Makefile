###### pia/Makefile:	Platform for Information Appliances
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


PACKAGE=crc.pia
TOPDIR=../..

PACKAGES = \
	agent \
	handle

FILES = \
	Athread.java \
	HttpBuffer.java \
	Accepter.java \
	Transaction.java \
	Machine.java \
	Resolver.java \
	Configuration.java \
	Content.java \
	InputContent.java \
	ContentOperationUnavailable.java \
	Crontab.java \
	CrontabEntry.java \
	Agent.java \
	ContentFactory.java \
	GenericAgent.java \
	Logger.java \
	Pia.java \
	Setup.java \
	Piaproperties.java \
	PiaInitException.java \
	PiaRuntimeException.java \
	FormContent.java \
	MultipartFormContent.java \
	HeaderFactory.java \
	Headers.java \
	ThreadPool.java \
	HTTPRequest.java \
	HTTPResponse.java \
	FileAccess.java \
	Authenticator.java 

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:

GenericAgent.class: Agent.java
