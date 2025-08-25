
package mg.ituprom16.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.sql.Time;
import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.math.BigDecimal;
import mg.ituprom16.annotation.*;
import mg.ituprom16.exception.AllTypeFormatException;
import mg.ituprom16.exception.TypeFormatException;


public class ObjectUtil { 
    public static Object parseStringToObject(String stringValue,Class targetClass)throws Exception{
        try
        {
            if (targetClass == Integer.class || targetClass == Integer.TYPE) {
                return Integer.parseInt(stringValue);
            } else if (targetClass == Double.class || targetClass == Double.TYPE || targetClass == BigDecimal.class) {
                return Double.parseDouble(stringValue);
            } else if (targetClass == Boolean.class || targetClass == Boolean.TYPE) {
                return Boolean.parseBoolean(stringValue);
            } else if (targetClass == Date.class) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return dateFormat.parse(stringValue);
            }
            else if (targetClass == Timestamp.class) {
                stringValue = stringValue.replace("T", " ");
                if (!stringValue.contains(":")) {
                    stringValue += ":00"; // Ajouter secondes si absentes
                }
                return Timestamp.valueOf(stringValue); // Format attendu : "yyyy-MM-dd HH:mm:ss[.fff]"
            } else if (targetClass == Time.class) {
                if (stringValue.length() == 5) { // Vérifie si le format est HH:MM
                    stringValue += ":00"; // Ajoute secondes
                }
                return Time.valueOf(stringValue); // Format attendu : "HH:mm:ss"
            }
            else
            {
                throw new Exception("TYPE NOT RECOGNIZED FOR "+ targetClass.getName());
            }
        }
        catch(Exception e)
        {
            throw new Exception("Error parsing To "+targetClass.getName()+" for "+"\""+stringValue+"\"");
        }
    }
    public static boolean is_partial_attributes(String key)
    {
        String[] toCheck = key.split(".");
        if (toCheck.length>1) {
            return true;
        }
        return false;
    }
    public static void setSessionObject(Object object_controller, HttpSession session)throws Exception
    {
        Field[] fields = object_controller.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType() == MySession.class) {
                fields[i].setAccessible(true);
                fields[i].set(object_controller, new MySession(session));
                fields[i].setAccessible(false);
            }
        }
    }
    public static HashMap<String,List> prepareFields(Map<String,String> parameters)
    {
        HashMap<String,List> toReturn = new HashMap<String,List>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String[] input = key.split("\\.");
            if (input.length>1) {
                String nameObject = key.split("\\.")[0].trim();
                String nameField = key.split("\\.")[1].trim();
                if (!toReturn.containsKey(nameObject)) {
                    List<KeyValue> toInsert = new ArrayList<KeyValue>();
                    KeyValue value_key = new KeyValue(nameField, value);
                    toInsert.add(value_key);
                    toReturn.put(nameObject, toInsert);
                }
                else{
                    toReturn.get(nameObject).add(new KeyValue(nameField, value));
                }
            }
        }
        return toReturn;
    }

    public static String getKeyValue(List<KeyValue> list,String key)
    {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getKey().compareTo(key)==0) {
                return list.get(i).getValue();
            }
        }
        return null;
    }
    public static String getKey(List<KeyValue> list,String key)
    {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getKey().compareTo(key)==0) {
                return list.get(i).getValue();
            }
        }
        return null;
    }
    //verifier le field et la valeur avant d'inserer
    //Numeric or Date or Soratra
    public static TypeFormatException verify_type_field(String paramAnnotaion,Field field,Object valeur)
    {
        Annotation[] listeAnnotations = field.getAnnotations();
        String champ = field.getName();
        if (field.isAnnotationPresent(FieldAnnotation.class)) {
           champ =(field.getAnnotation(FieldAnnotation.class)).field();
        }
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < listeAnnotations.length; i++) {
            Annotation temp = listeAnnotations[i];
            if (temp instanceof Numeric) {
                int min = ((Numeric)temp).minLength();
                int max = ((Numeric)temp).maxLength();
                try
                {
                    if (max<Double.parseDouble(valeur.toString()) || min>Double.parseDouble(valeur.toString())) {
                        errors.add("Numeric value out of min and max");
                    }
                }
                catch(Exception e)
                {
                    errors.add("Numeric type error");
                }
                
            }
            if (temp instanceof DateType ) {
                try
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dateFormat.parse(valeur.toString());
                }
                catch(Exception e)
                {
                    errors.add("Date type error");
                }
                
            }
            if (temp instanceof Soratra) {
                int min = ((Soratra)temp).minLength();
                int max = ((Soratra)temp).maxLength();
                if (max<((String)valeur).length() && min>((String)valeur).length()) {
                    errors.add("String value out of Min and Max");
                }
            }
        }
        if (errors.size()!=0) {
            return new TypeFormatException(paramAnnotaion.trim()+"."+champ, errors);
        }
        return null;
    
    }
    // Invoke the object in the method to Use
    //Manana liste Valeurs (list Kely Value)
    public static Object invoke_for_object(String paramAnnotation,Object o,List<KeyValue> list_KeyValues,HttpSession session) throws Exception
    {
        List<TypeFormatException> lstypeFormatExceptions = new ArrayList<>();
        Field[] fields = o.getClass().getDeclaredFields();
        if (list_KeyValues == null)
        {
            return o;
        }
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object value = null;
            if (fields[i].isAnnotationPresent(FieldAnnotation.class)) {
                FieldAnnotation fieldAnnotation = fields[i].getAnnotation(FieldAnnotation.class);
                String string_value = getKeyValue(list_KeyValues, fieldAnnotation.field());
                if (verify_type_field(paramAnnotation,fields[i],value) == null) {
                    if (string_value != null)
                    {
                        value = parseStringToObject(string_value, fields[i].getType());    
                    }
                }
                else
                {
                    value = string_value;
                }
             
            }
            else
            {

                    String  string_value = getKeyValue(list_KeyValues,fields[i].getName());
                    if (verify_type_field(paramAnnotation,fields[i],value) == null) {
                        if (string_value != null)
                        {
                            value = parseStringToObject(string_value, fields[i].getType());
                        }
                        
                    }
                    else
                    {
                        value = string_value;
                    }
                  
            }
            if (verify_type_field(paramAnnotation,fields[i],value) != null) {
                lstypeFormatExceptions.add(verify_type_field(paramAnnotation,fields[i],value));
            }
            else{
                fields[i].set(o, parseStringToObject(value.toString(), fields[i].getType()));
                fields[i].setAccessible(false);
            }
           
        }
        if (lstypeFormatExceptions.size() != 0) {
            throw new AllTypeFormatException(lstypeFormatExceptions);    
        }
        return o;
    }
    public static boolean classic_object(Class clazz)
    {
        if (clazz == String.class || clazz == Integer.class || clazz == Double.class || clazz == Boolean.class || clazz == Date.class )
        {
            return true;
        }
        return false;
    }
    //Invoke the object by the method used
    public static Object invoke_object(Object caller,Method method,Map<String,String> parameters,HttpSession session,HttpServletRequest request) throws Exception  {
        
        HashMap<String,List> prepareField = prepareFields(parameters);
        ObjectUtil.setSessionObject(caller, session);
        List methodAttributs = new ArrayList();
        Class[] parameterType = method.getParameterTypes();
        Parameter[] methodParams = method.getParameters();
        for (int i = 0; i < methodParams.length; i++) {
            //raha type tsotra ilay objet dia alaina ao amle Map<String,String> fotsiny le valeur
            if (classic_object(parameterType[i]) == true)
            {
                if(methodParams[i].isAnnotationPresent(Match.class)){
                    Match annotation=methodParams[i].getAnnotation(Match.class);
                    methodAttributs.add(parameters.get(annotation.param()));
                }
                else{
                    throw new Exception("ETU2500: Attribut non annoté");
                }
            }
            else if (parameterType[i] == MySession.class) {
                methodAttributs.add(new MySession(session));
            }

            else if(parameterType[i] == Part.class)
            {
                if(methodParams[i].isAnnotationPresent(Match.class)){
                    Match annotation=methodParams[i].getAnnotation(Match.class);
                    methodAttributs.add(request.getPart(annotation.param()));
                }
                else{
                    throw new Exception("ETU2500: Attribut non annoté");
                }
            }
            //Ito raha ohatra ka objet le paramater anle fonction (parameter[i])
            //Alaina anaty prepare field valeur de chaque parametre du paramtre[i]
            else
            {
                List<KeyValue> keyValueList = null;
                if(methodParams[i].isAnnotationPresent(Match.class)){
                    Match annotation=methodParams[i].getAnnotation(Match.class);
                    keyValueList = prepareField.get(annotation.param());
                    Object toInvoke = parameterType[i].newInstance();
                    Object o = invoke_for_object(annotation.param(),toInvoke,keyValueList,session);
                    methodAttributs.add(o);
                }
                else {
                   throw new Exception("ETU2500 : Attribut non annoté");
                }
                
            }
        }
        Object toreturnObject = method.invoke(caller,methodAttributs.toArray());
        if (toreturnObject instanceof ModelAndView && method.isAnnotationPresent(RestApi.class)) {
            return ((ModelAndView)toreturnObject).getData();
        }
        return toreturnObject;
        
    }

}