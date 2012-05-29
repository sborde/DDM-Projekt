package gossipLearning.controls.initializers;

import gossipLearning.DataBaseReader;
import gossipLearning.InstanceHolder;
import gossipLearning.controls.observers.PredictionObserver;
import gossipLearning.interfaces.LearningProtocol;

import java.io.File;
import java.util.Vector;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Protocol;

/**
 * This control reads the training and evaluation sets from files and stores them.
 * The format of the files should be the Joachims' file format. <br/>
 * Moreover, this control loads the training instances onto the nodes, and specifies the
 * evaluation set for the error observer. <br/>
 * The number of training instances per node can be parameterized, the default
 * value is 1.
 * @author Róbert Ormándi
 *
 * @navassoc - - - LearningProtocol
 */
public class InstanceLoader implements Control {
  private static final String PAR_PROT = "protocol";
  private static final String PAR_TFILE = "trainingFile";
  private static final String PAR_EFILE = "evaluationFile";
  private static final String PAR_SIZE = "samplesPerNode";
  
  protected final int pid;
  /** @hidden */
  protected final File tFile;
  protected Vector<PredictionObserver> observers;
  protected DataBaseReader reader;
  /** @hidden */
  protected final File eFile;
  protected final int samplesPerNode;
    
  public InstanceLoader(String prefix) {
    pid = Configuration.getPid(prefix + "." + PAR_PROT);
    tFile = new File(Configuration.getString(prefix + "." + PAR_TFILE));
    eFile = new File(Configuration.getString(prefix + "." + PAR_EFILE));
    samplesPerNode = Configuration.getInt(prefix + "." + PAR_SIZE, 1);
    observers = new Vector<PredictionObserver>();
  }
  
  public boolean execute(){
    try {
      // read instances
      reader = DataBaseReader.createDataBaseReader(tFile, eFile);
      
      // InstanceLoader initializes the evaluation set of prediction observer
      for (PredictionObserver observer : observers) {
        observer.setEvalSet(reader.getEvalSet());
      }
      
      // init the nodes by adding the instances read before
      int numOfSamples = reader.getTrainingSet().size();
      for (int i = 0; i < Network.size(); i++) {
        Node node = Network.get(i);
        Protocol protocol = node.getProtocol(pid);
        if (protocol instanceof LearningProtocol) {
          LearningProtocol learningProtocol = (LearningProtocol) protocol;
          InstanceHolder instances = new InstanceHolder(reader.getTrainingSet().getNumberOfClasses());
          for (int j = 0; j < samplesPerNode; j++){
            instances.add(reader.getTrainingSet().getInstance((i * samplesPerNode + j) % numOfSamples), reader.getTrainingSet().getLabel((i * samplesPerNode + j) % numOfSamples));
          }
          
          // set the instances for current node
          learningProtocol.setInstenceHolder(instances);
        } else {
          throw new RuntimeException("The protocol " + pid + " have to implement LearningProtocol interface!");
        }
      }
    } catch (Exception ex) {
      throw new RuntimeException("Exception has occurred in InstanceLoader!", ex);
    }
    
    return false;
  }
  
  /**
   * Sets the specified prediction observer.
   * @param observer prediction observer
   */
  public void setPredictionObserver(PredictionObserver observer) {
    observers.add(observer);
  }
}
