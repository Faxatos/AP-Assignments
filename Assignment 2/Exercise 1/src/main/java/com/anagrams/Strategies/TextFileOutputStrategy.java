package com.anagrams.Strategies;

import com.framework.Utils.Pair;

import com.framework.Strategies.OutputStrategy;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Implements the OutputStrategy to output results in text format.
 * 
 * @author Faxy
 */
public class TextFileOutputStrategy implements OutputStrategy<String, String> {
    private final String outputFilePath;

    /**
     * Constructor to initialize output file path.
     * 
     * @param outputFilePath - Path to the output text file
     */
    public TextFileOutputStrategy(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    /**
     * Outputs the given stream of pairs to a text file.
     * 
     * This method implements the hook method for formatting output in text format.
     * 
     * @param result -> stream of pairs containing keys and associated list of values
     */
    @Override
    public void output(Stream<Pair<String, List<String>>> result) {
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            result.forEach(pair -> {
                String key = pair.getKey();
                List<String> values = pair.getValue();
                String valueCount =  key + ": " + values.size() + "\n";
                try {
                    writer.write(valueCount);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
