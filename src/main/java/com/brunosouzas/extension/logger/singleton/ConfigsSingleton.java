package com.brunosouzas.extension.logger.singleton;

import java.util.HashMap;
import java.util.Map;

import com.brunosouzas.extension.logger.internal.LoggerConfiguration;

public class ConfigsSingleton {

    private Map<String, LoggerConfiguration> configs = new HashMap<String, LoggerConfiguration>();

    public Map<String, LoggerConfiguration> getConfigs() {
        return configs;
    }

    public LoggerConfiguration getConfig(String configName) {
        return this.configs.get(configName);
    }

    public void addConfig(String configName, LoggerConfiguration config) {
        this.configs.put(configName, config);
    }

}
