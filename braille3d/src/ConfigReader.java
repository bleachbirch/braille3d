import jdk.nashorn.internal.runtime.regexp.joni.Config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlusslus on 31.05.2016.
 */
public class ConfigReader {

    private List<ConfigParam> configParams;

    private class ConfigParam {

        private String paramName;
        private Double paramValue;

        public ConfigParam(String paramName, String paramValue) {
            this.paramName = paramName;
            this.paramValue = Double.parseDouble(paramValue);
        }

        public String getParamName() {
            return paramName;
        }

        public Double getParamValue() {
            return paramValue;
        }
    }

    public ConfigReader(String configFile) throws IOException{

        this.configParams = new ArrayList<ConfigParam>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            if(line.compareTo("end")!=0) {
                int indexOfName = line.lastIndexOf(":");
                int indexOfValue = line.lastIndexOf(";");
                this.configParams.add(new ConfigParam(line.substring(0, indexOfName), line.substring(indexOfName + 1, indexOfValue)));
            } else {
                break;
            }
        }
        bufferedReader.close();
    }

    public double getParamByName(String name) {
        for(ConfigParam configParam:this.configParams) {
            if(configParam.getParamName().compareTo(name)==0) {
                return configParam.getParamValue();
            }
        }
        return 0;
    }
}
