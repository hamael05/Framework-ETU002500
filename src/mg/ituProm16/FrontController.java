package mg.ituProm16;

import jakarta.servlet.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.ituProm16.annotation.*;
import mg.ituProm16.utilitaire.*;

import java.util.*;
import java.io.*;
import java.text.*;
import java.lang.reflect.Method;


import java.io.IOException;
import java.io.PrintWriter;

public class FrontController extends HttpServlet {
    String packageName;
    List<Class<?>> listController;
    HashMap<String, Mapping> hashMap;

    public void init() throws ServletException {
        try{
            packageName = getInitParameter("package-source");
            if (listController==null){
                getListController();
            }
            hashMap = new HashMap<>(); // Initialisation de hashMap
            Utilitaire utilitaire = new Utilitaire();
            utilitaire.scanAllClasses(listController, hashMap);
        } 
        catch(NullPointerException npe){
            throw new Error("No package source", npe);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
    }

    public String modifierClassPath(String classpath) {
        classpath = classpath.substring(1);
        classpath = classpath.replace("%20", " ");
        return classpath;
    }  

    public void getListController() throws Exception {
        ServletContext servletContext = getServletContext();
        String classpath =this.modifierClassPath(servletContext.getResource(this.packageName).getPath());
        File classPathDirectory = new File(classpath);
        this.listController = new ArrayList<Class<?>>();
        
        for(File file : classPathDirectory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                Class<?> class1 = Thread.currentThread().getContextClassLoader().loadClass(this.packageName.split("classes/")[1].replace("/", ".") + className);
                if (class1.isAnnotationPresent(Controller.class)) {
                    this.listController.add(class1);
                }
            }
        }    
        
    }
    public String getkeyHash(HttpServletRequest req, HttpServletResponse resp) {
        String urlPath = req.getRequestURL().toString();
        String[] urlSplit = urlPath.split("/");
        String key = "/";
        for (int i = 4; i < urlSplit.length; i++) {
            key += urlSplit[i];
            if ( i < urlSplit.length-1 ){
                key += "/";
            }
        }
        return key;
    }

    

    public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, Exception {   
        try {
            String toPrint = "Value of method= " ;
            String urlPath = req.getRequestURL().toString();

            String keyHash = getkeyHash(req, resp);
            Utilitaire utilitaire = new Utilitaire();

            Map<String, String[]> arguments = req.getParameterMap();
            HashMap<String, String> argument = new HashMap<>();
            Set<String> paramkeys = arguments.keySet();
            for (String key : paramkeys) {
                argument.put(key, arguments.get(key)[0]);
            }
        
            Object result = utilitaire.methodInvoke(hashMap, keyHash, argument);
            
            if (result instanceof String) {
                toPrint += (String) result + " / url : " + keyHash;
            } else if (result instanceof ModelView) {
                ModelView view = (ModelView) result;
                RequestDispatcher dispatcher = req.getRequestDispatcher(view.getUrl());
                HashMap<String, Object> data = view.getData();
                Set<String> keys = data.keySet();
                for (String key : keys) {
                    req.setAttribute(key, data.get(key));
                }
                dispatcher.forward(req, resp);
            } else {
                throw new Exception("Incompatible type");
            }
            PrintWriter out = resp.getWriter();
            out.println(toPrint);
            out.close();
        }   
        catch(Exception e){
            if(e instanceof IllegalArgumentException) {
                resp.sendError(400, e.getMessage());
            } else {
                resp.sendError(500, e.getMessage());
            }
        }  
        
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            this.processRequest(req, resp);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            this.processRequest(req, resp);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
