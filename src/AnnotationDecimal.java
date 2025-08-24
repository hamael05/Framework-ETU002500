package annotation ;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) 
public @interface AnnotationDecimal{
    String min(); 
    String max(); 
}
