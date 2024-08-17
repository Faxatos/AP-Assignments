package com.anagrams.Strategies;

import com.framework.Utils.Pair;

import com.framework.Strategies.OutputStrategy;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Implements the OutputStrategy to output results in CSV format.
 * 
 * @author Faxy
 */
public class CSVFileOutputStrategy implements OutputStrategy<String, String> {
    private final String outputFilePath;

    /**
     * Constructor to initialize output file path.
     * 
     * @param outputFilePath - Path to the output CSV file
     */
    public CSVFileOutputStrategy(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    /**
     * Outputs the given stream of pairs to a CSV file.
     * 
     * This method implements the hook method for formatting output in CSV format.
     * 
     * @param result -> stream of pairs containing keys and associated list of values
     */
    @Override
    public void output(Stream<Pair<String, List<String>>> result) {
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            // Write CSV header
            writer.write("Key,Count\n");

            // Write each pair to the CSV file
            result.forEach(pair -> {
                String key = pair.getKey();
                int count = pair.getValue().size();
                String csvLine = key + "," + count + "\n";
                try {
                    writer.write(csvLine);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
