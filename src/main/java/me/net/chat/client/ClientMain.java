package me.net.chat.client;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 12345);
        client.start();
    }
}
