package annotation ;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) 
public @interface AnnotationGet {
    String name() default ""; // Attribut optionnel "name" avec une valeur par défaut vide
}
