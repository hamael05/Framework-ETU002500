package validation ;

import annotation.AnnotationDecimal;
import annotation.AnnotationField;
import annotation.AnnotationNotNull;
import annotation.AnnotationSize;
import java.lang.reflect.Field;


public class Validation  {
    public void validateValue(double value, Double minValue, Double maxValue) throws Exception {
        if (maxValue == null) {
            maxValue = 9999.99;
        }
        if (minValue == null) {
            minValue = 0.00;
        }
        if (value > maxValue || value < minValue) {
            throw new Exception("La valeur doit être entre " + minValue + " et aussi" + maxValue + ".");
        }
    }   

    public boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false; 
        }
    }
    public boolean notNumeric ( String str ) {  
        boolean isValidDouble = false;
        try {
            Double.parseDouble(str);
            isValidDouble = false;  
        } catch (NumberFormatException e) {
            isValidDouble = true;   
        }
        return isValidDouble ; 
    }
    public void validateValuesNull( Object value ) throws Exception {  
        if(  value == null )  { 
            throw new Exception ( "La valeur doit pas etre null ") ; 
        }
    }
    public void validateSizeString( String value, int minValue , int maxValue ) throws Exception { 
       
            int size = value.length()  ; 
            if ( size > maxValue || size < minValue) {
                throw new Exception("La taille du texte doit être entre " + minValue + " et " + maxValue + ".");
            }
        
    } 
     //Validation 
    public int checkAnnotationDecimal( Field field ) { 
        if( field.isAnnotationPresent(AnnotationDecimal.class) )
        {  return 1 ; } 
        return 0 ; 
    }

    public int checkAnnotationField( Field field ) { 
        if( field.isAnnotationPresent(AnnotationField.class) )
        {  return 1 ; } 
        return 0 ; 
    }

    public int checkAnnotationSize( Field field ) { 
        if( field.isAnnotationPresent(AnnotationSize.class) )
        {  return 1 ; } 
        return 0 ;
    }

    public int checkAnnotationNotNull( Field field ) { 
        if( field.isAnnotationPresent(AnnotationNotNull.class) )
        {  return 1 ; } 
        return 0 ;
    }





}
