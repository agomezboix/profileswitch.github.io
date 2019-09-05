package fingerprint.clustering.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fingerprint.distance.FPDistance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public abstract class Scenario implements Serializable, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6255247094689040085L;
	Instances data;
	FPDistance distance;
	List<Partition> partitions;

	String name;

	int minUsers;
	
	int kmin, kmax;

	protected int goal;
	public static int GOAL_MAX = 1;
	public static int GOAL_MIN = -1;

	public Scenario(Instances data, FPDistance distance) {
		super();
		this.data = data;
		this.distance = distance;
		partitions = new ArrayList<Partition>();
	}

	public Scenario() {
	}

	
	public Instances getData() {
		return data;
	}

	public void setData(Instances data) {
		this.data = data;
	}

	public FPDistance getDistance() {
		return distance;
	}

	public void setDistance(FPDistance distance) {
		this.distance = distance;
	}

	public void setPartitions(List<Partition> partitions) {
		this.partitions = partitions;
	}

	public void run() {
		buildPartitions();
		ExecutorService executor = Executors.newFixedThreadPool(partitions.size());
		for (Partition p : partitions) {
			executor.execute(p);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		System.out.println(name+" done");
	}

	public String getStats() {
		String stats = "";
		for (Partition p : partitions) {
			stats += data.relationName().replaceAll(" ", "_") + "," + getScenarioName() + "," + p.getStats() + "\n";
		}
		return stats;
	}

	public List<Partition> getPartitions() {
		return partitions;
	}

	public abstract void buildPartitions();

	public String getScenarioName() {
		return name;
	}

	public void load(String ctrFile) throws Exception {
		if (ctrFile.endsWith("ctr")) {
			File f = new File(ctrFile);
			BufferedReader br = new BufferedReader(new FileReader(f));

			ArffLoader loader = new ArffLoader();
			String line = br.readLine();
			loader.setSource(new File(line));
			data = loader.getDataSet();

			distance = new FPDistance();
			distance.loadMatrix(line);
			distance.useMatrix = true;

			partitions = new ArrayList<Partition>();

			String[] ss;
			while ((line = br.readLine()) != null) {
				ss = line.split(" ");
				if (name.equals(ss[0])) {
					List<Integer> centroids = new ArrayList<Integer>();
					for (int i = 0; i < Integer.parseInt(ss[1]); i++) {
						line = br.readLine();
						centroids.add(Integer.parseInt(line.split(" ")[0]));
					}
					Partition p = new Partition(centroids, distance, data);
					p.run();
					partitions.add(p);
				}
			}
			br.close();
		}
	}

	public void improveCentroids() {
		name += "_EC";
		List<Partition> improved = new ArrayList<Partition>();
		for (Partition p : partitions) {
			improved.add(improvePartition(p));
		}
		partitions = improved;
		System.out.println(name+" improved");
	}

	protected Partition improvePartition(Partition p) {
		HashMap<Integer, Set<Integer>> clusters = p.getClusters();
		HashMap<Integer, Set<Integer>> improvedClusters = new HashMap<Integer, Set<Integer>>();
		int idxMax, maxValue, value;
		for (Entry<Integer, Set<Integer>> e : clusters.entrySet()) {
			idxMax = -1;
			maxValue = -1;
			for (Integer idx : e.getValue()) {
				value = (int) data.instance(idx).value(data.attribute(data.numAttributes() - 1));
				if (value > maxValue) {
					maxValue = value;
					idxMax = idx;
				}
			}
			improvedClusters.put(idxMax, e.getValue());
		}
		Partition newPartition = new Partition(improvedClusters, distance, data);
		newPartition.run();
		return newPartition;
	}

	public void setKs(int kmin, int kmax) {
		this.kmin = kmin;
		this.kmax = kmax;
	}

	public int getMinUsers() {
		return minUsers;
	}

	public void setMinUsers(int minUsers) {
		this.minUsers = minUsers;
	}	
	
	
}
