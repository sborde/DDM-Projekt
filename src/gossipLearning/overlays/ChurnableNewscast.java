package gossipLearning.overlays;

import gossipLearning.controls.ChurnControl;
import gossipLearning.interfaces.Churnable;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.extras.mj.ednewscast.CycleMessage;
import peersim.extras.mj.ednewscast.EdNewscast;

/**
 * This is a simple extension of the EDProtocol-based Newscast
 * which supports the modeling of churn.
 *
 * @author Róbert Ormándi
 *
 */
public class ChurnableNewscast extends EdNewscast implements Churnable {
  protected int cacheSize;
  private long sessionLength = ChurnControl.INIT_SESSION_LENGTH;
  /** @hidden */
  private final String prefix;
  
  public ChurnableNewscast(String prefix) {
    super(prefix);
    cacheSize = Configuration.getInt(prefix + ".cache");
    this.prefix = prefix;
  }
  
  public ChurnableNewscast clone() {
    return new ChurnableNewscast(prefix);
  }

  public long getSessionLength() {
    return sessionLength;
  }

  public void setSessionLength(long sessionLength) {
    this.sessionLength = sessionLength;
  }

  public void initSession(Node node, int protocol) {
    deleteNeighbors();
    while (degree() < cacheSize) {
      int onlineNeighbor = CommonState.r.nextInt(Network.size());
      if ( Network.get(onlineNeighbor).getFailState() != Fallible.DOWN
          && Network.get(onlineNeighbor).getFailState() != Fallible.DEAD
          && Network.get(onlineNeighbor).getID() != node.getID()) {
        //System.out.println(currentNode.getID() + " addNeighbor who is UP=" + onlineNeighbor + ", state=" + ((Network.get(onlineNeighbor).getFailState() == Fallible.OK) ? "UP" : "DOWN"));
        addNeighbor(Network.get(onlineNeighbor));
      }
    }
    EDSimulator.add(0, CycleMessage.inst, node, protocol);
  }
}
