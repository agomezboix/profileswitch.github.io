package fingerprint.clustering.search;

import fingerprint.distance.FPDistance;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeansEntropyScenario extends KMeansScenario {

	/**
	 * 
	 */
	private static final long serialVersionUID = -653048153643611435L;

	public KMeansEntropyScenario(Instances data, FPDistance distance) {
		super(data, distance);
		processors = 1;
		name = "kmeansentropy";
	}

	public KMeansEntropyScenario(Instances data, FPDistance distance, int proc) {
		super(data, distance);
		processors = proc;
		name = "kmeansentropy";
	}
	
	@Override
	protected Partition createPartition(int k) {
		SimpleKMeans clusterer_kmeans = new SimpleKMeans();
		clusterer_kmeans.setDontReplaceMissingValues(false);
		try {
			clusterer_kmeans.setDistanceFunction(distance);
			clusterer_kmeans.setNumClusters(k);
			clusterer_kmeans.setNumExecutionSlots(processors);
			clusterer_kmeans.useRankInitCentroids();

			clusterer_kmeans.buildClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Partition(calcMedoids(clusterer_kmeans), distance, data);
	}

}
