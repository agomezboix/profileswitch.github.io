package fingerprint.clustering.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.SerializationUtils;

import fingerprint.clustering.util.MatrixCalculator;
import fingerprint.clustering.util.ResultsWriter;
import fingerprint.distance.FPDistance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/*main class */
public class SearchRunner {
	/**
	 * main method for simulating all scenarios
	 * 
	 * @param arffFile
	 * @param use
	 *            matrix= true | false | yes | no
	 * @param minimum
	 *            number of users per cluster
	 * @param numberOfProcessors
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String arffFile = args[0];
		boolean useMatrix;

		int minUsers = Integer.parseInt(args[2]);
		int threads = Integer.parseInt(args[3]);

		FPDistance distance = new FPDistance();
		double[] entropy = { 0.0, 0.0, 0.0, 0.489, 1.922, 0.096, 4.437, 10.281, 0.042, 0.042, 0, 0, 6.967, 0, 0, 0,
				2.559, 6.323, 0, 0 };
		distance.setEntropyWeights(entropy);
		distance.setAttributeIndices("4-10,13,17,18");

		if (args[1].startsWith("t") || args[1].startsWith("T") || args[1].startsWith("y") || args[1].startsWith("Y")) {
			useMatrix = true;
			File matx = new File(arffFile.replaceAll(".arff", ".mtx"));
			if (!matx.exists()) {
				MatrixCalculator matrix = new MatrixCalculator(arffFile, distance);
				matrix.setRunInParallel(true);
				MatrixCalculator.computeMatrixInParallel(arffFile, distance, threads, false);
			}
			distance.loadMatrix(arffFile.replaceAll(".arff", ".mtx"));
			distance.useMatrix = useMatrix;
		} else {
			useMatrix = false;
		}

		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(arffFile));
		Instances data = loader.getDataSet();

		int U = 0;
		for (int i = 0; i < data.numInstances(); i++) {
			U += data.get(i).value(data.attribute(data.numAttributes() - 1));
		}

		int processors = (threads - 7) / 3;
		processors = (processors > 1) ? processors : 1;
		// run first lot of scenarios
		List<Scenario> scenarios = new ArrayList<Scenario>();
		scenarios.add(new RawDataScenario(data, distance));
		scenarios.add(new LowestEntropyScenario(data, distance));
		scenarios.add(new KRandomScenario(data, distance));
		scenarios.add(new HierarchicalScenario(data, distance));
		scenarios.add(new KMeansScenario(data, distance, processors));
		scenarios.add(new KMeansEntropyScenario(data, distance, processors));
		scenarios.add(new DensityClusteringScenario(data, distance, processors));

		for (Scenario s : scenarios) {
			s.setKs(2, U / minUsers);
			s.setMinUsers(minUsers);
		}

		System.out.println(Arrays.toString(args));
		System.out.println(U);
		
		System.out.println("\nStarting executions");
		ExecutorService executor = Executors.newFixedThreadPool(scenarios.size());
		for (Scenario job : scenarios) {
			executor.execute(job);
		}
		executor.shutdown();

		while (!executor.isTerminated()) {
		}
		System.out.println("\nImproving centroids");
		// computing stats with _EC entropy centroids
		int size = scenarios.size();
		for (int i = 2; i < size; i++) {
			Scenario sc = SerializationUtils.clone(scenarios.get(i));
			sc.improveCentroids();
			scenarios.add(sc);
		}

		System.out.println("Aggregating FPs");
		// aggregating process for all the scenarios
		List<ScenarioAggregator> aggregators = new ArrayList<ScenarioAggregator>();
		for (int i = 1; i < scenarios.size(); i++) {
			aggregators.add(new ScenarioAggregator(scenarios.get(i)));
		}
		executor = Executors.newFixedThreadPool(aggregators.size());
		for (ScenarioAggregator job : aggregators) {
			executor.execute(job);
		}
		executor.shutdown();

		while (!executor.isTerminated()) {
		}

		for (ScenarioAggregator agg : aggregators) {
			if (agg.s != null) {
				scenarios.add(agg.s);
			}
		}
		System.out.println("\nCreating result files");
		ResultsWriter.createRESFileMinStats(arffFile, scenarios);
	}
	
}
