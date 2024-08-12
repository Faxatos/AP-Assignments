package com.mycompany.xmlserializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annonation for identifying serializable fields.
 * 
 * The presence of this annotation states that the field must be serialized. 
 * The annotation has a mandatory argument type, which is the type of the field, and an optional argument name.
 * 
 * @author Faxy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XMLfield {
    String type(); // Type of the field
    String name() default ""; // Custom tag name (optional)
}
