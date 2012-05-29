package gossipLearning.models.weakLearners;

import gossipLearning.interfaces.WeakLearner;
import gossipLearning.utils.SparseVector;
import gossipLearning.utils.Utils;

import java.util.Arrays;

import peersim.config.Configuration;

public class SequentialLearner extends WeakLearner {
  private static final long serialVersionUID = 6400546153303842520L;
  
  private static final String PAR_NUMLEARNERS = "numLearners";
  private static final String PAR_LEARNERNAME = "learnerName";
  
  private int numberOfClasses;
  private int numberOfLearners;
  
  private String baseLearnerName;
  private WeakLearner[] baseLearners;
  
  public SequentialLearner() {
  }
  
  public SequentialLearner(SequentialLearner a) {
    numberOfClasses = a.numberOfClasses;
    numberOfLearners = a.numberOfLearners;
    baseLearnerName = a.baseLearnerName;
    if (a.baseLearners != null) {
      baseLearners = new WeakLearner[numberOfLearners];
      for (int i = 0; i < numberOfLearners; i++) {
        baseLearners[i] = (WeakLearner)a.baseLearners[i].clone();
      }
    }
  }
  
  @Override
  public void init(String prefix) {
    numberOfLearners = Configuration.getInt(prefix + "." + PAR_NUMLEARNERS);
    baseLearnerName = Configuration.getString(prefix + "." + PAR_LEARNERNAME);
    baseLearners = new WeakLearner[numberOfLearners];
    for (int i = 0; i < numberOfLearners; i++) {
      try {
        baseLearners[i] = (WeakLearner)Class.forName(baseLearnerName).newInstance();
        baseLearners[i].init(prefix);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public int getNumberOfClasses() {
    return numberOfClasses;
  }

  @Override
  public void setNumberOfClasses(int numberOfClasses) {
    this.numberOfClasses = numberOfClasses;
    for (int i = 0; i < numberOfLearners; i++) {
      baseLearners[i].setNumberOfClasses(numberOfClasses);
    }
  }

  @Override
  public Object clone() {
    return new SequentialLearner(this);
  }

  @Override
  public void update(SparseVector instance, double label, double[] weight) {
    double[] dist;
    for (int i = 0; i < numberOfLearners; i++) {
      baseLearners[i].update(instance, label, weight);
      dist = baseLearners[i].distributionForInstance(instance);
      for (int j = 0; j < numberOfClasses; j++) {
        if (dist[j] < 0.0) {
          weight[j] *= -1.0;
        }
      }
    }
  }

  @Override
  public double[] distributionForInstance(SparseVector instance) {
    double[] distribution = new double[numberOfClasses];
    Arrays.fill(distribution, 1.0);
    double[] dist;
    for (int i = 0; i < numberOfLearners; i++) {
      dist = baseLearners[i].distributionForInstance(instance);
      for (int j = 0; j < numberOfClasses; j++) {
        distribution[j] *= dist[j];
      }
    }
    return Utils.normalize(distribution);
  }

}
