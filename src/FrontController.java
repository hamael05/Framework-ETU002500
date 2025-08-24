package servlet ; 

import java.util.*;

import annotation.*;
import java.text.* ; 
import java.io.* ; 
import java.lang.reflect.* ;
import mapping.* ; 
import jakarta.servlet.* ; 
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse; 
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.net.URLDecoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import utility.*;
import vm.VerbeMethod;
import modelview.ModelView ; 
import exception.* ;
import java.lang.annotation.Annotation;
import com.google.gson.Gson;
import jakarta.servlet.http.Part;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpSession; 
import session.* ; 
import modelview.ModelView;

@MultipartConfig
public class FrontController extends HttpServlet {
    

    private String Source ;  
    private HashMap<String, Mapping> hashmapUtility =  new HashMap<>(); 
    private Utility util = new Utility(); 

    public void init() throws ServletException 
    {
        try
        {
            //Prendre  les controllers dans le WEB.xml 
            this.Source = this.getInitParameter("Source")  ;
            this.configMap(); 
        }catch(Exception e )
        {   
                e.printStackTrace() ; 
        }
    } 

    public boolean pathVerification(String path , String transformed)
    {
        if(path.equals(transformed))
        {
            return true ; 
        }
        return false ;
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
        String transformed = "/" + lastElement ; 
        return transformed;
    }
        
    public void configMap() throws Exception
    {
     try{
            ServletContext context = getServletContext() ; 
            if( context.getResource(this.util.PathWithoutPackageName(this.Source)).getPath() != null){
                String classpath = context.getResource(this.util.PathWithoutPackageName(this.Source)).getPath() ; 
                String normalizedPath = this.util.normalizePath(classpath);   
                String packageName = this.util.transformPath(this.Source) ;   
                this.util.AddMethodeAnnotation( normalizedPath , packageName , this.hashmapUtility) ; 
            }else {  throw new Exception("Error package Scan verify our file xml \n") ;  } 
        }catch(Exception e )
        { e.printStackTrace(); }   
    } 

    public void dispacthModelView(  ModelView mv , HttpServletRequest request  ,  HttpServletResponse response )
    {
        try{
            HashMap<String, ValueAndError> mapValidation = this.util.getMapValidation() ; 
            String url = mv.getUrl() ;
            System.out.println("Actu url : " + url + "\n") ; 
            Set<String> keyMap= mv.getData().keySet(); 
            String httpMethod = request.getMethod(); 
            for(String keymap : keyMap)
            { request.setAttribute( keymap , mv.getData().get(keymap)) ; }

           
                RequestDispatcher dispatch = request.getRequestDispatcher( url ); 
                dispatch.forward(request, response);
            
      
        }catch(Exception e )
        { 
            System.out.println(e);
            e.printStackTrace(); 
        }
    }
    public void setObjectParam ( Method myMethod  , ArrayList<Object> valueArg  ,HttpServletRequest request   ) throws  Exception
    {
         try{ 
                Enumeration<String> parameterNames = request.getParameterNames() ;   
                String[] part = null ; 
                while (parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    String[] partiesInput = paramName.split("\\.");
                    if( partiesInput.length > 1 ) 
                    {   this.util.SetAttributeObject( myMethod , valueArg, partiesInput , request , paramName);  }
                }       

         }catch(Exception e)
         { e.printStackTrace(); } 
    } 

