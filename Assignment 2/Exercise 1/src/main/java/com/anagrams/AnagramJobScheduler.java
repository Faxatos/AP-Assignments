package com.anagrams;

import com.anagrams.Strategies.CSVFileOutputStrategy;
import com.anagrams.Strategies.DirectoryEmitStrategy;
import com.anagrams.Strategies.TextFileOutputStrategy;

import com.framework.JobScheduler;

import java.util.Scanner;

/**
 * Implements a simple CLI for selecting the output format and running the job scheduler.
 * 
 * The main method demonstrates the use of the Strategy Design Pattern by allowing
 * the user to select the output strategy (text or CSV) at runtime.
 * 
 * @author Faxy
 */
public class AnagramJobScheduler extends JobScheduler<String, String> {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java AnagramJobScheduler <directory-path>");
            System.exit(1);
        }

        AnagramJobScheduler scheduler = new AnagramJobScheduler();

        // Set the Emit and Output strategies
        String directoryPath = args[0];
        scheduler.setEmitStrategy(new DirectoryEmitStrategy(directoryPath));
        scheduler.setOutputStrategy(new TextFileOutputStrategy("count_anagrams.txt"));
        
        /* Uncomment this to get a simple CLI to select output format
        Scanner scanner = new Scanner(System.in);
        boolean validChoice = false;

        while (!validChoice) {
            System.out.println("Select output format (1 for text, 2 for CSV):");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Select output format: CSV");
                    scheduler.setOutputStrategy(new TextFileOutputStrategy("count_anagrams.txt"));
                    validChoice = true;
                    break;
                case 2:
                    System.out.println("Select output format: text");
                    scheduler.setOutputStrategy(new CSVFileOutputStrategy("count_anagrams.csv"));
                    validChoice = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }*/

        // Run the scheduler
        scheduler.main();
    }
}
