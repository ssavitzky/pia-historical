/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 10.20.97 - revised 02-26-98
 *
 */
package ricoh.rhpm;

import java.lang.*;
import java.awt.*;
import java.util.*;
import java.io.*;

import ricoh.rh.RH_GlobalVars;

public class RHPatternMatcher {
    

    private RHActiveConcept[] allConcepts, activeConcepts;
    private RHSentenceSummary[] sentenceSummary;
    private RHSortableConcept[] sortedConcepts;
    private Hashtable conceptHash;
    private int weAreHot=0, weAreDoingWell=0, weAreDoingOK=0,  wordType=RH_GlobalVars.regularWord;
    private int maxNumberKeywords=0, maxSentencesInSummary=0, maxMatchesPerSentence=30, documentSentenceCount=0;
    private int sentenceBegin=0, sentenceEnd=0, stackLen=0, maxStack=10;
    private int newPtr=0, bufferPtr=0, numSentences=0, urlBufferSize=0, eosFlag=0;
    private StringBuffer saveStreamFilename=null, lexStreamFilename=null, jsbuffer=null;
    private String [] wordStack = new String[maxStack];
    private byte[] urlBuffer, // this is the main buffer which we write to during the match
	rhDocumentHeader, rhDocumentFooter;
    private Color weAreHotColor=Color.blue, weAreDoingWellColor=Color.cyan, 
	weAreDoingOKColor=new Color(128,255,255), 
	startColor=Color.red, //new Color(128,255,255), 
	//startColor=new Color(200,255,255), // use this start color when changing colors during annotation
	weAreHotColorBkgd=Color.lightGray, otherColorBkgd=Color.gray;
    private byte nextChar;
    private boolean sentenceComplete=false, useProximityMeasure=false;
    public ByteArrayOutputStream  anohStream; //
    //public RH_ByteArraySaveStream  anohStream; //
    //private byte[] saveStream;
    private boolean previouslyAnnotated=false, headerFound=false;
    private String firstline, annotationDurationStr=null;
    //** LONGS
    private long annotationDuration=0;
    
    RHPatternMatcher() {
	rhDocumentHeader=new byte[RH_GlobalVars.RH_DocumentHeader_BeginTag.length()];
	rhDocumentFooter=new byte[RH_GlobalVars.RH_DocumentHeader_EndTag.length()+4];
    }
    /**
     * Reset for next document match (must run prior to matchConcepts)
     */
    public void reset () {
    }

    /**
     * Check to see if document has already been annotated, i.e. contains the RH tag in the DOCTYPE tag
     */
    public boolean preAnnotated(byte[] buffer) {
	int start=0;
	firstline=readDocTypeHeader(buffer);
	if (firstline.indexOf(RH_GlobalVars.HTML_Doctype_Name)>=0) headerFound=true;
	else headerFound=false;
	if (firstline.indexOf(RH_GlobalVars.RH_HTML_Doctype_Name)>=0) previouslyAnnotated=true;
	else previouslyAnnotated=false;
	return previouslyAnnotated;
    }

   public byte[] matchConcepts(byte[] buffer, String conceptsList, String version, String urlstr) {
	System.out.println("----------------------------> BEGIN PATTERN MATCHING:");
	Date starttime=new Date();
	//String str=new String(buffer,0,buffer.length);
	System.out.print("--->... Activating concepts...");
	activateConcepts(conceptsList);
	//** Reset
	maxNumberKeywords=RH_GlobalVars.maxNumberKeywords;
	maxSentencesInSummary=RH_GlobalVars.maxSentencesInSummary;
	urlBufferSize=buffer.length;
	urlBuffer=buffer;
	useProximityMeasure=true;
	previouslyAnnotated=false;
	headerFound=false;
	firstline="";
	//** end reset
	//System.out.println("...done");
	preAnnotated(buffer);
	//System.out.println("--->... Start document processing... header:"+headerFound+" len="+urlBufferSize);
	//System.out.println(new String(buffer,0,buffer.length));
	buffer=contentMatcher(headerFound,version,firstline.length(), urlstr);
	//System.out.println(new String(buffer,0,buffer.length));
	Date endtime=new Date();
	annotationDurationStr=calculateAnnotationTime(starttime,endtime);
	System.out.println("----------------------------> END PATTERN MATCHING:");
	return buffer;
    }

