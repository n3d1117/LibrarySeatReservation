package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    /**
     * Parse and return the .properties file as a Properties object
     * @return the Properties object
     * @throws IOException if a .properties file is not found
     */
    public static Properties getProperties() throws IOException {
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = ConfigProperties.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputStream == null)
            throw new FileNotFoundException("Configuration file '" + propFileName + "' not found in the classpath");
        prop.load(inputStream);
        inputStream.close();
        return prop;
    }
}