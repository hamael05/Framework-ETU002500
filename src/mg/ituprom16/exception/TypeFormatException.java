package mg.ituprom16.exception;

import java.rmi.ServerException;
import java.util.List;

import jakarta.servlet.ServletException;

public class TypeFormatException extends ServletException {
    String paramName;
    List<String> errors;
    public String getParamName()
    {
        return this.paramName;
    }
    public List<String> getErrors()
    {
        return this.errors;
    }
    public String getMessage()
    {
        String ans = "Le champ "+this.getParamName()+" comporte les erreurs";
        for(String error : this.getErrors())
        {
           ans+=" and"+error;
        }
        return ans;
    }
    public TypeFormatException(String paramName,List<String> errors)
    {
        this.paramName = paramName;
        this.errors = errors;
    }
}
