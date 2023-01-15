package server.interfaces.common;

public enum Action {
    UNKNOWN("unknown"),
    SET("set"),
    GET("get"),
    DELETE("delete"),
    EXIT("exit");

    private final String commandStr;

    Action(String commandStr) {
        this.commandStr = commandStr;
    }

    public static Action from(String input) {
        final var sanitizedInput = input.trim().toLowerCase();
        if (sanitizedInput.equals("unknown")) {
            return UNKNOWN;
        }
        for (var cmd : Action.values()) {
            if (cmd.commandStr.equals(sanitizedInput)) {
                return cmd;
            }
        }
        return UNKNOWN;
    }
}