package server.database;

import java.util.Optional;

public class DatabaseResult<T> {
    private final ResponseCode responseCode;
    private final T data;

    private DatabaseResult(ResponseCode responseCode, T data) {
        this.responseCode = responseCode;
        this.data = data;
    }

    public Optional<T> getData() {
        return Optional.of(data);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public static class Builder<T> {
        private ResponseCode responseCode;
        private T data = null;

        public Builder<T> responseCode(ResponseCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        DatabaseResult<T> build() {
            return new DatabaseResult<T>(responseCode, data);
        }
    }
}
