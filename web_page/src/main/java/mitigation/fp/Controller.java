package mitigation.fp;

import conf.ConfigLoader;
import conf.Configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

public class Controller {

    String dataDirectory;
    Configuration config;
    FPEntity fp;
    FPEntity recommendedFP;

    public Controller() {
        ConfigLoader configLoader = new ConfigLoader();
        config = configLoader.getConfig();
    }

    public FPEntity getRecpommendation(FPEntity fp) {
        String os = "", br = "";
        FPController fp1 = null;
        boolean supported = true;
        FPEntity recommended = new FPEntity();
        try {
            //TODO: change path to execution time   

            dataDirectory = config.getDataPath();
            FPController fprint = new FPController(fp.toString());
            //add fp to table
            System.out.println("");
            UAParser parser = new UAParser(fp.getUserAgentHttp());

            HashMap<String, String> osbyDefault = config.getDefaultOS();

            os = parser.getOS() + ((parser.getOSVersion().equals("") ? "" : "_" + parser.getOSVersion()));
            br = parser.getBrowser() + ((parser.getBrowserVersion().equals("") ? "" : "_" + parser.browserVersion));

            String filename = os.toLowerCase().replaceAll(" ", "") + "-" + parser.getBrowser().toLowerCase() + ".csv";
            String file = dataDirectory + filename;

            File csvFile = new File(file);

            if (!csvFile.exists()) {
                filename = osbyDefault.get(os.toLowerCase().replaceAll(" ", "").split("_")[0]) + "-" + parser.getBrowser().toLowerCase() + ".csv";
                file = dataDirectory + filename;
                csvFile = new File(file);
                System.out.println(os + " " + br + " " + filename + " " + file);

                if (!csvFile.exists()) {
                    supported = false;
                }
            }
            if (supported) {
                BufferedReader reader = new BufferedReader(new FileReader(dataDirectory + filename));
                String line;
                double val, min = Double.MAX_VALUE;
                String minFp = "";

                while ((line = reader.readLine()) != null) {
                    fp1 = new FPController(line);
                    val = fprint.compare(fp1);
                    if (val < min) {
                        min = val;
                        minFp = line;
                    }
                }
                fp1 = new FPController(minFp);

                recommended = new FPEntity(config.getEmptyValue(), os, br,
                        fp1.getPlatformJs(), fp1.getDntJs(), fp1.getTimezoneJs(),
                        fp1.getResolutionJs(), fp1.getPluginsJs(), fp1.getLocalJs(),
                        fp1.getSessionJs(), fp1.getAdBlock(), "Affected by virtualization",//"Affected by virtualization", 
                        "Affected by virtualization", fp1.getFontsJs(), "Affected by virtualization",
                        // "Affected by virtualization",
                        "HTTP Protocol value", "HTTP Protocol value", fp1.getLanguageHttp(),
                        fp1.getUserAgentHttp(), "HTTP Protocol value", "yes");

                
                fprint.setId(fp1.getId());
                fprint.addToDataBase();
                fp1.addToDataBase();

            } else {
                recommended.setOs(os);
                recommended.setBrowser(br);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(recommended.toString());
        return recommended;
    }

    public void writeToFile(FPEntity fp) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("C:\\Users\\agomezbo\\Documents\\PhD\\MitigationProject\\FProtec\\fp"));
            pw.println("id:" + "e");
            pw.println("os:" + fp.getOs());
            pw.println("browser:" + fp.getBrowser());
            pw.println("platformJs:" + fp.getPlatformJs());
            pw.println("dntJs:" + fp.getDntJs());
            pw.println("timezoneJs:" + fp.getTimezoneJs());
            pw.println("resolutionJs:" + fp.getResolutionJs());
            pw.println("pluginsJs:" + fp.getPluginsJs());
            pw.println("localJs:" + fp.getLocalJs());
            pw.println("sessionJs:" + fp.getSessionJs());
            pw.println("adBlock:" + fp.getAdBlock());
            pw.println("vendorWebGljs:" + "Affected by virtualization");
            pw.println("rendererWebGljs:" + "Affected by virtualization");
            pw.println("fontsJs:" + fp.getFontsJs());
            pw.println("canvasJs:" + "Affected by virtualization");
            pw.println("acceptHttp:" + "HTTP Protocol value");
            pw.println("encodingHttp:" + "HTTP Protocol value");
            pw.println("languageHttp:" + fp.getLanguageHttp());
            pw.println("userAgentHttp:" + "HTTP Protocol value");
            pw.println("orderHttp:" + "HTTP Protocol value");
            pw.println("cookiesJs:" + "yes");
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