    private byte[] contentMatcher(boolean headerFound, String version, int start, String urlstr) {
	System.out.println("contentMatcher urlstr: " + urlstr);
	try {
	    anohStream=new ByteArrayOutputStream();
	    int newSize=2*urlBufferSize, wordLen=0, i=0, j=0, wordCount=0, topicsIdx=0, tagIdx=0, startIdx=0, wordIdx=0;
	    int totalMatches=0, count=0, 
		//** sentenceCOunter is the counter for sentences which have matches in them
		sentenceCounter=0, 
		//** otherSentenceCounter is the global counter for all sentences in the document
		otherSentenceCounter=1,
		sPtr=0, totalByteCount=0,
		// 11-13-97 1024 can be too small for tags, like style tags and such which are very long so i'll make this bigger
		maxSentenceBufferLength=10000,bigword=maxSentenceBufferLength;
	    double percentDone=0, score=0;
	    int[] matchVector=new int[maxNumberKeywords];
	    // states for the progress meter
	    int incr1=10, incr2=20, incr3=30, incr4=40, incr5=50, incr6=60, incr7=70, incr8=80, incr9=90, laststate=0;
	    String conceptName="", wordName="";
	    
	    // setup newline byte char
	    String newline=getNewlineByte();
	    
	    byte[] wordBuf, holderBuffer, tagBuffer, sentenceBuffer;
	    byte nextChar;
	    // Misc flags
	    boolean sentenceComplete=false, wordMatch=false, foundTag=false, foundSeparator=false, aMatch=false, sameSentence=false,
		pushedOnStack=false, debugStack=false;
	    RHLexEntry  entry=null;
	    
	    String wordString="", nameTag="", anohStatusStr="Analyzing...", anohReallocateStr="Reallocating...";
	    // Pointer which keep strack of match list (list of concept matches)
	    int matchListPtr=0;
	    // The match list
	    RHSentenceMatchList[] matchList=new RHSentenceMatchList[maxMatchesPerSentence];
	    
	    System.out.println("--->... Starting to match patterns  urlBufferSize="+urlBufferSize);
	    
	    //*** INITIALIZATION of variables
	    //***
	    wordCount=newPtr=bufferPtr=0;  
	    if (headerFound) {
		bufferPtr=start;
	    }
	    StringBuffer tmpStr=new StringBuffer().append(RH_GlobalVars.RH_HTML_Doctype_Header).append(newline).append("<").
		append(RH_GlobalVars.HTML_META_Tag).append(" name=").append(RH_GlobalVars.RH_DocumentHeader_BeginTag).append(" ").
		append(RH_GlobalVars.RH_DocumentHeader_URLTag).append("=\"").
		append(urlstr).append("\" ").
		append("content=\"").append(version).append("\">");
	    rhDocumentHeader=new byte[tmpStr.length()];
	    rhDocumentHeader=tmpStr.toString().getBytes();
	    rhDocumentFooter=new StringBuffer("").toString().getBytes(); //append("<").append(RH_GlobalVars.RH_DocumentHeader_EndTag).append(">").toString().getBytes();
	    //*** Write the Annotate HTML Tag at the beginning of the buffer
	    //for (i=0;i<rhDocumentHeader.length;i++) annotationBuffer[newPtr++]=rhDocumentHeader[i];
	    //annotationBuffer[newPtr++]=newline.getBytes();
	    for (i=0;i<rhDocumentHeader.length;i++) {
		anohStream.write(rhDocumentHeader[i]);
		newPtr++;
		totalByteCount++;
	    }
	    anohStream.write(newline.getBytes()); newPtr++; totalByteCount++;
	    
	    // This is the buffer which will contain the current sentence being processed; sPtr points to the last byte
	    sentenceBuffer=new byte[maxSentenceBufferLength]; sPtr=0;
	    
	    sentenceBegin=sentenceEnd=stackLen=0;
	    sentenceComplete=false;
	    sentenceSummary=new RHSentenceSummary[maxSentencesInSummary];
	    
	    numSentences=0;
	    
	    // **** MAIN LOOP THROUGH BUFFER
	    while(bufferPtr<urlBufferSize) {
		wordBuf=new byte[bigword];
		wordType=RH_GlobalVars.regularWord;
		startIdx=bufferPtr; // hold this position.
		
		foundTag=false;
		foundSeparator=false;
		pushedOnStack=false;
		
		nextChar=urlBuffer[bufferPtr];
		//*** Start by looking at the first character - if it is of the stopChar class then process as a stop char or a tag
		if (memberStopChars(nextChar)) {
		    //*** Parse the stream as a tag if we find the tagChar
		    if (nextChar=='<') {
			wordBuf=parseTag(urlBuffer,urlBufferSize);
			if (debugStack) System.out.print("["+bufferPtr+","+(newPtr+wordBuf.length)+"]"+new String(wordBuf));
			//*** IF this is a tag which definitely defines a new sentence, then reset the sentence marker
			if (memberStopTags(wordBuf) ||
			    /// Also check for the case: "end of sentence.</i>" because i don't want to start a new sentence before the </i>
			    (wordBuf[0]=='<' && wordBuf[1]=='/' && newPtr==sentenceBegin)) {
			    sentenceEnd=newPtr+wordBuf.length;
			    if (debugStack) System.out.println(" END:"+sentenceEnd + " state:"+ (aMatch&&sameSentence));
			    sentenceComplete=true;
			    otherSentenceCounter++;
			}
			else if (debugStack) System.out.println("");
			foundTag=true;
		    }
		    //*** Otherwise it's just a stop char and we'll write just the char
		    else {
			foundSeparator=true;
			wordLen=1;
			bufferPtr++;
		    }
		}
		//***Else we will try locating a word on the stream...
		else {
		    wordBuf=parseWord(urlBuffer,urlBufferSize);
		    if (debugStack) System.out.print("["+bufferPtr+","+(newPtr+wordBuf.length)+"]"+ new String(wordBuf));
		    if (eosFlag==1) { // hack because sentenceComplete could not be set in parseWord ?????
			sentenceComplete=true;
			otherSentenceCounter++;
			sentenceEnd=newPtr+wordBuf.length;
			eosFlag=0; 
			if (debugStack) System.out.println(" END:"+sentenceEnd+" STATE:[match="+ aMatch+"] same="+sameSentence);
		    }
		    else if (debugStack) System.out.println("");
		    if (bufferPtr<urlBufferSize && wordBuf.length==0) bufferPtr++;  // increment to skip the just found char      
		    else wordCount++;
		}
		//-------------------------------------------
		//*** If we have a tag or a word then we setup the string and the word length
		if (!foundSeparator) {
		    wordString = new String(wordBuf,0,wordBuf.length);
		    
		    //** Add Word to Lexicon
		    //********** 1.13.99 need to figure out what to do about lexicons
		    //if (mainFrame.useLexicon && !foundTag) mainFrame.documentLexicon.addWord(wordString,otherSentenceCounter); 
		    
		    //*** Evaluate the new Word by performing pattern matching against the concepts
		    if (!foundTag && checkKeywords2(wordString)==1) {
			//** NOTE: if i leave this 'addWord' call here, it gathers all words in the document which are somehow connected
			//** to the concept's topic strings.  do i want this collection?  if so, i need to create a new lexicon
			//mainFrame.documentSpecialLexicon.addWord(wordString,mainFrame.documentLexicon.getWordCount(),otherSentenceCounter);
			
			pushedOnStack=true;
			//*** Because we have a valid word in wordString, check for a match in the activeConcepts
			for (i=0; i<activeConcepts.length; i++) {
			    RHActiveConcept currentConcept=activeConcepts[i];
			    if (currentConcept.isActive()) {
				//*** Loop through all keywords for this concept to see if we have a valid match
				for (topicsIdx=0; topicsIdx<currentConcept.getLength(); topicsIdx++) {
			  	    //** System.out.println("*****TOPICIDX:"+topicsIdx);
				    RHTopicKeyword topic=currentConcept.getTopic(topicsIdx);
				    String[] keyword=topic.getPhrase();
				    // init matchvector; this is where i store IDs for matches in this concept
				    for (int mv=0;mv<maxNumberKeywords;mv++) matchVector[mv]=-1;
				    
				    //***-- This check to see if any of the stack words match the keywords; the location of the match (in 
				    //***-- the stack) is stored in the matchVector
				    for (wordIdx=0; keyword[wordIdx]!=null; wordIdx++) {
					//***-- I want to match from the bottom of the stack so that I get a decending order of match locations
					//System.out.println(wordIdx+">Topic:"+topic.getName()+" Word:"+keyword[wordIdx]);
					for (int stackIdx=stackLen; stackIdx>=0 && stackLen>0; stackIdx--) {
					    if (keyword[wordIdx]!=null && keyword[wordIdx].equalsIgnoreCase(wordStack[stackIdx])) matchVector[wordIdx]=stackIdx;
					}
				    }
				    //***--- Test to see if we have match
				    wordMatch=true; // initialize flag first
				    //***--------Test 1: If the first item in the matchVector is != -1, then we *may* have a match
				    for (int testIdx=0; testIdx<wordIdx && wordMatch; testIdx++)
					if (matchVector[testIdx]==-1) wordMatch=false;
				    if (wordMatch) {
					//***--------Test 2: Contiguous-ness:  are the values in the match vector contiguous from top to bottom?
					for (int contIdx=wordIdx; contIdx>=0 && wordMatch; contIdx--)
					    if (contIdx>0 && matchVector[contIdx]+1!=matchVector[contIdx-1]) wordMatch=false;
					
					//***FINAL TEST: do still have a match??
					if (wordMatch) {  // Then we have a MATCH!!
					    if (debugStack) System.out.println(newPtr+">...........Match:"+topicsIdx+">"+topic.getName());
					    score=currentConcept.match(topic, newPtr, wordCount,sentenceCounter,sentenceCounter);
					    totalMatches++;
					    aMatch=true;
					    conceptName=currentConcept.getShortName();
					    wordName=wordString;  // grab the word string that matched before wordString gets changed; for lexicon
					    //*** Add to match list for summarization
					    matchList[matchListPtr++]=new RHSentenceMatchList(i,sentenceBegin,topicsIdx,topic.getFrequency(), otherSentenceCounter);
					    
					    //********** 1.13.99 need to figure out what to do about lexicons
					    //** Update the lexical entry to reflect a match for each word in this topic phrase
					    /*
					      if (mainFrame.useLexicon && false) {
					      for (int lexIdx=0; keyword[lexIdx]!=null; lexIdx++) {
					      entry=mainFrame.documentLexicon.getWord(keyword[lexIdx]);
					      if (entry!=null) {
					      entry.setMatched(true, otherSentenceCounter, conceptName);
					      entry=null;
					      }
					      }
					      }*/
					    
					    //***---Create the HTML Tag for this concept
					    // This method is suppose to optimize the string concatenation process
					    StringBuffer tagString = new StringBuffer().append(newline).append("<").append(RH_GlobalVars.RH_ANNOTATE_BEGIN).
						append(" ").append(RH_GlobalVars.RH_CONCEPT_TAG).append("=\"").append(currentConcept.getName()).
						append("\" ").append(RH_GlobalVars.RH_TOPIC_TAG).append("=\"").append(topic.getName()).append("\" ").
						append(RH_GlobalVars.RH_SENTENCE_TAG).append("=\"").append(sentenceCounter).append("\" ").
						append(RH_GlobalVars.RH_NUMBER_TAG).append("=").append(topic.getFrequency()).append(">");
					    
					    // I believe i'm grabbing the keyword phrase here but maybe i can do this directly without this loop ???
					    for (tagIdx=0, count=0; tagIdx<wordIdx; tagIdx++) {
						//tagString=new StringBuffer().append(tagString.toString()).append(wordStack[matchVector[tagIdx]]).append(" ");
						tagString.append(wordStack[matchVector[tagIdx]]).append(" ");
						count+=wordStack[matchVector[tagIdx]].length();
					    }
					    
					    tagString.append("<").append(RH_GlobalVars.RH_ANNOTATE_END).append(">").append(newline);
					    
					    //***--- Merge Strings if highlight sentence is ON: if not sameSentence, then this is the first 
					    //***--- instance of a find in this sentence so go back and tag the beginning of the sentence.
					    if (!sameSentence) {
						int goBack=0;
						goBack=(sPtr+wordBuf.length+1)-topic.getTotalLengthOfKeywords();
						//System.out.println("->wordBuf="+wordBuf.length+"  sPtr="+sPtr+" GoBack="+goBack);
						//System.out.println("-->PhraseLen="+topic.getTotalLengthOfKeywords()+":"+topic.getName());
						sameSentence=true;
						//int goBack=(newPtr+wordBuf.length)-sentenceBegin-topic.getName().length();
						
						if (debugStack) System.out.println(newPtr+"> Sentence begin="+sentenceBegin+" goBack="+goBack);
						
						// When goBack is less than 0, we have are at the beginning of the sentence and so do not need to go back
						// to grab the chars from the beginning of the sentence
						if (sPtr>0) {
						    // This prevents landing on top of a end of sentence marker
						    int startUp=0;
						    if (sentenceBuffer[0]=='.' || sentenceBuffer[0]=='!' || sentenceBuffer[0]=='?' || 
							sentenceBuffer[0]==';' || sentenceBuffer[0]==':') startUp=1;
						    //System.out.println(startUp+"===So Far("+sPtr+"):"+new String(sentenceBuffer,0,sPtr));
						    
						    nameTag = new StringBuffer().append(newline).append("<a name=\"").append
							(RH_GlobalVars.RH_ANOH_Tag).append(sentenceCounter).append("\"></a>").toString();
						    // Supposed to optimize string concat. 11-19-97
						    String tmpString = new StringBuffer().append(new String(sentenceBuffer,0,startUp)).append(nameTag).append
							(newline).append("<").append(RH_GlobalVars.RH_ANNOTATE_SENTENCE_BEGIN).append(" ").append
							(RH_GlobalVars.RH_ANNOTATE_SENTENCE_NUMBER).append("=").append(sentenceCounter).append(">").append
							(new String(sentenceBuffer,startUp,goBack-startUp)).toString();
						    
						    byte[] tmpBuf=new byte[tmpString.length()];
						    tmpBuf=tmpString.getBytes();
						    
						    //***TRY
						    System.arraycopy(tmpBuf,0,sentenceBuffer,0,tmpString.length());
						    sPtr=tmpString.length();
						    //for (startUp=0;startUp<tmpString.length();startUp++) sentenceBuffer[startUp]=tmpBuf[startUp];
						    //sPtr=startUp;
						    //System.out.println("===And Now("+sPtr+"):"+new String(sentenceBuffer,0,sPtr));
						}
						// We are at the beginning of the sentence so add tag to tagstring
						else {
						    tagString=new StringBuffer().append(nameTag).append(newline).append("<").append
							(RH_GlobalVars.RH_ANNOTATE_SENTENCE_BEGIN).append(">").append(tagString.toString());
						    //tagString=new String (nameTag+newline+"<"+RH_GlobalVars.RH_ANNOTATE_SENTENCE_BEGIN+">"+ tagString);
						    
						    //sentenceBuffer=tagString.getBytes();
						    //sPtr=tagString.length();
						    //newPtr=sentenceBegin-1;
						    nameTag="";
						}
					    }
					    //*** Else reset the pointer back to the location when this pattern started in the text
					    else {
						//newPtr=newPtr-(topic.getName().length()-wordBuf.length);
						int goBack=0, startUp=0;
						// here, goBack is based on all but the last word we just read
						for (int goo=0;goo<topic.getNumberKeywordStrings()-1;goo++) goBack+=keyword[goo].length()+1;
						
						//goBack=(sPtr+wordBuf.length+1)-topic.getName().length();
						//System.out.println("->GoBack="+goBack+" vs. sPtr="+sPtr);
						String tmpString = new String (new String(sentenceBuffer,0,sPtr-goBack));
						byte[] tmpBuf=new byte[tmpString.length()];
						tmpBuf=tmpString.getBytes();
						
						//*** TRY
						System.arraycopy(tmpBuf,0,sentenceBuffer,0,tmpString.length());
						sPtr=tmpString.length();
						//for (startUp=0;startUp<tmpString.length();startUp++) sentenceBuffer[startUp]=tmpBuf[startUp];
						//sPtr=startUp;
					    }
					    
					    if (debugStack) System.out.println("TagStr:"+tagString.toString());
					    wordString=tagString.toString();
					    
					    //***---Remove the items used for the match from the stack
					    for (int contIdx=0; contIdx<wordIdx; contIdx++)
						removeFromStack(matchVector[contIdx]);
					}
				    }
				}
			    }
			}
		    }
		    //*** If stack has items, then they must be popped because we no longer have a contiguous pattern
		    //*** from incoming word stream.
		    else if (stackLen>0) {
			int counter=0, stackIdx=0;
			for (stackIdx=0,counter=0;stackLen>0;stackIdx++) {
			    //System.out.print(stackLen+">Popping Stack: " + stackIdx + " =" + wordStack[stackIdx]);
			    if (stackIdx==0) counter+=popOffStack()+wordBuf.length;  
			    else counter+=popOffStack(); 
			    //System.out.println(" counter="+counter);
			}
			pushedOnStack=false;
		    }
		    else pushedOnStack=false;
		    //*** else nada, just return the word
		    
		    wordLen=wordBuf.length;
		    //System.out.println("UrlBufferSize="+urlBufferSize+ " bufferPtr="+bufferPtr+">WORD: [" + wordString +"] -- newPtr="+newPtr);
		    if (sentenceComplete && sameSentence) {
			if (debugStack) System.out.println("SENTENCE COMPLETE");
			
			// *** When a tag has caused an EOS then do not put that tag within the ANOH Sentence tag
			if (foundTag) wordString=new StringBuffer().append("<").append(RH_GlobalVars.RH_ANNOTATE_SENTENCE_END).append(">").append
					  (newline).append(wordString).toString();
			else wordString=wordString.concat("<"+RH_GlobalVars.RH_ANNOTATE_SENTENCE_END+">"+newline);
			
			// this handles multiple matches by multiple concepts in a sentence all using the same end location
			if (matchListPtr+numSentences<maxSentencesInSummary-1) {
			    //addNewSentence(matchList,matchListPtr,totalByteCount+wordString.length()+nameTag.length(),sentenceCounter);
			    addNewSentence(matchList,matchListPtr,new String(sentenceBuffer,0,sPtr)+wordString,sentenceCounter,otherSentenceCounter);
			    sentenceCounter++;      
			}
			matchList=new RHSentenceMatchList[maxMatchesPerSentence]; // re-init
			matchListPtr=0;
			
			aMatch=false;
			sameSentence=false;
		    }
		}
		
		//mainFrame.statusControl.message2(anohStatusStr);
		//*** If we have just the char (e.g. like a space between words) then write it
		if (foundSeparator) sentenceBuffer[sPtr++]=nextChar;
		
		//*** Otherwise write the new word or tag but only if they have not been pushed on the stack (because they will be 
		//    taken care of when the stack is popped or a match is found.)
		else {
		    //if (wordString.length()>=bigword) System.out.println("["+newPtr+"]W:"+wordString);
		    //wordBuf=new byte[wordString.length()];  // if i reallocate here, then i have to reallocate the sentence buffer too
		    // because they are the same size - 11-13-97 jmg
		    wordBuf=wordString.getBytes();
		    
		    //*** If we have a match in a sentence, then we automatically add a tag to the beginning of the sentence
		    /*  i moved this so that it gets added when we add sentence tags
			if (nameTag.length()>0) {
			System.out.println("***NAMETAG:"+nameTag);
			byte[] nameBuf=new byte[nameTag.length()]; nameBuf=nameTag.getBytes();
			//*** TRY
			      System.arraycopy(nameBuf,0,sentenceBuffer,sPtr,nameBuf.length);
			      sPtr+=nameBuf.length;
			      //for (i=0;i<nameBuf.length;i++) sentenceBuffer[sPtr++]=nameBuf[i];
			      nameTag="";
			      }
		    */
		    //*** Write the new information into the new buffer ***
		    //*** TRY
		    System.arraycopy(wordBuf,0,sentenceBuffer,sPtr,wordBuf.length);
		    sPtr+=wordBuf.length;
		    //for (i=0;i<wordBuf.length;i++) sentenceBuffer[sPtr++]=wordBuf[i];
		}
		if (sentenceComplete) {
		    sentenceComplete=false;
		    sentenceEnd=newPtr;
		    sentenceBegin=sentenceEnd;
		    aMatch=false; // reset
		    cleanUpStack();
		    nameTag="";
		    
		    // Keep a total of the number of bytes processed
		    totalByteCount+=sPtr;
		    // Write latest sentence buffer to the outputsream; then reset both the buffer and the point to handle the next sentence
		    anohStream.write(sentenceBuffer,0,sPtr);
		    sentenceBuffer=new byte[maxSentenceBufferLength]; sPtr=0;
		    if (debugStack) System.out.println("***NEW SENTENCEBEGIN:"+sentenceBegin+" stackLen="+stackLen);
		}
		
		// ** This implements a smooth progress update but i think it's also makes the process slower because of
		//    updates to the progress bar after each interation.
		percentDone=(double)((double)bufferPtr/(double)urlBufferSize)*100;
		//mainFrame.statusControl.updateProgress((int)percentDone);
	    }
	    
	    System.out.println("--->... Total Sentences Processed:"+ otherSentenceCounter+" scounter:"+sentenceCounter);
	    
	    /** We're done with pattern matching ... now clean up and return the buffer ***/
	    
	    documentSentenceCount=otherSentenceCounter;
	    //if (!mainFrame.keepRunning) System.out.println("SYSTEM STOPPED");
	    //*** Compute the likelihoods for the concepts based onthe findings found during the buffer reconstruction
	    //mainFrame.numSentences=numSentences; // must set numSentences before computation!
	    computeProbabilities();
	    //*** Write the Annotate HTML Tag at the end of the buffer
	    System.out.print("--->... closing anoh stream...");
	    anohStream.write(newline.getBytes()); 
	    newPtr++;
	    anohStream.write(rhDocumentFooter,0,rhDocumentFooter.length);
	    anohStream.close();
	    System.out.println("done");

	    //System.out.println("RH: BufferLength: " + urlBufferSize + " annotationBuffer Length=" + newPtr);
	    //mainFrame.statusControl.resetProgress();
	    
	    //*** This to return back to RH_MainFrame (i.e. set the variables in MainFrame to the values discovered here)
	    //mainFrame.newPtr=newPtr;
	    //mainFrame.statusControl.message2("Loading...");
	    
	    //** 01-24-98 trying this to release the objects in this array
	    matchList=null;
	    sentenceBuffer=null;
	    
	    return anohStream.toByteArray();

	} catch (IOException ex) {
	    System.out.println("+++MAJOR PROBLEM: could not open or cannot write to ANOH Stream");
	}
	// OR... if something went wrong, return original buffer
	return urlBuffer;
    }
    
