/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_GlobalVars.java: contains global variables for RH
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 02.04.98
 *
 */
package ricoh.rh;
import java.util.*;

/**
 * Put anything in here that you would like to have accessible to all classes or other applications
 */
public class RH_GlobalVars {

  // The HTML DOCTYPE tag comes from: http://www.w3.org/TR/REC-html32
  // EX: <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML Experimental 970421//EN">
  // EX: <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
  public final static String HTML_Doctype_Slashes="//";
  public final static String HTML_Doctype_Name="!DOCTYPE";  // separate this so i can use it to search for this string in matchConcepts
  public final static String HTML_Doctype_Type=" HTML PUBLIC";
  public final static String HTML_Doctype_W3C="W3C";
  public final static String HTML_Doctype_Version="DTD HTML 3.2 Final";
  public final static String HTML_Doctype_Info=HTML_Doctype_Name+HTML_Doctype_Type+" \"-"+HTML_Doctype_Slashes+HTML_Doctype_W3C+HTML_Doctype_Slashes+HTML_Doctype_Slashes+HTML_Doctype_Version+HTML_Doctype_Slashes;
  public final static String HTML_Doctype_Header="<"+HTML_Doctype_Info+"EN\">";
  public final static String RH_HTML_Doctype_Name="RH.ANOH";
  public final static String RH_HTML_Doctype_Header="<"+HTML_Doctype_Name+HTML_Doctype_Type+" \"-"+HTML_Doctype_Slashes+HTML_Doctype_W3C+HTML_Doctype_Slashes+HTML_Doctype_Version+" "+RH_HTML_Doctype_Name+HTML_Doctype_Slashes+"EN\">";
  public final static String HTML_HTML_Tag="<HTML>";
  public final static String HTML_HTML_Tag_End= "</HTML>";
  public final static String HTML_HEAD_Tag= "<HEAD>";
  public final static String HTML_HEAD_Tag_End= "</HEAD>";
  public final static String HTML_TITLE_Tag= "<TITLE>";
  public final static String HTML_TITLE_Tag_End= "</TITLE>";
  public final static String HTML_BODY_Tag= "<BODY>";
  public final static String HTML_BODY_Tag_End= "</BODY>";
  public final static String HTML_META_Tag="META";
  
  public final static String RH_ANNOTATE_SENTENCE_BEGIN="RH.ANOH.S";
  public final static String RH_ANNOTATE_SENTENCE_END="/RH.ANOH.S";
  public final static String RH_ANNOTATE_SENTENCE_NUMBER="NUMBER";
  public final static String RH_ANNOTATE_BEGIN="RH.ANOH";
  public final static String RH_ANNOTATE_END="/RH.ANOH";
  public final static String RH_CONCEPT_TAG="CONCEPT";
  public final static String RH_TOPIC_TAG="TOPIC";
  public final static String RH_SCORE_TAG="SCORE";
  public final static String RH_NUMBER_TAG="NUMBER";
  public final static String RH_SENTENCE_TAG="SENTENCE";

  public final static String RH_EMAIL_TAG_BEGIN="RH.EMAIL";
  public final static String RH_EMAIL_TAG_END="/RH.EMAIL";
  public final static String RH_URL_TAG_BEGIN="RH.URL";
  public final static String RH_URL_TAG_END="/RH.URL";
  public final static String RH_ANOH_Tag="ANOH";

  public final static String RH_DocumentHeader_BeginTag="RH.ANNOTATION";
  public final static String RH_DocumentHeader_EndTag="/RH.ANNOTATION";
  public final static String RH_GroupSummaryHeader_BeginTag="RH.ANNOTATION.GROUPSUMMARY";
  public final static String RH_GroupSummaryHeader_EndTag="/RH.ANNOTATION.GROUPSUMMARY";
  public final static String RH_SummaryHeader_BeginTag="RH.ANNOTATION.SUMMARY";
  public final static String RH_SummaryHeader_EndTag="/RH.ANNOTATION.SUMMARY";
  public final static String RH_IndexHeader_BeginTag="RH.ANNOTATION.INDEX";
  public final static String RH_IndexHeader_EndTag="/RH.ANNOTATION.INDEX";
  public final static String RH_CalendarHeader_BeginTag="RH.ANNOTATION.CALENDAR";
  public final static String RH_CalendarHeader_EndTag="/RH.ANNOTATION.CALENDAR";
  public final static String RH_DocumentHeader_URLTag="URL";
  public final static String RH_DocumentHeader_VERTag="VER";


