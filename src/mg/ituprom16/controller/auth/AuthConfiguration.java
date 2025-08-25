package mg.ituprom16.controller.auth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.PortUnreachableException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import mg.ituprom16.annotation.auth.Authentified;
import mg.ituprom16.annotation.auth.ConfiguredAuth;
import mg.ituprom16.annotation.auth.Free;
import mg.ituprom16.exception.AuthException;

public class AuthConfiguration {
    public static  HashMap<String, Integer> readRoles(String filePath) {
    HashMap<String, Integer> rolesMap = new HashMap<>();
    try {
        for (String line : Files.readAllLines(Path.of(filePath))) {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    String role = parts[0];
                    Integer level = Integer.parseInt(parts[1]);
                    rolesMap.put(role, level);
                }
            }
        }   catch (IOException e) {
            e.printStackTrace();
        }
        return rolesMap;
    }
    public static boolean check_auth_class(Class caller,HttpServletRequest request) throws AuthException {

        Object auth_of_log = request.getSession().getAttribute(request.getServletContext().getAttribute("session_auth").toString());
        HashMap<String,Integer> roles = (HashMap<String,Integer>)request.getServletContext().getAttribute("roles");
        Integer value = 0;
        Integer value_log = 0;
        if(auth_of_log != null && roles.get(auth_of_log.toString())!=null)
        {
            value_log = roles.get(auth_of_log);
        }
        if(caller.isAnnotationPresent(Free.class))
        {
            value = roles.get("Free"); 
        }
        else if(caller.isAnnotationPresent(Authentified.class))
        {
            value = roles.get("Authentified");
        }
        else if(caller.isAnnotationPresent(ConfiguredAuth.class))
        {
            ConfiguredAuth configuredAuth =(ConfiguredAuth) caller.getAnnotation(ConfiguredAuth.class);
            value = roles.get(configuredAuth.auth());
        }
        if(value<=value_log)
        {
             return  true;
        }
        throw new AuthException("Vous n avez pas access a ce Methode .");
    }
    
    public static boolean check_auth(Class caller,Method method,HttpServletRequest request) throws AuthException {

        check_auth_class(caller, request);
        Object auth_of_log = request.getSession().getAttribute(request.getServletContext().getAttribute("session_auth").toString());
        HashMap<String,Integer> roles = (HashMap<String,Integer>)request.getServletContext().getAttribute("roles");
        Integer value = 0;
        Integer value_log = 0;
        if(auth_of_log != null && roles.get(auth_of_log.toString())!=null)
        {
            value_log = roles.get(auth_of_log);
        }
        if(method.isAnnotationPresent(Free.class))
        {
            value = roles.get("Free"); 
        }
        else if(method.isAnnotationPresent(Authentified.class))
        {
            value = roles.get("Authentified");
        }
        else if(method.isAnnotationPresent(ConfiguredAuth.class))
        {
            ConfiguredAuth configuredAuth = method.getAnnotation(ConfiguredAuth.class);
            value = roles.get(configuredAuth.auth());
        }
        if(value<=value_log)
        {
             return  true;
        }
        throw new AuthException("Vous n avez pas access a ce Methode .");
    }
}
