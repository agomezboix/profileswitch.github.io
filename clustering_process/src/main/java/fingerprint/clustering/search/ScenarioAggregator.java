package fingerprint.clustering.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import fingerprint.distance.FPDistance;
import weka.core.Instances;

public class ScenarioAggregator implements Runnable {
	Scenario s;
	int minUsers;

	public ScenarioAggregator(Scenario s) throws Exception {
		super();
		this.s = s;
		minUsers = s.getMinUsers();
	}

	public ScenarioAggregator(Scenario s, int minUsers) {
		super();
		this.s = s;
		this.minUsers = minUsers;
	}

	public Scenario aggregate(Scenario s) {
		Scenario sc = SerializationUtils.clone(s);
		List<Partition> aggregated = new ArrayList<Partition>();
		Partition newP;
		for (Partition p : sc.getPartitions()) {
			newP = aggregate(p);
			if (newP != null) {
				aggregated.add(newP);
			}
		}
		if (aggregated.isEmpty()) {
			sc = null;
		} else {
			sc.partitions = aggregated;
			sc.name += "_agg";
		}
		return sc;
	}

	public Partition aggregate(Partition p) {
		Partition res;
		if (p.getClusterMinDevices() < minUsers) {
			List<Integer> finalCentroids = new ArrayList<Integer>();
			List<Integer> toAggregate = new ArrayList<Integer>();
			HashMap<Integer, Set<Integer>> clusters = p.clusters;

			Instances data = p.data;
			int uck;
			for (Map.Entry<Integer, Set<Integer>> e : clusters.entrySet()) {
				uck = 0;
				for (Integer ins : e.getValue()) {
					uck += data.get(ins).value(data.numAttributes() - 1);
				}
				if (uck < minUsers) {
					toAggregate.addAll(e.getValue());
				} else {
					finalCentroids.add(e.getKey());
				}
			}

			// adding finalCentroids and instances not to be aggregate
			clusters = new HashMap<Integer, Set<Integer>>();
			for (Integer centroid : finalCentroids) {
				clusters.put(centroid, p.clusters.get(centroid));
			}

			// aggregating
			Integer centroid;
			for (Integer i : toAggregate) {
				centroid = classify(i, finalCentroids, p.data, p.distance);

				clusters.get(centroid).add(i);
			}

			res = new Partition(clusters, p.distance, p.data);
		} else {
			res = null;
		}
		return res;
	}

	public Integer classify(Integer instance, List<Integer> centroids, Instances data, FPDistance distance) {
		Integer cluster = -1;
		double dist, minDist = Double.MAX_VALUE;
		for (Integer centroid : centroids) {
			dist = distance.distance(data.get(centroid), data.get(instance));
			if (dist < minDist) {
				minDist = dist;
				cluster = centroid;
			}
		}
		return cluster;
	}

	public void run() {
		s = aggregate(s);
	}

}
