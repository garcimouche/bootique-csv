package io.bootique.csv.command;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Consumer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.csv.CSVSettings;
import io.bootique.meta.application.CommandMetadata;

public class CSVParseCommand extends CommandWithMetadata {
    
    private Provider<CSVSettings> csvSettingsProvider;
    private Provider<Set<Consumer>> listenersProviders;
    
    @Inject
    public CSVParseCommand(Provider<CSVSettings> csvSettings, Provider<Set<Consumer>> listeners) {
        super(CommandMetadata
                .builder(CSVParseCommand.class)
                .description("Parse csv")
                .build());
        this.csvSettingsProvider = csvSettings;
        this.listenersProviders = listeners;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        CSVSettings csvSettings = csvSettingsProvider.get();
        Set<Consumer> listeners = listenersProviders.get();
        //TODO where should we put config param validation ?
        final CSVParser parser = new CSVParserBuilder()
                                        .withSeparator(csvSettings.getSeparator().orElse(ICSVParser.DEFAULT_SEPARATOR))
                                        .withQuoteChar(csvSettings.getSeparator().orElse(ICSVParser.DEFAULT_QUOTE_CHARACTER))
                                        .build();
        try (Reader reader = Files.newBufferedReader(Paths.get(csvSettings.getCsvFilePath()));
             CSVReader csvRead = new CSVReaderBuilder(reader).withCSVParser(parser).build();
                ){
            //call back appropriate handler
            listeners.forEach(csvRead::forEach);
        }
        catch (IOException e) {
            return CommandOutcome.failed(-1, e);
        }
        return CommandOutcome.succeeded();
                
        
    }

}
