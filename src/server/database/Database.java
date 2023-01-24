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

    public int getCapacity() {
        return capacity;
    }

    public HashMap<String, JsonObject> getDatabase() {
        return database;
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
            log.fine("processing secondary key: " + key);
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

        final JsonObject item = database.get(keys[0]);

        if (item == null) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        } else if (keys.length == 1) {
            builder.responseCode(ResponseCode.OK);
            builder.data(item.get("value"));
        } else {
            builder.responseCode(ResponseCode.OK);
            builder.data(filterDataForSecondaryKeys(item, keys).element());
        }
        return builder.build();
    }

    /**
     * Set
     * @param keys
     * @param payload
     * @return
     */
    public DatabaseResult<JsonObject> set(String[] keys, JsonObject payload) {
        var builder = new DatabaseResult.Builder<JsonObject>();

        if (isOutOfBounds(keys[0])) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        builder.responseCode(ResponseCode.OK);
        builder.data(null);

        final JsonObject item = database.get(keys[0]);
        if (item == null) {
            log.fine("insert:\n" +
                    "item does not exist in database, will enter it:\n" +
                    payload);
            database.put(keys[0], payload);
        } else if (keys.length == 1) {
            log.fine("update:\n" +
                    "item exists in the DB:\n" +
                    item + "\n" +
                    "we're updating the root element to:\n" +
                    payload);
            database.put(keys[0], payload);
        } else {
            final var searchResult = filterDataForSecondaryKeys(item, keys);
            if (searchResult.parentElement() instanceof JsonObject parentElement) {
                log.fine("update:\n" +
                        "item exists in the DB:\n" +
                        item + "\n" +
                        "we have found a secondary key and we will update it to:\n" +
                        payload);
                parentElement.add(searchResult.key(), payload.get("value"));
            } else {
                log.info("update:\n" +
                        "parent is not an object, but we traversed(?), inconsistency detected in DB(!):\n" +
                        item);
                builder.responseCode(ResponseCode.ERROR_NO_DATA);
            }
        }
        log.fine("after update:\nDB state for\nkey: " + keys[0] + "\nvalue: " + database.get(keys[0]));

        return builder.build();
    }

    /**
     * Delete
     * @param keys
     * @return
     */
    public DatabaseResult<JsonObject> delete(String[] keys) {
        var builder = new DatabaseResult.Builder<JsonObject>();

        builder.responseCode(ResponseCode.OK);
        builder.data(null);

        final JsonObject item = database.get(keys[0]);
        if (item == null) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        } else if (keys.length == 1) {
            database.remove(keys[0]);
        } else {
            final var searchResult = filterDataForSecondaryKeys(item, keys);
            if (searchResult.parentElement() instanceof JsonObject parentElement) {
                log.fine("delete:\n" +
                        "item exists in the DB:\n" +
                        item + "\n" +
                        "we have found a secondary key and we will delete it");
                parentElement.remove(searchResult.key());
            } else {
                log.info("update:\n" +
                        "parent is not an object, but we traversed(?), inconsistency detected in DB(!):\n" +
                        item);
                builder.responseCode(ResponseCode.ERROR_NO_DATA);
            }
        }
        log.fine("after delete:\nDB state for\nkey: " + keys[0] + "\nvalue: " + database.get(keys[0]));

        return builder.build();
    }
}
