package io.bootique.csv;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import com.google.inject.Module;

import io.bootique.BQModuleProvider;
import io.bootique.BQModule.Builder;

public class CSVModuleProvider implements BQModuleProvider{

    @Override
    public Module module() {
        return new CSVModule();
    }

    @Override
    public Map<String, Type> configs() {
        //TODO understand this
        return Collections.singletonMap("csv", CSVFactory.class);
    }
 
    @Override
    public Builder moduleBuilder() {
        return BQModuleProvider.super.moduleBuilder().description("Bootique CSV module");
    }
    
}
