package mitigation.fp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.*;

import com.opencsv.CSVParser;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import ua_parser.Client;
import ua_parser.Parser;

public class FPController {

    private String id;
    private String os;
    private String browser;
    private String platformJs;
    private String dntJs;
    private String timezoneJs;
    private String resolutionJs;
    private String pluginsJs;
    private String localJs;
    private String sessionJs;
    private String adBlock;
    private String vendorWebGljs;
    private String rendererWebGljs;
    private String fontsJs;
    private String canvasJs;
    private String acceptHttp;
    private String encodingHttp;
    private String languageHttp;
    private String userAgentHttp;
    private String orderHttp;// check name***
    private String cookiesJs; // all FPs collected by <bcom> cookies were
    // accepted
    private int counter;

    private String encoding;

    private String[] attributes;
    // stores the indices of attributes
    HashMap<String, Integer> attIdxMap;
    HashMap<String, Integer> attDomainMap;

    public static final int NOMINAL = 0;
    public static final int ORDERED_LIST = 1;
    public static final int TUPLE = 2;
    public static final int SET = 3;

    private double[] entropyValues;// = { 0.0, 0.489, 1.922, 0.096, 4.437,
    // 10.281, 0.042, 0.042, 1.820, 5.278,
    // 6.967,8.043, 0.776, 0.153, 2.559, 6.323,
    // 1.521 };
    // attribute order and attributes to be compared are in method fpIdxMap()
    private double[] weights;
    private boolean weighted;

    public static char DEFAULT_SEPARATOR = ',';
    public static char DEFAULT_QUOTE = '\'';

