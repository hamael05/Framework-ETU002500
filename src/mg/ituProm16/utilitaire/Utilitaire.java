package mg.ituProm16.utilitaire;

import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Method;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

import mg.ituProm16.annotations.*;

public class Utilitaire {
    public static void scan (Class clazz, HashMap<String, Mapping> hashMap){
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Get.class)){
                Get get = method.getAnnotation(Get.class);
                hashMap.put(get.value(), new Mapping(clazz.getName(), method.getName()));
            }
        }
    }

    public static void scanAllClasses (List<Class> classes, HashMap<String, Mapping> hashMap) {
        for (Class clazz : classes) {
            scan(clazz, hashMap);
        }
    }
}