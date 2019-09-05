package fingerprint.clustering.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fingerprint.distance.FPDistance;
import weka.core.Instances;

public class LowestEntropyScenario extends KRandomScenario {

	
	private static final long serialVersionUID = -4777232503853387773L;
	/**
	 * 
	 */
	
	public LowestEntropyScenario(Instances data, FPDistance distance) {
		super(data, distance);
		name = "lowestentropy";
	}
	@Override
	protected Partition createPartition(int k) {

		List<Integer> l = new ArrayList<Integer>();
		HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		int key, idx = data.numAttributes() - 1;

		// getting anonymity sets
		for (int i = 0; i < data.numInstances(); i++) {
			key = (int) data.get(i).value(idx);
			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<Integer>());
			}
			map.get(key).add(i);
		}
		// ordering
		List<Integer> rank = new ArrayList<Integer>(map.keySet());
		Collections.sort(rank, Collections.reverseOrder());
		// getting FPs with the highest anonymity set
		List<Integer> indices = new ArrayList<Integer>();
		int i = 0;
		while (indices.size() < k && i < rank.size()) {
			indices.addAll(map.get(rank.get(i)));
			i++;
		}

		for (int j = 0; j < k && j < indices.size(); j++) {
			l.add(indices.get(j));
		}

		return new Partition(l, distance, data);
	}
}
