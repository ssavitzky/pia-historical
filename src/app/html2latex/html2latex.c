/*
 * html2latex
 *
 * Copyright (c) 1993 by Nathan Torkington.  Educational and commercial
 * use permitted.  Adaptation of this code by permission of Nathan
 * Torkington only.
 */

#include "HTMLparse.h"
#include <stdio.h> 
#include <stdlib.h>
/* now in stdlib #include <getopt.h>*/
#include <unistd.h>
#include <ctype.h>
#include <string.h>


/*
 * USAGE definition.
 *   Expects to be called as printf(USAGE, argv[0])
 */

#define USAGE "usage:\n\t%s [opt ...] [file ...]\n\twhere opt is one of \n\t\t-n (number sections)\n\t\t-t string (set title to string)\n\t\t-a string (set author to string)\n\t\t-p (page break after title/table of contents)\n\t\t-c (table of contents)\n\t\t-h string (string appears after \\begin{document})\n\t\t-f string (string appears before \\end{document})\n\t\t-o (specify LaTeX \\documentstyle options\n\t\t-s (write to stdout)\n\tIf file is - then input is taken from stdin and written to stdout.\n"
/*
 * ID
 *   The comment text added to the top of every file converted
 */
#define ID "% This file was converted from HTML to LaTeX with Nathan Torkington's\n% html2latex program\n"
/*
 * VER
 *   Version number of the program
 */
#define VER "0.9c-GW1"

					/* Added hack for images */
					/* HTML should be preprocessed and all the images 
					 brought over and stored in the local
					 directory (where this is being run)
					 as htmlIMG.#.ps 
					 Where the # indicates the number of the image
					 as it occurs in the file starting with 1*/
int gw_imagenumber = 1;
char gw_imagename[50];
#define GW_IMAGE_NAME  sprintf(gw_imagename,"htmlIMG.%d.ps",gw_imagenumber)
#define GW_PSFIG_STRING "\\psfig{figure=%s}",gw_imagename
 
FILE * gw_afile;
int gw_anchor_num=102;
int gw_anchor_nums[] = {    0,1,0,2    };



char gw_anchor_string[100];
#define GW_MIN_DIG 6
#define GW_TEX_HEADER "\\include{/usr/local/lib/pia/tex/codemacro}\n"
#define GW_ANCHOR_START_STRING sprintf(gw_anchor_string,"\\encode{%d}{",gw_anchor_num_next())   /*puts down code*/
#define GW_ANCHOR_END_STRING "}"

#define GW_ANCHOR_FILE_NAME  "/tmp/URLS/allUrls"
#define GW_SAVE_ANCHOR(text) fprintf(gw_afile,"%d %s \n",gw_anchor_num,text)
/*
 * type definitions
 */

/* state_t is used by the LaTeX converter */
typedef enum {
  s_quit, s_normal, s_title, s_header, s_list, s_describing, s_verbatim
} state_t;

/* option_t is used to enumerate the options */
typedef enum {
  o_author, o_title, o_number, o_page, o_contents, o_stdout, o_header,
  o_footer, o_options, o_error
} option_t;

/*
 * global variables for options
 * in C++, these would be in an option type and not global
 */

char *option_string="npcst:a:h:f:o:";	/* getopts string */
char option[o_error+1];			/* booleans -- if given or not */
char *option_s[o_error+1];		/* strings if needed */
int option_i[o_error+1];		/* integers if needed */

/* global variable for the name of the program (as invoked) */
char *program;



/*
 * code
 */

