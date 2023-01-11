package server.database;

public enum ResponseCode {
    OK("all ok"),
    ERROR_NO_DATA("no such data"),
    ERROR_OUT_OF_BOUNDS("requested data out of database max size"),
    ;

    private final String message;

    ResponseCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
