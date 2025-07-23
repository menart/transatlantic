package express.atc.backend.helper;

public class StringHelper {
    public static String removeTrailingSlash(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.charAt(input.length() - 1) == '/'
                ? input.substring(0, input.length() - 1)
                : input;
    }
}
