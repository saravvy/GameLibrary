package app.utils;



import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Purpose: Utility class to read properties from a file
 * Author: Thomas Hartmann
 */
public class Utils {
    public static void main(String[] args) {
        System.out.println(getPropertyValue("db.name", "config.properties"));
        System.out.println(getPropertyValue("ISSUER", "config.properties"));
    }
    public static String getPropertyValue(String propName, String resourceName)  {
        // REMEMBER TO BUILD WITH MAVEN FIRST. Read the property file if not deployed (else read system vars instead)
        // Read from resources/config.properties or from pom.xml depending on the resourceName
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) { //"config.properties" or "properties-from-pom.properties"
            if (is == null) {
                throw new RuntimeException("Could not find resource: " + resourceName);
            }
            Properties prop = new Properties();
            prop.load(is);
            String value = prop.getProperty(propName);
            if (value == null || value.isBlank()) {
                throw new RuntimeException(String.format("Could not find property %s in %s", propName, resourceName));
            }
            return value;
        } catch (IOException ex) {
            throw new RuntimeException(String.format("Could not read property %s from %s", propName, resourceName), ex);
        }
    }


}
