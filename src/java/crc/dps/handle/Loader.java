////// Loader.java: Handler loading and initialization utilities.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;

import crc.dps.Handler;
import crc.ds.Table;
import crc.util.NameUtils;

/** Loader for Handler classes. 
 *
 *	Note that because this class is in the <code>crc.dps.handle</code>
 *	package, it can load the non-public handler classes that are declared
 *	in the same files as top-level handlers, which typically handle
 *	elements specialized by one of their attributes.
 *
 */
public class Loader {


  /************************************************************************
  ** Handler cache:
  ************************************************************************/

  protected static Table handlerCache = new Table();

  /** Define a handler class.  
   *	This is available for use in handler implementations, for preloading
   *	the cache with subclasses and variants.  Such a handler should perform
   *	any additional initialization in its default constructor.
   */
  static void defHandle(String cname, AbstractHandler handler) {
    handlerCache.at(cname, handler);
  }

  static {
    defHandle("tagset", new tagsetHandler());
    defHandle("define", new defineHandler());
    /**/defHandle("action", new actionHandler());
    /**/defHandle("value", new valueHandler());
    defHandle("connect", new connectHandler());
    defHandle("expand", new expandHandler());
    defHandle("hide", new hideHandler());
    defHandle("if", new ifHandler());
    /**/defHandle("else", new elseHandler());
    /**/defHandle("elsf", new elsfHandler());
    /**/defHandle("then", new thenHandler());
    defHandle("get", new getHandler());
    defHandle("include", new includeHandler());
    defHandle("logical", new logicalHandler());
    defHandle("numeric", new numericHandler());
    defHandle("protect", new protectHandler());
    defHandle("repeat", new repeatHandler());
    defHandle("set", new setHandler());
    defHandle("subst", new substHandler());
    defHandle("tagset", new tagsetHandler());
    defHandle("test", new testHandler());
    defHandle("text", new textHandler());
  }

  /** Load an appropriate handler class and instantiate it. 
   */
  public static AbstractHandler loadHandler(String tag, String cname,
				    int syntax, boolean defaultOK) {
    if (cname == null) return new GenericHandler(syntax);

    String name = ("".equals(cname))
      ? NameUtils.javaName(tag, -1, -1, true, false)
      : cname;

    AbstractHandler h = (AbstractHandler) handlerCache.at(name);
    if (h != null && syntax != 0) h.setSyntaxCode(syntax);
    if (h != null) return h;

    h = (GenericHandler) loadHandler(name, syntax, false);
    if (h == null && defaultOK) {
      h = new GenericHandler();
    }
    if (h != null) handlerCache.at(name, h);
    return h;
  }

  /** Load an appropriate handler class and instantiate it. 
   */
  public static AbstractHandler loadHandler(String cname, int syntax,
					    boolean defaultOK) {
    if (cname == null) return new BasicHandler(syntax);

    AbstractHandler h = (AbstractHandler) handlerCache.at(cname);
    if (h != null && syntax != 0) h.setSyntaxCode(syntax);
    if (h != null) return h;

    Class c = NameUtils.loadClass(cname, "crc.dps.handle.");
    if (c == null) {
      c = NameUtils.loadClass(cname+"Handler", "crc.dps.handle.");
    }
    try {
      if (c != null) h = (BasicHandler)c.newInstance();
    } catch (Exception e) {}
    if (h == null && defaultOK) h = new BasicHandler(syntax);
    if (h != null) handlerCache.at(cname, h);
    return h;
  }

}
