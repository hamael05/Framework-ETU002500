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


import java.io.IOException;
import java.io.PrintWriter;

public class FrontController extends HttpServlet {
    String packageName;
    List<Class<?>> listController;
    HashMap<String, Mapping> hashMap;

    public void init() throws ServletException {
        try{
            packageName = getInitParameter("package-source");
            hashMap = new HashMap<>(); // Initialisation de hashMap
        } catch (Exception e){
            e.printStackTrace();
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
    public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, Exception {
        resp.setContentType("text/plain");
        ServletOutputStream out = resp.getOutputStream();
        String result="Controller : \n";
        if (listController==null){
            getListController();
        }
        for (int i = 0; i< listController.size(); i++) {
            result += listController.get(i).getName() + " "+i+" \n";
        }

        result += "annoted method: \n";
    
            Utilitaire utilitaire = new Utilitaire();
            utilitaire.scanAllClasses(listController, hashMap);
            for(Map.Entry<String, Mapping> entry : hashMap.entrySet()){
                String key = entry.getKey();
                Mapping mapping = entry.getValue();
                result += "key : " + key + " ; method : " + mapping.getMethodName() +" in class " + mapping.getClassName() + "\n";
            }
            

        out.write((result).getBytes());
        out.close();
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