  public final static String RH_TitleString="ReadersHelper";
  // Possible file types
  public final static int RH_FILETYPE_NULL=-1;
  public final static int RH_FILETYPE_HTML=1000;
  public final static int RH_FILETYPE_ANOH=1001;
  public final static int RH_FILETYPE_ANOH_SUMMARY=1002;
  public final static int RH_FILETYPE_TEXT=1003;
  public final static int RH_FILETYPE_IMAGE=1004; 

  public final static String RH_LinearSummaryText="Contains a list of all relevant sentences as they were processed by the system";
  public final static String RH_GroupedSummaryText="Shows each concept that scored well with relevant sentences as they occurred in the document";

  public final static String RH_HistoryDirectoryName="rhd";  // combined with a ID number, this defines a unique directory name
  // Annotation types
  public final static int RH_Annotate_Highlight=0, RH_Annotate_BoldType=1, RH_Annotate_BoldUnderline=2, RH_Annotate_Balloon=3,
    RH_Annotate_ContinueType=4, RH_Annotate_Outline=5, RH_WholeSentenceType=6;

  public final static String RH_ANOH_History_Symbol="*"; // symbol used in history list to denote that the document is of the anoh type

  public final static String rhGroupedSummaryDir="grouped", rhSummaryDir="straight", titleString="ReadersHelper: ", plainLabel = "PlainText", 
    annotateLabel = "Annotate", summaryLabel = "Summary",  groupLabel="Group", rhInfoFileName="info", rhHTMLFileName="document",rhSSFileName="ss",
    rhGroupedFileName="grouped", rhSummaryFileName="summary", rhScoresFileName="scores", rhInfoTempDir="current", rhIndexFileName="index", 
    rhInfoConceptsFileName="concepts", rhHistoryDBFileName="history.rh", rhCalendarDataFileName="data", rhTimeLineFileName="timeline", rhANOHGIFFileName="anoh.gif",
    rhUserLexiconFileName="dictionary",rhStopwordsFileName="stopwords",rhLexiconFileName="lex", rhSpecialFileTag="-special",
    // This gif path should change when you release versions to real users -- make it "./gifs" so it's local
    httpFileTypeTag="file:", rhDefaultHomeURLName="default.html", rhDocumentDir="documents",  rhCalendarDir="calendar", rhSimilarDir="similar",
    rhInfoFileExt=".rhi", rhHTMLExtension=".html", rhSHTMLExtension=".shtml", rhCalendarExtension=".rhc", rhSimilarExt=".rhs", NoHighConcept="nohigh",
    rhFieldSeparator="|", rhHTMLIncludeExtention=".inc", rhLocalGIFPath="../images", rhWeekViewFileName="weekview", rhWordBufferFileName="buffer",
    rhTextFileExtension=".txt", rhFieldSubSeparator=",", rhSimFileName="compare", rhInfoFileHeader="RH_InfoFile", rhPrivateDirName="private",
     httpTypeTag="http:",  rhPathSeparator="/";  // there's a way to get this from the OS -- find it!;

    //***--- FREQUENCY SCORES
    public final static int RH_VeryMany_Topics=20, RH_Many_Topics=10, RH_Few_Topics=5, RH_MoreThanOne_Topics=2, RH_One_Topics=1;
    public final static int RH_Prox_ReallyGood=6, RH_Prox_VeryGood=10, RH_Prox_Good=15, RH_Prox_OK=20, RH_Prox_Poor=30;
    
    //***--- PROXIMITY SCORES
    public final static double RH_Prox_ReallyGood_Score=.7, RH_Prox_VeryGood_Score=.6, RH_Prox_Good_Score=.5, 
	RH_Prox_OK_Score=.3, RH_Prox_Poor_Score=.1;
    
    //***--- SENTENCE LOCATION SCORES
    //public final static double RH_Sent_ReallyGood_Score=.7, RH_Sent_VeryGood_Score=.6, RH_Sent_Good_Score=.5, 
    //RH_Sent_OK_Score=.3, RH_Sent_Poor_Score=.1;
    public final static double RH_Sent_ReallyGood_Score=.4, RH_Sent_VeryGood_Score=.3, RH_Sent_Good_Score=.2, 
	RH_Sent_OK_Score=.1, RH_Sent_Poor_Score=.05;
    
    //***---FREQUENCY Probabilities
    public final static double RH_VeryMany_Prob=.99, RH_Many_Prob=.9, RH_Few_Prob=.6, RH_MoreThanOne_Prob=.3, 
	RH_One_Prob=.1, RH_None_Prob=.01;
    public final static double RH_VeryMany_Not_Prob=.01, RH_Many_Not_Prob=.1, RH_Few_Not_Prob=.2, RH_MoreThanOne_Not_Prob=.3, 
	RH_One_Not_Prob=.35, RH_None_Not_Prob=.4;
    
