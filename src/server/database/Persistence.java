package server.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

public class Persistence {
    private static final Logger log = Logger.getLogger(Persistence.class.getSimpleName());

    private static final String DATABASE_FILE = "db.dat";

    private static final Gson gson = new GsonBuilder().create();

    public static void persistDatabase(Database<String> database) {
        final var serializedDb = gson.toJson(database);

        final var filePath = Paths.get(DATABASE_FILE);

        try (
                final FileWriter fw = new FileWriter(filePath.toFile());
                ) {
            fw.write(serializedDb);
        } catch (IOException e) {
            log.fine("Failed to persist the DB: " + e);
        }
    }

    public static Optional<Database<String>> loadDbFromFile() {
        final var filePath = Paths.get(DATABASE_FILE);
        try {
            final var dbStr = Files.readString(filePath);
            return Optional.of(gson.fromJson(dbStr, Database.class));
        } catch (IOException e) {
            log.fine("DB file could not be read: " + e);
            return Optional.empty();
        }
    }
}