/***** GW function to calculate anchor number ***/
int gw_anchor_num_next()
{
    static int curr_dig = 2;
    int oldnum,i;
    int changed=0;
    
    oldnum = gw_anchor_num;
    curr_dig = 3;
    while((!changed) && curr_dig)
    {
	if((gw_anchor_nums[curr_dig] < gw_anchor_nums[curr_dig-1]) || (gw_anchor_nums[curr_dig] < GW_MIN_DIG))
	{ gw_anchor_nums[curr_dig]++;
	  changed=1;
	  for(i=curr_dig+1;i<4;i++) gw_anchor_nums[i] = 0;
      }
	curr_dig--;
    }
    if(!changed) {
	gw_anchor_nums[0] += 2;
	changed=1;
	gw_anchor_nums[1]=1;
	gw_anchor_nums[2]=0;
	gw_anchor_nums[3]=1;
    }
    
    gw_anchor_num = 1000*gw_anchor_nums[0]+100*gw_anchor_nums[1]+10*gw_anchor_nums[2]+gw_anchor_nums[3];

    return(oldnum);
}


/*
 * LaTeXProof
 *   takes a FILE * to write to, and some text to encode.  Ensures that
 * under normal LaTeX conditions, the text will be reproduced accurately.
 */
void
  LaTeXProof(FILE *fpout, char *text)
{
  char *tptr;

  /* step through string, character by character */
  tptr = text;
  while (*tptr) {
    switch(*tptr) {
    case '<':
    case '>':
      fprintf(fpout, "$%c$", *tptr);
      break;
    case '^':
      fprintf(fpout, "\\^{}");
      break;
    case '\\':
      fprintf(fpout, "$\\backslash$");
      break;
    case '$':
	  case '{':
    case '}':
    case '%':
    case '&':
    case '#':
    case '_':
    case '~':
      fputc('\\', fpout);
    default:
      fputc(*tptr, fpout);
      break;
    }
    tptr++;
  }
}


/*
 * ListToLaTeX
 *   Takes a FILE * to write to, a pointer into a list of parsed HTML
 * markup, and a state to begin reading in.  Reads and converts into
 * LaTeX the parsed HTML.  Returns NULL if successful, otherwise a
 * pointer to the place where decoding ended.
 */

int outstanding_braces;

