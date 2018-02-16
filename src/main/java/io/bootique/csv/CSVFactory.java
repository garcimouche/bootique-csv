package io.bootique.csv;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

@BQConfig("Configures CSV Reader/Writer")
public class CSVFactory {
    
    private String separator;
    
    private String quote;
    
    private String csvFilePath;
    
    CSVSettings createCSVSettings() {
        return new CSVSettings(separator, quote, csvFilePath);
    }
    
    @BQConfigProperty("Separator character if different from default (i.e. comma [,]).")
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @BQConfigProperty("Quote character if different from default (i.e. double quote ['']).")
    public void setQuote(String quote) {
        this.quote = quote;
    }
    
    @BQConfigProperty("The CSV File to parse.")
    public void setCsvFilePath(String path) {
        this.csvFilePath = path;
    }
}
