package fingerprint.clustering.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fingerprint.distance.FPDistance;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeansScenario extends KRandomScenario {

	int processors;

	public KMeansScenario(Instances data, FPDistance distance) {
		super(data, distance);
		processors = 1;
		name = "kmeans";

	}

	public KMeansScenario(Instances data, FPDistance distance, int proc) {
		super(data, distance);
		processors = proc;
		name = "kmeans";

	}
	
	@Override
	protected Partition createPartition(int k) {
		SimpleKMeans clusterer_kmeans = new SimpleKMeans();
		clusterer_kmeans.setDontReplaceMissingValues(false);
		try {
			clusterer_kmeans.setDistanceFunction(distance);
			clusterer_kmeans.setNumClusters(k);

			clusterer_kmeans.setNumExecutionSlots(processors);

			clusterer_kmeans.buildClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Partition(calcMedoids(clusterer_kmeans), distance, data);
	}

	public List<Integer> calcMedoids(SimpleKMeans kmeans) {

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

	protected int calcClusterMean(int[] cluster) {
		double sum, min_sum;
		int medoid = 0;
		min_sum = Double.MAX_VALUE;
		for (int i = 0; i < cluster.length; i++) {
			sum = 0;
			for (int j = 0; j < cluster.length; j++) {
				sum += distance.distance(data.instance(cluster[i]), data.instance(cluster[j]));
			}
			sum /= cluster.length;
			if (sum < min_sum) {
				min_sum = sum;
				medoid = i;
			}
		}
		return cluster[medoid];
	}

}
