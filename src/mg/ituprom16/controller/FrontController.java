package mg.ituprom16.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import mg.ituprom16.controller.auth.AuthConfiguration;
import mg.ituprom16.utils.*;
import com.google.gson.*;
import javax.naming.Context;


@MultipartConfig
public class FrontController extends HttpServlet{

    private String packageSource;
    private List<Class> lsController;
    private HashMap<String ,Mapping> listeMapping;

    public void init() throws ServletException{
        try
        {
            ServletContext context = getServletContext();
            //SET AUTH 
            String auth_path = context.getResource(this.getInitParameter("authentification")).getPath();

          
            auth_path = auth_path.replace("%20", " ");
            auth_path = auth_path.substring(1);
            context.setAttribute("roles",AuthConfiguration.readRoles(auth_path));
            context.setAttribute("session_auth", this.getInitParameter("session_auth"));
            context.setAttribute("session_connect", this.getInitParameter("session_connect"));

            this.packageSource = this.getInitParameter("package-source"); 
            getControllers();
           
        }
        catch(NullPointerException e)
        {
            throw new Error("Package not found",e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }

    }

    public void getControllers() throws Exception
    {
        ServletContext context = getServletContext();
        String classpath = context.getResource(this.packageSource).getPath();
        this.lsController = PackageUtils.getClassesByAnnotation(classpath, mg.ituprom16.annotation.Controller.class);
        
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       processRequest(req, resp);
    }

    public static Map<String,String> all_parameters(HttpServletRequest request)
    {
        Map<String,String[]> paramsBrut = request.getParameterMap();
        Map<String,String> toReturn = new HashMap<String,String>();
        Set<String> cles = paramsBrut.keySet();
        for(String key : cles)
        {
            toReturn.put(key,paramsBrut.get(key)[0]);
        }   
        return toReturn;
    }
    public static String get_the_get(String ressource)
    {
        String[] path = ressource.split("/"); // separer par /
        String url = "/"+path[path.length-1].trim(); // enlever nom du prosjet
        if (path.length<3) {
            url = "/";
        }
        return url;
    }
    public void processRequest(HttpServletRequest req, HttpServletResponse resp)throws IOException,ServletException{

        PrintWriter writer = resp.getWriter();   
        this.listeMapping = new HashMap<String , Mapping>();
        String[] path = req.getRequestURI().split("/"); // separer par /
        String url = "/"+path[path.length-1].trim(); // enlever nom du prosjet
        if (path.length<3) {
            url = "/";
        }
        try
        {
            PackageUtils.scan_all_url(lsController, listeMapping, url.trim());
            if (listeMapping.size()==0) {
                throw new IllegalArgumentException("URL NOT FOUND");
            }
            for(Map.Entry<String,Mapping> hashEntry : listeMapping.entrySet())
            {
                Mapping mapping = hashEntry.getValue();
        
                // CLASS OF OBJECT
                Map<String,String> allParameters = all_parameters(req);//ALL PARAMETERS OF FORM
                String toPrint;
                Object resultOfInvoke = PackageUtils.invokeMethodObject(lsController,mapping,req.getRequestURL().toString(), allParameters,req.getMethod(),req.getSession(),req);
                
                if (resultOfInvoke instanceof String) {
                    toPrint = (String)resultOfInvoke;
                    writer.write("Value of url : "+ toPrint+"\n");
                }
                else if (resultOfInvoke instanceof ModelAndView) {
                    ModelAndView modelAndView = (ModelAndView) resultOfInvoke;
                    HashMap<String,Object> data = modelAndView.getData();
                    RequestDispatcher dispatcher = req.getRequestDispatcher(modelAndView.getUrl());
                    Set<String> keySet = data.keySet();
                    for(String keyString : keySet)
                    {
                        req.setAttribute(keyString, data.get(keyString));
                    }
                    dispatcher.forward(req, resp);
                } 
                else{
                    Gson gson = new Gson();
                    resp.setContentType("application/json");
                    String gson_print = gson.toJson(resultOfInvoke);
                    writer.write(gson_print);
                }
            }
        }
        catch(Exception e){
            if (e instanceof IllegalArgumentException) {
                resp.setContentType("text/html");
                writer.print("ERROR 400 "+e.getMessage());
            }
            else
            {
                req.setAttribute("error", e.getMessage());
                e.printStackTrace(writer);
                resp.setContentType("text/html");
                writer.print("ERROR 500 "+e.getMessage()+ " "+req.getSession().getAttribute("referer"));
            }
        }          
    }
}
