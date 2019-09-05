package fingerprint.clustering.search;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import fingerprint.distance.FPDistance;
import weka.core.Instances;

public class RawDataScenario extends Scenario {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RawDataScenario(Instances data, FPDistance distance) {
		super(data, distance);
		name = "rawData";
	}

	@Override
	public void buildPartitions() {
		HashMap<Integer, Set<Integer>> clusters = new HashMap<Integer, Set<Integer>>();
		for (int i = 0; i < data.numInstances(); i++) {
			TreeSet<Integer> s = new TreeSet<Integer>();
			s.add(i);
			clusters.put(i, s);
		}
		partitions.add(new Partition(clusters, distance, data));
	}
}
