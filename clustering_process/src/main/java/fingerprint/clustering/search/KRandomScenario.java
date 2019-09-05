package fingerprint.clustering.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fingerprint.distance.FPDistance;
import weka.core.Instances;

public class KRandomScenario extends Scenario {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2673046662913793179L;
	protected int clusterSize, iterations;

	public KRandomScenario(Instances data, FPDistance distance) {
		super(data, distance);
		iterations = 10;
		clusterSize = 50;
		name = "kRandom";
	}

	public int getClusterSize() {
		return clusterSize;
	}

	public void setClusterSize(int clusterSize) {
		this.clusterSize = clusterSize;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	@Override
	public void buildPartitions() {

		int[] k = setK(kmin, kmax);
		double[] k_values = new double[3];
		int iter = iterations;
		// clustering
		Partition p = createPartition(k[0]);
		p.run();
		partitions.add(p);
		k_values[0] = p.getIdentifiability();

		if (k[0] < k[2]) {
			// running kf
			p = createPartition(k[2]);
			p.run();
			partitions.add(p);
			k_values[2] = p.getIdentifiability();

			// running km
			p = createPartition(k[1]);
			p.run();
			partitions.add(p);
			k_values[1] = p.getIdentifiability();

			// the goal is to minimize privacy
			int goal = GOAL_MIN;
			while (iter > 0 && hasNextIter(k, k_values, goal)) {
						
				// the goal is to minimize privacy
				k = updateKs(k, k_values, goal);
				// put value
				p = createPartition(k[1]);
				p.run();
				partitions.add(p);
				k_values[1] = p.getIdentifiability();
			}
			iter--;
		}
	}

	protected Partition createPartition(int k) {
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		List<Integer> l = new ArrayList<Integer>();
		int r;
		while (l.size() != k) {
			r = rand.nextInt(data.numInstances());
			if (!l.contains(r)) {
				l.add(r);
			}
		}
		return new Partition(l, distance, data);
	}

	/*
	 * returns an array with tha values of [ki,km,kf] computed with the current
	 * k-limits, values of the k and the goal of the function
	 */
	protected int[] updateKs(int[] k, double[] values, int goal) {
		int[] res = new int[3];
		int ki = k[0], km = k[1], kf = k[2];
		double vi = values[0], vm = values[1], vf = values[2];
		if (goal == GOAL_MIN) {
			if (vi < vm && vi < vf) {
				kf = km;
			} else if (vf < vm && vf < vi) {
				ki = km;
			} else if (vm < vi && vm < vf) {
				if (vi < vf) {
					kf = km;
				} else {
					ki = km;
				}
			}
		} else if (goal == GOAL_MAX) {
			if (vi > vm && vi > vf) {
				kf = km;
			} else if (vf > vm && vf > vi) {
				ki = km;
			} else if (vm > vi && vm > vf) {
				if (vi > vf) {
					kf = km;
				} else {
					ki = km;
				}
			}
		}
		km = (kf - ki) / 2 + ki;
		res[0] = ki;
		res[1] = km;
		res[2] = kf;
		return res;
	}

	protected boolean hasNextIter(int[] k, double[] values, int goal) {
		if (k[0] > k[2]) {
			return false;
		}
		if (k[1] == k[0] || k[1] == k[2]) {
			return false;
		}
		int[] next = updateKs(k, values, goal);
		if (k[0] == next[0] && k[1] == next[1] && k[2] == next[2]) {
			return false;
		}
		return true;
	}

	protected int[] setK(int ki, int kf) {
		int[] k = new int[3];

		kf = (kf < 2) ? 2 : kf;
		ki = (ki > kf) ? kf : ki;

		k[0] = ki;
		k[1] = (kf - ki) / 2 + ki;
		k[2] = kf;

		return k;
	}
}
