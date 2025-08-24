package mapping ; 

import vm.VerbeMethod ; 
import java.util.HashSet;

public class Mapping {

    String ClasseName ; 
    HashSet<VerbeMethod> verbeMethods ; 

    public Mapping(String ClassName, HashSet<VerbeMethod> verbeMethods ) 
    {  
        this.setClasseName(ClassName);
        this.setVerbeMethods(verbeMethods);
    }
    public String getClasseName() {
        return ClasseName;
    }
    public HashSet<VerbeMethod> getVerbeMethods() {
        return verbeMethods;
    }
    public void setClasseName(String classeName) {
        ClasseName = classeName;
    }
    public void setVerbeMethods(HashSet<VerbeMethod> verbeMethods) {
        this.verbeMethods = verbeMethods;
    }
}
