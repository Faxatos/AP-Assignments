package com.mycompany.xmlserializer;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing static method for serialization.
 * 
 * @author Faxy
 */
public class XMLSerializer {
    
    // Cache to store class metadata to avoid introspection multiple times
    private static final Map<Class<?>, Field[]> classFieldCache = new HashMap<>();

    /**
     * Static method to serialize an array of Objects.
     * 
     * It checks
     * 
     * @param arr array of objects to be serialized
     * @param fileName path of output file
     */
    public static void serialize(Object[] arr, String fileName) {
        StringBuilder xmlContent = new StringBuilder();

        // Append XML declaration
        xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        for (Object obj : arr) {
            Class<?> objClass = obj.getClass();
            
            // Check if the class is marked with @XMLable
            
            //System.out.println(Arrays.toString(objClass.getAnnotations())); //DEBUG print
            
            if (objClass.isAnnotationPresent(XMLable.class)) {
                xmlContent.append("<").append(objClass.getSimpleName()).append(">\n");

                // Get the cached fields or introspect the class if it's not cached yet
                Field[] fields = classFieldCache.computeIfAbsent(objClass, clazz -> clazz.getDeclaredFields());
                // History time: clazz has been used in Java in place of the reserved word "class" since JDK 1.0!
                
                // Iterate through fields and serialize the ones with @XMLfield annotation
                for (Field field : fields) {
                    if (field.isAnnotationPresent(XMLfield.class)) {
                        XMLfield xmlField = field.getAnnotation(XMLfield.class);
                        // check if optional name has been used
                        String tagName = xmlField.name().isEmpty() ? field.getName() : xmlField.name();
                        String type = xmlField.type();
                        
                        field.setAccessible(true);
                        try {
                            Object value = field.get(obj);
                            xmlContent.append("\t<").append(tagName)
                                      .append(" type=\"").append(type).append("\">")
                                      .append(value)
                                      .append("</").append(tagName).append(">\n");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                xmlContent.append("</").append(objClass.getSimpleName()).append(">\n");
            } else {
                xmlContent.append("<notXMLable />\n");
            }
        }

        // Write the content to the specified file
        try (FileWriter writer = new FileWriter(fileName + ".xml")) {
            System.out.println(xmlContent); //DEBUG print
            writer.write(xmlContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