struct mark_up *
  ListToLaTeX(FILE *fpout, struct mark_up *mptr, state_t *state)
{
  char *text;
  char *anchortext;
  
  mark_t type;
  state_t current = *state;
  state_t temp_s;

  while (current != s_quit && mptr) {
    type = mptr->type;
    text = mptr->text ? mptr->text : "";
    switch(type) {
    case M_NONE:
      if (current == s_title)		/* no titles please, we're British */
	break;
      if (current == s_verbatim)
	fprintf(fpout, "%s", text);
      else
	LaTeXProof(fpout, text);
      break;
    case M_COMMENT:
      break;
      
    case M_TITLE:
      if (mptr->is_end) {
/* we don't print titles, but just in case, here's some base code */
/*	fprintf(fpout, "}");*/
	current = s_normal;
	break;
      }
/*      fprintf(fpout, "\\title{");*/
      current = s_title;
      break;
					/* Add stuff for doing rule */
    case M_HRULE:
      fprintf(fpout, "\n\n\\rule{7in}{0.75pt} \n\n");
      break;
					/* Add linebreaks */
    case M_LINEBREAK:
      fprintf(fpout, "\n\\\\ \n");
      break;
					/* Add stuff for preformatted lists */
					/* GJW 11/8/94 */
    case M_PREFORMAT:
      if (mptr->is_end) {
	  fprintf(fpout,"\\end{preform}"); 
    
	break;
      }
	  fprintf(fpout,"\\begin{preform}"); 
      break;
	
    case M_HEADER_1:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	current = s_normal;
	break;
      }
      fprintf(fpout, "\\section%s{", option_s[o_number]);
      current = s_header;
      break;
    case M_HEADER_2:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	current = s_normal;
	break;
      }
      fprintf(fpout, "\\subsection%s{", option_s[o_number]);
      current = s_header;
      break;
    case M_HEADER_3:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	current = s_normal;
	break;
      }
      fprintf(fpout, "\\subsubsection%s{", option_s[o_number]);
      current = s_header;
      break;
    case M_HEADER_4:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	current = s_normal;
	break;
      }
      fprintf(fpout, "\\paragraph%s{", option_s[o_number]);
      current = s_header;
      break;
    case M_HEADER_5:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	current = s_normal;
	break;
      }
      fprintf(fpout, "\\subparagraph%s{", option_s[o_number]);
      current = s_header;
      break;
    case M_HEADER_6:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	current = s_normal;
	break;
      }
      fprintf(fpout, "\\subparagraph%s{", option_s[o_number]);
      current = s_header;
      break;
    case M_PARAGRAPH:
      while(outstanding_braces)
      {
      fprintf(fpout, "]");
      outstanding_braces--;
  }
      
      fprintf(fpout, "\\par ");
      break;
    case M_UNUM_LIST:
    case M_MENU:
      if (mptr->is_end) {
	fprintf(fpout, "\\end{itemize}");
	return mptr;
      }
      fprintf(fpout, "\\begin{itemize}");
      mptr = mptr->next;
      temp_s = s_list;
      mptr = ListToLaTeX(fpout, mptr, &temp_s);
      if (!mptr)
	current = temp_s;
      break;
    case M_NUM_LIST:
      if (mptr->is_end) {
	fprintf(fpout, "\\end{enumerate}");
	return mptr;
      }
      fprintf(fpout, "\\begin{enumerate}");
      mptr = mptr->next;
      temp_s = s_list;
      mptr = ListToLaTeX(fpout, mptr, &temp_s);
      if (!mptr)
	current = temp_s;
      break;
    case M_DESC_LIST:
      if (mptr->is_end) {
	fprintf(fpout, "\\end{description}");
	return mptr;
      }
      fprintf(fpout, "\\begin{description}");
      mptr = mptr->next;
      temp_s = s_describing;
      mptr = ListToLaTeX(fpout, mptr, &temp_s);
      if (!mptr)
	current = temp_s;
      break;
    case M_DESC_TITLE:
      fprintf(fpout, "\n\\item[");
      outstanding_braces++;
      
      break;
    case M_DESC_TEXT:
      fprintf(fpout, "]");
      outstanding_braces--;
      
      break;
    case M_LIST_ITEM:
      fprintf(fpout, "\n\\item ");
      break;
    case M_TT:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\tt ");
      break;
    case M_B:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\bf ");
      break;
    case M_I:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\it ");
      break;
    case M_U:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "\\underline{");
      break;
    case M_EM:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\it ");
      break;
    case M_STRONG:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\bf ");
      break;
    case M_SAMP:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\tt ");
      break;
    case M_KBD:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\tt\\bf ");
      break;
    case M_VAR:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\it ");
      break;
    case M_DFN:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\bf\\it ");
      break;
    case M_CITE:
      if (mptr->is_end) {
	fprintf(fpout, "}");
	break;
      }
      fprintf(fpout, "{\\sc ");
      break;
    case M_LISTING_TEXT:
      if (mptr->is_end) {
	fprintf(fpout, "\\end{verbatim}");
	return mptr;
      }
      fprintf(fpout, "\\begin{verbatim}");
      mptr = mptr->next;
      temp_s = s_verbatim;
      mptr = ListToLaTeX(fpout, mptr, &temp_s);
      if (!mptr)
	current = temp_s;
      break;
    case M_IMAGE:			/* put in a psfig reference--hope that it has been loaded already */
      GW_IMAGE_NAME;
      
      fprintf(fpout, GW_PSFIG_STRING);
      gw_imagenumber++;
      
      break;
    case M_ANCHOR:
					/* Put a box with a footnote of the textand 
					   start underlining */
      if (mptr->is_end) {
	  fprintf(fpout,GW_ANCHOR_END_STRING);
	  
/*	fprintf(fpout, "}");		/* end underlining */
					/* Put in footnote text of link */
/*      fprintf(fpout, "\\fbox{\\footnotemark}");*/
/* use footnotetext with above..*/
/*      fprintf(fpout, "\\footnote{");	/*Make anchors latex proof  */
/*      LaTeXProof(fpout, anchortext);*/
/*      fprintf(fpout,"} \\framebox[2mm][4mm]{ }");*/

	break;
	
      }
					/* Parse the anchor text */
      anchortext = ParseMarkTag(mptr->start,MT_ANCHOR,AT_HREF);
      
					/* Put in a check box with link #*/