    private byte[] contentMatcher2(boolean headerFound, String version, int start, String urlstr) {
	//anohStream=new RH_ByteArraySaveStream();
	anohStream=new ByteArrayOutputStream();
	int newSize=2*urlBufferSize, wordLen=0, i=0, j=0, wordCount=0, topicsIdx=0, tagIdx=0, startIdx=0, wordIdx=0;
	int totalMatches=0, count=0, 
	    //** sentenceCOunter is the counter for sentences which have matches in them
	    sentenceCounter=0, 
	    //** otherSentenceCounter is the global counter for all sentences in the document
	    otherSentenceCounter=1,
	    sPtr=0, totalByteCount=0,
	    // 11-13-97 1024 can be too small for tags, like style tags and such which are very long so i'll make this bigger
	    maxSentenceBufferLength=10000,bigword=maxSentenceBufferLength;
	double percentDone=0, score=0;
	int[] matchVector=new int[maxNumberKeywords];
	// states for the progress meter
	int incr1=10, incr2=20, incr3=30, incr4=40, incr5=50, incr6=60, incr7=70, incr8=80, incr9=90, laststate=0;
	String conceptName="", wordName="";
	
	// setup newline byte char
	String newline=getNewlineByte();
	
	byte[] wordBuf, holderBuffer, tagBuffer, sentenceBuffer;
	byte nextChar;
	// Misc flags
	boolean sentenceComplete=false, wordMatch=false, foundTag=false, foundSeparator=false, aMatch=false, sameSentence=false,
	    pushedOnStack=false, debugStack=true;
	RHLexEntry  entry=null;

	String wordString="", nameTag="", anohStatusStr="Analyzing...", anohReallocateStr="Reallocating...";
	// Pointer which keep strack of match list (list of concept matches)
	int matchListPtr=0;
	// The match list
	RHSentenceMatchList[] matchList=new RHSentenceMatchList[maxMatchesPerSentence];

	System.out.println("*****:Starting to match patterns");
	
	//*** INITIALIZATION of variables
	//***
	wordCount=newPtr=bufferPtr=0;  
	if (headerFound) {
	    bufferPtr=start;
	}
	StringBuffer tmpStr=new StringBuffer().append(RH_GlobalVars.RH_HTML_Doctype_Header).append(newline).append("<").
	    append(RH_GlobalVars.HTML_META_Tag).append(" name=").append(RH_GlobalVars.RH_DocumentHeader_BeginTag).append(" ").
	    append(RH_GlobalVars.RH_DocumentHeader_URLTag).append("=\"").
	    append(urlstr).append("\" ").
	    append("content=\"").append(version).append("\">");
	rhDocumentHeader=new byte[tmpStr.length()];
	rhDocumentHeader=tmpStr.toString().getBytes();
	rhDocumentFooter=new StringBuffer("").toString().getBytes(); //append("<").append(RH_GlobalVars.RH_DocumentHeader_EndTag).append(">").toString().getBytes();
	//*** Write the Annotate HTML Tag at the beginning of the buffer
	//for (i=0;i<rhDocumentHeader.length;i++) annotationBuffer[newPtr++]=rhDocumentHeader[i];
	//annotationBuffer[newPtr++]=newline.getBytes();
	for (i=0;i<rhDocumentHeader.length;i++) {
	    anohStream.write(rhDocumentHeader[i]);
	    newPtr++;
	    totalByteCount++;
	}
	try {
	    anohStream.write(newline.getBytes()); newPtr++; totalByteCount++;
	} catch (IOException ex) {
	    System.out.println("+++MAJOR PROBLEM: cannot write to ANOH Stream in MatchConcepts");
	}
	
	// This is the buffer which will contain the current sentence being processed; sPtr points to the last byte
	sentenceBuffer=new byte[maxSentenceBufferLength]; sPtr=0;
	
	sentenceBegin=sentenceEnd=stackLen=0;
	sentenceComplete=false;
	sentenceSummary=new RHSentenceSummary[maxSentencesInSummary];
	
	numSentences=0;
	//mainFrame.keepRunning=true;

	// **** MAIN LOOP THROUGH BUFFER
	while(bufferPtr<urlBufferSize) {
	    wordBuf=new byte[bigword];
	    wordType=RH_GlobalVars.regularWord;
	    startIdx=bufferPtr; // hold this position.
	    
	    foundTag=false;
	    foundSeparator=false;
	    pushedOnStack=false;
	    
	    nextChar=urlBuffer[bufferPtr];
	    //*** Start by looking at the first character - if it is of the stopChar class then process as a stop char or a tag
	    if (memberStopChars(nextChar)) {
		//*** Parse the stream as a tag if we find the tagChar
		if (nextChar=='<') {
		    wordBuf=parseTag(urlBuffer,urlBufferSize);
		    if (debugStack) System.out.print("["+bufferPtr+","+(newPtr+wordBuf.length)+"]"+new String(wordBuf));
		    //*** IF this is a tag which definitely defines a new sentence, then reset the sentence marker
		    if (memberStopTags(wordBuf) ||
			/// Also check for the case: "end of sentence.</i>" because i don't want to start a new sentence before the </i>
			(wordBuf[0]=='<' && wordBuf[1]=='/' && newPtr==sentenceBegin)) {
			sentenceEnd=newPtr+wordBuf.length;
			if (debugStack) System.out.println(" END:"+sentenceEnd + " state:"+ (aMatch&&sameSentence));
			sentenceComplete=true;
			otherSentenceCounter++;
		    }
		    else if (debugStack) System.out.println("");
		    foundTag=true;
		}
		//*** Otherwise it's just a stop char and we'll write just the char
		else {
		    foundSeparator=true;
		    wordLen=1;
		    bufferPtr++;
		}
	    }
	    //***Else we will try locating a word on the stream...
      else {
	  wordBuf=parseWord(urlBuffer,urlBufferSize);
	  if (debugStack) System.out.print("["+bufferPtr+","+(newPtr+wordBuf.length)+"]"+ new String(wordBuf));
	  if (eosFlag==1) { // hack because sentenceComplete could not be set in parseWord ?????
	      sentenceComplete=true;
	      otherSentenceCounter++;
	      sentenceEnd=newPtr+wordBuf.length;
	      eosFlag=0; 
	      if (debugStack) System.out.println(" END:"+sentenceEnd+" STATE:[match="+ aMatch+"] same="+sameSentence);
	  }
	  else if (debugStack) System.out.println("");
	  if (bufferPtr<urlBufferSize && wordBuf.length==0) bufferPtr++;  // increment to skip the just found char      
	  else wordCount++;
      }
	    //-------------------------------------------
	    //*** If we have a tag or a word then we setup the string and the word length
	    if (!foundSeparator) {
		wordString = new String(wordBuf,0,wordBuf.length);
		
		//** Add Word to Lexicon
		//********** 1.13.99 need to figure out what to do about lexicons
		//if (mainFrame.useLexicon && !foundTag) mainFrame.documentLexicon.addWord(wordString,otherSentenceCounter); 
		
		//*** Evaluate the new Word by performing pattern matching against the concepts
		if (!foundTag && checkKeywords2(wordString)==1) {
		    //** NOTE: if i leave this 'addWord' call here, it gathers all words in the document which are somehow connected
		    //** to the concept's topic strings.  do i want this collection?  if so, i need to create a new lexicon
		    //mainFrame.documentSpecialLexicon.addWord(wordString,mainFrame.documentLexicon.getWordCount(),otherSentenceCounter);
		    
		    pushedOnStack=true;
		    //*** Because we have a valid word in wordString, check for a match in the activeConcepts
		    for (i=0; i<activeConcepts.length; i++) {
			RHActiveConcept currentConcept=activeConcepts[i];
			if (currentConcept.isActive()) {
			    //*** Loop through all keywords for this concept to see if we have a valid match
			    for (topicsIdx=0; topicsIdx<currentConcept.getLength(); topicsIdx++) {
				//System.out.println("*****TOPICIDX:"+topicsIdx);
				RHTopicKeyword topic=currentConcept.getTopic(topicsIdx);
				String[] keyword=topic.getPhrase();
				// init matchvector; this is where i store IDs for matches in this concept
				for (int mv=0;mv<maxNumberKeywords;mv++) matchVector[mv]=-1;
				
				//***-- This check to see if any of the stack words match the keywords; the location of the match (in 
				//***-- the stack) is stored in the matchVector
				for (wordIdx=0; keyword[wordIdx]!=null; wordIdx++) {
				    //***-- I want to match from the bottom of the stack so that I get a decending order of match locations
				    //System.out.println(wordIdx+">Topic:"+topic.getName()+" Word:"+keyword[wordIdx]);
				    for (int stackIdx=stackLen; stackIdx>=0 && stackLen>0; stackIdx--) {
					if (keyword[wordIdx]!=null && keyword[wordIdx].equalsIgnoreCase(wordStack[stackIdx])) matchVector[wordIdx]=stackIdx;
				    }
				}
				//***--- Test to see if we have match
				wordMatch=true; // initialize flag first
				//***--------Test 1: If the first item in the matchVector is != -1, then we *may* have a match
				for (int testIdx=0; testIdx<wordIdx && wordMatch; testIdx++)
				    if (matchVector[testIdx]==-1) wordMatch=false;
				if (wordMatch) {
				    //***--------Test 2: Contiguous-ness:  are the values in the match vector contiguous from top to bottom?
				    for (int contIdx=wordIdx; contIdx>=0 && wordMatch; contIdx--)
					if (contIdx>0 && matchVector[contIdx]+1!=matchVector[contIdx-1]) wordMatch=false;
				    
				    //***FINAL TEST: do still have a match??
				    if (wordMatch) {  // Then we have a MATCH!!
					if (debugStack) System.out.println(newPtr+">...........Match:"+topicsIdx+">"+topic.getName());
					score=currentConcept.match(topic, newPtr, wordCount,sentenceCounter,sentenceCounter);
					totalMatches++;
					aMatch=true;
					conceptName=currentConcept.getShortName();
					wordName=wordString;  // grab the word string that matched before wordString gets changed; for lexicon
					//*** Add to match list for summarization
					matchList[matchListPtr++]=new RHSentenceMatchList(i,sentenceBegin,topicsIdx,topic.getFrequency(), otherSentenceCounter);
					
					//********** 1.13.99 need to figure out what to do about lexicons
					//** Update the lexical entry to reflect a match for each word in this topic phrase
					/*
					  if (mainFrame.useLexicon && false) {
					  for (int lexIdx=0; keyword[lexIdx]!=null; lexIdx++) {
					  entry=mainFrame.documentLexicon.getWord(keyword[lexIdx]);
					  if (entry!=null) {
					  entry.setMatched(true, otherSentenceCounter, conceptName);
					  entry=null;
					  }
					  }
					  }*/
					
					//***---Create the HTML Tag for this concept
					// This method is suppose to optimize the string concatenation process
					StringBuffer tagString = new StringBuffer().append(newline).append("<").append(RH_GlobalVars.RH_ANNOTATE_BEGIN).
					    append(" ").append(RH_GlobalVars.RH_CONCEPT_TAG).append("=\"").append(currentConcept.getName()).
					    append("\" ").append(RH_GlobalVars.RH_TOPIC_TAG).append("=\"").append(topic.getName()).append("\" ").
					    append(RH_GlobalVars.RH_SENTENCE_TAG).append("=\"").append(sentenceCounter).append("\" ").
					    append(RH_GlobalVars.RH_NUMBER_TAG).append("=").append(topic.getFrequency()).append(">");
					
					//**JSTEST 6.22.98
					/*
					  jsbuffer.append("allconcepts[").append(totalMatches-1).append("]=new concept(\"").
					  append(currentConcept.getName()).append("\", \"").append(topic.getName()).append("\",").
					  append(sentenceCounter).append(",").append(topic.getFrequency()).append(");").append(newline);
					  StringBuffer tagString = new StringBuffer().append(newline).
					  append("<A class=rhconcept href=\"javascript:void(0)\" onMouseOver=\"showconcept(").append(totalMatches-1).
					  append(")\" onMouseOut=\"hideconcept()\">");
					*/
					
					
					// I believe i'm grabbing the keyword phrase here but maybe i can do this directly without this loop ???
					for (tagIdx=0, count=0; tagIdx<wordIdx; tagIdx++) {
					    //tagString=new StringBuffer().append(tagString.toString()).append(wordStack[matchVector[tagIdx]]).append(" ");
					    tagString.append(wordStack[matchVector[tagIdx]]).append(" ");
					    count+=wordStack[matchVector[tagIdx]].length();
					}
					
					tagString.append("<").append(RH_GlobalVars.RH_ANNOTATE_END).append(">").append(newline);
					//**JSTEST 6.22.98
					//tagString.append("</A>").append(newline);
					
					//***--- Merge Strings if highlight sentence is ON: if not sameSentence, then this is the first 
					//***--- instance of a find in this sentence so go back and tag the beginning of the sentence.
					if (!sameSentence) {
					    int goBack=0;
					    goBack=(sPtr+wordBuf.length+1)-topic.getTotalLengthOfKeywords();
					    //System.out.println("->wordBuf="+wordBuf.length+"  sPtr="+sPtr+" GoBack="+goBack);
					    //System.out.println("-->PhraseLen="+topic.getTotalLengthOfKeywords()+":"+topic.getName());
					    sameSentence=true;
					    //int goBack=(newPtr+wordBuf.length)-sentenceBegin-topic.getName().length();
					    
					    if (debugStack) System.out.println(newPtr+"> Sentence begin="+sentenceBegin+" goBack="+goBack);
					    
					    // When goBack is less than 0, we have are at the beginning of the sentence and so do not need to go back
					    // to grab the chars from the beginning of the sentence
					    if (sPtr>0) {
						// This prevents landing on top of a end of sentence marker
						int startUp=0;
						if (sentenceBuffer[0]=='.' || sentenceBuffer[0]=='!' || sentenceBuffer[0]=='?' || 
						    sentenceBuffer[0]==';' || sentenceBuffer[0]==':') startUp=1;
						//System.out.println(startUp+"===So Far("+sPtr+"):"+new String(sentenceBuffer,0,sPtr));
						
						nameTag = new StringBuffer().append(newline).append("<a name=\"").append
						    (RH_GlobalVars.RH_ANOH_Tag).append(sentenceCounter).append("\"></a>").toString();
						// Supposed to optimize string concat. 11-19-97
						String tmpString = new StringBuffer().append(new String(sentenceBuffer,0,startUp)).append(nameTag).append
						    (newline).append("<").append(RH_GlobalVars.RH_ANNOTATE_SENTENCE_BEGIN).append(" ").append
						    (RH_GlobalVars.RH_ANNOTATE_SENTENCE_NUMBER).append("=").append(sentenceCounter).append(">").append
						    (new String(sentenceBuffer,startUp,goBack-startUp)).toString();
						
						byte[] tmpBuf=new byte[tmpString.length()];
						tmpBuf=tmpString.getBytes();
						
						//***TRY
						System.arraycopy(tmpBuf,0,sentenceBuffer,0,tmpString.length());
						sPtr=tmpString.length();
						//for (startUp=0;startUp<tmpString.length();startUp++) sentenceBuffer[startUp]=tmpBuf[startUp];
						//sPtr=startUp;
						//System.out.println("===And Now("+sPtr+"):"+new String(sentenceBuffer,0,sPtr));
					    }
					    // We are at the beginning of the sentence so add tag to tagstring
					    else {
						tagString=new StringBuffer().append(nameTag).append(newline).append("<").append
						    (RH_GlobalVars.RH_ANNOTATE_SENTENCE_BEGIN).append(">").append(tagString.toString());
						//tagString=new String (nameTag+newline+"<"+RH_GlobalVars.RH_ANNOTATE_SENTENCE_BEGIN+">"+ tagString);
						
						//sentenceBuffer=tagString.getBytes();
						//sPtr=tagString.length();
						//newPtr=sentenceBegin-1;
						nameTag="";
					    }
					}
					//*** Else reset the pointer back to the location when this pattern started in the text
					else {
					    //newPtr=newPtr-(topic.getName().length()-wordBuf.length);
					    int goBack=0, startUp=0;
					    // here, goBack is based on all but the last word we just read
					    for (int goo=0;goo<topic.getNumberKeywordStrings()-1;goo++) goBack+=keyword[goo].length()+1;
					    
					    //goBack=(sPtr+wordBuf.length+1)-topic.getName().length();
					    //System.out.println("->GoBack="+goBack+" vs. sPtr="+sPtr);
					    String tmpString = new String (new String(sentenceBuffer,0,sPtr-goBack));
					    byte[] tmpBuf=new byte[tmpString.length()];
					    tmpBuf=tmpString.getBytes();
					    
					    //*** TRY
					    System.arraycopy(tmpBuf,0,sentenceBuffer,0,tmpString.length());
					    sPtr=tmpString.length();
					    //for (startUp=0;startUp<tmpString.length();startUp++) sentenceBuffer[startUp]=tmpBuf[startUp];
					    //sPtr=startUp;
					}
					
					if (debugStack) System.out.println("TagStr:"+tagString.toString());
					wordString=tagString.toString();
					
					//***---Remove the items used for the match from the stack
					for (int contIdx=0; contIdx<wordIdx; contIdx++)
					    removeFromStack(matchVector[contIdx]);
				    }
				}
			    }
			}
		    }
		}
		//*** If stack has items, then they must be popped because we no longer have a contiguous pattern
		//*** from incoming word stream.
		else if (stackLen>0) {
		    int counter=0, stackIdx=0;
		    for (stackIdx=0,counter=0;stackLen>0;stackIdx++) {
			//System.out.print(stackLen+">Popping Stack: " + stackIdx + " =" + wordStack[stackIdx]);
			if (stackIdx==0) counter+=popOffStack()+wordBuf.length;  
			else counter+=popOffStack(); 
			//System.out.println(" counter="+counter);
		    }
		    pushedOnStack=false;
		}
		else pushedOnStack=false;
		//*** else nada, just return the word
		
		wordLen=wordBuf.length;
		//System.out.println("UrlBufferSize="+urlBufferSize+ " bufferPtr="+bufferPtr+">WORD: [" + wordString +"] -- newPtr="+newPtr);
		if (sentenceComplete && sameSentence) {
		    if (debugStack) System.out.println("SENTENCE COMPLETE");
		    
		    // *** When a tag has caused an EOS then do not put that tag within the ANOH Sentence tag
		    if (foundTag) wordString=new StringBuffer().append("<").append(RH_GlobalVars.RH_ANNOTATE_SENTENCE_END).append(">").append
				      (newline).append(wordString).toString();
		    else wordString=wordString.concat("<"+RH_GlobalVars.RH_ANNOTATE_SENTENCE_END+">"+newline);
		    
		    // this handles multiple matches by multiple concepts in a sentence all using the same end location
		    if (matchListPtr+numSentences<maxSentencesInSummary-1) {
			//addNewSentence(matchList,matchListPtr,totalByteCount+wordString.length()+nameTag.length(),sentenceCounter);
			addNewSentence(matchList,matchListPtr,new String(sentenceBuffer,0,sPtr)+wordString,sentenceCounter,otherSentenceCounter);
			sentenceCounter++;      
		    }
		    matchList=new RHSentenceMatchList[maxMatchesPerSentence]; // re-init
		    matchListPtr=0;
		    
		    aMatch=false;
		    sameSentence=false;
		}
	    }
	    
	    //mainFrame.statusControl.message2(anohStatusStr);
	    //*** If we have just the char (e.g. like a space between words) then write it
	    if (foundSeparator) sentenceBuffer[sPtr++]=nextChar;
	    
	    //*** Otherwise write the new word or tag but only if they have not been pushed on the stack (because they will be 
	    //    taken care of when the stack is popped or a match is found.)
	    else {
		//if (wordString.length()>=bigword) System.out.println("["+newPtr+"]W:"+wordString);
		//wordBuf=new byte[wordString.length()];  // if i reallocate here, then i have to reallocate the sentence buffer too
		// because they are the same size - 11-13-97 jmg
		wordBuf=wordString.getBytes();
		
		//*** If we have a match in a sentence, then we automatically add a tag to the beginning of the sentence
		/*  i moved this so that it gets added when we add sentence tags
		    if (nameTag.length()>0) {
		    System.out.println("***NAMETAG:"+nameTag);
		    byte[] nameBuf=new byte[nameTag.length()]; nameBuf=nameTag.getBytes();
		    //*** TRY
			  System.arraycopy(nameBuf,0,sentenceBuffer,sPtr,nameBuf.length);
			  sPtr+=nameBuf.length;
			  //for (i=0;i<nameBuf.length;i++) sentenceBuffer[sPtr++]=nameBuf[i];
			  nameTag="";
			  }
		*/
		//*** Write the new information into the new buffer ***
		//*** TRY
		System.arraycopy(wordBuf,0,sentenceBuffer,sPtr,wordBuf.length);
		sPtr+=wordBuf.length;
		//for (i=0;i<wordBuf.length;i++) sentenceBuffer[sPtr++]=wordBuf[i];
	    }
	    if (sentenceComplete) {
		sentenceComplete=false;
		sentenceEnd=newPtr;
		sentenceBegin=sentenceEnd;
		aMatch=false; // reset
		cleanUpStack();
		nameTag="";
		
		// Keep a total of the number of bytes processed
		totalByteCount+=sPtr;
		// Write latest sentence buffer to the outputsream; then reset both the buffer and the point to handle the next sentence
		anohStream.write(sentenceBuffer,0,sPtr);
		sentenceBuffer=new byte[maxSentenceBufferLength]; sPtr=0;
		if (debugStack) System.out.println("***NEW SENTENCEBEGIN:"+sentenceBegin+" stackLen="+stackLen);
	    }
	    
	    // ** This implements a smooth progress update but i think it's also makes the process slower because of
	    //    updates to the progress bar after each interation.
	    percentDone=(double)((double)bufferPtr/(double)urlBufferSize)*100;
	    //mainFrame.statusControl.updateProgress((int)percentDone);
	}
	
	System.out.println("*****TOTAL SENTENCES PROCESSED:"+ otherSentenceCounter+" scounter:"+sentenceCounter);
	
	/** We're done with pattern matching ... now clean up and return the buffer ***/
	
	
	//**JSTEST 6.22.98
	/*
	  StringBuffer tmpbuf=new StringBuffer("var allconcepts;").append(newline).append("allconcepts=new Array(").
	  append(totalMatches).append(");").append(newline).append(jsbuffer);
	  writeJSFile(tmpbuf,newline);
	 */
	    
	    documentSentenceCount=otherSentenceCounter;
	    //if (!mainFrame.keepRunning) System.out.println("SYSTEM STOPPED");
	    //*** Compute the likelihoods for the concepts based onthe findings found during the buffer reconstruction
	    //mainFrame.numSentences=numSentences; // must set numSentences before computation!
	    computeProbabilities();
	    
	    //*** Write the Annotate HTML Tag at the end of the buffer
	    try {
		System.out.print("...closing anoh stream...");
		anohStream.write(newline.getBytes()); 
		newPtr++;
		anohStream.write(rhDocumentFooter,0,rhDocumentFooter.length);
		anohStream.close();
		System.out.println("done");
	    } catch (IOException ex) {
		System.out.println("+++MAJOR PROBLEM: cannot write to ANOH Stream in MatchConcepts");
	    }
	    
	    //System.out.println("RH: BufferLength: " + urlBufferSize + " annotationBuffer Length=" + newPtr);
	    //mainFrame.statusControl.resetProgress();
	    
	    //*** This to return back to RH_MainFrame (i.e. set the variables in MainFrame to the values discovered here)
	    //mainFrame.newPtr=newPtr;
	    //mainFrame.statusControl.message2("Loading...");
	    
	    //** 01-24-98 trying this to release the objects in this array
	    matchList=null;
	    sentenceBuffer=null;
	    
	    return anohStream.toByteArray();
    }

