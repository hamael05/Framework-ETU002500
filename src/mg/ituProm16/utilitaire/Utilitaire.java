package mg.ituProm16.utilitaire;

import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Method;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

import java.util.*;
import java.io.*;
import java.text.*;
import java.lang.reflect.Method; 

import mg.ituProm16.annotation.*;

public class Utilitaire {
    public static void scan (Class<?> clazz, HashMap<String, Mapping> hashMap) throws Exception {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Get.class)){
                Get get = method.getAnnotation(Get.class);
                if (hashMap.containsKey(get.value())){
                    throw new Exception("URL en trop");
                } else {
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

    public static Object methodInvoke(HashMap<String, Mapping> hashMap, String key) throws Exception {
        Object result = new Object();

        if(hashMap.containsKey(key)){
            Mapping m = hashMap.get(key);
            Class myclass = Class.forName(m.getClassName());
            Object myobject = myclass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Method method = myclass.getDeclaredMethod(m.getMethodName(), new Class[0]);
            result=method.invoke(myobject, new Object[0]) ;
        } 
        else{
            throw new IllegalArgumentException("No URL found for key ");
        }
        return result;
    }


}