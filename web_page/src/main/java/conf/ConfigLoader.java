package conf;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author agomezbo
 */
public class ConfigLoader {

    public Configuration getConfig() {
        Configuration conf = new Configuration();
        try {
            File f=new File("config");
            System.out.println("************* "+f.getAbsolutePath());
            BufferedReader reader = new BufferedReader(new FileReader("config"));
            Token t;
            while ((t = nextToken(reader)) != null) {
                String fieldName = t.getName();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method = conf.getClass().getMethod(methodName, String.class);
                method.invoke(conf, t.getValue());
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conf;
    }

    protected Token nextToken(BufferedReader input) {
        Token t = null;
        try {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.matches("\\.\\..*")) {
                    String name, value;
                    name = line.substring(2, line.indexOf("="));
                    value = line.substring(line.indexOf("=") + 1);
                    //if the value is map type and it is not i one line
                    if (value.contains("{") && !value.contains("}")) {
                        boolean found = false;
                        while ((line = input.readLine()) != null && !line.contains("}")) {
                            value += line.trim();
                        }
                        if (line.contains("}")) {
                            value += line.trim();
                        } else {
                            return t;
                        }
                    }
                    return new Token(name, value);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;
    }

    class Token {

        String name, value;

        public Token(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Token{" + "name=" + name + ", value=" + value + '}';
        }

    }
}
