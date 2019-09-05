package fingerprint.clustering.util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import fingerprint.clustering.search.Scenario;

public class ResultsWriter {
	public static void createRESFileMinStats(String arffFile, List<Scenario> scenarios) throws Exception {
		String header = "DataName,Algorithm,K,Identifiability,NormalizedIdentifiability,Disruption,GlobalDisruption,Deviation,DevicesPerCluster,FPPerCluster,ClusterMinFPs,ClusterMinDevices";
		PrintWriter pw = new PrintWriter(new FileWriter(arffFile.replace(".arff", ".res")));
		pw.println(header);
		for (Scenario s : scenarios) {
			pw.println(s.getStats());
		}
		pw.close();

	}
}
