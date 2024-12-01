package me.net.chat.server.command;

import me.net.chat.server.Session;

import java.io.IOException;
import java.util.Arrays;

public class DefaultCommand implements Command {
    @Override
    public void execute(String[] args, Session session) throws IOException {
        session.send("Invalid command: " + Arrays.toString(args));
    }
}
