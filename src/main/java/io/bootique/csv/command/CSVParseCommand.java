package io.bootique.csv.command;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    private Provider<Set<Consumer<String[]>>> rowListenersProviders;
    private Provider<Set<Consumer<List<String[]>>>> documentListenersProviders;
    
    private List<String[]> rows = new ArrayList<>();
    private boolean isDocumentListenerPresent;
    
    @Inject
    public CSVParseCommand(Provider<CSVSettings> csvSettings, 
            Provider<Set<Consumer<String[]>>> rowListeners,
            Provider<Set<Consumer<List<String[]>>>> documentListeners) {
        super(CommandMetadata
                .builder(CSVParseCommand.class)
                .description("Parse csv")
                .build());
        this.csvSettingsProvider = csvSettings;
        this.rowListenersProviders = rowListeners;
        this.documentListenersProviders = documentListeners;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        CSVSettings csvSettings = csvSettingsProvider.get();
        isDocumentListenerPresent = !documentListenersProviders.get().isEmpty();
        //TODO where should we put config param validation ?
        final CSVParser parser = new CSVParserBuilder()
                                        .withSeparator(csvSettings.getSeparator().orElse(ICSVParser.DEFAULT_SEPARATOR))
                                        .withQuoteChar(csvSettings.getSeparator().orElse(ICSVParser.DEFAULT_QUOTE_CHARACTER))
                                        .build();
        try (Reader reader = Files.newBufferedReader(Paths.get(csvSettings.getCsvFilePath()));
             CSVReader csvRead = new CSVReaderBuilder(reader).withCSVParser(parser).build();
                ){
            //call back listeners interleaving results
            csvRead.forEach(this::process);
            documentListenersProviders.get().forEach(l -> l.accept(rows));
        }
        catch (IOException e) {
            return CommandOutcome.failed(-1, e);
        }
        return CommandOutcome.succeeded();
    }
    
    private void process(String[] row){
        rowListenersProviders.get().forEach(l -> l.accept(row));
        if(isDocumentListenerPresent)
            rows.add(row);
    }
    

}
