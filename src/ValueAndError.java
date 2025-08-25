package utility  ; 
public class ValueAndError {
    String value; 
    String error; 

    public ValueAndError(String value, String error) {
        this.value = value;
        this.error = error;
    }
    public ValueAndError() {  }
    
    public String getError() {
        return error;
    }
    public String getValue() {
        return value;
    }
    public void setError(String error) {
        this.error = error;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
