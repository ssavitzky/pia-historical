###### interform/handle/Makefile:	InterForm actor and tagset handles
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.interform.handle
TOPDIR=../../../..

# The effect of these actors is entirely confined to the current file
BASIC_ACTORS = \
	Actor.java \
	Add_markup.java \
	Difference.java \
	Element.java \
	Equal.java \
	Expand.java \
	Foreach.java \
	Get.java \
	If.java \
	Pad.java \
	Product.java \
	Protect.java \
	Quotient.java \
	Repeat.java \
	Set.java \
	Sort.java \
	Sorted.java \
	Subst.java \
	Sum.java \
	Tagset.java \
	Tagset_include.java \
	Test.java \
	Text.java \
	Trim.java \
	Basic_ts.java	

# The effect of these actors is local; they do not require a PIA.
SA_ACTORS = \
	Read.java \
	Write.java \
	User_message.java \
	Os_command.java \
	Os_command_output.java \
	StandAlone_ts.java 

# These actors can affect the operation of the PIA
PIA_ACTORS= \
	Agent_home.java \
	Agent_running.java \
	Agent_set_options.java \
	Agent_options.java \
	Agent_set_criteria.java \
	Agent_set_criterion.java \
	Agent_install.java \
	Agent_remove.java \
	Trans_control.java \
	Pia_exit.java \
	Standard_ts.java


TAGSETS = \
	HTML_ts.java

FILES = $(BASIC_ACTORS) $(SA_ACTORS) $(PIA_ACTORS) $(TAGSETS)

include $(TOPDIR)/makefiles/file.make
