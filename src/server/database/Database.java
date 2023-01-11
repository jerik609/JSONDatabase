package server.database;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class Database<T> {
    private static final int DATABASE_CAPACITY = 100;
    private final T emptyValue;

    private static boolean isOutOfBounds(int index) {
        return index > DATABASE_CAPACITY;
    }

    private final HashMap<Integer, T> database = new HashMap<>(DATABASE_CAPACITY);

    public Database(T emptyValue) {
        this.emptyValue = emptyValue;
    }

    public DatabaseResult<T> get(Integer key) {
        var builder = new DatabaseResult.Builder<T>();

        if (isOutOfBounds(key)) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        var item = database.get(key);
        if (item == null) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        } else if (item.equals(emptyValue)) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        } else {
            builder.responseCode(ResponseCode.OK);
        }
        builder.data(item);

        return builder.build();
    }

    public DatabaseResult<T> set(Integer key, T value) {
        var builder = new DatabaseResult.Builder<T>();

        if (isOutOfBounds(key)) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        database.put(key, value);
        builder.responseCode(ResponseCode.OK);
        builder.data(null);
        return builder.build();
    }

    public DatabaseResult<T> delete(Integer key) {
        var builder = new DatabaseResult.Builder<T>();

        if (isOutOfBounds(key)) {
            builder.responseCode(ResponseCode.ERROR_OUT_OF_BOUNDS);
            return builder.build();
        }

        if (database.containsKey(key)) {
            builder.responseCode(ResponseCode.OK);
            database.remove(key);
        } else {
            builder.responseCode(ResponseCode.OK);
            database.put(key, emptyValue);
            //builder.responseCode(ResponseCode.ERROR_NO_DATA);
        }

        return builder.build();
    }
}
