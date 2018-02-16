package io.bootique.csv;

import java.util.Optional;

public class CSVSettings {

    private Optional<Character> separator;
    private Optional<Character> quote;
    private int skipLines;
    
    public CSVSettings(String separator, String quote, int skipLines) {
        this.separator = getFirstChar(separator);
        this.quote = getFirstChar(quote);
        this.skipLines = skipLines;
    }

    public Optional<Character> getSeparator() {
        return separator;
    }
    
    public Optional<Character> getQuote() {
        return quote;
    }

    public int getSkipLines() {
        return skipLines;
    }
    
    private Optional<Character> getFirstChar(String separator) {
        return Optional.ofNullable(separator).map(s -> s.length() > 0 ? s.charAt(0) : null);
    }
    
}
