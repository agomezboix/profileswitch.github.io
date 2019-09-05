package conf;


import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author agomezbo
 */
public class Configuration {

    public String dataPath;
    public HashMap<String, String> defaultOS;
    public String emptyValue;

    public String getDataPath() {
        return dataPath;
    }

    public HashMap<String, String> getDefaultOS() {
        return defaultOS;
    }

    public String getEmptyValue() {
        return emptyValue;
    }     
    
    public void setDataPath(String datasetPATH) {
        if (datasetPATH.matches("\".*\"")) {
            this.dataPath = datasetPATH.substring(1, datasetPATH.length() - 1);
        } else {
            this.dataPath = datasetPATH;
        }
    }

    public void setDefaultOS(String osbyDefault) {
        defaultOS = new HashMap<String, String>();
        if (osbyDefault.matches("\\{.*\\}")) {
            String[] vs, values = osbyDefault.substring(1, osbyDefault.length() - 1).split(",");
            for (String entry : values) {
                vs = entry.replace("\"", "").split(":");
                defaultOS.put(vs[0], vs[1]);
            }
        }
    }

    public void setEmptyValue(String emptyValue) {
        if (emptyValue.matches("\".*\"")) {
            this.emptyValue = emptyValue.substring(1, emptyValue.length() - 1);
        } else {
            this.emptyValue = emptyValue;
        }
    }

    public Configuration() {
        dataPath = "";
        defaultOS = new HashMap<String, String>();
        emptyValue = "empty";
    }

    @Override
    public String toString() {
        return "Configuration{" + "datasetPATH=" + dataPath + ", osbyDefault=" + defaultOS + ", emptyValue=" + emptyValue + '}';
    }

}