    public RHActiveConcept findConcept(String name) {
	int i=0;
	for (i=0; i<allConcepts.length; i++)
	    if (allConcepts[i].getShortName().equalsIgnoreCase(name))
		return allConcepts[i];
	return null;
    }
    public int findConceptIndex(String name) {
	int i=0;
	for (i=0; i<allConcepts.length; i++)
	    if (allConcepts[i].getShortName().equalsIgnoreCase(name))
		return i;
	return -1;
    }
    private void activateConcepts(String conceptsList) {
	StringTokenizer tokens=new StringTokenizer(conceptsList," ");
	String name=null;
	int idx=0, i=0, size=tokens.countTokens();
	if (size>0) {
	    activeConcepts=new RHActiveConcept[size];
	    i=0;
	    while (tokens.hasMoreTokens()) {
		name=(String)tokens.nextToken();
		idx=findConceptIndex(name);
		if (idx>=0) {
		    //System.out.println(idx+"..>"+allConcepts[idx].getName());
		    activeConcepts[i]=allConcepts[idx];
		    activeConcepts[i++].setActive(true);
		}
	    }
	    makeConceptHash();
	}
	else System.out.println("****ERROR: no tokens in concept list!!!");
    }


    /**
     * Here the input is something like:
     * "Agents 45 Java 23 NLP 80 ExpSys 67"
     * where each entry has the shortname for a concept and score.  i use this when i'm reloading a
     * document that has previously been annoptated.  this updates the concepts and then i generate
     * a list to send back to the client with the concept info in it.
     */
    public void activateConceptsWithScores(String conceptsList) {
	//System.out.println("***Activating concepts: "+conceptsList);
	StringTokenizer tokens=new StringTokenizer(conceptsList," ");
	String name=null;
	int idx=0, i=0, size=tokens.countTokens(), value=0;
	if (size>0) {
	    activeConcepts=new RHActiveConcept[size/2];  // note the div by 2 because each entry hs two elements
	    i=0;
	    while (tokens.hasMoreTokens()) {
		name=(String)tokens.nextToken();
		value=Integer.parseInt((String)tokens.nextToken());
		idx=findConceptIndex(name);
		if (idx>=0) {
		    //System.out.println(idx+"..>"+allConcepts[idx].getName());
		    activeConcepts[i]=allConcepts[idx];
		    activeConcepts[i].setValue(value);
		    activeConcepts[i++].setActive(true);
		}
	    }
	    makeConceptHash();
	}
	else System.out.println("****ERROR: no tokens in concept list!!!");
	//System.out.println("***DONE ACTIVATING CONCEPTS");
    }

