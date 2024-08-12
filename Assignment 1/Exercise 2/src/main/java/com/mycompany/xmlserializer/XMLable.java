package com.mycompany.xmlserializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annonation for informations about the class.
 * 
 * The presence of this annotation says that the objects of this class should be serialized
 * 
 * @author Faxy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XMLable {
}
