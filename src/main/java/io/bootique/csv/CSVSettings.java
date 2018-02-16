package io.bootique.csv;

import java.util.Optional;

public class CSVSettings {

    private Optional<Character> separator;
    private Optional<Character> quote;
    private String csvFilePath;
    
    
    public CSVSettings(String separator, String quote, String csvFilePath) {
        this.separator = getFirstChar(separator);
        this.quote = getFirstChar(quote);
        this.csvFilePath = csvFilePath;
    }

    public Optional<Character> getSeparator() {
        return separator;
    }
    
    public Optional<Character> getQuote() {
        return quote;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }
    
    private Optional<Character> getFirstChar(String separator) {
        return Optional.ofNullable(separator).map(s -> s.length() > 0 ? s.charAt(0) : null);
    }
}
