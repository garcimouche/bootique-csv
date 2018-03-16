package io.bootique.csv.command;

import static java.lang.String.format;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.bean.CsvToBeanBuilder;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.csv.CSVBean;
import io.bootique.csv.CSVSettings;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;

public class CSVParseCommand extends CommandWithMetadata {
    
    private static final String CSVFILE_OPTION = "csvfile";
    private Provider<CSVSettings> csvSettingsProvider;
    private Provider<Set<Consumer<String[]>>> rowListenersProviders;
    private Provider<Set<Consumer<List<String[]>>>> documentListenersProviders;
    private Provider<Map<Class<? extends CSVBean>, Consumer<List<? extends CSVBean>>>> beanListeners;
    
    private List<String[]> rows = new ArrayList<>();
    private boolean isDocumentListenerPresent;
    
    @Inject
    public CSVParseCommand(Provider<CSVSettings> csvSettings, 
            Provider<Set<Consumer<String[]>>> rowListeners,
            Provider<Set<Consumer<List<String[]>>>> documentListeners,
            Provider<Map<Class<? extends CSVBean>, Consumer<List<? extends CSVBean>>>> beanListeners            
            ) {
        super(CommandMetadata
                .builder(CSVParseCommand.class)
                .description("Parse csv")
                .addOption(
                        OptionMetadata.builder(CSVFILE_OPTION)
                        .description("csvparse command - the path to the csv file")
                        .valueRequired()
                        .shortName('f')
                        .build())
                .shortName('p')
                .build());
        this.csvSettingsProvider = csvSettings;
        this.rowListenersProviders = rowListeners;
        this.documentListenersProviders = documentListeners;
        this.beanListeners = beanListeners;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        String csvFilePath = cli.optionString(CSVFILE_OPTION);
        
        if(csvFilePath == null)
            return CommandOutcome.failed(-1, 
                    new IllegalArgumentException(format("required command option '%s' not provided",CSVFILE_OPTION)));
        
        CSVSettings csvSettings = csvSettingsProvider.get();
        isDocumentListenerPresent = !documentListenersProviders.get().isEmpty();
        Character separator = csvSettings.getSeparator().orElse(ICSVParser.DEFAULT_SEPARATOR);
        //TODO where should we put config param validation ?
        Character quote = csvSettings.getQuote().orElse(ICSVParser.DEFAULT_QUOTE_CHARACTER);
        
        try {
            if(rowListenersProviders.get().size() > 0 || documentListenersProviders.get().size() > 0)
                processLines(csvFilePath, csvSettings, separator, quote);
            if(beanListeners.get().size()>0)
                processBeans(csvFilePath, csvSettings, separator, quote);
        }
        catch (IOException | RuntimeException e) {
            return CommandOutcome.failed(-1, e);
        }

        return CommandOutcome.succeeded();
    }

    private void processBeans(String csvFilePath, CSVSettings csvSettings, Character separator, Character quote) {
        beanListeners.get().entrySet().stream().forEach(e -> {
           try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))){
                    
                    List<? extends CSVBean> beans = 
                            new CsvToBeanBuilder<CSVBean>(reader)
                                .withType(e.getKey())
                                .withSeparator(separator)
                                .withQuoteChar(quote)
                                .withSkipLines(csvSettings.getSkipLines())
                                .build()
                                .parse();
                    e.getValue().accept(beans);
                    
           }
           catch (IOException ioe) {
               throw new RuntimeException(ioe);
           }
        });
    }

    private void processLines(String csvFilePath, CSVSettings csvSettings, Character separator, Character quote) throws IOException {
        final CSVParser parser = new CSVParserBuilder()
                                        .withSeparator(separator)
                                        .withQuoteChar(quote)
                                        .build();
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
             CSVReader csvRead = new CSVReaderBuilder(reader)
                                         .withCSVParser(parser)
                                         .withSkipLines(csvSettings.getSkipLines())
                                         .build();
                ){
            //call back listeners interleaving results
            csvRead.forEach(this::processSingleLine);
            documentListenersProviders.get().forEach(l -> l.accept(rows));
        }
    }
    
    private void processSingleLine(String[] row){
        rowListenersProviders.get().forEach(l -> l.accept(row));
        if(isDocumentListenerPresent)
            rows.add(row);
    }
    

}
