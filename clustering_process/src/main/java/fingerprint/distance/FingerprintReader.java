package fingerprint.distance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import ua_parser.Client;
import ua_parser.Parser;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class FingerprintReader {
	char attributeDelimiter;
	char stringDelimiter;
	int numberAtts;
	int[] attributeIndices;

	public FingerprintReader() {
		attributeDelimiter = ';';
		stringDelimiter = '"';
	}

	public void countAndSave(String input, String output) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(input));
		String key;
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		while ((key = br.readLine()) != null) {
			if (!map.containsKey(key)) {
				map.put(key, 0);
			}
			map.put(key, map.get(key) + 1);
		}
		br.close();

		PrintWriter pw = new PrintWriter(new FileWriter(output));
		for (Map.Entry<String, Integer> e : map.entrySet()) {
			pw.println(e.getKey() + attributeDelimiter + stringDelimiter + e.getValue() + stringDelimiter);
		}
		pw.close();
	}

	public void joinScreenResolution(int starIndex, String input, String output) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(input));
		PrintWriter pw = new PrintWriter(new FileWriter(output));
		String separator = "" + stringDelimiter + attributeDelimiter + stringDelimiter;
		String line, newLine, attributes[];
		while ((line = br.readLine()) != null) {
			attributes = line.split(separator);
			newLine = "";
			for (int i = 0; i < starIndex; i++) {
				newLine += attributes[i] + separator;
			}
			newLine += attributes[starIndex] + 'x' + attributes[starIndex + 1] + 'x' + attributes[starIndex + 2]
					+ separator;
			for (int i = starIndex + 3; i < attributes.length - 1; i++) {
				newLine += attributes[i] + separator;
			}
			newLine += attributes[attributes.length - 1];
			pw.println(newLine);
		}

		br.close();
		pw.close();
	}

	/*
	 * the method adds three atts: unique id(int), OS_version and webBrowser
	 */
	public void addOSandWB(int userAgentIdx, String input, String outputFolder) throws Exception {

		String separator = "" + stringDelimiter + attributeDelimiter + stringDelimiter;
		String os, osVersion, wb, line, fileName, atts[];
		Parser uaparser = new Parser();
		Client c;
		BufferedReader br = new BufferedReader(new FileReader(input));
		HashMap<String, PrintWriter> writers = new HashMap<String, PrintWriter>();

		if (!outputFolder.endsWith("\\")) {
			outputFolder += "\\";
		}

		// unique id for fingerprints
		int counter = 0;

		while ((line = br.readLine()) != null) {
			atts = line.split(separator);
			c = uaparser.parse(atts[userAgentIdx]);
			wb = c.userAgent.family;
			// filename=os_osVersion-wb
			if (wb.matches("\\w+")) {
				os = c.os.family;
				osVersion = (c.os.major == null) ? "" : (c.os.major + ((c.os.minor == null ? "" : "." + c.os.minor)));

				fileName = os + ((osVersion.equals("")) ? "" : ("_" + osVersion)) + "-" + wb;

				if (!writers.containsKey(fileName)) {
					writers.put(fileName, new PrintWriter(new FileWriter(outputFolder + fileName)));
				}
				writers.get(fileName).println("\"" + counter++ + "\";\""
						+ (os + ((osVersion.equals("")) ? "" : ("_" + osVersion))) + "\";\"" + wb + "\";" + line);
			}
		}

		// closing writers
		for (Map.Entry<String, PrintWriter> e : writers.entrySet()) {
			e.getValue().close();
		}

		br.close();

	}

	public void convertToCSV(String inputFolder, String header) throws Exception {
		BufferedReader br;
		PrintWriter pw;

		String line;

		File folder = new File(inputFolder);
		// creating csv files, adding headers and replacing ';' separator by ','
		for (File f : folder.listFiles()) {
			if (!f.getName().endsWith(".csv") && !f.getName().endsWith(".arff")) {
				br = new BufferedReader(new FileReader(f));
				pw = new PrintWriter(new FileWriter(f.getAbsolutePath() + ".csv"));

				pw.println(header);
				while ((line = br.readLine()) != null) {
					pw.println(line.replaceAll("\";\"", "\",\""));
				}
				br.close();
				pw.close();
			}
		}
		// removing no-csv files
		folder = new File(inputFolder);
		List<String> toDelete = new ArrayList<String>();
		for (File f : folder.listFiles()) {
			if (!f.getName().endsWith(".csv")) {
				toDelete.add(f.getAbsolutePath());
			}
		}

		for (String s : toDelete) {
			File f = new File(s);
			// f.delete();
		}

		// creating arff files and removing csv files
		CSVLoader loader = new CSVLoader();
		ArffSaver saver = new ArffSaver();

		NumericToNominal toNominal = new NumericToNominal();
		NominalToString toString = new NominalToString();
		Instances data;
		File arff;
		for (File f : folder.listFiles()) {
			if (f.getName().endsWith(".csv")) {
				System.out.println(f.getName());
				if (!(new File(f.getAbsolutePath().replaceAll(".csv", ".arff"))).exists()) {
					loader.setSource(f);
					data = loader.getDataSet();

					toNominal.setInputFormat(data);
					toNominal.setAttributeIndices("first");
					data = weka.filters.Filter.useFilter(data, toNominal);

					saver.setInstances(data);
					saver.setFile(new File(f.getAbsolutePath().replaceAll(".csv", ".arff")));
					saver.writeBatch();
				}
			}
		}

	}

	public char getAttributeDelimiter() {
		return attributeDelimiter;
	}

	public void setAttributeDelimiter(char attributeDelimiter) {
		this.attributeDelimiter = attributeDelimiter;
	}

	public char getStringDelimiter() {
		return stringDelimiter;
	}

	public void setStringDelimiter(char stringDelimiter) {
		this.stringDelimiter = stringDelimiter;
	}

	public int getNumberAtts() {
		return numberAtts;
	}

	public void setNumberAtts(int numberAtts) {
		this.numberAtts = numberAtts;
	}

	public int[] getAttributeIndices() {
		return attributeIndices;
	}

	public void setAttributeIndices(int[] attributeIndices) {
		this.attributeIndices = attributeIndices;
	}

	// methods for handling file names (distance matrix, data, results, etc...)

	public static String getDataName(String fileName) {
		String name = "";
		if (fileName.endsWith(".arff")) {
			String splitter = System.getProperty("file.separator");
			splitter = (splitter.equals("\\") ? splitter += "\\" : splitter);
			String[] array = fileName.split(splitter);
			name = array[array.length - 1];
			name = name.substring(0, name.lastIndexOf('.'));
		}
		return name;
	}

	public static String getMatrixFileName(String filename) {
		String name = "";
		if (!getDataName(filename).equals("")) {
			name += ".mtx";
		}
		return name;
	}

	public static String getAbsoluteMatrixFileName(String filename) {
		String name = "";
		if (!(name = getDataName(filename)).equals("")) {
			File f = new File(filename);
			name = f.getParent() + getJavaFileSeparator() +  name + ".mtx";
		}
		return name;
	}

	public static String getResultsFileName(String filename) {
		String name = "";
		if (!getDataName(filename).equals("")) {
			name += ".res";
		}
		return name;
	}

	public static String getAbsoluteResultsFileName(String filename) {
		String name = "";
		if (!(name = getDataName(filename)).equals("")) {
			File f = new File(filename);
			String dir;
			dir = "mtx" + getJavaFileSeparator();
			name = f.getParent() + getJavaFileSeparator() + dir + name + ".res";
		}
		return name;
	}

	public static String getJavaFileSeparator() {
		String separator = System.getProperty("file.separator");
		if (separator.equals("\\")) {
			separator += "\\";
		}
		return separator;
	}

	public static String getPrivacyFileName(String filename){
		String name = "";
		if (!getDataName(filename).equals("")) {
			name += ".pdr";
		}
		return name;
	}

	public static String getAbsolutePrivacyFileName(String filename) {
		String name = "";
		if (!(name = getDataName(filename)).equals("")) {
			File f = new File(filename);
			String dir;
			dir = "mtx" + getJavaFileSeparator();
			name = f.getParent() + getJavaFileSeparator() + dir + name + ".pdr";
		}
		return name;
	}

	public static String getCentroidsFileName(String filename){
		String name = "";
		if (!getDataName(filename).equals("")) {
			name += ".ctr";
		}
		return name;
	}

	public static String getAbsoluteCentroidsFileName(String filename) {
		String name = "";
		if (!(name = getDataName(filename)).equals("")) {
			File f = new File(filename);
			String dir;
			dir = "mtx" + getJavaFileSeparator();
			name = f.getParent() + getJavaFileSeparator() + dir + name + ".ctr";
		}
		return name;
	}

}
