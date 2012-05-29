package gossipLearning.utils;

import java.io.Serializable;
import java.util.Random;

public class LogNormalRandom implements Serializable{
  private static final long serialVersionUID = -4939915314829563458L;
  /** @hidden */
  private final Random rand;
  private final double mu;
  private final double sigma;
  
  public LogNormalRandom(double mu, double sigma) {
    this(mu, sigma, System.currentTimeMillis());
  }
  
  public LogNormalRandom(double mu, double sigma, long seed) {
    this.mu = mu;
    this.sigma = sigma;
    rand = new Random(seed);
  }
  
  public double nextDouble() {
    double r = mu + sigma * rand.nextGaussian(); // scaled normal
    return Math.pow(Math.E, r);
  }

}
