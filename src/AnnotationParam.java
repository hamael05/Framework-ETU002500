package annotation ;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER) 
public @interface AnnotationParam {
    String name() default ""; // Attribut optionnel "name" avec une valeur par défaut vide
}
