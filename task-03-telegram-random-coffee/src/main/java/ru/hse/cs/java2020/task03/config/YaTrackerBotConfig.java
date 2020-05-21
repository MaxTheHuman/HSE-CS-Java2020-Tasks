package ru.hse.cs.java2020.task03.config;

import org.glassfish.jersey.server.ResourceConfig;

public class YaTrackerBotConfig extends ResourceConfig {

    public YaTrackerBotConfig() {
        register(new DependencyBinder());
        packages(true, "ru.hse.cs.java2020.task03");
    }
}
