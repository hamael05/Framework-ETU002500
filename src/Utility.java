package utility ; 

import java.io.File;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import exception.* ; 
import mapping.Mapping;
import modelview.ModelView;
import annotation.* ; 
import jakarta.servlet.* ; 
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse; 
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.* ;
import session.MySession; 

public class Utility {
    public Utility()
    {

    }
    //L'instabiliter de tomcat modifie le path c'est la raison de cette fonction 
    public String normalizePath(String path) {
       //Enlever le premier / 
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        // Remplace tous les % en espace 
        try {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public String PathWithoutPackageName(String path)
    {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            } 
            // Find the last occurrence of '/'
            int lastSlashIndex = path.lastIndexOf('/');
            // Extract the substring up to the last '/'
            String transformed = path.substring(0, lastSlashIndex + 1);
            return transformed;
    }
    public boolean pathVerification(String path , String transformed)
    {
        if(path.equals(transformed))
        {
            return true ; 
        }
        return false ;
    }

    public String transformPath2(String path) {
        //Verifier si le dernier caracter est un "/" 
        if (path.endsWith("/")) {
            //suppression du "/""
            path = path.substring(0, path.length() - 1);
        }
        String[] parts = path.split("/");
        //Dernier element du path 
        String lastElement = parts[parts.length - 1];
        // Concatenange avec un point 
        String transformed = "/" + lastElement ; 
        return transformed;
    }

    public String transformPath(String path) {
        //Verifier si le dernier caracter est un "/" 
        if (path.endsWith("/")) {
            //suppression du "/""
            path = path.substring(0, path.length() - 1);
        }
        String[] parts = path.split("/");
        //Dernier element du path 
        String lastElement = parts[parts.length - 1];
        // Concatenange avec un point 
        String transformed =  lastElement + "." ; 
        return transformed;
    } 

    //Concatenage du package et classeName 
    public String fusionPackageAndClassName(String className , String packageName)
    {
        String path =  packageName + className ; 
        return path ; 
    }

    public boolean identifyType( String typeName)
    {
        boolean result = true ; 
         if( typeName.equals("String")) 
         { return result ; } 
         if( typeName.equals("int") ) 
         { return result ; }  
         if( typeName.equals("Integer") ) 
         { return result ; }  
         if( typeName.equals("Double") ) 
         { return result ; }  
         if( typeName.equals("double") ) 
         { return result ; } 
         if( typeName.equals("Date") ) 
         { return result ; } 
         else
         {
             result = false ; //if type Object 
             return result ; 
         }
    }    
    public String covertMinMaj ( String value )
    {   
        String firstChar = value.substring(0, 1).toUpperCase();
        String newValue = firstChar + value.substring(1);
        return newValue ;
    }   

    public void AddMethodeAnnotation( String normalizedPath , String packageName  , HashMap HashmapUtility) throws Exception 
    {  
        try {    

            File classpathDirectory = new File(normalizedPath) ; 
                    for ( File file : classpathDirectory.listFiles() )   
                    {
                        if(file.isFile() && file.getName().endsWith(".class"))
                        {   
                            String className = file.getName().substring( 0 , file.getName().length() - 6 ) ; 
                            String trueClassName = this.fusionPackageAndClassName(className , packageName); 
                            //Transformation en classe
                            Class<?> myclass = Thread.currentThread().getContextClassLoader().loadClass(trueClassName) ; 
                                if(myclass.isAnnotationPresent(AnnotationController.class))
                                {
                                        Method [] methods = myclass.getDeclaredMethods() ;
                                        //Liste de Methode pour chaque Classe 
                                        for (Method method : methods)
                                            if(method.isAnnotationPresent(AnnotationGet.class))  
                                            {
                                                AnnotationGet annotation = method.getAnnotation(AnnotationGet.class);
                                                AnnotationRestapi annotationApi = method.getAnnotation(AnnotationRestapi.class);
                                                String url = "" ; 
                                                if ( annotation != null ) {  url = annotation.name(); }
                                                else { url =  annotationApi.nameApi() ; }
                                                Mapping mapping = new Mapping( trueClassName, method.getName() )  ;
                                                //Ajout des information dans le Hashmap 
                                                if(HashmapUtility.containsKey(url))
                                                { throw new DuplicateKeyException("Error Annotation duplicated : " + url + "\n"); }
                                                HashmapUtility.put( url , mapping ) ;
                                            }
                                }
                        } 
                    }
        }catch( Exception e )
        { e.printStackTrace(); } 
    }

    public boolean CheckAnnotationRestApi (Method myMethod ,  String normalizedPath , String packageName  ){ 
        try {
            
            File classpathDirectory = new File(normalizedPath) ; 
            for ( File file : classpathDirectory.listFiles() )   
            {
                if(file.isFile() && file.getName().endsWith(".class"))
                {   
                    String className = file.getName().substring( 0 , file.getName().length() - 6 ) ; 
                    String trueClassName = this.fusionPackageAndClassName(className , packageName); 
                    //Transformation en classe
                    Class<?> myclass = Thread.currentThread().getContextClassLoader().loadClass(trueClassName) ; 
                    Method [] methods = myclass.getDeclaredMethods() ;
                        for (Method method : methods)
                            if(method.isAnnotationPresent(AnnotationRestapi .class) && myMethod.getName().equals(method.getName()))  
                            { return true ; }  
                    } 
            } 
                return false;  
        } catch (Exception e) {
            // TODO: handle exception
        }
            return false ;
    }


    public void verifyCorrespondenceAnnotation(  Annotation paramAnnotations , Class<?> paramType ,  ArrayList<Object> valueArg  , HttpServletRequest request ) throws Exception 
    {  
        try { 
            if( paramAnnotations != null)
            {       
                    if( this.identifyType(paramType.getSimpleName() ) == false ){    //Si c'est un object 
                        if( paramType.equals(MySession.class) )  //Verification is SessionType 
                        {
                            MySession mySession = new MySession( request.getSession()) ; 
                            valueArg.add( mySession )  ; 
                        }else{     
                            Object objParam = paramType.getDeclaredConstructor().newInstance();
                            valueArg.add( objParam )  ; 
                        }
                    }if( this.identifyType(paramType.getSimpleName() ) ){
                        AnnotationParam annotationParam = (AnnotationParam) paramAnnotations;  //Si c'est Annottee
                        valueArg.add( request.getParameter(  annotationParam.name() ) ) ;  
                    // out.print("AnnotParma namer : " + annotationParam.name() +  "\n") ; 
                    }
            }
        }catch( Exception e ) { e.printStackTrace() ;  }
    }
    public void verifyCorrespondenceNotAnnotation( Annotation paramAnnotations , Class<?> paramType ,  ArrayList<Object> valueArg , HttpServletRequest request ,  String paramName  ) throws Exception 
    {
        try { 
            if( paramAnnotations == null ){ 
                if( this.identifyType(paramType.getSimpleName() ) == false ){    //Si c'est un object 
                    throw new Exception(" ETU 2785 : Parameter not Annot  present \n") ;      
                }if( this.identifyType(paramType.getSimpleName() )  ){
                    //valueArg.add( request.getParameter( paramName ) ) ; //Si c'est pas Annotter
                    throw new Exception(" ETU 2785 : Parameter not Annot  present \n") ;      
                }
            }     
        }catch( Exception e ) 
        { e.printStackTrace();  }
    }

    public void verifyCorrespondenceFieldSession( Class ClassController , HttpServletRequest request  )
    {
         try{ 
            Field[] fields = ClassController.getDeclaredFields();
            for (Field field : fields) {
                Class<?> fieldType = field.getType() ; 
                String nameTypeField = fieldType.getSimpleName() ;
                if ( nameTypeField.equals(MySession.class)) {
                    field.setAccessible(true);
                    MySession mySession = new MySession( request.getSession()) ; 
                    field.set(this,  mySession );
                }
            } 
         }catch( Exception e )
         { e.printStackTrace(); }
    } 
    public ArrayList<Object> verifyCorrespondence( HttpServletRequest request  , Method myMethod   , PrintWriter out ) throws Exception
    {
        try{ 
            ArrayList<Object> valueArg = new ArrayList<>() ; 
            Parameter[] parameters = myMethod.getParameters();
              for (Parameter parameter : parameters) {

                        Annotation paramAnnotations = parameter.getAnnotation( AnnotationParam.class) ;  
                        String paramName = parameter.getName();
                        Class<?> paramType = parameter.getType();
                        this.verifyCorrespondenceAnnotation( paramAnnotations , paramType , valueArg , request  ) ; 
                        this.verifyCorrespondenceNotAnnotation( paramAnnotations , paramType , valueArg  , request , paramName ) ; 
              }
                return valueArg ; 
             
        }catch(Exception e )
        { e.printStackTrace() ; }
        return null ; 
    } 
  
    public Object invokingMethod( ArrayList<Object> argsList , Object myObject , Method myMethod   ) throws Exception
    {
       try{
       // Adapter les arguments pour correspondre au nombre de paramètres de la méthode
       Object[] args = null ; 
       Object res = null ; 
       if( myMethod.getParameterCount() == 0  ) 
       {
           res = myMethod.invoke( myObject , new Object[0]) ;
           return res ; 
       }
       args = argsList.toArray() ; 
       while (args.length < myMethod.getParameterCount()  ) {
           args = Arrays.copyOf(args, args.length + 1);
           args[args.length - 1] = null; // Remplacer par null les arguments manquant
       }
       res = myMethod.invoke(myObject, args); 
       return res ; 
       }catch(Exception e){ e.printStackTrace();} return null ; 
    } 
   
    public Method checkMethod( Class myClass , String methodName ) throws Exception 
    {
        try{
        Method myMethod = null ; 
        Method [] Allmethod = myClass.getDeclaredMethods()  ; 
        for( int i = 0 ; i < Allmethod.length  ; i++ )
        {
            if( Allmethod[i].getName().equals(methodName) )
            {
                myMethod = Allmethod[i];
                return myMethod ;  
            }
        }
        if( myClass.getDeclaredMethod( methodName, new Class[0]) != null)
        {  return  myClass.getDeclaredMethod( methodName, new Class[0])  ; }
        return myMethod ; 
        }catch(Exception e){
            e.printStackTrace();
        } return null ; 
    }
    
 
  public void SetAttributeObject2(Method setMethod, String typefield, Object objClass, String[] partiesInput, HttpServletRequest request) throws Exception {
        String parameterName = partiesInput[0] + "." + partiesInput[1];
        String parameterValue = request.getParameter(parameterName);
        try {
            switch (typefield) {
                case "String":
                    setMethod.invoke(objClass, parameterValue);
                    break;
                case "int":
                    setMethod.invoke(objClass, Integer.valueOf(parameterValue));
                    break;
                case "Integer":
                    setMethod.invoke(objClass, Integer.valueOf(parameterValue));
                    break;
                case "double":
                    setMethod.invoke(objClass, Double.valueOf(parameterValue));
                    break;
                case "Double":
                    setMethod.invoke(objClass, Double.valueOf(parameterValue));
                    break;
                case "Date":
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // adjust format as needed
                    setMethod.invoke(objClass, dateFormat.parse(parameterValue));
                    break;
                default:
        // throw new IllegalArgumentException("Unsupported type: " + typefield);
            }
        } catch (ParseException e) {
        //  System.err.println("Error parsing date: " + parameterValue);
        } catch (Exception e) {
        //  System.err.println("Error invoking method: " + e.getMessage());
        }
    }
    public void SetAttributeObject(Method myMethod, ArrayList<Object> valueArg, String[] partiesInput, HttpServletRequest request , PrintWriter out ) throws Exception{ 
        try {

            Parameter[] parameters = myMethod.getParameters();
            int count = 0; 
            for (Parameter parameter : parameters) {
                Annotation paramAnnotations = parameter.getAnnotation(AnnotationParam.class);  
                if (paramAnnotations != null) {
                    AnnotationParam annotationParam = (AnnotationParam) paramAnnotations; 
                    if (annotationParam.name().equals(partiesInput[0])) { // Annotation Param Object 
                        Class<?> paramType = parameter.getType();
                        if (this.identifyType( paramType.getSimpleName() )  == false) { // Si c'est un Object 
                            Object objClass = valueArg.get(count); 
                            Field[] fields = paramType.getDeclaredFields(); 
                            for (Field field : fields) { 
                                Annotation fieldAnnotations = field.getAnnotation(AnnotationField.class);  
                                if (fieldAnnotations != null) {
                                    AnnotationField annotationField = (AnnotationField) fieldAnnotations;         
                                    if (annotationField.name().equals(partiesInput[1])) { // Annotation Field
                                        field.setAccessible(true); // Accès champ privé
                                        Method setMethod = paramType.getDeclaredMethod("set" + this.covertMinMaj(field.getName()), field.getType() );  
                                        setMethod.setAccessible(true);     
                                        this.SetAttributeObject2( setMethod , field.getType().getSimpleName()  , objClass ,  partiesInput ,  request )  ;
                                        //throw new Exception("Count value :"  + count +  "ObjEmp : " + valueArg.get(count) +  " Set Succes2  "  + "set" + this.covertMinMaj(field.getName())   + " fieldType : "  + field.getType()  +  "\n") ;    
                                    } 
                                }
                              
                            }
                        }
                    }
                }
                count++; 
            }
        } catch (Exception e) {
            e.printStackTrace(); // Toujours bon de loguer les exceptions pour le debugging
        }
    }  

}