    //***--- PROXIMITY Probabilities
    public final static double RH_Prox_ReallyGood_Prob=.5, RH_Prox_VeryGood_Prob=.45, RH_Prox_Good_Prob=.4, 
	RH_Prox_OK_Prob=.35, RH_Prox_Poor_Prob=.3;
    public final static double RH_Prox_ReallyGood_Not_Prob=.01, RH_Prox_VeryGood_Not_Prob=.05, RH_Prox_Good_Not_Prob=.1, 
	RH_Prox_OK_Not_Prob=.15, RH_Prox_Poor_Not_Prob=.2;
    
    //***--- SENTENCE LOCATION Probabilities
    /*
    public final static double RH_Sent_ReallyGood_Prob=.5, RH_Sent_VeryGood_Prob=.45, RH_Sent_Good_Prob=.4, 
	RH_Sent_OK_Prob=.35, RH_Sent_Poor_Prob=.3;
    public final static double RH_Sent_ReallyGood_Not_Prob=.01, RH_Sent_VeryGood_Not_Prob=.05, RH_Sent_Good_Not_Prob=.1, 
	RH_Sent_OK_Not_Prob=.15, RH_Sent_Poor_Not_Prob=.2;
    */
    public final static double RH_Sent_ReallyGood_Prob=.4, RH_Sent_VeryGood_Prob=.35, RH_Sent_Good_Prob=.3, 
	RH_Sent_OK_Prob=.25, RH_Sent_Poor_Prob=.2;
    //public final static double RH_Sent_ReallyGood_Not_Prob=.01, RH_Sent_VeryGood_Not_Prob=.05, RH_Sent_Good_Not_Prob=.1, 
    //RH_Sent_OK_Not_Prob=.15, RH_Sent_Poor_Not_Prob=.2;
    public final static double RH_Sent_ReallyGood_Not_Prob=.1, RH_Sent_VeryGood_Not_Prob=.15, RH_Sent_Good_Not_Prob=.2, 
	RH_Sent_OK_Not_Prob=.25, RH_Sent_Poor_Not_Prob=.3;
    
    public final static double RH_Default_Prior=.1;
    public final static double RH_Default_Not_Prior=1-RH_Default_Prior;
    public final static int RH_DefaultMatchVectorSize=30; // the expected number of matches for a topic in a document (reallocates if it needs more)
    
    public final static int plainTextMode=0, annotationMode=1, summaryMode=2, summaryGroupMode=3, regularWord=0, emailWord=1,urlWord=2;

  public final static int RH_DefaultSensitivity=10;  // default value for sensitivity
  public final static String defaultDurationStr="00:00:00";

  public final static String RH_MatchSymbol="*";
  public final static String RH_NoMatchSymbol="#";

  // this is the percentage used for the range at the top of the document for scoring sentences
  public final static double topSentenceRangePercentage=.1, bottomSentenceRangePercentage=.1;  
    
    public final static String metalLAF="javax.swing.plaf.metal.MetalLookAndFeel";
    public final static String windowsLAF="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    public final static String motifLAF="com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    
    //** This is the threshold value for evaluating percentages when comparing documents
    public final static  double  similarityThresholdPercent=.4;

    public final static String rhContentHeaderName="X-application/rh";
    public final static String rhContentTypeUser="X-RH-User";
    public final static String rhContentTypePrinter="X-RH-Printer";
    public final static String rhContentTypeMsg="X-RH-Msg";
    public final static String rhContentTypeURL="X-RH-URL";
    public final static String rhContentTypeTitle="X-RH-Title";
    public final static String rhContentTypeConcepts="X-RH-Concepts";
    public final static String rhContentTypeHliteStyle="X-RH-HliteStyle";
    public final static String rhContentTypeActiveSentences="X-RH-ActiveSentences";
    public final static String rhContentTypeStartPage="X-RH-StartPage";
    public final static String rhContentTypeEndPage="X-RH-EndPage";
    public final static String rhContentTypeCoverSheet="X-RH-CoverSheet";
    public final static String rhContentTypeHardcopy="X-RH-Hardcopy";
    public final static String rhContentTypeBufferLen="X-RH-Buffer-Len";
    public final static String rhContentTypeClientVersion="X-RH-ClientVersion";
    public final static String rhContentTypeThreshold="X-RH-Threshold";
    public final static String rhContentTypeSensitivity="X-RH-Sensitivity";
    public final static String rhContentTypeProxyName="X-RH-ProxyName";
    public final static String rhContentTypeProxyPort="X-RH-ProxyPort";
    public final static String rhContentTypeProxyAgent="X-RH-ProxyAgent";

