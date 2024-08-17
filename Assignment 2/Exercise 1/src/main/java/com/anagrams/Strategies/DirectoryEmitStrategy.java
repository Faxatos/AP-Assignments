package com.anagrams.Strategies;

import com.anagrams.FileJob;
import com.framework.AJob;

import com.framework.Strategies.EmitStrategy;

import java.io.File;
import java.util.stream.Stream;

/**
 * Implements the EmitStrategy to emit new jobs for each text file in the given directory.
 * 
 * @author Faxy
 */
public class DirectoryEmitStrategy implements EmitStrategy<String, String> {
    private final String directoryPath;

    /**
     * Constructor to initialize directory path.
     * 
     * @param directoryPath - Path to the given directory
     */
    public DirectoryEmitStrategy(String directoryPath) {
        this.directoryPath = directoryPath;
    }

     /**
     * Emits jobs for each text file in the given directory.
     * 
     * This method walks through the directory to find .txt files,
     * and creates a new job (FileJob) for each .txt file. 
     * It returns a stream of these jobs.
     * 
     * @return A stream of AJob instances for each .txt file
     */
    @Override
    public Stream<AJob<String, String>> emit() {
        /* DEGUB code to get and print the current absolute path
        
        Path currRelativePath = Paths.get("");
        String currAbsolutePathString = currRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is - " + currAbsolutePathString);
        */
        
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("The provided path is not a directory.");
        }

        return Stream.of(dir.listFiles(file -> file.isFile() && file.getName().endsWith(".txt")))
            .map(file -> new FileJob(file.getAbsolutePath()));
    }
}
