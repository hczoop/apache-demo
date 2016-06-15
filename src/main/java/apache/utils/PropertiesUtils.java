package apache.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    private static final Properties properties = new Properties();

    public static Properties loadProperties() {

        InputStream is = null;
        try {
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
	}
	
	
	public static String getValue(String key) {
		String value = properties.getProperty(key);
		return value;
	}


}
