////// Test.java: Utilities for testing nodes and strings
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.util;

import crc.dom.NodeList;
import crc.dom.NodeEnumerator;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.output.*;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

/**
 * Utilities to determine the status (properties) of resources. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class Status {

  /************************************************************************
  ** Construct nodelist for return:
  ************************************************************************/

  protected static NodeList nodes(String v) {
    if (v == null) return null;
    return new ParseNodeList(new ParseTreeText(v));
  }

  protected static NodeList nodes(long v) {
    return new ParseNodeList(new ParseTreeText("" + v));
  }

  protected static NodeList nodes(boolean v) {
    return v? nodes("true") : null;
  }

  protected static NodeList nodes(String v[]) {
    ParseNodeList nl = new ParseNodeList();
    for (int i = 0; i < v.length; ++i) 
      nl.append(new ParseTreeText(v[i]));
    return nl;
  }

  /************************************************************************
  ** Status of Files:
  ************************************************************************/

  /** Get the value of a named status item for a file.
   *	Returns all values if the name is null.
   */
  public static NodeList getStatusItem(File res, String name) {
    if (res == null) return null;
    if (name == null) return getStatusItems(res);
    name = name.toLowerCase();

    if (name.equals("local")) return nodes(true);

    if (name.equals("path")) return nodes(res.getPath()); 
    if (name.equals("name")) return nodes(res.getName()); 
    if (name.equals("absolute-path")) return nodes(res.getAbsolutePath());
    //if (name.equals("cannonical-path")) return nodes(res.getCanonicalPath());
    if (name.equals("parent")) return nodes(res.getParent());
    if (name.equals("exists")) return nodes(res.exists());
    if (name.equals("writeable")) return nodes(res.canWrite());
    if (name.equals("readable")) return nodes(res.canRead());
    if (name.equals("file")) return nodes(res.isFile());
    if (name.equals("directory")) return nodes(res.isDirectory());
    if (name.equals("last-modified")) return nodes(res.lastModified());
    if (name.equals("length")) return nodes(res.length());
    if (name.equals("files"))
      return res.isDirectory()? nodes(res.list()) : null;

    return null;
  }

  /** Return an attribute list containing all non-null status items
   *	for a File. 
   */
  public static ActiveAttrList getStatusItems(File res) {
    return getStatusItems(res, fileItems);
  }

  /** Return an attribute list containing the specified status items
   *	for a File.
   */
  public static ActiveAttrList getStatusItems(File res, String items[]) {
    ParseTreeAttrs list = new ParseTreeAttrs();
    for (int i = 0; i < items.length; ++i) {
      NodeList v = getStatusItem(res, items[i]);
      if (v != null) list.setAttributeValue(items[i], v);
    }
    return list;
  }

  public static String fileItems[] = {
    "local", "name", "path", "absolute-path", "canonical-path", "parent", 
    "exists", "writeable", "readable", "file", "directory", "last-modified",
    "length", "files",
  };

  /************************************************************************
  ** Status of URL's:
  ************************************************************************/

  /** Get the value of a named status item for a URL. */
  public static NodeList getStatusItem(URL res, String name) {
    if (res == null) return null;
    if (name == null) return getStatusItems(res);
    name = name.toLowerCase();

    if (name.equals("url")) return nodes(res.toString());
    if (name.equals("path")) return nodes(res.getFile()); 
    if (name.equals("protocol")) return nodes(res.getProtocol());
    if (name.equals("port")) return nodes(res.getPort());
    if (name.equals("reference")) return nodes(res.getRef());
    //if (name.equals("query")) return nodes(res.getQuery());

    if (name.equals("remote")) return nodes(true);

    return null;
  }

  public static ActiveAttrList getStatusItems(URL res) {
    return getStatusItems(res, urlItems);
  }

  public static ActiveAttrList getStatusItems(URL res, String items[]) {
    ParseTreeAttrs list = new ParseTreeAttrs();
    for (int i = 0; i < items.length; ++i) 
      list.setAttributeValue(items[i], getStatusItem(res, items[i]));
    return list;
  }

  public static String urlItems[] = {
    "remote", "url", "protocol", "host", "port", "path", "query",
    "reference",    
  };


  /************************************************************************
  ** Status of Entities:
  ************************************************************************/

  /** Get the value of a named status item for an Entity. */
  public static NodeList getStatusItem(ActiveEntity res, String name) {
    if (res == null) return null;
    if (name == null) return getStatusItems(res);
    name = name.toLowerCase();

    if (name.equals("entity")) return nodes(true);
    if (name.equals("name")) return nodes(res.getName());

    ParseTreeExternal ext = (res instanceof ParseTreeExternal) 
      ? (ParseTreeExternal) res : null;

    if (ext != null) {
      if (name.equals("system")) return nodes(ext.getResourceName());
      if (ext.resourceURL != null) {
	if (name.equals("url"))	return nodes(ext.resourceURL.toString());
      }
      if (ext.resourceConnection != null) {
	URLConnection rc = ext.resourceConnection;
	if (name.equals("content-type")) return nodes(rc.getContentType());
	if (name.equals("last-modified")) return nodes(rc.getLastModified());
	if (rc instanceof HttpURLConnection) try {
	  HttpURLConnection hc = (HttpURLConnection) rc;
	  if (name.equals("method")) return nodes(hc.getRequestMethod());
	  if (name.equals("code")) return nodes(hc.getResponseCode());
	  if (name.equals("message")) return nodes(hc.getResponseMessage());
	} catch (IOException ex) {}
      } 
      if (ext.resourceFile != null) {
	if (name.equals("file")) return nodes(ext.resourceFile.getPath());
	if (name.equals("last-modified"))
	  return nodes(ext.resourceFile.lastModified());
      }
    }
    return null;
  }

  public static ActiveAttrList getStatusItems(ActiveEntity res) {
    return getStatusItems(res, entityItems);
  }

  public static ActiveAttrList getStatusItems(ActiveEntity res,
					      String items[]) {
    ParseTreeAttrs list = new ParseTreeAttrs();
    for (int i = 0; i < items.length; ++i) {
      NodeList v = getStatusItem(res, items[i]);
      if (v != null) list.setAttributeValue(items[i], v);
    }
    return list;
  }

  public static String entityItems[] = {
    "name", "system", "url", "code", "message", "method", 
    "file", "last-modified", "content-type"
  };
}
