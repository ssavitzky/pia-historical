/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 02.24.98
 *
 */
package ricoh.rh;

import javax.swing.tree.*;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.*;

public class RH_TreeCellRenderer implements TreeCellRenderer {
  private JLabel label;
  private String expandedName="/cm8.gif", leafName="/cm5.gif", otherName="/cm0.gif", gifpath="";
  private String nodeFilename="cm", imageFileExt=".gif", leafFilename="/cml", offFilename="cmoff", collapsedFilename="/cmc";
    //Dimitri: added
  RH_CommBus commBus;  

  RH_TreeCellRenderer (String path, RH_CommBus bus) {
    commBus = bus;
    gifpath=path;
    System.out.println("Tree Node:"+(gifpath+expandedName));
    label=new JLabel("foo",new ImageIcon(gifpath+expandedName),JLabel.LEFT);
  }

  public Component getTreeCellRendererComponent(JTree tree,
						Object value,
						boolean selected,
						boolean expanded,
						boolean leaf,
						int row,
						boolean hasFocus)  {
    DefaultMutableTreeNode name = (DefaultMutableTreeNode) value;
    //System.out.println("**TreeValue:"+name+" row:"+row);
    //*** Do search here for concept using shortname as string:
    RH_Concept concept=commBus.mainFrame.findConcept(name.toString());
    label.setText(name.toString()+" ("+concept.getValue()+"%)");    
    int score=5;
    if (expanded) {
      label.setIcon(new ImageIcon(gifpath+nodeFilename+score+imageFileExt));
    }
    else {
      if (leaf) {
	label.setIcon(new ImageIcon(gifpath+leafFilename+score+imageFileExt));
      }
      else {
	label.setIcon(new ImageIcon(gifpath+collapsedFilename+score+imageFileExt));
      }
    }

    return label;
  }
}


