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

    private static SearchResult filterDataForSecondaryKeys(JsonObject item, String[] keys) {
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

    /**
     * Get
     * @param keys
     * @return
     */
    public DatabaseResult<JsonElement> get(String[] keys) {
        final var builder = new DatabaseResult.Builder<JsonElement>();
        builder.data(null);

        final JsonElement item = database.get(keys[0]);

        if (item == null) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        } else if (keys.length == 1) {
            builder.responseCode(ResponseCode.OK);
            builder.data(item);
        } else if (item instanceof JsonObject jsonObject) {
            builder.responseCode(ResponseCode.OK);
            builder.data(filterDataForSecondaryKeys(jsonObject, keys).element());
        } else {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        }
        return builder.build();
    }

    /**
     * Set
     * @param keys
     * @param newValue
     * @return
     */
    public DatabaseResult<JsonObject> set(String[] keys, JsonElement newValue) {
        var builder = new DatabaseResult.Builder<JsonObject>();

        if (isOutOfBounds(keys[0])) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        builder.responseCode(ResponseCode.OK);
        builder.data(null);

        final JsonElement item = database.get(keys[0]);
        if (item == null) {
            log.fine("insert:\n" +
                    "item does not exist in database, will enter it:\n" +
                    newValue);
            database.put(keys[0], newValue);
        } else if (keys.length == 1) {
            log.fine("update:\n" +
                    "item exists in the DB:\n" +
                    item + "\n" +
                    "we're updating the root element to:\n" +
                    newValue);
            database.put(keys[0], newValue);
        } else if (item instanceof JsonObject jsonObject) {
            final var searchResult = filterDataForSecondaryKeys(jsonObject, keys);
            if (searchResult.parentElement() instanceof JsonObject parentElement) {
                log.fine("update:\n" +
                        "item exists in the DB:\n" +
                        item + "\n" +
                        "we have found a secondary key and we will update it to:\n" +
                        newValue);
                parentElement.add(searchResult.key(), newValue);
            } else {
                log.info("update:\nno parent, root element (value), updating to:\n" + newValue);
                jsonObject.add("value", newValue);
            }
        } else {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        }
        log.fine("after update:\nDB state for\nkey: " + keys[0] + "\nvalue: " + database.get(keys[0]));

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
