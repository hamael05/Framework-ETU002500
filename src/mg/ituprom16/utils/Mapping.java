package mg.ituprom16.utils;

import java.util.*;


public class Mapping {
    private String className;
    private HashSet<VerbMethod> listVerbMethods;

    public String getClassName() {
        return this.className;
    }

    public HashSet<VerbMethod> getVerbMethods() {
        return this.listVerbMethods;
    }
    
    public Mapping(String className, HashSet<VerbMethod> verbMethods) {
        this.className = className;
        this.listVerbMethods = verbMethods;
    }
    public void addVerbMethod(String verb,String method)
    {
        this.getVerbMethods().add(new VerbMethod(verb, method));
    }
    public boolean checkIfVerbExist(String verb) {
        for(VerbMethod verbMethod : this.getVerbMethods())
        {
            if(verbMethod.getVerb().compareTo(verb)==0)
            {
                return true;
            }
        }
        return false;
    }
}

