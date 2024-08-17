package com.mycompany.jobscheduler;

import com.mycompany.jobscheduler.Strategies.EmitStrategy;
import com.mycompany.jobscheduler.Strategies.OutputStrategy;
import com.mycompany.jobscheduler.Utils.Pair;
import com.mycompany.jobscheduler.Utils.AJob;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import java.util.Map;

/**
 * Abstract class that defines the framework for scheduling and executing jobs.
 * 
 * This class utilizes the Strategy design pattern:
 * it allows flexible and interchangeable implementations of the emit and output strategies (hot spots).
 * 
 * The following methods are considered frozen spots of the framework:
 * - main(): coordinates the execution of the framework following the assignment flow specification.
 * - compute(Stream): computes results from jobs by executing them and flattening their results.
 * - collect(Stream): collects and groups results by key into a stream of pairs where each key is associated with a list of values.
 * 
 * @param <K> - Type of keys
 * @param <V> - Type of values
 * 
 * @author Faxy
 */
public abstract class JobScheduler<K, V> {
    // Hot spots
    private EmitStrategy<K, V> emitStrategy; // Strategy for emitting jobs
    private OutputStrategy<K, V> outputStrategy; // Strategy for outputting results
    
    /**
     * Sets the strategy for emitting jobs (hot spot).
     * 
     * @param emitStrategy -> strategy to emit jobs
     */
    public void setEmitStrategy(EmitStrategy<K, V> emitStrategy) {
        this.emitStrategy = emitStrategy;
    }

    /**
     * Sets the strategy for outputting results (hot spot).
     * 
     * @param outputStrategy -> strategy to output results
     */
    public void setOutputStrategy(OutputStrategy<K, V> outputStrategy) {
        this.outputStrategy = outputStrategy;
    }

    /**
     * Computes the results of the jobs (frozen spot).
     * 
     * Executes all jobs and flattens their results into a single stream of key-value pairs.
     * 
     * @param jobs -> stream of jobs to be executed
     * @return Stream of key-value pairs obtained by executing the jobs
     */
    protected final Stream<Pair<K, V>> compute(Stream<AJob<K, V>> jobs) {
         // Flat-maps the result of executing all jobs into a single stream
        return jobs.flatMap(AJob::execute);
    }

    /**
     * Collects and groups the results by key (frozen spot).
     * 
     * Groups key-value pairs by their keys and returns a stream of pairs where each key
     * is associated with a list of values.
     * 
     * @param stream -> stream of key-value pairs to be grouped
     * @return Stream of pairs where each key is associated with a list of values
     */
    protected final Stream<Pair<K, List<V>>> collect(Stream<Pair<K, V>> stream) {
        // Grouping by key and collecting values into a list
        Map<K, List<V>> groupedMap = stream
            .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));

        // Converting map entries back to a stream of pairs
        return groupedMap.entrySet().stream()
            .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }

    /**
     * Main method to run the job scheduler.
     * 
     * This method acts coordinates the execution of the framework following this specification:
     * emit (Stream<AJob<K, V>>) -> compute (Stream<Pair<K, V>>) -> collect (Stream<Pair<K, List<V>>>) -> output
     * 
     * This method is a frozen spot of the framework, meaning it cannot be modified by subclasses.
    */
    public void main() {
        // Emit jobs
        Stream<AJob<K, V>> jobs = emitStrategy.emit();

        // Compute results from jobs
        Stream<Pair<K, V>> computedResults = compute(jobs);

        // Collect results
        Stream<Pair<K, List<V>>> result = collect(computedResults);

        // Output the final results
        outputStrategy.output(result);
    }
    
}