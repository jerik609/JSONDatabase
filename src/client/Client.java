package client;

import com.beust.jcommander.Parameter;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    private static final Pattern pattern = Pattern.compile("(# \\d+)");
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34567;

    private final Scanner scanner = new Scanner(System.in);

    @Parameter(names={"--type", "-t"})
    String type;
    @Parameter(names={"--index", "-i"})
    int index;
    @Parameter(names={"--message", "-m"})
    String message;

    private static int getRecordNoFromResponse(String response) {
        final var matcher = pattern.matcher(response);
        if (matcher.find()) {
            final var group = matcher.group(0);
            return Integer.parseInt(group.split(" ")[1]);
        }
        throw new RuntimeException("Invalid response: " + response);
    }

    public void run(boolean withParams) {
        try (
                final var socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                final var inputStream = new DataInputStream(socket.getInputStream());
                final var outputStream = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");
            socket.setSoTimeout(5000);

            if (withParams) {
                var command = type;
                command += index != 0 ? " " + index : "";
                if (message != null && !message.equals("null")) {
                    command += " " + message;
                }

                // send request
                outputStream.writeUTF(command);
                System.out.println("Sent: " + command);

                // receive response
                final var response = inputStream.readUTF();
                System.out.println("Received: " + response);
            } else {
                String input;
                do {
                    System.out.println("Provide input:");
                    input = scanner.nextLine();

                    outputStream.writeUTF(input);
                    System.out.println("Sent: " + input);

                    final var response = inputStream.readUTF();
                    System.out.println("Received: " + response);
                } while (!input.equals("DONE"));
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host: " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw new RuntimeException("Client failed: " + e);
        }
        //System.out.println("Finished!");
    }
}
