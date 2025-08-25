package mg.ituprom16.exception;

import java.util.List;

import jakarta.servlet.ServletException;

public class AllTypeFormatException extends ServletException{
    List<TypeFormatException> typeFormatExceptions ;
    public List<TypeFormatException> getExceptions()
    {
        return this.typeFormatExceptions;
    }
    public AllTypeFormatException(List<TypeFormatException> lExceptions)
    {
        this.typeFormatExceptions = lExceptions;
    }
}