    public FPController(String fp) throws Exception {
        if (fp.isEmpty() || fp.equals("") || fp == null) {
            throw new Exception("Empty string representation");
        }
        @SuppressWarnings("deprecation")
        CSVParser parser = new CSVParser(DEFAULT_SEPARATOR, DEFAULT_QUOTE);
        attributes = parser.parseLine(fp);

        id = attributes[0];
        os = attributes[1];
        browser = attributes[2];
        platformJs = attributes[3];
        dntJs = attributes[4];
        timezoneJs = attributes[5];
        resolutionJs = attributes[6];
        pluginsJs = attributes[7];
        sessionJs = localJs = attributes[8];
        adBlock = attributes[9];
        vendorWebGljs = attributes[10];
        rendererWebGljs = attributes[11];
        fontsJs = attributes[12];
        canvasJs = attributes[13];
        acceptHttp = attributes[14];
        encodingHttp = attributes[15];
        languageHttp = attributes[16];
        userAgentHttp = attributes[17];
        orderHttp = attributes[18];
        if (attributes.length > 19) {
            if (attributes[19].matches("\\d+")) {
                counter = Integer.parseInt(attributes[19]);
            } else {
                counter = -1;
            }
        }

        cookiesJs = "yes";
        double[] entrValues = {0.0, 0.0, 0.0, 0.489, 1.922, 0.096, 4.437, 10.281, 0.042, 0.042, 0, 0, 6.967, 0, 0, 0,
            2.559, 6.323, 0, 0};
        entropyValues = entrValues;
        weight(entropyValues);
        fpIdxMap();
        attDomainMap();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getPlatformJs() {
        return platformJs;
    }

    public void setPlatformJs(String platformJs) {
        this.platformJs = platformJs;
    }

    public String getDntJs() {
        return dntJs;
    }

    public void setDntJs(String dntJs) {
        this.dntJs = dntJs;
    }

    public String getTimezoneJs() {
        return timezoneJs;
    }

    public void setTimezoneJs(String timezoneJs) {
        this.timezoneJs = timezoneJs;
    }

    public String getResolutionJs() {
        return resolutionJs;
    }

    public void setResolutionJs(String resolutionJs) {
        this.resolutionJs = resolutionJs;
    }

    public String getPluginsJs() {
        return pluginsJs;
    }

    public void setPluginsJs(String pluginsJs) {
        this.pluginsJs = pluginsJs;
    }

    public String getLocalJs() {
        return localJs;
    }

    public void setLocalJs(String localJs) {
        this.localJs = localJs;
    }

    public String getSessionJs() {
        return sessionJs;
    }

    public void setSessionJs(String sessionJs) {
        this.sessionJs = sessionJs;
    }

    public String getAdBlock() {
        return adBlock;
    }

    public void setAdBlock(String adBlock) {
        this.adBlock = adBlock;
    }

    public String getVendorWebGljs() {
        return vendorWebGljs;
    }

    public void setVendorWebGljs(String vendorWebGljs) {
        this.vendorWebGljs = vendorWebGljs;
    }

    public String getRendererWebGljs() {
        return rendererWebGljs;
    }

    public void setRendererWebGljs(String rendererWebGljs) {
        this.rendererWebGljs = rendererWebGljs;
    }

    public String getFontsJs() {
        return fontsJs;
    }

    public void setFontsJs(String fontsJs) {
        this.fontsJs = fontsJs;
    }

    public String getCanvasJs() {
        return canvasJs;
    }

    public void setCanvasJs(String canvasJs) {
        this.canvasJs = canvasJs;
    }

    public String getAcceptHttp() {
        return acceptHttp;
    }

    public void setAcceptHttp(String acceptHttp) {
        this.acceptHttp = acceptHttp;
    }

    public String getEncodingHttp() {
        return encodingHttp;
    }

    public void setEncodingHttp(String encodingHttp) {
        this.encodingHttp = encodingHttp;
    }

    public String getLanguageHttp() {
        return languageHttp;
    }

    public void setLanguageHttp(String languageHttp) {
        this.languageHttp = languageHttp;
    }

    public String getUserAgentHttp() {
        return userAgentHttp;
    }

    public void setUserAgentHttp(String userAgentHttp) {
        this.userAgentHttp = userAgentHttp;
    }

    public String getOrderHttp() {
        return orderHttp;
    }

    public void setOrderHttp(String orderHttp) {
        this.orderHttp = orderHttp;
    }

    public String getCookiesJs() {
        return cookiesJs;
    }

    public void setCookiesJs(String cookiesJs) {
        this.cookiesJs = cookiesJs;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public FPController(String id, String os, String browser, String platformJs, String dntJs, String timezoneJs,
            String resolutionJs, String pluginsJs, String localJs, String sessionJs, String adBlock,
            String vendorWebGljs, String rendererWebGljs, String fontsJs, String canvasJs, String acceptHttp,
            String encodingHttp, String languageHttp, String userAgentHttp, String orderHttp, String cookiesJs,
            int counter) {
        super();
        this.id = id;
        this.os = os;
        this.browser = browser;
        this.platformJs = platformJs;
        this.dntJs = dntJs;
        this.timezoneJs = timezoneJs;
        this.resolutionJs = resolutionJs;
        this.pluginsJs = pluginsJs;
        this.localJs = localJs;
        this.sessionJs = sessionJs;
        this.adBlock = adBlock;
        this.vendorWebGljs = vendorWebGljs;
        this.rendererWebGljs = rendererWebGljs;
        this.fontsJs = fontsJs;
        this.canvasJs = canvasJs;
        this.acceptHttp = acceptHttp;
        this.encodingHttp = encodingHttp;
        this.languageHttp = languageHttp;
        this.userAgentHttp = userAgentHttp;
        this.orderHttp = orderHttp;
        this.cookiesJs = cookiesJs;
        this.counter = counter;
        weighted = false;
    }

    public HashMap<String, Integer> fpIdxMap() {
        attIdxMap = new HashMap<String, Integer>();

        attIdxMap.put("id", 0);
        attIdxMap.put("os", 1);
        attIdxMap.put("browser", 2);
        attIdxMap.put("platformJs", 3);
        attIdxMap.put("dntJs", 4);
        attIdxMap.put("timezoneJs", 5);
        attIdxMap.put("resolutionJs", 6);
        attIdxMap.put("pluginsJs", 7);
        attIdxMap.put("localJs", 8);
        // fpIdxMap.put("sessionJs", -1);
        attIdxMap.put("adBlock", 9);
        attIdxMap.put("vendorWebGljs", 10);
        attIdxMap.put("rendererWebGljs", 11);
        attIdxMap.put("fontsJs", 12);
        attIdxMap.put("canvasJs", 13);
        attIdxMap.put("acceptHttp", 14);
        attIdxMap.put("encodingHttp", 15);
        attIdxMap.put("languageHttp", 16);
        attIdxMap.put("userAgentHttp", 17);
        attIdxMap.put("orderHttp", 18);// check name***
        attIdxMap.put("counter", 19);
        return attIdxMap;
    }

    public HashMap<String, Integer> attDomainMap() {
        attDomainMap = new HashMap<String, Integer>();

        attDomainMap.put("id", NOMINAL);
        attDomainMap.put("os", NOMINAL);
        attDomainMap.put("browser", NOMINAL);
        attDomainMap.put("platformJs", NOMINAL);
        attDomainMap.put("dntJs", NOMINAL);
        attDomainMap.put("timezoneJs", NOMINAL);
        attDomainMap.put("resolutionJs", NOMINAL);
        attDomainMap.put("pluginsJs", TUPLE);
        attDomainMap.put("localJs", NOMINAL);
        // attDomainMap.put("sessionJs", -1);
        attDomainMap.put("adBlock", NOMINAL);
        attDomainMap.put("vendorWebGljs", NOMINAL);
        attDomainMap.put("rendererWebGljs", NOMINAL);
        attDomainMap.put("fontsJs", SET);
        attDomainMap.put("canvasJs", NOMINAL);
        attDomainMap.put("acceptHttp", NOMINAL);
        attDomainMap.put("encodingHttp", NOMINAL);
        attDomainMap.put("languageHttp", ORDERED_LIST);
        attDomainMap.put("userAgentHttp", NOMINAL);
        attDomainMap.put("orderHttp", NOMINAL);// check name***
        attDomainMap.put("counter", NOMINAL);
        return attDomainMap;
    }

    public String getAttByName(String attName) {
        return attributes[attIdxMap.get(attName)];
    }

    public double compare(FPController that) throws Exception {
        double dist = 1;
        if (that == null) {
            return dist;
        }
        if (!weighted) {
            // all attributes have the same weight, attributes
            // "id","os","browser" and "counter" are never taken into account
            double[] w = new double[20];
            Arrays.fill(w, 1);
            w[0] = w[1] = w[2] = w[19] = 0;
            weight(w);
        }
        dist = 0;
        if ((dist = userAgentDist(userAgentHttp, that.getUserAgentHttp())) == 1) {
            return 1;
        }
        dist = weights[attIdxMap.get("userAgentHttp")] * dist;
        String v1, v2;
        double w, local_dist = 0;
        for (Map.Entry<String, Integer> att : attDomainMap.entrySet()) {
            v1 = getAttByName(att.getKey());
            v2 = that.getAttByName(att.getKey());
            w = weights[attIdxMap.get(att.getKey())];
            if (w > 0) {
                switch (att.getValue()) {
                    case NOMINAL:
                        if (!att.getKey().equals("userAgentHttp")) {
                            local_dist = nominalDist(v1, v2);
                        }
                        break;
                    case ORDERED_LIST:
                        local_dist = languageDist(v1, v2);
                        break;
                    case SET:
                        local_dist = fontDist(v1, v2);
                        break;
                    case TUPLE:
                        local_dist = pluginDist(v1, v2);
                        break;
                }
            }
            dist += w * local_dist;
        }
        return dist;
    }

    public double[] getEntropyValues() {
        return entropyValues;
    }

    public void setEntropyValues(double[] entropyValues) {
        this.entropyValues = entropyValues;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    /**
     * This method computes the weights of attributes based on entropy values,
     * entropy value equal 0 (zero) means that the attribute is not weighted and
     * it will not be compared length of array 20
     *
     * @param array of entropy values
     */
    public void weight(double[] entropy) {
        entropyValues = entropy;
        weights = new double[entropy.length];

        double total = 0;
        for (double d : entropy) {
            total += d;
        }
        for (int i = 0; i < weights.length; i++) {
            weights[i] = entropy[i] / total;
        }
        weighted = true;
    }

    public double nominalDist(String v1, String v2) {
        double val = (v1.equals(v2)) ? 0 : 1;
        return val;
    }

    public double fontDist(String lf1, String lf2) {

        if (lf1.equals(lf2)) {
            return 0;
        }
        if (lf1.equals("") || lf2.equals("")) {
            return (lf1.endsWith(lf2)) ? 0 : 1;
        }
        double dist = 1;
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

        return dist;
    }

    public double userAgentDist(String ua1, String ua2) throws Exception {

        if (ua1.equals("") || ua2.equals("")) {
            return (ua1.equals(ua2)) ? 0 : 1;
        }

        if (ua1.equals(ua2)) {
            return 0;
        }

        Parser p = new Parser();
        Client c1 = p.parse(ua1), c2 = p.parse(ua2);

        double dist = 1;

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

        return dist;
    }

    public double pluginDist(String plugin1, String plugin2) {

        if (plugin1.equals("") || plugin2.equals("")) {
            return (plugin1.equals(plugin2)) ? 0 : 1;
        }
        if (plugin1.equals(plugin2)) {
            return 0;
        }

        double dist = 1;
        Set<Plugin> lp1 = new HashSet<Plugin>(), lp2 = new HashSet<Plugin>();

        lp1 = getListPlugin(plugin1);
        lp2 = getListPlugin(plugin2);

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

        noCommonElem = Math.abs(noCommonElem);
        double factor = 0.5;

        dist = (double) (noCommonElem + eNameDifVersion * factor) / (lp1.size() + lp2.size());
        return dist;
    }

    /*
	 * the list of plugins in the private data 0@@@@ | <Integer>@@<Plugin>+@@
	 * <Plugin>=<String>~<String>~<String>~<String>@
     */
    public Set<Plugin> getListPlugin(String line) {
        Set<Plugin> plugins = new HashSet<Plugin>();
        Plugin plugin;
        try {
            plugin = new Plugin("", "", "");

            if (line.matches("[0-9]+@@.*") && !line.equals("0@@@@")) {

                String newLine = line.substring(line.indexOf('@') + 2, line.length() - 3);
                String[] lplugins = newLine.split("@");
                String[] vals;
                String l, name, file = "", version = "";
                for (String p : lplugins) {
                    vals = p.split("~");
                    name = plugin.extractName(vals[0]);
                    if (vals.length > 2) {
                        file = vals[2];
                    }

                    if (vals.length == 4) {
                        version = (vals[3].equals("undefined") || vals[3].equals("undefine")) ? ""
                                : plugin.extractVersion(vals[3]);
                    } else {
                        version = plugin.extractVersion(vals[0]);
                    }
                    try {
                        plugins.add(new Plugin(name, file, version));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else if (!line.equals("0@@@@")) {
                plugins.add(null);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return plugins;
    }

    public double languageDist(String l1, String l2) {
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

    public int numberPermutations(List<String> l1, List<String> l2) {
        int commonElem = 0;
        for (String plug : l1) {
            if (l2.contains(plug)) {
                commonElem++;
            }
        }
        return (commonElem > 0 ? --commonElem : 0) - orderedCouplesElems(l1, l2);
    }

    public int orderedCouplesElems(List<String> l1, List<String> l2) {
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

    public boolean areOrdered(String string, String string2, List<String> l2) {
        boolean are = false;
        int idx1, idx2;
        idx1 = l2.indexOf(string);
        idx2 = l2.indexOf(string2);
        if (idx1 != -1 && idx2 != -1) {
            if (idx1 < idx2) {
                are = true;
            }
        }
        return are;
    }

    class Plugin {

        public String name;
        public String file;
        String description;
        int major;
        int minor;
        String current;

        public Plugin(String name, String file, int major, int minor, String current) {
            super();
            this.name = name;
            this.file = file;
            this.major = major;
            this.minor = minor;
            this.current = current;
            description = "";
        }

        public Plugin(String name, String file, String version) throws Exception {
            super();
            this.name = name;
            this.file = file;
            current = "";
            description = "";
            major = -1;
            minor = -1;
            if (!version.equals("") && !version.equals(" ") && matchVersion(version)) {
                // extract info
                String[] n = version.split("\\.");
                switch (n.length) {
                    case 1:
                        major = Integer.parseInt(n[0]);
                        break;
                    case 2:
                        major = Integer.parseInt(n[0]);
                        minor = Integer.parseInt(n[1]);
                        break;
                    default:
                        major = Integer.parseInt(n[0]);
                        minor = Integer.parseInt(n[1]);
                        for (int i = 2; i < n.length - 1; i++) {
                            current += n[i] + ".";
                        }
                        current += n[n.length - 1];
                        break;
                }

            } else {
                major = minor = -1;
                current = "";
            }
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "Plugin [name=" + name + ", file=" + file + ", description=" + description + ", major=" + major
                    + ", minor=" + minor + ", current=" + current + "]";
        }

        public boolean equalNames(Plugin plugin) {
            return (plugin.name.equals(name) && plugin.file.equals(file));
        }

        // _.v > plugin.v --> 1 _.v = plugin.v --> 0 _.v < plugin.v --> -1
        public int compareVersion(Plugin plugin) throws Exception {
            if (this.major == -1 && plugin.major == -1) {
                return 0;
            }
            if (this.major == -1) {
                return -1;
            }
            if (plugin.major == -1) {
                return 0;
            }
            int val = 1;

            try {
                if (this.major > plugin.major) {
                    val = 1;
                } else if (this.major < plugin.major) {
                    val = -1;
                } else {
                    if (this.minor > plugin.minor) {
                        val = 1;
                    } else if (this.minor < plugin.minor) {
                        val = -1;
                    } else {
                        if (this.current.equals(plugin.current)) {
                            val = 0;
                        } else {

                            if (!this.current.equals("") && !plugin.current.equals("")) {
                                String[] v1 = (this.current).split("\\.");
                                String[] v2 = plugin.current.split("\\.");

                                Integer[] c1 = new Integer[v1.length], c2 = new Integer[v2.length];
                                for (int i = 0; i < v1.length; i++) {
                                    c1[i] = Integer.parseInt(v1[i]);
                                }
                                for (int i = 0; i < v2.length; i++) {
                                    c2[i] = Integer.parseInt(v2[i]);
                                }
                                for (int i = 0; i < Math.min(c1.length, c2.length); i++) {
                                    if (c1[i] > c2[i]) {
                                        val = 1;
                                        break;
                                    } else if (c1[i] < c2[i]) {
                                        val = -1;
                                        break;
                                    }
                                }
                                if (c1.length == c2.length) {
                                    val = 0;
                                } else if (c1.length > c2.length) {
                                    val = 1;
                                } else {
                                    val = -1;
                                }
                            } else {
                                if (this.current.equals("") && plugin.current.equals("")) {
                                    val = 0;
                                } else if (this.current.equals("")) {
                                    val = -1;
                                } else {
                                    val = 1;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(toString());
                System.out.println(plugin.toString());
                e.printStackTrace();
            }
            return val;
        }

        @Override
        public boolean equals(Object o) {
            Plugin p = (Plugin) o;
            boolean eq = false;
            try {
                eq = this.name.equals(p.name) && compareVersion(p) == 0 && this.file.equals(p.file)
                        && this.description.equals(p.description);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return eq;
        }

        public String[] extractInfo(String plugin) {

            // 0->name 1->file 2->version
            String[] info = new String[3];

            String[] fields = plugin.split(";");
            info[0] = info[1] = info[2] = "";
            switch (fields.length) {
                case 1:
                    info[0] = plugin;
                    break;
                case 2:
                    info[0] = extractName(fields[0]);
                    info[2] = extractVersion(fields[0]);
                    break;
                case 3:
                    info[0] = extractName(fields[0]);
                    info[1] = extractFile(fields[2]);
                    info[2] = extractVersion(fields[0]);
                    if ("".equals(info[2])) {
                        info[2] = extractVersion(fields[1]);
                    }
                    break;
                case 4:
                    info[0] = extractName(fields[0]);
                    info[1] = extractFile(fields[3]);
                    info[2] = extractVersion(fields[0]);
                    if ("".equals(info[2])) {
                        info[2] = extractVersion(fields[2]);
                    }
                    break;
            }
            return info;
        }

        public String extractVersion(String string) {
            String[] tmp = string.split("\\s");
            int i = 0;
            while (i < tmp.length && !matchVersion(tmp[i])) {
                i++;
            }
            return (i < tmp.length) ? tmp[i] : "";
        }

        public String extractFile(String string) {
            if (string.length() > 0) {
                int idx = string.length() - 1;
                while (idx > 0 && string.substring(idx, idx + 1).matches("[^a-zA-Z]")) {
                    idx--;
                }
                string = string.substring(0, idx + 1);
            }
            String[] tmp = string.split("\\s");
            int i = 0;
            while (i < tmp.length && !matchFileExt(tmp[i])) {
                i++;
            }
            return (i < tmp.length) ? tmp[i] : "";
        }

        public String extractName(String string) {
            String[] tmp = string.split(" ");
            String name = "";
            int i = 0;
            while (i < tmp.length && !matchVersion(tmp[i])) {
                i++;
            }
            if (i < tmp.length) {
                int j = 0;
                for (; j < i - 1; j++) {
                    name += (tmp[j] + " ");
                }
                name += tmp[j];
            } else {
                name = string;
            }
            return name;
        }

        public boolean matchVersion(String string) {
            String typeVersion = "[0-9]{1,5}(\\.[0-9]{1,5})*";
            return string.matches(typeVersion);
        }

        public boolean matchFileExt(String string) {
            String fileExt = "[\\w-]+\\.[a-zA-Z]{2,}";
            return string.matches(fileExt);
        }

    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void addToDataBase() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String connectionUrl = "jdbc:mysql://localhost:3306/fpdatabase";
            String connectionUser = "root";
            String connectionPassword = "";
            conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
            stmt = conn.createStatement();

            //LocalDateTime time = LocalDateTime.now();
            //time = time.truncatedTo(ChronoUnit.HOURS);
            // Timestamp.valueOf(time)
            String query = "INSERT INTO fpdata (id, os, browser, time, userAgentHttp, acceptHttp, encodingHttp,languageHttp,orderHttp, pluginsJS, platformJS, cookiesJS, dntJS, timezoneJS, resolutionJS, localJS, sessionJS, canvasJS, detectedFonts, fontsJS, adBlock, vendorWebGLJS, rendererWebGLJS)" + 
                    "VALUES ('" + id + "', '" + os + "', '" + browser + "', " + "now()" + ",'" + userAgentHttp + "','" + acceptHttp + "','" + encodingHttp + "','" + languageHttp + "',"+ "'" + orderHttp + "', '" + pluginsJs + "', '" + platformJs + "', '" + cookiesJs + "', '" + dntJs + "', '" + timezoneJs + "', '" + resolutionJs + "',"+ "'" + localJs + "', '" + sessionJs + "', '" + canvasJs + "', '" + fontsJs + "', '" + fontsJs + "', '" + adBlock + "', '" + vendorWebGljs + "','" + rendererWebGljs + "')";
            System.out.println("******************************************************************************************");
            System.out.println(query);

            stmt.executeUpdate(query);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
