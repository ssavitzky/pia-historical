import java.util.*; 
import java.awt.*; 
import installshield.jshield.runtime.*; 

/** 
* This InfoPanel gets some information (in this case, a port number and a directory, which is 
* presumably used to initialize a web server application) from the 
* user. 
* 
* 
* Being a purely visual interface, this InfoPanel should not actually 
* do anything with the data. Instead, it saves it as a Properties instance 
* in the InstallContext, which is later retrieved by the PropertyAction 
* class file, which uses it to write out a property file containing this 
* information. 
* 
* This class is implemented in a couple of classes, 
* so you can figure out how to include multiple classes into the 
* installation. 
*/ 

public class PropertyPanel extends InfoPanel 

{ 

private TextField hUhome;
private TextField agentName;
private List agentList;

// Properties instance, stored in InstallContext, 
// and passed onto PropertyAction 

private Properties htvals = new Properties(); 

public PropertyPanel() 
{ 
  setLayout(new SimpleGridLayout()); 
  add(new Label("Agents's data directory: ")); 
  add(hUhome = new TextField("80")); 

  //which agent
  //add(new Label("Agent to install: ")); 
  //add(agentName = new TextField("80")); 

  add(new Label("Agent list: "));

  // read from an index file containing agents's names.

  agentList = new List();
  agentList.addItem("MB3");
  agentList.addItem("Photo");
  add( agentList );
  
} 

/** 
* This method is called anytime the user navigates into this panel. 
* Doesn’t have much to do, so just create an empty method. 
*/ 

public void enter(InstallContext ctx) 
{ } 

/** 
* This method is called anytime the user wants to go to the next 
* panel. This gives us a chance to check if the entries are correct, 
* and possibly update stored values into the InstallContext. 
* 
* Returning a true permits the next InfoPanel or Action to run, otherwise it 
* doesn't move ahead. 
*/ 

public boolean verify(InstallContext ctx) 
{ 
  if ( setit(hUhome, "uhome", ctx) &&
       setit(new TextField( agentList.getSelectedItem() ), "agentname", ctx) ) {
    // put the properties instance into 
    // the context 
    ctx.putData("example.PropertyPanel", htvals); 
    return true; 
  } 

return false; 

} 

private boolean setit(TextField tf, String name, InstallContext ctx) 
{ 
  String s = tf.getText(); 
  if ((s == null) || (s.length() == 0)) 
    { 
      ctx.error("HTTP settings", "Please set the "+name); 
      tf.requestFocus(); 
      return false; 
    } 
  htvals.put(name, s); 
  return true; 
} 

} 









