////// BasicProcessor.java: Document Processor basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A minimal implementation for a document Processor. <p>
 *
 *	BasicProcessor extends ParseStack, which makes it somewhat
 *	more efficient to maintain the corresponding information.  It
 *	implements the Input interface but not InputStack; if it did,
 *	there would be conflicts between the Input stack that it
 *	<em>uses</em> and any that it might be linked <em>onto</em> as
 *	part of a chain of processors. <p>
 *
 * === NOTE: Both Parser and Processor need DTD and parse stack info. ===
 * === it's up to the Parser to associate Handler, etc. with Token. ===
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 * @see crc.dps.Input */

public class BasicProcessor extends ParseStack implements Processor {

  /************************************************************************
  ** Generating Output:
  ************************************************************************/

  protected boolean running;

  public boolean isRunning() { return running; }

  /** The Processor's main loop.  <p>
   *
   *	Token's are obtained from the input and the appropriate
   *	handler called.  The Parse stack is adjusted as needed.  Processing
   *	continues until one or more output Tokens are generated. <p>
   *
   *	If the Processor <code>isRunning</code>, the output is sent to
   *	the current Output.  Otherwise it just waits until the
   *	<code>nextToken</code> is requested.
   */
  protected void process() {
				// ===
  }

  /************************************************************************
  ** Pushing Output from the Processor:
  ************************************************************************/

  protected Output output;

  /** Registers an Output object for the Processor.  The Processor 
   *	will call the Output's <code>nextToken</code> method with
   *	each Token as it becomes available, and finally call the 
   *	Output's <code>endOutput</code> function.
   */
  public void setOutput(Output anOutput) { output = anOutput; }

  /** Run the Processor, pushing a stream of Token objects at its
   *	registered Output, until the Output's <code>nextToken</code>
   *	method returns <code>false</code>.
   */
  public void run() {
				// ===
  }


  /************************************************************************
  ** Context Operations:
  ************************************************************************/

  /** Obtain the Handler for a given tag. */
  public Handler getHandlerForTag(String tag) {
    return null;		// ===
  }

  /** Obtain the Handler for a given Node. */
  public Handler getHandlerForNode(Node aNode) {
    return null;		// ===
  }

  /** Obtain the value associated with a given entity. */
  public NodeList getEntityValue(String name) {
    return null;		// ===
  }


  /************************************************************************
  ** Input Stack Operations:
  ************************************************************************/

  protected InputStack inputStack;

  /** Return the input stack */
  public InputStack getInputStack() { return inputStack; }

  /** Push an Input onto the input stack. */
  public void pushInput(Input anInput) { 
    inputStack = inputStack.pushInput(anInput);
  }

  /** Push an InputStackFrame (specialized Input) onto the stack. */
  public void pushFrame(InputStackFrame aFrame) {
    inputStack = inputStack.pushInput(aFrame);
  }

  /** Push a Token onto the input stack.
   *	This is a convenience function, included in the Processor interface 
   *	mainly for increased efficiency.
   */
  public void pushInput(Token aToken) {
    inputStack = new crc.dps.input.SingleToken(aToken, inputStack);
  }

  /** Push a Token onto the input stack to be expanded as a start tag,
   *	content, and end tag. 
   */
  public void pushInto(Token aToken){
    inputStack = new crc.dps.input.ExpandToken(aToken, inputStack);
  }

  /** Push a Node onto the input stack.
   *	This is a convenience function, included in the Processor
   *	interface mainly for increased efficiency.  The Node is
   *	converted to a Token using the Processor's current Tagset. <p>
   *
   *	Converting Node's to Token's requires a Tagset, so the Input
   *	we use will actually be an implementation of Parser.  They will
   *	probably still be in <code>crc.dps.input</code>, though, for
   *	consistancy.
   */
  public void pushInput(Node aNode){
				// ===
  }

  /** Push a Node onto the input stack to be expanded as a start tag,
   *	content, and end tag.  Token conversion is done using the 
   *	Processor's current Tagset.
   */
  public void pushInto(Node aNode){
				// ===
  }

  /** Push a NodeList onto the input stack.
   *	This is a convenience function, included in the Processor
   *	interface mainly for increased efficiency.  The Nodes are
   *	converted to Tokens using the Processor's current Tagset.
   */
  public void pushInput(NodeList aNodeList){
				// ===
  }


  /************************************************************************
  ** Parse Stack Operations:
  ************************************************************************/

  /** Push a Node onto the parse stack. */
  public void pushNode(Node aNode){
				// ===
  }

  /** Push a Token onto the parse stack. */
  public void pushToken(Token aToken){
				// ===
  }


  /************************************************************************
  ** Input Operations:
  ************************************************************************/

    /** Returns the next Token from this source and advances to the
   *	next.  This is just the typed version of the Enumeration
   *	operation <code>nextElement</code>, except that it returns
   *	<code>null</code> at the end of the input rather than throwing
   *	NoSuchElementException.  <p>
   *
   * @return next Token, 
   *	or <code>null</code> if and only if no more tokens are available.
   */
  public Token nextToken() {
    return null;		// ===
  }

  /** Returns true if it is known that no more Tokens are available.
   * 	Note that in some cases <code>nextToken</code> will return 
   *	<code>null</code> even after <code>atEnd</code> has returned 
   *	<code>false</code> This may happen if, for example, the end of
   *	a token can be determined without asking the input stream for
   *	the next character, or if the remainder of the input stream is
   *	ignorable.
   */
  public boolean atEnd() {
    return false;		// ===
  }

  /************************************************************************
  ** Enumeration Operations:
  ************************************************************************/

  public Object nextElement() throws NoSuchElementException {
    Object o = nextToken();
    if (o == null) throw new NoSuchElementException();
    else return o;
  }

  /** Are there more elements waiting to be returned?  
   *	Note that some subclasses of AbstractInput may have to ``look
   *	ahead'' to ensure that <code>hasMoreElements</code> can return
   *	an accurate result.  This implementation assumes that
   *	<code>atEnd</code> is accurate.
   */
  public boolean hasMoreElements() { return ! atEnd(); }


  /************************************************************************
  ** Operations Used by Handlers:
  ************************************************************************/


}
