package server;

import server.database.Database;
import server.input.Controller;
import server.input.Executor;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final var scanner = new Scanner(System.in);
        final var executor = new Executor();
        final var database = new Database<String>("");
        final var controller = new Controller(scanner, executor, database);

        controller.run();
    }
}
