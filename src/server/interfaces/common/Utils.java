package server.interfaces.common;

import java.util.regex.Pattern;

public class Utils {
    public static String[] splitOffFirst(String input, char delimiter) {
        final var index = input.indexOf(delimiter);
        if (index == -1) {
            return new String[]{input, " "};
        } else {
            return new String[]{input.substring(0, index), input.substring(index + 1)};
        }
    }

    private static final Pattern pattern = Pattern.compile("(# \\d+)");

    public static int getRecordNoFromResponse(String response) {
        final var matcher = pattern.matcher(response);
        if (matcher.find()) {
            final var group = matcher.group(0);
            return Integer.parseInt(group.split(" ")[1]);
        }
        throw new RuntimeException("Invalid response: " + response);
    }
}
