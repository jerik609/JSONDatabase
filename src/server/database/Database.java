package server.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

public class Database {
    private static final Logger log = Logger.getLogger(Database.class.getSimpleName());

    @Expose
    private final int capacity;
    @Expose
    private final HashMap<String, JsonElement> database;

    public Database(int capacity, HashMap<String, JsonElement> database) {
        this.capacity = capacity;
        this.database = database;
    }

    public Database(int capacity) {
        this.capacity = capacity;
        this.database = new HashMap<String, JsonElement>(capacity);
    }

    private boolean isOutOfBounds(String index) {
        if (database.containsKey(index)) {
            return false;
        }
        return database.size() + 1 > capacity;
    }

    private record SearchResult(String key, JsonElement parentElement, JsonElement element) {}

    private static SearchResult filterDataForSecondaryKeys(JsonElement item, String[] keys) {
        final var value = item.get("value");
        final var secondaryKeys = Arrays.copyOfRange(keys, 1, keys.length);

        log.fine("filtering by keys: " + Arrays.toString(secondaryKeys) + ", value: " + value);

        JsonElement parentElement = value; // value for first (main) key
        String elementKey = "";
        JsonElement element = value;
        for (var key : secondaryKeys) {
            log.info("processing secondary key: " + key);
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
        return new SearchResult(elementKey, parentElement, element);
    }

    public DatabaseResult<JsonElement> get(String[] keys) {
        final var builder = new DatabaseResult.Builder<JsonElement>();

        final JsonElement item = database.get(keys[0]);
        if (item == null) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
            builder.data(null);
        } else {
            builder.responseCode(ResponseCode.OK);
            builder.data(filterDataForSecondaryKeys(item, keys).element());
        }

        return builder.build();
    }

    public DatabaseResult<JsonObject> set(String[] keys, JsonElement newValue) {
        var builder = new DatabaseResult.Builder<JsonObject>();

        if (isOutOfBounds(keys[0])) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        builder.responseCode(ResponseCode.OK);

        final JsonElement item = database.get(keys[0]);
        if (item == null) {
            log.fine("insert:\nitem does not exist in database,\nwill enter it: " + newValue);
            database.put(keys[0], newValue);
            builder.data(null);
        } else {
            if (keys.length > 1) {
                final var searchResult = filterDataForSecondaryKeys(item, keys);
                if (searchResult.parentElement() instanceof JsonObject parentElement) {
                    log.fine("update:\n" + item + " exists in database,\nwill update it to: " + newValue);
                    parentElement.add(searchResult.key(), newValue.get("value"));
                } else {
                    log.info("no parent, root element (value), updating to: " + newValue);
                    item.add("value", newValue);
                }
            } else {
                log.fine("update:\n" + item + " exists in the DB,\nit is the root key, updating to: " + newValue);
                database.put(keys[0], newValue);
            }
        }
        log.fine("after update:\nDB state for key: " + keys[0] + ": " + database.get(keys[0]));

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
