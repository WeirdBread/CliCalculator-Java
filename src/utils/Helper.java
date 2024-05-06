package utils;

public class Helper {
    public static Double tryParseDouble(String string){
        try {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException _) {
            return null;
        }
    }
}
