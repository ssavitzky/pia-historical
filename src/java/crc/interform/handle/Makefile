###### interform/handle/Makefile:	InterForm actor and tagset handles
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.interform.handle
TOPDIR=../../..

### The effect of these actors is entirely confined to the current file
BASIC_ACTORS = \
	Actor.java \
	Actor_attrs.java \
	Actor_doc.java \
	Actor_dscr.java \
	Actor_syntax.java \
	Actor_names.java \
	Add_markup.java \
	Difference.java \
	Dl.java \
	Element.java \
	Else.java \
	Equal.java \
	Expand.java \
	Foreach.java \
	Get.java \
	If.java \
	Pad.java \
	Product.java \
	Protect.java \
	Protect_result.java \
	Quotient.java \
	Repeat.java \
	Set.java \
	Sort.java \
	Sorted.java \
	Subst.java \
	Sum.java \
	Table.java \
	Tagset.java \
	Tagset_include.java \
	Test.java \
	Text.java \
	Then.java \
	Trim.java	

### The effect of these actors is local; they do not require a PIA, 
###	but may have an effect on the local system. 

SA_ACTORS = \
	Read.java \
	Read_file.java \
	Read_href.java \
	Write.java \
	Write_file.java \
	Write_href.java \
	User_message.java \
	Get_env.java \
	Set_env.java \
	Os_command.java \
	Os_command_output.java

### These actors can affect the operation of the PIA

PIA_ACTORS= \
	Agent_home.java \
	Agent_list.java \
	Agent_running.java \
	Agent_set_options.java \
	Agent_options.java \
	Agent_criteria.java \
	Agent_set_criteria.java \
	Agent_set_criterion.java \
	Agent_install.java \
	Agent_remove.java \
	Get_agent.java \
	Get_form.java \
	Get_pia.java \
	Get_trans.java \
	Pia_exit.java \
	Set_agent.java \
	Set_pia.java \
	Set_trans.java \
	Submit_forms.java \
	Trans_control.java


TAGSETS = \
	HTML_ts.java \
	Basic_ts.java \
	StandAlone_ts.java \
	Standard_ts.java

ACTORS = $(BASIC_ACTORS) $(SA_ACTORS) $(PIA_ACTORS)
FILES = $(ACTORS) $(TAGSETS)

TAGSET_DOCS =  \
	Basic-tagset.html  \
	StandAlone-tagset.html  \
	Standard-tagset.html

include $(TOPDIR)/makefiles/file.make

TSDOCDIR = $(PIADIR)/Doc/Manuals/InterForm/Tagset

### Reporting.

all:: report

### Count unimplemented and total actor handles.  
###	This finds only actors that call "Interp.unimplemented", not
###	individual features that call "Interp.error" directly.
report::
	@echo 'BASIC:	' `ls -l $(BASIC_ACTORS) | wc -l`,  \
		`grep 'unimplemented(' $(BASIC_ACTORS) | wc -l` unimp.
	@echo 'SA:	' `ls -l $(SA_ACTORS) | wc -l`, \
		`grep 'unimplemented(' $(SA_ACTORS) | wc -l` unimp.
	@echo 'PIA:	' `ls -l $(PIA_ACTORS) | wc -l`, \
		`grep 'unimplemented(' $(PIA_ACTORS) | wc -l` unimp.
	@echo 'TOTAL:	' `ls -l $(ACTORS) | wc -l`, \
		`grep 'unimplemented(' $(ACTORS) | wc -l` unimp.

### This finds and prints out every instance of the string "unimplemented" 
report::
	@grep -q 'unimplemented' $(BASIC_ACTORS) && \
	  (echo 'BASIC:	' ; grep 'unimplemented' $(BASIC_ACTORS)) || true
	@grep -q 'unimplemented' $(SA_ACTORS) && \
	  (echo 'SA:	' ; grep 'unimplemented' $(SA_ACTORS)) || true
	-@grep -q 'unimplemented' $(PIA_ACTORS) && \
	  (echo 'PIA:	' ; grep 'unimplemented' $(PIA_ACTORS)) || true

### Extract the documentation comments:

DOC_CMD= $(BINDIR)/pia_wrapper crc.interform.Filter

Basic-tagset.html::	 $(BASIC_ACTORS) Basic_ts.java
	$(DOC_CMD) -t Basic actor-doc.if > Basic-tagset.html

StandAlone-tagset.html:: $(BASIC_ACTORS) $(SA_ACTORS) StandAlone_ts.java
	$(DOC_CMD) -t StandAlone actor-doc.if > StandAlone-tagset.html

Standard-tagset.html::	 $(BASIC_ACTORS) $(SA_ACTORS) $(PIA_ACTORS) \
	Standard_ts.java
	$(DOC_CMD) -t Standard actor-doc.if > Standard-tagset.html

actor-doc:	$(TAGSET_DOCS)
	cp $(TAGSET_DOCS) $(TSDOCDIR)

### Run a perl script over all actors.  It can be redefined for various
###	one-shot fixups; the first use is transforming syntax comments
###	into a combination of javadoc and executable forms.

FIXUP=./fixup.pl
fixup:
	$(FIXUP) $(BASIC_ACTORS)
	$(FIXUP) $(SA_ACTORS)
	$(FIXUP) $(PIA_ACTORS)

unfix:
	for f in *.java ; do if [ -f $$f.bak ]; then mv -f $$f.bak $$f; fi ; done
