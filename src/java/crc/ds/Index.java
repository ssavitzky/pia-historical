// Index.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

import crc.ds.List; 
import crc.ds.Table;
import crc.sgml.SGML;
import crc.sgml.Util;
import crc.sgml.AttrWrap;

import java.util.Enumeration;



/** An index object is used for indexing SGML objects.
 *      foo.bar.1 should return the 1st token of the bar element of foo
 *       semantics depend upon SGML type
 *	 indexes behave somewhat like enumerations-- asking for the next
 *       element moves a pointer up one
 *       this class also implements the recursive lookup by delegating
 *       to each SGML object in secession
 *     Possible indices:
 *       'bar' is a simple attribute name  : isString
 *       '1'  is a numeric index           : isNumeric
 *       '1-5'  is a numeric range '1-' == '1-END'  :isRange
 *       '-tag-value' return all elements with the tag=value :isExpression
 *       '-attr-value' return all elements with the attr=value :isExpression
 */
public class Index {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** The actual items. */
  protected  List  items;
  protected int  currentItem = 0;
  String range[] = new String[2];
  

  /** The number of indexed items. */
  public int nItems() {
    return items == null? 0 : items.nItems();
  }

  /** The number of indexed items. */
  public int size() {
    return items == null? 0 : items.size();
  }

  /** Access an individual item */
  public Object at(int i) {
    if (i >= items.size()) { return null; }
    return items.at(i);
  }

  /** Replace an individual item <em>i</em> with value <em>v</em>. */
  public void at(int i, Object v) {
    items.at(i,v);
  }
    
  /************************************************************************
  ** Construction and copying:
  ************************************************************************/

  public Index( String path) {

    items = Util.split(path,'.');
    
  }

  public Index(List l) {
    items = l; // we don't manipulate items, so no need to copy
    
  }
  
    

  /************************************************************************
  ** Lookups: instance methods
  ************************************************************************/

public SGML lookup(SGML datum)
  {
    while(currentItem < size() && datum != null){
      datum = datum.attr(this);  //delegate to SGML
      next();  // shift counter up
      
    }
    return datum;
  }

public SGML lookup(Table datum)
  {
    SGML data = (SGML)datum.at((String)items.shift());
    return lookup(data);
  }
  
  /**  operations  to provide data to sgml attr */
 public String next() {
  if( currentItem<size()){
    currentItem++;
  } else {
    return null;
  }
  String item = (String)items.at(currentItem);
  return item;
 }

/**  add an entry to this table
 */
public void lookupSet(Table context, SGML value)
  {
    String s= (String)items.shift();
    if(items.isEmpty()){
      context.at(s,value);
      return;
    }
    
    SGML data = (SGML)context.at(s);
    if(data == null){
      //create SGML AttrTable to hold data structures
      data = new AttrWrap();   
      context.at(s,data);
    }
    lookupSet(data,value);
    
  }

  /**  recursive function for setting path to value for SGML objects
        semantics of set, attr(name,value), depend on object
      same  inner loop as lookup except that missing objects get created
	*/

public void lookupSet(SGML context,SGML value)
  {
    SGML mycontext = context;
    SGML context1 = context;
    
    while(currentItem < size()){
      mycontext=context1;
      // now move down to next level
      context1 = mycontext.attr(this);  //delegate to SGML
      next();  // shift counter up
      //check for emptiness
      if(context1==null && currentItem < size()){
	String s  =  string();
	context1= new AttrWrap();   
        mycontext.attr(s,context1);
      }
    }
    // at this point, my context should be the last SGML object
    // specified by my path -- add  value to it under last name
    if(currentItem != size() - 1){
      //problem -- indexing off
      //quick fix
      currentItem=size()-1;
    }
    mycontext.attr(string(),value);
  }
  
    

  /************************************************************************
  ** Tests and accesses: what type of indexing
  ************************************************************************/

public boolean isNumeric()
  {
    return numeric() >= 0;
  }
  
  
/** return just the current item as string
 */
 public String string()
  {
    return (String)items.at(currentItem);
  }

/**  return the entire path as string
 */
public String toString()
  {
    Enumeration e  = items.elements();
    String result=(String)e.nextElement();
    while(e.hasMoreElements()){
      result+="." + (String)e.nextElement();
    }
    return result;
  }
  
    

 public int numeric()
  {
    String s = (String)items.at(currentItem);
    try {
      return java.lang.Integer.valueOf(s).intValue();
    } catch (Exception e) {
      return -1;  //not legal index
    }
  }
  
 public boolean isRange() {
   String s=string();
   return !s.startsWith("-") && s.indexOf('-') > 0;

 }
  
 public int[] range(int max) {
   String s = string();
   
   List words  = Util.split(s,'-');
   int size=words.nItems();
   if(s.endsWith("-")) size++;
   

   int result[]= new int[size];
   int last=0;
   
   for(int i=0;i<words.nItems();i++){
     try{
      last=java.lang.Integer.valueOf((String)words.at(i)).intValue();
      last=(last>max) ? max : last;
      
      } catch (Exception e) {
      //use previous value  
    }
     result[i]=last;
   }
   if(s.endsWith("-") &&size>1) result[size-1]=max;
   return result;
 }

public boolean isExpression()
  {
    return string().startsWith("-");
  }
  
/** return a list of keys, values -- semantics depend upon SGML object
  -tag-li   should return all list items from a list
  -attr-href  should return all items with an href attribute
  -size   should return number of elements in object
  -columns  should return number of columns in a table, etc
 */
public List expression()
  {
    return Util.split(string(),'-');    
  }
  
  
  /************************************************************************
  ** Lookups class methods
  ************************************************************************/

 public static SGML get(String path, SGML data) {
   if(path == null){
     return data;
   }
   if(path.indexOf('.')>0 ||  path.indexOf('-')>=0){
     Index i = new Index(path);
     return i.lookup(data);
   }
   
   return data.attr(path); //simple string lookup  
 }


 public static SGML get(String path, Table data) {
   if(path == null){
     return null;
   }    
   if(path.indexOf('.')>0 ||  path.indexOf('-')>=0){
     Index i = new Index(path);
     return i.lookup(data);
   }
   return (SGML)data.at(path);
   
 }

 public static void set(Table context, String path, SGML data) {
   if(path == null || context == null){
     return;
   }
   if(path.indexOf('.')>0 ||  path.indexOf('-')>=0){
     Index i = new Index(path);
     i.lookupSet(context,data);
     return;
   }
   context.at(path,data); //simple string lookup  
 }
  

}


