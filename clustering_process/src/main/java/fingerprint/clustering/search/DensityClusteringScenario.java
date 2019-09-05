package fingerprint.clustering.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fingerprint.distance.FPDistance;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class DensityClusteringScenario extends KMeansScenario {

	public DensityClusteringScenario(Instances data, FPDistance distance) {
		super(data, distance);
		processors = 1;
		name = "MakeDensityBasedClustering";
	}

	public DensityClusteringScenario(Instances data, FPDistance distance, int proc) {
		super(data, distance, proc);
		processors = proc;
		name = "MakeDensityBasedClustering";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Partition createPartition(int k) {
		MakeDensityBasedClusterer density = new MakeDensityBasedClusterer();
		SimpleKMeans kmeans = new SimpleKMeans();
		try {
			kmeans.setDistanceFunction(distance);
			kmeans.setDontReplaceMissingValues(false);
			kmeans.setNumExecutionSlots(processors);
			kmeans.setNumClusters(k);
			density.setClusterer(kmeans);
			density.buildClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Partition(calcMedoids(density), distance, data);
	}

	public List<Integer> calcMedoids(MakeDensityBasedClusterer kmeans) {

		HashMap<Integer, List<Integer>> n_groups = new HashMap<Integer, List<Integer>>();

		int ci;
		for (int i = 0; i < data.numInstances(); i++) {
			try {
				ci = kmeans.clusterInstance(data.get(i));
				if (!n_groups.containsKey(ci)) {
					n_groups.put(ci, new ArrayList<Integer>());
				}
				n_groups.get(ci).add(i);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		HashMap<Integer, Integer> centroids = new HashMap<Integer, Integer>();
		int[] cluster;
		for (Map.Entry<Integer, List<Integer>> c : n_groups.entrySet()) {
			cluster = new int[c.getValue().size()];
			for (int j = 0; j < c.getValue().size(); j++) {
				cluster[j] = c.getValue().get(j).intValue();
			}
			if (cluster.length != 0) {
				centroids.put(c.getKey(), calcClusterMean(cluster));
			}
		}

		List<Integer> cent = new ArrayList<Integer>();
		for (Map.Entry<Integer, Integer> e : centroids.entrySet()) {
			cent.add(e.getValue());
		}
		return cent;
	}
}
