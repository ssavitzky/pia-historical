/****************************************************************************
 * NCSA Mosaic for the X Window System                                      *
 * Copyright (C) 1993                                                       *
 * National Center for Supercomputing Applications                          *
 * Software Development Group                                               *
 * 605 E. Springfield, Champaign IL 61820                                   *
 *                                                                          *
 * The NCSA software Mosaic, both binary and source, is copyrighted,        *
 * but available without fee for education, academic research and           *
 * non-commercial purposes.  The software is copyrighted in the name of     *
 * the University of Illinois, and ownership of the software remains with   *
 * the University of Illinois.  Users may distribute the binary and         *
 * source code to third parties provided that the copyright notice and      *
 * this statement appears on all copies and that no charge is made for      *
 * such copies.  Any entity wishing to integrate all or part of the         *
 * source code into a product for commercial use or resale, should          *
 * contact the University of Illinois, c/o NCSA, to negotiate an            *
 * appropriate license for such commercial use.                             *
 *                                                                          *
 * THE UNIVERSITY OF ILLINOIS MAKES NO REPRESENTATIONS ABOUT THE            *
 * SUITABILITY OF THE SOFTWARE FOR ANY PURPOSE.  IT IS PROVIDED "AS IS"     *
 * WITHOUT EXPRESS OR IMPLIED WARRANTY.  THE UNIVERSITY OF ILLINOIS SHALL   *
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY THE USER OF THIS SOFTWARE.     *
 * The software may have been developed under agreements between the        *
 * University of Illinois and the Federal Government which entitle the      *
 * Government to certain rights.                                            *
 *                                                                          *
 * By copying this program, you, the user, agree to abide by the            *
 * copyright conditions and understandings with respect to any software     *
 * which is marked with a copyright notice.                                 *
 *                                                                          *
 * If you have problems or comments about NCSA Mosaic, please feel free     *
 * to mail them to marca@ncsa.uiuc.edu.                                     *
 ****************************************************************************/

#ifndef HTML_PLAIN__H
#define HTML_PLAIN_H

/*
 * Public Structures
 */

/*
 * defines and structures used for the HTML parser, and the
 * parsed object list.
 */

typedef enum {
  M_UNKNOWN, M_NONE, M_TITLE, M_HEADER_1, M_HEADER_2, M_HEADER_3,
  M_HEADER_4, M_HEADER_5, M_HEADER_6, M_ANCHOR, M_PARAGRAPH,
  M_ADDRESS, M_PLAIN_TEXT, M_UNUM_LIST, M_LIST_ITEM, M_DESC_LIST,
  M_DESC_TITLE, M_DESC_TEXT, M_PREFORMAT, M_PLAIN_FILE, M_LISTING_TEXT,
  M_INDEX, M_MENU, M_DIRECTORY, M_IMAGE, M_NUM_LIST, M_EM,
  M_TT, M_B, M_I, M_U, M_STRONG, M_CODE, M_SAMP, M_KBD, M_VAR, M_DFN,
  M_CITE, M_HRULE, M_LINEBREAK, M_COMMENT, M_SENTINEL
  } mark_t;

/* amperstand escapes */
#define	A_LESS_THAN	"&lt"
#define	A_GREATER_THAN	"&gt"
#define	A_AMPERSTAND	"&amp"


/* syntax of Mark types */
#define	MT_TITLE	"title"
#define	MT_HEADER_1	"h1"
#define	MT_HEADER_2	"h2"
#define	MT_HEADER_3	"h3"
#define	MT_HEADER_4	"h4"
#define	MT_HEADER_5	"h5"
#define	MT_HEADER_6	"h6"
#define	MT_ANCHOR	"a"
#define	MT_PARAGRAPH	"p"
#define	MT_ADDRESS	"address"
#define	MT_PLAIN_TEXT	"xmp"
#define	MT_UNUM_LIST	"ul"
#define	MT_NUM_LIST	"ol"
#define	MT_LIST_ITEM	"li"
#define	MT_DESC_LIST	"dl"
#define	MT_DESC_TITLE	"dt"
#define	MT_DESC_TEXT	"dd"
#define	MT_PREFORMAT	"pre"
#define	MT_PLAIN_FILE	"plaintext"
#define MT_LISTING_TEXT	"listing"
#define MT_INDEX	"isindex"
#define MT_MENU		"menu"
#define MT_DIRECTORY	"dir"
#define MT_IMAGE	"img"
#define MT_FIXED	"tt"
#define MT_BOLD		"b"
#define MT_ITALIC	"i"
#define MT_EMPHASIZED	"em"
#define MT_STRONG	"strong"
#define MT_CODE		"code"
#define MT_SAMPLE	"samp"
#define MT_KEYBOARD	"kbd"
#define MT_VARIABLE	"var"
#define MT_CITATION	"cite"
#define MT_BLOCKQUOTE	"blockquote"
#define MT_STRIKEOUT	"strike"
#define MT_INPUT	"input"
#define MT_FORM		"form"
#define MT_HRULE	"hr"
#define MT_LINEBREAK	"br"
#define MT_BASE		"base"
#define MT_SELECT	"select"
#define MT_OPTION	"option"
#define MT_TEXTAREA	"textarea"
#define MT_COMMENT      "!"

/* anchor tags */
#define	AT_NAME		"name"
#define	AT_HREF		"href"



struct mark_up {
	mark_t type;
	int is_end;
	char *start;
	char *text;
	char *end;
	struct mark_up *next;
};



/*
 * Public routines
 */

#ifdef NO_PROTO
extern struct mark_up *HTMLParse();
extern void FreeObjList();
extern struct mark_up *AddObj();
extern void PrintType();
extern void PrintList();
extern char * ParseMarkTag();

#else
extern struct mark_up *HTMLParse(struct mark_up *old_list, char *str);
extern void FreeObjList(struct mark_up *);
extern struct mark_up *AddObj(struct mark_up **listp, struct mark_up *current, struct mark_up *mark, int keep_wsp);
extern void PrintType(mark_t type);
extern void PrintList(struct mark_up *list);
extern char * ParseMarkTag(char *text,char *mtext,char *mtag);
#endif


#endif /* HTML_PLAIN_H */

