package edu.uci.ics.khefner.service.idm.configs;

import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import edu.uci.ics.khefner.service.idm.models.ConfigsModel;

public class Configs {
    // Default service configs
    private final String DEFAULT_SCHEME = "http://";
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int    DEFAULT_PORT = 3990;
    private final String DEFAULT_PATH = "/api/idm";
    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "idm.log";

    // Default database configs
    //Erase these because don't need a default connection to a database
    private final String DEFAULT_DBUSERNAME = "cs122b_db39";
    private final String DEFAULT_DBPASSWORD = "Kfizzle1212";
    private final String DEFAULT_DBHOSTNAME = "localhost";
    private final int DEFAULT_DBPORT = 3306;
    private final String DEFAULT_DBDRIVER = "";
    private final String DEFAULT_DBNAME = "basicdb";
    private final String DEFAULT_DBSETTINGS = "";

    // Default session configs
    private final long DEFAULT_EXPIRATION = 1800000;
    private final long DEFAULT_TIMEOUT = 600000;


    // Service configs
    private String scheme;
    private String hostName;
    private int    port;
    private String path;

    // Logger configs
    private String outputDir;
    private String outputFile;

    //Database configs
    private  String dbUsername;
    private  String dbPassword;
    private  String dbHostname;
    private  int    dbPort;
    private  String dbDriver;
    private  String dbName;
    private  String dbSettings;

    //Session configs
    private long timeout;
    private long expiration;

    public Configs() {
        scheme = DEFAULT_SCHEME;
        hostName = DEFAULT_HOSTNAME;
        port = DEFAULT_PORT;
        path = DEFAULT_PATH;
        outputDir = DEFAULT_OUTPUTDIR;
        outputFile = DEFAULT_OUTPUTFILE;
        dbUsername = DEFAULT_DBUSERNAME;
        dbPassword = DEFAULT_DBPASSWORD;
        dbHostname = DEFAULT_HOSTNAME;
        dbPort = DEFAULT_DBPORT;
        dbDriver = DEFAULT_DBDRIVER;
        dbName = DEFAULT_DBNAME;
        dbSettings = DEFAULT_DBSETTINGS;
        expiration = DEFAULT_EXPIRATION;
        timeout = DEFAULT_TIMEOUT;

    }

