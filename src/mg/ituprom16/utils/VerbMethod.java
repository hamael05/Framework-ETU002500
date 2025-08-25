package mg.ituprom16.utils;

import java.util.Objects;

public class VerbMethod {
    String verb;
    String method;
    public String getVerb(){return this.verb;}
    public String getMethod(){return this.method;}

    public VerbMethod(String verb,String method){
        this.verb=verb;
        this.method=method;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof VerbMethod)
        {
            if(((VerbMethod) obj).getVerb().compareTo(this.verb)==0)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(verb, method);
    }
}