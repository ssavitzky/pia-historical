package IF::Tagset; ###### Sets of Tags (Interform Actors)
###	$Id$
###
###	A Tagset is a collection of related Actors.  It is defined
###	using a <tagset name="...">...</tagset> element containing the
###	<actor> elements that are part of the set.  A Tagset element
###	can contain arbitrary text, so it can also serve as its own
###	documentation. 
###
###	There are actually three sets of actors for processing
###	tagsets: one that actually defines the tagset and its actors,
###	one that formats it as a document, and one that deletes
###	extraneous text for more efficient loading.  The latter two
###	are usually run to generate .html and .ts files, respectively.

use IF::IA;
push(@ISA,IF::IA);


#############################################################################
###
### Creation:
###




#############################################################################
###
### Initialization:
###
###	These are the tagsets and actors required to initialize the
###	InterForm Interpretor and the PIA.  We have to be able to
###	process START-UP.html (which initializes the PIA) and
###	Standard.if (which defines the Standard tagset).  This
###	requires the actors -form-submit-, tagset, and actor.