    public final static String piaProxyMsgPing="ping";
    public final static String piaProxyMsgGetProfile="getprofile";
    public final static String piaProxyMsgGetLocations="getlocations";
    public final static String piaProxyMsgGetGroups="getgroups";
    public final static String piaProxyMsgGetConcepts="getconcepts";
    public final static String piaProxyMsgMatchContent="match";
    public final static String piaProxyMsgProcessContent="processcontent";
    public final static String piaProxyMsgAnnotationOn="annotationon";
    public final static String piaProxyMsgAnnotationOff="annotationoff";
    public final static String piaProxyMsgPutProfile="putprofile";
    public final static String piaProxyMsgPutConcepts="putconcepts";
    public final static String piaProxyMsgPutGroups="putgroups";
    public final static String piaProxyMsgPutLocations="putlocations";
    public final static String piaProxyMsgGetConceptInfo="getconceptinfo";
    public final static String piaProxyMsgPutThreshold="putthreshold";
    public final static String piaProxyMsgStoreResult="storeresult";
    public final static String piaProxyMsgGetSummary="getsummary";
    public final static String piaProxyMsgGetCalendar="getcalendar";
    public final static String piaProxyMsgGetSimilar="getsimilar";
    public final static String piaProxyMsgPutSensitivity="putsensitivity";
    public final static String piaProxyMsgGetAnohFileConcepts="getanohconcepts";
    public final static String piaProxyMsgGetAnohFileSentences="getanohsentences";

    public final static int maxNumberKeywords=50;
    public final static int maxNumberConcepts=50;
    public final static int maxSentencesInSummary=100;

    //**JSTEST 6.22.98
    /*
    public final static String rhJS_StyleHeader="<STYLE TYPE=\"text/css\"> A.rhconcept { COLOR: BLACK; FONT-FAMILY: SERIF; BACKGROUND-COLOR: RGB(255,255,100); FONT-SIZE: small; ONMOUSEOVER: showconcept(conceptzero); ONMOUSEOUT: hideconcept();  TEXT-DECORATION: none;  } </STYLE>";

    public final static String rhJS_ScriptHeader="<SCRIPT language=\"JavaScript\"> document.captureEvents(Event.MOUSEDOWN|Event.MOUSEUP); function concept(name,topic,sentence,number) { this.name=name; this.topic=topic; this.sentence=sentence; this.number=number; } var conceptnum;";

    public final static String rhJS_ScriptFooter="function showconcept(num) {conceptnum=num;} function hideconcept(concept) {document.layers[\"CONCEPTVIEW\"].visibility=\"hide\";} document.onMouseDown=doMouseDown; function doMouseDown(ev) { x = ev.pageX-70; y = ev.pageY; if (conceptnum>=0) { document.layers[\"CONCEPTVIEW\"].document.open(); document.layers[\"CONCEPTVIEW\"].document.write(\"Concept: \"+allconcepts[conceptnum].name+\"<br>\"+\"Topic (\"+allconcepts[conceptnum].number+\"): \"+allconcepts[conceptnum].topic+\"<br>\"+\"Sentence: \"+allconcepts[conceptnum].sentence+\"<br>\"); document.layers[\"CONCEPTVIEW\"].document.close(); document.layers[\"CONCEPTVIEW\"].moveToAbsolute(x,y); document.layers[\"CONCEPTVIEW\"].visibility=\"show\"; } else document.layers[\"CONCEPTVIEW\"].visibility=\"hide\"; } document.onMouseUp=doMouseUp; function doMouseUp(ev) { conceptnum=-1; document.layers[\"CONCEPTVIEW\"].document.open(); document.layers[\"CONCEPTVIEW\"].document.write(\"\"); document.layers[\"CONCEPTVIEW\"].document.close(); document.layers[\"CONCEPTVIEW\"].visibility=\"hide\"; } </SCRIPT> <LAYER NAME=\"CONCEPTVIEW\" left=5 top=10 bgcolor=\"gray\" width=200 height=50 border=3 visibility=hidden></LAYER>";
     */

  public RH_GlobalVars() {
      //Properties props = new Properties(System.getProperties());
      //rhPathSeparator=props.getProperty("file.separator");
  }

}