/*      fprintf(fpout, "\\framebox[2mm]{}");*/

					/* Put anchor in as footnote */

					/* put anchor if file for future ref */
      GW_SAVE_ANCHOR(anchortext);
      sprintf(gw_anchor_string,"\\encode{%d}{",gw_anchor_num_next());   /*puts down code*/
      fprintf(fpout, gw_anchor_string);
      
	/*  fprintf(fpout, GW_ANCHOR_START_STRING);*/

/*      fprintf(fpout, "\\underline{");*/

      
      break;

    default:
      /* ignore things we know not wot of */
/*      fprintf(fpout, "$s",text);*/
      break;
    }
    if (mptr)
      mptr = mptr->next;
  }
  *state = current;
  return NULL;
}


/*
 * file_to_string
 *   reads a file into a malloc()ed string.
 */

char *file_to_string(FILE *fp)
{
  char *s=malloc(sizeof(char)*9);
  int len=1;
  int max=8;
  int c, lastc=EOF;

  if (!s) {
    fprintf(stderr, "malloc() failed.\n");
    exit(2);
  }
  *s = '\0';

  c = fgetc(fp);
  while (c != EOF) {
    if (len == max) {
      max += 8;
      if (NULL == (s = realloc(s, sizeof(char)*(max+1)))) {
	fprintf(stderr, "realloc() failed.\n");
	exit(3);
      }
    }
    if (c != lastc || (c != '\r' && c != '\n')) {
      s[len-1]=c;
      s[len++]='\0';
    }
    lastc = c;
    c = fgetc(fp);
  }

  return s;
}


/*
 * parse_options
 *   decipher the options to the program
 */

void parse_options(int argc, char **argv)
{
  option_t opt;
  int c;

  for (opt = 0; opt != o_error; opt++) {
    option[opt] = 0;
    option_s[opt] = NULL;
    option_i[opt] = 0;
  }
    
  while ((c = getopt(argc, argv, option_string)) != EOF)
    switch(c) {
    case 'n':
      option[o_number]=1;
      break;
    case 't':
      option_s[o_title] = optarg;
      option[o_title] = 1;
      break;
    case 'a':
      option_s[o_author] = optarg;
      option[o_author] = 1;
      break;
    case 'p':
      option[o_page] = 1;
      break;
    case 'c':
      option[o_contents] = 1;
      break;
    case 's':
      option[o_stdout] = 1;
      break;
    case 'h':
      option[o_header] = 1;
      option_s[o_header] = optarg;
      break;
    case 'f':
      option[o_footer] = 1;
      option_s[o_footer] = optarg;
      break;
    case 'o':
      option[o_options] = 1;
      option_s[o_options] = optarg;
      break;
    case '?':
      option[o_error] = 1;
      break;
    }
  if (option[o_error] || optind == argc) {
    fprintf(stderr, USAGE, program);
    exit(1);
  }
  option_s[o_number] = option[o_number] ? "" : "*";
}    
  

/*
 * s_malloc
 *   safe malloc() code.  Does the checking for NULL returns from malloc()
 */

void *s_malloc(size_t size) {
  void *f = malloc(size);

  if (!f) {
    fprintf(stderr, "cannot allocate memory\n");
    exit(1);
  }
  return f;
}


/*
 * main
 *   call argument parser, then open, read, translate and close for each
 * file
 */

