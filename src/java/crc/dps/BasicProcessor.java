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
 * ===	In order to generate a parse tree, we need to start with 
 *	<code>node</code> being the Document.
 *
 * ===	The bottom thing on the input stack needs to be a guard that makes
 *	sure that all unmatched start tags get ended.
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
  ** Main Loop:
  ************************************************************************/

  /** The Processor's main loop.  <p>
   *
   *	Token's are obtained from the input and the appropriate
   *	handler called.  The Parse stack is adjusted as needed.  Processing
   *	continues until one or more output Tokens are generated. <p>
   *
   *	If the Processor <code>isRunning</code>, the output is sent to
   *	the current Output.  Otherwise it just waits in the
   *	<code>outputQueue</code> until the <code>nextToken</code> is
   *	requested.
   */
  protected void process() {
    Handler handler;
    // Loop as long as there's input.
    if (token == null) token = nextInput();
    anyResults = false;
    while (token != null) {
      if (token.isStartTag()) {			// Start tag
	Token t = token;
	processStart();
	// Empty element: pop the frame and treat as an end tag.
	if (t.isEmptyElement()) {
	  token = t; 
	  processEnd();
	}
      } else if (token.isEndTag()) {		// End tag
	processEnd();
      } else /* token.isNode() */ {		// Complete node
	processNode();
      }

      if (anyResults && !running) return;
      if (token == null) token = nextInput();
    }
  }

  /************************************************************************
  ** Result Processing:
  ************************************************************************/

  protected boolean anyResults;

  public Token result(Node aNode) {
    debug((parsing && node == null)? " dropped parse " : "ok");
    if (aNode == null) return null;
    if (parsing) appendNode(aNode);
    if (passing) passOutput(new BasicToken(aNode));

    anyResults = true;
    return null;
  }

  public Token result(Node aNode, Token aToken) {
    debug((parsing && node == null)? " dropped parse " : "ok");
    if (parsing && aNode != null) appendNode(aNode);
    if (passing && aToken != null) passOutput(aToken);

    anyResults = true;
    return null;
  }

  public Token results(NodeList aNodeList) {
    if (aNodeList == null) return null;
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      result(node);
    }
    return null;
  }

  /************************************************************************
  ** Processing:
  ************************************************************************/

  /** The processing associated with a start tag. */
  protected final void processStart() {
    Handler handler;
    debug("<" + token.getTagName() + ">", depth);
    /* Here's where it gets tricky.
     *	We want to end up with parseStack.token being the current
     *	token (which we will pop when we see the end tag).  But
     *	we have to save all the other variables before calling the
     *	handler.
     */
    pushToken(token, token.getTagName());
    if (expanding) {
      handler = token.getHandler();
      if (handler != null) {
	node = handler.startAction(token, this);
	debug(" handled ");
      } else if (parsing) {
	node = token;
	token.setNode();
	debug(" parsing "); }
      else node = null;
    } else if (parsing) { 
      node = token; 
      token.setNode();
    } else {
      node = null;
    }
    if (passing && !token.isEmptyElement()) passOutput(token);
    debug("\n");
    token = null;
  }

  /** The processing associated with an end tag or empty element */
  protected final void processEnd() {
    debug("</" + token.getTagName()
	  + (token.implicitEnd()? " i" : "") +">", depth-1);
    // remember whether we were passing the contents ,
    // and the new Node we constructed.
    boolean werePassing = passing;
    Token oldToken = token;
    Node newNode = node;
    Handler handler;
    // then pop the parse stack, call the handler, and append the node.
    popParseStack();
    // the handler we want is the one from the start tag on the stack.
    // It should still be there in <code>token</code>.
    if (!token.isEmptyElement()) {
      oldToken.setEndTag();
      oldToken.setTagName(token.getTagName());
    }
    if (oldToken.implicitEnd()) {
      token.setImplicitEnd(true);
    }
    if (parsing && newNode == null) { debug(" null result "); }
    if (expanding) {
      handler = token.getHandler();
      if (handler != null) {
	token = handler.endAction(token, this, newNode);
	debug(" handled ");
      } else {
	token = token.isEmptyElement()? result(newNode)
	  			      : result(newNode, oldToken);
	debug(newNode == null? " vanished " : parsing? " parsed " : " passed ");
      }
    } else {
      token = token.isEmptyElement()? result(newNode)
	  			    : result(newNode, oldToken);
    }
    debug("\n");
  }

  /** The processing associated with a complete Node. */
  protected final void processNode() {
    Handler handler;
    debug(token.isElement() ? "<" + token.getTagName() + "/>\n"
	  : "[" + token.getNodeType() + "]\n", depth-1);
    // === We would like nodeAction on a parsed subtree to do an eval.
    // === We require eval to be equivalent to the blind expansion...
    if (expanding) {
      handler = token.getHandler();
      if (handler != null) token = handler.nodeAction(token, this);
      else token = result(token.createNode(getHandlers()));
    } else if (parsing) {
      if (node != null) token.copyTokenUnder(node);
      token = null;
    } else {
      token = result(token);
    }
  }

  /************************************************************************
  ** Generating Output:
  ************************************************************************/

  protected boolean running;

  public boolean isRunning() { return running; }

  /** The output queue. Used as a buffer in case we generate more than
   *	one token in one step.
   */
  protected BasicTokenList outputQueue = new BasicTokenList();

  /************************************************************************
  ** Pushing Output from the Processor:
  ************************************************************************/

  protected Output output = null;

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
    running = true;
    process();
    if (running && output != null) { output.endOutput(); }
  }

  /** Pass a Token to the output. 
   *	If anything is in the <code>outputQueue</code>, it is put out first.
   */
  protected void passOutput(Token aToken) {
    if (! running || output == null) {
      outputQueue.append(aToken);
    } else if (outputQueue.getLength() > 0) {
      outputQueue.append(aToken);
      passOutputQueue();
    } else {
      running = output.nextToken(aToken);
    }
  }

  /** Pass several Tokens to the output. */
  protected void passOutput(TokenList someTokens) {
    long length = someTokens.getLength();
    for (long i = 0; i < length; ++i) {
      passOutput(someTokens.tokenAt(i));
    }
  }

  /** Pass the output queue to the output. */
  protected void passOutputQueue() {
    // Correctly, though inefficiently, handles the case where the
    // Output returns <code>false</code> in the middle of the queue.
    TokenList queue = outputQueue;
    outputQueue = new BasicTokenList();
    passOutput(queue);
  }

  /************************************************************************
  ** Input Stack Operations:
  ************************************************************************/

  protected InputStack inputStack = new crc.dps.input.Guard(this, 0);

  /** Return the input stack */
  public InputStack getInputStack() { return inputStack; }

  /** Get a Token from the InputStack.
   *	If the top Input on the stack returns null, pop it.
   *<p>
   *	We don't worry about keeping start and end tags balanced, since
   *	most Input's don't need it.  When pushing a Parser which may need
   *	its own environment, we make an empty parse stack frame and push
   *	an anonymous end tag before pushing the Parser.
   */
  public Token nextInput() {
    if (inputStack == null) return null;
    Token t;
    for (t = inputStack.nextToken();
	 t == null;
	 t = inputStack.nextToken()) {
      inputStack = inputStack.popInput();
      if (inputStack == null) return null;
    }
    return t;
  }

  /** Push an Input onto the input stack. */
  public void pushInput(Input anInput) { 
    inputStack = anInput.pushOnto(inputStack);
  }

  /** Push a ProcessorInput (specialized Input) onto the stack. */
  public void pushProcessorInput(ProcessorInput anInput) {
    inputStack = anInput.pushOnto(inputStack);
    anInput.setProcessor(this);
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
  ** Input Operations:
  ************************************************************************/

  /** Returns the next Token from this source and advances to the
   *	next.
   *
   * @return next Token, 
   *	or <code>null</code> if and only if no more tokens are available.
   */
  public Token nextToken() {
    return null;		// ===
  }

  /** Returns true if it is known that no more Tokens are available.
   *
   */
  public boolean atEnd() {
    return false;		// ===
  }

  /** set the next-frame pointer and return <code>this</code> */
  public InputStack pushOnto(InputStack anInputStack) {
    return anInputStack.pushInput(this);
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
  ** Debugging:
  **	This is a subset of crc.util.Report.
  ************************************************************************/

  protected int verbosity = 0;

  public int getVerbosity() { return verbosity; }
  public void setVerbosity(int value) { verbosity = value; }

  public void debug(String message) {
    if (verbosity >= 2) System.err.print(message);
  }

  public void debug(String message, int indent) {
    if (verbosity < 2) return;
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    s += message;
    System.err.print(s);
  }

  public void setDebug() 	{ verbosity = 2; }
  public void setVerbose() 	{ verbosity = 1; }
  public void setNormal() 	{ verbosity = 0; }
  public void setQuiet() 	{ verbosity = -1; }

}
