package mapping ; 

public class Mapping {

    String ClasseName ; 
    String MethodeName ; 

    public Mapping(String ClasseName, String MethodeName) { 
        this.setClasseName(ClasseName);
        this.setMethodeName(MethodeName);
    } 
    public String getClasseName() {
        return ClasseName;
    }
    public String getMethodeName() {
        return MethodeName;
    }
    public void setClasseName(String classeName) {
        ClasseName = classeName;
    }
    public void setMethodeName(String methodeName) {
        MethodeName = methodeName;
    }
}
