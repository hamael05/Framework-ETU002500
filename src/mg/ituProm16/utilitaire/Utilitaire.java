package mg.ituProm16.utilitaire;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

import java.util.*;
import java.io.*;
import java.text.*;


import mg.ituProm16.annotation.*;

public class Utilitaire {
    public static void scan (Class<?> clazz, HashMap<String, Mapping> hashMap) throws Exception {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Get.class)){
                Get get = method.getAnnotation(Get.class);
                if(hashMap.containsKey(get.value())){
                    throw new Exception("Same URL detected");
                }
                else{
                    hashMap.put(get.value(), new Mapping(clazz.getName(), method.getName()));
                }
            }
        }
    }

    public static void scanAllClasses (List<Class<?>> classes, HashMap<String, Mapping> hashMap) throws Exception {
        for (Class<?> clazz : classes) {
            scan(clazz, hashMap);
        }
    }

    public static Method getMethodToUse (String key, Method[] isMethod) throws Exception {
        for (int i = 0; i < isMethod.length; i++) {
            if (isMethod[i].isAnnotationPresent(Get.class) ) {
                Get annotation = isMethod[i].getAnnotation(Get.class);
                if (annotation.value().equals(key)) {
                    return isMethod[i];
                }
            }
        }
        return null;
    }

    public static Object methodInvoke(HashMap<String, Mapping> hashMap, String key,HashMap<String, String> parameters) throws Exception {
        Object result = new Object();

        for (int j = 0; j < hashMap.size(); j++) {
            if (hashMap.get(key) != null) {
                Mapping mapping = hashMap.get(key);
                Class myclass = Class.forName(mapping.getClassName());
                Method[] methods = myclass.getMethods();
                Method myMethod = Utilitaire.getMethodToUse(key, methods);
                Parameter[] myParameters = myMethod.getParameters();
                Object[] methodAttributs = new Object[myParameters.length];
            
                int count = 0;
                for (int i = 0; i < myParameters.length; i++) {
                    if (myParameters[i].isAnnotationPresent(Param.class)) {
                        Param annotation = myParameters[i].getAnnotation(Param.class);
                        methodAttributs[count] = parameters.get(annotation.value());
                        count++;
                    } else if (parameters.containsKey(myParameters[i].getName())) {
                        methodAttributs[count] = parameters.get(myParameters[i].getName());
                        count++;

                    }
                }
                Object myobject = myclass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                result = myMethod.invoke(myobject, methodAttributs);
                return result;
            }
            else {
                throw new IllegalArgumentException("No URL detected");
            }
        }
        return result;
    }


}