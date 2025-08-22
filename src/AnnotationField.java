package annotation ;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) 
public @interface AnnotationField {
    String name() default ""; 
}
