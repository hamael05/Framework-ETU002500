package validation ; 

public class Validation  {
    public void validateValue(double value, Double minValue, Double maxValue) throws IllegalArgumentException {
        if (maxValue == null) {
            maxValue = 9999.99;
        }
        if (minValue == null) {
            minValue = 0.00;
        }
        if (value > maxValue || value < minValue) {
            throw new IllegalArgumentException("La valeur doit être entre " + minValue + " et " + maxValue + ".");
        }
    }    
}
