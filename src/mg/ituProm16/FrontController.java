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
        resp.setContentType("text/plain");
        ServletOutputStream out = resp.getOutputStream();
        String result="Controller : \n";
        String urlPath = req.getRequestURL().toString();

        String keyHash = getkeyHash(req, resp);

        if (listController==null){
            getListController();
        }
            Utilitaire utilitaire = new Utilitaire();
            utilitaire.scanAllClasses(listController, hashMap);
      
        
        if(!hashMap.containsKey(keyHash)){
            result+="No mapping found";
        } else {
            Mapping m = hashMap.get(keyHash);
            result+="mapping found ; with method :" + m.getMethodName() + " in " + m.getClassName() +" for url : " + keyHash + " \n"; 

            Class myclass = Class.forName(m.getClassName());
            Object myobject = myclass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Method method = myclass.getDeclaredMethod(m.getMethodName(), new Class[0]);
            result+="method value : " + (String) (method.invoke(myobject, new Object[0])) + "\n";
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
