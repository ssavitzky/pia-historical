/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 6.20.97 - revised 02-06-98
 *
 */
package ricoh.rh;

import java.io.*;
import java.util.*;


public class RH_Concept {
    private int numTopics, value;
    // This was used to hold a collection of topic arrays (RH_TopicKeywords) but I have recently
    // changed my method of concept (and subconcept) representation such that I currently only
    // use 1 RH_TopicKeyword object for each concept.  This object contains all keyword phrases assoc.
    // with the main concept.  In the future this may change, and i may expand the use of topics.
    // For now (11-26-97), the array "topics" is singular and thus, the small number (5) for maxTopics. jmg
    private int maxTopics=5, documentSentenceCount=0;
    private String name, shortname;
    private boolean active=false, satisfied=false;
    private boolean useSLoc=false;
    private double prior, priorNot;
    private Vector sentences;
    
    
    RH_Concept (String newname, String sname) {
	name=newname;
	shortname=sname;
	
	priorNot=1-prior;
    }
    RH_Concept (String newname, String sname, double newprior) {
	name=newname;
	shortname=sname;
	prior=newprior;
	priorNot=1-prior;
    }
   

    public String getName() {
	return name;
    }
    public String getShortName() {
	return shortname;
    }
    
    public void setShortName(String nm) {
	shortname=nm;
    }
    public void setName(String nm) {
	name=nm;
    }
    // Satisfied, means that we have a match with this concept
    public boolean satisfied() {
	return satisfied;
    }
    public void setSatisfied (boolean set) {
	satisfied=set;
    }
    
    // Active means that the user wants the system to look for this concept
    public boolean isActive() {
	return active;
    }
    public void setActive(boolean set) {
	active=set;
    }
    public void setPrior(double np) {
	prior=np;
	priorNot=1-prior;
    }
    public double getPrior() {
	return prior;
    }

    public int getValue() {
	return value;
    }
    /**
     * This is used when reading the info file which only contains the overall value for the concept given the 
     * current document.  so i set the value but all topic values are not set!
     */
    public void setValue(int val) {
	value=val;
    }

    public void setSentenceVector(Vector v) {
	sentences=v;
    }
    public Vector getSentenceVector() {
	return sentences;
    }

    public int getIconValue() {
	double diff=12.5; 
	int incr=2;
	
	if (value>=(100-diff)) return 8;
	else if (value>=(100-(incr++*diff))) return 7;
	else if (value>=(100-(incr++*diff))) return 6;
	else if (value>=(100-(incr++*diff))) return 5;
	else if (value>=(100-(incr++*diff))) return 4;
	else if (value>=(100-(incr++*diff))) return 3;
	else if (value>=(100-(incr++*diff))) return 2;
	else if (value>0) return 1;
	else return 0;
    }
    
}
