package ru.hse.cs.java2020.task03.config;


import org.glassfish.jersey.internal.inject.AbstractBinder;
import ru.hse.cs.java2020.task03.core.UpdateHandler;
import ru.hse.cs.java2020.task03.core.YaTrackerBotApi;

import javax.inject.Singleton;

public class DependencyBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(YaTrackerBotApi.class).to(UpdateHandler.class).in(Singleton.class);
    }
}