    public Configs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException("Unable to create Configs from ConfigsModel.");
        } else {
            // Set service configs
            scheme = cm.getServiceConfig().get("scheme");
            if (scheme == null) {
                scheme = DEFAULT_SCHEME;
                System.err.println("Scheme not found in configuration file. Using default.");
            } else {
                System.err.println("Scheme: " + scheme);
            }

            hostName = cm.getServiceConfig().get("hostName");
            if (hostName == null) {
                hostName = DEFAULT_HOSTNAME;
                System.err.println("Hostname not found in configuration file. Using default.");
            } else {
                System.err.println("Hostname: " + hostName);
            }

            port = Integer.parseInt(cm.getServiceConfig().get("port"));
            if (port == 0) {
                port = DEFAULT_PORT;
                System.err.println("Port not found in configuration file. Using default.");
            } else if (port < 1024 || port > 65536) {
                port = DEFAULT_PORT;
                System.err.println("Port is not within valid range. Using default.");
            } else {
                System.err.println("Port: " + port);
            }

            path = cm.getServiceConfig().get("path");
            if (path == null) {
                path = DEFAULT_PATH;
                System.err.println("Path not found in configuration file. Using default.");
            } else {
                System.err.println("Path: " + path);
            }

            // Set logger configs
            outputDir = cm.getLoggerConfig().get("outputDir");
            if (outputDir == null) {
                outputDir = DEFAULT_OUTPUTDIR;
                System.err.println("Logging output directory not found in configuration file. Using default.");
            } else {
                System.err.println("Logging output directory: " + outputDir);
            }

            outputFile = cm.getLoggerConfig().get("outputFile");
            if (outputFile == null) {
                outputFile = DEFAULT_OUTPUTFILE;
                System.err.println("Logging output file not found in configuration file. Using default.");
            } else {
                System.err.println("Logging output file: " + outputFile);
            }


            dbUsername = cm.getDatabaseConfig().get("dbUsername");
            if (dbUsername == null) {
                dbUsername = DEFAULT_DBUSERNAME;
                System.err.println("dbUsername not found in configuration file. Using default.");
            } else {
                System.err.println("dbUsername: " + dbUsername);
            }

            dbPassword = cm.getDatabaseConfig().get("dbPassword");
            if (dbPassword == null) {
                dbPassword = DEFAULT_DBPASSWORD;
                System.err.println("dbPassword not found in configuration file. Using default.");
            } else {
                System.err.println("Database Password found in config file: ");
            }

            dbHostname = cm.getDatabaseConfig().get("dbHostname");
            if (dbHostname == null) {
                dbHostname = DEFAULT_DBHOSTNAME;
                System.err.println("dbHostname not found in configuration file. Using default.");
            } else {
                System.err.println("dbHostname: " + dbHostname);
            }

            dbPort = Integer.parseInt(cm.getDatabaseConfig().get("dbPort"));
            if (dbPort == 0) {
                dbPort = DEFAULT_DBPORT;
                System.err.println("dbPort not found in configuration file. Using default.");
            } else if (port < 1024 || port > 65536) {
                dbPort = DEFAULT_DBPORT;
                System.err.println("dbPort is not within valid range. Using default.");
            } else {
                System.err.println("dbPort: " + dbPort);
            }

            dbDriver = cm.getDatabaseConfig().get("dbDriver");
            if (dbDriver == null) {
                dbDriver = DEFAULT_DBDRIVER;
                System.err.println("dbDriver not found in configuration file. Using default.");
            } else {
                System.err.println("dbDriver: " + dbDriver);
            }

            dbName = cm.getDatabaseConfig().get("dbName");
            if (dbName == null) {
                dbName = DEFAULT_DBNAME;
                System.err.println("dbName not found in configuration file. Using default.");
            } else {
                System.err.println("dbName: " + dbName);
            }

            dbSettings = cm.getDatabaseConfig().get("dbSettings");
            if (dbSettings == null) {
                dbSettings = DEFAULT_DBSETTINGS;
                System.err.println("dbSettings not found in configuration file. Using default.");
            } else {
                System.err.println("dbSettings: " + dbSettings);
            }

            expiration = Long.parseLong(cm.getSessionConfig().get("expiration"));
            if (expiration == 0) {
                expiration = DEFAULT_EXPIRATION;
                System.err.println("expiration not found in configuration file. Using default.");
            } else {
                System.err.println("expiration " + expiration);
            }

            timeout = Long.parseLong(cm.getSessionConfig().get("timeout"));
            if (timeout == 0) {
                timeout = DEFAULT_TIMEOUT;
                System.err.println("timeout not found in configuration file. Using default.");
            } else {
                System.err.println("timeout " + timeout);
            }
        }
    }

    public void currentConfigs() {
        ServiceLogger.LOGGER.config("Scheme: " + scheme);
        ServiceLogger.LOGGER.config("Hostname: " + hostName);
        ServiceLogger.LOGGER.config("Port: " + port);
        ServiceLogger.LOGGER.config("Path: " + path);
        ServiceLogger.LOGGER.config("Logger output directory: " + outputDir);
        ServiceLogger.LOGGER.config("Logger output file: " + outputFile);
        ServiceLogger.LOGGER.config("Database username: " + dbUsername);
        //check dbpassword
        String dbPasswordCheck = (dbPassword == null) ? "false" : "true";
        ServiceLogger.LOGGER.config("dbPassword Provided?: " + dbPasswordCheck);
        ServiceLogger.LOGGER.config("Database hostname: " + dbHostname);
        ServiceLogger.LOGGER.config("Database port: " + dbPort);
        ServiceLogger.LOGGER.config("Database driver: " + dbDriver);
        ServiceLogger.LOGGER.config("Database name: " + dbName);
        ServiceLogger.LOGGER.config("Database settings: " + dbSettings);
        ServiceLogger.LOGGER.config("timeout: " + timeout);
        ServiceLogger.LOGGER.config("expiration: " + expiration);

    }

    public String getScheme() {
        return scheme;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getDatabaseUserName(){
        return dbUsername;
    }

    public String getDatabaseHostName(){
        return dbHostname;
    }

    public String getDatabasePassword(){
        return dbPassword;
    }

    public int getDatabasePort(){
        return dbPort;
    }

    public String getDatabaseDriver(){
        return dbDriver;
    }

    public String getDatabaseName(){
        return dbName;
    }

    public String getDatabaseSettings(){
        return dbSettings;
    }

    public long getTimeout(){
        return timeout;
    }

    public long getExpiration(){
        return expiration;
    }

}
