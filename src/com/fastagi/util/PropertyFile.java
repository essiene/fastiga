package com.fastiga.util;

import javax.servlet.*;
import java.io.*;
import java.util.Properties;


/**
 * A collection of utility functions for working with java.util.Properties objects
 * */

public class PropertyFile
{
    /**
     * Load properties from a <b>physical</b> file on the file system
     *
     * @return The java.util.Properties instance that has been loaded and populated
     * @param path The path on the physical file system
     * @throws Exception
     * */
    public static Properties loadProperties(String path)
    {
        try {
            FileInputStream f = new FileInputStream(path);
            Properties p = new Properties();
            p.load(f);
            return p;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Save properties back to a <b>physical</b> file on the file system
     *
     * @param p The properties object to save
     * @param path The path on the physical file system
     * @throws Exception
     * */
    public static void saveProperties(Properties p, String path)
    {
	    try {
		    FileOutputStream f = new FileOutputStream(path);
		    p.save(f, null);
	    } catch (FileNotFoundException ex) {
		    ex.printStackTrace();
	    } catch (SecurityException ex) {
		    ex.printStackTrace();
	    } catch (IOException ex) {
		    ex.printStackTrace();
	    } catch (ClassCastException ex) {
		    ex.printStackTrace();
	    } catch (NullPointerException ex) {
		    ex.printStackTrace();
	    }
    }

    /**
     * Load properties from a file whose path is relative to /WEB-INF/classes in the servlets
     * web directory or web archive.
     * @return The java.util.Properties instance that has been loaded and populated
     * @param sc The ServletContext field of the Servlet. 
     * @param path The actual properties file to load. Should be relative to /WEB-INF/classes in
     * the servlet's WAR or Web directory.
     * @throws Exception
     * */
    public static Properties loadProperties(ServletContext sc, String path)
    {
        try {
            InputStream f = sc.getResourceAsStream("/WEB-INF/classes/" + path);
            Properties p = new Properties();
            p.load(f);
            return p;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /** 
     * Get property value from by key from Properties object or throw exception if not set.
     *
     * @return The property value if it exists else will throw an exception.
     * @param key The name of the property the Properties object
     * @throws PolarisApplicationException {@link com.polaris.core.PolarisApplicationException}
     * */
    public static String getProperty(Properties prop, String key) throws Exception
    {
        String p = prop.getProperty(key);
        if(p == null) {
            throw new Exception("Property '" + key + "' is not defined"); //TODO: in " + filename);
        } else {
            return p;
        }
    }
    /** 
     * Get property value from by key from Properties object or return default value
     *
     * @return The property value if it exists or the default value
     * @param key The name of the property the Properties object
     * @param def The default value to be return if <b>key</b> is not found
     * @throws PolarisApplicationException 
     * */

    public static String getProperty(Properties prop, String key, String def)
    {
        try {
            return getProperty(prop, key);
        } catch (Exception ex) {
            return def;
        }
    }
}
