package io.bootique.csv;

import java.util.List;
import java.util.function.Consumer;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;

import io.bootique.ModuleExtender;

/**
 * Provides API to contribute custom extensions to {@link CSVModule}.
 */
public class CSVModuleExtender extends ModuleExtender<CSVModuleExtender> {

    private Multibinder<Consumer<String[]>> rowListeners;
    
    private Multibinder<Consumer<List<String[]>>> documentListeners;

    public CSVModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public CSVModuleExtender initAllExtensions() {
        contributeRowListeners();
        contributeDocumentListeners();
        return this;
    }

    /**
     * Attach a listener that will be called back on every single line read
     * @param lineByLine the listener
     * @return this extender instance
     */
    public CSVModuleExtender addRowListener(Consumer<String[]> lineByLine){
        contributeRowListeners().addBinding().toInstance(lineByLine);
        return this;
    }

    /**
     * Attach a listener will be called back once when the entire csv document has been read
     * @param all the listener
     * @return this extender instance
     */
    public CSVModuleExtender addDocumentListener(Consumer<List<String[]>> all){
        contributeDocumentListeners().addBinding().toInstance(all);
        return this;
    }
    
    protected Multibinder<Consumer<String[]>> contributeRowListeners() {
        return rowListeners != null ? rowListeners : (rowListeners = newSet(new Key<Consumer<String[]>>(){}));
    }
    
    protected Multibinder<Consumer<List<String[]>>> contributeDocumentListeners() {
        return documentListeners != null ? documentListeners : (documentListeners = newSet(new Key<Consumer<List<String[]>>>(){}));
    }

}
