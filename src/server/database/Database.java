package server.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

public class Database {
    private static final Logger log = Logger.getLogger(Database.class.getSimpleName());

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

    private static JsonElement getSecondaryKeys(JsonElement value, String[] keys) {
        log.fine("filtering by keys: " + Arrays.toString(keys) + ", value: " + value);
        JsonElement parentElement = value; // value for first (main) key
        String elementKey = "";
        JsonElement element = value;
        for (var key : keys) {
            elementKey = key;
            if (element instanceof JsonObject node) {
                element = node.get(key);
                if (element == null) {
                    throw new RuntimeException("Key is not one of node's children: Key '" + key + "' not found!");
                }
                parentElement = node;
            } else {
                throw new RuntimeException("Trying to search past leaf: Key '" + key + "' not found!");
            }
        }
        log.fine("element (key:" + elementKey + ", parent: " + parentElement +"): " + element);
        return element;
    }

    public DatabaseResult<JsonElement> get(String[] keys) {
        final var builder = new DatabaseResult.Builder<JsonElement>();

        final var item = database.get(keys[0]);
        if (item == null) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
            builder.data(null);
        } else {
            builder.responseCode(ResponseCode.OK);
            final var value = item.get("value");
            final var secondaryKeys = Arrays.copyOfRange(keys, 1, keys.length);
            builder.data(getSecondaryKeys(value, secondaryKeys));
        }

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
