// ChildNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

/**
 * This list expands a children collection through the start node.
 */

public class ChildNodeList implements NodeList {

  public ChildNodeList(){
    header = new HeaderNode();
    header.setPrevious( null );
    header.setNext( null );
  }

  public ChildNodeList(AbstractNode newChild){
    header = new HeaderNode();

    newChild.setPrevious( null );
    newChild.setNext( null );

    header.setPrevious( newChild );
    header.setNext( newChild );

    header.incCount();
  }

  public NodeEnumerator getEnumerator(){ return new ChildNodeListEnumerator( this ); }

  public Node item(long index)
       throws NoSuchNodeException
  {
    long i = 0;
    AbstractNode n = header.getPrevious();

    if( index < header.getCount() - 1 && index >= 0 ){
      while( i != index ){
	n = n.getNext();
      }
      return n;
    }
    else{
      String err = ("Index out of bound.");
      throw new NoSuchNodeException(err);
    }
   
  }

    /**
     * walk till end of child
     */
  public long getLength(){ return header.getCount(); }


  protected void doInsertAtStart(AbstractNode newChild, AbstractNode refChild){
    if( newChild == null || refChild == null ) return;

    AbstractNode temp = refChild.getPrevious();

    newChild.setPrevious( temp );
    newChild.setNext( refChild );

    refChild.setPrevious( newChild );

    temp.setPrevious( newChild );
  }



  protected void doInsertBefore(AbstractNode newChild, AbstractNode refChild){
    if( newChild == null || refChild == null ) return;

    if( refChild == header.getPrevious() ){
      doInsertAtStart(newChild, refChild);      
      header.incCount();
      return;
    }

    AbstractNode temp = refChild.getPrevious();

    newChild.setPrevious( temp );
    newChild.setNext( refChild );

    refChild.setPrevious( newChild );

    temp.setNext( newChild );
    header.incCount();
  }

  protected void doInsertLast( AbstractNode newChild){
    if( header.getPrevious() == null && header.getNext() == null ){
      header.setPrevious( newChild );
      header.setNext( newChild );
      header.incCount();
      return;
    }
      
    AbstractNode last = header.getNext();
    
    newChild.setPrevious( last );
    newChild.setNext( last.getNext() );

    last.setNext( newChild );

    header.setNext( newChild );
    header.incCount();
  }


  protected Node doRemoveChild( AbstractNode p ){
    if( p == null ) return null;

    if( header.getPrevious() == header.getNext() ){ // only one child
      header.setPrevious( null );
      header.setNext( null );
    }else if( header.getNext() == p ){// remove the last child, should adjust header's next reference
      p.getPrevious().setNext( p.getNext() );
      header.setNext( p.getPrevious() ); 
    }else if( header.getPrevious() == p ){ // the first item, but more than one child
      p.getPrevious().setPrevious( p.getNext() );
      p.getNext().setPrevious( header );
    }else{
      p.getPrevious().setNext( p.getNext() );
      p.getNext().setPrevious( p.getPrevious() );
    }
    p.setPrevious( null );
    p.setNext( null );
    header.decCount();
    return p;

  }


  protected Node doReplaceChild(AbstractNode oldChild, AbstractNode newChild){
    if( oldChild == null || newChild == null ) return null;
    
    AbstractNode previous = oldChild.getPrevious();
    AbstractNode next = oldChild.getNext();
    
    newChild.setPrevious( previous );
    newChild.setNext( next );

    oldChild.setPrevious (null );
    oldChild.setNext ( null );

    // more than one item in the list, adjust header's previous reference
    if( header.getPrevious() != header.getNext() ){
      header.setPrevious( newChild );
    }else if ( oldChild == header.getPrevious() ){ // only one item
      header.setPrevious( newChild );
      header.setNext( newChild );
    }else if ( oldChild == header.getNext() ){ // replacing the last item
      header.setNext( newChild );
    }

    return oldChild;
  }

  /**
   * Header node where previous points to start and next points to last.
   * Make list circular
   */
  protected HeaderNode header;
}

class HeaderNode extends AbstractNode{
  HeaderNode(){}

  public int getNodeType(){ return NodeType.COMMENT; }
  protected long incCount(){ return count++; }
  protected long decCount(){ return count--; }
  protected long getCount(){ return count; }

  /**
   * how many items
   */
  private long count = 0;

  
}
