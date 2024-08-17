package com.anagrams;

import com.framework.Utils.Pair;
import com.framework.AJob;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.regex.Pattern;

/**
 * Concrete implementation of AJob that processes a text file to find and return anagrams.
 * 
 * This job reads a file, filters words based on specific criteria, 
 * ("You should ignore all words of less than four characters, and those containing non-alphabetic characters")
 * and returns a stream of key-value pairs where the key is the ciao form of the word (anagram) and the value is the word itself.
 * 
 * @author Faxy
 */
public class FileJob extends AJob<String, String> {
    private final String fileName;

    /**
     * Constructor to initialize the job with the file name.
     * 
     * @param fileName - The path of the file to process
     */
    public FileJob(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Executes the job and processes the file to find anagrams.
     * 
     * Reads the file, filters words, and returns a stream of key-value pairs where
     * the key is the sorted word (anagram) and the value is the word itself.
     * 
     * @return A stream of key-value pairs representing anagrams
     */
    @Override
    public Stream<Pair<String, String>> execute() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            Stream<Pair<String, String>> result = reader.lines()
                .flatMap(line -> Pattern.compile("\\W+").splitAsStream(line)) // Split line into words
                .map(word -> word.toLowerCase()) // Convert words to lowercase
                .filter(word -> word.length() >= 4 && word.chars().allMatch(Character::isAlphabetic)) // Filter short and non-alphabetic words
                .map(word -> new Pair<>(canonicalForm(word), word)); // Create key-value pair with sorted word
            
            // Wrap the stream with onClose to close the reader when the stream is closed
            return result.onClose(() -> {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    /**
     * Sorts the characters of a string.
     * 
     * @param word -> the string to be sorted
     * @return The sorted string
     */
    private String canonicalForm(String word) {
        return word.chars()
            .sorted()
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}