void main(int argc, char **argv)
{
  char *fin, *fout;
  FILE *fpin, *fpout;
  struct mark_up *list=NULL;
  state_t state=s_normal;
  char *s;

  

  program = argv[0];
  parse_options(argc, argv);
  gw_afile = fopen(GW_ANCHOR_FILE_NAME,"a");
  if(gw_afile == NULL) 
  {
      fprintf(stderr,"problem opening");
      fprintf(stderr,GW_ANCHOR_FILE_NAME);
      exit(-1);
  }
  
      
  fin="stdin"; fpin = stdin;
  fout="stdout"; fpout = stdout;
  do {
    if (optind != argc)
      if (!strcmp(argv[optind], "-")) {
	/* read from standard in and write to standard out */
	fpin = stdin;
	fpout = stdout;
      } else {
	/* must find filenames and open ourselves */
	fin = s_malloc(sizeof(char)*(strlen(argv[optind])+6));
	fout = s_malloc(sizeof(char)*(strlen(argv[optind])+5));
	strcpy(fin, argv[optind]);
	strcpy(fout, argv[optind]);
	s = strrchr(fout, '.');
	if (s && !strcmp(s, ".html"))
	  /* file given as foo.html */
	  strcpy(strrchr(fout, '.'), ".tex");
	else {
	  /* file given as foo, as we must add .tex and .html */
	  strcat(fout, ".tex");
	  /* check if .html needed */
	  fpin = fopen(fin, "r");		/* if it opens, use it */
	  if (fpin)
	    fclose(fpin);
	  else
	    strcat(fin, ".html");		/* otherwise try .html */
	}
	/* filenames are decided and allocated -- try and open */
	if (!(fpin = fopen(fin, "r"))) {
	  fprintf(stderr, "can't open file %s for reading.\n", fin);
	  free(fin);
	  free(fout);
	  optind++;
	  continue;
	}
	if (option[o_stdout])
	  fpout = stdout;
	else if (!(fpout = fopen(fout, "w"))) {
	  fprintf(stderr, "can't open file %s for writing.\n", fin);
	  free(fin);
	  free(fout);
	  fclose(fpin);
	  optind++;
	  continue;
	}
      }
	
    s = file_to_string(fpin);		/* load the file */
    list = HTMLParse(list, s);		/* parse it */
    
    /* write LaTeX headers and title pages, etc */
    fprintf(fpout, "%s", ID);
    fprintf(fpout, "%% Version %s\n", VER);
					/* Add in GW headers */
    fprintf(fpout, GW_TEX_HEADER);
    if (option[o_options])
      fprintf(fpout, "\\documentstyle%s\n", option_s[o_options]);
    else
      fprintf(fpout, "\\documentstyle{article}\n");
    fprintf(fpout, "\\begin{document}\n");
    if (option[o_header]) {
      fprintf(fpout, "%s", option_s[o_header]);
    }
    if (option[o_title]) {
      fprintf(fpout, "\\title{");
      LaTeXProof(fpout, option_s[o_title]);
      fprintf(fpout, "}\n");
    }
    if (option[o_author]) {
      fprintf(fpout, "\\author{");
      LaTeXProof(fpout, option_s[o_author]);
      fprintf(fpout, "}\n");
    } else if (option[o_title])
      fprintf(fpout, "\\author{}\n");
      
    if (option[o_title] || option[o_author]) {
      fprintf(fpout, "\\maketitle\n");
      if (option[o_page])
	fprintf(fpout, "\\newpage\n");
    }
    if (option[o_contents])  {
      fprintf(fpout, "\\tableofcontents \n");
      if (option[o_page])
	fprintf(fpout, "\\newpage\n");
    }

    /* convert the file */
    if (ListToLaTeX(fpout, list, &state))
      fprintf(stderr, "\n!Abnormal exit\n");

    /* write trailers */
    if (option[o_footer])
      fprintf(fpout, "%s", option_s[o_footer]);
    fprintf(fpout, "\\end{document}\n");
    
    /* tidy up */
    free(s);
    FreeObjList(list);
    list = NULL;
    
    /* if we processed a file, then we malloc()ed memory.  Free it */
    if (optind != argc && !strcmp(argv[optind], "-")) {
      free(fin);
      free(fout);
      fclose(fpin);
      if (!option[o_stdout])
	fclose(fpout);
    }
    /* move on to next argument */
    optind++;
  } while (optind != argc);
  exit(0);
}

