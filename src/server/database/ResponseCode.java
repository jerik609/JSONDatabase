package server.database;

public enum ResponseCode {
    OK("all ok"),
    ERROR_NO_DATA("no such data"),
    ;

    private final String message;

    ResponseCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
