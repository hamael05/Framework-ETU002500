package mg.ituprom16.controller;

import java.io.File;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.HashSet;
import mg.ituprom16.utils.Mapping;
import mg.ituprom16.utils.ModelAndView;
import mg.ituprom16.utils.ObjectUtil;
import mg.ituprom16.annotation.*;
import mg.ituprom16.controller.auth.AuthConfiguration;
import mg.ituprom16.exception.AllTypeFormatException;
import mg.ituprom16.exception.TypeFormatException;
import mg.ituprom16.utils.VerbMethod;

public class PackageUtils {
    public static List<Class> getAllClasses(String packageSource) throws Exception
    {
        File classpathDirectory = new File(packageSource);
        File[] listeFiles = classpathDirectory.listFiles();
        String namePackage = (packageSource.split("classes/")[1]).replace("/", ".");
        List<Class> toReturn = new ArrayList<>(); 
        if (classpathDirectory.exists()) {
            for(File file : listeFiles)
            {   
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0,file.getName().length()-6);
                    Class clazz = Class.forName(namePackage+className);
                    toReturn.add(clazz);
                }
            }   
        }
        return toReturn;
    }
    public static List<Class> getClassesByAnnotation(String packageSource,Class <? extends Annotation> annotationClass) throws Exception{
        List<Class> toReturn = new ArrayList<>(); 
        List<Class> allClasses = getAllClasses(packageSource);
        for (int i = 0; i < allClasses.size(); i++) {
            Class tempoClass = allClasses.get(i);
            if (tempoClass.isAnnotationPresent(annotationClass)) {
                toReturn.add(tempoClass);
            }
        }
        return toReturn;
    }
    public static String getStringVerb(Method method) {
        if(method.isAnnotationPresent(Post.class))
        {
            return "POST";
        }
        else{
            return "GET";
        }
    }
    public static void scan_url(Class annotedClass, HashMap<String, Mapping> map, String key)throws Exception
    {
        Method[] methods = annotedClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(mg.ituprom16.annotation.Url.class)) {
                Url annotation = methods[i].getAnnotation(mg.ituprom16.annotation.Url.class);
                if (map.containsKey(annotation.name())) {
                    map.get(annotation.name()).addVerbMethod(getStringVerb(methods[i]), methods[i].getName());
                }
                else{
                    if(annotation.name().compareTo(key) == 0){
                        HashSet<VerbMethod> list = new HashSet<VerbMethod>();
                        Mapping mapping = new Mapping(annotedClass.getName(),list);
                        mapping.addVerbMethod(methods[i].getName(), getStringVerb(methods[i]));
                        map.put(key, mapping);
                    }
                }
            }
        }
    }

    public static void scan_all_url(List<Class> listClasses , HashMap<String , Mapping> map,String key)throws Exception
    {
        for (int i = 0; i < listClasses.size(); i++) {
            scan_url(listClasses.get(i), map ,key);
        }
    }

    public static Method get_method_annoted(Class myClass,String key,String verb)throws Exception
    {
        Method[] listeMethods = myClass.getDeclaredMethods();
        for (int i = 0; i < listeMethods.length; i++) {
            if (listeMethods[i].isAnnotationPresent(Url.class)) {
                Url annotation = listeMethods[i].getAnnotation(Url.class);
                if (annotation.name().compareTo(key)==0 && getStringVerb(listeMethods[i]).compareTo(verb)==0) {
                    return listeMethods[i];
                }
                
            }
        }
        throw new Exception("Method not found for "+myClass.getName()+" "+key+" "+verb);
    }
    public static Vector<String> get_params_vector(Map<String,String> map)
    {
        Set<String> keys = map.keySet();
        Vector<String> toReturn = new Vector<String>();
        for (String key : keys) {
            toReturn.add(key);
        }
        return toReturn;
    }
    public static String get_referer(HttpServletRequest req)
    {
        String referer = req.getHeader("Referer");
        String contextPath = req.getContextPath();
        return referer.substring(referer.indexOf(contextPath) + contextPath.length());
    }

    public static String modify_bar(String referer,HttpServletRequest request)
    {
        return "<script>window.history.pushState({},\"\",\""+request.getContextPath()+referer+"\");</script>";
    }

    public static Object invokeAfterExcetion(String referer,List<Class> lsController,HttpSession session,HttpServletRequest request,AllTypeFormatException exception)throws Exception
    {
         
            HashMap<String,Mapping> listeMapping =  new HashMap<String , Mapping>();
            String url = referer;
            PackageUtils.scan_all_url(lsController, listeMapping, url);
            for(Map.Entry<String,Mapping> entry : listeMapping.entrySet())
            {
                Class myClass = Class.forName(entry.getValue().getClassName());
                Method methodToUse=get_method_annoted(myClass, url,"GET"); 
                Object myObject = myClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                ModelAndView mv = (ModelAndView)ObjectUtil.invoke_object(myObject, methodToUse, new HashMap<String, String>(),session,request);
                for(TypeFormatException typeFormatException : exception.getExceptions()){
                    mv.addObject("error_"+typeFormatException.getParamName(), typeFormatException.getMessage()+modify_bar(url,request));
                }
                return mv;
            }
            return "CANNOT Handle Exception for "+url;
       
    }
    public static Object invokeMethodObject(List<Class> lsController,Mapping myMap, String cheminRessource,Map<String, String> parameters,String verb,HttpSession session,HttpServletRequest request)throws Exception
    {
        Class myClass = Class.forName(myMap.getClassName());
        Method methodToUse=get_method_annoted(myClass, FrontController.get_the_get(cheminRessource),verb); 
        AuthConfiguration.check_auth(myClass,methodToUse,request);
        try
        {
           
            Object myObject = myClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            
            Object object = ObjectUtil.invoke_object(myObject, methodToUse, parameters,session,request);
            return object;
        }
        catch(AllTypeFormatException e)
        {
           
            UrlAfterException uae = methodToUse.getAnnotation(UrlAfterException.class);
            String referer = get_referer(request);
            if(uae != null)
            {
                referer = uae.name();
            }
            return invokeAfterExcetion(referer,lsController,session, request,e);
        }
     
    }
}
