package winnow;

import gossipLearning.interfaces.Model;
import gossipLearning.interfaces.SimilarityComputable;
import gossipLearning.utils.SparseVector;
import peersim.config.Configuration;
import java.util.*;

public class P2Winnow implements Model, SimilarityComputable<P2Winnow>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * A sulyvektor, amit tanulunk.
	 */
	private SparseVector w;
	/**
	 * Osztalyok szama.
	 */
	private int numberOfClasses;
	
	/**
	 * Az eta erteke.
	 */
	protected double eta = 0.1;
	protected static final String PAR_ETA = "eta";
	/**
	 * Attributumok maximalis erteke.
	 */
	protected double attrnum = 57;
	protected static final String PAR_ATTRNUM = "attrnum";
	
	public P2Winnow() {
		setNumberOfClasses(2);
		w = new SparseVector();
	}
	
	/**
	 * Konstruktor, ami parameterul varja az osztalyok szamat.
	 * @param numberOfClasses osztalyok szama
	 */
	public P2Winnow(int numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
		w = new SparseVector();
	}
	
	/**
	 * Copy konstruktor, letrehozza a deep copyt a clone metodus szamara
	 * @param w masolando sulyvektor
	 * @param numberOfClasses osztalyok szama
	 */
	public P2Winnow(SparseVector w, int numberOfClasses, double eta) {
		this.numberOfClasses = numberOfClasses;
		this.w = (SparseVector)w.clone();
		this.eta = eta;
	}
	
	public Object clone(){
		return new P2Winnow(w, numberOfClasses, eta);
	}
	
	@Override
	public void init(String prefix) {
		eta = Configuration.getDouble(prefix + "." + PAR_ETA, 0.001);
		double[] wvector = new double[(int)this.attrnum];
		Arrays.fill(wvector, 1.0);
		w = new SparseVector(wvector);
	}

	@Override
	public void update(SparseVector instance, double label) {
		double y = label;
		double y_pred = predict(instance);
		if ( y != y_pred ) {	//teves osztalyozas eseten javitjuk a sulyt
			if ( y == 1.0 ) {	//false negativ esetben
				w.add(w, eta);	//szorozzuk (1+eps) -al
				/*for ( VectorEntry ve : instance ) {
					w.put(ve.index, w.get(ve.index)*Math.exp(eta*y*ve.value));
				}*/
			} else if ( y == 0.0 ) {	//false negativ esetben
				w.mul(1.0/(1+eta));
			}
		}
	}

	@Override
	public double predict(SparseVector instance) {
		double innerProduct = w.mul(instance);	//kiszamitja a belso szorzatat a sulyoknak es a jellemzoknek
		double n = ((double)instance.size())/2;		//a jellemzok szama, ez lesz alapbol a kuszobertek
		return (innerProduct > n)?1.0:0.0;		//ha a kuszoberteknel nagyobb a szorzat, akkor igaznak vesszuk
	}

	@Override
	public int getNumberOfClasses() {
		return this.numberOfClasses;
	}

	@Override
	public void setNumberOfClasses(int numberOfClasses) {
		this.numberOfClasses = numberOfClasses;		
	}

	@Override
	public double computeSimilarity(P2Winnow model) {
		
		return 0;
	}
	
}

