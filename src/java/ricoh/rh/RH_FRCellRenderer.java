/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 5.13.98
 *
 */
package ricoh.rh;

import java.util.*;
import java.awt.*;
import javax.swing.*;

//** Comment this line out when using swing 1.1.x
//import javax.swing.plaf.basic.BasicListCellRenderer;
//** Comment this line out when using swing 1.0.x
import javax.swing.DefaultListCellRenderer;

class RH_FRCellRenderer extends 
    //** Comment this line out when using swing 1.0.x
    DefaultListCellRenderer {
    //** Comment this line out when using swing 1.1.x
    //BasicListCellRenderer {
    private RH_FurtherReading parent;
    private String iconname="fr", gifext=".gif", path="";
    private double similarityThresholdPercent=RH_GlobalVars.similarityThresholdPercent;
    private int v1=90, v2=80, v3=70, v4=60, v5=(int)(similarityThresholdPercent*100);
    private JLabel label;
    
    RH_FRCellRenderer(JList listBox, RH_FurtherReading fr, String gifpath) {
	path=gifpath;
	parent=fr;
	setFont(new Font("Sans Serif",Font.PLAIN,10));
	//values=new int[valdata.length];
	//System.arraycopy(valdata,0,values,0,valdata.length);
	//setText("");
	setIcon(new ImageIcon(path+"/"+iconname+6+gifext));
    }

    public Component getListCellRendererComponent(JList list,Object value, int modelIndex, boolean isSelected, boolean cellHasFocus) {
	//int index = ((Integer)value).intValue();
	//System.out.println("**Rendering Cell: modelIndex="+modelIndex+" value="+value);
	int val=parent.getListItemValue(modelIndex);
	String text=parent.getListItemTitle(modelIndex);

	int num=0;
	if (val>=v1) num=1;
	else if (val>=v2) num=2;
	else if (val>=v3) num=3;
	else if (val>=v4) num=4;
	else if (val>=v5) num=5;
	else num=6;
	setIcon(new ImageIcon(path+"/"+iconname+num+gifext));
	//System.out.println("USE ICON: val="+val+" path="+path+"/"+iconname+num+gifext);
	
	return super.getListCellRendererComponent(list, text, modelIndex, isSelected, cellHasFocus);
    }

}




