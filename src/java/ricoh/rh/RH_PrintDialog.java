/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.24.97
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

class RH_PrintDialog extends JDialog implements ActionListener {

    private RH_CommBus commBus;
    private JLabel startPage, endPage, title, url, server, dummy2;
    private JTextField startPgField, endPgField;
    private JButton ok, cancel;
    private JCheckBox coverSheet, hardcopy;
    private String documentTitle="", documentURL="";
    private String fontname="Sans Serif";
    private int width=450, height=230, fontsize=11, coversheetFlag=0, hardcopyFlag=0;

    RH_PrintDialog (RH_CommBus bus, String titlestr, String urlstr) {
	commBus=bus;
	documentTitle=titlestr;
	documentURL=urlstr;
	Font font=new Font(fontname,Font.PLAIN,fontsize);
	Font boldfont=new Font(fontname,Font.BOLD,fontsize+1);
	Font italicfont=new Font(fontname,Font.ITALIC,fontsize-1);

	JPanel mainPanel=new JPanel();
	mainPanel.setLayout(new BorderLayout());
	getContentPane().add("Center",mainPanel); 

	JPanel titlePanel=new JPanel();
	titlePanel.setLayout(new GridLayout(7,1));
	mainPanel.add(titlePanel,BorderLayout.NORTH);
	title=new JLabel(documentTitle,JLabel.CENTER);
	titlePanel.add(title,BorderLayout.NORTH);
	title.setFont(boldfont);
	url=new JLabel(documentURL,JLabel.CENTER);
	titlePanel.add(url);
	url.setFont(boldfont);
	server=new JLabel("PIA Print Server:  "+commBus.getPiaHost()+":"+commBus.getPiaPort(),JLabel.CENTER);
	titlePanel.add(server);
	server.setFont(boldfont);

	titlePanel.add(new JLabel(""));
	titlePanel.add(new JLabel(""));
	JLabel dummy1=new JLabel("(Leave end page empty if you do not know how many pages to print)",JLabel.CENTER);
	titlePanel.add(dummy1);
	dummy1.setFont(italicfont);
	titlePanel.add(new JLabel(""));

	Dimension fieldsize=new Dimension(20,10);
	JPanel pagePanel=new JPanel();
	pagePanel.setLayout(new GridLayout(1,6,25,1));
	mainPanel.add(pagePanel,BorderLayout.CENTER);
	coverSheet=new JCheckBox("CoverSheet",true);
	coverSheet.setFont(font);
	coversheetFlag=1;  // default is true
	coverSheet.addActionListener(this);
	pagePanel.add(coverSheet);
	hardcopy=new JCheckBox("Hardcopy",true);
	hardcopy.setFont(font);
	hardcopyFlag=1;  // default is true
	hardcopy.addActionListener(this);
	pagePanel.add(hardcopy);

	startPage=new JLabel("StartPage:",JLabel.RIGHT);
	pagePanel.add(startPage);
	startPage.setFont(font);
	startPgField=new JTextField("1");
	pagePanel.add(startPgField);
	startPgField.setFont(font);
	startPgField.setEditable(true);
	startPgField.setMaximumSize(fieldsize);
	startPgField.setPreferredSize(fieldsize);
	startPgField.addActionListener(this);
	endPage=new JLabel("EndPage:", JLabel.RIGHT);
	pagePanel.add(endPage);
	endPage.setFont(font);
	endPgField=new JTextField();
	pagePanel.add(endPgField);
	endPgField.setFont(font);
	endPgField.setEditable(true);
	endPgField.setMaximumSize(fieldsize);
	endPgField.setPreferredSize(fieldsize);
	endPgField.addActionListener(this);


	JPanel buttonPanel=new JPanel();
	buttonPanel.setLayout(new GridLayout(3,1));
	mainPanel.add(buttonPanel,BorderLayout.SOUTH);
	buttonPanel.add(new JLabel(""));
	buttonPanel.add(new JLabel(""));
	JPanel buttonContainer=new JPanel();
	buttonPanel.add(buttonContainer);
	buttonContainer.setLayout(new GridLayout(1,2));
	ok=new JButton("OK");
	buttonContainer.add(ok);
	ok.addActionListener(this);
	cancel=new JButton("Cancel");
	buttonContainer.add(cancel);
	cancel.addActionListener(this);


	Point loc=commBus.documentControl.getLocation();
	Dimension size=commBus.parent.getSize();
	int x=(int)(loc.x+(size.width/2))-(width/2), y=(int)((loc.y+(size.height/2))-(height-2));
	setSize(width,height);
	setLocation(x,y);
	setModal(false);
	show();
    }