    /**
     * this is basically the same routine from ricoh.rh.RH_Profile.readConceptContents()
     */
    public boolean setConcepts(String conceptsString) {
	boolean success=true;
	String lineString="", conceptName="", conceptShortName="";
	int i=0, j=0, k=0, x=0, num=0, numStyles=0, wordsProcessed=0, maxKeywords=RH_GlobalVars.maxNumberKeywords, 
	    maxWordStrings=10,numKeywords=0, active=0;
	Double prior;
	byte[] buffer, wordBuf=null; 
	String[][] keywords=new String[maxKeywords][maxWordStrings];
	
	//System.out.println(conceptsString);
	
	/**
	 * 1.6.99 - Converts string into a BufferedReader so i can read a line at a time
	 */
	char[] chararray=new char[conceptsString.length()];
	conceptsString.getChars(0,conceptsString.length(),chararray,0);
	BufferedReader dataInput=new BufferedReader(new CharArrayReader(chararray));
	try {
	    lineString=dataInput.readLine();
	    //System.out.println("-->> FirstLine:"+lineString);
	    lineString=dataInput.readLine();
	    //System.out.println("-->> SecondLine:"+lineString);
	    num=Integer.parseInt(lineString);
	} catch (IOException ex) { success=false; System.out.println("Failed to read the next line"); }
	
	try {
	    if (num>0) {
		allConcepts=new RHActiveConcept[num];
		for (i=0;i<num;i++) {
		    //System.out.println("------------------------");
		    conceptName=dataInput.readLine();
		    conceptShortName=dataInput.readLine();
		    // Read prior probability
		    lineString=dataInput.readLine();
		    prior=new Double(lineString);
		    // Read state
		    lineString=dataInput.readLine();
		    active=Integer.parseInt(lineString);
		    // Read number keywords
		    lineString=dataInput.readLine();
		    numKeywords=Integer.parseInt(lineString);
		    //System.out.println("Name:"+conceptName+" phrases:"+numKeywords);
		    //System.out.println(i+"> Concept:"+conceptName+" - "+conceptShortName+" prior="+prior.doubleValue()+" topics="+numKeywords+" active="+active);
		    // Now pick out the highlight style and RGB values 
		    for (j=0;j<numKeywords && j<maxKeywords;j++) {  
			lineString=dataInput.readLine();
			buffer=new byte[lineString.length()];
			buffer=lineString.getBytes();
			wordBuf=new byte[lineString.length()]; 
			k=0;
			// Process line of text making individual keywords for each topic
			for (k=0, wordsProcessed=0;k<buffer.length && wordsProcessed<maxWordStrings;k++) {
			    for (x=0;x<buffer.length && k<buffer.length && buffer[k]!=' ';x++) wordBuf[x]=buffer[k++];
			    keywords[j][wordsProcessed]=new String(wordBuf,0,x);
			    //System.out.print(j+">...["+wordsProcessed+"]:");
			    //System.out.print(keywords[j][wordsProcessed]);
			    wordsProcessed++;
			}
			//System.out.println("");
			keywords[j][wordsProcessed]=null;
		    }
		    allConcepts[i] = new RHActiveConcept(conceptName,conceptShortName,prior.doubleValue());
		    allConcepts[i].addKeywords(keywords,j);
		    if (active==1) allConcepts[i].setActive(true);
		    else allConcepts[i].setActive(false);
		    lineString=dataInput.readLine(); // read blank line that separates concepts
		}
		lineString=dataInput.readLine();
		success=true;
	    }
	} catch (IOException ex) {lineString=null; System.out.println("Failed to readline in MeterHeader"); success=false;}
	
	return success;
    }

