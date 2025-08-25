package mg.ituprom16.exception;

import java.rmi.ServerException;

public class AuthException extends ServerException{

    public AuthException(String message) {
        super(message);
    }
    
}