    public void sendDocument (int startpage, int endpage) {
	StringBuffer url=new StringBuffer("http://"+commBus.getPiaHost()+":"+commBus.getPiaPort()+"/"+commBus.getPiaAgentName());
	HttpURLConnection httpConnect=null;
	boolean success=false;
	int thumbarStatus=commBus.getThumbarImageStatus();
	String conceptsStr=commBus.getActiveConcepts(), hliteStyleStr=new String(""+commBus.getCurrentHliteStyle()), 
	    activeSentencesStr=commBus.getActiveSentences();
	commBus.statusMsg2("Printing...");
	commBus.statusMsg1("Sending current document to print server...");
	
	if (commBus.checkThumbarLoading()) {
	    System.out.println(">>>>ActiveSentences:"+activeSentencesStr);
	    System.out.println(">>>>HlitleStyle:"+hliteStyleStr);
	    commBus.setWaitCursor();
	    byte[] buffer=commBus.getAnnotationBuffer();
	    if (buffer.length>0) {
		
		try {
		    URL piaURL=new URL(url.toString());
		    //connect.setAllowUserInteraction(true);
		    System.out.println("...protocol:"+piaURL.getProtocol());
		    System.out.println("...host:"+piaURL.getHost());
		    System.out.println("...port:"+piaURL.getPort());
		    System.out.println("...filename:"+piaURL.getFile());
		    System.out.println("...user:"+commBus.getUserAccountName());
		    System.out.println("...printer:"+commBus.getPiaPrinterName());
		    //System.out.println("...type:"+connect.getContentType());
		    System.out.println("...Creating streams...");
		    
		    try {
			httpConnect=(HttpURLConnection)piaURL.openConnection();
			//PUT /testest HTTP/1.1
			//Host: ds65.csie.ncu.edu.tw
			System.out.println("...connection created...bufLen="+buffer.length);
			httpConnect.setRequestMethod("POST");
			httpConnect.setRequestProperty("Content-Length",new String(buffer.length+""));
			httpConnect.setRequestProperty("Accept","*/*");
			httpConnect.setRequestProperty("Content-Type",RH_GlobalVars.rhContentHeaderName);
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeUser,commBus.getUserAccountName());
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypePrinter,commBus.getPiaPrinterName());
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeURL,documentURL);
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeTitle,documentTitle);
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeConcepts,conceptsStr);
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeHliteStyle,hliteStyleStr);
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeActiveSentences,activeSentencesStr);
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeStartPage,new String(""+startpage));
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeEndPage,new String(""+endpage));
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeCoverSheet,new String(""+coversheetFlag));
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeHardcopy,new String(""+hardcopyFlag));
			httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeBufferLen,new String(buffer.length+""));
			//httpConnect.setRequestProperty("Content-RH-Buffer",URLEncoder.encode(new String(strBuf.length()+"")));
			httpConnect.setDoOutput(true);
			httpConnect.connect();
			System.out.println("...connection instantiated...");
			//DataOutputStream output = new DataOutputStream(httpConnect.getOutputStream()); 
			//output.write(requestString.getBytes(),0,requestString.length());//buffer.length); 
			//output.flush();
			//output.close();
			//PrintWriter out=new PrintWriter(new OutputStreamWriter(output));
			BufferedOutputStream output = new BufferedOutputStream(httpConnect.getOutputStream());
			output.write(buffer,0,buffer.length);
			output.flush();
			output.close();
			//OutputStream output=httpConnect.getOutputStream();
			InputStream input=httpConnect.getInputStream();
			System.out.println("...response to creating input:"+httpConnect.getResponseMessage());
			//----------------
			//System.out.println("...response to writing output:"+httpConnect.getResponseMessage());
			input.close();
		    } catch (Exception e) {
			System.out.println("ERROR: "+ e);
		    }
		} catch (MalformedURLException ex) {
		    System.out.println("Bad URL:"+url.toString());
		}
	    }
	    else System.out.println("***Error: document buffer==0");
	    commBus.statusMsg1("");
	    commBus.statusMsg2("Done");
	    commBus.setDefaultCursor();
	}
	else commBus.statusMsg1("Please wait until Thumbar completes image rendering before printing");
    }

    public void actionPerformed(ActionEvent ev) {
	Object source = ev.getSource();
	if (source == ok) {
	    System.out.println("Found OK");
	    // Make changes
	    dispose();
	    int startpage=0, endpage=0;
	    boolean start=false, end=false;
	    try {
		startpage=Integer.parseInt(startPgField.getText());
		start=true;
	    } catch (NumberFormatException ex) { start=false; }
	    try{
		endpage=Integer.parseInt(endPgField.getText());
		end=true;
	    } catch (NumberFormatException ex) { end=false; }
	    if (start&&end) sendDocument(startpage,endpage);
	    else if (start&&!end) sendDocument(startpage,0);
	    else {
		new RH_PopupError(commBus.parent,"Unrecognizable page range -- no document printed");
	    }

	}
	
	else if (source == coverSheet) {
	    if (coversheetFlag==1) coversheetFlag=0;
	    else coversheetFlag=1;
	}
	else if (source == hardcopy) {
	    if (hardcopyFlag==1) hardcopyFlag=0;
	    else hardcopyFlag=1;
	}
	else if (source == cancel) {
	    System.out.println("Found Cancel");
	    // do not make changes
	    dispose();
	}
	else if (source == startPgField) {
	    System.out.println("Start page:"+startPgField.getText());
	}
	else if (source == endPgField) {
	    System.out.println("End page:"+endPgField.getText());
	}
	
    }


}
