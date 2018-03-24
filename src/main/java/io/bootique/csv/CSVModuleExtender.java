package io.bootique.csv;

import java.util.List;
import java.util.function.Consumer;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

import io.bootique.ModuleExtender;

/**
 * Provides API to contribute custom extensions to {@link CSVModule}.
 */
public class CSVModuleExtender extends ModuleExtender<CSVModuleExtender> {

    private Multibinder<Consumer<String[]>> rowListeners;
    
    private Multibinder<Consumer<List<String[]>>> documentListeners;
    
    private MapBinder<Class<? extends CSVBean>, Consumer<List<? extends CSVBean>>> beanListeners;

    public CSVModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public CSVModuleExtender initAllExtensions() {
        contributeRowListeners();
        contributeDocumentListeners();
        contributeBeanListeners();
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

    /**
     * Attach a listener that will return a List of {@link CSVBean} instances
     * @param allBeans the callback 
     * @param beanClass the target bean class to use to read csv file 
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends CSVBean> CSVModuleExtender addBeanListener(Consumer<List<T>> allBeans, Class<T> beanClass){
        Consumer beans = allBeans;
        contributeBeanListeners().addBinding(beanClass).toInstance(beans);
        return this;
    }
    
    protected Multibinder<Consumer<String[]>> contributeRowListeners() {
        return rowListeners != null ? rowListeners : (rowListeners = newSet(new Key<Consumer<String[]>>(){}));
    }
    
    protected Multibinder<Consumer<List<String[]>>> contributeDocumentListeners() {
        return documentListeners != null ? documentListeners : (documentListeners = newSet(new Key<Consumer<List<String[]>>>(){}));
    }

    protected MapBinder<Class<? extends CSVBean>, Consumer<List<? extends CSVBean>>> contributeBeanListeners(){
        return beanListeners!=null ? beanListeners : newMap(new TypeLiteral<Class<? extends CSVBean>>(){}, new TypeLiteral<Consumer<List<? extends CSVBean>>>(){});
    }
    
}
