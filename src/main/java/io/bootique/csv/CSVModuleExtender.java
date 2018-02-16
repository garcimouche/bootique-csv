package io.bootique.csv;

import java.util.function.Consumer;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;

import io.bootique.ModuleExtender;

/**
 * Provides API to contribute custom extensions to {@link CSVModule}. 
 */
public class CSVModuleExtender extends ModuleExtender<CSVModuleExtender> {

    private Multibinder<Consumer> simpleListeners;
    
    public CSVModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public CSVModuleExtender initAllExtensions() {
        contributeSimpleListeners();
        return this;
    }

    public CSVModuleExtender addSimpleListener(Consumer<String[]> lineByLine){
        contributeSimpleListeners().addBinding().toInstance(lineByLine);
        return this;
        
    }
    
    protected Multibinder<Consumer> contributeSimpleListeners() {
        return simpleListeners != null ? simpleListeners : (simpleListeners = newSet(Consumer.class));
    }
    

}
