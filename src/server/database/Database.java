package server.database;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class Database {
    @Expose
    private final int capacity;
    @Expose
    private final HashMap<String, JsonObject> database;

    public Database(int capacity, HashMap<String, JsonObject> database) {
        this.capacity = capacity;
        this.database = database;
    }

    public Database(int capacity) {
        this.capacity = capacity;
        this.database = new HashMap<>(capacity);
    }

    private boolean isOutOfBounds(String index) {
        if (database.containsKey(index)) {
            return false;
        }
        return database.size() + 1 > capacity;
    }

    public DatabaseResult<JsonObject> get(String key) {
        var builder = new DatabaseResult.Builder<JsonObject>();

        if (isOutOfBounds(key)) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        var item = database.get(key);
        if (item == null) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        } else {
            builder.responseCode(ResponseCode.OK);
        }
        builder.data(item);

        return builder.build();
    }

    public DatabaseResult<JsonObject> set(String key, JsonObject value) {
        var builder = new DatabaseResult.Builder<JsonObject>();

        if (isOutOfBounds(key)) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        database.put(key, value);
        builder.responseCode(ResponseCode.OK);
        builder.data(null);
        return builder.build();
    }

    public DatabaseResult<JsonObject> delete(String key) {
        var builder = new DatabaseResult.Builder<JsonObject>();

        if (isOutOfBounds(key)) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        if (database.containsKey(key)) {
            builder.responseCode(ResponseCode.OK);
            database.remove(key);
        } else {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        }

        return builder.build();
    }
}
