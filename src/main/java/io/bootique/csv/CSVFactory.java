package io.bootique.csv;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

/**
 * Provides configuration settings for csv process 
 * @author franck
 */
@BQConfig("Configures CSV Reader/Writer")
public class CSVFactory {
    
    private String separator;
    
    private String quote;
    
    private int skipLines;
    
    CSVSettings createCSVSettings() {
        return new CSVSettings(separator, quote, skipLines);
    }
    
    @BQConfigProperty("Separator character if different from default (i.e. comma [,])")
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @BQConfigProperty("Quote character if different from default (i.e. double quote [''])")
    public void setQuote(String quote) {
        this.quote = quote;
    }

    @BQConfigProperty("The number of lines to skip")
    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
    }
    
}
