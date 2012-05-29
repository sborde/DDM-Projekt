package gossipLearning.interfaces;

import peersim.core.Node;


/**
 * Protocols implementing this interface can model churn i.e. logging in and out of nodes.
 * 
 * @author Róbert Ormándi
 */
public interface Churnable {
  /**
   * This method sets the <b>length of the online interval</b> of a node.
   * @param sessionLength
   */
  public void setSessionLength(long sessionLength);
  /**
   * This method returns the <b>length of the online interval</b> of a node.<br/>
   * If this value is decreased to 0, the node must log out. This behavior is granted by ChurnControl.
   * @see p2pChurn.controls.ChurnControl
   *   
   * @return the length of the online interval of the node
   */
  public long getSessionLength();
  /**
   * This method is called by control ChurnControl each time when a node logs in to its neighbor.
   * 
   * @param node Reference to the current node
   * @param protocol Protocol ID
   */
  public void initSession(Node node, int protocol);
}
