// ObservableProperties.java
// $Id$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package crc.pia;

import java.util.*;
import java.io.File;
import crc.ds.Tabular;

/**
 * This class extends the basic properties class of Java, by providing
 * more type conversion.
 */

public class Piaproperties extends Properties implements Tabular {

  /** Put a value for a name.  (From Tabular interface).
   * @param name The name of the property to assign.
   * @param value The new value for this property, or <strong>null</strong>
   *    if the property setting is to be cancelled.
   * @see crc.ds.Tabular
   */
  public synchronized void put(String name, Object value) {
    if (value == null) super.remove(name);
    else super.put(name, value);
  }

  /** Get value associated with a name.  (From Tabular interface).
   * @param name The name of the property to assign.
   * @see crc.ds.Tabular
   */
  public synchronized Object get(String name) {
    return getProperty(name, null);
  }

    /**
     * Assign a value to a property. 
     * @param name The name of the property to assign.
     * @param value The new value for this property, or <strong>null</strong>
     *    if the property setting is to be cancelled.
     * @return A boolean <strong>true</strong> if change was accepted by 
     *    our observers, <strong>false</strong> otherwise.
     */
    
  public synchronized boolean putValue (String name, String value) {
	// If null value, remove the prop definition:
	if ( value == null ) {
	    super.remove(name) ;
	    return true ;
	}
	// Otherwise, proceed:
	String old = (String) get (value) ;
	if ( (old == null) || (!  old.equals (value)) ) {
	    super.put (name, value) ;
	}
	return true ;
    }
    
    /**
     * Get this property value, as a boolean.
     * @param name The name of the property to be fetched.
     * @param def The default value, if the property isn't defined.
     * @return A Boolean instance.
     */

    public boolean getBoolean(String name, boolean def) {
	String v = getProperty(name, null);
	if ( v != null )
	    return "true".equalsIgnoreCase(v) ? true : false ;
	return def ;
    }

    /**
     * Get this property value, as a String.
     * @param name The name of the property to be fetched.
     * @param def The default value, if the property isn't defined.
     * @return An instance of String.
     */

    public String getString(String name, String def) {
	String v = getProperty (name, null);
	if ( v != null )
	    return v ;
	return def ;
    }

  // steve: removed getStringArray, getFile -- unused.

    /**
     * Get this property value, as an integer.
     * @param name The name of the property to be fetched.
     * @param def The default value, if the property isn't defined.
     * @return An integer value.
     */

    public int getInteger(String name, int def) {
	String v = getProperty (name, null);
	if ( v != null ) {
	    try {
		if (v.startsWith("0x")) {
		    return Integer.valueOf(v.substring(2), 16).intValue();
		}
		if (v.startsWith("#")) {
		    return Integer.valueOf(v.substring(1), 16).intValue();
		}
		return Integer.valueOf(v).intValue();
	    } catch (NumberFormatException e) {
	    }
	}
	return def ;
    }

    /**
     * Get this property value, as a double.
     * @param name The name of the property.
     * @param def The default value if undefined.
     * @return A double value.
     */

    public double getDouble(String name, double def) {
	String v = getProperty(name, null);
	if ( v != null ) {
	    try {
		return Double.valueOf(v).doubleValue();
	    } catch (NumberFormatException ex) {
	    }
	}
	return def;
    }

    /**
     * Set this property value, as a string
     * Return null (needs to return type Object to
     * be compatible with new version of Properties class
     * in JDK1.2beta4, previously this method was void)
     * @param name The name of the property to be defined.
     * @param def The value to define.
     */
    public Object setProperty(String name, String def) {
      put(name, def);
      return null;
    }

  
    /**
     * Set this property value, as a boolean.
     * @param name The name of the property to be defined.
     * @param def The value to define.
     */
    public void setBoolean(String name, boolean def) {
      setProperty(name, def? "true" : "false");
    }

    /**
     * Set this property value, as an integer.
     * @param name The name of the property to be defined.
     * @param def The value to define.
     */
    public void setInteger(String name, int def) {
      setProperty(name, "" + def);
    }

    /**
     * Build an Piaproperties instance from a Properties instance.
     * @param props The Properties instance.
     */

    public Piaproperties(Properties props) {
	super (props) ;
    }
   
}

 
