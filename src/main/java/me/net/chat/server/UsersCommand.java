package me.net.chat.server;

import java.io.IOException;
import java.util.List;

public class UsersCommand implements Command {
    private final SessionManager sessionManager;

    public UsersCommand(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void execute(String[] args, Session session) throws IOException {
        List<String> allUserName = sessionManager.getAllUserName();
        session.send("All Users : " + String.join(", ", allUserName));
    }
}
