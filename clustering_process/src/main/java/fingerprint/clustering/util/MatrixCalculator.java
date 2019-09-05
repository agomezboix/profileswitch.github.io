package fingerprint.clustering.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fingerprint.distance.FPDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class MatrixCalculator implements Runnable {
	String inputFile;
	// String atts;
	FPDistance dist;
	boolean runInParallel;
	int threadPoolSize;

	public MatrixCalculator(String inputFile) {
		super();
		dist = new FPDistance();
		this.inputFile = inputFile;
		runInParallel = false;
		threadPoolSize = 10;
	}

	public MatrixCalculator(String inputFile, FPDistance d) {
		super();
		dist = d;
		this.inputFile = inputFile;
		runInParallel = false;
		threadPoolSize = 10;

	}

	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	public boolean isRunInParallel() {
		return runInParallel;
	}

	public void setRunInParallel(boolean runInParallel) {
		this.runInParallel = runInParallel;
	}

	// matrix file --> *.mtx
	public void run() {
		if (runInParallel)
			computeMatrixInParallel(inputFile, dist, threadPoolSize, false);
		else
			computeMatrix(inputFile, dist);
	}

	public static String getMatrixFileName(String arffFile) {
		File f = new File(arffFile);
		return f.getName().replaceAll(".arff", ".mtx");
	}

	public static String getAbsoluteMatrixFileName(String arffFile) {
		File f = new File(arffFile);
		return f.getParent() + System.getProperty("file.separator") + getMatrixFileName(arffFile);
	}

	public static void computeMatrix(String arffFile, FPDistance distance) {
		try {
			File f = new File(arffFile);
			ArffLoader load = new ArffLoader();
			load.setSource(f);
			Instances data = load.getDataSet();

			String filename =getAbsoluteMatrixFileName(f.getAbsolutePath());
			if (!(new File(filename).exists())) {
				PrintWriter pw = new PrintWriter(new FileWriter(filename));
				for (int i = 0; i < data.numInstances(); i++) {
					for (int j = i + 1; j < data.numInstances(); j++) {
						pw.println(data.instance(i).stringValue(data.attribute(0)) + " "
								+ data.instance(j).stringValue(data.attribute(0)) + " "
								+ String.format("%.4f", distance.distance(data.get(i), data.get(j))));
					}
				}
				pw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void computeMatrixInParallel(String arffFile, FPDistance distance, int poolSize, boolean retrieve) {
		try {
			File f = new File(arffFile);
			ArffLoader load = new ArffLoader();
			load.setSource(f);
			Instances data = load.getDataSet();
			System.out.println("computing distance matrix... " + arffFile);
			ExecutorService executor = Executors.newFixedThreadPool(poolSize);
			List<ParallelDistance> runners = new ArrayList<ParallelDistance>();
			PrintWriter pw;
			int i, j;
			String filename = getAbsoluteMatrixFileName(f.getAbsolutePath());

			if ((new File(filename).exists()) && retrieve) {
				String line, lastestCompleteEntry = null;
				BufferedReader br = new BufferedReader(new FileReader(filename));
				// empty file->create new
				if ((line = br.readLine()) == null) {
					i = 0;
					j = i + 1;
					pw = new PrintWriter(new FileWriter(filename));
				} else {
					// search the last computed entry , retrieve indices and
					// continue
					boolean end = false;
					if (line.split(" ").length == 3 && line.split(" ")[2].length() == 6) {
						lastestCompleteEntry = line;
						while ((line = br.readLine()) != null && !end) {
							if (line.split(" ").length == 3 && line.split(" ")[2].length() == 6) {
								lastestCompleteEntry = line;
							} else {
								end = true;
							}
						}

						// in lastestCompleteEntry -->[idx_i,idx_j,value]
						String[] entry = lastestCompleteEntry.split(" ");
						// looking for idx_i
						int ii = 0;
						String idx_string = data.get(ii).stringValue(data.attribute(0));
						while (ii < data.numInstances() && !idx_string.equals(entry[0])) {
							idx_string = data.get(++ii).stringValue(data.attribute(0));
						}
						i = ii;
						// looking for idx_j
						while (ii < data.numInstances() && !idx_string.equals(entry[1])) {
							idx_string = data.get(++ii).stringValue(data.attribute(0));
						}
						j = ii + 1;
						// recovering file
						br.close();
						br = new BufferedReader(new FileReader(filename));
						String tmp_f = f.getName() + "_tmp_matrix_file_";
						PrintWriter tmp_pw = new PrintWriter(new FileWriter(tmp_f));
						while ((line = br.readLine()) != null && line.split(" ").length == 3
								&& line.split(" ")[2].length() == 6) {
							tmp_pw.println(line);
						}
						tmp_pw.close();
						br.close();
						File f1 = new File(filename);
						File f2 = new File(tmp_f);
						f1.delete();
						f2.renameTo(f1);
						pw = new PrintWriter(new FileWriter(filename, true));
					} else {
						i = 0;
						j = i + 1;
						pw = new PrintWriter(new FileWriter(filename));
					}
				}
				br.close();
			} else {
				i = 0;
				j = i + 1;
				pw = new PrintWriter(new FileWriter(filename));

			}

			// finishing the remaining j-indices
			for (; j < data.numInstances(); j++) {

				if (runners.size() == poolSize) {
					// process all and empty the list
					for (ParallelDistance pd : runners) {
						executor.execute(pd);
					}
					executor.shutdown();

					while (!executor.isTerminated()) {
					}
					for (ParallelDistance pd : runners) {
						pw.println(pd.first.stringValue(data.attribute(0)) + " "
								+ pd.second.stringValue(data.attribute(0)) + " " + String.format("%.4f", pd.value));

					}
					runners.clear();
					executor = Executors.newFixedThreadPool(poolSize);
				}
				MatrixCalculator a = new MatrixCalculator("");
				runners.add(a.new ParallelDistance(distance, data.get(i), data.get(j)));
			}
			if (!runners.isEmpty()) {
				// process all and empty the list
				for (ParallelDistance pd : runners) {
					executor.execute(pd);
				}
				executor.shutdown();

				while (!executor.isTerminated()) {
				}
				for (ParallelDistance pd : runners) {
					pw.println(pd.first.stringValue(data.attribute(0)) + " " + pd.second.stringValue(data.attribute(0))
							+ " " + String.format("%.4f", pd.value));
				}
			}

			// start in the next index
			executor = Executors.newFixedThreadPool(poolSize);
			i++;
			for (; i < data.numInstances(); i++) {
				for (j = i + 1; j < data.numInstances(); j++) {

					if (runners.size() == poolSize) {
						// process all and empty the list
						for (ParallelDistance pd : runners) {
							executor.execute(pd);
						}
						executor.shutdown();

						while (!executor.isTerminated()) {
						}
						for (ParallelDistance pd : runners) {
							pw.println(pd.first.stringValue(data.attribute(0)) + " "
									+ pd.second.stringValue(data.attribute(0)) + " " + String.format("%.4f", pd.value));

						}
						runners.clear();
						executor = Executors.newFixedThreadPool(poolSize);
					}
					MatrixCalculator a = new MatrixCalculator("");
					runners.add(a.new ParallelDistance(distance, data.get(i), data.get(j)));
				}
			}
			if (!runners.isEmpty()) {
				// process all and empty the list
				for (ParallelDistance pd : runners) {
					executor.execute(pd);
				}
				executor.shutdown();

				while (!executor.isTerminated()) {
				}
				for (ParallelDistance pd : runners) {
					pw.println(pd.first.stringValue(data.attribute(0)) + " " + pd.second.stringValue(data.attribute(0))
							+ " " + String.format("%.4f", pd.value));
				}
			}

			pw.close();
			System.out.println("distance matrix computed... " + arffFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ParallelDistance implements Runnable {
		FPDistance distance;
		Instance first, second;
		double value;

		public ParallelDistance(FPDistance distance, Instance first, Instance second) {
			super();
			this.distance = distance;
			this.first = first;
			this.second = second;
		}

		public void run() {
			value = distance.distance(first, second);
		}

		public double value() {
			return value;
		}
	}
}
