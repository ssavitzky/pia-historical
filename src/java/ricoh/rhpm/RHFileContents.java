/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 1.6.99
 *
 * Grabs the contents of a file and returns it in a string
 *
 */
package ricoh.rhpm;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.*;
import java.lang.*;
import java.util.StringTokenizer;
import java.util.*;

public class RHFileContents {

    RHFileContents() {
    }

    public static String grabFileContents(String filename) {
	String s = new String();
	File f;
	char[] buff = new char[50000];
	InputStream is;
	InputStreamReader reader;
	URL url;

	try {
	    f = new File(filename);
	    reader = new FileReader(f);
	    int nch;
	    while ((nch = reader.read(buff, 0, buff.length)) != -1) {
		s = s + new String(buff, 0, nch);
	    }
	    reader.close();
	} catch (java.io.IOException ex) {
	    s = "Could not load file: " + filename;
	}
	
	return s;
    }

    public static String grabFileContents(InputStream is) {
	String s = new String();
	char[] buff = new char[50000];
	InputStreamReader reader;
	URL url;

	try {
	    reader = new InputStreamReader(is);
	    int nch;
	    while ((nch = reader.read(buff, 0, buff.length)) != -1) {
		s = s + new String(buff, 0, nch);
	    }
	    reader.close();
	} catch (java.io.IOException ex) {
	    s = "Could not read stream: ";
	}
	
	return s;
    }
}
