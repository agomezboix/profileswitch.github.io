package fingerprint.clustering.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fingerprint.distance.FPDistance;
import weka.core.Instances;

public class Partition implements Serializable, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4746741515814795555L;
	/*
	 * storage of the partitions or clusters; indices reference the index of the
	 * instance (FP) inthe dataset (Instances
	 * object)<centroid_index,<list_members_index>>
	 */
	HashMap<Integer, Set<Integer>> clusters;
	FPDistance distance;
	Instances data;
	int N, K, U;
	String stats;
	protected boolean alreadyExec;

	public Partition(List<Integer> centroids, FPDistance distance, Instances data) {
		super();
		this.distance = distance;
		this.data = data;
		K = centroids.size();
		N = data.numInstances();
		U = 0;

		alreadyExec = false;
		// classify
		clusters = new HashMap<Integer, Set<Integer>>();
		for (Integer c : centroids) {
			Set<Integer> s = new TreeSet<Integer>();
			s.add(c);
			clusters.put(c, s);
		}

		double[] dist = new double[centroids.size()];
		double min;
		int cluster = 0;

		for (int i = 0; i < data.numInstances(); i++) {
			U += data.get(i).value(data.attribute(data.numAttributes() - 1));

			// computing distances to centroids
			for (int j = 0; j < dist.length; j++) {
				dist[j] = distance.distance(data.instance(i), data.instance(centroids.get(j)));
			}

			// selected nearest distance
			min = Double.MAX_VALUE;
			for (int j = 0; j < dist.length; j++) {
				if (dist[j] < min) {
					min = dist[j];
					cluster = j;
				}
			}
			clusters.get(centroids.get(cluster)).add(i);
		}
	}

	public Partition(HashMap<Integer, Set<Integer>> clusters, FPDistance distance, Instances data) {
		super();
		this.clusters = clusters;
		this.distance = distance;
		this.data = data;

		K = clusters.keySet().size();
		N = data.numInstances();
		U = 0;
		alreadyExec = false;

		for (int i = 0; i < data.numInstances(); i++) {
			U += data.get(i).value(data.attribute(data.numAttributes() - 1));
		}
	}

	public double getIdentifiability() {
		double I = 0, s;
		for (Integer k : clusters.keySet()) {
			s = 0;
			for (Integer i : clusters.get(k)) {
				s += data.get(i).value(data.attribute(data.numAttributes() - 1));
			}
			I += 1 / s;
		}
		return I;
	}

	public double getNormIdentifiability() {
		double I = getIdentifiability();
		I = 1 - (U * I - K * K) / (U * U - K * K);
		return I;
	}

	/*
	 * each fingerprint is weighted by the number of devices in that fingerprint
	 */
	public double getDisruption() {
		double R = 0;
		for (Integer k : clusters.keySet()) {
			for (Integer i : clusters.get(k)) {
				R += distance.distance(data.get(k), data.get(i))
						* (data.get(i).value(data.attribute(data.numAttributes() - 1)) / U);
			}
		}
		return R;
	}

	/*
	 * The sum of the distances to the fingerprint targeted is weighted by total
	 * number of devices within the cluster (expect the devices in the
	 * fingerprint targeted)
	 */
	public double getGlobalDisruption() {
		double u, D = 0, d;
		for (Integer k : clusters.keySet()) {
			u = 0;
			d = 0;
			for (Integer i : clusters.get(k)) {
				d += distance.distance(data.get(k), data.get(i));
				u += data.get(i).value(data.attribute(data.numAttributes() - 1));
			}
			u -= data.get(k).value(data.attribute(data.numAttributes() - 1));
			D += d * u / U;
		}
		return D;
	}

	public double getDeviation() {
		double d = (2 * (K - 1)) / K, D = 0, f = (1.0 / K), s;
		for (Integer k : clusters.keySet()) {
			s = (double) clusters.get(k).size();
			D += Math.abs(f - s / N);
		}
		return d - D;
	}

	public String getStats() {
		if (!alreadyExec) {
			run();
		}
		return stats;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public double getU() {
		return U;
	}

	public void setU(int u) {
		U = u;
	}

	protected int getNumberUsers(Set<Integer> set) {
		int u = 0;
		for (Integer i : set) {
			u += data.get(i).value(data.attribute(data.numAttributes() - 1));
		}
		return u;
	}

	/*
	 * String with values in the following order
	 * k,Identifiability,normIdentifiability,disruption,globalDisruption,
	 * deviation, cluster sizes{array}
	 */

	public void run() {
		if (!alreadyExec) {

			String cs = "";
			for (Integer i : clusters.keySet()) {
				cs += getNumberUsers(clusters.get(i)) + ",";
			}
			stats = "" + K + "," + getIdentifiability() + "," + getNormIdentifiability() + "," + getDisruption() + ","
					+ getGlobalDisruption() + "," + getDeviation() + ",\"" + cs.substring(0, cs.length() - 1) + "\","
					+ getClusterSizeFP() + "," + getClusterMinDevices() + "," + getClusterMinFPs();
			alreadyExec = true;
		}
	}

	public String printCentroids() {
		String cents = "";
		for (Integer c : clusters.keySet()) {
			cents += data.get(c).toString() + "\n";
		}
		return cents;
	}

	public Set<Integer> getCentroids() {
		return clusters.keySet();
	}

	public HashMap<Integer, Set<Integer>> getClusters() {
		return clusters;
	}

	public int getClusterMinFPs() {
		int min = Integer.MAX_VALUE;

		for (Integer k : clusters.keySet()) {
			if (clusters.get(k).size() < min)
				min = clusters.get(k).size();
		}
		return min;
	}

	public int getClusterMinDevices() {
		int users, min = Integer.MAX_VALUE;

		for (Integer k : clusters.keySet()) {
			users = 0;
			for (Integer i : clusters.get(k)) {
				users += data.get(i).value(data.attribute(data.numAttributes() - 1));
			}
			if (users < min)
				min = users;
		}
		return min;
	}

	public String getClusterSizeFP() {
		String s = "\"";
		for (Integer k : clusters.keySet()) {
			s += clusters.get(k).size() + ",";
		}
		return s.substring(0, s.length() - 1) + "\"";
	}
}
