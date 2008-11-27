package com.fastiga.util;

import java.util.*;
import javax.servlet.*;

/**
 * Encapsulates interactions with the various polaris config files and the polaris file system.
 *
 * <p>
 * Typically, the polaris config tree looks like this:</p>
 *
 * <p>
 * POLARIS_HOME/polaris.properties<br/>
 * POLARIS_HOME/POLARIS_SUBSYSTEM/APP_NAME/config.properties<br/>
 * CATALINA_HOME/webapps/APP_NAME.war/WEB-INF/classes/app.properties</p>
 *
 * <p>
 * The PolarisProperties class makes these files and their contained property
 * values available via getXXXProperty() methods.</p>
 *
 * <p>
 * An example:</p>
 *
 * <p>
 * ServletContext sc = servletContextEvent.getServletContext();<br/>
 * PolarisProperties pp = new PolarisProperties(sc);<br/>
 * String appName = pp.getAppName(); //read app.name from CATALINA_HOME/webapps/APP_NAME.war/WEB-INF/classes/app.properties<br/>
 * //or<br/>
 * String appName2 = pp.getAppProperty("app.name");<br/>
 * String polarisHome = pp.getAppProperty("polaris.home");<br/>
 * String appConfigHome = pp.getConfigHome();</p>
 *
 * <p>
 * //To get the value of a property foo.bar stored in POLARIS_HOME/POLARIS_SUBSYSTEM/APP_NAME/config.properties<br/>
 * String fooBar = pp.getConfigProperty("foo.bar");</p>
 * */

public class IVRProperties {
    private Properties appProperties;

    private String appName;
    private String appHome;
    private String appPackage;
    
    private ServletContext servletContext;
    
    public IVRProperties(ServletContext sc)
    {
        this.servletContext = sc;

        this.loadAppProperties(sc);

        try {
            this.appName = this.getAppProperty("app.name");
            this.appHome = this.getAppProperty("app.home", "/etc/fastagi");
            this.appPackage = this.getAppProperty("app.package");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void loadAppProperties(ServletContext sc)
    {
        this.appProperties = PropertyFile.loadProperties(sc, "app.properties");
    }

    /** 
     * Get application property from app.properties or throw exception if not set.
     *
     * @return The property value if it exists else will throw an exception.
     * @param key The name of the property in app.properties
     * @throws PolarisApplicationException {@link com.polaris.core.PolarisApplicationException}
     * */
    public String getAppProperty(String key) throws Exception
    {
        if(this.appProperties == null) {
            this.loadAppProperties(this.servletContext);
        }

        return PropertyFile.getProperty(this.appProperties, key);
    }

    /** 
     * Get application property from app.properties or return default value.
     *
     * @return The property value if it exists or the default value.
     * @param key The name of the property in app.properties
     * @param def The default value to be returned if <b>key</b> is not found 
     * in app.properties
     * */
    public String getAppProperty(String key, String def)
    {
        if(this.appProperties == null) {
            this.loadAppProperties(this.servletContext);
        }

        return PropertyFile.getProperty(this.appProperties, key, def);
    }

    /**
     * Get the unique application name stored.
     * Read from app.properties in the servlet application folder or war archive.
     *
     * @return APP_NAME
     * */
    public String getAppName() 
    {
        return appName;
    }

    /**
     * Get the value of POLARIS_HOME.
     * Read from app.properties in the servlet application folder or war archive.
     * @return POLARIS_HOME
     * */
    public String getAppHome()
    {
        return appHome;
    }

    public String getAppPackage()
    {
        return appPackage;
    }
}
