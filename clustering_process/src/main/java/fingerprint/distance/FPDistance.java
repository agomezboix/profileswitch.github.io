package fingerprint.distance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ua_parser.Client;
import ua_parser.Parser;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NormalizableDistance;
import weka.core.converters.ArffLoader;
import weka.core.neighboursearch.PerformanceStats;

public class FPDistance extends NormalizableDistance {

	protected double[] m_weights;
	protected int dataset = 2;
	int weightMethod = 1;
	// first attribute-->id
	double[] entropy = { 0.0, 0.489, 1.922, 0.096, 4.437, 10.281, 0.042, 0.042, 1.820, 5.278, 6.967, 8.043, 0.776,
			0.153, 2.559, 6.323, 1.521 };
	Instances dat;
	protected String file = "";
	public boolean setWeights = false;
	public boolean useMatrix;

	HashMap<String, HashMap<String, Float>> matrix;

	public double[] getWeights() {
		return m_weights;
	}

	public void setDataSet(Instances data) {
		m_Data = data;
		dat = new Instances(data);
	}

	public void setInstances(String inputFile) throws IOException {
		File f = new File(inputFile);
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(inputFile));
		m_Data = loader.getDataSet();
		dat = new Instances(m_Data);
		file = inputFile;
	}

	public boolean loadMatrix() throws Exception {
		if (file.equals("")) {
			throw new Exception("the file has not been set");
		}
		return loadMatrix(FingerprintReader.getAbsoluteMatrixFileName((new File(file)).getAbsolutePath()));
	}

	// load matrix (HashMap: id1 id2 distance_value) from .mtx file
	public boolean loadMatrix(String inputFile) {
		boolean success;
		try {
			File f = new File(inputFile);
			if (f.exists() && inputFile.endsWith(".mtx")) {
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				String line, d[];
				matrix = new HashMap<String, HashMap<String, Float>>();

				String k1, k2;
				Float v;

				while ((line = br.readLine()) != null) {
					d = line.split(" ");
					k1 = d[0];
					k2 = d[1];
					v = Float.parseFloat(d[2]);

					if (!matrix.containsKey(k1)) {
						matrix.put(k1, new HashMap<String, Float>());
					}
					matrix.get(k1).put(k2, v);
				}
				br.close();
				success = true;
				useMatrix = true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		return success;
	}

	public FPDistance() {
		super();
		useMatrix = false;
	}

	public FPDistance(Instances data) {
		super();
		useMatrix = false;
		m_Data = data;
		dat = new Instances(data);
		m_weights = new double[m_Data.numAttributes()];

		if (!setWeights) {
			switch (weightMethod) {
			case 1:
				double total = 0;
				for (double d : entropy) {
					total += d;
				}
				for (int i = 0; i < m_weights.length; i++) {
					m_weights[i] = entropy[i] / total;
				}
				break;
			default:
				for (int i = 0; i < m_weights.length; i++) {
					m_weights[i] = 1.0 / m_weights.length;
				}
				break;
			}
			setWeights = true;
		}
	}

	public void setweights(int numAttributes) {
		m_weights = new double[numAttributes];

		for (int i = 0; i < m_weights.length; i++) {
			m_weights[i] = 1.0 / m_weights.length;
		}
		setWeights = true;
	}

	public void setEntropyWeights(double[] ent) {
		entropy = ent;
		m_weights = new double[entropy.length];

		double total = 0;
		for (double d : entropy) {
			total += d;
		}
		for (int i = 0; i < m_weights.length; i++) {
			m_weights[i] = entropy[i] / total;
		}
		setWeights = true;
		//for (double ww : m_weights)
			//System.out.println(ww);
	}

	public String getRevision() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String globalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double updateDistance(double currDist, double diff) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double distance(Instance first, Instance second) {
		double dist;

		try {
			if (useMatrix) {
				String i, j;
				i = first.stringValue(0);
				j = second.stringValue(0);
				if (i.equals(j)) {
					dist = 0;
				} else {
					int ii, ij;
					ii = Integer.parseInt(i);
					ij = Integer.parseInt(j);
					if (ii > ij) {
						String tmp = i;
						i = j;
						j = tmp;
					}
					// existen las llaves
					try {
						if (matrix.containsKey(i) && matrix.get(i).containsKey(j)) {
							// System.out.println(ii+ " "+ij+">>>");
							dist = matrix.get(i).get(j);
						} else {
							dist = distance(first, second, Double.POSITIVE_INFINITY);
						}
					} catch (Exception e) {
						// System.out.println(" " + i + " " + j);
						dist = distance(first, second, Double.POSITIVE_INFINITY);
					}
				}
			} else {
				dist = distance(first, second, Double.POSITIVE_INFINITY);
			}
		} catch (Exception e) {
			return 1;
		}
		return dist;
	}

	@Override
	public double distance(Instance first, Instance second, double cutOffValue) {
		return distance(first, second, cutOffValue, null);
	}

	/**
	 * Write description
	 */
	@Override
	public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
		double distance = 0;
		int numAttributes = first.numAttributes();
		//
		if (!setWeights) {
			setweights(numAttributes);
		}
		double val = 0;
		String name;

		for (int i = 0; i < numAttributes; i++) {
			if (m_weights[i] != 0) {
				switch (first.attribute(i).type()) {
				case Attribute.NOMINAL:
					val = nominalDist(first.stringValue(i), second.stringValue(i));

					/*
					 * Plugins, fonts, languages and user agent will be treated
					 * as STRING, they will be identified by the name *
					 */

					name = first.attribute(i).name();

					if (name.equals("user_agent") || name.equals("userAgent") || name.equals("UserAgent")) {
						try {
							val = userAgentDist(first.stringValue(i), second.stringValue(i));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if (name.equals("plugins") || name.equals("list_of_plugins") || name.equals("Plugins")) {
						val = pluginDist(first.stringValue(i), second.stringValue(i));
					}

					if (name.equals("fonts") || name.equals("available_fonts") || name.equals("Fonts")) {
						val = fontDist(first.stringValue(i), second.stringValue(i));
					}

					if (name.equals("language") || name.equals("content language") || name.equals("ContentLanguage")) {
						val = languageDist(first.stringValue(i), second.stringValue(i));
					}
					break;
				case Attribute.STRING:
					name = first.attribute(i).name();
					if (name.equals("plugins") || name.equals("list_of_plugins") || name.equals("Plugins")) {
						val = pluginDist(first.stringValue(i), second.stringValue(i));
					}
					break;
				}
				distance += m_weights[i] * val;
			}
		}
		return distance;
	}

	protected double nominalDist(String v1, String v2) {
		double val = (v1.equals(v2)) ? 0 : 1;
		return val;
	}

	protected double fontDist(String lf1, String lf2) {

		if (lf1.equals(lf2)) {
			return 0;
		}
		if (lf1.equals("") || lf2.equals("")) {
			return (lf1.endsWith(lf2)) ? 0 : 1;
		}
		double dist = 1;

		switch (dataset) {
		// for amiunique font
		case 1:
			Set<String> s1 = new HashSet<String>(Arrays.asList(lf1.split(" "))),
					s2 = new HashSet<String>(Arrays.asList(lf2.split(" ")));
			if (s1.equals(s2)) {
				dist = 0;
			} else {
				Set<String> union = new HashSet<String>();
				union.addAll(s1);
				union.addAll(s2);
				Set<String> intersection = new HashSet<String>();
				intersection.addAll(s1);
				intersection.retainAll(s2);

				int lunion = union.size();
				union.removeAll(intersection);
				dist = ((double) union.size()) / lunion;
			}
			break;

		case 2:
			int eq = 0;
			if (lf1.length() != lf2.length()) {
				return 1;
			}
			for (int i = 0; i < lf1.length(); i++) {
				if (lf1.charAt(i) == lf2.charAt(i)) {
					eq++;
				}
			}
			dist = ((double) (lf1.length() - eq)) / lf1.length();
			break;
		}

		return dist;
	}

	protected double userAgentDist(String ua1, String ua2) throws Exception {

		if (ua1.equals("") || ua2.equals("")) {
			return (ua1.equals(ua2)) ? 0 : 1;
		}

		if (ua1.equals(ua2))
			return 0;

		Parser p = new Parser();
		Client c1 = p.parse(ua1), c2 = p.parse(ua2);

		double dist = 1;

		/*
		 * System.out.println(c1.device + " " + c1.os + " " +
		 * c1.userAgent.family); System.out.println(c2.device + " " + c2.os +
		 * " " + c2.userAgent.family);
		 * System.out.println(c1.device.equals(c2.device) + " " +
		 * c1.os.equals(c2.os) + " " +
		 * c1.userAgent.family.equals(c2.userAgent.family));;
		 */

		if (c1.device.family.equals(c2.device.family) && c1.os.family.equals(c2.os.family)
				&& ((c1.os.major == null && c2.os.major == null) || (c1.os.major.equals(c2.os.major)
						&& ((c1.os.minor == null && c2.os.minor == null) || c1.os.minor.equals(c2.os.minor))))
				&& c1.userAgent.family.equals(c2.userAgent.family)) {

			if (c1.userAgent.major != null && c2.userAgent.major != null && c1.userAgent.minor != null
					&& c2.userAgent.minor != null) {
				if (c1.userAgent.major.equals(c2.userAgent.major) && c1.userAgent.minor.equals(c2.userAgent.minor)) {
					dist = 0.25;
				} else {
					dist = 0.5;
				}
			} else {
				dist = 0.5;
			}

		}
		// if (dist != 1)
		// System.out.println(dist+"\n"+ua1+"\n"+ua2+"\n");
		return dist;
	}

	protected double pluginDist(String plugin1, String plugin2) {

		if (plugin1.equals("") || plugin2.equals("")) {
			return (plugin1.equals(plugin2)) ? 0 : 1;
		}
		if (plugin1.equals(plugin2)) {
			return 0;
		}

		double dist = 1;
		Set<Plugin> lp1 = new HashSet<Plugin>(), lp2 = new HashSet<Plugin>();

		switch (dataset) {
		// for amiunique font
		case 1:
			// lp1 = getListPluginAMIUNIQUE(plugin1);
			// lp2 = getListPluginAMIUNIQUE(plugin2);
			break;
		// our dataset
		case 2:
			lp1 = getListPlugin(plugin1);
			lp2 = getListPlugin(plugin2);
			break;
		// our dataset
		default:
			lp1 = getListPlugin(plugin1);
			lp2 = getListPlugin(plugin2);

			break;
		}

		int equalName = 0;
		int eNameDifVersion = 0;

		if (lp1.contains(null) || lp2.contains(null)) {
			return 1;
		}

		for (Plugin p1 : lp1) {
			for (Plugin p2 : lp2) {
				if (p1.equalNames(p2)) {
					equalName++;
					try {
						if (p1.compareVersion(p2) != 0) {
							eNameDifVersion++;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		int noCommonElem = lp1.size() + lp2.size() - equalName * 2;
		// there are list of plugins containing the same plugins but woth diff
		// version
		if (noCommonElem < 0) {
			Plugin[] t1 = new Plugin[lp1.size()], t2 = new Plugin[lp2.size()];
			lp1.toArray(t1);
			lp2.toArray(t2);
			for (int i = 0; i < t1.length; i++) {
				for (int j = i + 1; j < t1.length; j++) {
					if (t1[i].equalNames(t1[j])) {
						noCommonElem += 1;
					}
				}
			}
			for (int i = 0; i < t2.length; i++) {
				for (int j = i + 1; j < t2.length; j++) {
					if (t2[i].equalNames(t2[j])) {
						noCommonElem += 1;
					}
				}
			}
		}

		/*
		 * if (noCommonElem < 0) { for (Plugin i : lp1) {
		 * System.out.print(i.name + i.file + " >> "); } System.out.println();
		 * for (Plugin i : lp2) { System.out.print(i.name + i.file + " >> "); }
		 * System.out.println();
		 * 
		 * System.out.println(noCommonElem + " " + lp1.size() + " " + lp2.size()
		 * + " " + equalName + "\n"); }
		 */
		noCommonElem = Math.abs(noCommonElem);
		double factor = 0.5;

		dist = (double) (noCommonElem + eNameDifVersion * factor) / (lp1.size() + lp2.size());

		// if(dist!=1)System.out.println(x);
		return dist;
	}

	/*
	 * the list of plugins in the private data 0@@@@ | <Integer>@@<Plugin>+@@
	 * <Plugin>=<String>~<String>~<String>~<String>@
	 */
	protected Set<Plugin> getListPlugin(String line) {
		Set<Plugin> plugins = new HashSet<Plugin>();

		if (line.matches("[0-9]+@@.*") && !line.equals("0@@@@")) {

			String newLine = line.substring(line.indexOf('@') + 2, line.length() - 3);
			String[] lplugins = newLine.split("@");
			String[] vals;
			String l, name, file = "", version = "";
			for (String p : lplugins) {
				vals = p.split("~");
				name = Plugin.extractName(vals[0]);
				if (vals.length > 2) {
					file = vals[2];
				}

				if (vals.length == 4) {
					version = (vals[3].equals("undefined") || vals[3].equals("undefine")) ? ""
							: Plugin.extractVersion(vals[3]);
				} else {
					version = Plugin.extractVersion(vals[0]);
				}
				try {
					plugins.add(new Plugin(name, file, version));
				} catch (Exception e) {
					// System.out.println(p);
				}
			}

		} else if (!line.equals("0@@@@")) {
			plugins.add(null);
		}
		return plugins;
	}

	protected double languageDist(String l1, String l2) {
		double dist = 1;
		List<String> ll1 = new ArrayList<String>();
		List<String> ll2 = new ArrayList<String>();

		for (String l : l1.split(",")) {
			String tmp = l.split(";")[0];
			ll1.add(tmp);
		}

		for (String l : l2.split(",")) {
			String tmp = l.split(";")[0];
			ll2.add(tmp);
		}

		int commonElem = 0;
		for (String lan : ll1) {
			if (ll2.contains(lan)) {
				commonElem++;
			}
		}

		int noCommonElem = ll1.size() + ll2.size() - commonElem * 2;

		//
		double factor = 0.5;

		// *** *** **********************************************************
		// distance function for languages comparison
		dist = (double) (noCommonElem + numberPermutations(ll1, ll2) * factor) / (ll1.size() + ll2.size());
		return dist;
	}

	protected int numberPermutations(List<String> l1, List<String> l2) {
		int commonElem = 0;
		for (String plug : l1) {
			if (l2.contains(plug)) {
				commonElem++;
			}
		}
		return (commonElem > 0 ? --commonElem : 0) - orderedCouplesElems(l1, l2);
	}

	protected int orderedCouplesElems(List<String> l1, List<String> l2) {
		int ordered = 0;

		for (int i = 0; i < l1.size() - 1; i++) {
			if (areOrdered(l1.get(i), l1.get(i + 1), l2)) {
				ordered++;
			}
		}

		// return (ordered > 0) ? (++ordered) : 0;

		// with that i am counting ordered couples
		return ordered;
	}

	protected boolean areOrdered(String string, String string2, List<String> l2) {
		boolean are = false;
		int idx1, idx2;
		idx1 = l2.indexOf(string);
		idx2 = l2.indexOf(string2);
		if (idx1 != -1 && idx2 != -1) {
			if (idx1 < idx2)
				are = true;
		}
		return are;
	}

	public Instances getInstances() {
		return dat;
	}
}
