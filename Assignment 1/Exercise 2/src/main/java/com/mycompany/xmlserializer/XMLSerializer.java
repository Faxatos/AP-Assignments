package com.mycompany.xmlserializer;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing static method for serialization.
 * 
 * @author Faxy
 */
public class XMLSerializer {
    
    // Cache to store class metadata to avoid introspection multiple times
    private static final Map<Class<?>, List<CustomField>> classFieldCache = new HashMap<>();
    
    // Allowed types for serialization
    private static final String[] ALLOWED_TYPES = {
        "int", "long", "double", "float", "char", "boolean", "String"
    };

    /**
     * Static method to serialize an array of Objects.
     * 
     * It checks if each object is annotated with @XMLable and retrieves fields annotated with @XMLfield to generate XML.
     * Introspection is performed only once per class.
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
            
            //System.out.println(Arrays.toString(objClass.getAnnotations())); //DEBUG print
            
            // Check if the class is marked with @XMLable
            if (objClass.isAnnotationPresent(XMLable.class)) {
                xmlContent.append("<").append(objClass.getSimpleName()).append(">\n");

                // Get the cached fields or introspect the class if it's not cached yet
                // History time: clazz has been used in Java in place of the reserved word "class" since JDK 1.0!
                List<CustomField> customFields = classFieldCache.computeIfAbsent(objClass, clazz -> {
                    List<CustomField> pairs = new ArrayList<>();
                    Field[] fields = clazz.getDeclaredFields();
                    
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(XMLfield.class)) {
                            XMLfield xmlField = field.getAnnotation(XMLfield.class);
                            // check if optional name has been used
                            String xmlName  = xmlField.name().isEmpty() ? field.getName() : xmlField.name();
                            String type = xmlField.type();
                            
                            // Check if the type is allowed
                            if (isAllowedType(type)) {
                                pairs.add(new CustomField(field.getName(), xmlName, type));
                            } else {
                                System.err.println("Field " + xmlName + " with type " + type + " is not allowed for serialization.");
                            }
                        }
                    }
                    return pairs;
                });
                
                // Iterate through cached field pairs and serialize them
                for (CustomField customField : customFields) {
                    try {
                        // Use the actual field name to access the field with reflection
                        Field field = objClass.getDeclaredField(customField.getName());
                        field.setAccessible(true);
                        Object value = field.get(obj);
                            xmlContent.append("\t<").append(customField.getXMLName())
                                      .append(" type=\"").append(customField.getType()).append("\">")
                                      .append(value)
                                      .append("</").append(customField.getXMLName()).append(">\n");
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
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
    
    private static boolean isAllowedType(String type) {
        for (String allowedType : ALLOWED_TYPES) {
            if (allowedType.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
