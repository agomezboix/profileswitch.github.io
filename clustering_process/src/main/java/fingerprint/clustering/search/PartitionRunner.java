package fingerprint.clustering.search;

public class PartitionRunner implements Runnable {
	Scenario s;
	int k;
	int type;
	Partition p;

	public PartitionRunner(DensityClusteringScenario sc, int kk) {
		k = kk;
		type = 0;
		s = sc;
	}

	public PartitionRunner(KMeansScenario sc, int kk) {
		k = kk;
		type = 1;
		s = sc;
	}

	public PartitionRunner(KMeansEntropyScenario sc, int kk) {
		k = kk;
		type = 2;
		s = sc;
	}

	public void run() {
		if (type == 0) {
			DensityClusteringScenario alg = (DensityClusteringScenario) s;
			p = alg.createPartition(k);
		} else if (type == 1) {
			KMeansScenario alg = (KMeansScenario) s;
			p = alg.createPartition(k);
		} else {
			KMeansEntropyScenario alg = (KMeansEntropyScenario) s;
			p = alg.createPartition(k);
		}
	}

	public Partition getPartition() {
		return p;
	}
}