    /**
     * Make a hash table of the keywords in each topic for every concept in the user's profile
     */
    public void makeConceptHash () {
	int i=0,j=0,k=0,p=0,numberKeywords=0;
	
	// Count the total number of keywords for all concepts
	for (i=0; i<activeConcepts.length; i++) {
	    if (activeConcepts[i].isActive()) {
		int numberTopics=activeConcepts[i].getLength();
		numberKeywords+=activeConcepts[i].getNumberTopicPhrases();
	    }
	}
	System.out.println("[active phrases: " + numberKeywords+"] ");
	
	//  SHOULD I USE A HASH FOR the CONCEPT WORDS??? 7-18-97
	conceptHash=new Hashtable(numberKeywords);
	
	// Make the hash table of keywords
	for (i=0; i<activeConcepts.length; i++) {
	    if (activeConcepts[i].isActive()) {
		RHActiveConcept concept=activeConcepts[i];
		int numberTopics=concept.getLength();
		for (j=0;j<numberTopics;j++) {
		    RHTopicKeyword topic=concept.getTopic(j);
		    String[] keywords=topic.getPhrase();
		    int len=concept.topics[j].getNumberKeywordStrings();
		    for (k=0; k<len;k++) {
			//System.out.println("Word:" + keywords[k]);
			// No duplicates: i use the hash as verification that a word is needed (not based on location in a topic, etc.)
			if (conceptHash.get(keywords[k])==null)
			    // note that all strings are reduced to lower case
			    conceptHash.put(keywords[k].toLowerCase(),topic);
		    }
		}
	    }
	}
    }

    /**
     * Compute the posteriors probabilities for each active concept
     */
    public void computeProbabilities() {
	//** First Create sentence location vectors and assign them to each concept
	createSentenceVectors();
	for (int i=0; i<activeConcepts.length; i++) {
	    if (activeConcepts[i]!=null && activeConcepts[i].isActive()) 
		activeConcepts[i].computePosterior(documentSentenceCount,true); //useSentenceLocation);
	}
    }

    /**
     *  Creates sentence location vectors and assigns them to each concept
     */
    private void createSentenceVectors() {
	Vector vector=null;
	//System.out.println("--Sentences:"+sentenceSummary.length);
	for (int i=0;i<activeConcepts.length;i++) {
	    RHActiveConcept concept=activeConcepts[i];
	    vector=new Vector();
	    //System.out.println("--...Concept:"+concept.getShortName());
	    // find all relevant sentences pertaining to the current concept
	    for (int j=0;j<numSentences;j++) {
		RHSentenceSummary sentence=sentenceSummary[j];
		//System.out.print("--...:::S"+j+"+>");
		if (sentence.getConcept()==concept) {
		    vector.addElement(new Integer(sentence.getOverallSentenceNumber()));
		    //System.out.println("***");
		}
		//else System.out.println("");
	    }
	    concept.setSentenceVector(vector);
	}
    }

    /**
     * returns a null if the newchar is not a member of the stopChars;  otherwise returns the matched char
     */
    private char[] stopChars={' ', '\n', '\t', '\r', '.', '=', '+', '!', '?', '"', ';', ':', 
			      '(', ')', '{', '}', '[', ']', '_', '|', '@', '~', '`', '*', '^', '<', '>', ','};
    
