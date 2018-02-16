package io.bootique.csv;

import static java.util.Arrays.asList;

import com.google.inject.Binder;
import com.google.inject.Provides;

import io.bootique.BQCoreModule;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.csv.command.CSVParseCommand;

public class CSVModule extends ConfigModule {

    @Override
    public void configure(Binder binder) {
        asList(
                CSVParseCommand.class
        ).forEach(command -> BQCoreModule.extend(binder).addCommand(command));

        CSVModule.extend(binder).initAllExtensions();
    }

    public static CSVModuleExtender extend(Binder binder) {
        return new CSVModuleExtender(binder);
    }
    
    @Provides
    public CSVSettings createCSVSettings(ConfigurationFactory configurationFactory) {
        return configurationFactory
                .config(CSVFactory.class, configPrefix).createCSVSettings();
    }
}
