;;; html-helper-mode.el --- Major mode for composing html files.

;; Author: Nelson Minar <nelson@reed.edu>
;; Maintainer: Nelson Minar <nelson@reed.edu>
;; Created: 01 Feb 1994
;; Version: $Revision$
;; Keywords: HTML major-mode

;; LCD Archive Entry:
;; html-helper-mode|Nelson Minar|nelson@reed.edu|
;; Major mode for editing HTML.|
;; 16-Mar-94|Version 2.0|ftp://ftp.reed.edu/pub/src/html-helper-mode.tar.Z

;; Copyright (C) 1994 Nelson Minar

;; This program is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with GNU Emacs; see the file COPYING.  If not, write to
;; the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

;;; Commentary:
;;{{{ 

;; Installation:
;;   add this line in your .emacs:
;;     (autoload 'html-helper-mode "html-helper-mode" "Yay HTML" t)
;;   to invoke html-helper-mode automatically on .html files, do this:
;;     (setq auto-mode-alist (cons '("\\.html$" . html-helper-mode) auto-mode-alist))
;;   NOTE - this mode requires another lisp file, tempo.el. This can be
;;          retrieved from ftp://ftp.lysator.liu.se/pub/emacs/tempo.el

;; Configuration:
;;   see the "user variables" section, or the documentation on configuration
;;   in http://www.reed.edu/~nelson/tools/. There are variables you want to
;;   configure, particularly html-helper-do-write-file-hooks,
;;   html-helper-build-new-buffer, and html-helper-address-string

;; Description:
;;   html-helper-mode makes it easier to write HTML documents. This mode
;;   handles inserting HTML codes in a variety of ways (keybindings,
;;   completion in the buffer). It also supports indentation, timestamps,
;;   skeletons for new documents, hilit19 patterns, and a variety of other
;;   things. For the full skinny, see the HTML documentation that comes
;;   with the package or is at http://www.reed.edu/~nelson/tools/

;; Thank yous:
;;   David Kågedal <davidk@lysator.liu.se> for the tempo code which
;;   forms the core of the HTML insertion, as well as the HTML+
;;   cookies.

;;   Magnus Homann <d0asta@dtek.chalmers.se> for suggestions and code
;;   for the timestamp insertion

;;   Marc Andreessen <marca@ncsa.uiuc.edu> for writing the original html-mode
;;   that inspired this one

;; To do:
;;   some general way of building menus (easymenu.el)?
;;   font-lock patterns
;;   a way to send the right "load URL" signal to xmosaic for the current file?

;; The newest version of html-helper-mode should always be available from
;;   http://www.reed.edu/~nelson/tools/
;;   ftp://ftp.reed.edu/pub/src/html-helper-mode.tar.Z

;; This code was writting using folding.el, a wonderful folding editor
;; minor mode for emacs. That's what the strange {{{ comments are for.

;;}}}

;;; Code:

;;{{{ user variables

;; features. I recommend you turn these on.

(defvar html-helper-do-write-file-hooks nil
  "*If not nil, then html-helper-mode will modify the local-write-file-hooks
to do timestamps.")

(defvar html-helper-build-new-buffer nil
  "*If not nil, then html-helper will insert html-helper-new-buffer-strings
when new buffers are generated")

;; (see also tempo.el)

;; variables to configure

(defvar html-helper-basic-offset 2
  "*basic indentation size used for list indentation")

(defvar html-helper-item-continue-indent 5
  "*Indentation of lines that follow a <li> item. Default is 5, the length
of things like \"<li> \" and \"<dd> \".")

(defvar html-helper-never-indent nil
  "*If t, the indentation code for html-helper is turned off.")


;; hooks (see also tempo.el)

(defvar html-helper-mode-hook nil
  "*Hook run when html-helper-mode is started.")

(defvar html-helper-load-hook nil
  "*Hook run when html-helper-mode is loaded.")

(defvar html-helper-timestamp-hook 'html-helper-default-insert-timestamp
  "*Hook called for timestamp insertion. Override this for your own
timestamp styles.")


;; strings you might want to change

(defvar html-helper-address-string ""
  "*The default author string of each file.")

(defvar html-helper-new-buffer-template
  '("<html> <head>\n"
    "<title>" p "</title>\n</head>\n\n"
    "<body>\n"
    "<h1>" p "</h1>\n\n"
    p
    "\n\n<hr>\n"
    "<address>" html-helper-address-string "</address>\n"
    html-helper-timestamp-start
    html-helper-timestamp-end
    "\n</body> </html>\n")
  "*Template for new buffers, inserted by html-helper-insert-new-buffer-strings if
html-helper-build-new-buffer is set to t")

(defvar html-helper-timestamp-start "<!-- hhmts start -->\n"
  "*Delimiter for timestamps. Everything between html-helper-timestamp-start
and html-helper-timestamp-end will be deleted and replaced with the output of
the function html-helper-insert-timestamp if html-helper-do-write-file-hooks
is t")

(defvar html-helper-timestamp-end "<!-- hhmts end -->"
  "*Delimiter for timestamps. Everything between html-helper-timestamp-start
and html-helper-timestamp-end will be deleted and replaced with the output of
the function html-helper-insert-timestamp if html-helper-do-write-file-hooks
is t")

;; this is what the byte compiler does to see if its emacs18. You probably
;; don't need to change this.

(defvar html-helper-emacs18
  (and (boundp 'emacs-version)
       (or (and (boundp 'epoch::version) epoch::version)
	   (string-lessp emacs-version "19")))
  "I'll do minimal emacs18 support, grumble.")

;;}}}

(require 'tempo)
(require 'easymenu)

;;{{{ html-helper-mode-syntax-table

;; emacs doesn't really seem to be general enough to handle SGML like
;; syntax. In particular, comments are a loss. We do try this, though:
;;   give < and > matching semantics

(defvar html-helper-mode-syntax-table nil
  "Syntax table for html-helper.")

(if html-helper-mode-syntax-table
    ()
  (setq html-helper-mode-syntax-table (make-syntax-table text-mode-syntax-table))
  (modify-syntax-entry ?<  "(>  " html-helper-mode-syntax-table)
  (modify-syntax-entry ?>  ")<  " html-helper-mode-syntax-table)
  (modify-syntax-entry ?\" ".   " html-helper-mode-syntax-table)
  (modify-syntax-entry ?\\ ".   " html-helper-mode-syntax-table)
  (modify-syntax-entry ?'  "w   " html-helper-mode-syntax-table))

;;}}}
;;{{{ keymap variable and function setup

(defvar html-helper-keymap-list
  '(html-helper-head-map html-helper-header-map html-helper-anchor-map
    html-helper-logical-map html-helper-phys-map html-helper-list-map
    html-helper-note-map html-helper-form-map html-helper-image-map html-helper-struct-map
    intf-tests-map \intf-num-map intf-listproc-map intf-form-map intf-nb-map intf-sp-map
    intf-ca-map intf-os-map intf-ac-map)
  "list of all the subkeymaps html-helper uses")

(defvar html-helper-keymap-alist
  '((head . html-helper-head-map)
    (header . html-helper-header-map)
    (anchor . html-helper-anchor-map)
    (logical . html-helper-logical-map)
    (phys . html-helper-phys-map)
    (list . html-helper-list-map)
    (note . html-helper-note-map)
    (form . html-helper-form-map)
    (image . html-helper-image-map)
    (struct . html-helper-struct-map)
    (intftests . intf-tests-map)
    (intfnum . intf-num-map)
    (intflistproc . intf-listproc-map)
    (intfform . intf-form-map)
    (intfnb . intf-nb-map)
    (intfsp . intf-sp-map)
    (intfos . intf-os-map)
    (intfca . intf-ca-map)
    (intfac . intf-ac-map)

)
  "alist associating cookie types with keymaps")

;; basic keymap variables (not easy to mapcar a macro)
(defvar html-helper-mode-map (make-sparse-keymap)
  "Keymap for html-helper")
(defvar html-helper-head-map nil
  "Keymap used for head info.")
(defvar html-helper-header-map nil
  "Keymap used for headers.")
(defvar html-helper-anchor-map nil
  "Keymap used for anchors.")
(defvar html-helper-logical-map nil
  "Keymap used for logical styles.")
(defvar html-helper-phys-map nil
  "Keymap used for physical styles.")
(defvar html-helper-list-map nil
  "Keymap used for lists.")
(defvar html-helper-note-map nil
  "Keymap used for notes.")
(defvar html-helper-form-map nil
  "Keymap used for forms.")
(defvar html-helper-image-map nil
  "Keymap used for images.")
(defvar html-helper-struct-map nil
  "Keymap used for interform control sturctures.")
(defvar intf-tests-map nil
  "Keymap used for interform tests.")
(defvar intf-num-map nil
  "Keymap used for interform numeric processing.")
(defvar intf-listproc-map nil
  "Keymap used for interform list processing.")
(defvar intf-form-map nil
  "Keymap used for PIA form processing.")
(defvar intf-nb-map nil
  "Keymap used for PIA name binding.")
(defvar intf-sp-map nil
  "Keymap used for PIA string processing.")
(defvar intf-ca-map nil
  "Keymap used for PIA calendar.")
(defvar intf-os-map nil
  "Keymap used for PIA OS command.")
(defvar intf-ac-map nil
  "Keymap used for PIA agent criteria.")




;; make keymaps into prefix commands (does this do anything useful in 18?)
(mapcar 'define-prefix-command html-helper-keymap-list)

;; if we're emacs18, we have to build the prefix maps by hand
(if html-helper-emacs18
    (mapcar (function (lambda (v) (set v (make-sparse-keymap))))
	    html-helper-keymap-list))

;; now build the mode keymap.
;; special mode keys
(mapcar
 (function (lambda (l) (define-key html-helper-mode-map (car l) (nth 1 l))))
 '(("\M-\C-f" tempo-forward-mark)
   ("\M-\C-b" tempo-backward-mark)
   ("\M-\t"   tempo-complete-tag)
   
   ("\M-\C-t" html-helper-insert-timestamp-delimiter-at-point)))
 
;; indentation keys - only rebind these if the user wants indentation
(if html-helper-never-indent
    ()
  (define-key html-helper-mode-map "\t" 'html-helper-indent-command)
  (define-key html-helper-mode-map "\C-m" 'newline-and-indent))

;; special keybindings in the prefix maps (not in the list of cookies)
(define-key html-helper-list-map "i" 'html-helper-smart-insert-item)

;; install the prefix maps themselves into the mode map
;; eval the keymap in 18 so we get the value, not the symbol
(defun html-helper-install-prefix (l)
  "Install a prefix key into the map. Special code for emacs18"
  (if html-helper-emacs18
      (define-key html-helper-mode-map (car l) (eval (nth 1 l)))
    (define-key html-helper-mode-map (car l) (nth 1 l))))

(mapcar
 'html-helper-install-prefix
 '(("\C-c\C-b" html-helper-head-map)
   ("\C-c\C-t" html-helper-header-map)
   ("\C-c\C-a" html-helper-anchor-map)
   ("\C-c\C-s" html-helper-logical-map)
   ("\C-c\C-p" html-helper-phys-map)
   ("\C-c\C-l" html-helper-list-map)
   ("\C-c\C-n" html-helper-note-map)
   ("\C-c\C-f" html-helper-form-map)
   ("\C-c\C-i" html-helper-image-map)
   ("\M-cs" html-helper-struct-map)
   ("\M-te" intf-tests-map)
   ("\M-np" intf-num-map)
   ("\M-lp" intf-listproc-map)
   ("\M-fp" intf-form-map)
   ("\M-nb" intf-nb-map)
   ("\M-sp" intf-sp-map)
   ("\M-ca" intf-ca-map)
   ("\M-os" intf-os-map)
   ("\M-ac" intf-ac-map)
))

;;}}}
;;{{{ html-helper-mode-abbrev-table

(defvar html-helper-mode-abbrev-table nil
  "Abbrev table used while in html-helper-mode.")
(define-abbrev-table 'html-helper-mode-abbrev-table ())

;;}}}

;;{{{ html-helper-add-cookie function for building basic cookies

(defvar html-helper-tempo-tags nil
  "List of tags used in completion.")

(defun html-helper-add-cookie (l)
  "Add a new cookie to html-helper-mode. Builds a tempo-template for the
cookie and puts it into the appropriate keymap if a key is
requested."
  (let* ((type (car l))
	 (keymap (cdr-safe (assq type html-helper-keymap-alist)))
	 (key (nth 1 l))
	 (tag (nth 2 l))
	 (name (nth 3 l))
	 (cookie (nth 4 l))
	 (doc (nth 5 l))
	 (command (tempo-define-template name cookie tag doc 'html-helper-tempo-tags)))

    (if (stringp key)			                  ;bind at all?
	(if keymap			                  ;special keymap?
	    (define-key (eval keymap) key command)        ;bind to prefix
	  (define-key html-helper-mode-map key command))  ;bind to global
      )))

;;}}}

;;{{{ html-helper-smart-insert-item

;; there are two different kinds of items in HTML - those in regular
;; lists <li> and those in dictionaries <dt>..<dd>
;; This command will insert the appropriate one depending on context.

(defun html-helper-smart-insert-item (&optional arg)
  "Insert a new item, either in a regular list or a dictionary."
  (interactive "*P")
  (let ((case-fold-search t))
    (if
        (save-excursion
          (re-search-backward "<li>\\|<dt>\\|<ul>\\|<ol>\\|<dd>\\|<menu>\\|<dir>\\|<dl>\\|<if>" nil t)
          (looking-at "<dt>\\|<dl>\\|<dd>"))
        (tempo-template-html-definition-item arg)
      (tempo-template-html-item arg))))

;;}}}
;;{{{ most of the HTML cookies and keymap

;; taken partially from the HTML quick reference and the elements document
;; http://www.ncsa.uiuc.edu/General/Internet/WWW/HTMLQuickRef.html
;; http://info.cern.ch/hypertext/WWW/MarkUp/Tags.html
;; There are also some HTML+ tokens from I don't know which reference

;; I could put documentation in for each command - would that be useful?

(mapcar
 'html-helper-add-cookie
 '(
   ;;entities
   (entity  "\C-c&"   "&amp;"		"html-ampersand"	  ("&amp;")) 
   (entity  "\C-c<"   "&lt;"		"html-less-than"	  ("&lt;"))
   (entity  "\C-c>"   "&gt;"	  	"html-greater-than"       ("&gt;"))
   (entity  "\C-c "   "&nbsp;"		"html-nonbreaking-space"  ("&nbsp;"))

   ;; logical styles
   (logical "p"       "<pre>"		"html-preformatted"   	  ("<pre>" (r . "Text: ") "</pre>"))
   (logical "b"       "<blockquote>"	"html-blockquote"     	  ("<blockquote>" (r . "Quote: ") "</blockquote>"))
   (logical "e"       "<em>"		"html-emphasized"     	  ("<em>" (r . "Text: ") "</em>"))
   (logical "s"       "<strong>"	"html-strong"         	  ("<strong>" (r . "Text: ") "</strong>"))
   (logical "c"       "<code>"		"html-code"           	  ("<code>" (r . "Code: ") "</code>"))
   (logical "x"       "<samp>"		"html-sample"         	  ("<samp>" (r . "Sample code") "</samp>"))
   (logical "r"       "<cite>"		"html-citation"       	  ("<cite>" (r . "Citation: ") "</cite>"))
   (logical "k"       "<kbd>"		"html-keyboard"       	  ("<kbd>" (r . "Keyboard: ") "</kbd>"))
   (logical "v"       "<var>"		"html-variable"       	  ("<var>" (r . "Variable: ") "</var>"))
   (logical "d"       "<dfn>"		"html-definition"     	  ("<dfn>" (r . "Definition: ") "</dfn>"))
   (logical "q"       "<q>"		"html-quote"          	  ("<q>" (r . "Quote: ") "</q>"))
   (logical "n"       "<person>"	"html-person"         	  ("<person>" (r . "Person: ") "</person>"))
   (logical "y"       "<acronym>"	"html-acronym"        	  ("<acronym>" (r . "Acronym: ") "</acronym>"))
   (logical "."       "<abbrev>"	"html-abbrev"         	  ("<abbrev>" (r . "Abbrevation: ") "</abbrev>"))
   (logical "m"       "<cmd>"		"html-cmd"            	  ("<cmd>" (r . "Command name: ") "</cmd>"))
   (logical "g"       "<arg>"		"html-arg"            	  ("<arg>" (r . "Argument: ") "</arg>"))
   (logical "l"       "<lit>"		"html-lit"            	  ("<lit>" r "</lit>"))
   (logical "a"	      "<address>"	"html-address"		  ("<address>" r "</address>"))

   ;;physical styles
   (phys    "b"	      "<b>"    		"html-bold"               ("<b>" (r . "Text: ") "</b>"))
   (phys    "i"       "<i>"		"html-italic"             ("<i>" (r . "Text: ") "</i>"))
   (phys    "u"       "<u>"		"html-underline"          ("<u>" (r . "Text: ") "</u>"))
   (phys    "f"       "<tt>"		"html-fixed"              ("<tt>" (r . "Text: ") "</tt>"))
   (phys    "x"       "<s>"		"html-strikethru"         ("<s>" (r . "Text: ") "</s>"))
   (phys    "^"       "<sup>"		"html-superscript"        ("<sup>" (r . "Text: ") "</sup>"))
   (phys    "_"       "<sub>"		"html-subscript"          ("<sub>" (r . "Text: ") "</sub>"))
   (phys    "r"       "<render"		"html-render"             ("<render tag=\"" (p . "Render: ") "\" style=\"" (p . "Render as: ") "\">"))

   ;;headers
   (header  "1"	      "<h1>"     	"html-header-1"       	  ("<h1>" (r . "Header: ") "</h1>"))
   (header  "2"       "<h2>"		"html-header-2"       	  ("<h2>" (r . "Header: ") "</h2>"))
   (header  "3"       "<h3>"		"html-header-3"       	  ("<h3>" (r . "Header: ") "</h3>"))
   (header  "4"       "<h4>"		"html-header-4"       	  ("<h4>" (r . "Header: ") "</h4>"))
   (header  "5"       "<h5>"		"html-header-5"       	  ("<h5>" (r . "Header: ") "</h5>"))
   (header  "6"       "<h6>"		"html-header-6"       	  ("<h6>" (r . "Header: ") "</h6>"))

   (note    "a"	      "<abstract>"      "html-abstract"       	  ("<abstract>\n" r "\n</abstract>\n"))
   (note    "n"	      "<note role="     "html-note"           	  ("<note role=\"" (p . "Note role:") "\">" (r . "Note text: ") "</note>"))
   (note    "f"	      "<footnote>"	"html-footnote"		  ("<footnote>" (r . "Footnote: ") "</footnote>"))
   (note    "m"	      "<margin>"	"html-margin"         	  ("<margin>" (r . "Margin note: ") "</margin>"))

   ;; forms
   (form    "f"	      "<form"           "html-form"		  ("<form action=\"" (p . "Action: ") "\">\n" r "\n</form>\n"))
   (form    "t"	      "<input"		"html-input-text"	  ("<input name=\"" (p . "Name: ") "\"" " size=\"" (p . "Size: ") "\">"))
   (form    "i"	      "<input"		"html-input-int"     	  ("<input type=\"INT\" name=\"" (p . "Name: ") "\" size=\"" (p . "Number of digits: ") "\">"))
   (form    "."	      "<input"		"html-input-float"   	  ("<input type=\"FLOAT\" name=\"" (p . "Name: ") "\" size=\"" (p . "Size: ") "\">"))
   (form    "d"	      "<input"		"html-input-date"    	  ("<input type=\"DATE\" name=\"" (p . "Name: ") "\" size=\"" (p . "Size: ") "\">"))
   (form    "u"	      "<input"		"html-input-url"     	  ("<input type=\"URL\" name=\"" (p . "Name: ") "\" size=\"" (p . "Size: ") "\">"))
   (form    "c"	      "<input"		"html-input-check"   	  ("<input type=\"CHECKBOX\" name=\"" (p . "Name: ") "\">"))
   (form    "r"	      "<input"		"html-input-radio"   	  ("<input type=\"RADIO\" name=\"" (p . "Name: ") "\">"))
   (form    "g"	      "<input"		"html-input-image"   	  ("<input type=\"IMAGE\" name=\"" (p . "Name: ") "\" src=\"" (p . "Image URL: ") "\">"))
   (form    "s"	      "<input"		"html-input-scribble" 	  ("<input type=\"SCRIBBLE\" name=\"" (p . "Name: ") "\" size=\"" (p . "Size: ") "\">"))
   (form    "a"	      "<input"		"html-input-audio"    	  ("<input type=\"AUDIO\" name=\"" (p . "Name: ") "\">"))
   (form    "b"	      "<input"		"html-input-submit"   	  ("<input type=\"SUBMIT\" value=\"" (p . "Submit button text: ") "\">"))
   (form    "x"	      "<input"		"html-input-reset"    	  ("<input type=\"RESET\" value=\"" (p . "Reset button text: ") "\">"))
   (form    "p"	      "<textarea"	"html-input-textarea"	  ("<textarea name=\"" (p . "Name: ") "\" rows=" (p . "Rows: ") " cols=" (p . "Columns: ") ">\n" r "\n</textarea>\n"))
   (form    "c"	      "<select"		"html-input-select"	  ("<select name=\"" (p . "Name: ") "\">\n" r "\n\n</select>\n")"<select")


   ;;lists
   (list    "o"	      "<ol>"		"html-ordered-list"   	  (& "<ol>" > "\n<li> " > (r . "Item: ") "\n</ol>" >))
   (list    "u"	      "<ul>"		"html-unordered-list" 	  (& "<ul>" > "\n<li> " > (r . "Item: ") "\n</ul>" >))
   (list    "r"	      "<dir>"		"html-directory"      	  (& "<dir>" > "\n<li> " > (r . "Item: ") "\n</dir>" >))
   (list    "m"	      "<menu>"		"html-menu"		  (& "<menu>" > "\n<li> " > (r . "Item: ") "\n</menu>" >))
   (list    "d"	      "<dl>"		"html-definition-list" 	  (& "<dl>" > "\n<dt> " > (p . "Term: ") "\n<dd> " > (r . "Definition: ") "\n</dl>" >))
   (list    "l"       "<li>"            "html-item"               (& "<li> " > (r . "Item: ")))
   (list    "t"       "<dt>"            "html-definition-item"    (& "<dt> " > (p . "Term: ") "\n<dd> " > (r . "Definition: ")))

   ;;anchors
   (anchor  "n"	      "<a name="	"html-target-anchor"	  ("<a name=\"" (p . "Anchor name: ") "\">" (r . "Anchor text: ") "</a>"))
   (anchor  "l"	      "<a href="        "html-anchor"          	  ("<a href=\"" (p . "URL: ") "\">" (r . "Anchor text: ") "</a>"))                

   ;;graphics
   (image   "i"       "<img src="	"html-image"		  ("<img src=\"" (r . "Image URL: ") "\">"))
   (image   "t"       "<img alt="	"html-alt-image"	  ("<img alt=\"" (r . "Text URL: ") "\" src=\"" (r . "Image URL: ") "\">"))
   (image   "a"       nil               "html-align-image"	  ("<img align=\"" (r . "Alignment: ") "\" src=\"" (r . "Image URL: ") "\">"))
   (image   "e"       "<img align="     "html-align-alt-image"	  ("<img align=\"" (r . "Alignment: ") "\" src=\"" (r . "Image URL: ") "\" alt=\"" (r . "Text URL: ") "\">"))

   ;;interform control structures
   (struct   "r"       "<repeat>"	"interform-repeat"	  ("<repeat " (P . "Attribute entry(list=\"...\" | start=start stop=stop [step=step] [entity=\"name\"]): ") ">" (r) "\n</repeat>") "Repeat content with entity (default &amp;li;) in list. start and stop specify a numeric list from start to stop with step stepsize (default +/-1). Return the repeated content")
   (struct   "i"       "<if>"	        "interform-if"	          (&"<if>" "\n<then>" > (p . "Statements: ")"</then>" "\n<else>" > (p . "Statements: ") "</else>" "\n</if>" >) "If test non-null, expand then, else else.")
   (struct   "f"       "<foreach>"	"interform-foreach"	  ("<foreach " (P . "Attribute entry(list=\"list\" [entity=ident]): ") ">" (r . "Content: ") "\n</foreach>") "Repeat content for each entity (default &amp;li;) in list. Return the repeated
    content")

   ;;interform tests
   (intftests   "t"      "<test>"	"interform-test"	  ("<test " 
								  (P "Attribute entry ([iftrue=\"value\"][iffalse=\"value\"])<cr> to see more: ") " "
								  (P "Attribute entry ([not][text|link][zero|positive|negative|null||markup|match=\"pattern\"[exact][case]]): ")
 ">" (r) "\n</test>") "Test content; return null or iffalse if false, else '1' or iftrue. Tests: default (non-whitespace), zero, positive, negative, null, (contains) markup, match='pattern'. Modifiers: not, case (sensitive), text, link, exact (match)")
   (intftests   "e"       "<equal>"	"interform-equal"	  ("<equal " (P . "Attribute entry([not][case][text][numeric]): ") ">" (r) "</equal>") "Test list items in content for equality; return null or iffalse if false, else '1' or iftrue. Modifiers: not, case (sensitive), text, numeric.")
   (intftests   "s"       "<sorted>"	"interform-sorted"	  ("<sorted " (P . "Attribute entry([case][text][numeric][reverse]): ") ">" "</sorted>") "Test whether items in content are sorted. Optionally case (sensitive), text, numeric, reverse. ")

   ;;interform numeric processing
   (intfnum   "s"      "<sum>"	"interform-sum"	  ("<sum " (P . "Attribute entry([digits=D]): ") ">" (r) "\n</sum>") "Return sum of numbers in content. Shows D digits after the decimal point.  (Default D=-1 which shows all digits.)")
   (intfnum   "d"      "<difference>"	"interform-difference"	  ("<difference " (P . "Attribute entry([digits=D]): ") ">" (r) "\n</difference>") "Return difference of numbers in content Shows D digits after the decimal point. (Default D=-1 which shows all digits.) ")
   (intfnum   "p"      "<product>"	"interform-product"	  ("<product "  (P . "Attribute entry([digits=D]): ")   ">" (r) "\n</product>") "Return product of numbers in content Shows D digits after the decimal point. (Default D=-1 which shows all digits.) ")
   (intfnum   "q"      "<quotient>"	"interform-quotient"	  ("<quotient " (P . "Attribute entry([digits=D]): ") ">" (r) "\n</quotient>") "Return difference (n1/n2/...) of numbers in content. Shows D digits after the decimal point. (Default D=-1 which shows all digits.)")

   ;;interform list processing
   (intflistproc   "s"      "<sort>"	"interform-sort"	  ("<sort " (P "Attribute entry ([case][text][numeric][reverse]): ") ">" (r) "\n</sort>") "Sort items in content. Optionally case (sensitive), text, numeric, reverse. ")

   ;;interform form processing
   (intfform   "f"      "<form>"	"interform-form"	  ("<form " (P "Attribute entry ([submit][copy][if-processed=\"repl\"]): ") ">" "<process>"(r) "</process>" "\n</form>") "html <form> tag. Process or copy content if submitted, and contains <process> element.  Optional id. Optionally submit. If processed, replace with value of if-processed if present. Increment &forms; entity when finished.")

   (intfform   "s"      "<submit-forms>"	"interform-submit-forms"	  ("<submit-forms " 
										  (P "Attribute entry ([hour=hh][minute=mm][day=dd][month=[\"name\"|mm]][weekday=[\"name\"|n]])<cr> to see more: ") " "
										  (P "Attribute entry ([repeat=count][until=mm-dd-hh]): ")
 ">" (r . "<a href=\"query\">...</a>|...form...") "\n</submit-forms>") "Submit a form or link element or every form (not links) in content.  Optionally submit at hour, minute, day, month, weekday. Optionally repeat=N times (missing hour, day, month, weekday are wildcards).Optionally until=mm-dd-hh time when submissions are halted. Use options interform of agent to delete repeating entries. ")

   ;;interform name bindings
   (intfnb   "s"      "<set>"	"interform-set"	  ("<set " (P "Attribute entry (name=\"name\" | index=\"index\"): ") " "                                              (P "Attribute entry ([copy][attr=attr|insert=where[replace]])<cr> to see more: ") " "
						   (P "Attribute entry ([pia|agent|trans[feature]|env|[element[tag=ident]]|)<cr> to see more: " where 'noinsert)
						   (if (equal (tempo-lookup-named 'where) "") (tempo-forget-insertions))
						   (P "Attribute entry (entity[global|local]]): " where 'noinsert)
(s where)
 ">" (r) "</set>" ""))

   (intfnb   "g"      "<get>"	"interform-get"	  ("<get " (P "Attribute entry ([name=\"name\" | index=\"index\"]): ") " " 
						  (P "Attribute entry ([file=\"filename\"|href=\"url\"|[file|href] name=\"string\"])<cr> to see more: " where 'noinsert) 
						  (if (equal (tempo-lookup-named 'where) "") (tempo-forget-insertions))
						  (P "Attribute entry (pia|agent|form|trans|env|element[tag=tag]|local|global): " where 'noinsert)
 (s where) " "
                                                  (P "Attribute entry ([attr=attr|size|tree|row=row col=col|rows=rows cols=cols|): " misc 'noinsert)
						  (if (equal (tempo-lookup-named 'misc) "") (tempo-forget-insertions))
                                                  (P "Attribute entry (key=key|keys|values|findAll=ftag|findfirst=ftag]): " misc 'noinsert)
 (s misc)						 
 ">" ""))

   ;;interform string processing
   (intfsp   "a"       "<add-markup>"   "interform-add-markup"  ("<add-markup>" (r) "</add-markup>"))
   (intfsp   "p"       "<pad>"          "interform-pad"         ("<pad " (P "Attribute entry(width=N [align=[left|right|center]][spaces]): ") ">" (r) "</pad>"))
   (intfsp   "t"       "<trim>"         "interform-trim"        ("<trim " (P "Attribute entry([all]): ") ">"(r) "</trim>"))

   ;;interform calendar
   (intfca   "c"       "<calendar>"      "interform-calendar"   ("<calendar " (P "Attribute entry(month=\"month\" year=\"year\"): ") ">")) 
   (intfca   "d"       "<calendar-day>"  "interform-calendar-day"   ("<calendar-day " (P "Attribute entry(cal=\"calendar-table\" day=\"day\"): ") ">" (r) "</calendar-day>")) 


   ;;interform os processing
   (intfos   "a"       "<authenticate>"   "interform-authenticate"  ("<authenticate " (P "Attribute entry(user=ident password=\"string\"[file=\"fn\"]): ") ">"))
   (intfos   "o"       "<os-command>"   "interform-os-command"  ("<os-command " (P "Attribute entry([bypass]): ") ">" (r) "</os-command>"))
   (intfos   "t"       "<os-command-output>"   "interform-os-command-output"  ("<os-command-output " (P "Attribute entry([bypass]): ") ">" (r) "</os-command-output>"))
   (intfos   "p"       "<password-file-entry>"   "interform-password-file-entry"  ("<password-file-entry " (P "Attribute entry(user=ident password=\"string\"[file=\"fn\"]): ") ">" (r) "</password-file-entry>"))
   (intfos   "r"       "<read>"   "interform-read"  ("<read " 
        (P "Attribute entry([file=\"name\"[interform[agent=\"agentName\"]][info|head|directory[links][tag=tag][all|match=\"regexp\"]] |)<cr> to see more: " file 'noinsert)
        (if (equal (tempo-lookup-named 'file) "") (tempo-forget-insertions))
        (P "Attribute entry (href=\"url\" [resolve] ]): " file 'noinsert)
(s file) " "
        (P "Attribute entry ([base=\"path\"] [process [tagset=\"name\"]]): ")
 ">"))
    (intfos   "w"       "<write>"   "interform-write"  ("<write " 
        (P "Attribute entry([file=\"name\" [interform] [append] | href=\"url\" [post]]): ") " "
        (P "Attribute entry([base=\"path\"][trim][line][copy[protect [markup]]]): ")
 ">" (r) "</write>"))


   ;;interform os processing
   (intfac   "a"       "<agent-criteria>"   "interform-agent-criteria"  ("<agent-criteria " (P "Attribute entry([agent=ident]): ") ">"))
   (intfac   "h"       "<agent-home>"   "interform-agent-home"  ("<agent-home " (P "Attribute entry(name=ident [link]): ") ">"))
   (intfos   "i"       "<agent-install>"   "interform-agent-install"  ("<agent-install " (P "Attribute entry(name=ident [type=ident]): ") ">" (r) "</agent-install>"))
   (intfac   "l"       "<agent-list>"   "interform-agent-list"  ("<agent-list " (P "Attribute entry([type=type][subs]): ") ">"))
   (intfac   "o"       "<agent-options>"   "interform-agent-options"  ("<agent-options " (P "Attribute entry([name=\"agent-name\"]): ") ">"))
   (intfac   "r"       "<agent-remove>"   "interform-agent-remove"  ("<agent-remove " (P "Attribute entry(name=\"agent-name\"): ") ">"))
   (intfac   "n"       "<agent-running>"   "interform-agent-running"  ("<agent-running " (P "Attribute entry(name=\"agent-name\"): ") ">"))
   (intfac   "s"       "<agent-set-criterion>"   "interform-agent-set-criterion"  ("<agent-set-criterion " (P "Attribute entry(name=\"name\"[value=\"value\"][agent=\"agent-name\"]): ") ">"))
   (intfac   "p"       "<agent-set-options>"   "interform-agent-set-options"  ("<agent-set-options " (P "Attribute entry([name=\"agent-name\"]): ") ">" (r) "</agent-set-options>"))



   ;;text elements
   (textel  "\e\C-m"  nil		"html-paragraph"	  ("<p>\n"))
   (textel  "\C-c-"    nil		"html-horizontal-rule"	  (& "<hr>\n"))
   (textel  "\C-c\C-m" nil		"html-break"		  ("<br>\n"))

   ;;head elements
   (head    "t"	      "<title>"		"html-title"           ("<title>" (r . "Document title: ") "</title>"))
   (head    "i"	      "<isindex>"	"html-isindex"         ("<isindex>\n"))
   (head    "n"	      "<nextid>"	"html-nextid"          ("<nextid>\n"))
   (head    "l"	      "<link"		"html-link"            ("<link href=\"" p "\">"))
   (head    "b"       "<base"		"html-base"            ("<base href=\"" r "\">"))
   ))

;;}}}

;;{{{ context guessing

;; guess where we are in indented lists based on the last list token.
;; it would be much better to try to match </ul> to <ul>, and </ol> to <ol>
;; etc, but that is pretty unwieldy and slow.

(defvar html-helper-any-list-item "<li>\\|<dt>\\|<dd>")
(defvar html-helper-any-list-start "<dl>\\|<ul>\\|<ol>\\|<menu>\\|<dir>\\|<if>")
(defvar html-helper-any-list-end "</dl>\\|</ul>\\|</ol>\\|</menu>\\|</dir>\\|</if>")
(defvar html-helper-any-list (format "\\(%s\\)\\|\\(%s\\)\\|\\(%s\\)"
                                     html-helper-any-list-item
				     html-helper-any-list-start
				     html-helper-any-list-end))
(defvar html-helper-search-limit 2000 "limit on how far back we search")

(defun html-helper-guess-context ()
  "figure out what the last list type thing before point is."
  (save-excursion
    (let* ((lim (max (point-min) (- (point) html-helper-search-limit)))
	   (context (if (re-search-backward html-helper-any-list lim t)
			(cond ((match-beginning 1) 'item)
			      ((match-beginning 2) 'start)
			      ((match-beginning 3) 'end)
			      (t 'error))
		      nil)))
      (cons context (current-indentation)))))

(defun html-helper-print-context ()
  (interactive)
  (message "%s" (html-helper-guess-context)))

;;}}}
;;{{{ indentation

(defvar html-helper-print-indent-info nil
  "If t, indent will print out information as a message.")

(defun html-helper-indent-command ()
  "Command for indenting text. Just calls html-helper-indent."
  (interactive)
  (html-helper-indent))

;; some of the ideas are borrowed from cc-mode.el.

;; lots of special cases because we're not doing true parsing, we're
;; trying to guess what to do based on what the last item cookie was.

;; this code works best if the cookies that are the beginnings of menus
;; are on the left end of the line, and are already indented.

(defun html-helper-indent ()
  "indentation workhorse function."
  (if html-helper-never-indent
      ()
    (let ((m (point-marker))
	  (bol (progn (beginning-of-line) (point))))

      ;; unindent the line
      (delete-region (point) (progn (back-to-indentation) (point)))

      (let* ((where (html-helper-guess-context))
	     (context (car where))
	     (previ (cdr where))
	     (newi (cond 
		    ((eq context 'end) previ)
		    ((eq context 'item) previ)
		    ((eq context 'start) (+ previ html-helper-basic-offset))
		    (t previ))))

	;; newi is set to the basic indentation, now adjust indentation
	;; based on what the current line is.
	(if (looking-at html-helper-any-list)
	    (cond

	     ;; list token and last line was an end?
	     ;; Probably inside a continued item - go backwards.
	     ((and (match-beginning 1) (eq context 'end))
	      (setq newi (- newi html-helper-item-continue-indent)))

	     ;; end of list and last line was an end?
	     ;; Probably inside a continued item - go backwards twice
	     ((and (match-beginning 3) (eq context 'end))
	      (setq newi (- newi html-helper-item-continue-indent html-helper-basic-offset)))

	     ;; Any other end of list?
	     ;; Indent negative
	     ((match-beginning 3)
	      (setq newi (- newi html-helper-basic-offset)))

	     ;; start of list and last line
	     ;; Beginning of continued item - go forwards
	     ((and (match-beginning 2) (eq context 'item))
	      (setq newi (+ newi html-helper-item-continue-indent))))

	  ;; we're not any sort of item, must be text.
	  (cond
	   ;; last line an item?
	   ;; Beginning of continued item - go forward
	   ((eq context 'item)
	    (setq newi (+ newi html-helper-item-continue-indent)))))

	(if html-helper-print-indent-info
	    (message "Context: %s, Previous: %s New: %s" context previ newi))

	;; just in case
	(if (< newi 0)
	    (setq newi 0))
	(indent-to newi newi)

	;; adjust point to where it was before, or at start of indentation
	(goto-char (marker-position m))
	(if (< (current-column) (current-indentation))
	    (back-to-indentation))))))

;;}}}
;;{{{ completion finder for tempo

(defvar html-helper-completion-finder
  (if html-helper-emacs18
      'html-helper-emacs18-completion-finder
    "\\(\\(<\\|&\\).*\\)\\=")
  "Passed to tempo-use-tag-list, used to find tags to complete.")

;; The regexp finds everything between the last < or & and point,
;; which is good enough to match the tags HTML might complete.
;; emacs18 doesn't have the \= for regexps, though, so we do something
;; more hackish.

(defun html-helper-emacs18-completion-finder ()
  "Unfortunately emacs18 doesn't support \\= in regexps, so we do this hack.
If you have problems with it, maybe you should upgrade to emacs19 :-)"
  (let* ((where nil)
	 (s (buffer-substring
	     (point)
	     (setq where (save-excursion
			   (re-search-backward "<\\|&" (min (point-min) 100) t)
			   (point))))))
    (cons s where)))

;;}}}

;;{{{ timestamps

(defun html-helper-update-timestamp ()
  "Basic function for updating timestamps. It finds the timestamp in
the buffer by looking for html-helper-timestamp-start, deletes all text
up to html-helper-timestamp-end, and runs html-helper-timestamp-hook
which will presumably insert an appropriate timestamp in the buffer."
  (save-excursion
    (goto-char (point-max))
    (if (not (search-backward html-helper-timestamp-start nil t))
	(message "timestamp delimiter start was not found")
      (let ((ts-start (+ (point) (length html-helper-timestamp-start)))
	    (ts-end (if (search-forward html-helper-timestamp-end nil t)
			(- (point) (length html-helper-timestamp-end))
		      nil)))
	(if (not ts-end)
	    (message "timestamp delimiter end was not found. Type C-c C-t to insert one.")
	  (delete-region ts-start ts-end)
	  (goto-char ts-start)
	  (run-hooks 'html-helper-timestamp-hook)))))
  nil)

(defun html-helper-default-insert-timestamp ()
  "Default timestamp insertion function"
  (insert "Last modified: "
	  (current-time-string)
	  "\n"))

(defun html-helper-insert-timestamp-delimiter-at-point ()
  "Simple function that inserts timestamp delimiters at point, useful
for adding timestamps to existing buffers."
  (interactive)
  (insert html-helper-timestamp-start)
  (insert html-helper-timestamp-end))

;;}}}
;;{{{ html-helper-insert-new-buffer-strings

(tempo-define-template "html-skeleton" html-helper-new-buffer-template
		       nil
		       "Insert a skeleton for a HTML document")

(defun html-helper-insert-new-buffer-strings ()
  "Insert html-helper-new-buffer-strings."
  (tempo-template-html-skeleton))

;;}}}

;;{{{ html-helper-mode

(defun html-helper-mode ()
  "
Mode for editing HTML documents. For more documentation and the newest
version, see http://www.reed.edu/~nelson/tools/

The main function html-helper-mode provides is a bunch of keybindings
for the HTML cookies one inserts when writing HTML documents. Typing
the key sequence for a command inserts the corresponding cookie and
places point in the right place. If a prefix argument is supplied, the
cookie is instead wrapped around the region. Alternately, one can type
in part of the cookie and complete it.

There is also code for indentation, timestamps, skeletons for new
documents, and lots of other neat features.

\\{html-helper-mode-map}
Written by nelson@reed.edu, http://www.reed.edu/~nelson/
"
  (interactive)
  (kill-all-local-variables)

  (use-local-map html-helper-mode-map)
  (setq local-abbrev-table html-helper-mode-abbrev-table)
  (set-syntax-table html-helper-mode-syntax-table)

  (setq mode-name "HTML helper")
  (setq major-mode 'html-helper-mode)

  (make-local-variable 'comment-start)
  (make-local-variable 'comment-end)
  (make-local-variable 'comment-column)
  (make-local-variable 'comment-start-skip)
  (make-local-variable 'indent-line-function)

  (setq comment-start "<!-- "
	comment-end " -->"
	comment-start-skip "<!--[ \t]*"
	comment-column 0
	indent-line-function 'html-helper-indent)

  (tempo-use-tag-list 'html-helper-tempo-tags html-helper-completion-finder)
  
  (if html-helper-do-write-file-hooks
      (add-hook 'local-write-file-hooks 'html-helper-update-timestamp))

  (if (and html-helper-build-new-buffer (zerop (buffer-size)))
      (html-helper-insert-new-buffer-strings))
  
  (run-hooks 'html-helper-mode-hook))

;;}}}

;;{{{ patterns for hilit19

;; Define some useful highlighting patterns for the hilit19 package.
;; These will activate only if the function hilit-set-mode-patterns
;; is already bound - ie, if hilit19 has already been loaded when this
;; mode is loaded. Nonoptimal. I could put this in the mode function,
;; but then that has other problems.

;; suggestions for highlight patterns are most welcome. I tried to
;; choose a middle ground between lots of highlighting (ugly and slow)
;; and only a little bit (not so useful).

(if (fboundp 'hilit-set-mode-patterns)
    (hilit-set-mode-patterns
     'html-helper-mode
     '(("<!--" "-->" comment)           
       ("<a\\b" ">" define)
       ("</a>" nil define)
       ("<img\\b" ">" include)
       ("<b>" "</b>" bold)
       ("<i>" "</i>" italic)
       ("<u>" "</u>" underline)
       ("&" ";" string)
       ("<" ">" keyword))
     nil 'case-insensitive)
  nil)

;;}}}

(provide 'html-helper-mode)

(setq mymenu (list "Interform"      
		                    (vector "Agent-criteria" 'tempo-template-interform-agent-criteria t)
				    (vector "Agent-home" 'tempo-template-interform-agent-home t)
				    (vector "Agent-install" 'tempo-template-interform-agent-install t)
				    (vector "Agent-list" 'tempo-template-interform-agent-list t)
				    (vector "Agent-options" 'tempo-template-interform-agent-options t)
				    (vector "Agent-remove" 'tempo-template-interform-agent-remove t)
				    (vector "Agent-running" 'tempo-template-interform-agent-running t)
				    (vector "Agent-set-criterion" 'tempo-template-interform-agent-set-criterion t)
				    (vector "Agent-set-options" 'tempo-template-interform-agent-set-options t)

		                    (vector "Cal-calendar" 'tempo-template-interform-calendar t)
				    (vector "Cal-calendar-day" 'tempo-template-interform-calendar-day t)

				    (vector "Formproc-form" 'tempo-template-interform-form t)
				    (vector "Formproc-submit-forms" 'tempo-template-interform-submit-forms t)

				    (vector "Listproc-sort" 'tempo-template-interform-sort t)

				    (vector "Namebnd-set" 'tempo-template-interform-set t)
				    (vector "Namebnd-get" 'tempo-template-interform-get t)

				    (vector "Numproc-sum" 'tempo-template-interform-sum t)
				    (vector "Numproc-difference" 'tempo-template-interform-difference t)
				    (vector "Numproc-product" 'tempo-template-interform-product t)
				    (vector "Numproc-quotient" 'tempo-template-interform-quotient t)

				    (vector "OS-authenticate" 'tempo-template-interform-authenticate t)
				    (vector "OS-command" 'tempo-template-interform-os-command t)
				    (vector "OS-command-output" 'tempo-template-interform-os-command-output t)
				    (vector "OS-password-file-entry" 'tempo-template-interform-password-file-entry t)
				    (vector "OS-read" 'tempo-template-interform-read t)
				    (vector "OS-write" 'tempo-template-interform-write t)

				    (vector "Stringp-add-markup" 'tempo-template-interform-add-markup t)
				    (vector "Stringp-pad" 'tempo-template-interform-pad t)
				    (vector "Stringp-trim" 'tempo-template-interform-trim t)

		                    (vector "Struct-if" 'tempo-template-interform-if t)
		                    (vector "Struct-foreach" 'tempo-template-interform-foreach t)
				    (vector "Struct-repeat" 'tempo-template-interform-repeat t)
				    (vector "Test-test" 'tempo-template-interform-test t)
				    (vector "Test-equal" 'tempo-template-interform-equal t)
				    (vector "Test-sorted" 'tempo-template-interform-sorted t)
))


(easy-menu-define  SYMBOL html-helper-mode-map "" mymenu)
(run-hooks 'html-helper-load-hook)

;;; html-helper-mode.el ends here



