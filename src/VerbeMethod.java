package vm ;

import java.util.HashSet;
import java.util.Objects;

public class VerbeMethod {
    private String verb;
    private String method;

   public VerbeMethod(String verb, String method ) 
   {
            this.verb = verb; 
            this.method = method ; 
   } 
   public String getMethod() {
       return method;
   }
   public String getVerb() {
       return verb;
   }
   public void setMethod(String method) {
       this.method = method;
   }
   public void setVerb(String verb) {
       this.verb = verb;
   }
    // Redéfinir equals() pour comparer les objets Fruit par nom et couleur
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Si c'est le même objet, alors c'est égal
        if (o == null || getClass() != o.getClass()) return false; // Vérifie la classe
        VerbeMethod vm = (VerbeMethod) o;
        return Objects.equals(verb, vm.verb) && Objects.equals( method, vm.method ); // Compare les propriétés
    }
    // Redéfinir hashCode() pour générer un hash basé sur le nom et la couleur
    @Override
    public int hashCode() {
        return Objects.hash( verb , method );
    }
}