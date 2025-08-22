package annotation;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationController {
    String nameController() default ""; // Attribut optionnel "name" avec une valeur par défaut vide
}
