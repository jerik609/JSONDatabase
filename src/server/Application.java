package server;

import server.database.Database;
import server.input.Controller;
import server.input.Executor;
import server.input.net.Server;

import java.util.Scanner;

public class Application {
    public void start() {
        final var scanner = new Scanner(System.in);
        final var executor = new Executor();
        final var database = new Database<String>("");
        final var controller = new Controller(scanner, executor, database);

        // we'll wrap this in input and provide as dependency injection, but let's
        // do this as standalone component for now
        // even the arch will be a bit retro ... we will use fork join pool
        // but a reactive pattern would be more suitable
        final var server = new Server();
        server.start();

        controller.start();
    }
}
