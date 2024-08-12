package com.mycompany.xmlserializer;

/**
 * Main class used to test the XML serializer.
 * 
 * @author Faxy
 */
public class Main {
    public static void main(String[] args) {
        // Create instances
        Student student1 = new Student("Jak", "Mar", 19);
        Student student2 = new Student("Daxter", "Ottsel", 19);
        Book book1 = new Book("The Precursor Legacy", "Famitsu", 19.99, 3.9);
        Thread thread1 = new Thread();

        // Array of objects to serialize
        Object[] arr = { student1, thread1, student2, book1};

        // Serialize to XML
        XMLSerializer.serialize(arr, "output");
    }
}
