PACKAGE=crc.content.text
TOPDIR=../../..


FILES = \
	Default.java \
	html.java \
	ProcessedContent.java \
	StringContent.java

include $(TOPDIR)/makefiles/file.make

### Dependencies:

Default.class:	../StreamingContent.java

html.class:  Default.java 
annotatedhtml.class:  html.java
ProcessedContent.class: ../../dps/process/ActiveDoc.java
