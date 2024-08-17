package com.mycompany.jobscheduler.Strategies;

import com.mycompany.jobscheduler.Utils.AJob;

import java.util.stream.Stream;

/**
 * Abstract class EmitStrategy.
 * 
 * This abstract class provides a method to generate a stream of jobs, which are instances of AJob.
 * Concrete subclasses must implement this method to define how jobs are created and emitted.
 * 
 * @param <K> - Type of keys for the jobs
 * @param <V> - Type of values for the jobs
 * 
 * @author Faxy
 */
public interface EmitStrategy<K, V> {
    
    /**
     * Emits a stream of jobs based on the strategy implementation.
     * 
     * This abstract method must be implemented by subclasses to create and return a stream of AJob instances. 
     * 
     * @return A stream of AJob instances
     */
    Stream<AJob<K, V>> emit();
}
