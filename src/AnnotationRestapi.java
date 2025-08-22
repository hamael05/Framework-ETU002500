package annotation;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) 
public @interface AnnotationRestapi {
    String nameApi() default ""; // Attribut optionnel "name" avec une valeur par défaut vide
}
