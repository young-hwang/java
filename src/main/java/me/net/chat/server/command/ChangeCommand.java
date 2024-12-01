package me.net.chat.server.command;

import me.net.chat.server.Session;
import me.net.chat.server.SessionManager;

import java.io.IOException;

public class ChangeCommand implements Command {
    private final SessionManager sessionManager;

    public ChangeCommand(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void execute(String[] args, Session session) throws IOException {
        String oldName = session.getUsername();
        String newName = args[2];
        session.setUsername(newName);
        sessionManager.sendAll("Changed Name: " + oldName + " -> " + newName);
    }
}
