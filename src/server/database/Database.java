package server.database;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class Database<T> {
    private final int capacity;
    private final T emptyValue;

    private boolean isOutOfBounds(int index) {
        return index > capacity;
    }

    @Expose
    private final HashMap<Integer, T> database;

    public Database(int capacity, T emptyValue) {
        this.capacity = capacity;
        this.emptyValue = emptyValue;
        this.database = new HashMap<>(capacity);
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

    public void SerializeToJson() {
        var gsonB = new GsonBuilder();
        var gson = gsonB.setPrettyPrinting().create();
        var x = gson.toJson(this);
        System.out.println(x);


        var y = gson.fromJson(x, Database.class);
        System.out.println(y.get(1));
        System.out.println(y.get(2));
    }
}
