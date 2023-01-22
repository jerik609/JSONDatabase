package server;


import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        var application = new Application();
        application.start(Level.FINE);
    }
}
