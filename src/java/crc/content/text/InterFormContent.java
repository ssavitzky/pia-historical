// InterFormContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.content.text;
import crc.pia.Agent;

/**
 * content object for interforms.
 * same as ParsedContent in agent context
 */
public class InterFormContent extends ParsedContent{
  /**
   * interform has an agent context
   */
  Agent agent;

  /************************************************************
  ** agent access functions
  ************************************************************/

  Agent getAgent(){
    return agent;
  }
  void setAgent(Agent a){
    agent = a;
  }

}
