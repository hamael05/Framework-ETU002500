package exception ;

public class TypeErrorException extends Exception { 
    public TypeErrorException ()
    {
        super() ;
    }
    public TypeErrorException (String message ) 
    {
        super(message); 
    }
    public TypeErrorException (String message , Throwable cause )  
    {
        super(message, cause);
    }
    public TypeErrorException (Throwable cause )
    {
        super(cause) ; 
    } 
}
