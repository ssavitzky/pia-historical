/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_StopWords: stop word class
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 3.11.98
 *
 */
package ricoh.rhpm;

import java.io.*;
import java.lang.*;
import java.util.*;

import ricoh.rh.RH_GlobalVars;

public class RHStopWords {
    private Hashtable words;
    private StringBuffer filename=null;
    
    RHStopWords(String path) {
	words=new Hashtable();
	filename=new StringBuffer(path).append(RH_GlobalVars.rhStopwordsFileName).append(RH_GlobalVars.rhInfoFileExt);
	setup();
    }
    private void setup() {
	File file = new File(filename.toString());
	String line="";
	//System.out.print("***-Reading StopWords...");
	if (file.exists()) {
	    try {
		BufferedReader dataInput=new BufferedReader(new FileReader(file));
		while ((line=dataInput.readLine())!=null) {
		    words.put(line.toUpperCase(),line);
		}
		dataInput.close();
		//System.out.println(": "+words.size()+" words read");
	    } catch (IOException ex) { 
		System.out.println("***ERROR: Could not open stopword file:"+ filename.toString());
	    }
	}
	else System.out.println("***Error: cannot load stopwords:"+filename.toString()+" - not found");
    }
    
    public boolean stopWord(String word) {
	if (words.get(word.toUpperCase())!=null) return true;
	else return false;
    }
    public int size() {
	return words.size();
    }
}

