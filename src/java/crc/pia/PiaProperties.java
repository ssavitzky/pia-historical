// PiaProperties.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;

import crc.pia.Pia;
import java.util.* ;

public class PiaProperties extends Properties {

    /**
     * Build up the name of a property, specific to the given pia agency.
     * @return The full name of the property relevant to this agency.
     */

    public String getPropertyName(Pia agency, String name) {
	return agency.getIdentifier() + "." + name;
    }

   /**
    * Build up the name of a property, specific to the given id string.
    * @return The full name of the property.
    */

    public String getPropertyName(String id, String name) {
	return id+"."+name;
    }

    protected String getPropString(String name, String def) {
	String v = getProperty (name, null);
	if ( v != null )
	    return v ;
	return def ;
    }

    /**
     * Get this property value, as a String.
     * @param Pia The agency from which you want to retrieve a property 
     * @param name The name of the property to be fetched.
     * @param def The default value, if the property isn't defined.
     * @return An instance of String.
     */

    public String getString(Pia agency
			    , String name
			    , String def) {
	String n = getPropertyName(agency, name);
	return getPropString(n,def);
    }

    public String getString(String id, String name, String def) {
	String n = getPropertyName(id, name);
	return getPropString(n,def);
    }

    public PiaProperties (Properties props) {
	super (props) ;
    }
   
}

 






