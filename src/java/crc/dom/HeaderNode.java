package crc.dom;

public class HeaderNode extends AbstractNode {
  public HeaderNode(){}
  public int getNodeType(){ return NodeType.ELEMENT; }

  protected long incCount(){ return count++; }
  protected long decCount(){ return count--; }
  protected long getCount(){ return count; }
  /**
   * how many items
   */
  private long count = 0;
}
