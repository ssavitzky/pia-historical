////// Input.java: Input stack frames
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

/**
 * Interface for Input stack frames.
 *	The input stack may contain either "raw" tokens (the easy case),
 *	tokens or token lists being <em>expanded</em>, or <em>streams</em>
 *	of tokens (represented by functor objects).  The primary example
 *	of the latter is the HTML tokenizer.
 */
public abstract class Input {
  /** Link to the previous Input in the stack */
  public Input prev;

  /** Return the next item in this frame and advances to the next. 
   *	nextInput() should never return null.
   */
  public abstract SGML nextInput();

  /** Return true if there are no more items.
   * 	Called <em>after</em> nextInput(), so a single token may return 
   *	itself.  This makes the Input stack inherently tail-recursive:
   *	a frame that returns its last item will be popped before anything
   *	that item calls is pushed.
   */
  public boolean endInput() {
    return true;
  }

  Input() {
  }

  Input(Input previous) {
    prev = previous;
  }

  /** Control interface: ignore tags, and optionally entities as well,
   *	up to the given string (which is given literally).  Most Input
   *	subclasses ignore this; only Parser really has to look.
   */
  public void ignoreMarkup(String endString, boolean ignoreEntities) {
    this.endString = endString;
    this.ignoreEntities = ignoreEntities;
  }

  /** Ignore markup until this string is found in the input: */
  String endString;

  /** Ignore entities (pass them as part of the text) if true. */
  boolean ignoreEntities;
}
