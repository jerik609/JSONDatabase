package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34567;

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        try (
                final var socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                final var inputStream = new DataInputStream(socket.getInputStream());
                final var outputStream = new DataOutputStream(socket.getOutputStream());
        )
        {
            System.out.println("Client started!");
            String input;
            do {
                //System.out.println("Provide input:");
                input = scanner.nextLine();
                outputStream.writeUTF(input);
                System.out.println("Sent: " + input);
                System.out.println("Received: " + inputStream.readUTF());
            } while (!input.equals("DONE"));
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host: " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException("Client failed.");
        }
        //System.out.println("Finished!");
    }

    public static void main(String[] args) {
        var client = new Client();
        client.start();
    }
}
