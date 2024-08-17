package com.framework.Strategies;

import com.framework.Utils.Pair;

import java.util.List;
import java.util.stream.Stream;

/**
 * Abstract class OutputStrategy.
 * 
 * This abstract class provides a method to output a stream of results, which are key-value pairs (each key is associated with a list of values).
 * Concrete subclasses must implement this method to define how results are formatted and written to the specified output format.
 * 
 * @param <K> - Type of keys in the results
 * @param <V> - Type of values in the results
 * @author Faxy
 */
public interface OutputStrategy<K, V> {
    
    /**
     * Outputs the given stream of pairs to a designated output format.
     * 
     * This abstract method must be implemented by subclasses to handle writing a stream of key-value pairs to the specified output format.
     * 
     * @param result - A stream of pairs containing keys and associated list of values
     */
    void output(Stream<Pair<K, List<V>>> result);
}
