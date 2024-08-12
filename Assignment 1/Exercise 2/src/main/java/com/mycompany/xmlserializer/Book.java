package com.mycompany.xmlserializer;

/**
 * Book class used for testing.
 * 
 * It can be XML serialized.
 * 
 * @author Faxy
 */
@XMLable
public class Book {
    @XMLfield(type = "String")
    public String title;
    
    @XMLfield(type = "String", name = "author")
    public String authorName;
    
    @XMLfield(type = "double")
    private double price;
    
    @XMLfield(type = "double", name = "stars")
    private double rating;

    public Book() {}

    public Book(String title, String authorName, double price, double rating) {
        this.title = title;
        this.authorName = authorName;
        this.price = price;
        this.rating = rating;
    }
}