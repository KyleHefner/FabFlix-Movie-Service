package edu.uci.ics.khefner.service.idm.models;

import java.util.Map;

/*
    This is the POJO that Jackson will attempt to map a YAML configuration file to. Each grouping of parameters in
    the YAML config file correspond to a Map<ValidateStringEntry,ValidateStringEntry> within this model. If the YAML config file were expanded to
    include additional parameter groupings, then this model would need to be expanded to a Map<ValidateStringEntry,ValidateStringEntry> for that
    grouping. Remember that Jackson can only perform a mapping if their exists a Map<ValidateStringEntry,ValidateStringEntry> of the same name as
    the name for each of the parameter groupings in the config file. Jackson also needs getters and setters for each of
    the Maps. For HW1, you will need to expand on this class to include extracting configurations for the database
    connection parameters.
 */
public class ConfigsModel {
    private Map<String, String> serviceConfig;
    private Map<String, String> loggerConfig;
    private Map<String, String> databaseConfig;
    private Map<String, String> sessionConfig;

    public ConfigsModel() { }

    public Map<String, String> getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(Map<String, String> serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public Map<String, String> getLoggerConfig() {
        return loggerConfig;
    }

    public void setLoggerConfig(Map<String, String> loggerConfig) {
        this.loggerConfig = loggerConfig;
    }

    public Map<String, String> getDatabaseConfig() {return databaseConfig; }

    public void setDatabaseConfig(Map<String, String> databaseConfig){this.databaseConfig = databaseConfig; }

    public Map<String, String> getSessionConfig() {return sessionConfig; }

    public void setSessionConfig(Map<String, String> sessionConfig) { this.sessionConfig = sessionConfig; }
}
