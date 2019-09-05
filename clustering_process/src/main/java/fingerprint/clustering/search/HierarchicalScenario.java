package fingerprint.clustering.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fingerprint.distance.FPDistance;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Instances;

public class HierarchicalScenario extends KMeansScenario {

	public HierarchicalScenario(Instances data, FPDistance distance) {
		super(data, distance);
		iterations = 10;
		clusterSize = 50;
		name = "HierarchicalClustering";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Partition createPartition(int k) {
		HierarchicalClusterer hierarchical = new HierarchicalClusterer();
		try {
			hierarchical.setDistanceFunction(distance);
			hierarchical.setNumClusters(k);
			hierarchical.buildClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Partition(calcMedoids(hierarchical), distance, data);
	}
	
	
	public List<Integer> calcMedoids(HierarchicalClusterer kmeans) {

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
