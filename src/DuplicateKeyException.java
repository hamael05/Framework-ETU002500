package exception ; 

public class DuplicateKeyException extends Exception {
    public DuplicateKeyException() {
        super();
    }
    public DuplicateKeyException(String message) {
        super(message);
    }
    public DuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }
    public DuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
