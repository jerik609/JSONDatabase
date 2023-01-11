package server.input;

public class Utils {
    public static String[] splitOffFirst(String input, char delimiter) {
        final var index = input.indexOf(delimiter);
        if (index == -1) {
            return new String[]{input, " "};
        } else {
            return new String[]{input.substring(0, index), input.substring(index + 1)};
        }
    }
}
