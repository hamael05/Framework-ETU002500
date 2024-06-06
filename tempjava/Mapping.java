package mg.ituProm16.utilitaire;

public class Mapping {
    String className;
    String methodName;

    public Mapping(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public Mapping() {}

    public void setClassName(String className) {
        this.className = className;
    }
    public String getClassName() {
        return this.className;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public String getMethodName() {
        return this.methodName;
    }



}