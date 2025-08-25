package utility ; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.annotation.*;
import java.net.URLDecoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import exception.* ; 
import mapping.Mapping;
import modelview.ModelView;
import annotation.* ;
import authentification.AuthLevel;
import jakarta.servlet.* ; 
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse; 
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.* ;
import java.math.BigDecimal;


import session.MySession;
import validation.Validation;
import vm.VerbeMethod; 
import jakarta.servlet.http.Part;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig
public class Utility {

    HashMap<String, ValueAndError> mapValidation = new HashMap<String , ValueAndError>(); 
    public Utility()
    {

    }
    public HashMap<String, ValueAndError> getMapValidation() {
        return this.mapValidation;
    }
    public void setMapValidation(HashMap<String, ValueAndError> mapValidation) {
        this.mapValidation = mapValidation;
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
         if( typeName.equals("Part"))
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
    public String checkVerbe(Method method ) { 
        try {
            if( method.isAnnotationPresent(AnnotationGet.class) )
            { return "GET";  }
            if( method.isAnnotationPresent(AnnotationPost.class) )
            { return "POST" ; } 
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "GET" ; // annotation get default  
    }

    //Scan url 
    public void AddMethodeAnnotation( String normalizedPath , String packageName  , HashMap hashmapUtility) throws Exception 
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
                                { this.scan_url(trueClassName , myclass , hashmapUtility ) ; }
                        } 
                    }
        }catch( Exception e )
        { e.printStackTrace(); } 
    }

    public void scan_url( String classeName , Class<?> myClass , HashMap hashmapUtility  ) throws Exception { 
        Method [] methods = myClass.getDeclaredMethods() ;
        String url = "" ; 
        for (Method method : methods) {
            Url annotationUrl = method.getAnnotation(Url.class);
            this.putHashmap( hashmapUtility , annotationUrl , method , url , classeName );  
        }
    }    

    public void putHashmap( HashMap hashmapUtility, Url annotationUrl , Method method , String url , String classeName) throws Exception{
        if (annotationUrl != null) {
            url = annotationUrl.nameUrl();
            String verbe = this.checkVerbe(method);
            VerbeMethod verbeMethod = new VerbeMethod(verbe, method.getName());
            if (hashmapUtility.containsKey(url)) {
                HashSet<VerbeMethod> ls_verbeMethods = ((Mapping)hashmapUtility.get(url)).getVerbeMethods();
                if (this.checkVerbeDuplicate(ls_verbeMethods, verbeMethod)) { throw new Exception("Erreur : 2 URL :'" + url + "' avec 2 verbe :'" + verbe + "' identique") ; } 
                else {
                    ls_verbeMethods.add(verbeMethod);
                    hashmapUtility.put(url, new Mapping(classeName, ls_verbeMethods));
                }
            } 
            else {   
                HashSet<VerbeMethod> ls_verbeMethods = new HashSet<>();
                // if(  ls_verbeMethods.add(new VerbeMethod(verbe, method.getName())) == false ) {  throw new Exception("Erreur 2 verbe et 2 methodName identique"); }
                ls_verbeMethods.add(verbeMethod);
                hashmapUtility.put(url, new Mapping(classeName, ls_verbeMethods));
            }
        }
     }
    public boolean checkVerbeDuplicate(HashSet<VerbeMethod> ls_verbeMethods, VerbeMethod vm) {
        if (ls_verbeMethods.stream().anyMatch(v -> v.getVerb().equals(vm.getVerb()))) { return true; } 
        else { return false ; }
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
                            if(method.isAnnotationPresent(AnnotationRestapi.class) && myMethod.getName().equals(method.getName()))  
                            { return true ; }  
                    } 
            } 
                return false;  
        } catch (Exception e) {
            // TODO: handle exception
        }
            return false ;
    }

    public void verifyCorrespondenceAnnotation(  Annotation paramAnnotations , Class<?> paramType ,  ArrayList<Object> valueArg  , HttpServletRequest request , HttpServletResponse response) throws Exception 
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
                    }if( this.identifyType( paramType.getSimpleName() ) ){
                        AnnotationParam annotationParam = (AnnotationParam) paramAnnotations;  //Si c'est Annottee
                        if(paramType.equals(Part.class)) { 
                            System.out.println(" annotation name ::: " + annotationParam.name() +"\n"   );
                            valueArg.add(  request.getPart( annotationParam.name() ) ) ; 
                            Part partFile  = (Part)request.getPart( annotationParam.name() ) ; 
                            saveFile( partFile , response);
                        }else {  valueArg.add( request.getParameter(  annotationParam.name() ) ) ; }
                    // out.print("AnnotParma namer : " + annotationParam.name() +  "\n") ; 
                    }
            }
        }catch( Exception e ) { e.printStackTrace() ; }
    }

    public void saveFile( Part partFile , HttpServletResponse response) { 
        try { 
        String fileName = getFileName( partFile );
        String savePath = "D:\\IT UNIVERSITY\\WorkSpace\\S4\\Projet_Mr_Aina\\Framework\\Sprint12\\" + fileName;
        partFile.write(savePath);
        response.getWriter().println("Fichier téléchargé avec succès : " + fileName);
        }catch(Exception e ) 
        { 
            e.printStackTrace() ; 
        } 
    }

    public String getFileName(Part part) {
        try{ 
        String contentDisposition = part.getHeader("content-disposition");
        for (String cd : contentDisposition.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        }catch ( Exception e ) { e.printStackTrace() ; }
        return null;
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
    public ArrayList<Object> verifyCorrespondence( HttpServletRequest request  , HttpServletResponse response , Method myMethod ) throws Exception
    {
        try{ 
            ArrayList<Object> valueArg = new ArrayList<>() ; 
            Parameter[] parameters = myMethod.getParameters();
              for (Parameter parameter : parameters) {
                        Annotation paramAnnotations = parameter.getAnnotation( AnnotationParam.class) ;  
                        String paramName = parameter.getName();
                        Class<?> paramType = parameter.getType();
                        this.verifyCorrespondenceAnnotation( paramAnnotations , paramType , valueArg , request , response ) ; 
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

    public void managerValidation(Method myMethod, String nameInput, HttpServletRequest request, Field field , String partieInput ) throws Exception {
        boolean checkValidation = true;
        try {
            Validation validation = new Validation();
            AnnotationField fieldAnnotation = field.getAnnotation(AnnotationField.class);
        if( validation.checkAnnotationField(field) == 1) {  
            if( fieldAnnotation.name().equals( partieInput ) ) {
                if (validation.checkAnnotationDecimal(field) == 1) {
                    AnnotationDecimal fieldAnnotations = field.getAnnotation(AnnotationDecimal.class);
                    if (validation.isNumeric(request.getParameter(nameInput))) {           
                        validation.validateValue(
                                Double.valueOf(request.getParameter(nameInput)),
                                Double.valueOf(fieldAnnotations.min()),
                                Double.valueOf(fieldAnnotations.max())
                        );
                    }
                }
                // Validation Size
                if (validation.checkAnnotationSize(field) == 1) {
                    AnnotationSize fieldAnnotationsSize = field.getAnnotation(AnnotationSize.class);
                    if( validation.isNumeric(request.getParameter(nameInput) ) == false )  {
                        validation.validateSizeString(
                                request.getParameter(nameInput),
                                Integer.valueOf(fieldAnnotationsSize.min()),
                                Integer.valueOf(fieldAnnotationsSize.max())
                        )  ; 
                    } 
                }
                // Validation NotNull
                if (validation.checkAnnotationNotNull(field) == 1) {   
                    AnnotationNotNull fieldAnnotationsNotNull = field.getAnnotation(AnnotationNotNull.class);
                    validation.validateValuesNull(request.getParameter(nameInput));
                }
             } else { 
                checkValidation = false ; 
             }
        } else { 
            checkValidation = false ; 
        }

        } catch (Exception e) {
            checkValidation = false;
            this.getMapValidation().put(
                    nameInput + "_error",
                    new ValueAndError(request.getParameter(nameInput), e.getMessage())
            );
            this.setMapValidation( this.getMapValidation() );
            System.out.println("map added \n");
        }
        if (checkValidation && this.getMapValidation().containsKey(nameInput + "_error")) {
            this.getMapValidation().remove(nameInput + "_error");

        }
    }
    

    public static Map<String, String> renameDuplicateKey(Map<String, String> map) {
        Map<String, String> mapResultat = new HashMap<>();
        Map<String, Integer> compteurCles = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String cle = entry.getKey();
            String valeur = entry.getValue();
            if (compteurCles.containsKey(cle)) {
                int suffixe = compteurCles.get(cle) + 1;
                compteurCles.put(cle, suffixe);
                cle = cle + suffixe; 
            } else {
                compteurCles.put(cle, 1);
            }
            mapResultat.put(cle, valeur);
        }
        return mapResultat;
    }

    ///Set attribute Object
    public void SetAttributeObject(Method myMethod, ArrayList<Object> valueArg, String[] partiesInput, HttpServletRequest request , String nameInput ) throws Exception{ 
        try {
            int count = 0 ; 
            Parameter[] parameters = myMethod.getParameters();
         
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

                                this.managerValidation(myMethod, nameInput, request, field , partiesInput[1]);

                                Annotation fieldAnnotations = field.getAnnotation(AnnotationField.class);  
                                if (fieldAnnotations != null) {
                                    AnnotationField annotationField = (AnnotationField) fieldAnnotations;         
                                    if (annotationField.name().equals(partiesInput[1])) { // Annotation Field

                                        field.setAccessible(true); // Accès champ privé
                                        Method setMethod = paramType.getDeclaredMethod("set" + this.covertMinMaj(field.getName()), field.getType() );  
                                        setMethod.setAccessible(true);     
                                        this.SetAttributeObject2( setMethod , field.getType().getSimpleName()  , objClass ,  partiesInput ,  request )  ;
                                    } 
                                }
                              
                            }
                        }
                    }
                }
                count++; 
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }  

    public boolean CheckAnnotationMethod ( Method myMethod ,  String normalizedPath , String packageName  , String pathValidation ){ 
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
                            if(method.isAnnotationPresent(Url.class)   )  
                            { 
                                Url methodAnnotation = method.getAnnotation(Url.class);  
                                if( methodAnnotation.nameUrl().equals(  pathValidation ) )  { 
                                    System.out.println( "method Annoter :" +  methodAnnotation.nameUrl() + "\n" ) ;
                                    System.out.println( "path validation :" + pathValidation  + "\n" ) ; 
                                return true ; } 
                            }  
                    } 
            } 
            return false ;    
        } catch (Exception e) {
          e.printStackTrace(); 
          System.out.println(e);
        }
        return false ; 
    }


    //Authentification 
    public List<AuthLevel> getAuthLevels(String authFilePath) throws Exception {
    List<AuthLevel> authLevels = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(authFilePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            // Supposons que les lignes du fichier suivent le format : "name level"
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 2) {
                String nameAuth = parts[0];
                int level = Integer.parseInt(parts[1]);
                authLevels.add(new AuthLevel(nameAuth, level));
            }
        }
    } catch (Exception e) {
        throw new Exception("Erreur lors de la lecture du fichier auth.txt", e);
    }
         return authLevels;
    }

    public void CheckAnnotationAuth (Method myMethod ,  String normalizedPath , String packageName , HttpServletRequest request , String authSessionName , List<AuthLevel> ls_AuthLevels) throws Exception{ 
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
                            if(method.isAnnotationPresent(AnnotationAuth.class) && myMethod.getName().equals(method.getName()))  
                            { 
                                AnnotationAuth authAnnotation = method.getAnnotation(AnnotationAuth.class);
                                MySession sessionAuth = new MySession (request.getSession() ) ;
                                this.verifyLevelAuthentification(sessionAuth , ls_AuthLevels , authAnnotation , authSessionName ) ;
                            }  
                    } 
            } 
        } catch (Exception e) {
           e.printStackTrace();
           throw e; 
        }
    }

    public int addLevelAuthName( String authName , List<AuthLevel> authLevels ) { 
        for( AuthLevel authLevel : authLevels ) { 
            if( authName.equals(authLevel.getNameAuth() ) )  {
                return authLevel.getLevel() ;
            }
        }
        return -1 ;  
    }
    
    public void verifyLevelAuthentification ( MySession sessionAuth , List<AuthLevel> ls_AuthLevels , AnnotationAuth authAnnotation  , String authSession ) throws Exception {   
       if (!authAnnotation.name().equals((String) sessionAuth.getSession(authSession))) { 
            String authNameAnnotation = authAnnotation.name() ;  
            String authNameSession = (String) sessionAuth.getSession( authSession ) ;
            int levelauthNameAnnotation = this.addLevelAuthName( authNameAnnotation, ls_AuthLevels) ; 
            int levelauthNameSession = this.addLevelAuthName(authNameSession, ls_AuthLevels) ; 
            if( levelauthNameAnnotation > levelauthNameSession ) {
                throw new Exception("Vous n'avez pas les droits pour effectuer cette action");
            }
        }
    } 
}


