package gossipLearning;

import gossipLearning.interfaces.VectorEntry;
import gossipLearning.utils.SparseVector;

import java.io.Serializable;
import java.util.Vector;

/**
 * This class stores instances and the corresponding class labels in Vector containers. 
 * An instance is represented as a SparseVector and the class label is as a double value. 
 * 
 * @author István Hegedűs
 *
 */
public class InstanceHolder implements Serializable{
  private static final long serialVersionUID = 7677759922507815758L;

  private int size;
  /** @hidden */
  private Vector<SparseVector> instances;
  /** @hidden */
  private Vector<Double> labels;
  private final int numberOfClasses;
  
  /**
   * Constructs and initializes a new InstanceHolder object.
   */
  public InstanceHolder(int numberOfClasses){
    this.numberOfClasses = numberOfClasses;
    size = 0;
    instances = new Vector<SparseVector>();
    labels = new Vector<Double>();
  }
  
  /**
   * This method creates an instance holder based on existing containers which hold the data. The constructor does not
   * copy the data and does not perform any checking on the data!
   * 
   * @param instances instances
   * @param labels class labels
   * @param numberOfClasses number of classes (0 - clustering, N - classification, Integer.MAX_VALUE - regression)
   */
  public InstanceHolder(Vector<SparseVector> instances, Vector<Double> labels, int numberOfClasses) {
    this.instances = instances;
    this.labels = labels;
    this.numberOfClasses = numberOfClasses;
    this.size = instances.size();
  }
  
  /**
   * Copy constructor.
   * @param size
   * @param instances
   * @param labels
   */
  private InstanceHolder(int size, Vector<SparseVector> instances, Vector<Double> labels, int numberOfClasses){
    this.size = size;
    this.numberOfClasses = numberOfClasses;
    this.instances = new Vector<SparseVector>();
    this.labels = new Vector<Double>();
    for (int i = 0; i < instances.size(); i++){
      this.instances.add((SparseVector)instances.get(i).clone());
    }
    for (int i = 0; i < labels.size(); i++){
      this.labels.add((double)labels.get(i));
    }
  }
  
  public Object clone(){
    return new InstanceHolder(size, instances, labels, numberOfClasses);
  }
  
  /**
   * Return the number of classes set by the constructor.
   * 
   * @return number of classes
   */
  public int getNumberOfClasses() {
    return numberOfClasses;
  }
  
  /**
   * Returns the number of stored instances.
   * @return The number of stored instances.
   */
  public int size(){
    return size;
  }
  
  /**
   * Returns the stored instances as a Vector<Map<Integer, Double>>. 
   * If there are no stored instances, returns an empty container.
   * @return the Vector of the stored instances.
   */
  protected Vector<SparseVector> getInstances(){
    return instances;
  }
  
  /**
   * Returns the labels that correspond to the stored instances as a Vector<Double>.
   * If there are no stored instances, returns an empty container.
   * @return the Vector of labels correspond to the stored instances.
   */
  protected Vector<Double> getLabels(){
    return labels;
  }
  
  /**
   * Returns a stored instance at the specified position.
   * @param index index of the instance to return
   * @return instance at the specified position
   */
  public SparseVector getInstance(int index){
    return instances.get(index);
  }
  
  /**
   * Replaces the instance in the container at the specified position with the specified instance.
   * @param index index of the instance to replace
   * @param instance instance to be stored at the specified position
   */
  public void setInstance(int index, SparseVector instance){
    instances.set(index, instance);
  }
  
  /**
   * Returns the label of a stored instance at the specified position.
   * @param index index of the label to return
   * @return label at the specified position
   */
  public double getLabel(int index){
    return labels.get(index);
  }
  
  /**
   * Replaces the label of the instance in the container at the specified position with the specified label.
   * @param index index of the label to replace
   * @param label label to be stored at the specified position
   */
  public void setLabel(int index, double label){
    labels.set(index, label);
  }
  
  /**
   * Adds the specified instance and corresponding label to the container.
   * @param instance instances to be added
   * @param label label to be added
   * @return true if the specified instance and label were added <br/> false otherwise
   */
  public boolean add(SparseVector instance, double label){
    if (instances.add(instance)) {
      if (labels.add(label)) {
        size ++;
        return true;
      }
      instances.remove(instances.size() -1);
    }
    return false;
  }
  
  /**
   * Removes the instance and corresponding label at the specified position in the container.
   * @param index index of the instance and the corresponding label to be removed
   */
  public void remove(int index){
    instances.remove(index);
    labels.remove(index);
    size --;
  }
  
  /**
   * Removes all of the models from the container.
   */
  public void clear(){
    instances.clear();
    labels.clear();
    size = 0;
  }
  
  /**
   * Returns the string representation of this object in Joachims' format.
   */
  public String toString(){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < instances.size(); i++){
      sb.append(labels.get(i));
      for (VectorEntry e : instances.get(i)){
        sb.append(' ');
        sb.append(e.index);
        sb.append(':');
        sb.append(e.value);
      }
      sb.append('\n');
    }
    return sb.toString();
  }
  
}
