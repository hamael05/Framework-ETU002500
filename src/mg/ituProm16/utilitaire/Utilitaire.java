package mg.ituProm16.utilitaire;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.reflect.*;

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


    public static int verifyType(Object o){
        if(o instanceof ModelView){
            return 0;
        }
        else if(o instanceof String){
            return 0;
        }
        else{
            return 1;
        }
    }

    public static Object[] getObjectToUseAsParameter(Method method,HashMap<String,String> inputs)throws Exception{
        Parameter[] methodParams=method.getParameters();
        HashMap<String,Vector<String>> inputsPerObjects=Utilitaire.triParObject(inputs);
        Object[] objectToUseAsParameter=new Object[methodParams.length];
        int count=0;
        for(int k=0;k<methodParams.length;k++){
            if(methodParams[k].isAnnotationPresent(Param.class)){
                 Param annotation=methodParams[k].getAnnotation(Param.class);
                if(inputsPerObjects.get(annotation.value()).elementAt(0).equals("simple")==false){
                    Vector<String> listeAttributsClasse=inputsPerObjects.get(annotation.value());
                    objectToUseAsParameter[count]=Utilitaire.buildObjectFromForAnnoted(methodParams[k],listeAttributsClasse,inputs);
                    count++;
                }
                else{
                    String value=inputs.get(methodParams[k].getAnnotation(Param.class).value());
                    if(methodParams[k].getType().getSimpleName().equals("int"))
                    {
                        objectToUseAsParameter[count]=Integer.valueOf(value).intValue();
                    }
                    else if(methodParams[k].getType().getSimpleName().equals("double"))
                    {
                        objectToUseAsParameter[count]=Double.valueOf(value).doubleValue();
                    }
                    else if(methodParams[k].getType().getSimpleName().equals("String"))
                    {
                        objectToUseAsParameter[count]=value;
                    }
                    else if(methodParams[k].getType().getSimpleName().equals("Date"))
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        objectToUseAsParameter[count]=dateFormat.parse(value);
                    }
                    count++;
                }
            }
            else if(inputsPerObjects.containsKey(methodParams[k].getName()))
            {
                if(inputsPerObjects.get(methodParams[k].getName()).elementAt(0).equals("simple")==false){
                    Vector<String> listeAttributsClasse=inputsPerObjects.get(methodParams[k].getName());
                    objectToUseAsParameter[count]=buildObjectByName(methodParams[k],methodParams[k].getName(),listeAttributsClasse,inputs);
                    count++;
                }
                else{
                    String value=inputs.get(methodParams[k].getName());
                    if(methodParams[k].getType().getSimpleName().equals("int"))
                    {
                        objectToUseAsParameter[count]=Integer.valueOf(value).intValue();
                    }
                    else if(methodParams[k].getType().getSimpleName().equals("double"))
                    {
                        objectToUseAsParameter[count]=Double.valueOf(value).doubleValue();
                    }
                    else if(methodParams[k].getType().getSimpleName().equals("String"))
                    {
                        objectToUseAsParameter[count]=value;
                    }
                    else if(methodParams[k].getType().getSimpleName().equals("Date"))
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        objectToUseAsParameter[count]=dateFormat.parse(value);
                    }
                    count++;
                }
            }
        }
        return objectToUseAsParameter;
    }

    public static Object buildObjectByName(Parameter param,String paramName,Vector<String> inputsPerObjects,HashMap<String,String> inputs)throws Exception{
        Class type=param.getType();
        Constructor builder =type.getConstructor(new Class[0]);
        Object built=builder.newInstance(new Object[0]);
        for(int i=0;i<inputsPerObjects.size();i++){
            Field attribut=type.getDeclaredField(Utilitaire.getFieldName(type,inputsPerObjects.elementAt(i)));
            if(attribut.isAccessible())
            {
                String tempValue=inputs.get(paramName+":"+inputsPerObjects.elementAt(i));
                Utilitaire.setter(type,attribut,tempValue,built);
            }
            else{
                attribut.setAccessible(true);
                String tempValue=inputs.get(paramName+":"+inputsPerObjects.elementAt(i));
                Utilitaire.setter(type,attribut,tempValue,built);
                attribut.setAccessible(false);
            } 
        }
        return built;
    }

    public static String getFieldName(Class type,String supposedName){
        Field[] lsField=type.getDeclaredFields();
        for(int i=0;i<lsField.length;i++){
            if(lsField[i].isAccessible()){
                if(lsField[i].isAnnotationPresent(FieldParamer.class)){ 
                    FieldParamer annotation =lsField[i].getAnnotation(FieldParamer.class);
                    if(annotation.value().equals(supposedName))
                    {   
                        return lsField[i].getName();
                    }
                }   
            }
            else{
                lsField[i].setAccessible(true);
                if(lsField[i].isAnnotationPresent(FieldParamer.class)){ 
                    FieldParamer annotation =lsField[i].getAnnotation(FieldParamer.class);
                    if(annotation.value().equals(supposedName))
                    {   
                        return lsField[i].getName();
                    }
                } 
                lsField[i].setAccessible(false);
            }
           
        }
        return supposedName;
    } 
    public static Object buildObjectFromForAnnoted(Parameter param,Vector<String> inputsPerObjects,HashMap<String,String> inputs)throws Exception{
        Class type=param.getType();
        Constructor builder =type.getConstructor(new Class[0]);
        Object built=builder.newInstance(new Object[0]);
        for(int i=0;i<inputsPerObjects.size();i++){
            Field attribut=type.getDeclaredField(Utilitaire.getFieldName(type,inputsPerObjects.elementAt(i)));
            if(attribut.isAccessible())
            {
                String tempValue=inputs.get(param.getAnnotation(Param.class).value()+":"+inputsPerObjects.elementAt(i));
                Utilitaire.setter(type,attribut,tempValue,built);
            }
            else{
                attribut.setAccessible(true);
                String tempValue=inputs.get(param.getAnnotation(Param.class).value()+":"+inputsPerObjects.elementAt(i));
                Utilitaire.setter(type,attribut,tempValue,built);
                attribut.setAccessible(false);
            } 
        }
        return built;
    }
    public static Vector<Method> getDeclaredMethodsByName(Class type,String methodsName){
        Vector<Method> lsAns=new Vector<Method>();
        Method[] lsMethod=type.getDeclaredMethods();
        for(int i=0;i<lsMethod.length;i++){
            if(lsMethod[i].getName().equals(methodsName))   
            lsAns.add(lsMethod[i]); 
        }
        return lsAns;
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void setter(Class type,Field attribut,String value,Object built)throws Exception{
        Class[] parameterTypes=new Class[1];
        parameterTypes[0]=attribut.getType();
        try{
            if(attribut.getType().getSimpleName().equals("Date")){
                Object[] lsParams=new Object[1];
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                lsParams[0]=dateFormat.parse(value);
                Method toUse=type.getDeclaredMethod("set"+Utilitaire.capitalizeFirstLetter(attribut.getName()),parameterTypes);
                toUse.invoke(built,lsParams);
                    return;
            }
            if(attribut.getType().getSimpleName().equals("String")){
                Object[] lsParams=new Object[1];
                lsParams[0]=value;
                Method toUse=type.getDeclaredMethod("set"+Utilitaire.capitalizeFirstLetter(attribut.getName()),parameterTypes);
                toUse.invoke(built,lsParams);
                return;
            }
            if(attribut.getType().getSimpleName().equals("int")){
                Object[] lsParams=new Object[1];
                lsParams[0]=Integer.valueOf(value).intValue();
                Method toUse=type.getDeclaredMethod("set"+Utilitaire.capitalizeFirstLetter(attribut.getName()),parameterTypes);
                toUse.invoke(built,lsParams);
                return;
            }
            if(attribut.getType().getSimpleName().equals("double")){
                Object[] lsParams=new Object[1];
                lsParams[0]=Double.valueOf(value).doubleValue();
                Method toUse=type.getDeclaredMethod("set"+Utilitaire.capitalizeFirstLetter(attribut.getName()),parameterTypes);
                toUse.invoke(built,lsParams);
                return;
            }
        }
        catch(IllegalArgumentException e){
            throw new IllegalArgumentException("une valeur que vous avez envoyez,n'a pas le bon type ou format");
        }
        
    }
    public static void setter(Field attribut,String value,Object built)throws Exception{

        if(attribut.getType().getSimpleName().equals("String")){
            attribut.set(built,value);
            return;
        }
        try{
             attribut.setInt(built,Integer.valueOf(value).intValue());
             return;
        }
        catch(Exception e)
        {
            attribut.setDouble(built,Double.valueOf(value).doubleValue());
             return;
        }
    }


    public static HashMap<String,Vector<String>> triParObject(HashMap<String, String> inputs)throws Exception{
        HashMap<String,Vector<String>> ansTries=new HashMap();
        Vector<String> tabKeys=Utilitaire.getKeys(inputs);
        for(int i=0;i<tabKeys.size();i++){
            String tempKey=((tabKeys.elementAt(i)).split(":"))[0];
            Vector<String> tempValues=new Vector<String>();
            if(!ansTries.containsKey(tempKey)){
                try
                {
                    tempValues.add((tabKeys.elementAt(i).split(":"))[1]); 
                    for(int j=i+1;j<tabKeys.size();j++){
                        if(tabKeys.elementAt(j).split(":")[0].equals(tempKey)){
                            tempValues.add((tabKeys.elementAt(j).split(":"))[1]);
                        } 
                    }
                }
                catch(Exception e){
                    tempValues.add("simple");    
                }
                ansTries.put(tempKey,tempValues);
            }
        }
        return ansTries;
    }

    public static Vector<String> getKeys(HashMap<String, String> inputs)throws Exception{
        Set<String> keys = inputs.keySet();
        Vector<String> tabKeys=new Vector<String>();
        for (String key : keys){
            tabKeys.add(key);
        }
        return tabKeys;
    }
    
    public static Method getMethodToUse (String key, Method[] lsMethod) throws Exception {
        for (int i = 0; i < lsMethod.length; i++) {
            if (lsMethod[i].isAnnotationPresent(Get.class) ) {
                Get annotation = lsMethod[i].getAnnotation(Get.class);
                if (annotation.value().equals(key)) {
                    return lsMethod[i];
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
                // Parameter[] myParameters = myMethod.getParameters();
                Object[] methodAttributs = Utilitaire.getObjectToUseAsParameter(myMethod,parameters);
            
                
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