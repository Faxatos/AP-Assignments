package com.mycompany.xmlserializer;

/**
 * CustomField class to represent a custom field for serialization purposes.
 * 
 * It holds both the actual field name (used for reflection) and the name used in XML, along with the type of the field.
 * 
 * @author Faxy
 */
public class CustomField {
    private final String name; // The actual name of the field in the class
    private final String xmlName; // The name to be used in the XML output
    private final String type;

    public CustomField(String name, String xmlName, String type) {
        this.name = name;
        this.xmlName = xmlName;
        this.type = type;
    }

    public String getName() {
        return name;
    }
    
    public String getXMLName() {
        return xmlName;
    }

    public String getType() {
        return type;
    }
}