    private String[] stopTags={"<h1>","</h1>","<h2>","</h2>","<h3>","</h3>","<h4>","</h4>","<h5>","</h5>",
			       "<h6>","</h6>","<p>","<li>","<ul>","</ul>", "<hr>", "<body>", "<html>", "</body>", "</html>",
			       "<ol>", "<dt>", "<title>", "</title>", "<head>", "</head>" };
    
    private boolean memberStopChars(byte newchar) {
	int i=0;
	boolean found=false;
	for (i=0; i<stopChars.length && !found;i++)
	    if (newchar==stopChars[i]) found=true;
	return found;
    }
    private boolean memberStopTags(byte[] buf) {
	String tag=new String(buf);
	//System.out.print("Testing TAG:"+tag);
	boolean found=false;
	for (int i=0;i<stopTags.length && !found;i++)
	    if (stopTags[i].equalsIgnoreCase(tag)) found=true;
	//if (tag.startsWith(stopTags[i])) found=true;
	//System.out.print(found);
	return found;
    }
    
    private byte[] parseWord(byte[] buffer, int size) {
	byte[] buf=new byte[1]; // initialization
	int tmpPtr=bufferPtr, len=0, i=0, emailCount=0, urlCount=0, wordCount=0; // default
	boolean stop=false;
	//for (len=0;tmpPtr<size && !memberStopChars(buffer[tmpPtr]);len++, tmpPtr++);
	eosFlag=0;
	
	for (len=0;tmpPtr<size && !stop;len++, tmpPtr++) {
	    if ((buffer[tmpPtr]=='.' || buffer[tmpPtr]=='!' || buffer[tmpPtr]=='?' || buffer[tmpPtr]==';' || buffer[tmpPtr]==':') &&
		tmpPtr+1<size && memberStopChars(buffer[tmpPtr+1])) {
		//	  tmpPtr+1<size && (buffer[tmpPtr+1]==' ' || buffer[tmpPtr+1]=='\n' || buffer[tmpPtr+1]=='\r' ||
		//	    buffer[tmpPtr+1]=='\t' || buffer[tmpPtr+1]=='<' || buffer[tmpPtr+1]==buffer[size-1])) {
		//System.out.print("<EOS>");
		eosFlag=1;
		//sentenceBegin=sentenceEnd+1;
		//sentenceEnd=tmpPtr+1;
		sentenceComplete=true;
		//System.out.println("***NEW SENTENCE END:"+sentenceEnd + " flag="+sentenceComplete);
		stop=true;
	    }
	    else if (buffer[tmpPtr]=='.' && tmpPtr+1<size && buffer[tmpPtr+1]!=' ') {
		if (emailCount>0) emailCount++; // this would be greater than 0 because it would have already encountered an '@'
		else urlCount++;
	    }
	    else if (buffer[tmpPtr]=='@' && tmpPtr+1<size && buffer[tmpPtr+1]!=' ') emailCount++;
	    else if (buffer[tmpPtr]=='<' && tmpPtr+1<size && buffer[tmpPtr+1]!='/') stop=true;
	    else if (memberStopChars(buffer[tmpPtr])) stop=true; 
	    else wordCount++;
	}
	
	//if (tmpPtr<size) nextChar=buffer[tmpPtr-1];  // save the char that stopped the word parsing
	//else nextChar=0;
	buf=new byte[len-1];
	for (i=0;i<len-1;i++) buf[i]=buffer[bufferPtr++];
	
	// now figure out what kind of word this was
	if (urlCount>0 && urlCount>=emailCount) wordType=RH_GlobalVars.urlWord;
	else if (emailCount>0) wordType=RH_GlobalVars.emailWord;
	else wordType=RH_GlobalVars.regularWord;
	byte[] foo=new byte[1];
	if (bufferPtr<size) foo[0]=buffer[bufferPtr];
	//System.out.print(new String(buf)+" "+bufferPtr+" | "+new String(foo));
	//System.out.println(" flag="+sentenceComplete);
	return buf;
    }
    
    private byte[] parseTag(byte[] buffer, int size) {
	byte[] tag;
	int i=0,len=0, tmpPtr=bufferPtr;
	for (len=0;tmpPtr<size && buffer[tmpPtr]!='>';len++, tmpPtr++);
	nextChar=buffer[tmpPtr];  // save the char that stopped the word parsing
	tag=new byte[len+1];
	for (i=0;i<len+1;i++) tag[i]=buffer[bufferPtr++];
	//bufferPtr--;
	//System.out.println(new String(tag)+" "+bufferPtr);
	return tag;
    }
    private byte[] makeEndOfSentence (byte[] buf) {
	String word;
	// This is a hack because i can't find info on converting a single byte to a string - :^/
	byte[] foo=new byte[1];
	foo[0]=nextChar;
	word=new String(new String(buf)+new String(foo)+"<"+RH_GlobalVars.RH_ANNOTATE_SENTENCE_END+">");
	return word.getBytes();
    }

    /**
     * Adds a new sentence to the summary collection as the document is processed
     */
    private void addNewSentence(RHSentenceMatchList[] list, int len, int newEnd, int sentenceNum, int overallSNum) {
	int begin=0;
	//System.out.println("AddingSentence:" + len + " total so far="+numSentences);
	for (int i=0; i<len && i<maxSentencesInSummary-1;i++) {
	    begin=list[i].getBegin();
	    System.out.print(i+">"+ list[i].getIdx()+" - "+list[i].getBegin()+" topic="+list[i].getNumber()+
			     " sentence="+sentenceNum);
	    sentenceSummary[numSentences++]=new RHSentenceSummary(activeConcepts[list[i].getIdx()],begin,newEnd,
								  list[i].getTopicIdx(),list[i].getNumber(),sentenceNum, overallSNum);
	}
    }
    
    private void addNewSentence(RHSentenceMatchList[] list, int len, String sentence, int sentenceNum, int overallSNum) {
	int begin=0;
	for (int i=0; i<len && i<maxSentencesInSummary-1;i++) {
	    //System.out.println(i+">"+ list[i].getIdx()+" - "+sentence);
	    sentenceSummary[numSentences++]=new RHSentenceSummary(activeConcepts[list[i].getIdx()],begin,sentence,
								  list[i].getTopicIdx(),list[i].getNumber(),sentenceNum,overallSNum);
	}
    }

    // Hash table based version
    private int checkKeywords2(String word) {
	int stackModified=0;
	if (conceptHash.get(word.toLowerCase())!=null) {
	    if (!memberOfStack(word)) {
		pushOnStack(word);
		return 1;
	    }
	}
	return 0;
    }
    
    /**
     * Push a word onthe stack that has matched a topic keyword; return 1 if successful
     */
    private int pushOnStack(String word) {
	int i=0;
	// Push word onto stack
	if (stackLen+1<maxStack) {
	    if (stackLen!=0) {
		for (i=stackLen; i>0; i--) 
		    wordStack[i]=wordStack[i-1];
	    }
	    stackLen++;
	    wordStack[0]=new String(word);
	    return 1;
	}
	else return 0;
    }
    /**
     * Pop words from stack
     */
    private int popOffStack() {
	int size=0;
	//System.out.println("Popped Off[" + stackLen+"]: " + wordStack[0]);
	if (stackLen<=0) return 0;
	else if (stackLen==1) {
	    size=wordStack[0].length();
	    wordStack[0]=null;
	    stackLen=0;
	    return size;
	}
	else {
	    size=wordStack[0].length();
	    for (int i=0; i<stackLen-1; i++) {
		if (i<stackLen+1) wordStack[i]=wordStack[i+1];
	    }
	    wordStack[stackLen-1]=null;
	    stackLen--;
	    return size;
	}
    }
    /**
     * Remove an item from the stack
     */
    private int removeFromStack(int idx) {
	boolean itemFound=false;
	for (int i=idx; i<stackLen-1;i++) {
	    if (i+1<stackLen) wordStack[i]=wordStack[i+1];
	}
	stackLen--;
	return 1;
    }
    private boolean memberOfStack(String word) {
	int i=0;
	boolean found=false;
	for (i=0;i<stackLen && !found;i++)
	    found=wordStack[0].equalsIgnoreCase(word);
	return found;
    }
    /**
     * Clean up stack: removes all items from stack
     */
    private void cleanUpStack() {
	wordStack=new String[maxStack];
	stackLen=0;
    }

    public int getNumberConcepts() {
	return allConcepts.length;
    }

    public String getNewlineByte() {
	byte nl=(byte)'\n';
	byte[] newLine=new byte[1];
	newLine[0]=nl;
	return new String(newLine);
    }

    /**
     * This simply reads the first tag in every document and returns that tag; i'm looking for the DOCTYPE tage
     * but if it is not found then i will add my own after annotation.
     */
    public String readDocTypeHeader(byte[] buffer) {
	int i=0, size=160,incr=80, idx=0;
	boolean found=false;
	String text=null;
	//System.out.println ("**Here we go...");
	if (size>buffer.length) {
	    size=40;
	    incr=40;
	}
	while (size<buffer.length && !found) {
	    text=new String(buffer,i,size);
	    idx=text.indexOf(">");
	    //System.out.println("**Idx="+idx+" >>"+text);
	    if (idx>0) {
		text=new String(buffer,i,idx+1);
		found=true;
	    }
	    else size+=incr;
	}
	//System.out.println("=====LOOKY HERE:"+text);
	return text;
    }

    public String getConceptInfo() {
	StringBuffer buf=new StringBuffer();
	for (int i=0; i<allConcepts.length; i++) {
	    buf.append(allConcepts[i].getShortName()).append(" "+allConcepts[i].getValue()+" ");
	}
	return buf.toString();
    }

    /**
     * Given a path to a user's profile which includes a document key, read the conept file and ocnstruct
     * a string with data as follows:
     * "Agents 45 Java 23 NLP 80 ExpSys 67 Interface 0"
     */
    public String readConceptDataFile(String path) {
	StringBuffer filename=new StringBuffer(path).append(RH_GlobalVars.rhInfoConceptsFileName+RH_GlobalVars.rhInfoFileExt), buffer;
	//System.out.println("**Reading concept file: "+filename.toString());
	BufferedReader dataInput=null;
	int num=0;
	String lineString=null;
	buffer=new StringBuffer();
	try { 
	    dataInput=new BufferedReader(new FileReader(filename.toString()));
	    num=Integer.parseInt(dataInput.readLine());
	    for (int i=0;i<num;i++) buffer.append(dataInput.readLine()+" ");
	    dataInput.close();
	} catch (IOException ex) { System.out.println("Could not open concepts file:"+filename.toString()); }
	return buffer.toString();
    }