    public boolean verifyAnnotaionrRestApi( Method mymethod ) throws Exception  { 
        try {
            ServletContext context = getServletContext() ; 
            if( context.getResource(this.util.PathWithoutPackageName(this.Source)).getPath() != null){
                String classpath = context.getResource(this.util.PathWithoutPackageName(this.Source)).getPath() ; 
                String normalizedPath = this.util.normalizePath(classpath);   
                String packageName = this.util.transformPath(this.Source) ;  
                boolean result = this.util.CheckAnnotationRestApi( mymethod , normalizedPath, packageName) ; 
                return result ; 
            }else {  throw new Exception("Error package Scan verify our file xml in verifyAnnRestApi \n") ;  } 
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false ; 
    }

    public boolean verifyUrlMethod( Method myMethod , String pathValidation ) throws Exception  { 
        try {
            ServletContext context = getServletContext() ; 
            if( context.getResource(this.util.PathWithoutPackageName(this.Source)).getPath() != null){
                String classpath = context.getResource(this.util.PathWithoutPackageName(this.Source)).getPath() ; 
                String normalizedPath = this.util.normalizePath(classpath);   
                String packageName = this.util.transformPath(this.Source) ;  
                boolean result = this.util.CheckAnnotationMethod(myMethod, normalizedPath, packageName , pathValidation  ) ;
                return result ; 
            }else {  throw new Exception("Error package Scan verify our file xml in verifyAnnRestApi \n") ;  } 
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false ; 
    }

    public void ShowResultJson( boolean boolRestapi , Object res , PrintWriter out ,  HttpServletRequest request  ,  HttpServletResponse response    ) throws TypeErrorException 
    {
            response.setContentType("application/json");
            Gson gson = new Gson();
            boolean responseSent = false;
            if (  res instanceof ModelView ) 
            { 
                if( boolRestapi ) { 
                    ((ModelView)res).getUrl() ; 
                    String jsonUrl = gson.toJson(((ModelView)res).getUrl()) ; 
                    String jsonData = gson.toJson(((ModelView)res).getData()); 
                    String jsonData2 = gson.toJson(((ModelView)res).getData().get("DataUser")); 
                    out.print("Url json  :" + jsonUrl + "\n") ; 
                    out.write("Data json write  :" + jsonData  + "\n") ; 
                } else { 
                    this.dispacthModelView( (ModelView)res , request , response ); 
                }
            }else if( res instanceof String){ 
                if( boolRestapi ){
                    String json = gson.toJson(res) ; 
                    out.print( "Data not mv : " + res  + '\n'  ) ; 
                }else{ out.print("Valeur de la methode String :" + res + "\n") ;  }
            }else { throw new TypeErrorException(" Error Type of return incorrect methode "); }
    }
    
    public boolean setErrorValidation (  HttpServletRequest request  ,  HttpServletResponse response  , ArrayList<Object> valueArg,  Object myObject  , Method myMethod ) { 
        try{    
            String referer = request.getHeader("Referer");
            HashMap<String, ValueAndError> mapValidation = this.util.getMapValidation() ; 
            if( referer != null && mapValidation.size() > 0) { 
                String packageProjectName =  referer.substring(referer.indexOf("/", referer.indexOf("//") + 2), referer.lastIndexOf("/"));
                String pathValidation = referer.substring(referer.lastIndexOf("/"));  
                if( verifyUrlMethod( myMethod , pathValidation ) ) { 
                    for (Map.Entry<String, ValueAndError > entry : mapValidation.entrySet()) {
                        String key = entry.getKey();
                        ValueAndError value =  entry.getValue();           
                        request.setAttribute(key ,  value.getError() + "<script> window.history.pushState({}, \"\", \""+ packageProjectName + pathValidation +"\"); console.log(\"referer not move\")</script>") ; 
                        request.setAttribute( key+"_init" , value.getValue()  ) ; 
                      
                    }      
                    return true ; 
                }
            }
            return false ; 
        }catch(Exception e){ 
            System.out.println("e") ;
            e.printStackTrace(); 
        }
        return false ; 
    }
    
    public void ShowResult(Mapping mapping  , String methodName , PrintWriter out  , HttpServletRequest request  ,  HttpServletResponse response  )throws TypeErrorException ,Exception
    {
        try { 
                Class myClass = Class.forName(mapping.getClasseName());
                Method myMethod = this.util.checkMethod(myClass, methodName ) ;  
                ArrayList<Object> valueArg = this.util.verifyCorrespondence( request , response , myMethod  ) ;
                this.util.verifyCorrespondenceFieldSession(myClass , request ) ;
                Object myObject = myClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]) ; 
                boolean boolRestapi = this.verifyAnnotaionrRestApi( myMethod ) ; 
                this.setObjectParam(myMethod, valueArg, request ); 
                Object res = null ; 
                    if( this.setErrorValidation( request, response, valueArg, myObject, myMethod )  ) { 
                        String referer = request.getHeader("Referer");
                        String pathValidation = "/" + referer.substring(referer.lastIndexOf("/") + 1); 

                        Mapping mapping1 = this.hashmapUtility.get( pathValidation ); 
                        HashSet<VerbeMethod> ls_verbeMethod = mapping1.getVerbeMethods() ;  

                        for ( VerbeMethod method : ls_verbeMethod) {  
                            Class myClass1 = Class.forName(mapping1.getClasseName());
                            Method myMethod1 = this.util.checkMethod(myClass1 , method.getMethod() ) ;  
                            ArrayList<Object> valueArg1 = this.util.verifyCorrespondence( request , response , myMethod1  ) ;
                            this.util.verifyCorrespondenceFieldSession(myClass1 , request ) ;
                            Object myObject1 = myClass1.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]) ; 
                            //this.setObjectParam(myMethod1, valueArg1, request );
                            res = this.util.invokingMethod( valueArg1 , myObject1 , myMethod1 ) ;  
                        } 
                        
                    } else { 
                        res = this.util.invokingMethod( valueArg , myObject , myMethod ) ; 
                    }
                this.ShowResultJson( boolRestapi, res , out , request , response );
                
        }catch(Exception e )
        {  e.printStackTrace();  }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws TypeErrorException ,Exception  , IllegalArgumentException
    {  
        PrintWriter out = response.getWriter() ; 
        try{
            StringBuffer url = request.getRequestURL();
            String urlString = url.toString();
            String transformedUrl = this.util.transformPath2(urlString) ;   
            Mapping mapping = this.hashmapUtility.get(transformedUrl);   
            System.out.println(" transforme Url ::: " + transformedUrl  + "\n") ;  
            if(mapping == null) { out.print("404 error , url incorrect"); } 
            else {     
                        HashSet<VerbeMethod> ls_verbeMethod = mapping.getVerbeMethods() ;  
                        for ( VerbeMethod method : ls_verbeMethod) { 
                            System.out.println("methode dans le mappin : " + method.getMethod() + " \n") ;  //TODO : Check if the method is allowed in this context (e.g., authenticated, authorized, etc.)
                            if ( method.getVerb().equals( request.getMethod() )) { 
                                this.ShowResult(mapping , method.getMethod()  ,  out , request , response ); 
                            }else {   out.print("error 500\n") ;  }          
                       }
            }
        }catch( Exception e ) 
        {
            request.setAttribute("errorMessage", e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("Error.jsp");
            dispatcher.forward(request, response);
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        try{
            processRequest(request, response);
        }catch(Exception e){
                e.printStackTrace() ;
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {  
        try{
            processRequest(request, response);
        }catch(Exception e )
        {
            e.printStackTrace() ;
        }
    }
}



