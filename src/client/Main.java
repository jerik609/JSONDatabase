package client;

import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) {
        var client = new Client();
        JCommander.newBuilder()
                .addObject(client)
                .build()
                .parse(args);
        client.run();
    }
}