    public void readSentenceDataFile(String path) {
	StringBuffer filename=new StringBuffer(path).append(RH_GlobalVars.rhSSFileName+RH_GlobalVars.rhInfoFileExt), buffer;
	//System.out.println("**Reading sentence file: "+filename.toString());
	String newline=getNewlineByte();	
	String sstr=null, namestr=null, snumstr=null, topicnumstr=null, overallsnumstr=null, beginstr=null, endstr=null, numStr=null;

	RHFileContents fc=new RHFileContents();

	System.out.println("grab7: " + filename.toString());
	String sentenceBuffer=fc.grabFileContents(filename.toString());
	//System.out.println("**grabbed filecontents...");
	StringTokenizer tmp =new StringTokenizer(sentenceBuffer,newline);
	//System.out.println("**make first tokens...");
	if (tmp.hasMoreTokens()) {
	    numStr=(String)tmp.nextToken();
	    numSentences=Integer.parseInt(numStr);
	}
	else numSentences=0;
	//System.out.println("**numsentence="+numSentences);
	String tmpString=sentenceBuffer.substring(numStr.length()+1);
	//System.out.println("**grabbed substring...");
	StringTokenizer tokens=new StringTokenizer(tmpString,RH_GlobalVars.rhFieldSeparator);
	//System.out.println("**made next tokens...");
	RHActiveConcept concept=null;

	int counter=0;
	sentenceSummary=new RHSentenceSummary[numSentences];
	while (tokens.hasMoreTokens() && counter<numSentences) {
	    //System.out.print(counter+">");
	    sstr=(String)tokens.nextToken();
	    namestr=(String)tokens.nextToken();
	    snumstr=(String)tokens.nextToken();
	    topicnumstr=(String)tokens.nextToken();
	    overallsnumstr=(String)tokens.nextToken();
	    beginstr=(String)tokens.nextToken();
	    endstr=(String)tokens.nextToken();
	    
	    concept=findConcept(namestr);
	    if (concept!=null) { 
		sentenceSummary[counter++]=new RHSentenceSummary(concept, Integer.parseInt(beginstr), sstr, 0, 
								 Integer.parseInt(topicnumstr), Integer.parseInt(snumstr), Integer.parseInt(overallsnumstr));
		//System.out.println(namestr);
		concept.pushSentence(Integer.parseInt(overallsnumstr));
	    }
	    //else System.out.println("**!!");
	}
    }

    public String createConceptSentenceData() {
	//System.out.println("***Creating concept sentence data...");
	Enumeration enum=null;
	Vector vector=null;
	StringBuffer buffer=null, mainbuffer=new StringBuffer();
	int counter=0;
	for (int i=0;i<activeConcepts.length;i++) {
	    //System.out.print(i+">"+activeConcepts[i].getShortName());
	    mainbuffer.append(activeConcepts[i].getShortName()+" ");
	    buffer=new StringBuffer();
	    counter=0;
	    vector=activeConcepts[i].getSentenceVector();
	    if (vector!=null) {
		enum=vector.elements();
		
		while(enum.hasMoreElements()) {
		    buffer.append(((Integer)enum.nextElement()).intValue()+" ");
		    counter++;
		}
		//System.out.println(counter+" "+buffer.toString());
	    }
	    mainbuffer.append(counter+" "+buffer.toString());
	}
	return mainbuffer.toString();
    }

    public RHActiveConcept getNumberOneConcept() {
	RHActiveConcept concept=null;
	if (sortedConcepts!=null && sortedConcepts.length>0) {
	    int idx=sortedConcepts[sortedConcepts.length-1].getIdx();
	    concept=activeConcepts[idx];
	}
	return (concept);
		
    }

    public void sortConcepts() {
	sortedConcepts=new RHSortableConcept[activeConcepts.length];
	for (int i=0,j=0;i<activeConcepts.length;i++) 
	    if (activeConcepts[i]!=null) sortedConcepts[j++]=new RHSortableConcept(i,activeConcepts[i].getValue());
	
	RHQSort qsort=new RHQSort();
	try {
	    qsort.sort(sortedConcepts);
	} catch (Exception ex) {
	    System.out.println("Sort threw up!");
	}
    }

    public RHSortableConcept[] getSortedConcepts() {
	return (sortedConcepts!=null ? sortedConcepts : null);
    }
    public RHActiveConcept[] getActiveConcepts() {
	return (activeConcepts!=null ? activeConcepts : null);
    }

    private String calculateAnnotationTime(Date starttime, Date endtime) { //Calendar scal, Calendar ecal) {
	//turn new StringBuffer().append(endtime.getTime()-starttime.getTime()).toString();
	int seconds, minutes, hours, max_seconds=60;
	long secs=(endtime.getTime()-starttime.getTime())/1000, secs_n_hour=max_seconds*max_seconds;
	annotationDuration=secs;
	StringBuffer minbuf, secbuf, hrbuf;
	
	hours=(int)(secs/secs_n_hour); //floor(secs/secs_n_hour);
	minutes=(int)secs/max_seconds;  // grabs whole number for minutes, e.g. 2451secs/60secs=40.85, minutes=40
	seconds=(int)(secs-(minutes*max_seconds)); // grab remainder, seconds=51
	
	//System.out.println("secs="+secs+" secs_n_hour="+secs_n_hour+" hours="+hours+" min="+minutes+" secs="+seconds);
	//** Setup hour buffer
	if (hours>9) hrbuf=new StringBuffer().append(hours); //sprintf(hrbuf,"%1.0f",hours);
	else hrbuf=new StringBuffer().append("0").append(hours); //printf(hrbuf,"0%1.0f",hours);
	//** Setup minutes buffer
	if (minutes>9) minbuf=new StringBuffer().append(minutes); //sprintf(minbuf,"%1.0f",minutes);
	else minbuf=new StringBuffer().append("0").append(minutes); //sprintf(minbuf,"0%1.0f",minutes);
	//** Setup seconds buffer
	if (seconds>9) secbuf=new StringBuffer().append(seconds); //sprintf(secbuf,"%1.0f",seconds);
	else secbuf=new StringBuffer().append("0").append(seconds); //sprintf(secbuf,"0%1.0f",seconds);
	
	//** create final buffer
	return new StringBuffer().append(hrbuf.toString()).append(":").append(minbuf.toString()).append(":").append(secbuf.toString()).toString();
  }

    public String getAnnotationDurationStr() {
	return annotationDurationStr;
    }
    public long getAnnotationDuration() {
	return annotationDuration;
    }

    public RHSentenceSummary[] getSentenceSummary() {
	return sentenceSummary;
    }
    public int getSentenceSummaryLength() {
	return numSentences;
    }


    /**
     * Because tagged sentences do not have info about whether the concepts within are active or not, i had to come up 
     * with a method for 'activating" sentences on the fly, as users change sensitivity or turn concepts off.
     * This checks all topics and activates the sentences they point to.  Then, in the browser, when processing a sentence
     * I can check first to see if the sentence (given the occurance number) is active and render it appropriately
     */
    public String setActiveSentences(int currentSensitivity) { 
	System.out.println("ooo>Starting sentence processing...");
	int scoresPtr=0, counter=0;;
	StringBuffer activeSentencesBuffer=new StringBuffer(), conceptBuffer=null, sentenceBuffer=null;
	System.out.println("oooo> Concepts="+activeConcepts.length);
	System.out.println("oooo> ActiveSentences="+numSentences);
	System.out.println("oooo> actual="+sentenceSummary);
	if (numSentences>0) {
	    for (int i=0;i<numSentences && sentenceSummary[i]!=null;i++) sentenceSummary[i].setActive(false); // init
	    for (int i=0;i<activeConcepts.length;i++) {
		conceptBuffer=new StringBuffer(activeConcepts[i].getShortName()+" ");
		sentenceBuffer=new StringBuffer();
		counter=0;
		if (activeConcepts[i]!=null && activeConcepts[i].isActive() && activeConcepts[i].getValue()>=currentSensitivity) {
		    int numberTopics=activeConcepts[i].getLength();
		    //System.out.println(i+">Concept:"+activeConcepts[i].getName()+" Val="+activeConcepts[i].getValue());
		    //System.out.println(i+">"+activeConcepts[i].getSentenceVector());
		    for (int j=0;j<numberTopics;j++) {
			RHTopicKeyword topic=activeConcepts[i].getTopic(j);
			scoresPtr=topic.getFrequency();
			//*** Iterate throught the matches for this topic and set the associated sentence to active 
			//    (there will be redundancy but it's that or checking first -- which is better?)
			//System.out.print("...TOPIC: "+j+"> ");
			for (int k=0;k<scoresPtr;k++) {
			    //System.out.print(topic.sentenceLocation[k]+",");
			    if (topic.sentenceLocation[k]>=0 && topic.sentenceLocation[k]<numSentences) {
				//System.out.println("***Active: k="+k+" loc="+topic.sentenceLocation[k]);
				if (sentenceSummary[topic.sentenceLocation[k]]!=null) {
				    sentenceSummary[topic.sentenceLocation[k]].setActive(true);
				    sentenceBuffer.append(sentenceSummary[topic.sentenceLocation[k]].getSentenceNumber()+" ");
				    counter++;
				}
			    }
			    //System.out.print(sentenceSummary[topic.sentenceLocation[k]].getSentenceNumber()+".");
			}
			//System.out.println("");
		    }
		    conceptBuffer.append(counter+" ").append(sentenceBuffer.toString());
		    System.out.println("===>"+conceptBuffer.toString());
		}
	    }
	    activeSentencesBuffer.append(conceptBuffer);
	    //System.out.println("");
	}
	//System.out.println("");
	return activeSentencesBuffer.toString();
    }
 }

