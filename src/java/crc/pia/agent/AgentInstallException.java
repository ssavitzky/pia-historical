// AgentInstallException.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia.agent;

/** The only use of this appears to be when the agent name is missing */
public class AgentInstallException extends Exception {
    
    public AgentInstallException(String msg) {
	super(msg);
    }
}
