This documents how to use jshield.

1- Install jshield; after you do this visit the help subdirectory and view the help.html file, starting from
   "Using the InstallShield Java Edition Wizard" .  Also, "check out Extending Your Java Installation" .

2- Jshield is a framework from which you can extend its functionalities to do things like copy files or
   generate script.

   Basically, there are two classes that you can extend: InfoPanel and Action.
   You can create a panel with input GUIs to get information from a user and create an action to be
   executed when the panel is closed.  At which point the panel is shown to the user during your
   application installation is determine by its specification in the object.ini file.

   If you create a PortInfo Panel, then you would inherit it from InfoPanel and add to it any
   input fields you like.  Then in the object.ini, you add the following:

	[License]
	RootClass=installshield.jshield.panels.LicensePanel
	Configurator=installshield.jshield.panels.LicenseConfigurator

	[PortInfo Panel]
	RootClass=PropertyPanel
	Runtime=SimpleGridLayout

   This indicates that PortInfo is to appear after the License panel.  The step above also applies to an 
   action object.  In an action object, you can do things like generate scripts or copy files.
   
3- Imagine that you are trying to install the PIA for a particular agent, say MB3.  Thus,
   you might want to ask a user the following:
   
	a - where the agent data directory is
	b - which agent does the user want to install
	c - where the PIA directory going to be located

   So, in this directory I have created two files: PropertyPanel.java and PropertyAction.java.
   PropertyPanel get items "a" and "b" from the user, and PropertyAction does the following:
	
	d - generates a batchfile to run the PIA using jre.  The batch file goes to
	    the PIA's bin directory obtained in step "c" above.
	e - copy MB3's directory content to the directory obtained from "a".

4- After you compile PropertyPanel and PropertyAction, you move them to the classes subdirectory of where
   you install Jshield.  Then, ask Jshield to compile an generate a self-extracted,installation executable.

5- Then, you can execute the executable.